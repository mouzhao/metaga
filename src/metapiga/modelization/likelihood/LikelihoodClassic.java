// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.likelihood;

import Jama.Matrix;
import metapiga.utilities.Tools;
import metapiga.modelization.data.DNA;
import java.util.List;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.Node;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.Tree;

import java.util.Map;
import metapiga.parameters.Parameters;
import metapiga.modelization.Dataset;
import metapiga.RateParameter;

public class LikelihoodClassic extends Likelihood
{
    private final SequenceArrays4Dimension localSequence;
    
    protected LikelihoodClassic(final Dataset.Partition partition, final Parameters.EvaluationRate rate, final Parameters.EvaluationModel model, final Parameters.EvaluationDistribution distribution, final double distributionShape, final double pinv, final double apRate, final Map<RateParameter, Double> rateParameters, final Parameters.EvaluationStateFrequencies stateFreq, final Tree tree, final int numSubsets, final SequenceArrays4Dimension seq) throws UnrootableTreeException {
        super(partition, rate, model, distribution, distributionShape, pinv, apRate, rateParameters, stateFreq, tree, numSubsets, seq);
        this.localSequence = seq;
    }
    
    protected LikelihoodClassic(final LikelihoodClassic L, final Tree tree) throws UnrootableTreeException {
        super(L, tree);
        this.localSequence = (SequenceArrays4Dimension)super.sequences;
    }
    
    @Override
    protected void update(final Node node) throws NullAncestorException {
        for (final Node child : node.getChildren()) {
            if (this.toUpdate.contains(child)) {
                this.update(child);
                this.toUpdate.remove(child);
            }
        }
        switch (this.model) {
            case JC: {
                this.computeJC(node);
                break;
            }
            case K2P: {
                this.computeK2P(node);
                break;
            }
            case HKY85: {
                this.computeHKY85(node);
                break;
            }
            case TN93: {
                this.computeTN93(node);
                break;
            }
            case POISSON: {
                this.computePoisson(node);
                break;
            }
            case GTR2:
            case GTR20:
            case GY:
            case GTR64:
            case ECM:
            case WAG:
            case JTT:
            case DAYHOFF:
            case VT:
            case BLOSUM62:
            case CPREV:
            case MTREV:
            case RTREV:
            case MTMAM:
            case GTR: {
                this.computeGTR(node);
                break;
            }
        }
        if (this.tree.getRoot() == node) {
            this.calculateLikelihoodAtRoot();
        }
    }
    
    protected void computeJC(final Node node) throws NullAncestorException {
        final List<Node> children = node.getChildren();
        Node node2 = children.get(0);
        Node node3 = children.get(1);
        for (int count = 1; count < children.size(); ++count) {
            double bl1;
            double bl2;
            float[][][] tempRootSeq;
            if (count > 1) {
                node2 = children.get(count);
                node3 = node;
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = 0.0;
                tempRootSeq = new float[this.numCat][this.numStates][this.numCharComp];
            }
            else {
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = node3.getAncestorBranchLength() / (1.0 - this.pInv);
                tempRootSeq = null;
            }
            final Node key = (count > 1) ? null : node;
            if (this.underflowScaling.containsKey(key)) {
                this.underflowScaling.remove(key);
                this.underflow = !this.underflowScaling.isEmpty();
            }
            final int n = this.nodeIndex.get(node);
            final int n2 = this.nodeIndex.get(node2);
            final int n3 = this.nodeIndex.get(node3);
            final double[][] ufScaling = new double[this.numCat][this.numCharComp];
            boolean underflowEncountered = false;
            boolean rescalingNeeded = false;
            do {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    final double exp1 = Math.exp(-bl1 * this.rateScaling * this.rates[cat] * this.apRate);
                    final double exp2 = Math.exp(-bl2 * this.rateScaling * this.rates[cat] * this.apRate);
                    final double diag1 = 0.25 + 0.75 * exp1;
                    final double offdiag1 = 0.25 - 0.25 * exp1;
                    final double diag2 = 0.25 + 0.75 * exp2;
                    final double offdiag2 = 0.25 - 0.25 * exp2;
                    final float[][] seq1 = this.localSequence.getSequenceAtCategoryAndNode(cat, n2);
                    final float[][] seq2 = this.localSequence.getSequenceAtCategoryAndNode(cat, n3);
                    final float[][] seq3 = (count > 1) ? tempRootSeq[cat] : this.localSequence.getSequenceAtCategoryAndNode(cat, n);
                    final double[] scalingFactor = ufScaling[cat];
                    for (int site = 0; site < this.numCharComp; ++site) {
                        double lMax = 0.0;
                        for (int state = 0; state < this.numStates; ++state) {
                            double sum1 = 0.0;
                            double sum2 = 0.0;
                            for (int s = 0; s < this.numStates; ++s) {
                                sum1 += seq1[s][site] * ((state == s) ? diag1 : offdiag1);
                                sum2 += seq2[s][site] * ((state == s) ? diag2 : offdiag2);
                            }
                            final double prod = sum1 * sum2;
                            if (rescalingNeeded) {
                                seq3[state][site] = (float)(prod / scalingFactor[site]);
                            }
                            else {
                                seq3[state][site] = (float)prod;
                                if (prod > lMax) {
                                    lMax = prod;
                                }
                                if (prod < 1.401298464324817E-45) {
                                    underflowEncountered = true;
                                }
                            }
                        }
                        if (!rescalingNeeded) {
                            scalingFactor[site] = lMax;
                        }
                        assert !Double.isInfinite(lMax) : site;
                        assert !Double.isNaN(lMax) : site;
                    }
                }
                if (underflowEncountered) {
                    this.underflowScaling.put(key, ufScaling);
                    this.underflow = true;
                    rescalingNeeded = true;
                    underflowEncountered = false;
                }
                else {
                    rescalingNeeded = false;
                }
            } while (rescalingNeeded);
            if (count > 1) {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    this.localSequence.setSequenceAtNodeInCategory(tempRootSeq[cat], cat, n);
                }
            }
        }
    }
    
    protected void computeK2P(final Node node) throws NullAncestorException {
        final double kappa = this.rateParameters.get(RateParameter.K);
        final int A = DNA.A.state;
        final int C = DNA.C.state;
        final int G = DNA.G.state;
        final int T = DNA.T.state;
        final List<Node> children = node.getChildren();
        Node node2 = children.get(0);
        Node node3 = children.get(1);
        for (int count = 1; count < children.size(); ++count) {
            double bl1;
            double bl2;
            float[][][] tempRootSeq;
            if (count > 1) {
                node2 = children.get(count);
                node3 = node;
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = 0.0;
                tempRootSeq = new float[this.numCat][this.numStates][this.numCharComp];
            }
            else {
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = node3.getAncestorBranchLength() / (1.0 - this.pInv);
                tempRootSeq = null;
            }
            final Node key = (count > 1) ? null : node;
            if (this.underflowScaling.containsKey(key)) {
                this.underflowScaling.remove(key);
                this.underflow = !this.underflowScaling.isEmpty();
            }
            final int n = this.nodeIndex.get(node);
            final int n2 = this.nodeIndex.get(node2);
            final int n3 = this.nodeIndex.get(node3);
            final double[][] ufScaling = new double[this.numCat][this.numCharComp];
            boolean underflowEncountered = false;
            boolean rescalingNeeded = false;
            do {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    final double exp1a = Math.exp(-bl1 * this.rateScaling * this.rates[cat] * this.apRate);
                    final double exp2a = Math.exp(-bl2 * this.rateScaling * this.rates[cat] * this.apRate);
                    final double exp1b = Math.exp(-bl1 * this.rateScaling * this.rates[cat] * this.apRate * ((kappa + 1.0) / 2.0));
                    final double exp2b = Math.exp(-bl2 * this.rateScaling * this.rates[cat] * this.apRate * ((kappa + 1.0) / 2.0));
                    final double diag1 = 0.25 + 0.25 * exp1a + 0.5 * exp1b;
                    final double diag2 = 0.25 + 0.25 * exp2a + 0.5 * exp2b;
                    final double ti1 = 0.25 + 0.25 * exp1a - 0.5 * exp1b;
                    final double ti2 = 0.25 + 0.25 * exp2a - 0.5 * exp2b;
                    final double tv1 = 0.25 - 0.25 * exp1a;
                    final double tv2 = 0.25 - 0.25 * exp2a;
                    final float[][] seq1 = this.localSequence.getSequenceAtCategoryAndNode(cat, n2);
                    final float[][] seq2 = this.localSequence.getSequenceAtCategoryAndNode(cat, n3);
                    final float[][] seq3 = (count > 1) ? tempRootSeq[cat] : this.localSequence.getSequenceAtCategoryAndNode(cat, n);
                    final double[] scalingFactor = ufScaling[cat];
                    for (int site = 0; site < this.numCharComp; ++site) {
                        double lMax = 0.0;
                        for (int base = 0; base < this.numStates; ++base) {
                            double sum1 = 0.0;
                            double sum2 = 0.0;
                            if (base == A) {
                                sum1 += seq1[A][site] * diag1;
                                sum2 += seq2[A][site] * diag2;
                                sum1 += seq1[C][site] * tv1;
                                sum2 += seq2[C][site] * tv2;
                                sum1 += seq1[G][site] * ti1;
                                sum2 += seq2[G][site] * ti2;
                                sum1 += seq1[T][site] * tv1;
                                sum2 += seq2[T][site] * tv2;
                            }
                            else if (base == C) {
                                sum1 += seq1[A][site] * tv1;
                                sum2 += seq2[A][site] * tv2;
                                sum1 += seq1[C][site] * diag1;
                                sum2 += seq2[C][site] * diag2;
                                sum1 += seq1[G][site] * tv1;
                                sum2 += seq2[G][site] * tv2;
                                sum1 += seq1[T][site] * ti1;
                                sum2 += seq2[T][site] * ti2;
                            }
                            else if (base == G) {
                                sum1 += seq1[A][site] * ti1;
                                sum2 += seq2[A][site] * ti2;
                                sum1 += seq1[C][site] * tv1;
                                sum2 += seq2[C][site] * tv2;
                                sum1 += seq1[G][site] * diag1;
                                sum2 += seq2[G][site] * diag2;
                                sum1 += seq1[T][site] * tv1;
                                sum2 += seq2[T][site] * tv2;
                            }
                            else if (base == T) {
                                sum1 += seq1[A][site] * tv1;
                                sum2 += seq2[A][site] * tv2;
                                sum1 += seq1[C][site] * ti1;
                                sum2 += seq2[C][site] * ti2;
                                sum1 += seq1[G][site] * tv1;
                                sum2 += seq2[G][site] * tv2;
                                sum1 += seq1[T][site] * diag1;
                                sum2 += seq2[T][site] * diag2;
                            }
                            final double prod = sum1 * sum2;
                            if (rescalingNeeded) {
                                seq3[base][site] = (float)(prod / scalingFactor[site]);
                            }
                            else {
                                seq3[base][site] = (float)prod;
                                if (prod > lMax) {
                                    lMax = prod;
                                }
                                if (prod < 1.401298464324817E-45) {
                                    underflowEncountered = true;
                                }
                            }
                        }
                        if (!rescalingNeeded) {
                            scalingFactor[site] = lMax;
                        }
                        assert !Double.isInfinite(lMax) : site;
                        assert !Double.isNaN(lMax) : site;
                    }
                }
                if (underflowEncountered) {
                    this.underflowScaling.put(key, ufScaling);
                    this.underflow = true;
                    rescalingNeeded = true;
                    underflowEncountered = false;
                }
                else {
                    rescalingNeeded = false;
                }
            } while (rescalingNeeded);
            if (count > 1) {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    this.localSequence.setSequenceAtNodeInCategory(tempRootSeq[cat], cat, n);
                }
            }
        }
    }
    
    protected void computeHKY85(final Node node) throws NullAncestorException {
        final double kappa = this.rateParameters.get(RateParameter.K);
        final int A = DNA.A.state;
        final int C = DNA.C.state;
        final int G = DNA.G.state;
        final int T = DNA.T.state;
        final List<Node> children = node.getChildren();
        Node node2 = children.get(0);
        Node node3 = children.get(1);
        for (int count = 1; count < children.size(); ++count) {
            final double[] bl = new double[2];
            final double[] PIj = new double[this.numStates];
            final double[][] expA = new double[2][this.numStates];
            final double[][] expB = new double[2][this.numStates];
            final double[][] diag = new double[2][this.numStates];
            final double[][] ti = new double[2][this.numStates];
            final double[][] tv = new double[2][this.numStates];
            float[][][] tempRootSeq;
            if (count > 1) {
                node2 = children.get(count);
                node3 = node;
                bl[0] = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl[1] = 0.0;
                tempRootSeq = new float[this.numCat][this.numStates][this.numCharComp];
            }
            else {
                bl[0] = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl[1] = node3.getAncestorBranchLength() / (1.0 - this.pInv);
                tempRootSeq = null;
            }
            final Node key = (count > 1) ? null : node;
            if (this.underflowScaling.containsKey(key)) {
                this.underflowScaling.remove(key);
                this.underflow = !this.underflowScaling.isEmpty();
            }
            PIj[A] = (PIj[G] = this.equiFreq[A] + this.equiFreq[G]);
            PIj[C] = (PIj[T] = this.equiFreq[C] + this.equiFreq[T]);
            final int n = this.nodeIndex.get(node);
            final int n2 = this.nodeIndex.get(node2);
            final int n3 = this.nodeIndex.get(node3);
            final double[][] ufScaling = new double[this.numCat][this.numCharComp];
            boolean underflowEncountered = false;
            boolean rescalingNeeded = false;
            do {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    for (int seq = 0; seq < 2; ++seq) {
                        for (int base = 0; base < this.numStates; ++base) {
                            expA[seq][base] = Math.exp(-bl[seq] * this.rateScaling * this.rates[cat] * this.apRate);
                            expB[seq][base] = Math.exp(-bl[seq] * this.rateScaling * this.rates[cat] * this.apRate * (1.0 + PIj[base] * (kappa - 1.0)));
                        }
                    }
                    for (int seq = 0; seq < 2; ++seq) {
                        for (int base = 0; base < this.numStates; ++base) {
                            diag[seq][base] = this.equiFreq[base] + this.equiFreq[base] * (1.0 / PIj[base] - 1.0) * expA[seq][base] + (PIj[base] - this.equiFreq[base]) / PIj[base] * expB[seq][base];
                            ti[seq][base] = this.equiFreq[base] + this.equiFreq[base] * (1.0 / PIj[base] - 1.0) * expA[seq][base] - this.equiFreq[base] / PIj[base] * expB[seq][base];
                            tv[seq][base] = this.equiFreq[base] * (1.0 - expA[seq][base]);
                        }
                    }
                    final float[][] seq2 = this.localSequence.getSequenceAtCategoryAndNode(cat, n2);
                    final float[][] seq3 = this.localSequence.getSequenceAtCategoryAndNode(cat, n3);
                    final float[][] seq4 = (count > 1) ? tempRootSeq[cat] : this.localSequence.getSequenceAtCategoryAndNode(cat, n);
                    final double[] scalingFactor = ufScaling[cat];
                    for (int site = 0; site < this.numCharComp; ++site) {
                        double lMax = 0.0;
                        for (int base2 = 0; base2 < this.numStates; ++base2) {
                            double sum1 = 0.0;
                            double sum2 = 0.0;
                            if (base2 == A) {
                                sum1 += seq2[A][site] * diag[0][A];
                                sum2 += seq3[A][site] * diag[1][A];
                                sum1 += seq2[C][site] * tv[0][C];
                                sum2 += seq3[C][site] * tv[1][C];
                                sum1 += seq2[G][site] * ti[0][G];
                                sum2 += seq3[G][site] * ti[1][G];
                                sum1 += seq2[T][site] * tv[0][T];
                                sum2 += seq3[T][site] * tv[1][T];
                            }
                            else if (base2 == C) {
                                sum1 += seq2[A][site] * tv[0][A];
                                sum2 += seq3[A][site] * tv[1][A];
                                sum1 += seq2[C][site] * diag[0][C];
                                sum2 += seq3[C][site] * diag[1][C];
                                sum1 += seq2[G][site] * tv[0][G];
                                sum2 += seq3[G][site] * tv[1][G];
                                sum1 += seq2[T][site] * ti[0][T];
                                sum2 += seq3[T][site] * ti[1][T];
                            }
                            else if (base2 == G) {
                                sum1 += seq2[A][site] * ti[0][A];
                                sum2 += seq3[A][site] * ti[1][A];
                                sum1 += seq2[C][site] * tv[0][C];
                                sum2 += seq3[C][site] * tv[1][C];
                                sum1 += seq2[G][site] * diag[0][G];
                                sum2 += seq3[G][site] * diag[1][G];
                                sum1 += seq2[T][site] * tv[0][T];
                                sum2 += seq3[T][site] * tv[1][T];
                            }
                            else if (base2 == T) {
                                sum1 += seq2[A][site] * tv[0][A];
                                sum2 += seq3[A][site] * tv[1][A];
                                sum1 += seq2[C][site] * ti[0][C];
                                sum2 += seq3[C][site] * ti[1][C];
                                sum1 += seq2[G][site] * tv[0][G];
                                sum2 += seq3[G][site] * tv[1][G];
                                sum1 += seq2[T][site] * diag[0][T];
                                sum2 += seq3[T][site] * diag[1][T];
                            }
                            final double prod = sum1 * sum2;
                            if (rescalingNeeded) {
                                seq4[base2][site] = (float)(prod / scalingFactor[site]);
                            }
                            else {
                                seq4[base2][site] = (float)prod;
                                if (prod > lMax) {
                                    lMax = prod;
                                }
                                if (prod < 1.401298464324817E-45) {
                                    underflowEncountered = true;
                                }
                            }
                        }
                        if (!rescalingNeeded) {
                            scalingFactor[site] = lMax;
                        }
                        assert !Double.isInfinite(lMax) : site;
                        assert !Double.isNaN(lMax) : site;
                    }
                }
                if (underflowEncountered) {
                    this.underflowScaling.put(key, ufScaling);
                    this.underflow = true;
                    rescalingNeeded = true;
                    underflowEncountered = false;
                }
                else {
                    rescalingNeeded = false;
                }
            } while (rescalingNeeded);
            if (count > 1) {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    this.localSequence.setSequenceAtNodeInCategory(tempRootSeq[cat], cat, n);
                }
            }
        }
    }
    
    protected void computeTN93(final Node node) throws NullAncestorException {
        this.computeGTR(node);
    }
    
    protected void computePoisson(final Node node) throws NullAncestorException {
        final List<Node> children = node.getChildren();
        Node node2 = children.get(0);
        Node node3 = children.get(1);
        for (int count = 1; count < children.size(); ++count) {
            double bl1;
            double bl2;
            float[][][] tempRootSeq;
            if (count > 1) {
                node2 = children.get(count);
                node3 = node;
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = 0.0;
                tempRootSeq = new float[this.numCat][this.numStates][this.numCharComp];
            }
            else {
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = node3.getAncestorBranchLength() / (1.0 - this.pInv);
                tempRootSeq = null;
            }
            final Node key = (count > 1) ? null : node;
            if (this.underflowScaling.containsKey(key)) {
                this.underflowScaling.remove(key);
                this.underflow = !this.underflowScaling.isEmpty();
            }
            final int n = this.nodeIndex.get(node);
            final int n2 = this.nodeIndex.get(node2);
            final int n3 = this.nodeIndex.get(node3);
            final double[][] ufScaling = new double[this.numCat][this.numCharComp];
            boolean underflowEncountered = false;
            boolean rescalingNeeded = false;
            do {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    final double exp1 = Math.exp(-bl1 * this.rateScaling * this.rates[cat] * this.apRate);
                    final double exp2 = Math.exp(-bl2 * this.rateScaling * this.rates[cat] * this.apRate);
                    final double diag1 = 0.05 + 0.95 * exp1;
                    final double offdiag1 = 0.05 - 0.05 * exp1;
                    final double diag2 = 0.05 + 0.95 * exp2;
                    final double offdiag2 = 0.05 - 0.05 * exp2;
                    final float[][] seq1 = this.localSequence.getSequenceAtCategoryAndNode(cat, n2);
                    final float[][] seq2 = this.localSequence.getSequenceAtCategoryAndNode(cat, n3);
                    final float[][] seq3 = (count > 1) ? tempRootSeq[cat] : this.localSequence.getSequenceAtCategoryAndNode(cat, n);
                    final double[] scalingFactor = ufScaling[cat];
                    for (int site = 0; site < this.numCharComp; ++site) {
                        double lMax = 0.0;
                        for (int state = 0; state < this.numStates; ++state) {
                            double sum1 = 0.0;
                            double sum2 = 0.0;
                            for (int s = 0; s < this.numStates; ++s) {
                                sum1 += seq1[s][site] * ((state == s) ? diag1 : offdiag1);
                                sum2 += seq2[s][site] * ((state == s) ? diag2 : offdiag2);
                            }
                            final double prod = sum1 * sum2;
                            if (rescalingNeeded) {
                                seq3[state][site] = (float)(prod / scalingFactor[site]);
                            }
                            else {
                                seq3[state][site] = (float)prod;
                                if (prod > lMax) {
                                    lMax = prod;
                                }
                                if (prod < 1.401298464324817E-45) {
                                    underflowEncountered = true;
                                }
                            }
                        }
                        if (!rescalingNeeded) {
                            scalingFactor[site] = lMax;
                        }
                        assert !Double.isInfinite(lMax) : site;
                        assert !Double.isNaN(lMax) : site;
                    }
                }
                if (underflowEncountered) {
                    this.underflowScaling.put(key, ufScaling);
                    this.underflow = true;
                    rescalingNeeded = true;
                    underflowEncountered = false;
                }
                else {
                    rescalingNeeded = false;
                }
            } while (rescalingNeeded);
            if (count > 1) {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    this.localSequence.setSequenceAtNodeInCategory(tempRootSeq[cat], cat, n);
                }
            }
        }
    }
    
    protected void computeGTR(final Node node) throws NullAncestorException {
        final List<Node> children = node.getChildren();
        Node node2 = children.get(0);
        Node node3 = children.get(1);
        for (int count = 1; count < children.size(); ++count) {
            double bl1;
            double bl2;
            float[][][] tempRootSeq;
            if (count > 1) {
                node2 = children.get(count);
                node3 = node;
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = 0.0;
                tempRootSeq = new float[this.numCat][this.numStates][this.numCharComp];
            }
            else {
                bl1 = node2.getAncestorBranchLength() / (1.0 - this.pInv);
                bl2 = node3.getAncestorBranchLength() / (1.0 - this.pInv);
                tempRootSeq = null;
            }
            final Node key = (count > 1) ? null : node;
            if (this.underflowScaling.containsKey(key)) {
                this.underflowScaling.remove(key);
                this.underflow = !this.underflowScaling.isEmpty();
            }
            final int n = this.nodeIndex.get(node);
            final int n2 = this.nodeIndex.get(node2);
            final int n3 = this.nodeIndex.get(node3);
            final double[][] ufScaling = new double[this.numCat][this.numCharComp];
            boolean underflowEncountered = false;
            boolean rescalingNeeded = false;
            do {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    Matrix TPM1;
                    Matrix TPM2;
                    if (!Tools.isIdentity(this.Q)) {
                        if (this.temp == null) {
                            this.temp = new Matrix(this.numStates, this.numStates);
                        }
                        for (int i = 0; i < this.numStates; ++i) {
                            this.temp.set(i, i, Math.exp(bl1 * this.rates[cat] * this.apRate * this.eg.get(i, i)));
                        }
                        TPM1 = this.ev.times(this.temp).times(this.evi);
                        for (int i = 0; i < this.numStates; ++i) {
                            this.temp.set(i, i, Math.exp(bl2 * this.rates[cat] * this.apRate * this.eg.get(i, i)));
                        }
                        TPM2 = this.ev.times(this.temp).times(this.evi);
                    }
                    else {
                        TPM1 = this.Q;
                        TPM2 = this.Q;
                    }
                    final float[][] seq1 = this.localSequence.getSequenceAtCategoryAndNode(cat, n2);
                    final float[][] seq2 = this.localSequence.getSequenceAtCategoryAndNode(cat, n3);
                    final float[][] seq3 = (count > 1) ? tempRootSeq[cat] : this.localSequence.getSequenceAtCategoryAndNode(cat, n);
                    final double[] scalingFactor = ufScaling[cat];
                    final double[][] tpm1 = TPM1.getArray();
                    final double[][] tpm2 = TPM2.getArray();
                    for (int site = 0; site < this.numCharComp; ++site) {
                        double lMax = 0.0;
                        for (int state = 0; state < this.numStates; ++state) {
                            double sum1 = 0.0;
                            double sum2 = 0.0;
                            for (int s = 0; s < this.numStates; ++s) {
                                sum1 += seq1[s][site] * tpm1[state][s];
                                sum2 += seq2[s][site] * tpm2[state][s];
                            }
                            final double prod = sum1 * sum2;
                            if (rescalingNeeded) {
                                final double scalFact = scalingFactor[site];
                                seq3[state][site] = (float)(prod / scalFact);
                            }
                            else {
                                seq3[state][site] = (float)prod;
                                if (prod > lMax) {
                                    lMax = prod;
                                }
                                underflowEncountered = true;
                            }
                        }
                        if (!rescalingNeeded) {
                            scalingFactor[site] = lMax;
                        }
                    }
                }
                if (underflowEncountered) {
                    this.underflowScaling.put(key, ufScaling);
                    this.underflow = true;
                    rescalingNeeded = true;
                    underflowEncountered = false;
                }
                else {
                    rescalingNeeded = false;
                }
            } while (rescalingNeeded);
            if (count > 1) {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    this.localSequence.setSequenceAtNodeInCategory(tempRootSeq[cat], cat, n);
                }
            }
        }
    }
}

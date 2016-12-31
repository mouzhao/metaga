// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.likelihood;

import metapiga.trees.exceptions.NullAncestorException;
import cern.jet.stat.Gamma;
import metapiga.utilities.Tools;
import Jama.EigenvalueDecomposition;
import metapiga.modelization.data.codons.tables.CodonTransitionTable;
import metapiga.modelization.data.Codon;
import metapiga.exceptions.UnknownDataException;
import metapiga.modelization.data.Protein;
import metapiga.modelization.data.DNA;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.modelization.data.Data;
import metapiga.modelization.VanDePeer;
import metapiga.modelization.data.EmpiricalModels;
import java.util.HashSet;
import java.util.HashMap;
import java.util.EnumMap;
import Jama.Matrix;

import java.util.Map;
import metapiga.trees.Tree;
import metapiga.trees.Node;
import java.util.Set;
import metapiga.parameters.Parameters;
import metapiga.modelization.Dataset;
import metapiga.RateParameter;

public abstract class Likelihood
{
    protected static final double LIKE_EPSILON = 1.0E-300;
    protected static final double SIMPLE_PRECISION = 1.401298464324817E-45;
    protected final Dataset.Partition part;
    private final Parameters.EvaluationDistribution distribution;
    private final Parameters.EvaluationRate rate;
    protected final int numStates;
    protected final int numCharComp;
    protected final Parameters.EvaluationModel model;
    protected final SequenceArrays sequences;
    protected final Set<Node> toUpdate;
    protected final Tree tree;
    protected final int numNodes;
    protected boolean underflow;
    protected final Map<Node, double[][]> underflowScaling;
    protected final Map<Node, Integer> nodeIndex;
    protected double likelihoodValue;
    protected final double[] equiFreq;
    protected Map<RateParameter, Double> rateParameters;
    protected Matrix Q;
    protected double rateScaling;
    protected Matrix eg;
    protected Matrix ev;
    protected Matrix evi;
    protected Matrix temp;
    private double gammaShape;
    private final double[] cutpoints;
    protected final int numCat;
    protected final double[] rates;
    protected final double[] invariantSites;
    protected double pInv;
    protected double apRate;
    
    protected Likelihood(final Dataset.Partition partition, final Parameters.EvaluationRate rate, final Parameters.EvaluationModel model, final Parameters.EvaluationDistribution distribution, final double distributionShape, final double pinv, final double apRate, final Map<RateParameter, Double> rateParameters, final Parameters.EvaluationStateFrequencies stateFreq, final Tree tree, final int numSubsets, final SequenceArrays seq) throws UnrootableTreeException {
        this.underflow = false;
        this.rateParameters = new EnumMap<RateParameter, Double>(RateParameter.class);
        this.temp = null;

        this.part = partition;
        this.sequences = seq;
        this.numStates = this.sequences.getStateCount();
        this.numCharComp = this.sequences.getCharacterCountNoPadding();
        this.numNodes = this.sequences.getNodeCount();
        this.numCat = this.sequences.getCategoryCount();
        this.rate = rate;
        this.model = model;
        this.distribution = distribution;
        this.pInv = pinv;
        this.apRate = apRate;
        if (!tree.isRooted()) {
            tree.root();
        }
        this.tree = tree;
        this.nodeIndex = new HashMap<Node, Integer>();
        this.underflowScaling = new HashMap<Node, double[][]>();
        int nodeCounter = 0;
        this.toUpdate = new HashSet<Node>();
        this.equiFreq = new double[this.numStates];
        for (final Node node : tree.getNodes()) {
            this.nodeIndex.put(node, nodeCounter);
            if (node.isLeaf()) {
                for (int k = 0; k < this.numCharComp; ++k) {
                    final Data d = this.part.getData(node.getLabel(), k);
                    for (int cat = 0; cat < this.numCat; ++cat) {
                        for (int s = 0; s < this.numStates; ++s) {
                            final float stateAppearance = (float)(d.isState(s) ? 1 : 0);
                            this.sequences.setElement(stateAppearance, nodeCounter, cat, k, s);
                        }
                    }
                    if (!model.hasEqualBaseFrequencies()) {
                        for (int s2 = 0; s2 < this.numStates; ++s2) {
                            final double[] equiFreq = this.equiFreq;
                            final int n = s2;
                            equiFreq[n] += (d.isState(s2) ? (this.part.getWeight(k) / d.numOfStates()) : 0.0);
                        }
                    }
                }
            }
            else {
                this.toUpdate.add(node);
            }
            ++nodeCounter;
        }
        if (model.isEmpirical() && stateFreq == Parameters.EvaluationStateFrequencies.EMPIRICAL) {
            System.arraycopy(EmpiricalModels.getEmpiricalFrequencies(model), 0, this.equiFreq, 0, this.numStates);
        }
        else if (!model.hasEqualBaseFrequencies()) {
            for (int i = 0; i < this.equiFreq.length; ++i) {
                final double[] equiFreq2 = this.equiFreq;
                final int n2 = i;
                equiFreq2[n2] /= this.part.getNChar() * this.part.getNTax();
            }
        }
        else {
            for (int i = 0; i < this.equiFreq.length; ++i) {
                this.equiFreq[i] = 1.0 / this.numStates;
            }
        }
        RateParameter[] parametersOfModel;
        for (int length = (parametersOfModel = RateParameter.getParametersOfModel(model)).length, l = 0; l < length; ++l) {
            final RateParameter rp = parametersOfModel[l];
            this.rateParameters.put(rp, rateParameters.get(rp));
        }
        (this.cutpoints = new double[this.numCat + 1])[0] = 0.0;
        this.cutpoints[this.numCat] = 1000.0;
        this.rates = new double[this.numCat];
        if (this.numCat == 1) {
            this.rates[0] = 1.0;
        }
        this.Q = new Matrix(new double[this.numStates][this.numStates]);
        this.invariantSites = new double[this.numCharComp];
        for (int site = 0; site < this.invariantSites.length; ++site) {
            final int j = this.part.getData(0, site).getState();
            if (j >= 0 && j < this.equiFreq.length) {
                this.invariantSites[site] = this.equiFreq[j];
                for (int taxa = 1; taxa < this.part.getNTax(); ++taxa) {
                    if (this.part.getData(0, site) != this.part.getData(taxa, site)) {
                        this.invariantSites[site] = 0.0;
                        break;
                    }
                }
            }
        }
        if (distribution == Parameters.EvaluationDistribution.GAMMA) {
            this.updateGammaDistribution(distributionShape);
        }
        if (distribution == Parameters.EvaluationDistribution.VDP) {
            final VanDePeer vdp = new VanDePeer(this.part.getDataType(), partition, this.numCat);
            for (int r = 0; r < this.numCat; ++r) {
                this.rates[r] = vdp.getRate(r);
            }
            this.updateRateMatrix();
        }
        else {
            this.updateRateMatrix();
        }
    }
    
    protected Likelihood(final Likelihood L, final Tree tree) throws UnrootableTreeException {
        this.underflow = false;
        this.rateParameters = new EnumMap<RateParameter, Double>(RateParameter.class);
        this.temp = null;
        this.part = L.part;
        this.numStates = L.numStates;
        this.numCharComp = L.numCharComp;
        this.rate = L.rate;
        this.model = L.model;
        this.distribution = L.distribution;
        this.pInv = L.pInv;
        this.numCat = L.numCat;
        this.apRate = L.apRate;
        if (!tree.isRooted()) {
            tree.root();
        }
        this.tree = tree;
        this.equiFreq = new double[this.numStates];
        System.arraycopy(L.equiFreq, 0, this.equiFreq, 0, L.equiFreq.length);
        this.nodeIndex = new HashMap<Node, Integer>();
        this.numNodes = L.numNodes;
        this.sequences = L.sequences.clone();
        this.underflow = L.underflow;
        this.underflowScaling = new HashMap<Node, double[][]>();
        for (final Map.Entry<Node, double[][]> E : L.underflowScaling.entrySet()) {
            final double[][] ufs = new double[this.numCat][this.numCharComp];
            final double[][] Lufs = E.getValue();
            for (int i = 0; i < this.numCat; ++i) {
                System.arraycopy(Lufs[i], 0, ufs[i], 0, this.numCharComp);
            }
            final Node key = (E.getKey() == null) ? null : tree.getNode(E.getKey().getLabel());
            this.underflowScaling.put(key, ufs);
        }
        for (final Map.Entry<Node, Integer> E2 : L.nodeIndex.entrySet()) {
            this.nodeIndex.put(tree.getNode(E2.getKey().getLabel()), E2.getValue());
        }
        this.toUpdate = new HashSet<Node>();
        for (final Node n : L.toUpdate) {
            this.toUpdate.add(tree.getNode(n.getLabel()));
        }
        this.rateParameters = new EnumMap<RateParameter, Double>(L.rateParameters);
        this.invariantSites = new double[this.numCharComp];
        System.arraycopy(L.invariantSites, 0, this.invariantSites, 0, L.invariantSites.length);
        this.Q = L.Q.copy();
        this.rateScaling = L.rateScaling;
        if (this.model != Parameters.EvaluationModel.JC && this.model != Parameters.EvaluationModel.K2P && this.model != Parameters.EvaluationModel.HKY85 && this.model != Parameters.EvaluationModel.POISSON) {
            this.eg = L.eg.copy();
            this.ev = L.ev.copy();
            this.evi = L.evi.copy();
        }
        this.likelihoodValue = L.likelihoodValue;
        this.gammaShape = L.gammaShape;
        this.cutpoints = new double[this.numCat + 1];
        System.arraycopy(L.cutpoints, 0, this.cutpoints, 0, L.cutpoints.length);
        this.rates = new double[this.numCat];
        System.arraycopy(L.rates, 0, this.rates, 0, L.rates.length);
    }
    
    public void clone(final Likelihood L) throws UnrootableTreeException {
        this.pInv = L.pInv;
        this.apRate = L.apRate;
        if (!this.tree.isRooted()) {
            this.tree.root();
        }
        System.arraycopy(L.equiFreq, 0, this.equiFreq, 0, L.equiFreq.length);
        this.nodeIndex.clear();
        this.sequences.clone(L.sequences);
        this.underflow = L.underflow;
        for (final Map.Entry<Node, double[][]> E : L.underflowScaling.entrySet()) {
            final double[][] ufs = new double[this.numCat][this.numCharComp];
            final double[][] Lufs = E.getValue();
            for (int i = 0; i < this.numCat; ++i) {
                System.arraycopy(Lufs[i], 0, ufs[i], 0, this.numCharComp);
            }
            final Node key = (E.getKey() == null) ? null : this.tree.getNode(E.getKey().getLabel());
            this.underflowScaling.put(key, ufs);
        }
        for (final Map.Entry<Node, Integer> E2 : L.nodeIndex.entrySet()) {
            this.nodeIndex.put(this.tree.getNode(E2.getKey().getLabel()), E2.getValue());
        }
        this.toUpdate.clear();
        for (final Node n : L.toUpdate) {
            this.toUpdate.add(this.tree.getNode(n.getLabel()));
        }
        this.rateParameters = new EnumMap<RateParameter, Double>(L.rateParameters);
        System.arraycopy(L.invariantSites, 0, this.invariantSites, 0, L.invariantSites.length);
        this.Q = L.Q.copy();
        this.rateScaling = L.rateScaling;
        if (this.model != Parameters.EvaluationModel.JC && this.model != Parameters.EvaluationModel.K2P && this.model != Parameters.EvaluationModel.HKY85 && this.model != Parameters.EvaluationModel.POISSON) {
            this.eg = L.eg.copy();
            this.ev = L.ev.copy();
            this.evi = L.evi.copy();
        }
        this.likelihoodValue = L.likelihoodValue;
        this.gammaShape = L.gammaShape;
        System.arraycopy(L.cutpoints, 0, this.cutpoints, 0, L.cutpoints.length);
        System.arraycopy(L.rates, 0, this.rates, 0, L.rates.length);
    }
    
    private void updateRateMatrix() {
        for (int row = 0; row < this.numStates; ++row) {
            for (int col = 0; col < this.numStates; ++col) {
                if (row != col) {
                    double rate = 1.0;
                    switch (this.model) {
                        case HKY85:
                        case K2P: {
                            if ((row == DNA.A.state && col == DNA.G.state) || (row == DNA.G.state && col == DNA.A.state) || (row == DNA.C.state && col == DNA.T.state) || (row == DNA.T.state && col == DNA.C.state)) {
                                rate = this.rateParameters.get(RateParameter.K);
                                break;
                            }
                            break;
                        }
                        case TN93: {
                            if ((row == DNA.A.state && col == DNA.G.state) || (row == DNA.G.state && col == DNA.A.state)) {
                                rate = this.rateParameters.get(RateParameter.K1);
                                break;
                            }
                            if ((row == DNA.C.state && col == DNA.T.state) || (row == DNA.T.state && col == DNA.C.state)) {
                                rate = this.rateParameters.get(RateParameter.K2);
                                break;
                            }
                            break;
                        }
                        case GTR: {
                            if ((row == DNA.A.state && col == DNA.C.state) || (row == DNA.C.state && col == DNA.A.state)) {
                                rate = this.rateParameters.get(RateParameter.A);
                                break;
                            }
                            if ((row == DNA.A.state && col == DNA.G.state) || (row == DNA.G.state && col == DNA.A.state)) {
                                rate = this.rateParameters.get(RateParameter.B);
                                break;
                            }
                            if ((row == DNA.A.state && col == DNA.T.state) || (row == DNA.T.state && col == DNA.A.state)) {
                                rate = this.rateParameters.get(RateParameter.C);
                                break;
                            }
                            if ((row == DNA.C.state && col == DNA.G.state) || (row == DNA.G.state && col == DNA.C.state)) {
                                rate = this.rateParameters.get(RateParameter.D);
                                break;
                            }
                            if ((row == DNA.C.state && col == DNA.T.state) || (row == DNA.T.state && col == DNA.C.state)) {
                                rate = this.rateParameters.get(RateParameter.E);
                                break;
                            }
                            break;
                        }
                        case GTR20:
                        case WAG:
                        case JTT:
                        case DAYHOFF:
                        case VT:
                        case BLOSUM62:
                        case CPREV:
                        case MTREV:
                        case RTREV:
                        case MTMAM: {
                            Label_0804: {
                                if (row != Protein.Y.state || col != Protein.V.state) {
                                    if (row == Protein.V.state) {
                                        if (col == Protein.Y.state) {
                                            break Label_0804;
                                        }
                                    }
                                    try {
                                        final String cell = (row < col) ? (String.valueOf(Protein.getProteinWithState(row).toString()) + Protein.getProteinWithState(col).toString()) : (String.valueOf(Protein.getProteinWithState(col).toString()) + Protein.getProteinWithState(row).toString());
                                        rate = this.rateParameters.get(RateParameter.valueOf(cell));
                                    }
                                    catch (UnknownDataException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                            if (this.model == Parameters.EvaluationModel.MTMAM) {
                                rate = 0.0;
                                break;
                            }
                            break;
                        }
                        case GY: {
                            final CodonTransitionTable table = this.tree.parameters.getCodonTransitionTable();
                            try {
                                if (!table.isSynonymous(Codon.getCodonWithState(col), Codon.getCodonWithState(row))) {
                                    rate *= this.rateParameters.get(RateParameter.KAPPA);
                                }
                                if (table.isTransition(Codon.getCodonWithState(col), Codon.getCodonWithState(row))) {
                                    rate *= this.rateParameters.get(RateParameter.OMEGA);
                                }
                                if (table.isDifferentMoreThanOneNucleotide(Codon.getCodonWithState(col), Codon.getCodonWithState(row)) || table.isStopCodon(Codon.getCodonWithState(col)) || table.isStopCodon(Codon.getCodonWithState(row))) {
                                    rate = 0.0;
                                }
                            }
                            catch (UnknownDataException e2) {
                                e2.printStackTrace();
                                System.exit(-1);
                            }
                            break;
                        }
                        case GTR64:
                        case ECM: {
                            if (row != Codon.GGA.state || col != Codon.GGG.state) {
                                if (row == Codon.GGG.state) {
                                    if (col == Codon.GGA.state) {
                                        break;
                                    }
                                }
                                try {
                                    final String cell2 = (row < col) ? (String.valueOf(Codon.getCodonWithState(row).toString()) + Codon.getCodonWithState(col).toString()) : (String.valueOf(Codon.getCodonWithState(col).toString()) + Codon.getCodonWithState(row));
                                    rate = this.rateParameters.get(RateParameter.valueOf(cell2));
                                }
                                catch (UnknownDataException e2) {
                                    e2.printStackTrace();
                                }
                                break;
                            }
                            break;
                        }
                        case GTR2: {}
                        case JC: {}
                    }
                    this.Q.set(row, col, rate * this.equiFreq[col]);
                }
            }
        }
        for (int i = 0; i < this.numStates; ++i) {
            double sum = 0.0;
            for (int j = 0; j < this.numStates; ++j) {
                if (i != j) {
                    sum += this.Q.get(i, j);
                }
            }
            this.Q.set(i, i, -sum);
        }
        this.rateScaling = 0.0;
        for (int i = 0; i < this.numStates; ++i) {
            for (int k = 0; k < this.numStates; ++k) {
                if (i != k) {
                    this.rateScaling += this.equiFreq[i] * this.Q.get(i, k);
                }
            }
        }
        this.rateScaling = 1.0 / this.rateScaling;
        for (int i = 0; i < this.numStates; ++i) {
            for (int k = 0; k < this.numStates; ++k) {
                this.Q.set(i, k, this.Q.get(i, k) * this.rateScaling);
            }
        }
        if (this.model != Parameters.EvaluationModel.JC && this.model != Parameters.EvaluationModel.K2P && this.model != Parameters.EvaluationModel.HKY85 && this.model != Parameters.EvaluationModel.POISSON) {
            final EigenvalueDecomposition Rev = this.Q.eig();
            this.eg = Rev.getD();
            this.ev = Rev.getV();
            this.evi = this.ev.inverse();
        }
    }
    
    public void updateRateParameter(final RateParameter rateParameter, final double newValue) {
        this.rateParameters.put(rateParameter, newValue);
        this.updateRateMatrix();
        this.markAllInodesToUpdate();
    }
    
    public void updateGammaDistribution(final double newAlpha) {
        this.gammaShape = newAlpha;
        for (int i = 1; i < this.numCat; ++i) {
            final double k = i / this.numCat;
            this.cutpoints[i] = Tools.percentagePointChi2(k, 2.0 * this.gammaShape) / (2.0 * this.gammaShape);
        }
        for (int i = 0; i < this.numCat; ++i) {
            this.rates[i] = (Gamma.incompleteGamma(this.gammaShape + 1.0, this.cutpoints[i + 1] * this.gammaShape) - Gamma.incompleteGamma(this.gammaShape + 1.0, this.cutpoints[i] * this.gammaShape)) / (1.0 / this.numCat);
        }
        this.updateRateMatrix();
        this.markAllInodesToUpdate();
    }
    
    public void updateInvariant(final double newPinv) {
        this.pInv = newPinv;
        if (this.pInv > 1.0) {
            this.pInv = 1.0;
        }
        else if (this.pInv < 0.0) {
            this.pInv = 0.0;
        }
        this.markAllInodesToUpdate();
    }
    
    public void updateAmongPartitionRate(final double newRate) {
        this.apRate = newRate;
        if (this.apRate <= 0.0) {
            this.apRate = 0.01;
        }
        this.markAllInodesToUpdate();
    }
    
    public Parameters.EvaluationModel getModel() {
        return this.model;
    }
    
    public Parameters.EvaluationRate getRate() {
        return this.rate;
    }
    
    public Map<RateParameter, Double> getRateParameters() {
        return new EnumMap<RateParameter, Double>(this.rateParameters);
    }
    
    public Parameters.EvaluationDistribution getDistribution() {
        return this.distribution;
    }
    
    public int getDistributionSubsets() {
        return this.numCat;
    }
    
    public double getGammaShape() {
        return this.gammaShape;
    }
    
    public double getPInv() {
        return this.pInv;
    }
    
    public double getAmongPartitionRate() {
        return this.apRate;
    }
    
    public boolean hasUnderflow() {
        return this.underflow;
    }
    
    public void markInodeToUpdate(final Node node) {
        if (node.isInode()) {
            this.toUpdate.add(node);
        }
    }
    
    public void markAllInodesToUpdate() {
        for (final Node n : this.nodeIndex.keySet()) {
            if (n.isInode()) {
                this.toUpdate.add(n);
            }
        }
    }
    
    public double[][] getAncestralStates(final Node node) throws NullAncestorException {
        final double[][] ancStates = new double[this.numCharComp][this.numStates];
        final int nodeIdx = this.nodeIndex.get(node);
        if (!this.tree.getLeaves().contains(node)) {
            final Node previousRoot = this.tree.getRoot();
            this.tree.root(node);
            this.update(node);
            final double[] siteLikelihoods = new double[this.numCharComp];
            for (int site = 0; site < this.numCharComp; ++site) {
                for (int state = 0; state < this.numStates; ++state) {
                    ancStates[site][state] = 0.0;
                    for (int cat = 0; cat < this.numCat; ++cat) {
                        double ufscaling = 1.0;
                        if (this.underflow) {
                            for (final double[][] s : this.underflowScaling.values()) {
                                ufscaling *= s[cat][site];
                            }
                        }
                        final double[] array = ancStates[site];
                        final int n = state;
                        array[n] += this.sequences.getElement(nodeIdx, cat, site, state) * this.equiFreq[state] * ((1.0 - this.pInv) / this.numCat) * ufscaling;
                        final double[] array2 = siteLikelihoods;
                        final int n2 = site;
                        array2[n2] += ancStates[site][state];
                    }
                    if (ancStates[site][state] < 1.0E-300) {
                        ancStates[site][state] = 0.0;
                    }
                }
            }
            for (int site = 0; site < this.numCharComp; ++site) {
                for (int state = 0; state < this.numStates; ++state) {
                    final double[] array3 = ancStates[site];
                    final int n3 = state;
                    array3[n3] /= siteLikelihoods[site];
                }
            }
            this.tree.root(previousRoot);
        }
        else {
            for (int site2 = 0; site2 < this.numCharComp; ++site2) {
                for (int state2 = 0; state2 < this.numStates; ++state2) {
                    ancStates[site2][state2] = this.sequences.getElement(nodeIdx, 0, site2, state2);
                }
            }
        }
        return ancStates;
    }
    
    public double getLikelihoodValue() throws NullAncestorException {
        if (!this.toUpdate.isEmpty()) {
            this.update(this.tree.getRoot());
        }
        return this.likelihoodValue;
    }
    
    protected void calculateLikelihoodAtRoot() {
        final Node node = this.tree.getRoot();
        this.likelihoodValue = 0.0;
        final int n = this.nodeIndex.get(node);
        for (int site = 0; site < this.numCharComp; ++site) {
            double siteLikelihoodVar = 0.0;
            for (int state = 0; state < this.numStates; ++state) {
                for (int cat = 0; cat < this.numCat; ++cat) {
                    double ufscaling = 1.0;
                    if (this.underflow) {
                        for (final double[][] s : this.underflowScaling.values()) {
                            ufscaling *= s[cat][site];
                        }
                    }
                    final double sequenceValue = this.sequences.getElement(n, cat, site, state);
                    final double equilibriumF = this.equiFreq[state];
                    siteLikelihoodVar += sequenceValue * equilibriumF * ((1.0 - this.pInv) / this.numCat) * ufscaling;
                }
            }
            if (siteLikelihoodVar < 1.0E-300) {
                siteLikelihoodVar = 1.0E-300;
            }
            final double siteLikelihoodInv = this.invariantSites[site] * this.pInv;
            this.likelihoodValue += Math.log(siteLikelihoodVar + siteLikelihoodInv) * this.part.getWeight(site);
        }
        this.toUpdate.remove(this.tree.getRoot());
    }
    
    protected abstract void update(final Node p0) throws NullAncestorException;
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization;

import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import metapiga.utilities.Tools;
import java.awt.Color;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.util.Hashtable;
import javax.swing.text.DefaultStyledDocument;
import Jama.EigenvalueDecomposition;
import metapiga.modelization.data.DNA;
import metapiga.modelization.data.Codon;
import metapiga.modelization.data.Data;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import metapiga.parameters.Parameters;
import java.util.List;
import java.util.Set;
import metapiga.modelization.data.DataType;
import Jama.Matrix;

public class DistanceMatrix extends Matrix
{
    public final DataType dataType;
    public final int numOfStates;
    public final Set<Dataset.Partition> partitions;
    public final int ntax;
    public final List<String> taxas;
    public final Parameters.DistanceModel model;
    public final Parameters.StartingTreeDistribution distribution;
    public final double gammaShape;
    public final double pinv;
    public final Parameters.StartingTreePInvPi pi;
    private boolean saturation;
    public final double saturationThreshold;
    private int codonPosition;
    private int numElemMat;
    private List<DistanceMatrix> elemMatrices;
    private List<Double> lengthWeights;
    private List<Double> evolutionWeights;
    
    public DistanceMatrix(final DataType dataType, final Set<Dataset.Partition> partitions, final List<String> taxas, final Parameters.DistanceModel model, final Parameters.StartingTreeDistribution distribution, final double distributionShape, final double pinv, final Parameters.StartingTreePInvPi pi) {
        super(taxas.size(), taxas.size());
        this.saturation = false;
        this.codonPosition = -1;
        this.numElemMat = 0;
        this.elemMatrices = new ArrayList<DistanceMatrix>();
        this.lengthWeights = new ArrayList<Double>();
        this.evolutionWeights = new ArrayList<Double>();
        this.dataType = dataType;
        this.ntax = taxas.size();
        this.taxas = taxas;
        this.partitions = partitions;
        this.model = model;
        this.distribution = distribution;
        this.gammaShape = distributionShape;
        this.pinv = pinv;
        this.pi = pi;
        switch (dataType) {
            case DNA: {
                this.saturationThreshold = 0.65;
                this.numOfStates = dataType.numOfStates();
                break;
            }
            case PROTEIN: {
                this.numOfStates = dataType.numOfStates();
                this.saturationThreshold = 0.85;
                break;
            }
            case STANDARD: {
                this.numOfStates = dataType.numOfStates();
                this.saturationThreshold = 0.45;
                break;
            }
            case CODON: {
                this.numOfStates = DataType.DNA.numOfStates();
                this.saturationThreshold = 0.65;
                this.numElemMat = 3;
                break;
            }
            default: {
                assert false : "Unknown datatype";
                this.numOfStates = dataType.numOfStates();
                this.saturationThreshold = 0.65;
                break;
            }
        }
        if (dataType == DataType.CODON) {
            for (int i = 0; i < this.numElemMat; ++i) {
                this.codonPosition = i;
                final DistanceMatrix elemMat = new DistanceMatrix(this);
                this.elemMatrices.add(elemMat);
                this.lengthWeights.add(1.0);
            }
            this.findEvolWeights();
            this.initWeighted2CED();
        }
        else {
            this.init(model);
        }
    }
    
    private DistanceMatrix(final DistanceMatrix superMatrix) {
        super(superMatrix.taxas.size(), superMatrix.taxas.size());
        this.saturation = false;
        this.codonPosition = -1;
        this.numElemMat = 0;
        this.elemMatrices = new ArrayList<DistanceMatrix>();
        this.lengthWeights = new ArrayList<Double>();
        this.evolutionWeights = new ArrayList<Double>();
        this.dataType = superMatrix.dataType;
        this.ntax = superMatrix.taxas.size();
        this.taxas = superMatrix.taxas;
        this.partitions = superMatrix.partitions;
        this.model = superMatrix.model;
        this.distribution = superMatrix.distribution;
        this.gammaShape = superMatrix.gammaShape;
        this.pinv = superMatrix.pinv;
        this.pi = superMatrix.pi;
        this.saturationThreshold = superMatrix.saturationThreshold;
        this.numOfStates = superMatrix.numOfStates;
        if (this.dataType == DataType.CODON) {
            this.codonPosition = superMatrix.codonPosition;
            this.init(this.model);
        }
        else {
            assert false : "Unknown case!";
        }
    }
    
    private void init(final Parameters.DistanceModel model) {
        switch (model) {
            case JC: {
                this.initJC();
                break;
            }
            case K2P: {
                this.initK2P();
                break;
            }
            case HKY85: {
                this.initHKY85();
                break;
            }
            case TN93: {
                this.initTN93();
                break;
            }
            case GTR: {
                this.initGTR();
                break;
            }
            case GTR2: {
                this.initGTR();
                break;
            }
            case GTR20: {
                this.initGTR();
                break;
            }
            case POISSON: {
                this.initGTR();
                break;
            }
            case UNCORRECTED: {
                this.initUncorrected();
                break;
            }
            case ABSOLUTE: {
                this.initAbsoluteDifferences();
                break;
            }
            case GY: {
                this.initGTR();
                break;
            }
            case GTR64: {
                this.initGTR();
                break;
            }
            default: {
                assert false : "no model for the distance matrix";
                break;
            }
        }
    }
    
    private void initWeightedCED() {
        for (int p = 0; p < this.elemMatrices.size(); ++p) {
            final double rate = this.evolutionWeights.get(p);
            for (int i = 0; i < this.ntax; ++i) {
                for (int j = i + 1; j < this.ntax; ++j) {
                    final double pMatVal = this.elemMatrices.get(p).get(i, j);
                    final double currentVal = this.get(i, j);
                    final double newValue = currentVal + pMatVal * rate;
                    this.set(i, j, newValue);
                }
            }
        }
    }
    
    private void initWeighted2CED() {
        double sumRates = 0.0;
        final double[] oldEvolWeights = new double[this.numElemMat];
        for (int p = 0; p < this.numElemMat; ++p) {
            sumRates += this.evolutionWeights.get(p);
            oldEvolWeights[p] = this.evolutionWeights.get(p);
        }
        boolean isAllZeros = true;
        for (int p2 = 0; p2 < this.numElemMat; ++p2) {
            final double arboricity = this.arboricity(this.elemMatrices.get(p2));
            final double normalizedWeight = this.evolutionWeights.get(p2) * arboricity / sumRates;
            if (Math.abs(normalizedWeight - 0.0) > 0.001) {
                isAllZeros = false;
            }
            this.evolutionWeights.set(p2, normalizedWeight);
        }
        if (isAllZeros) {
            for (int p2 = 0; p2 < this.numElemMat; ++p2) {
                this.evolutionWeights.set(p2, 1.0);
            }
        }
        this.initWeightedCED();
    }
    
    private double arboricity(final DistanceMatrix matrix) {
        double arboricity = 0.0;
        double count = 0.0;
        for (int i = 0; i < this.ntax; ++i) {
            for (int j = i + 1; j < this.ntax; ++j) {
                for (int x = j + 1; x < this.ntax; ++x) {
                    for (int y = x + 1; y < this.ntax; ++y) {
                        final double[] vals = { matrix.get(i, j) + matrix.get(x, y), matrix.get(i, x) + matrix.get(j, y), matrix.get(i, y) + matrix.get(j, x) };
                        Arrays.sort(vals);
                        if (vals[1] - vals[0] > vals[2] - vals[1]) {
                            ++arboricity;
                        }
                        ++count;
                    }
                }
            }
        }
        arboricity /= count;
        return arboricity;
    }
    
    private void findEvolWeights() {
        double sumWeights = 0.0;
        for (int p = 0; p < this.lengthWeights.size(); ++p) {
            sumWeights += this.lengthWeights.get(p);
        }
        final int taxaTotal = this.taxas.size();
        for (int p2 = 0; p2 < this.numElemMat; ++p2) {
            boolean correctEntries = true;
            double maxIneqFact = -1.0;
            final Matrix dstMat = this.elemMatrices.get(p2);
            for (int i = 0; i < taxaTotal; ++i) {
                for (int j = i + 1; j < taxaTotal; ++j) {
                    if (dstMat.get(i, j) == 0.0) {
                        correctEntries = false;
                    }
                    for (int m = j + 1; m < taxaTotal; ++m) {
                        final double ineqFact = dstMat.get(i, j) - dstMat.get(j, m) - dstMat.get(i, m);
                        if (m != i && m != j && maxIneqFact < ineqFact) {
                            maxIneqFact = ineqFact;
                        }
                    }
                }
            }
            if (maxIneqFact == 0.0 && !correctEntries) {
                maxIneqFact = 1.0E-9;
            }
            if (maxIneqFact > 0.0) {
                for (int i = 0; i < taxaTotal; ++i) {
                    for (int j = i + 1; j < taxaTotal; ++j) {
                        if (i != j) {
                            this.elemMatrices.get(p2).set(i, j, maxIneqFact);
                        }
                    }
                }
            }
        }
        final Matrix A = new Matrix(this.numElemMat + 1, this.numElemMat + 1);
        final Matrix b = new Matrix(this.numElemMat + 1, 1);
        b.set(this.numElemMat, 0, this.numElemMat);
        for (int p3 = 0; p3 < this.numElemMat; ++p3) {
            A.set(p3, this.numElemMat, 1.0);
            A.set(this.numElemMat, p3, 1.0);
            b.set(p3, 0, 0.0);
            for (int k = 0; k < taxaTotal; ++k) {
                for (int l = k + 1; l < taxaTotal; ++l) {
                    double temp = A.get(p3, p3);
                    temp += this.elemMatrices.get(p3).get(k, l);
                    A.set(p3, p3, temp);
                    for (int m = 0; m < this.numElemMat; ++m) {
                        double temp2 = A.get(p3, m);
                        temp2 += this.lengthWeights.get(p3) * this.elemMatrices.get(m).get(k, l) / sumWeights;
                        A.set(p3, m, temp2);
                    }
                }
            }
        }
        final Matrix solution = A.solve(b);
        for (int k = 0; k < this.numElemMat; ++k) {
            this.evolutionWeights.add(k, solution.get(k, 0));
        }
    }
    
    private void initAbsoluteDifferences() {
        for (int i = 0; i < this.ntax - 1; ++i) {
            for (int j = i + 1; j < this.ntax; ++j) {
                double differences = 0.0;
                for (final Dataset.Partition P : this.partitions) {
                    final int[] weights = P.getAllWeights();
                    for (int numChar = P.getCompressedNChar(), k = 0; k < numChar; ++k) {
                        final Data col = this.getData(P, i, k);
                        final Data row = this.getData(P, j, k);
                        boolean same = false;
                        for (int s = 0; s < col.getMaxStates(); ++s) {
                            if (row.isState(s) && col.isState(s)) {
                                same = true;
                            }
                        }
                        differences += (same ? 0 : weights[k]);
                    }
                }
                this.set(i, j, differences);
            }
        }
    }
    
    private Data getData(final Dataset.Partition P, final int taxon, final int charIdx) {
        Data d = null;
        if (this.dataType == DataType.CODON) {
            final Codon dataCodon = (Codon)P.getData(taxon, charIdx);
            d = dataCodon.getNucleotides()[this.codonPosition];
        }
        else {
            d = P.getData(taxon, charIdx);
        }
        return d;
    }
    
    private void initUncorrected() {
        for (int i = 0; i < this.ntax - 1; ++i) {
            for (int j = i + 1; j < this.ntax; ++j) {
                double differences = 0.0;
                double nchar = 0.0;
                for (final Dataset.Partition P : this.partitions) {
                    final int[] weights = P.getAllWeights();
                    for (int numChar = P.getCompressedNChar(), k = 0; k < numChar; ++k) {
                        final Data col = this.getData(P, i, k);
                        final Data row = this.getData(P, j, k);
                        final double contribution = weights[k] / (col.numOfStates() * row.numOfStates());
                        for (int s = 0; s < row.getMaxStates(); ++s) {
                            for (int t = 0; t < col.getMaxStates(); ++t) {
                                if (s != t) {
                                    differences += ((row.isState(s) && col.isState(t)) ? contribution : 0.0);
                                }
                            }
                        }
                    }
                    nchar += P.getNChar();
                }
                differences /= nchar;
                if (differences > this.saturationThreshold) {
                    this.saturation = true;
                }
                this.set(i, j, differences);
            }
        }
    }
    
    private void initJC() {
        if (this.pinv > 0.0) {
            this.initGTR();
        }
        else {
            for (int i = 0; i < this.ntax - 1; ++i) {
                for (int j = i + 1; j < this.ntax; ++j) {
                    double differences = 0.0;
                    double nchar = 0.0;
                    for (final Dataset.Partition P : this.partitions) {
                        final int[] weights = P.getAllWeights();
                        for (int numChar = P.getCompressedNChar(), k = 0; k < numChar; ++k) {
                            final Data col = this.getData(P, i, k);
                            final Data row = this.getData(P, j, k);
                            final double contribution = weights[k] / (col.numOfStates() * row.numOfStates());
                            for (int s = 0; s < row.getMaxStates(); ++s) {
                                for (int t = 0; t < col.getMaxStates(); ++t) {
                                    if (s != t) {
                                        differences += ((row.isState(s) && col.isState(t)) ? contribution : 0.0);
                                    }
                                }
                            }
                        }
                        nchar += P.getNChar();
                    }
                    differences /= nchar;
                    if (differences > this.saturationThreshold) {
                        this.saturation = true;
                    }
                    double part1 = 1.0 - 1.3333333333333333 * differences;
                    if (part1 <= 0.0) {
                        part1 = 0.01;
                        this.saturation = true;
                    }
                    if (this.distribution == Parameters.StartingTreeDistribution.GAMMA) {
                        this.set(i, j, -0.75 * (this.gammaShape * (1.0 - Math.pow(part1, -1.0 / this.gammaShape))));
                    }
                    else {
                        this.set(i, j, -0.75 * Math.log(part1));
                    }
                }
            }
        }
    }
    
    private void initK2P() {
        if (this.pinv > 0.0) {
            this.initGTR();
        }
        else {
            final int A = DNA.A.state;
            final int C = DNA.C.state;
            final int G = DNA.G.state;
            final int T = DNA.T.state;
            for (int i = 0; i < this.ntax - 1; ++i) {
                for (int j = i + 1; j < this.ntax; ++j) {
                    double transition = 0.0;
                    double transversion = 0.0;
                    double nchar = 0.0;
                    for (final Dataset.Partition P : this.partitions) {
                        final int[] weights = P.getAllWeights();
                        for (int numChar = P.getCompressedNChar(), k = 0; k < numChar; ++k) {
                            final Data col = this.getData(P, i, k);
                            final Data row = this.getData(P, j, k);
                            final double contribution = weights[k] / (col.numOfStates() * row.numOfStates());
                            transition += ((row.isState(A) && col.isState(G)) ? contribution : 0.0);
                            transition += ((row.isState(G) && col.isState(A)) ? contribution : 0.0);
                            transition += ((row.isState(C) && col.isState(T)) ? contribution : 0.0);
                            transition += ((row.isState(T) && col.isState(C)) ? contribution : 0.0);
                            transversion += ((row.isState(A) && col.isState(C)) ? contribution : 0.0);
                            transversion += ((row.isState(A) && col.isState(T)) ? contribution : 0.0);
                            transversion += ((row.isState(C) && col.isState(A)) ? contribution : 0.0);
                            transversion += ((row.isState(C) && col.isState(G)) ? contribution : 0.0);
                            transversion += ((row.isState(G) && col.isState(C)) ? contribution : 0.0);
                            transversion += ((row.isState(G) && col.isState(T)) ? contribution : 0.0);
                            transversion += ((row.isState(T) && col.isState(A)) ? contribution : 0.0);
                            transversion += ((row.isState(T) && col.isState(G)) ? contribution : 0.0);
                        }
                        nchar += P.getNChar();
                    }
                    transition /= nchar;
                    transversion /= nchar;
                    double part1 = 1.0 - 2.0 * transition - transversion;
                    double part2 = 1.0 - 2.0 * transversion;
                    if (part1 <= 0.0) {
                        part1 = 0.01;
                        this.saturation = true;
                    }
                    if (part2 <= 0.0) {
                        part2 = 0.01;
                        this.saturation = true;
                    }
                    if (this.distribution == Parameters.StartingTreeDistribution.GAMMA) {
                        this.set(i, j, this.gammaShape / 2.0 * (Math.pow(part1, -1.0 / this.gammaShape) + 0.5 * Math.pow(part2, -1.0 / this.gammaShape) - 1.5));
                    }
                    else {
                        this.set(i, j, 0.5 * Math.log(1.0 / part1) + 0.25 * Math.log(1.0 / part2));
                    }
                }
            }
        }
    }
    
    private void initHKY85() {
        this.initGTR();
    }
    
    private void initTN93() {
        if (this.pinv > 0.0) {
            this.initGTR();
        }
        else {
            final int A = DNA.A.state;
            final int C = DNA.C.state;
            final int G = DNA.G.state;
            final int T = DNA.T.state;
            for (int i = 0; i < this.ntax - 1; ++i) {
                for (int j = i + 1; j < this.ntax; ++j) {
                    double transitionAG = 0.0;
                    double transitionCT = 0.0;
                    double transversion = 0.0;
                    final double[] F = new double[this.numOfStates];
                    double nchar = 0.0;
                    for (final Dataset.Partition P : this.partitions) {
                        final int[] weights = P.getAllWeights();
                        for (int numChar = P.getCompressedNChar(), k = 0; k < numChar; ++k) {
                            final Data col = this.getData(P, i, k);
                            final Data row = this.getData(P, j, k);
                            final double contribution = weights[k] / (col.numOfStates() * row.numOfStates());
                            transitionAG += ((row.isState(A) && col.isState(G)) ? contribution : 0.0);
                            transitionAG += ((row.isState(G) && col.isState(A)) ? contribution : 0.0);
                            transitionCT += ((row.isState(C) && col.isState(T)) ? contribution : 0.0);
                            transitionCT += ((row.isState(T) && col.isState(C)) ? contribution : 0.0);
                            transversion += ((row.isState(A) && col.isState(C)) ? contribution : 0.0);
                            transversion += ((row.isState(A) && col.isState(T)) ? contribution : 0.0);
                            transversion += ((row.isState(C) && col.isState(A)) ? contribution : 0.0);
                            transversion += ((row.isState(C) && col.isState(G)) ? contribution : 0.0);
                            transversion += ((row.isState(G) && col.isState(C)) ? contribution : 0.0);
                            transversion += ((row.isState(G) && col.isState(T)) ? contribution : 0.0);
                            transversion += ((row.isState(T) && col.isState(A)) ? contribution : 0.0);
                            transversion += ((row.isState(T) && col.isState(G)) ? contribution : 0.0);
                            final double[] array = F;
                            final int n = A;
                            array[n] += (col.isState(A) ? (weights[k] / col.numOfStates()) : 0);
                            final double[] array2 = F;
                            final int n2 = A;
                            array2[n2] += (row.isState(A) ? (weights[k] / row.numOfStates()) : 0);
                            final double[] array3 = F;
                            final int n3 = C;
                            array3[n3] += (col.isState(C) ? (weights[k] / col.numOfStates()) : 0);
                            final double[] array4 = F;
                            final int n4 = C;
                            array4[n4] += (row.isState(C) ? (weights[k] / row.numOfStates()) : 0);
                            final double[] array5 = F;
                            final int n5 = G;
                            array5[n5] += (col.isState(G) ? (weights[k] / col.numOfStates()) : 0);
                            final double[] array6 = F;
                            final int n6 = G;
                            array6[n6] += (row.isState(G) ? (weights[k] / row.numOfStates()) : 0);
                            final double[] array7 = F;
                            final int n7 = T;
                            array7[n7] += (col.isState(T) ? (weights[k] / col.numOfStates()) : 0);
                            final double[] array8 = F;
                            final int n8 = T;
                            array8[n8] += (row.isState(T) ? (weights[k] / row.numOfStates()) : 0);
                        }
                        nchar += P.getNChar();
                    }
                    transitionAG /= nchar;
                    transitionCT /= nchar;
                    transversion /= nchar;
                    for (int f = 0; f < F.length; ++f) {
                        final double[] array9 = F;
                        final int n9 = f;
                        array9[n9] /= nchar * 2.0;
                    }
                    final double Fr = F[A] + F[G];
                    final double Fy = F[C] + F[T];
                    double part1 = 1.0 - Fr / (2.0 * F[A] * F[G]) * transitionAG - 1.0 / (2.0 * Fr) * transversion;
                    double part2 = 1.0 - Fy / (2.0 * F[T] * F[C]) * transitionCT - 1.0 / (2.0 * Fy) * transversion;
                    double part3 = 1.0 - 1.0 / (2.0 * Fr * Fy) * transversion;
                    if (part1 <= 0.0) {
                        part1 = 0.01;
                        this.saturation = true;
                    }
                    if (part2 <= 0.0) {
                        part2 = 0.01;
                        this.saturation = true;
                    }
                    if (part3 <= 0.0) {
                        part3 = 0.01;
                        this.saturation = true;
                    }
                    double distance;
                    if (this.distribution == Parameters.StartingTreeDistribution.GAMMA) {
                        distance = F[A] * F[G] / Fr * Math.pow(part1, -1.0 / this.gammaShape);
                        distance += F[T] * F[C] / Fy * Math.pow(part2, -1.0 / this.gammaShape);
                        distance += (Fr * Fy - F[A] * F[G] * Fy / Fr - F[T] * F[C] * Fr / Fy) * Math.pow(part3, -1.0 / this.gammaShape);
                        distance += -F[A] * F[G] - F[T] * F[C] - Fr * Fy;
                        distance *= 2.0 * this.gammaShape;
                    }
                    else {
                        distance = 2.0 * F[A] * F[G] / Fr * Math.log(1.0 / part1);
                        distance += 2.0 * F[T] * F[C] / Fy * Math.log(1.0 / part2);
                        distance += 2.0 * (Fr * Fy - F[A] * F[G] * Fy / Fr - F[T] * F[C] * Fr / Fy) * Math.log(1.0 / part3);
                    }
                    this.set(i, j, distance);
                }
            }
        }
    }
    
    private void initGTR() {
        double[] equiFreq = null;
        if (this.pinv > 0.0) {
            equiFreq = new double[this.numOfStates];
            double nchar = 0.0;
            switch (this.pi) {
                case EQUAL: {
                    for (int i = 0; i < this.numOfStates; ++i) {
                        equiFreq[i] = 0.25;
                    }
                    break;
                }
                case ESTIMATED: {
                    for (int taxa = 0; taxa < this.ntax; ++taxa) {
                        for (final Dataset.Partition P : this.partitions) {
                            final int[] weights = P.getAllWeights();
                            for (int numChar = P.getCompressedNChar(), site = 0; site < numChar; ++site) {
                                final Data d = this.getData(P, taxa, site);
                                for (int s = 0; s < d.getMaxStates(); ++s) {
                                    final double[] array = equiFreq;
                                    final int n = s;
                                    array[n] += (d.isState(s) ? (weights[site] / d.numOfStates()) : 0);
                                }
                            }
                            nchar += P.getNChar();
                        }
                    }
                    for (int i = 0; i < equiFreq.length; ++i) {
                        final double[] array2 = equiFreq;
                        final int n2 = i;
                        array2[n2] /= nchar;
                    }
                    break;
                }
                case CONSTANT: {
                    for (final Dataset.Partition P2 : this.partitions) {
                        final int[] weights2 = P2.getAllWeights();
                        for (int numChar2 = P2.getCompressedNChar(), site2 = 0; site2 < numChar2; ++site2) {
                            final Data d2 = this.getData(P2, 0, site2);
                            boolean isConstant = d2.numOfStates() == 1;
                            if (isConstant) {
                                for (int taxa2 = 1; taxa2 < P2.getNTax(); ++taxa2) {
                                    if (d2 != this.getData(P2, taxa2, site2)) {
                                        isConstant = false;
                                        break;
                                    }
                                }
                                if (isConstant) {
                                    for (int s = 0; s < d2.getMaxStates(); ++s) {
                                        final double[] array3 = equiFreq;
                                        final int n3 = s;
                                        array3[n3] += (d2.isState(s) ? weights2[site2] : 0);
                                    }
                                    nchar += weights2[site2];
                                }
                            }
                        }
                    }
                    for (int i = 0; i < equiFreq.length; ++i) {
                        final double[] array4 = equiFreq;
                        final int n4 = i;
                        array4[n4] /= nchar;
                    }
                    break;
                }
            }
        }
        for (int taxaA = 0; taxaA < this.ntax - 1; ++taxaA) {
            for (int taxaB = taxaA + 1; taxaB < this.ntax; ++taxaB) {
                double[][] F = new double[this.numOfStates][this.numOfStates];
                double nchar2 = 0.0;
                for (final Dataset.Partition P3 : this.partitions) {
                    final int[] weights3 = P3.getAllWeights();
                    for (int numChar3 = P3.getCompressedNChar(), k = 0; k < numChar3; ++k) {
                        final Data col = this.getData(P3, taxaA, k);
                        final Data row = this.getData(P3, taxaB, k);
                        final double contribution = weights3[k] / (col.numOfStates() * row.numOfStates());
                        for (int s2 = 0; s2 < row.getMaxStates(); ++s2) {
                            for (int t = 0; t < col.getMaxStates(); ++t) {
                                final double[] array5 = F[s2];
                                final int n5 = t;
                                array5[n5] += ((row.isState(s2) && col.isState(t)) ? contribution : 0.0);
                            }
                        }
                    }
                    nchar2 += P3.getNChar();
                }
                if (this.pinv > 0.0) {
                    for (int s3 = 0; s3 < this.numOfStates; ++s3) {
                        final double[] array6 = F[s3];
                        final int n6 = s3;
                        array6[n6] -= equiFreq[s3] * this.pinv * nchar2;
                    }
                    for (int j = 0; j < this.numOfStates; ++j) {
                        for (int l = 0; l < this.numOfStates; ++l) {
                            final double[] array7 = F[j];
                            final int n7 = l;
                            array7[n7] /= 1.0 - this.pinv;
                        }
                    }
                }
                if (this.model != Parameters.DistanceModel.GTR) {
                    F = this.convertFtoNestedModel(F, this.model);
                }
                for (int s3 = 0; s3 < this.numOfStates - 1; ++s3) {
                    final double[] array8 = F[s3];
                    final int n8 = s3;
                    array8[n8] /= nchar2;
                    for (int t2 = s3 + 1; t2 <= this.numOfStates - 1; ++t2) {
                        F[t2][s3] = (F[t2][s3] + F[s3][t2]) / 2.0 / nchar2;
                        F[s3][t2] = F[t2][s3];
                    }
                }
                final double[] array9 = F[this.numOfStates - 1];
                final int n9 = this.numOfStates - 1;
                array9[n9] /= nchar2;
                final Matrix frequency = new Matrix(this.numOfStates, this.numOfStates);
                for (int m = 0; m < this.numOfStates; ++m) {
                    for (int j2 = 0; j2 < this.numOfStates; ++j2) {
                        frequency.set(m, m, F[j2][m] + frequency.get(m, m));
                    }
                }
                final Matrix net = new Matrix(this.numOfStates, this.numOfStates);
                for (int i2 = 0; i2 < this.numOfStates; ++i2) {
                    for (int j3 = 0; j3 < this.numOfStates; ++j3) {
                        if (frequency.get(i2, i2) == 0.0) {
                            net.set(i2, j3, 0.0);
                        }
                        else {
                            net.set(i2, j3, F[i2][j3] / frequency.get(i2, i2));
                        }
                    }
                }
                final EigenvalueDecomposition ev = net.eig();
                final Matrix omega = ev.getV();
                final Matrix psi = ev.getD();
                for (int i3 = 0; i3 < this.numOfStates; ++i3) {
                    if (psi.get(i3, i3) < 0.0) {
                        psi.set(i3, i3, -psi.get(i3, i3));
                        this.saturation = true;
                    }
                    if (this.distribution == Parameters.StartingTreeDistribution.GAMMA) {
                        psi.set(i3, i3, this.gammaShape * (1.0 - Math.pow(psi.get(i3, i3), -1.0 / this.gammaShape)));
                    }
                    else {
                        final double logPsi = (psi.get(i3, i3) == 0.0) ? 0.0 : Math.log(psi.get(i3, i3));
                        psi.set(i3, i3, logPsi);
                    }
                }
                Matrix rate = omega.times(psi.times(omega.inverse()));
                rate = frequency.times(rate);
                this.set(taxaA, taxaB, -rate.trace());
            }
        }
    }
    
    private double[][] convertFtoNestedModel(final double[][] F, final Parameters.DistanceModel model) {
        switch (model) {
            case JC: {
                F[0][1] = (F[0][1] + F[0][2] + F[0][3] + F[1][0] + F[1][2] + F[1][3] + F[2][0] + F[2][1] + F[2][3] + F[3][0] + F[3][1] + F[3][2]) / 12.0;
                F[0][2] = F[0][1];
                F[0][3] = F[0][1];
                F[1][0] = F[0][1];
                F[1][2] = F[0][1];
                F[1][3] = F[0][1];
                F[2][0] = F[0][1];
                F[2][1] = F[0][1];
                F[2][3] = F[0][1];
                F[3][0] = F[0][1];
                F[3][1] = F[0][1];
                F[3][2] = F[0][1];
                F[0][0] = (F[0][0] + F[1][1] + F[2][2] + F[3][3]) / 4.0;
                F[1][1] = F[0][0];
                F[2][2] = F[0][0];
                F[3][3] = F[0][0];
                break;
            }
            case K2P: {
                F[0][1] = (F[0][1] + F[0][3] + F[1][0] + F[1][2] + F[2][1] + F[2][3] + F[3][0] + F[3][2]) / 8.0;
                F[0][3] = F[0][1];
                F[1][0] = F[0][1];
                F[1][2] = F[0][1];
                F[2][1] = F[0][1];
                F[2][3] = F[0][1];
                F[3][0] = F[0][1];
                F[3][2] = F[0][1];
                F[0][2] = (F[0][2] + F[1][3] + F[2][0] + F[3][1]) / 4.0;
                F[1][3] = F[0][2];
                F[2][0] = F[0][2];
                F[3][1] = F[0][2];
                F[0][0] = (F[0][0] + F[1][1] + F[2][2] + F[3][3]) / 4.0;
                F[1][1] = F[0][0];
                F[2][2] = F[0][0];
                F[3][3] = F[0][0];
                break;
            }
            case HKY85: {
                F[0][1] = (F[0][1] + F[0][3] + F[1][0] + F[1][2] + F[2][1] + F[2][3] + F[3][0] + F[3][2]) / 8.0;
                F[0][3] = F[0][1];
                F[1][0] = F[0][1];
                F[1][2] = F[0][1];
                F[2][1] = F[0][1];
                F[2][3] = F[0][1];
                F[3][0] = F[0][1];
                F[3][2] = F[0][1];
                F[0][2] = (F[0][2] + F[1][3] + F[2][0] + F[3][1]) / 4.0;
                F[1][3] = F[0][2];
                F[2][0] = F[0][2];
                F[3][1] = F[0][2];
                break;
            }
            case TN93: {
                F[0][1] = (F[0][1] + F[0][3] + F[1][0] + F[1][2] + F[2][1] + F[2][3] + F[3][0] + F[3][2]) / 8.0;
                F[0][3] = F[0][1];
                F[1][0] = F[0][1];
                F[1][2] = F[0][1];
                F[2][1] = F[0][1];
                F[2][3] = F[0][1];
                F[3][0] = F[0][1];
                F[3][2] = F[0][1];
                F[0][2] = (F[0][2] + F[2][0]) / 2.0;
                F[2][0] = F[0][2];
                F[1][3] = (F[1][3] + F[3][1]) / 2.0;
                F[3][1] = F[1][3];
                break;
            }
            case POISSON: {
                double diag = 0.0;
                double other = 0.0;
                for (int i = 0; i < F.length; ++i) {
                    for (int j = 0; j < F[i].length; ++j) {
                        if (i == j) {
                            diag += F[i][j];
                        }
                        else {
                            other += F[i][j];
                        }
                    }
                }
                for (int i = 0; i < F.length; ++i) {
                    for (int j = 0; j < F[i].length; ++j) {
                        F[i][j] = ((i == j) ? (diag / F.length) : (other / (F.length * F.length - F.length)));
                    }
                }
                break;
            }
        }
        return F;
    }
    
    public DefaultStyledDocument show() throws BadLocationException {
        final String NORMAL = "Normal";
        final String ITALIC = "Italic";
        final String BOLD = "Bold";
        final DefaultStyledDocument doc = new DefaultStyledDocument();
        final Hashtable<String, SimpleAttributeSet> paraStyles = new Hashtable<String, SimpleAttributeSet>();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        paraStyles.put("Normal", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setItalic(attr, true);
        paraStyles.put("Italic", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setBold(attr, true);
        paraStyles.put("Bold", attr);
        final AttributeSet defaultStyle = paraStyles.get("Normal");
        final AttributeSet boldStyle = paraStyles.get("Bold");
        final SimpleAttributeSet redStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(redStyle, Color.RED);
        int longestTaxon = 0;
        for (final String taxa : this.taxas) {
            if (taxa.length() > longestTaxon) {
                longestTaxon = taxa.length();
            }
        }
        doc.insertString(doc.getLength(), "Distance matrix :\n\n", boldStyle);
        doc.insertString(doc.getLength(), String.valueOf(this.model.verbose()) + ".\n", defaultStyle);
        switch (this.distribution) {
            case NONE: {
                doc.insertString(doc.getLength(), "No rate heterogeneity among sites.\n", defaultStyle);
                break;
            }
            case GAMMA: {
                doc.insertString(doc.getLength(), "Rate heterogeneity among sites following a discrete Gamma distribution (Yang, J. Mol. Evol. 39:306-314 (1994)), using a shape parameter (alpha) of " + this.gammaShape + ".\n", defaultStyle);
                break;
            }
            case VDP: {
                doc.insertString(doc.getLength(), "Rate heterogeneity among sites following Van de Peer et al. (J. Mol. Evol. 37:221-232 (1993)), using " + this.gammaShape + " rate categories.\n", defaultStyle);
                break;
            }
        }
        if (this.pinv > 0.0) {
            doc.insertString(doc.getLength(), "Assume that " + this.pinv * 100.0 + "% of the sites can not change (adjusting total number of site to have distances equal to the mean number of substitutions over variable sites only).\n", defaultStyle);
            switch (this.pi) {
                case EQUAL: {
                    doc.insertString(doc.getLength(), "The invariant sites will have " + this.dataType.verbose().toLowerCase() + " composition equal for all.\n", defaultStyle);
                    break;
                }
                case CONSTANT: {
                    doc.insertString(doc.getLength(), "The invariant sites reflect as the " + this.dataType.verbose().toLowerCase() + " composition of the site which are constant.\n", defaultStyle);
                    break;
                }
                case ESTIMATED: {
                    doc.insertString(doc.getLength(), "The invariant sites reflect the average " + this.dataType.verbose().toLowerCase() + " composition across all sequences.\n", defaultStyle);
                    break;
                }
            }
        }
        else {
            doc.insertString(doc.getLength(), "No invariant sites.\n", defaultStyle);
        }
        doc.insertString(doc.getLength(), "\n", defaultStyle);
        String st = "";
        for (int s = 0; s < longestTaxon; ++s) {
            st = String.valueOf(st) + " ";
        }
        st = String.valueOf(st) + "\t";
        doc.insertString(doc.getLength(), st, defaultStyle);
        for (String t : this.taxas) {
            for (int spaces = longestTaxon - t.length(), s2 = 0; s2 < spaces; ++s2) {
                t = String.valueOf(t) + " ";
            }
            t = String.valueOf(t) + "\t";
            doc.insertString(doc.getLength(), t, defaultStyle);
        }
        doc.insertString(doc.getLength(), "\n", defaultStyle);
        for (int i = 0; i < this.ntax; ++i) {
            String t2 = this.taxas.get(i);
            for (int spaces = longestTaxon - t2.length(), s2 = 0; s2 < spaces; ++s2) {
                t2 = String.valueOf(t2) + " ";
            }
            t2 = String.valueOf(t2) + "\t";
            doc.insertString(doc.getLength(), t2, defaultStyle);
            for (int j = 0; j < i; ++j) {
                t2 = Tools.doubletoString(this.get(j, i), 4);
                for (int spaces = longestTaxon - t2.length(), s3 = 0; s3 < spaces; ++s3) {
                    t2 = String.valueOf(t2) + " ";
                }
                t2 = String.valueOf(t2) + "\t";
                if (this.model == Parameters.DistanceModel.UNCORRECTED && this.get(j, i) > this.saturationThreshold) {
                    doc.insertString(doc.getLength(), t2, redStyle);
                }
                else {
                    doc.insertString(doc.getLength(), t2, defaultStyle);
                }
            }
            doc.insertString(doc.getLength(), "\n", defaultStyle);
        }
        return doc;
    }
    
    public boolean hasSaturation() {
        return this.saturation;
    }
}

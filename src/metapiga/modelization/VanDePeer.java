// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization;

import metapiga.modelization.data.Data;
import java.util.Set;
import metapiga.parameters.Parameters;
import java.util.HashSet;
import metapiga.modelization.data.DataType;

public class VanDePeer
{
    private final DataType dataType;
    private final Dataset.Partition P;
    private final int nCat;
    private boolean[] skippedSites;
    private int totalSites;
    private double[] rates;
    private DistanceMatrix distance;
    private double min;
    private double max;
    private double interval;
    private final int numInterval = 40;
    
    public VanDePeer(final DataType dataType, final Dataset.Partition partition, final int numSubsets) {
        this.dataType = dataType;
        this.P = partition;
        this.nCat = numSubsets;
        this.rates = new double[this.nCat];
        this.min = 100000.0;
        this.max = -100000.0;
        this.computeDistanceMatrix();
        final double scope = this.max - this.min;
        this.interval = scope / 40.0;
        this.setSkippedSites();
        this.partitionVn(this.computeSortedVn(this.computePlot()));
    }
    
    public double getRate(final int category) {
        return this.rates[category];
    }
    
    public int getNCat() {
        return this.nCat;
    }
    
    private void computeDistanceMatrix() {
        final Set<Dataset.Partition> set = new HashSet<Dataset.Partition>();
        set.add(this.P);
        Parameters.DistanceModel dm = null;
        switch (this.dataType) {
            case DNA: {
                dm = Parameters.DistanceModel.JC;
                break;
            }
            case PROTEIN: {
                dm = Parameters.DistanceModel.POISSON;
                break;
            }
            case STANDARD: {
                dm = Parameters.DistanceModel.GTR2;
                break;
            }
        }
        this.distance = new DistanceMatrix(this.dataType, set, this.P.getTaxa(), dm, Parameters.StartingTreeDistribution.NONE, 0.0, 0.0, Parameters.StartingTreePInvPi.EQUAL);
        for (int i = 0; i < this.distance.getRowDimension(); ++i) {
            for (int j = i + 1; j < this.distance.getColumnDimension(); ++j) {
                if (this.max < this.distance.get(i, j)) {
                    this.max = this.distance.get(i, j);
                }
                if (this.min > this.distance.get(i, j)) {
                    this.min = this.distance.get(i, j);
                }
            }
        }
    }
    
    private void setSkippedSites() {
        this.skippedSites = new boolean[this.P.getCompressedNChar()];
        for (int site = 0; site < this.skippedSites.length && (this.skippedSites[site] = (this.P.getData(0, site).numOfStates() == 1)); ++site) {
            for (int taxa = 1; taxa < this.P.getNTax(); ++taxa) {
                if (this.P.getData(0, site) != this.P.getData(taxa, site)) {
                    this.skippedSites[site] = false;
                    break;
                }
            }
        }
        int skipped = 0;
        for (int site2 = 0; site2 < this.skippedSites.length; ++site2) {
            if (this.skippedSites[site2]) {
                skipped += this.P.getWeight(site2);
            }
        }
        this.totalSites = this.P.getNChar() - skipped;
    }
    
    private double[][] computePlot() {
        final double[][] plot = new double[this.P.getCompressedNChar()][40];
        for (int site = 0; site < this.P.getCompressedNChar(); ++site) {
            if (!this.skippedSites[site]) {
                double inf = this.min;
                for (int curInt = 0; curInt < 40; ++curInt) {
                    plot[site][curInt] = this.fallWithinInterval(inf, site);
                    inf += this.interval;
                }
            }
        }
        return plot;
    }
    
    private double fallWithinInterval(final double inf, final int site) {
        double nPair = 0.0;
        double differences = 0.0;
        for (int currentTaxa = 0; currentTaxa < this.P.getNTax(); ++currentTaxa) {
            final Data currentData = this.P.getData(currentTaxa, site);
            for (int taxa = currentTaxa + 1; taxa < this.P.getNTax(); ++taxa) {
                if (this.distance.get(currentTaxa, taxa) <= inf + this.interval && this.distance.get(currentTaxa, taxa) >= inf) {
                    final Data d = this.P.getData(taxa, site);
                    final double contribution = 1.0 / (currentData.numOfStates() * d.numOfStates());
                    for (int s = 0; s < d.getMaxStates(); ++s) {
                        for (int t = 0; t < currentData.getMaxStates(); ++t) {
                            if (s != t) {
                                differences += ((d.isState(s) && currentData.isState(t)) ? contribution : 0.0);
                            }
                        }
                    }
                    ++nPair;
                }
            }
        }
        if (nPair == 0.0) {
            return 0.0;
        }
        return differences / nPair;
    }
    
    private double[] computeSortedVn(final double[][] plot) {
        double acc1 = 0.0;
        double acc2 = 0.0;
        double acc3 = 0.0;
        for (double n = this.min; n <= this.max; n += this.interval * 0.5, acc3 += n, acc2 += n * Math.exp(-1.3333333333333333 * n), n += this.interval * 0.5) {}
        final double[] temp_vn_list = new double[this.P.getCompressedNChar()];
        for (int site = 0; site < this.P.getCompressedNChar(); ++site) {
            if (!this.skippedSites[site]) {
                acc1 = 0.0;
                double n = this.min + this.interval * 0.5;
                for (int curInt = 0; curInt < 40; ++curInt) {
                    acc1 += plot[site][curInt] * n;
                    n += this.interval;
                }
                temp_vn_list[site] = Math.abs(Math.log((acc3 + 1.3333333333333333 * acc1) / acc2));
            }
            else {
                temp_vn_list[site] = -1.0;
            }
        }
        return this.sortVn(temp_vn_list);
    }
    
    private double[] sortVn(final double[] list) {
        final double[] sortedVn = new double[this.totalSites];
        int current = 0;
        double min;
        while ((min = this.getMin(list)) != -1.0) {
            for (int i = 0; i < list.length; ++i) {
                if (list[i] == min) {
                    list[i] = -1.0;
                    for (int j = 0; j < this.P.getWeight(i); ++j) {
                        sortedVn[current] = min;
                        ++current;
                    }
                }
            }
        }
        return sortedVn;
    }
    
    private double getMin(final double[] list) {
        double min = Double.MAX_VALUE;
        for (final double d : list) {
            if (d != -1.0 && d < min) {
                min = d;
            }
        }
        if (min == Double.MAX_VALUE) {
            min = -1.0;
        }
        return min;
    }
    
    private void partitionVn(final double[] sortedVn) {
        double sum = 0.0;
        int siteCount = 0;
        int cat = 0;
        final double n = this.totalSites / this.nCat;
        for (int i = 0; i < sortedVn.length; ++i) {
            sum += sortedVn[i];
            if (++siteCount >= n) {
                siteCount = 1;
                this.rates[cat] = sum / n;
                sum = 0.0;
                ++cat;
            }
        }
    }
}

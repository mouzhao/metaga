// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.modeltest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.ArrayList;

import metapiga.utilities.Tools;
import java.util.Set;

import metapiga.modelization.Charset;
import java.util.TreeMap;
import java.util.Map;
import metapiga.parameters.Parameters;
import metapiga.RateParameter;
import java.util.concurrent.ExecutorService;

public class AIC implements ModelTest
{
    private final String endl = "\n";
    private volatile boolean stopAskedByUser;
    private ExecutorService samplingExecutor;
    private final ModelSampling sampling;
    private final int sampleSize;
    private Parameters.EvaluationModel bestModel;
    private boolean bestDistribution;
    private boolean bestInvariant;
    private Parameters.EvaluationStateFrequencies bestFreq;
    private double bestScore;
    private final boolean AICc;
    private final Map<Double, String> results;
    
    public AIC(final ModelSampling sampling) {
        this.stopAskedByUser = false;
        this.results = new TreeMap<Double, String>();
        this.sampling = sampling;
        this.AICc = false;
        this.sampleSize = -1;
    }
    
    public AIC(final ModelSampling sampling, final int sampleSize) {
        this.stopAskedByUser = false;
        this.results = new TreeMap<Double, String>();
        this.sampling = sampling;
        this.AICc = true;
        this.sampleSize = sampleSize;
    }
    
    @Override
    public Parameters.EvaluationDistribution getBestDistribution() {
        return this.bestDistribution ? Parameters.EvaluationDistribution.GAMMA : Parameters.EvaluationDistribution.NONE;
    }
    
    @Override
    public double getBestDistributionShape(final Charset c) throws Exception {
        return this.sampling.getDistributionShape(this.bestModel, this.bestDistribution, this.bestInvariant, this.bestFreq, c);
    }
    
    @Override
    public double getBestInvariant(final Charset c) throws Exception {
        if (this.bestInvariant) {
            return this.sampling.getInvariant(this.bestModel, this.bestDistribution, this.bestInvariant, this.bestFreq, c);
        }
        return 0.0;
    }
    
    @Override
    public boolean hasBestInvariant() {
        return this.bestInvariant;
    }
    
    @Override
    public Parameters.EvaluationModel getBestModel() {
        return this.bestModel;
    }
    
    @Override
    public Parameters.EvaluationStateFrequencies getBestStateFrequencies() {
        return this.bestFreq;
    }
    
    @Override
    public Map<RateParameter, Double> getBestRateParameters(final Charset c) throws Exception {
        return this.sampling.getRateParameters(this.bestModel, this.bestDistribution, this.bestInvariant, this.bestFreq, c);
    }
    
    @Override
    public void testModels(final int numCores, final Set<Parameters.EvaluationModel> models) throws Exception {
        this.prepareSamplings(numCores, models);
        this.results.clear();
        if (this.stopAskedByUser) {
            return;
        }
        this.bestScore = Double.MAX_VALUE;
        for (final Parameters.EvaluationModel model : models) {
            boolean gamma = false;
            for (int i = 0; i < 2; ++i, gamma = !gamma) {
                boolean invariant = false;
                for (int j = 0; j < 2; ++j, invariant = !invariant) {
                    for (int k = 0; k < (model.isEmpirical() ? 2 : 1); ++k) {
                        final Parameters.EvaluationStateFrequencies freq = (k == 0) ? Parameters.EvaluationStateFrequencies.EMPIRICAL : Parameters.EvaluationStateFrequencies.ESTIMATED;
                        final double likelihood = this.sampling.getLikelihood(model, gamma, invariant, freq);
                        final int K = this.sampling.getNbrParameters(model, gamma, invariant, freq);
                        double AIC = 2.0 * (likelihood + K);
                        if (this.AICc) {
                            AIC += 2.0 * K * (K + 1) / (this.sampleSize - K - 1.0);
                        }
                        while (this.results.containsKey(AIC)) {
                            AIC += 1.0E-10;
                        }
                        final String res = model + (gamma ? "+G" : "") + (invariant ? "+I" : "") + ((model.isEmpirical() && freq == Parameters.EvaluationStateFrequencies.ESTIMATED) ? "+F" : "") + " : ML = " + Tools.doubletoString(likelihood, 4) + ", K = " + K + (this.AICc ? ", AICc = " : ", AIC = ") + Tools.doubletoString(AIC, 4);
                        this.results.put(AIC, res);
                        if (AIC < this.bestScore) {
                            this.bestScore = AIC;
                            this.bestModel = model;
                            this.bestDistribution = gamma;
                            this.bestInvariant = invariant;
                            this.bestFreq = freq;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void stop() {
        this.stopAskedByUser = true;
        this.samplingExecutor.shutdownNow();
    }
    
    private void prepareSamplings(final int numCores, final Set<Parameters.EvaluationModel> models) throws Exception {
        final List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < numCores; ++i) {
            ids.add(i);
        }
        this.samplingExecutor = Executors.newFixedThreadPool(numCores);
        final CountDownLatch latch = new CountDownLatch(models.size() * 2 * 2);
        for (final Parameters.EvaluationModel model : models) {
            boolean gamma = true;
            for (int j = 0; j < 2; ++j, gamma = !gamma) {
                boolean invariant = false;
                for (int k = 0; k < 2; ++k, invariant = !invariant) {
                    for (int l = 0; l < (model.isEmpirical() ? 2 : 1); ++l) {
                        final Parameters.EvaluationStateFrequencies freq = (l == 0) ? Parameters.EvaluationStateFrequencies.EMPIRICAL : Parameters.EvaluationStateFrequencies.ESTIMATED;
                        final boolean gm = gamma;
                        final boolean inv = invariant;
                        final Parameters.EvaluationStateFrequencies fr = freq;
                        this.samplingExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                final int job = ids.remove(0);
                                try {
                                    AIC.this.sampling.createSampling(model, gm, inv, fr, job, numCores);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ids.add(job);
                                latch.countDown();
                            }
                        });
                    }
                }
            }
        }
        latch.await();
    }
    
    @Override
    public String getResults() {
        final StringBuilder sb = new StringBuilder();
        int max = 10;
        sb.append(String.valueOf(max) + " first ranking:" + "\n");
        for (final String s : this.results.values()) {
            sb.append(String.valueOf(s) + "\n");
            if (--max == 0) {
                break;
            }
        }
        return sb.toString();
    }
}

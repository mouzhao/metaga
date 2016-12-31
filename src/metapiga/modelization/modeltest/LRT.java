// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.modeltest;

import metapiga.utilities.Tools;
import cern.jet.stat.Probability;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import metapiga.modelization.Charset;
import metapiga.parameters.Parameters;
import metapiga.RateParameter;
import java.util.concurrent.ExecutorService;

public class LRT implements ModelTest
{
    private final double alpha = 0.01;
    private final String endl = "\n";
    private volatile boolean stopAskedByUser;
    private ExecutorService samplingExecutor;
    private final ModelSampling sampling;
    private Parameters.EvaluationModel bestModel;
    private boolean bestDistribution;
    private boolean bestInvariant;
    private Parameters.EvaluationStateFrequencies bestFreq;
    private StringBuilder results;
    
    public LRT(final ModelSampling sampling) {
        this.stopAskedByUser = false;
        this.bestFreq = null;
        this.sampling = sampling;
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
        this.results = new StringBuilder();
        if (this.stopAskedByUser) {
            return;
        }
        this.bestModel = Parameters.EvaluationModel.JC;
        if (this.ratioTest(this.bestModel, false, false, Parameters.EvaluationModel.K2P, false, false) < 0.01) {
            this.bestModel = Parameters.EvaluationModel.K2P;
        }
        if (this.ratioTest(this.bestModel, false, false, Parameters.EvaluationModel.HKY85, false, false) < 0.01) {
            this.bestModel = Parameters.EvaluationModel.HKY85;
        }
        if (this.ratioTest(this.bestModel, false, false, Parameters.EvaluationModel.TN93, false, false) < 0.01) {
            this.bestModel = Parameters.EvaluationModel.TN93;
        }
        if (this.ratioTest(this.bestModel, false, false, Parameters.EvaluationModel.GTR, false, false) < 0.01) {
            this.bestModel = Parameters.EvaluationModel.GTR;
        }
        this.bestDistribution = false;
        if (this.ratioTest(this.bestModel, this.bestDistribution, false, this.bestModel, true, false) < 0.01) {
            this.bestDistribution = true;
        }
        if (this.stopAskedByUser) {
            return;
        }
        this.bestInvariant = false;
        if (this.ratioTest(this.bestModel, this.bestDistribution, this.bestInvariant, this.bestModel, this.bestDistribution, true) < 0.01) {
            this.bestInvariant = true;
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
        final CountDownLatch latch = new CountDownLatch(models.size());
        for (final Parameters.EvaluationModel model : models) {
            this.samplingExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    final int job = ids.remove(0);
                    try {
                        LRT.this.sampling.createSampling(model, false, false, null, job, numCores);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    ids.add(job);
                    latch.countDown();
                }
            });
        }
        latch.await();
    }
    
    public double ratioTest(final Parameters.EvaluationModel model1, final boolean gamma1, final boolean invariant1, final Parameters.EvaluationModel model2, final boolean gamma2, final boolean invariant2) throws Exception {
        final double delta = 2.0 * (this.sampling.getLikelihood(model1, gamma1, invariant1, null) - this.sampling.getLikelihood(model2, gamma2, invariant2, null));
        final int df = this.sampling.getNbrParameters(model2, gamma2, invariant2, null) - this.sampling.getNbrParameters(model1, gamma1, invariant1, null);
        final double pvalue = (delta > 0.0) ? Probability.chiSquareComplemented(df, delta) : 1.0;
        this.results.append(model1 + (gamma1 ? "+G" : "") + (invariant1 ? "+I" : "") + " (ML = " + Tools.doubletoString(this.sampling.getLikelihood(model1, gamma1, invariant1, null), 4) + ")");
        this.results.append(" vs ");
        this.results.append(model2 + (gamma2 ? "+G" : "") + (invariant2 ? "+I" : "") + " (ML = " + Tools.doubletoString(this.sampling.getLikelihood(model2, gamma2, invariant2, null), 4) + ")");
        this.results.append("\n");
        this.results.append("Delta = " + Tools.doubletoString(delta, 4));
        this.results.append(", df = " + df);
        if (pvalue < 0.01) {
            this.results.append(", P-value < 0.01");
        }
        else if (pvalue == 1.0) {
            this.results.append(", P-value > 1");
        }
        else {
            this.results.append(", P-value >= 0.01");
        }
        this.results.append("\n\n");
        return pvalue;
    }
    
    @Override
    public String getResults() {
        return this.results.toString();
    }
}

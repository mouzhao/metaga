// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.modeltest;

import java.util.Map;
import metapiga.modelization.Charset;
import metapiga.parameters.Parameters;
import metapiga.RateParameter;
import java.util.Set;

public interface ModelTest
{
    void testModels(final int p0, final Set<Parameters.EvaluationModel> p1) throws Exception;
    
    String getResults();
    
    void stop();
    
    Parameters.EvaluationModel getBestModel();
    
    Map<RateParameter, Double> getBestRateParameters(final Charset p0) throws Exception;
    
    Parameters.EvaluationDistribution getBestDistribution();
    
    double getBestDistributionShape(final Charset p0) throws Exception;
    
    double getBestInvariant(final Charset p0) throws Exception;
    
    boolean hasBestInvariant();
    
    Parameters.EvaluationStateFrequencies getBestStateFrequencies();
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.likelihood;

import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.Tree;

import java.util.Map;
import metapiga.parameters.Parameters;
import metapiga.modelization.Dataset;
import metapiga.RateParameter;

public class LikelihoodFactory
{
    public static Likelihood makeLikelihoodClassic(final Dataset.Partition partition, final Parameters.EvaluationRate rate, final Parameters.EvaluationModel model, final Parameters.EvaluationDistribution distribution, final double distributionShape, final double pinv, final double apRate, final Map<RateParameter, Double> rateParameters, final Parameters.EvaluationStateFrequencies stateFreq, final Tree tree, final int numSubsets) throws UnrootableTreeException {
        final int numNodes = tree.getNumOfNodes();
        final int numCategories = (distribution == Parameters.EvaluationDistribution.NONE) ? 1 : numSubsets;
        final int numCharacters = partition.getCompressedNChar();
        final int numStates = partition.getDataType().numOfStates();
        final SequenceArrays4Dimension seq = new SequenceArrays4Dimension(numNodes, numCategories, numCharacters, numStates);
        return new LikelihoodClassic(partition, rate, model, distribution, distributionShape, pinv, apRate, rateParameters, stateFreq, tree, numSubsets, seq);
    }
    
    public static Likelihood makeLikelihoodCopy(final Likelihood L, final Tree tree) throws UnrootableTreeException {
        if (L instanceof LikelihoodClassic) {
            return new LikelihoodClassic((LikelihoodClassic)L, tree);
        }
        return null;
    }
}

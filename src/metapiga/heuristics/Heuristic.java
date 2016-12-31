// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.heuristics;

import java.util.Iterator;
import metapiga.modelization.Charset;
import metapiga.trees.Tree;
import metapiga.parameters.Parameters;

public abstract class Heuristic implements Runnable
{
    private final Parameters P;
    
    public Heuristic(final Parameters par) {
        this.P = par;
    }
    
    public abstract void smoothStop();
    
    public abstract Tree getBestSolution();
    
    public abstract String getName(final boolean p0);
    
    protected void allocateGPUcontextAndMemory() {
        final int numCategories = (this.P.evaluationDistribution == Parameters.EvaluationDistribution.NONE) ? 1 : this.P.evaluationDistributionSubsets;
        int maxNumCharComp = 0;
        int maxNumStates = 0;
        for (final Charset c : this.P.dataset.getPartitionCharsets()) {
            final int charN = this.P.dataset.getPartition(c).getCompressedNChar();
            if (charN > maxNumCharComp) {
                maxNumCharComp = charN;
            }
            final int statN = this.P.dataset.getPartition(c).getDataType().numOfStates();
            if (statN > maxNumStates) {
                maxNumStates = statN;
            }
        }
    }
}

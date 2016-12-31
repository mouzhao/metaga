// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import metapiga.trees.Consensus;
import metapiga.modelization.DistanceMatrix;
import java.util.List;
import metapiga.trees.Tree;
import metapiga.parameters.Parameters;
import java.util.Map;

public interface Monitor extends Runnable
{
    public static final String BEST_SOLUTION = "Best solution";
    public static final String CURRENT_SOLUTION = "Current solution";
    public static final String POPULATION_SOLUTION = "Population";
    public static final String INDIVIDUAL_SOLUTION = "Individual";
    public static final String TEMPERATURE = "Temperature";
    public static final String STARTING_TREE = "Starting tree (best)";
    
    MonitorType getMonitorType();
    
    boolean trackDataMatrix();
    
    boolean trackDistances();
    
    boolean trackStartingTree();
    
    boolean trackHeuristic();
    
    boolean trackHeuristicTrees();
    
    boolean trackConsensus();
    
    boolean trackOperators();
    
    boolean trackOperatorStats();
    
    boolean trackPerformances();
    
    boolean trackAncestralSequences();
    
    void showNextStep();
    
    void showRemainingTime(final long p0);
    
    void showAutoStopDone(final int p0);
    
    void showReplicate();
    
    void showEvaluations(final Map<String, Double> p0);
    
    void showCurrentTemperature(final double p0);
    
    void showCurrentCoolingSchedule(final Parameters.SASchedule p0);
    
    void showCurrentMRE(final double p0);
    
    void showStartingTree(final Tree p0);
    
    void showCurrentTree(final Tree p0);
    
    void showText(final String p0);
    
    void showStageDistanceMatrix(final int p0);
    
    void showStageStartingTree(final int p0);
    
    void showStageHCRestart();
    
    void showStageSATemperature(final int p0);
    
    void showStageGAPopulation(final int p0);
    
    void showStageCPMetapopulation(final int p0);
    
    void showStageSearchStart(final String p0, final int p1, final double p2);
    
    void showStageSearchProgress(final int p0, final long p1, final int p2);
    
    void showStageSearchStop(final List<Tree> p0, final Map<String, Double> p1);
    
    void showStageOptimization(final int p0, final String p1);
    
    void end(final List<Tree> p0);
    
    void endFromException(final Exception p0);
    
    void printDataMatrix();
    
    void printDistanceMatrix(final DistanceMatrix p0);
    
    void printStartingTrees(final List<Tree> p0);
    
    void printTreeBeforeOperator(final Tree p0, final Parameters.Operator p1, final boolean p2);
    
    void printOperatorInfos(final Tree p0, final String p1);
    
    void printOperatorInfos(final Tree p0, final String p1, final Consensus p2);
    
    void printTreeAfterOperator(final Tree p0, final Parameters.Operator p1, final boolean p2);
    
    void printOperatorFrequenciesUpdate(final int p0, final Map<Parameters.Operator, Integer> p1, final Map<Parameters.Operator, Double> p2, final Map<Parameters.Operator, Long> p3);
    
    void printOperatorFrequenciesUpdate(final Map<Parameters.Operator, Double> p0);
    
    void printOperatorStatistics(final int p0, final Map<Parameters.Operator, Integer> p1, final Map<Parameters.Operator, Double> p2, final Map<Parameters.Operator, Long> p3, final int p4, final int p5, final Map<Parameters.Operator, Map<Integer, Integer>> p6);
    
    void printDetailsHC(final int p0, final double p1, final double p2, final Parameters.Operator p3, final boolean p4);
    
    void printDetailsSA(final int p0, final double p1, final double p2, final double p3, final Parameters.Operator p4, final String p5, final double p6, final double p7, final int p8, final int p9, final int p10, final int p11);
    
    void printDetailsGA(final int p0, final Tree[] p1, final Parameters.Operator[] p2);
    
    void printDetailsGA(final int p0, final String p1, final Tree[] p2, final double p3);
    
    void printDetailsCP(final Tree[] p0, final Parameters.Operator[] p1, final int p2);
    
    void printDetailsCP(final Tree[] p0, final String[] p1, final int p2);
    
    void printDetailsCP(final int p0, final String[] p1, final Tree[][] p2, final double p3);
    
    void printTreesHC(final int p0, final Tree p1, final Tree p2);
    
    void printTreesSA(final int p0, final Tree p1, final Tree p2, final Tree p3);
    
    void printTreesGA(final int p0, final Tree[] p1, final boolean p2);
    
    void printTreesCP(final int p0, final Tree[] p1, final int p2, final boolean p3);
    
    void printTreesCP(final int p0, final Tree[][] p1);
    
    void printEndTreesHeuristic();
    
    void printConsensus(final int p0, final Consensus p1);
    
    void trackPerformances(final String p0, final int p1);
    
    void printAncestralSequences(final List<Tree> p0);
    
    void updateConsensusTree(final Tree p0);
    
    public enum MonitorType
    {
        INACTIVE("INACTIVE", 0), 
        SINGLE_SEARCH_GRAPHICAL("SINGLE_SEARCH_GRAPHICAL", 1), 
        BATCH_SEARCH_GRAPHICAL("BATCH_SEARCH_GRAPHICAL", 2), 
        CONSOLE("CONSOLE", 3), 
        SILENT("SILENT", 4), 
        OPTIMIZATION("OPTIMIZATION", 5);
        
        private MonitorType(final String s, final int n) {
        }
    }
}

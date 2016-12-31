// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import java.util.Map;
import metapiga.modelization.DistanceMatrix;
import metapiga.parameters.Parameters;
import metapiga.trees.Consensus;
import metapiga.trees.Tree;
import java.util.List;

public class InactiveMonitor implements Monitor
{
    @Override
    public MonitorType getMonitorType() {
        return MonitorType.INACTIVE;
    }
    
    @Override
    public boolean trackDataMatrix() {
        return false;
    }
    
    @Override
    public boolean trackDistances() {
        return false;
    }
    
    @Override
    public boolean trackStartingTree() {
        return false;
    }
    
    @Override
    public boolean trackHeuristic() {
        return false;
    }
    
    @Override
    public boolean trackHeuristicTrees() {
        return false;
    }
    
    @Override
    public boolean trackConsensus() {
        return false;
    }
    
    @Override
    public boolean trackOperators() {
        return false;
    }
    
    @Override
    public boolean trackOperatorStats() {
        return false;
    }
    
    @Override
    public boolean trackPerformances() {
        return false;
    }
    
    @Override
    public boolean trackAncestralSequences() {
        return false;
    }
    
    @Override
    public void end(final List<Tree> solutionTrees) {
    }
    
    @Override
    public void endFromException(final Exception e) {
    }
    
    @Override
    public void printAncestralSequences(final List<Tree> trees) {
    }
    
    @Override
    public void printConsensus(final int step, final Consensus consensus) {
    }
    
    @Override
    public void printDataMatrix() {
    }
    
    @Override
    public void printDetailsCP(final Tree[] mutantML, final Parameters.Operator[] operator, final int currentPop) {
    }
    
    @Override
    public void printDetailsCP(final Tree[] hybridML, final String[] parents, final int currentPop) {
    }
    
    @Override
    public void printDetailsCP(final int step, final String[] selectionDetails, final Tree[][] selectedML, final double bestLikelihood) {
    }
    
    @Override
    public void printDetailsGA(final int step, final Tree[] mutantML, final Parameters.Operator[] operator) {
    }
    
    @Override
    public void printDetailsGA(final int step, final String selectionDetails, final Tree[] selectedML, final double bestLikelihood) {
    }
    
    @Override
    public void printDetailsHC(final int step, final double bestLikelihood, final double currentLikelihood, final Parameters.Operator operator, final boolean improvement) {
    }
    
    @Override
    public void printDetailsSA(final int step, final double bestLikelihood, final double S0Likelihood, final double currentLikelihood, final Parameters.Operator operator, final String status, final double tempAcceptance, final double temperature, final int coolingSteps, final int successes, final int failures, final int reheatingSteps) {
    }
    
    @Override
    public void printDistanceMatrix(final DistanceMatrix dm) {
    }
    
    @Override
    public void printEndTreesHeuristic() {
    }
    
    @Override
    public void printOperatorFrequenciesUpdate(final int currentStep, final Map<Parameters.Operator, Integer> use, final Map<Parameters.Operator, Double> scoreImprovements, final Map<Parameters.Operator, Long> performances) {
    }
    
    @Override
    public void printOperatorFrequenciesUpdate(final Map<Parameters.Operator, Double> frequencies) {
    }
    
    @Override
    public void printOperatorInfos(final Tree tree, final String infos) {
    }
    
    @Override
    public void printOperatorInfos(final Tree tree, final String infos, final Consensus consensus) {
    }
    
    @Override
    public void printOperatorStatistics(final int numStep, final Map<Parameters.Operator, Integer> use, final Map<Parameters.Operator, Double> scoreImprovements, final Map<Parameters.Operator, Long> performances, final int outgroupTargeted, final int ingroupTargeted, final Map<Parameters.Operator, Map<Integer, Integer>> cancelByConsensus) {
    }
    
    @Override
    public void printStartingTrees(final List<Tree> startingTrees) {
    }
    
    @Override
    public void printTreeAfterOperator(final Tree tree, final Parameters.Operator operator, final boolean consensus) {
    }
    
    @Override
    public void printTreeBeforeOperator(final Tree tree, final Parameters.Operator operator, final boolean consensus) {
    }
    
    @Override
    public void printTreesCP(final int step, final Tree[] trees, final int pop, final boolean recombined) {
    }
    
    @Override
    public void printTreesCP(final int step, final Tree[][] trees) {
    }
    
    @Override
    public void printTreesGA(final int step, final Tree[] trees, final boolean selectionDone) {
    }
    
    @Override
    public void printTreesHC(final int step, final Tree bestTree, final Tree currentTree) {
    }
    
    @Override
    public void printTreesSA(final int step, final Tree bestTree, final Tree S0Tree, final Tree currentTree) {
    }
    
    @Override
    public void updateConsensusTree(final Tree consensusTree) {
    }
    
    @Override
    public void showAutoStopDone(final int noChangeSteps) {
    }
    
    @Override
    public void showCurrentCoolingSchedule(final Parameters.SASchedule currentCoolingSchedule) {
    }
    
    @Override
    public void showCurrentTemperature(final double currentTemperature) {
    }
    
    @Override
    public void showCurrentMRE(final double currentMRE) {
    }
    
    @Override
    public void showCurrentTree(final Tree tree) {
    }
    
    @Override
    public void showEvaluations(final Map<String, Double> evaluationsToShow) {
    }
    
    @Override
    public void showRemainingTime(final long time) {
    }
    
    @Override
    public void showReplicate() {
    }
    
    @Override
    public void showStageCPMetapopulation(final int numOfSteps) {
    }
    
    @Override
    public void showStageDistanceMatrix(final int numOfSteps) {
    }
    
    @Override
    public void showStageGAPopulation(final int numOfSteps) {
    }
    
    @Override
    public void showStageOptimization(final int active, final String target) {
    }
    
    @Override
    public void showStageSATemperature(final int numOfSteps) {
    }
    
    @Override
    public void showStageHCRestart() {
    }
    
    @Override
    public void showStageSearchProgress(final int currentIteration, final long remainingTime, final int noChangeSteps) {
    }
    
    @Override
    public void showStageSearchStart(final String heuristic, final int maxSteps, final double startingEvaluation) {
    }
    
    @Override
    public void showStageSearchStop(final List<Tree> solutionTrees, final Map<String, Double> evaluationsToShow) {
    }
    
    @Override
    public void showStageStartingTree(final int numOfSteps) {
    }
    
    @Override
    public void showStartingTree(final Tree tree) {
    }
    
    @Override
    public void showNextStep() {
    }
    
    @Override
    public void showText(final String text) {
    }
    
    @Override
    public void run() {
    }
    
    @Override
    public void trackPerformances(final String action, final int level) {
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import java.util.Date;
import java.util.ArrayList;
import metapiga.utilities.Tools;
import java.util.Map;
import metapiga.trees.Consensus;
import metapiga.modelization.DistanceMatrix;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.exceptions.UnrootableTreeException;
import java.util.Iterator;
import metapiga.trees.Tree;
import java.util.List;
import java.io.IOException;
import metapiga.heuristics.ConsensusPruning;
import metapiga.heuristics.GeneticAlgorithm;
import metapiga.ProgressHandling;
import metapiga.parameters.Parameters;
import metapiga.heuristics.Heuristic;

public class SearchConsoleMonitor implements Monitor
{
    private boolean DATA;
    private boolean DIST;
    private boolean TREESTART;
    private boolean HEUR;
    private boolean TREEHEUR;
    private boolean CONSENSUS;
    private boolean OPDETAILS;
    private boolean OPSTATS;
    private boolean ANCSEQ;
    private boolean PERF;
    private SearchConsole parent;
    private Heuristic H;
    private Thread thread;
    private int maxSteps;
    int currentStep;
    private int currentReplicate;
    private long repStartTime;
    private Parameters parameters;
    final PrintMonitor print;
    private final ProgressHandling progress;
    private final int progressId;
    
    public SearchConsoleMonitor(final SearchConsole parent, final ProgressHandling progress, final int progressId, final Parameters parameters, final String runLabel) {
        this.maxSteps = 0;
        this.currentReplicate = 1;
        this.parent = parent;
        this.progress = progress;
        this.progressId = progressId;
        this.parameters = parameters;
        this.DATA = parameters.logFiles.contains(Parameters.LogFile.DATA);
        this.DIST = parameters.logFiles.contains(Parameters.LogFile.DIST);
        this.TREESTART = parameters.logFiles.contains(Parameters.LogFile.TREESTART);
        this.HEUR = parameters.logFiles.contains(Parameters.LogFile.HEUR);
        this.TREEHEUR = parameters.logFiles.contains(Parameters.LogFile.TREEHEUR);
        this.CONSENSUS = parameters.logFiles.contains(Parameters.LogFile.CONSENSUS);
        this.OPDETAILS = parameters.logFiles.contains(Parameters.LogFile.OPDETAILS);
        this.OPSTATS = parameters.logFiles.contains(Parameters.LogFile.OPSTATS);
        this.ANCSEQ = parameters.logFiles.contains(Parameters.LogFile.ANCSEQ);
        this.PERF = parameters.logFiles.contains(Parameters.LogFile.PERF);
        (this.print = new PrintMonitor(this, runLabel)).setParameters(parameters);
    }
    
    @Override
    public MonitorType getMonitorType() {
        return MonitorType.CONSOLE;
    }
    
    @Override
    public final boolean trackDataMatrix() {
        return this.DATA;
    }
    
    @Override
    public final boolean trackDistances() {
        return this.DIST;
    }
    
    @Override
    public final boolean trackStartingTree() {
        return this.TREESTART;
    }
    
    @Override
    public final boolean trackHeuristic() {
        return this.HEUR;
    }
    
    @Override
    public final boolean trackHeuristicTrees() {
        return this.TREEHEUR;
    }
    
    @Override
    public final boolean trackConsensus() {
        return this.CONSENSUS;
    }
    
    @Override
    public final boolean trackOperators() {
        return this.OPDETAILS;
    }
    
    @Override
    public final boolean trackOperatorStats() {
        return this.OPSTATS;
    }
    
    @Override
    public final boolean trackPerformances() {
        return this.PERF;
    }
    
    @Override
    public final boolean trackAncestralSequences() {
        return this.ANCSEQ;
    }
    
    @Override
    public void run() {
        try {
            if (this.trackDataMatrix()) {
                this.printDataMatrix();
            }
            while ((this.currentReplicate = this.parent.getNextReplicate()) > 0) {
                this.print.initLogFiles(this.currentReplicate);
                switch (this.parameters.heuristic) {
                    case GA: {
                        this.H = new GeneticAlgorithm(this.parameters, this);
                        break;
                    }
                    case CP: {
                        this.H = new ConsensusPruning(this.parameters, this);
                        break;
                    }
                }
                (this.thread = new Thread(this.H, String.valueOf(this.H.getName(true)) + "-Rep-" + this.currentReplicate)).start();
                this.thread.join();
                try {
                    this.print.closeOutputFiles();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    this.showText("\n Error when closing log files");
                    this.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
                    StackTraceElement[] stackTrace;
                    for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                        final StackTraceElement el = stackTrace[i];
                        this.showText("\tat " + el.toString());
                    }
                }
            }
        }
        catch (Exception e2) {
            this.endFromException(e2);
        }
    }
    
    @Override
    public void end(final List<Tree> solutionTrees) {
        if (this.parameters.hasManyReplicates()) {
            for (final Tree tree : solutionTrees) {
                tree.setName(String.valueOf(tree.getName()) + "_Rep_" + this.currentReplicate);
            }
        }
        if (this.ANCSEQ) {
            this.printAncestralSequences(solutionTrees);
        }
        for (final Tree tree : solutionTrees) {
            tree.deleteLikelihoodComputation();
        }
        this.parent.addSolutionTree(solutionTrees);
    }
    
    @Override
    public void endFromException(final Exception e) {
        this.parent.endFromException(e);
    }
    
    public double getBestLikelihood() {
        try {
            return this.H.getBestSolution().getEvaluation();
        }
        catch (UnrootableTreeException e) {
            e.printStackTrace();
        }
        catch (NullAncestorException e2) {
            e2.printStackTrace();
        }
        return Double.NaN;
    }
    
    @Override
    public void printDataMatrix() {
        this.print.printDataMatrix();
    }
    
    @Override
    public void printDistanceMatrix(final DistanceMatrix dm) {
        this.print.printDistanceMatrix(dm);
    }
    
    @Override
    public void printStartingTrees(final List<Tree> startingTrees) {
        this.print.printStartingTrees(startingTrees, this.currentReplicate);
    }
    
    @Override
    public void printTreeBeforeOperator(final Tree tree, final Parameters.Operator operator, final boolean consensus) {
        this.print.printTreeBeforeOperator(tree, operator, consensus);
    }
    
    @Override
    public void printOperatorInfos(final Tree tree, final String infos) {
        this.print.printOperatorInfos(tree, infos);
    }
    
    @Override
    public void printOperatorInfos(final Tree tree, final String infos, final Consensus consensus) {
        this.print.printOperatorInfos(tree, infos, consensus);
    }
    
    @Override
    public void printTreeAfterOperator(final Tree tree, final Parameters.Operator operator, final boolean consensus) {
        this.print.printTreeAfterOperator(tree, operator, consensus);
    }
    
    @Override
    public void printOperatorFrequenciesUpdate(final int currentStep, final Map<Parameters.Operator, Integer> use, final Map<Parameters.Operator, Double> scoreImprovements, final Map<Parameters.Operator, Long> performances) {
        this.print.printOperatorFrequenciesUpdate(currentStep, use, scoreImprovements, performances);
    }
    
    @Override
    public void printOperatorFrequenciesUpdate(final Map<Parameters.Operator, Double> frequencies) {
        this.print.printOperatorFrequenciesUpdate(frequencies);
    }
    
    @Override
    public void printOperatorStatistics(final int numStep, final Map<Parameters.Operator, Integer> use, final Map<Parameters.Operator, Double> scoreImprovements, final Map<Parameters.Operator, Long> performances, final int outgroupTargeted, final int ingroupTargeted, final Map<Parameters.Operator, Map<Integer, Integer>> cancelByConsensus) {
        this.print.printOperatorStatistics(numStep, use, scoreImprovements, performances, outgroupTargeted, ingroupTargeted, cancelByConsensus);
    }
    
    @Override
    public void printDetailsHC(final int step, final double bestLikelihood, final double currentLikelihood, final Parameters.Operator operator, final boolean improvement) {
        this.print.printDetailsHC(step, bestLikelihood, currentLikelihood, operator, improvement);
    }
    
    @Override
    public void printDetailsSA(final int step, final double bestLikelihood, final double S0Likelihood, final double currentLikelihood, final Parameters.Operator operator, final String status, final double tempAcceptance, final double temperature, final int coolingSteps, final int successes, final int failures, final int reheatingDecrements) {
        this.print.printDetailsSA(step, bestLikelihood, S0Likelihood, currentLikelihood, operator, status, tempAcceptance, temperature, coolingSteps, successes, failures, reheatingDecrements);
    }
    
    @Override
    public void printDetailsGA(final int step, final Tree[] mutantML, final Parameters.Operator[] operator) {
        this.print.printDetailsGA(step, mutantML, operator);
    }
    
    @Override
    public void printDetailsGA(final int step, final String selectionDetails, final Tree[] selectedML, final double bestLikelihood) {
        this.print.printDetailsGA(step, selectionDetails, selectedML, bestLikelihood);
    }
    
    @Override
    public void printDetailsCP(final Tree[] mutantML, final Parameters.Operator[] operator, final int currentPop) {
        this.print.printDetailsCP(mutantML, operator, currentPop);
    }
    
    @Override
    public void printDetailsCP(final Tree[] hybridML, final String[] parents, final int currentPop) {
        this.print.printDetailsCP(hybridML, parents, currentPop);
    }
    
    @Override
    public void printDetailsCP(final int step, final String[] selectionDetails, final Tree[][] selectedML, final double bestLikelihood) {
        this.print.printDetailsCP(step, selectionDetails, selectedML, bestLikelihood);
    }
    
    @Override
    public void printTreesHC(final int step, final Tree bestTree, final Tree currentTree) {
        this.print.printTreesHC(step, bestTree, currentTree);
    }
    
    @Override
    public void printTreesSA(final int step, final Tree bestTree, final Tree S0Tree, final Tree currentTree) {
        this.print.printTreesSA(step, bestTree, S0Tree, currentTree);
    }
    
    @Override
    public void printTreesGA(final int step, final Tree[] trees, final boolean selectionDone) {
        this.print.printTreesGA(step, trees, selectionDone);
    }
    
    @Override
    public void printTreesCP(final int step, final Tree[] trees, final int pop, final boolean recombined) {
        this.print.printTreesCP(step, trees, pop, recombined);
    }
    
    @Override
    public void printTreesCP(final int step, final Tree[][] trees) {
        this.print.printTreesCP(step, trees);
    }
    
    @Override
    public void printEndTreesHeuristic() {
        this.print.printEndTreesHeuristic();
    }
    
    @Override
    public void printConsensus(final int step, final Consensus consensus) {
        this.print.printConsensus(step, consensus);
    }
    
    @Override
    public void trackPerformances(final String action, final int level) {
        this.print.trackPerformances(action, level);
    }
    
    @Override
    public void printAncestralSequences(final List<Tree> trees) {
        this.print.printAncestralSequences(trees);
    }
    
    @Override
    public void updateConsensusTree(final Tree consensusTree) {
        this.print.updateConsensusTree(consensusTree);
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
        this.progress.setText(this.progressId, "Best ML : " + Tools.doubletoString(evaluationsToShow.get("Best solution"), 4));
    }
    
    @Override
    public void showRemainingTime(final long time) {
        this.progress.setTime(this.progressId, time);
    }
    
    @Override
    public void showReplicate() {
    }
    
    @Override
    public void showStageCPMetapopulation(final int numOfSteps) {
        this.progress.newMultiProgress(this.progressId, 0, numOfSteps, "Creating metapopulation");
    }
    
    @Override
    public void showStageDistanceMatrix(final int numOfSteps) {
        this.progress.newMultiProgress(this.progressId, 0, numOfSteps, "Building distance matrix");
    }
    
    @Override
    public void showStageGAPopulation(final int numOfSteps) {
        this.progress.newMultiProgress(this.progressId, 0, numOfSteps, "Creating population");
    }
    
    @Override
    public void showStageOptimization(final int active, final String target) {
        if (active > 0) {
            this.progress.setText(this.progressId, "Intra-step optimization of " + target);
        }
    }
    
    @Override
    public void showStageSATemperature(final int numOfSteps) {
        this.progress.newMultiProgress(this.progressId, 0, numOfSteps, "Setting temperature");
    }
    
    @Override
    public void showStageHCRestart() {
    }
    
    @Override
    public synchronized void showStageSearchProgress(final int currentIteration, final long remainingTime, final int noChangeSteps) {
        if (this.parameters.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.STEPS) || this.parameters.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.STEPS)) {
            this.showNextStep();
        }
        if (this.parameters.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.TIME) || this.parameters.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.TIME)) {
            this.showRemainingTime(remainingTime);
        }
    }
    
    @Override
    public void showStageSearchStart(final String heuristic, final int maxSteps, final double startingEvaluation) {
        this.repStartTime = System.currentTimeMillis();
        final List<String> text = new ArrayList<String>();
        if (this.parameters.sufficientStopConditions.isEmpty() && this.parameters.necessaryStopConditions.isEmpty()) {
            text.add("Don't start " + heuristic + " as user commands");
        }
        else {
            text.add(String.valueOf(heuristic) + (this.parameters.hasManyReplicates() ? (" (Replicate " + this.currentReplicate + ")") : "") + " started at " + new Date(this.repStartTime).toString());
        }
        if (!this.parameters.sufficientStopConditions.isEmpty()) {
            text.add("Stop when ANY of the following sufficient conditions is met: \n");
            for (final Parameters.HeuristicStopCondition condition : this.parameters.sufficientStopConditions) {
                switch (condition) {
                    default: {
                        continue;
                    }
                    case STEPS: {
                        text.add("- " + this.parameters.stopCriterionSteps + " iterations performed" + "\n");
                        continue;
                    }
                    case TIME: {
                        text.add("- Pass over " + new Date(this.repStartTime + (long)this.parameters.stopCriterionTime * 3600L * 1000L).toString() + "\n");
                        continue;
                    }
                    case AUTO: {
                        text.add("- Likelihood stop increasing after " + this.parameters.stopCriterionAutoSteps + " iterations" + "\n");
                        continue;
                    }
                    case CONSENSUS: {
                        text.add("- Mean relative error of " + this.parameters.stopCriterionConsensusInterval + " consecutive consensus trees stay below " + Tools.doubleToPercent(this.parameters.stopCriterionConsensusMRE, 0) + " using trees sampled every " + this.parameters.stopCriterionConsensusGeneration + " generations" + "\n");
                        continue;
                    }
                }
            }
        }
        if (!this.parameters.necessaryStopConditions.isEmpty()) {
            text.add("Stop when ALL of the following necessary conditions are met: \n");
            for (final Parameters.HeuristicStopCondition condition : this.parameters.necessaryStopConditions) {
                switch (condition) {
                    default: {
                        continue;
                    }
                    case STEPS: {
                        text.add("- " + this.parameters.stopCriterionSteps + " iterations performed" + "\n");
                        continue;
                    }
                    case TIME: {
                        text.add("- Pass over " + new Date(this.repStartTime + (long)this.parameters.stopCriterionTime * 3600L * 1000L).toString() + "\n");
                        continue;
                    }
                    case AUTO: {
                        text.add("- Likelihood stop increasing after " + this.parameters.stopCriterionAutoSteps + " iterations" + "\n");
                        continue;
                    }
                    case CONSENSUS: {
                        text.add("- Mean relative error of " + this.parameters.stopCriterionConsensusInterval + " consecutive consensus trees stay below " + Tools.doubleToPercent(this.parameters.stopCriterionConsensusMRE, 0) + " using trees sampled every " + this.parameters.stopCriterionConsensusGeneration + " generations" + "\n");
                        continue;
                    }
                }
            }
        }
        text.add("");
        this.progress.displayEndMessage(text);
        if (this.parameters.stopCriterionSteps > 0) {
            this.maxSteps = maxSteps;
        }
        else {
            this.maxSteps = 0;
        }
        this.currentStep = 0;
        this.progress.newSearchProgress(this.progressId, this.maxSteps, (long)(this.parameters.stopCriterionTime * 3600.0 * 1000.0), Tools.doubletoString(startingEvaluation, 4));
    }
    
    @Override
    public void showStageSearchStop(final List<Tree> solutionTrees, final Map<String, Double> evaluationsToShow) {
        if (this.parameters.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.STEPS) || this.parameters.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.STEPS)) {
            this.currentStep = this.parameters.stopCriterionSteps - 1;
        }
        this.showNextStep();
        if (this.parameters.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.TIME) || this.parameters.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.TIME)) {
            this.showRemainingTime(0L);
        }
        this.showEvaluations(evaluationsToShow);
        final List<String> text = new ArrayList<String>();
        text.add(String.valueOf(this.H.getName(true)) + (this.parameters.hasManyReplicates() ? (" (Replicate " + this.currentReplicate + ")") : "") + " finished at " + new Date(System.currentTimeMillis()).toString());
        text.add("End after " + Tools.doubletoString((System.currentTimeMillis() - this.repStartTime) / 60000.0, 2) + " minutes");
        text.add("Best tree likelihood : " + Tools.doubletoString(evaluationsToShow.get("Best solution"), 4) + "\n");
        this.progress.displayEndMessage(text);
        this.end(solutionTrees);
    }
    
    @Override
    public void showStageStartingTree(final int numOfSteps) {
        this.progress.newMultiProgress(this.progressId, 0, numOfSteps, "Building starting tree");
    }
    
    @Override
    public void showStartingTree(final Tree tree) {
    }
    
    @Override
    public void showNextStep() {
        this.progress.setValue(this.progressId, ++this.currentStep);
    }
    
    @Override
    public void showAutoStopDone(final int noChangeSteps) {
    }
    
    @Override
    public void showText(final String text) {
        this.parent.showText(text);
    }
}

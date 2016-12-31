// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.heuristics;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import metapiga.trees.Consensus;
import java.util.HashMap;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import metapiga.utilities.Tools;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.UnknownTaxonException;
import metapiga.trees.exceptions.UncompatibleOutgroupException;
import metapiga.trees.exceptions.TooManyNeighborsException;
import metapiga.exceptions.OutgroupTooBigException;
import metapiga.trees.exceptions.NullAncestorException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import metapiga.trees.ConsensusMRE;
import metapiga.trees.Tree;
import metapiga.modelization.Dataset;
import metapiga.trees.Operators;
import java.util.Set;
import metapiga.parameters.Parameters;
import metapiga.monitors.Monitor;

public class ConsensusPruning extends Heuristic
{
    private final Monitor monitor;
    private final boolean trackDetails;
    private final boolean trackTrees;
    private final boolean trackConsensus;
    private final boolean trackPerf;
    private volatile boolean stopAskedByUser;
    private final Parameters P;
    private Set<Parameters.HeuristicStopCondition> sufficientStop;
    private Set<Parameters.HeuristicStopCondition> necessaryStop;
    private final int stopCriterionSteps;
    private final double stopCriterionTime;
    private final int stopCriterionAutoSteps;
    private final double stopCriterionAutoThreshold;
    private final double stopCriterionConsensusMRE;
    private final int stopCriterionConsensusGeneration;
    private final int stopCriterionConsensusInterval;
    private Operators operators;
    private final Parameters.Optimization optimization;
    private final double optimizationUse;
    private final Parameters.OptimizationAlgorithm optimizationAlgorithm;
    private final int popNum;
    private final int indNum;
    private final int coreNum;
    private final Parameters.CPConsensus consensusType;
    private final Parameters.CPOperator operatorBehaviour;
    private final double tolerance;
    private final double hybridization;
    private final Parameters.CPOperatorChange operatorChange;
    private final Parameters.CPSelection selection;
    private final double recombination;
    private final double replacementStrength;
    private final Dataset dataset;
    private Tree[][] populations;
    private Tree[][] offspring;
    private Tree bestSolution;
    private int step;
    private String[] allSelectionDetails;
    private ConsensusMRE consensusMRE;
    private int noLikelihoodChangeStop;
    
    public ConsensusPruning(final Parameters P, final Monitor monitor) {
        super(P);
        this.stopAskedByUser = false;
        this.sufficientStop = new HashSet<Parameters.HeuristicStopCondition>();
        this.necessaryStop = new HashSet<Parameters.HeuristicStopCondition>();
        this.monitor = monitor;
        this.trackDetails = monitor.trackHeuristic();
        this.trackTrees = monitor.trackHeuristicTrees();
        this.trackConsensus = monitor.trackConsensus();
        this.trackPerf = monitor.trackPerformances();
        this.sufficientStop = P.sufficientStopConditions;
        this.necessaryStop = P.necessaryStopConditions;
        this.stopCriterionSteps = P.stopCriterionSteps;
        this.stopCriterionTime = P.stopCriterionTime;
        this.stopCriterionAutoSteps = P.stopCriterionAutoSteps;
        this.stopCriterionAutoThreshold = P.stopCriterionAutoThreshold;
        this.stopCriterionConsensusMRE = P.stopCriterionConsensusMRE;
        this.stopCriterionConsensusInterval = P.stopCriterionConsensusInterval;
        this.stopCriterionConsensusGeneration = P.stopCriterionConsensusGeneration;
        this.operators = new Operators(P.operators, P.operatorsParameters, P.operatorsFrequencies, P.operatorIsDynamic, P.dynamicInterval, P.dynamicMin, P.operatorSelection, P.optimizationUse, P.cpPopNum * (P.cpIndNum - 1), monitor);
        this.popNum = P.cpPopNum;
        this.indNum = P.cpIndNum;
        this.coreNum = P.cpCoreNum;
        this.consensusType = P.cpConsensus;
        this.operatorBehaviour = P.cpOperator;
        this.tolerance = P.cpTolerance;
        this.hybridization = P.cpHybridization;
        this.operatorChange = P.cpOperatorChange;
        this.selection = P.cpSelection;
        this.recombination = P.cpRecombination;
        this.replacementStrength = P.cpReplacementStrength;
        this.dataset = P.dataset;
        this.populations = new Tree[this.popNum][this.indNum];
        this.optimization = P.optimization;
        this.optimizationUse = P.optimizationUse;
        this.optimizationAlgorithm = P.optimizationAlgorithm;
        this.P = P;
    }
    
    @Override
    public Tree getBestSolution() {
        return this.bestSolution;
    }
    
    @Override
    public String getName(final boolean full) {
        return full ? Parameters.Heuristic.CP.verbose() : Parameters.Heuristic.CP.toString();
    }
    
    @Override
    public void smoothStop() {
        this.stopAskedByUser = true;
    }
    
    private void setStartingTrees() throws NullAncestorException, OutgroupTooBigException, TooManyNeighborsException, UncompatibleOutgroupException, UnknownTaxonException, UnrootableTreeException, IOException, ClassNotFoundException {
        int numOfSteps = this.popNum * this.indNum + 1;
        final int offpop = (this.coreNum > 1) ? this.popNum : 1;
        switch (this.selection) {
            case RANK:
            case TOURNAMENT: {
                numOfSteps += offpop * this.indNum;
                break;
            }
        }
        this.monitor.showStageCPMetapopulation(numOfSteps);
        for (int pop = 0; pop < this.popNum; ++pop) {
            if (this.P.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
                this.populations[pop][0] = this.P.startingTrees.get(pop).clone();
            }
            else if (this.P.startingTreeGeneration == Parameters.StartingTreeGeneration.NJ) {
                if (pop == 0) {
                    this.populations[pop][0] = this.P.dataset.generateTree(this.P.outgroup, Parameters.StartingTreeGeneration.NJ, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor);
                }
                else {
                    this.populations[pop][0] = this.P.dataset.generateTree(this.P.outgroup, Parameters.StartingTreeGeneration.LNJ, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor);
                }
            }
            else {
                this.populations[pop][0] = this.P.dataset.generateTree(this.P.outgroup, this.P.startingTreeGeneration, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor);
            }
            this.populations[pop][0].setWeight(this.P.moweight.get((pop+1)%this.P.moweight.size()));
            this.populations[pop][0].setName("Best tree of population " + pop);
            this.monitor.showNextStep();
            for (int ind = 1; ind < this.indNum; ++ind) {
                (this.populations[pop][ind] = this.populations[pop][0].clone()).setName("Tree " + ind + " of population " + pop);
                this.monitor.showNextStep();
            }
        }
        Tree best = this.populations[0][0];
        for (int pop2 = 1; pop2 < this.popNum; ++pop2) {
            if (this.populations[pop2][0].isBetterThan(best)) {
                best = this.populations[pop2][0];
            }
        }
        (this.bestSolution = this.P.dataset.generateTree(this.P.outgroup, Parameters.StartingTreeGeneration.RANDOM, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor)).setName("Consensus pruning best solution");
        this.bestSolution.clone(best);
        this.monitor.showNextStep();
        this.offspring = new Tree[offpop][this.indNum];
        switch (this.selection) {
            case RANK:
            case TOURNAMENT: {
                for (int p = 0; p < offpop; ++p) {
                    for (int i = 0; i < this.indNum; ++i) {
                        (this.offspring[p][i] = this.P.dataset.generateTree(this.P.outgroup, Parameters.StartingTreeGeneration.RANDOM, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor)).setName("CP offspring " + p + "-" + i);
                        this.monitor.showNextStep();
                    }
                }
                break;
            }
        }
        final List<Tree> startingTrees = new ArrayList<Tree>();
        for (int pop3 = 0; pop3 < this.popNum; ++pop3) {
            for (int ind2 = 0; ind2 < this.indNum; ++ind2) {
                startingTrees.add(this.populations[pop3][ind2]);
            }
        }
        if (this.monitor.trackStartingTree()) {
            this.monitor.printStartingTrees(startingTrees);
        }
    }
    
    private boolean hasToContinue(final int step, final long currentTime, final long endTime, final int noMREChangeStop) {
        if (this.stopAskedByUser) {
            return false;
        }
        if (this.sufficientStop.isEmpty() && this.necessaryStop.isEmpty()) {
            return false;
        }
        for (final Parameters.HeuristicStopCondition condition : this.sufficientStop) {
            switch (condition) {
                case STEPS: {
                    if (step >= this.stopCriterionSteps) {
                        this.monitor.showText("Consensus Pruning will stop because it has met a sufficient stop condition : " + step + " steps have been done");
                        return false;
                    }
                    continue;
                }
                case TIME: {
                    if (currentTime >= endTime) {
                        this.monitor.showText("Consensus Pruning will stop because it has met a sufficient stop condition : search duration has exceeded " + this.stopCriterionTime + " hours");
                        return false;
                    }
                    continue;
                }
                case AUTO: {
                    if (this.noLikelihoodChangeStop > this.stopCriterionAutoSteps) {
                        this.monitor.showText("Consensus Pruning will stop because it has met a sufficient stop condition : no significantly better solution was found in the last " + this.stopCriterionAutoSteps + " steps");
                        return false;
                    }
                    continue;
                }
                case CONSENSUS: {
                    if (noMREChangeStop >= this.stopCriterionConsensusInterval) {
                        this.monitor.showText("Consensus Pruning will stop because it has met a sufficient stop condition : mean relative error of " + this.stopCriterionConsensusInterval + " consecutive consensus trees have stayed below " + Tools.doubleToPercent(this.stopCriterionConsensusMRE, 0) + " using trees sampled every " + this.stopCriterionConsensusGeneration + " generations");
                        return false;
                    }
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        if (!this.necessaryStop.isEmpty()) {
            boolean stop = true;
            String message = "";
            for (final Parameters.HeuristicStopCondition condition2 : this.necessaryStop) {
                switch (condition2) {
                    case STEPS: {
                        if (step < this.stopCriterionSteps) {
                            stop = false;
                            continue;
                        }
                        message = String.valueOf(message) + step + " steps have been done, ";
                        continue;
                    }
                    case TIME: {
                        if (currentTime < endTime) {
                            stop = false;
                            continue;
                        }
                        message = String.valueOf(message) + "search duration has exceeded " + this.stopCriterionTime + " hours, ";
                        continue;
                    }
                    case AUTO: {
                        if (this.noLikelihoodChangeStop <= this.stopCriterionAutoSteps) {
                            stop = false;
                            continue;
                        }
                        message = String.valueOf(message) + "no significantly better solution was found in the last " + this.stopCriterionAutoSteps + " steps, ";
                        continue;
                    }
                    case CONSENSUS: {
                        if (noMREChangeStop < this.stopCriterionConsensusInterval) {
                            stop = false;
                            continue;
                        }
                        message = String.valueOf(message) + "mean relative error of " + this.stopCriterionConsensusInterval + " consecutive consensus trees have stayed below " + Tools.doubleToPercent(this.stopCriterionConsensusMRE, 0) + " using trees sampled every " + this.stopCriterionConsensusGeneration + " generations, ";
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
            if (stop) {
                this.monitor.showText("Consensus Pruning will stop because it has met all necessary stop conditions : " + message);
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void run() {
        final long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        final long endTime = startTime + (long)(this.stopCriterionTime * 3600.0 * 1000.0);
        this.noLikelihoodChangeStop = 0;
        int noMREChangeStop = 0;
        double lastBestSolution = 0.0;
        try {
            if (this.trackPerf) {
                this.monitor.trackPerformances("Consensus Pruning starting trees generation", 0);
            }
            this.setStartingTrees();
            if (this.trackPerf) {
                this.monitor.trackPerformances("Consensus Pruning starting trees generation", 0);
            }
            final List<Tree> allTrees = new ArrayList<Tree>();
            Tree[][] populations;
            for (int length = (populations = this.populations).length, i = 0; i < length; ++i) {
                final Tree[] pop = populations[i];
                allTrees.addAll(Arrays.asList(pop));
            }
            final Map<String, Double> evaluationsToMonitor = new HashMap<String, Double>();
            this.monitor.showStageSearchStart("Consensus Pruning", this.stopCriterionSteps * (this.indNum - 1) * this.popNum, this.bestSolution.getEvaluation());
            this.monitor.showStartingTree(this.bestSolution);
            this.step = 0;

            //normalization
            double MaxML = 1;
            double MaxMP = 1;

            List<Double> valueList;
            while (this.hasToContinue(this.step, currentTime, endTime, noMREChangeStop)) {
                //normalization
                if(this.step == 0 && this.step == 1){
                    for(int pop = 0;pop<this.popNum;pop++){
                        for(int ind = 0;ind<this.indNum;ind++){
                            valueList = populations[pop][ind].Evaluation();
                            if(valueList.get(0) > MaxML){
                                MaxML = valueList.get(0);
                            }
                            if(valueList.get(1) > MaxMP){
                                MaxMP = valueList.get(1);
                            }
                        }
                    }
                    for(int pop = 0;pop<this.popNum;pop++){
                        for(int ind = 0;ind<this.indNum;ind++){
                            populations[pop][ind].setMPMax(MaxMP);
                            populations[pop][ind].setMLMax(MaxML);
                        }
                    }
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Consensus Pruning step " + this.step, 0);
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Build consensus list", 1);
                }
                final Consensus consensus = new Consensus(allTrees, this.dataset, this.consensusType);
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Build consensus list", 1);
                }
                if (this.trackConsensus) {
                    this.monitor.printConsensus(this.step, consensus);
                }
                if ((this.sufficientStop.contains(Parameters.HeuristicStopCondition.CONSENSUS) || this.necessaryStop.contains(Parameters.HeuristicStopCondition.CONSENSUS)) && this.step % this.stopCriterionConsensusGeneration == 0) {
                    if (this.step == 0) {
                        (this.consensusMRE = new ConsensusMRE()).addConsensus(consensus.getConsensusTree(this.P), this.P, true);
                    }
                    else {
                        this.consensusMRE.addConsensus(consensus.getConsensusTree(this.P), this.P, false);
                        final double mre = this.consensusMRE.meanRelativeError();
                        this.monitor.showCurrentMRE(mre);
                        if (mre < this.stopCriterionConsensusMRE) {
                            ++noMREChangeStop;
                        }
                        else {
                            noMREChangeStop = 0;
                            this.consensusMRE.addConsensus(consensus.getConsensusTree(this.P), this.P, true);
                        }
                    }
                }
                evaluationsToMonitor.put("Best solution", this.bestSolution.getEvaluation());
                this.allSelectionDetails = new String[this.popNum];
                int recombinedPop = -1;
                if (Math.random() < this.hybridization) {
                    recombinedPop = Tools.randInt(this.popNum);
                    evaluationsToMonitor.put("Population " + recombinedPop, this.populations[recombinedPop][0].getEvaluation());
                    final String[] hybridizationResults = new String[this.indNum];
                    for (int curInd = 1; curInd < this.indNum; ++curInd) {
                        if (this.trackPerf) {
                            this.monitor.trackPerformances("Recombination in population " + recombinedPop + " of individual " + curInd, 1);
                        }
                        this.monitor.showStageSearchProgress(this.step, endTime - System.currentTimeMillis(), this.noLikelihoodChangeStop);
                        this.populations[recombinedPop][curInd].setName(String.valueOf(this.getName(true)) + " step " + this.step + " population " + recombinedPop + " individual " + curInd);
                        hybridizationResults[curInd] = consensus.hybridization(this.populations[recombinedPop][curInd], this.populations[recombinedPop]);
                        if (this.trackPerf) {
                            this.monitor.trackPerformances("Recombination in population " + recombinedPop + " of individual " + curInd, 1);
                        }
                    }
                    if (this.trackDetails) {
                        this.monitor.printDetailsCP(this.populations[recombinedPop], hybridizationResults, recombinedPop);
                    }
                    if (this.trackTrees) {
                        this.monitor.printTreesCP(this.step, this.populations[recombinedPop], recombinedPop, true);
                    }
                    this.putBestIndividualFirst(recombinedPop);
                }
                final boolean optimize = (this.optimization == Parameters.Optimization.STOCH && Math.random() < this.optimizationUse) || (this.optimization == Parameters.Optimization.DISC && this.step % (int)this.optimizationUse == 0);
                if (this.P.getLikelihoodCalculationType() == Parameters.LikelihoodCalculationType.CLASSIC) {
                    final ExecutorService executor = Executors.newFixedThreadPool(this.coreNum);
                    for (int curPop = 0; curPop < this.popNum; ++curPop) {
                        if (curPop != recombinedPop) {
                            evaluationsToMonitor.put("Population " + curPop, this.populations[curPop][0].getEvaluation());
                            executor.execute(new IterateGA(curPop, this.populations[curPop], (this.coreNum > 1) ? this.offspring[curPop] : this.offspring[0], endTime, consensus, optimize));
                        }
                    }
                    executor.shutdown();
                    executor.awaitTermination(100L, TimeUnit.DAYS);
                }
                else if (this.P.getLikelihoodCalculationType() == Parameters.LikelihoodCalculationType.GPU) {
                    for (int curPop2 = 0; curPop2 < this.popNum; ++curPop2) {
                        if (curPop2 != recombinedPop) {
                            evaluationsToMonitor.put("Population " + curPop2, this.populations[curPop2][0].getEvaluation());
                            final IterateGA iterationOfGA = new IterateGA(curPop2, this.populations[curPop2], (this.coreNum > 1) ? this.offspring[curPop2] : this.offspring[0], endTime, consensus, optimize);
                            iterationOfGA.run();
                        }
                    }
                }
                this.monitor.showEvaluations(evaluationsToMonitor);
                if (this.operatorChange == Parameters.CPOperatorChange.STEP) {
                    this.operators.nextOperator();
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Update best solution", 1);
                }
                Tree bestOfThisStep = this.populations[0][0];
                for (int pop2 = 1; pop2 < this.popNum; ++pop2) {
                    if (this.populations[pop2][0].isBetterThan(bestOfThisStep)) {
                        bestOfThisStep = this.populations[pop2][0];
                    }
                }
                if (bestOfThisStep.isBetterThan(this.bestSolution)) {
                    this.bestSolution.clone(bestOfThisStep);
                }
                if (!this.P.hasManyReplicates() && this.monitor.getMonitorType() == Monitor.MonitorType.SINGLE_SEARCH_GRAPHICAL) {
                    final List<Tree> bestTrees = new ArrayList<Tree>();
                    for (int pop3 = 0; pop3 < this.popNum; ++pop3) {
                        bestTrees.add(this.populations[pop3][0]);
                    }
                    this.monitor.showCurrentTree(new Consensus(bestTrees, this.P.dataset).getConsensusTree(this.P));
                }
                else {
                    this.monitor.showCurrentTree(this.bestSolution);
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Update best solution", 1);
                }
                if (this.trackDetails) {
                    this.monitor.printDetailsCP(this.step, this.allSelectionDetails, this.populations, this.bestSolution.getEvaluation());
                }
                if (this.trackTrees) {
                    this.monitor.printTreesCP(this.step, this.populations);
                }
                if (this.sufficientStop.contains(Parameters.HeuristicStopCondition.AUTO) || this.necessaryStop.contains(Parameters.HeuristicStopCondition.AUTO)) {
                    if (lastBestSolution - this.bestSolution.getEvaluation() < lastBestSolution * this.stopCriterionAutoThreshold) {
                        ++this.noLikelihoodChangeStop;
                    }
                    else {
                        this.noLikelihoodChangeStop = 0;
                    }
                    lastBestSolution = this.bestSolution.getEvaluation();
                }
                currentTime = System.currentTimeMillis();
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Consensus Pruning step " + this.step, 0);
                }
                ++this.step;
            }
            if (this.optimization != Parameters.Optimization.NEVER && this.optimization != Parameters.Optimization.CONSENSUSTREE) {
                for (int pop4 = 0; pop4 < this.popNum; ++pop4) {
                    this.monitor.showStageOptimization(1, "best solution of population " + pop4);
                    if (this.trackPerf) {
                        this.monitor.trackPerformances("Optimize best solution of population " + pop4 + " with " + this.optimizationAlgorithm, 0);
                    }
                    this.populations[pop4][0] = this.P.getOptimizer(this.populations[pop4][0]).getOptimizedTree();
                    if (this.trackPerf) {
                        this.monitor.trackPerformances("Optimize best solution of population " + pop4 + " with " + this.optimizationAlgorithm, 0);
                    }
                    this.monitor.showStageOptimization(0, "best solution of population " + pop4);
                }
            }
            final List<Tree> solTree = new ArrayList<Tree>();
            solTree.add(this.bestSolution);
            for (int pop5 = 0; pop5 < this.popNum; ++pop5) {
                //if (this.populations[pop5][0].getEvaluation() != this.bestSolution.getEvaluation()) {
                    this.populations[pop5][0].setName("Best individual of population " + pop5);
                    solTree.add(this.populations[pop5][0]);
                //}
                evaluationsToMonitor.put("Population " + pop5, this.populations[pop5][0].getEvaluation());
            }
            evaluationsToMonitor.put("Best solution", this.bestSolution.getEvaluation());
            if (this.trackTrees) {
                this.monitor.printEndTreesHeuristic();
            }
            this.operators.printStatistics();
            this.monitor.showStageSearchStop(solTree, evaluationsToMonitor);
        }
        catch (OutOfMemoryError e2) {
            this.monitor.endFromException(new Exception("Out of memory: please, assign more RAM to MetaPIGA. You can easily do so by using the menu 'Tools --> Memory settings'."));
        }
        catch (Exception e) {
            this.monitor.endFromException(e);
        }
    }
    
    private void putBestIndividualFirst(final int pop) throws NullAncestorException, UnrootableTreeException {
        int bestSol = 0;
        for (int ind = 1; ind < this.indNum; ++ind) {
            if (this.populations[pop][ind].isBetterThan(this.populations[pop][bestSol])) {
                bestSol = ind;
            }
        }
        if (bestSol != 0) {
            final Tree bestIndividual = this.populations[pop][bestSol];
            this.populations[pop][bestSol] = this.populations[pop][0];
            this.populations[pop][0] = bestIndividual;
        }
    }
    
    private synchronized void addSelectionDetails(final int curPop, final String selectionDetails) {
        this.allSelectionDetails[curPop] = selectionDetails;
    }
    
    private class IterateGA implements Runnable
    {
        int curPop;
        Tree[] population;
        Tree[] offspring;
        long endTime;
        Consensus consensus;
        boolean optimize;
        
        public IterateGA(final int curPop, final Tree[] population, final Tree[] offspring, final long endTime, final Consensus consensus, final boolean optimize) {
            this.curPop = curPop;
            this.population = population;
            this.offspring = offspring;
            this.endTime = endTime;
            this.consensus = consensus;
            this.optimize = optimize;
        }
        
        @Override
        public void run() {
            try {
                Tree bestSol = this.population[0];
                int bestInd = 0;
                final Parameters.Operator[] usedOperators = new Parameters.Operator[ConsensusPruning.this.indNum];
                if (ConsensusPruning.this.operatorChange != Parameters.CPOperatorChange.IND) {
                    final Parameters.Operator op = (ConsensusPruning.this.operatorChange == Parameters.CPOperatorChange.POP) ? ConsensusPruning.this.operators.nextOperator() : ConsensusPruning.this.operators.getCurrentOperator();
                    for (int curInd = 1; curInd < ConsensusPruning.this.indNum; ++curInd) {
                        usedOperators[curInd] = op;
                    }
                }
                for (int curInd2 = 1; curInd2 < ConsensusPruning.this.indNum; ++curInd2) {
                    if (ConsensusPruning.this.operatorChange == Parameters.CPOperatorChange.IND) {
                        usedOperators[curInd2] = ConsensusPruning.this.operators.nextOperator();
                    }
                    if (ConsensusPruning.this.trackPerf) {
                        ConsensusPruning.this.monitor.trackPerformances("Mutation in population " + this.curPop + " of individual " + curInd2 + " with " + usedOperators[curInd2], 1);
                    }
                    ConsensusPruning.this.monitor.showStageSearchProgress(ConsensusPruning.this.step, this.endTime - System.currentTimeMillis(), ConsensusPruning.this.noLikelihoodChangeStop);
                    this.population[curInd2].setName(String.valueOf(ConsensusPruning.this.getName(true)) + " step " + ConsensusPruning.this.step + " population " + this.curPop + " individual " + curInd2);
                    if (Math.random() > ConsensusPruning.this.tolerance) {
                        ConsensusPruning.this.operators.mutateTree(this.population[curInd2], usedOperators[curInd2], this.consensus, ConsensusPruning.this.operatorBehaviour);
                    }
                    else {
                        ConsensusPruning.this.operators.mutateTree(this.population[curInd2], usedOperators[curInd2]);
                    }
                    if (ConsensusPruning.this.trackPerf) {
                        ConsensusPruning.this.monitor.trackPerformances("Mutation in population " + this.curPop + " of individual " + curInd2 + " with " + usedOperators[curInd2], 1);
                    }
                }
                if (ConsensusPruning.this.trackDetails) {
                    ConsensusPruning.this.monitor.printDetailsCP(this.population, usedOperators, this.curPop);
                }
                if (ConsensusPruning.this.trackTrees) {
                    ConsensusPruning.this.monitor.printTreesCP(ConsensusPruning.this.step, this.population, this.curPop, false);
                }
                if (this.optimize) {
                    if (ConsensusPruning.this.trackPerf) {
                        ConsensusPruning.this.monitor.trackPerformances("Optimize all individuals with " + ConsensusPruning.this.optimizationAlgorithm, 1);
                    }
                    for (int curInd2 = 0; curInd2 < ConsensusPruning.this.indNum; ++curInd2) {
                        ConsensusPruning.this.monitor.showStageOptimization(1, "individual " + curInd2 + " of population " + this.curPop);
                        this.population[curInd2].clone(ConsensusPruning.this.P.getOptimizer(this.population[curInd2]).getOptimizedTree());
                    }
                    if (ConsensusPruning.this.trackPerf) {
                        ConsensusPruning.this.monitor.trackPerformances("Optimize all individuals with " + ConsensusPruning.this.optimizationAlgorithm, 1);
                    }
                    ConsensusPruning.this.monitor.showStageOptimization(0, "");
                }
                final List<String> selectionDetails = new ArrayList<String>(ConsensusPruning.this.indNum);
                if (ConsensusPruning.this.trackPerf) {
                    ConsensusPruning.this.monitor.trackPerformances("Selection on population " + this.curPop, 1);
                }
                switch (ConsensusPruning.this.selection) {
                    case RANK: {
                        final List<Tree> ranking = new ArrayList<Tree>();
                        final List<Integer> rankingIndNum = new ArrayList<Integer>();
                        for (int ind = 0; ind < ConsensusPruning.this.indNum; ++ind) {
                            int rank;
                            for (rank = 0; rank < ranking.size() && !this.population[ind].isBetterThan(ranking.get(rank)); ++rank) {}
                            ranking.add(rank, this.population[ind]);
                            rankingIndNum.add(rank, ind);
                        }
                        final int numOfBestOffspring = (int)Math.ceil(ConsensusPruning.this.indNum * 0.25);
                        for (int ind = 0; ind < numOfBestOffspring; ++ind) {
                            this.offspring[ind].clone(ranking.get(0));
                            selectionDetails.add("(R1=" + rankingIndNum.get(0) + ")");
                        }
                        for (int ind = numOfBestOffspring; ind < ConsensusPruning.this.indNum; ++ind) {
                            double randNum;
                            double lewisProba;
                            int rank2;
                            for (randNum = Math.random(), lewisProba = 2.0 / (ConsensusPruning.this.indNum * (ConsensusPruning.this.indNum + 1)) * (ConsensusPruning.this.indNum - 1 + 1), rank2 = 1; randNum > lewisProba; randNum -= lewisProba, lewisProba = 2.0 / (ConsensusPruning.this.indNum * (ConsensusPruning.this.indNum + 1)) * (ConsensusPruning.this.indNum - rank2 + 1), ++rank2) {}
                            this.offspring[ind].clone(ranking.get(rank2 - 1));
                            selectionDetails.add("(R" + rank2 + "=" + rankingIndNum.get(rank2 - 1) + ")");
                        }
                        for (int i = 0; i < ConsensusPruning.this.indNum; ++i) {
                            this.population[i].clone(this.offspring[i]);
                        }
                        break;
                    }
                    case TOURNAMENT: {
                        for (int i = 0; i < ConsensusPruning.this.indNum; ++i) {
                            final int ind2 = Tools.randInt(ConsensusPruning.this.indNum - 1);
                            int ind3;
                            do {
                                ind3 = Tools.randInt(ConsensusPruning.this.indNum - 1);
                            } while (ind2 == ind3);
                            int strong;
                            int weak;
                            if (this.population[ind2].isBetterThan(this.population[ind3])) {
                                strong = ind2;
                                weak = ind3;
                            }
                            else {
                                strong = ind3;
                                weak = ind2;
                            }
                            this.offspring[i].clone(this.population[strong]);
                            if (Math.random() < ConsensusPruning.this.recombination) {
                                final Consensus cons = new Consensus(this.offspring[i], this.population[weak], ConsensusPruning.this.P.dataset);
                                if (cons.recombination(this.offspring[i], this.population[weak])) {
                                    selectionDetails.add("(" + ind2 + "vs" + ind3 + "=" + strong + "+" + weak + ")");
                                }
                                else {
                                    selectionDetails.add("(" + ind2 + "vs" + ind3 + "=" + strong + ")");
                                }
                            }
                            else {
                                selectionDetails.add("(" + ind2 + "vs" + ind3 + "=" + strong + ")");
                            }
                        }
                        for (int i = 0; i < ConsensusPruning.this.indNum; ++i) {
                            this.population[i].clone(this.offspring[i]);
                        }
                        ConsensusPruning.this.putBestIndividualFirst(this.curPop);
                        break;
                    }
                    case REPLACEMENT: {
                        final String[] replacedInd = new String[ConsensusPruning.this.indNum];
                        for (int j = 0; j < ConsensusPruning.this.indNum; ++j) {
                            replacedInd[j] = new StringBuilder().append(j).toString();
                        }
                        for (int j = 0; j < (int)Math.floor(ConsensusPruning.this.replacementStrength * ConsensusPruning.this.indNum); ++j) {
                            final int ind4 = Tools.randInt(ConsensusPruning.this.indNum - 1);
                            int ind5;
                            do {
                                ind5 = Tools.randInt(ConsensusPruning.this.indNum - 1);
                            } while (ind4 == ind5);
                            int strong2;
                            int weak2;
                            if (this.population[ind4].isBetterThan(this.population[ind5])) {
                                strong2 = ind4;
                                weak2 = ind5;
                            }
                            else {
                                strong2 = ind5;
                                weak2 = ind4;
                            }
                            if (Math.random() < ConsensusPruning.this.recombination) {
                                final Consensus cons2 = new Consensus(this.population[strong2], this.population[weak2], ConsensusPruning.this.P.dataset);
                                if (cons2.recombination(this.population[weak2], this.population[strong2])) {
                                    selectionDetails.add("(" + replacedInd[ind4] + "vs" + replacedInd[ind5] + "=" + replacedInd[weak2] + "+" + replacedInd[strong2] + ")");
                                    replacedInd[weak2] = String.valueOf(replacedInd[weak2]) + "+" + replacedInd[strong2];
                                }
                                else {
                                    this.population[weak2].clone(this.population[strong2]);
                                    selectionDetails.add("(" + replacedInd[ind4] + "vs" + replacedInd[ind5] + "=" + replacedInd[strong2] + ")");
                                    replacedInd[weak2] = replacedInd[strong2];
                                }
                            }
                            else {
                                this.population[weak2].clone(this.population[strong2]);
                                selectionDetails.add("(" + replacedInd[ind4] + "vs" + replacedInd[ind5] + "=" + replacedInd[strong2] + ")");
                                replacedInd[weak2] = replacedInd[strong2];
                            }
                        }
                        ConsensusPruning.this.putBestIndividualFirst(this.curPop);
                        break;
                    }
                    case KEEPBEST: {
                        for (int ind6 = 1; ind6 < ConsensusPruning.this.indNum; ++ind6) {
                            if (this.population[ind6].isBetterThan(bestSol)) {
                                bestSol = this.population[ind6];
                                bestInd = ind6;
                            }
                        }
                        selectionDetails.add(String.valueOf(bestInd) + " is best individual");
                        for (int ind6 = 0; ind6 < ConsensusPruning.this.indNum; ++ind6) {
                            if (ind6 != bestInd) {
                                if (Math.random() < ConsensusPruning.this.recombination) {
                                    final Consensus cons3 = new Consensus(this.population[ind6], bestSol, ConsensusPruning.this.P.dataset);
                                    if (cons3.recombination(this.population[ind6], bestSol)) {
                                        selectionDetails.add("(" + ind6 + " is recombined)");
                                    }
                                    else {
                                        this.population[ind6].clone(bestSol);
                                    }
                                }
                                else {
                                    this.population[ind6].clone(bestSol);
                                }
                            }
                        }
                        ConsensusPruning.this.putBestIndividualFirst(this.curPop);
                        break;
                    }
                    default: {
                        for (int ind6 = 1; ind6 < ConsensusPruning.this.indNum; ++ind6) {
                            if (this.population[ind6].isBetterThan(bestSol)) {
                                bestSol = this.population[ind6];
                                bestInd = ind6;
                            }
                        }
                        for (int ind6 = ConsensusPruning.this.indNum - 1; ind6 >= 0; --ind6) {
                            if (ind6 != bestInd) {
                                if (!this.population[ind6].isBetterThan(this.population[0])) {
                                    if (Math.random() < ConsensusPruning.this.recombination) {
                                        final Consensus cons3 = new Consensus(this.population[ind6], bestSol, ConsensusPruning.this.P.dataset);
                                        if (cons3.recombination(this.population[ind6], bestSol)) {
                                            selectionDetails.add(String.valueOf(ind6) + " is recombined");
                                        }
                                        else {
                                            this.population[ind6].clone(bestSol);
                                            selectionDetails.add(String.valueOf(ind6) + " is replaced");
                                        }
                                    }
                                    else {
                                        this.population[ind6].clone(bestSol);
                                        selectionDetails.add(String.valueOf(ind6) + " is replaced");
                                    }
                                }
                                else {
                                    selectionDetails.add(String.valueOf(ind6) + " is kept");
                                }
                            }
                            else {
                                selectionDetails.add(String.valueOf(ind6) + " is the best");
                            }
                        }
                        ConsensusPruning.this.putBestIndividualFirst(this.curPop);
                        break;
                    }
                }
                ConsensusPruning.this.addSelectionDetails(this.curPop, ConsensusPruning.this.selection + " : " + selectionDetails);
                if (ConsensusPruning.this.trackPerf) {
                    ConsensusPruning.this.monitor.trackPerformances("Selection on population " + this.curPop, 1);
                }
            }
            catch (OutOfMemoryError e2) {
                ConsensusPruning.this.monitor.endFromException(new Exception("Out of memory: please, assign more RAM to MetaPIGA. You can easily do so by using the menu 'Tools --> Memory settings'."));
            }
            catch (Exception e) {
                ConsensusPruning.this.monitor.endFromException(e);
            }
        }
    }
}

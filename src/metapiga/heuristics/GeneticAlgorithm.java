// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.heuristics;

import java.util.Map;
import metapiga.trees.Consensus;
import metapiga.utilities.Tools;
import java.util.HashMap;
import java.util.Iterator;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.UncompatibleOutgroupException;
import metapiga.trees.exceptions.UnknownTaxonException;
import metapiga.trees.exceptions.TooManyNeighborsException;
import metapiga.exceptions.OutgroupTooBigException;
import metapiga.trees.exceptions.NullAncestorException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import metapiga.trees.Tree;
import metapiga.trees.Operators;
import java.util.Set;
import metapiga.parameters.Parameters;
import metapiga.monitors.Monitor;

public class GeneticAlgorithm extends Heuristic
{
    private final Monitor monitor;
    private final boolean trackDetails;
    private final boolean trackTrees;
    private final boolean trackPerf;
    private volatile boolean stopAskedByUser;
    private Parameters P;
    private Set<Parameters.HeuristicStopCondition> sufficientStop;
    private Set<Parameters.HeuristicStopCondition> necessaryStop;
    private final int stopCriterionSteps;
    private final double stopCriterionTime;
    private final int stopCriterionAutoSteps;
    private final double stopCriterionAutoThreshold;
    private final Operators operators;
    private final Parameters.Optimization optimization;
    private final double optimizationUse;
    private final Parameters.OptimizationAlgorithm optimizationAlgorithm;
    private final int indNum;
    private final Parameters.GAOperatorChange operatorChange;
    private final Parameters.GASelection selection;
    private final double recombination;
    private final double replacementStrength;
    private Tree[] population;
    private Tree[] offspring;
    private Tree bestSolution;
    private int step;
    private int curInd;
    
    public GeneticAlgorithm(final Parameters P, final Monitor monitor) {
        super(P);
        this.stopAskedByUser = false;
        this.sufficientStop = new HashSet<Parameters.HeuristicStopCondition>();
        this.necessaryStop = new HashSet<Parameters.HeuristicStopCondition>();
        this.monitor = monitor;
        this.trackDetails = monitor.trackHeuristic();
        this.trackTrees = monitor.trackHeuristicTrees();
        this.trackPerf = monitor.trackPerformances();
        this.sufficientStop = P.sufficientStopConditions;
        this.necessaryStop = P.necessaryStopConditions;
        this.stopCriterionSteps = P.stopCriterionSteps;
        this.stopCriterionTime = P.stopCriterionTime;
        this.stopCriterionAutoSteps = P.stopCriterionAutoSteps;
        this.stopCriterionAutoThreshold = P.stopCriterionAutoThreshold;
        this.operators = new Operators(P.operators, P.operatorsParameters, P.operatorsFrequencies, P.operatorIsDynamic, P.dynamicInterval, P.dynamicMin, P.operatorSelection, P.optimizationUse, P.gaIndNum - 1, monitor);
        this.indNum = P.gaIndNum;
        this.recombination = P.gaRecombination;
        this.operatorChange = P.gaOperatorChange;
        this.selection = P.gaSelection;
        this.replacementStrength = P.gaReplacementStrength;
        this.population = new Tree[this.indNum];
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
        return full ? Parameters.Heuristic.GA.verbose() : Parameters.Heuristic.GA.toString();
    }
    
    @Override
    public void smoothStop() {
        this.stopAskedByUser = true;
    }
    
    private void setStartingTrees() throws NullAncestorException, OutgroupTooBigException, TooManyNeighborsException, UnknownTaxonException, UncompatibleOutgroupException, UnrootableTreeException {
        this.monitor.showStageGAPopulation(2 * this.indNum + 1);
        if (this.P.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
            for (int ind = 0; ind < this.indNum; ++ind) {
                this.population[ind] = this.P.startingTrees.get(ind).clone();
                this.monitor.showNextStep();
            }
        }
        else if (this.P.startingTreeGeneration == Parameters.StartingTreeGeneration.NJ) {
            this.population[0] = this.P.dataset.generateTree(this.P.outgroup, Parameters.StartingTreeGeneration.NJ, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor);
            this.monitor.showNextStep();
            for (int ind = 1; ind < this.indNum; ++ind) {
                (this.population[ind] = this.population[0].clone()).setName("Tree " + ind);
                this.monitor.showNextStep();
            }
        }
        else {
            for (int ind = 0; ind < this.indNum; ++ind) {
                (this.population[ind] = this.P.dataset.generateTree(this.P.outgroup, this.P.startingTreeGeneration, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor)).setName("Tree " + ind);
                this.monitor.showNextStep();
            }
        }
        Tree best = this.population[0];
        for (int ind2 = 1; ind2 < this.indNum; ++ind2) {
            if (this.population[ind2].isBetterThan(best)) {
                best = this.population[ind2];
            }
        }
        (this.bestSolution = best.clone()).setName("Genetic algorithm best solution");
        this.monitor.showNextStep();
        this.offspring = new Tree[this.indNum];
        switch (this.selection) {
            case RANK:
            case TOURNAMENT: {
                for (int i = 0; i < this.indNum; ++i) {
                    (this.offspring[i] = this.P.dataset.generateTree(this.P.outgroup, Parameters.StartingTreeGeneration.RANDOM, this.P.startingTreeGenerationRange, this.P.startingTreeModel, this.P.startingTreeDistribution, this.P.startingTreeDistributionShape, this.P.startingTreePInv, this.P.startingTreePInvPi, this.P, this.monitor)).setName("GA offspring " + i);
                    this.monitor.showNextStep();
                }
                break;
            }
        }
        final List<Tree> startingTrees = new ArrayList<Tree>();
        for (int ind3 = 0; ind3 < this.indNum; ++ind3) {
            startingTrees.add(this.population[ind3]);
        }
        if (this.monitor.trackStartingTree()) {
            this.monitor.printStartingTrees(startingTrees);
        }
    }
    
    private boolean hasToContinue(final int step, final long currentTime, final long endTime, final int noLikelihoodChangeStop) {
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
                        this.monitor.showText("Genetic Algorithm will stop because it has met a sufficient stop condition : " + step + " steps have been done");
                        return false;
                    }
                    continue;
                }
                case TIME: {
                    if (currentTime >= endTime) {
                        this.monitor.showText("Genetic Algorithm will stop because it has met a sufficient stop condition : search duration has exceeded " + this.stopCriterionTime + " hours");
                        return false;
                    }
                    continue;
                }
                case AUTO: {
                    if (noLikelihoodChangeStop > this.stopCriterionAutoSteps) {
                        this.monitor.showText("Genetic Algorithm will stop because it has met a sufficient stop condition : no significantly better solution was found in the last " + this.stopCriterionAutoSteps + " steps");
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
                        if (noLikelihoodChangeStop <= this.stopCriterionAutoSteps) {
                            stop = false;
                            continue;
                        }
                        message = String.valueOf(message) + "no significantly better solution was found in the last " + this.stopCriterionAutoSteps + " steps, ";
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
            if (stop) {
                this.monitor.showText("Genetic Algorithm will stop because it has met all necessary stop conditions : " + message);
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void run() {
        if (this.P.getLikelihoodCalculationType() == Parameters.LikelihoodCalculationType.GPU) {
            this.allocateGPUcontextAndMemory();
        }
        final long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        final long endTime = startTime + (long)(this.stopCriterionTime * 3600.0 * 1000.0);
        int noLikelihoodChangeStop = 0;
        double lastBestSolution = 0.0;
        try {
            if (this.trackPerf) {
                this.monitor.trackPerformances("Genetic Algorithm starting trees generation", 0);
            }
            this.setStartingTrees();
            if (this.trackPerf) {
                this.monitor.trackPerformances("Genetic Algorithm starting trees generation", 0);
            }
            final List<Tree> allTrees = new ArrayList<Tree>();
            Tree[] population;
            for (int length = (population = this.population).length, k = 0; k < length; ++k) {
                final Tree ind = population[k];
                allTrees.add(ind);
            }
            final Map<String, Double> evaluationsToMonitor = new HashMap<String, Double>();
            this.monitor.showStageSearchStart("Genetic Algorithm", this.stopCriterionSteps * (this.indNum - 1), this.bestSolution.getEvaluation());
            this.monitor.showStartingTree(this.bestSolution);
            this.step = 0;
            while (this.hasToContinue(this.step, currentTime, endTime, noLikelihoodChangeStop)) {
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Genetic Algorithm step " + this.step, 0);
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Graphical monitoring", 1);
                }
                evaluationsToMonitor.put("Best solution", this.bestSolution.getEvaluation());
                this.monitor.showEvaluations(evaluationsToMonitor);
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Graphical monitoring", 1);
                }
                Tree bestSol = this.population[0];
                int bestInd = 0;
                final Parameters.Operator[] usedOperators = new Parameters.Operator[this.indNum];
                this.curInd = 1;
                while (this.curInd < this.indNum) {
                    if (this.trackPerf) {
                        this.monitor.trackPerformances("Mutation of individual " + this.curInd + " with " + this.operators.getCurrentOperator(), 1);
                    }
                    this.monitor.showStageSearchProgress(this.step, endTime - currentTime, noLikelihoodChangeStop);
                    this.population[this.curInd].setName(String.valueOf(this.getName(true)) + " step " + this.step + " individual " + this.curInd);
                    this.operators.mutateTree(this.population[this.curInd]);
                    usedOperators[this.curInd] = this.operators.getCurrentOperator();
                    if (this.trackPerf) {
                        this.monitor.trackPerformances("Mutation of individual " + this.curInd + " with " + this.operators.getCurrentOperator(), 1);
                    }
                    if (this.operatorChange == Parameters.GAOperatorChange.IND) {
                        this.operators.nextOperator();
                    }
                    ++this.curInd;
                }
                if (this.trackDetails) {
                    this.monitor.printDetailsGA(this.step, this.population, usedOperators);
                }
                if (this.trackTrees) {
                    this.monitor.printTreesGA(this.step, this.population, false);
                }
                if ((this.optimization == Parameters.Optimization.STOCH && Math.random() < this.optimizationUse) || (this.optimization == Parameters.Optimization.DISC && this.step % (int)this.optimizationUse == 0)) {
                    if (this.trackPerf) {
                        this.monitor.trackPerformances("Optimize all individuals with " + this.optimizationAlgorithm, 1);
                    }
                    this.curInd = 0;
                    while (this.curInd < this.indNum) {
                        this.monitor.showStageOptimization(1, "individual " + this.curInd);
                        this.population[this.curInd].clone(this.P.getOptimizer(this.population[this.curInd]).getOptimizedTree());
                        ++this.curInd;
                    }
                    if (this.trackPerf) {
                        this.monitor.trackPerformances("Optimize all individuals with " + this.optimizationAlgorithm, 1);
                    }
                    this.monitor.showStageOptimization(0, "");
                }
                final List<String> selectionDetails = new ArrayList<String>(this.indNum);
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Selection", 1);
                }
                switch (this.selection) {
                    case RANK: {
                        final List<Tree> ranking = new ArrayList<Tree>();
                        final List<Integer> rankingIndNum = new ArrayList<Integer>();
                        for (int ind2 = 0; ind2 < this.indNum; ++ind2) {
                            int rank;
                            for (rank = 0; rank < ranking.size() && !this.population[ind2].isBetterThan(ranking.get(rank)); ++rank) {}
                            ranking.add(rank, this.population[ind2]);
                            rankingIndNum.add(rank, ind2);
                        }
                        final int numOfBestOffspring = (int)Math.ceil(this.indNum * 0.25);
                        for (int ind2 = 0; ind2 < numOfBestOffspring; ++ind2) {
                            this.offspring[ind2].clone(ranking.get(0));
                            selectionDetails.add("(R1=" + rankingIndNum.get(0) + ")");
                        }
                        for (int ind2 = numOfBestOffspring; ind2 < this.indNum; ++ind2) {
                            double randNum;
                            double lewisProba;
                            int rank2;
                            for (randNum = Math.random(), lewisProba = 2.0 / (this.indNum * (this.indNum + 1)) * (this.indNum - 1 + 1), rank2 = 1; randNum > lewisProba; randNum -= lewisProba, lewisProba = 2.0 / (this.indNum * (this.indNum + 1)) * (this.indNum - rank2 + 1), ++rank2) {}
                            this.offspring[ind2].clone(ranking.get(rank2 - 1));
                            selectionDetails.add("(R" + rank2 + "=" + rankingIndNum.get(rank2 - 1) + ")");
                        }
                        for (int i = 0; i < this.indNum; ++i) {
                            this.population[i].clone(this.offspring[i]);
                        }
                        break;
                    }
                    case TOURNAMENT: {
                        for (int i = 0; i < this.indNum; ++i) {
                            final int ind3 = Tools.randInt(this.indNum - 1);
                            int ind4;
                            do {
                                ind4 = Tools.randInt(this.indNum - 1);
                            } while (ind3 == ind4);
                            int strong;
                            int weak;
                            if (this.population[ind3].isBetterThan(this.population[ind4])) {
                                strong = ind3;
                                weak = ind4;
                            }
                            else {
                                strong = ind4;
                                weak = ind3;
                            }
                            this.offspring[i].clone(this.population[strong]);
                            if (Math.random() < this.recombination) {
                                final Consensus consensus = new Consensus(this.offspring[i], this.population[weak], this.P.dataset);
                                if (consensus.recombination(this.offspring[i], this.population[weak])) {
                                    selectionDetails.add("(" + ind3 + "vs" + ind4 + "=" + strong + "+" + weak + ")");
                                }
                                else {
                                    selectionDetails.add("(" + ind3 + "vs" + ind4 + "=" + strong + ")");
                                }
                            }
                            else {
                                selectionDetails.add("(" + ind3 + "vs" + ind4 + "=" + strong + ")");
                            }
                        }
                        for (int i = 0; i < this.indNum; ++i) {
                            this.population[i].clone(this.offspring[i]);
                        }
                        this.putBestIndividualFirst();
                        break;
                    }
                    case REPLACEMENT: {
                        final String[] replacedInd = new String[this.indNum];
                        for (int j = 0; j < this.indNum; ++j) {
                            replacedInd[j] = new StringBuilder().append(j).toString();
                        }
                        for (int j = 0; j < (int)Math.floor(this.replacementStrength * this.indNum); ++j) {
                            final int ind5 = Tools.randInt(this.indNum - 1);
                            int ind6;
                            do {
                                ind6 = Tools.randInt(this.indNum - 1);
                            } while (ind5 == ind6);
                            int strong2;
                            int weak2;
                            if (this.population[ind5].isBetterThan(this.population[ind6])) {
                                strong2 = ind5;
                                weak2 = ind6;
                            }
                            else {
                                strong2 = ind6;
                                weak2 = ind5;
                            }
                            if (Math.random() < this.recombination && !replacedInd[ind5].equals(replacedInd[ind6])) {
                                final Consensus consensus2 = new Consensus(this.population[strong2], this.population[weak2], this.P.dataset);
                                if (consensus2.recombination(this.population[weak2], this.population[strong2])) {
                                    selectionDetails.add("(" + replacedInd[ind5] + "vs" + replacedInd[ind6] + "=" + replacedInd[weak2] + "+" + replacedInd[strong2] + ")");
                                    replacedInd[weak2] = String.valueOf(replacedInd[weak2]) + "+" + replacedInd[strong2];
                                }
                                else {
                                    this.population[weak2].clone(this.population[strong2]);
                                    selectionDetails.add("(" + replacedInd[ind5] + "vs" + replacedInd[ind6] + "=" + replacedInd[strong2] + "*)");
                                    replacedInd[weak2] = replacedInd[strong2];
                                }
                            }
                            else {
                                this.population[weak2].clone(this.population[strong2]);
                                selectionDetails.add("(" + replacedInd[ind5] + "vs" + replacedInd[ind6] + "=" + replacedInd[strong2] + ")");
                                replacedInd[weak2] = replacedInd[strong2];
                            }
                        }
                        this.putBestIndividualFirst();
                        break;
                    }
                    case KEEPBEST: {
                        for (int ind7 = 1; ind7 < this.indNum; ++ind7) {
                            if (this.population[ind7].isBetterThan(bestSol)) {
                                bestSol = this.population[ind7];
                                bestInd = ind7;
                            }
                        }
                        selectionDetails.add(String.valueOf(bestInd) + " is best individual");
                        for (int ind7 = 0; ind7 < this.indNum; ++ind7) {
                            if (ind7 != bestInd) {
                                if (Math.random() < this.recombination) {
                                    final Consensus consensus3 = new Consensus(this.population[ind7], bestSol, this.P.dataset);
                                    if (consensus3.recombination(this.population[ind7], bestSol)) {
                                        selectionDetails.add("(" + ind7 + " is recombined)");
                                    }
                                    else {
                                        this.population[ind7].clone(bestSol);
                                    }
                                }
                                else {
                                    this.population[ind7].clone(bestSol);
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        for (int ind7 = 1; ind7 < this.indNum; ++ind7) {
                            if (this.population[ind7].isBetterThan(bestSol)) {
                                bestSol = this.population[ind7];
                                bestInd = ind7;
                            }
                        }
                        for (int ind7 = this.indNum - 1; ind7 >= 0; --ind7) {
                            if (ind7 != bestInd) {
                                if (!this.population[ind7].isBetterThan(this.population[0])) {
                                    if (Math.random() < this.recombination) {
                                        final Consensus consensus3 = new Consensus(this.population[ind7], bestSol, this.P.dataset);
                                        if (consensus3.recombination(this.population[ind7], bestSol)) {
                                            selectionDetails.add(String.valueOf(ind7) + " is recombined");
                                        }
                                        else {
                                            this.population[ind7].clone(bestSol);
                                            selectionDetails.add(String.valueOf(ind7) + " is replaced");
                                        }
                                    }
                                    else {
                                        this.population[ind7].clone(bestSol);
                                        selectionDetails.add(String.valueOf(ind7) + " is replaced");
                                    }
                                }
                                else {
                                    selectionDetails.add(String.valueOf(ind7) + " is kept");
                                }
                            }
                            else {
                                selectionDetails.add(String.valueOf(ind7) + " is the best");
                            }
                        }
                        break;
                    }
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Selection", 1);
                }
                if (this.operatorChange == Parameters.GAOperatorChange.STEP) {
                    this.operators.nextOperator();
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Update best solution", 1);
                }
                final Tree bestOfThisStep = this.population[0];
                if (bestOfThisStep.isBetterThan(this.bestSolution)) {
                    this.bestSolution.clone(bestOfThisStep);
                    this.monitor.showCurrentTree(this.bestSolution);
                }
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Update best solution", 1);
                }
                if (this.trackDetails) {
                    this.monitor.printDetailsGA(this.step, this.selection + " : " + selectionDetails, this.population, this.bestSolution.getEvaluation());
                }
                if (this.trackTrees) {
                    this.monitor.printTreesGA(this.step, this.population, true);
                }
                if (this.sufficientStop.contains(Parameters.HeuristicStopCondition.AUTO) || this.necessaryStop.contains(Parameters.HeuristicStopCondition.AUTO)) {
                    if (lastBestSolution - this.bestSolution.getEvaluation() < lastBestSolution * this.stopCriterionAutoThreshold) {
                        ++noLikelihoodChangeStop;
                    }
                    else {
                        noLikelihoodChangeStop = 0;
                    }
                    lastBestSolution = this.bestSolution.getEvaluation();
                }
                currentTime = System.currentTimeMillis();
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Genetic Algorithm step " + this.step, 0);
                }
                ++this.step;
            }
            if (this.optimization != Parameters.Optimization.NEVER && this.optimization != Parameters.Optimization.CONSENSUSTREE) {
                this.monitor.showStageOptimization(1, "best solution");
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Optimize best solution with " + this.optimizationAlgorithm, 0);
                }
                this.bestSolution = this.P.getOptimizer(this.bestSolution).getOptimizedTree();
                if (this.trackPerf) {
                    this.monitor.trackPerformances("Optimize best solution with " + this.optimizationAlgorithm, 0);
                }
                this.monitor.showStageOptimization(0, "best solution");
            }
            final List<Tree> solTree = new ArrayList<Tree>();
            solTree.add(this.bestSolution);
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
    
    private void putBestIndividualFirst() throws NullAncestorException, UnrootableTreeException {
        int bestSol = 0;
        for (int ind = 1; ind < this.indNum; ++ind) {
            if (this.population[ind].isBetterThan(this.population[bestSol])) {
                bestSol = ind;
            }
        }
        if (bestSol != 0) {
            final Tree bestIndividual = this.population[bestSol];
            this.population[bestSol] = this.population[0];
            this.population[0] = bestIndividual;
        }
    }
}

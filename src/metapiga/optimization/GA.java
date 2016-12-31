// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.optimization;

import metapiga.monitors.OptimizationMonitor;
import javax.swing.JDialog;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.monitors.Monitor;
import metapiga.monitors.InactiveMonitor;
import java.util.Iterator;
import java.util.Set;
import metapiga.trees.Tree;
import metapiga.heuristics.GeneticAlgorithm;
import metapiga.parameters.Parameters;

public class GA implements Optimizer
{
    private final Parameters P;
    private final int steps;
    private GeneticAlgorithm ga;
    
    public GA(final Tree tree, final Set<Parameters.OptimizationTarget> targetsToOptimize) {
        this(tree, targetsToOptimize, 200);
    }
    
    public GA(final Tree tree, final Set<Parameters.OptimizationTarget> targetsToOptimize, final int steps) {
        this.P = new Parameters("Optimization");
        this.steps = steps;
        this.P.necessaryStopConditions.add(Parameters.HeuristicStopCondition.AUTO);
        this.P.stopCriterionAutoSteps = steps;
        this.P.stopCriterionAutoThreshold = 1.0E-4;
        this.P.gaIndNum = 8;
        this.P.startingTreeGeneration = Parameters.StartingTreeGeneration.GIVEN;
        for (int i = 0; i < this.P.gaIndNum; ++i) {
            this.P.startingTrees.add(tree);
        }
        this.P.gaOperatorChange = Parameters.GAOperatorChange.STEP;
        this.P.gaRecombination = 0.0;
        this.P.gaSelection = Parameters.GASelection.TOURNAMENT;
        this.P.operatorSelection = Parameters.OperatorSelection.RANDOM;
        this.P.optimization = Parameters.Optimization.NEVER;
        this.P.operators.clear();
        for (final Parameters.OptimizationTarget target : targetsToOptimize) {
            switch (target) {
                default: {
                    continue;
                }
                case BL: {
                    this.P.operators.add(Parameters.Operator.BLM);
                    continue;
                }
                case R: {
                    this.P.operators.add(Parameters.Operator.RPM);
                    this.P.operatorsParameters.put(Parameters.Operator.RPM, 1);
                    continue;
                }
                case GAMMA: {
                    this.P.operators.add(Parameters.Operator.GDM);
                    continue;
                }
                case PINV: {
                    this.P.operators.add(Parameters.Operator.PIM);
                    continue;
                }
                case APRATE: {
                    this.P.operators.add(Parameters.Operator.APRM);
                    continue;
                }
            }
        }
        this.P.evaluationDistribution = tree.getEvaluationDistribution();
        this.P.evaluationModel = tree.getEvaluationModel();
        this.P.setLikelihoodCalcualtionType(tree.getLikelihoodCalculationType());
        this.P.dataset = tree.getDataset();
    }
    
    @Override
    public Tree getOptimizedTree() throws NullAncestorException, UnrootableTreeException {
        if (this.P.operators.isEmpty()) {
            return this.P.startingTrees.get(0);
        }
        this.ga = new GeneticAlgorithm(this.P, new InactiveMonitor());
        final Thread T = new Thread(this.ga);
        T.start();
        try {
            T.join();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        final Tree bestSol = this.ga.getBestSolution();
        this.ga = null;
        return bestSol;
    }
    
    @Override
    public Tree getOptimizedTreeWithProgress(final JDialog owner, final String title, final int idBar, final int maxBar) throws NullAncestorException, UnrootableTreeException {
        final OptimizationMonitor monitor = new OptimizationMonitor(owner, title, this.steps, idBar, maxBar);
        this.ga = new GeneticAlgorithm(this.P, monitor);
        monitor.setOptimizer(this);
        final Thread T = new Thread(this.ga);
        T.start();
        try {
            T.join();
        }
        catch (InterruptedException ie) {
            this.ga.smoothStop();
        }
        final Tree bestSol = this.ga.getBestSolution();
        this.ga = null;
        return bestSol;
    }
    
    @Override
    public Tree getOptimizedTreeWithProgress(final JDialog owner, final String title) throws NullAncestorException, UnrootableTreeException {
        return this.getOptimizedTreeWithProgress(owner, title, 0, 1);
    }
    
    @Override
    public void stop() {
        this.ga.smoothStop();
    }
}

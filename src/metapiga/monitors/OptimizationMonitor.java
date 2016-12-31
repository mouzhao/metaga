// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import java.util.Map;
import metapiga.modelization.DistanceMatrix;
import metapiga.parameters.Parameters;
import metapiga.trees.Consensus;
import javax.swing.JOptionPane;
import metapiga.trees.Tree;
import java.util.List;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import metapiga.utilities.Tools;

import java.awt.BorderLayout;

import metapiga.optimization.Optimizer;
import javax.swing.JProgressBar;
import javax.swing.JDialog;

public class OptimizationMonitor implements Monitor
{
    private final JDialog progressDialog;
    private final JProgressBar progressBar;
    private Optimizer optimizer;
    
    public OptimizationMonitor(final JDialog owner, final String title, final int maxAuto, final int idBar, final int maxBar) {
        this.progressBar = new JProgressBar();
        (this.progressDialog = new JDialog(owner, false)).setLayout(new BorderLayout());
        //this.progressDialog.setIconImage(Tools.getScaledIcon(MainFrame.imageMetapiga, 32).getImage());
        this.progressDialog.setDefaultCloseOperation(0);
        this.progressDialog.setResizable(false);
        this.progressDialog.setUndecorated(true);
        final JButton btnStop = new JButton("STOP CURRENT");
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                if (OptimizationMonitor.this.optimizer != null) {
                    OptimizationMonitor.this.optimizer.stop();
                }
            }
        });
        btnStop.setFont(new Font("Tahoma", 1, 16));
        btnStop.setForeground(Color.RED);
        this.progressDialog.getContentPane().add(btnStop, "East");
        //btnStop.setIcon(MainFrame.imageMetapiga);
        btnStop.setVerticalTextPosition(3);
        btnStop.setHorizontalTextPosition(0);
        final JPanel panel = new JPanel();
        this.progressDialog.getContentPane().add(panel, "Center");
        panel.setLayout(new GridBagLayout());
        panel.add(this.progressBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.progressBar.setMaximum(maxAuto);
        this.progressBar.setString(title);
        this.progressBar.setStringPainted(true);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = 170;
        if (height * maxBar > screenSize.height) {
            height = screenSize.height / (maxBar + 2);
            btnStop.setHorizontalTextPosition(4);
            btnStop.setVerticalTextPosition(0);
            //btnStop.setIcon(Tools.getScaledIcon(MainFrame.imageMetapiga, height));
        }
        this.progressDialog.setSize((int)screenSize.getWidth() / 2, height);
        final Dimension windowSize = this.progressDialog.getSize();
        this.progressDialog.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height * maxBar) / 2 + idBar * windowSize.height));
        this.progressDialog.setTitle(title);
    }
    
    public void setOptimizer(final Optimizer optimizer) {
        this.optimizer = optimizer;
    }
    
    @Override
    public MonitorType getMonitorType() {
        return MonitorType.OPTIMIZATION;
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
        e.printStackTrace();
        JOptionPane.showMessageDialog(this.progressDialog, Tools.getErrorPanel("Error during optimization", e), "Error", 0);
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
        this.progressBar.setValue(noChangeSteps);
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
        this.showAutoStopDone(noChangeSteps);
    }
    
    @Override
    public void showStageSearchStart(final String heuristic, final int maxSteps, final double startingEvaluation) {
        this.progressDialog.setVisible(true);
        this.progressDialog.toFront();
        this.progressDialog.requestFocus();
        this.showAutoStopDone(0);
    }
    
    @Override
    public void showStageSearchStop(final List<Tree> solutionTrees, final Map<String, Double> evaluationsToShow) {
        this.progressDialog.setVisible(false);
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

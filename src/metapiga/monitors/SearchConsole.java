// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import java.util.*;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.Component;
import javax.swing.JOptionPane;

import metapiga.trees.Consensus;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.utilities.Tools;
import java.text.DateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import metapiga.MetaPIGA;
import java.io.File;
import java.text.SimpleDateFormat;

import metapiga.trees.ConsensusMRE;
import java.util.concurrent.CountDownLatch;
import metapiga.ProgressHandling;
import metapiga.trees.Tree;
import metapiga.parameters.Parameters;
import javax.swing.DefaultListModel;
import javax.swing.text.BadLocationException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;

public class SearchConsole implements Runnable
{
    private ExecutorService executor;
    private int nJobs;
    private AtomicInteger replicatesDone;
    private AtomicInteger replicatesAvailable;
    private AtomicInteger replicatesTotal;
    private AtomicInteger countMRE;
    private DefaultListModel batch;
    private Parameters currentParameters;
    private List<Tree> allSolutionTrees;
    private String runDirectory;
    private String runLabel;
    public String dirPath;
    ProgressHandling progress;
    private SearchConsoleMonitor[] monitors;
    private CountDownLatch latch;
    private ConsensusMRE consensusMRE;
    
    public SearchConsole(final DefaultListModel parameters) {
        this.allSolutionTrees = new Vector<Tree>();
        this.consensusMRE = new ConsensusMRE();
        this.batch = parameters;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < this.batch.getSize(); ++i) {
            this.currentParameters = (Parameters) this.batch.get(i);
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd - HH_mm_ss");
            this.runDirectory = this.currentParameters.outputDir;
            File dir = new File(this.runDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            this.runLabel = String.valueOf(this.currentParameters.label) + " - " + df.format(System.currentTimeMillis());
            this.dirPath = String.valueOf(this.runDirectory) + "/" + this.runLabel;
            dir = new File(this.dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            switch (this.currentParameters.replicatesStopCondition) {
                case NONE: {
                    this.replicatesTotal = new AtomicInteger(this.currentParameters.replicatesNumber);
                    break;
                }
                case MRE: {
                    this.replicatesTotal = new AtomicInteger(this.currentParameters.replicatesMaximum);
                    break;
                }
            }
            this.replicatesDone = new AtomicInteger();
            this.replicatesAvailable = new AtomicInteger();
            this.countMRE = new AtomicInteger();
            this.nJobs = this.currentParameters.replicatesParallel;
            this.latch = null;
            this.monitors = new SearchConsoleMonitor[this.nJobs];
            (this.progress = new ProgressHandling(this.nJobs)).setUI(MetaPIGA.UI.CONSOLE);
            for (int j = 0; j < this.nJobs; ++j) {
                this.monitors[j] = new SearchConsoleMonitor(this, this.progress, j, this.currentParameters, this.dirPath);
            }
            try {
                final long startTime = System.currentTimeMillis();
                this.showText("\nRunning " + this.runLabel + "\n" + this.currentParameters.printParameters() + "\n");
                this.executor = Executors.newFixedThreadPool(this.currentParameters.useGrid ? Math.min(this.nJobs, 1000) : this.nJobs);
                for (int k = 0; k < this.nJobs; ++k) {
                    this.executor.execute(this.monitors[k]);
                }
                this.executor.shutdown();
                this.executor.awaitTermination(1000L, TimeUnit.DAYS);
                Thread.sleep(500L);
                this.end(startTime);
            }
            catch (Exception e) {
                this.endFromException(e);
            }
        }
        this.showText("Everything is finished !");
    }
    
    private void end(final long startTime) {
        if (this.currentParameters.hasManyReplicates()) {
            this.showText("\nAll replicates done in " + Tools.doubletoString((System.currentTimeMillis() - startTime) / 60000.0, 2) + " minutes");
        }
        Tree consensusTree = null;
//        if (this.currentParameters.hasManyReplicates()) {
//            try {
//                final Consensus consensus = new Consensus(this.allSolutionTrees, this.currentParameters.dataset);
//                consensusTree = consensus.getConsensusTree(this.currentParameters);
//                if (this.currentParameters.optimization == Parameters.Optimization.CONSENSUSTREE) {
//                    this.showText("\nOptimizing final consensus tree\n");
//                    final Tree optimizedConsensusTree = this.currentParameters.getOptimizer(consensusTree).getOptimizedTree();
//                    consensusTree.cloneWithConsensus(optimizedConsensusTree);
//                }
//            }
//            catch (Exception ex) {
//                JOptionPane.showMessageDialog(null, Tools.getErrorPanel("Cannot display result tree(s)", ex), "Consensus tree Error", 0);
//                System.out.println("Cannot build consensus tree : " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }
        if (consensusTree != null) {
            consensusTree.setName(String.valueOf(this.runLabel) + " - " + consensusTree.getName());
        }
        for (final Tree t : this.allSolutionTrees) {
            t.setName(String.valueOf(this.runLabel) + " - " + t.getName());
        }
        this.showText("\nJOB DONE\n");
        final File output = new File(String.valueOf(this.dirPath) + "/" + "Results.nex");
        try {
            final FileWriter fw = new FileWriter(output);
            final BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#NEXUS");
            bw.newLine();
            bw.newLine();
            this.currentParameters.getMetapigaBlock().writeObject(bw);
            bw.newLine();
            this.currentParameters.charactersBlock.writeObject(bw);
            bw.newLine();
            if (this.currentParameters.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
                this.currentParameters.writeTreeBlock(bw);
                bw.newLine();
            }
            bw.write("Begin trees;  [Result trees]");
            bw.newLine();
            if (consensusTree != null) {
                bw.write(consensusTree.toNewickLineWithML(consensusTree.getName(), false, true));
                bw.newLine();
            }
            for (final Tree t2 : this.allSolutionTrees) {
                bw.write(t2.toNewickLineWithML(t2.getName(), false, true));
                bw.newLine();
            }
            bw.write("End;");
            bw.newLine();
            bw.close();
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            this.showText("\n Error when writing results file");
            this.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.showText("\tat " + el.toString());
            }
        }
        System.gc();
    }
    
    public int addSolutionTree(final List<Tree> trees) {
        //TODO 增加非支配判断
        //1.满足  不被其他解支配 条件，才能被加入
        //2. 加入后删除集合中被它支配的解
        int result=1;
        List<Tree> dominated;
        for(Tree newtree : trees){
            if(this.allSolutionTrees.isEmpty()){
                this.allSolutionTrees.add(newtree);
            }else{
                dominated = new ArrayList<>();
                for(Tree tree : this.allSolutionTrees){
                    result = paretoSolve(tree,newtree);
                    if(result == 1) break;
                    else if(result == 2){
                        dominated.add(tree);
                    }else {
                        continue;
                    }
                }
                if(result != 1){
                    this.allSolutionTrees.add(newtree);
                    if(!dominated.isEmpty()){
                        this.allSolutionTrees.removeAll(dominated);
                    }

                }
                dominated.clear();
            }
//            try {
//                String newick = newtree.toNewickLineWithML(newtree.getName(),true,true);
//                String aa = newick;
//            } catch (UnrootableTreeException e) {
//                e.printStackTrace();
//            } catch (NullAncestorException e) {
//                e.printStackTrace();
//            } catch (BadLocationException e) {
//                e.printStackTrace();
//            }
            if(this.allSolutionTrees.size() == 10) break;
        }

        //this.allSolutionTrees.addAll(trees);
        this.showReplicate();
        return this.allSolutionTrees.size();
    }

    public int paretoSolve(Tree oldtree,Tree newtree){
        if((oldtree.getLikelihoodValue()<newtree.getLikelihoodValue() && oldtree.getParsimonyValue()<=newtree.getParsimonyValue()) ||
                (oldtree.getLikelihoodValue()<=newtree.getLikelihoodValue() && oldtree.getParsimonyValue()<newtree.getParsimonyValue())){
           return 1; //new 被 old 支配
        } else if((oldtree.getLikelihoodValue()>newtree.getLikelihoodValue() && oldtree.getParsimonyValue()>=newtree.getParsimonyValue()) ||
                (oldtree.getLikelihoodValue()>=newtree.getLikelihoodValue() && oldtree.getParsimonyValue()>newtree.getParsimonyValue())){
            return 2; //old 被 new 支配
        }else{
            return 3; //互不支配
        }
    }

    public void endFromException(final Exception e) {
        e.printStackTrace();
        this.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
        StackTraceElement[] stackTrace;
        for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
            final StackTraceElement el = stackTrace[i];
            this.showText("\tat " + el.toString());
        }
    }
    
    public void showText(final String text) {
        System.out.println(text);
    }
    
    public int getNextReplicate() {
        if (this.replicatesAvailable.get() < this.replicatesTotal.get()) {
            return this.replicatesAvailable.incrementAndGet();
        }
        return -1;
    }
    
    public void showReplicate() {
        if (this.replicatesDone.incrementAndGet() > 1) {
            try {
                final Consensus consensus = new Consensus(this.allSolutionTrees, this.currentParameters.dataset);
                synchronized (this.consensusMRE) {
                    final Tree consensusTree = consensus.getConsensusTree(this.currentParameters);
                    consensusTree.setName("Consensus_tree_" + this.replicatesDone + "_replicates");
                    this.consensusMRE.addConsensus(consensusTree, this.currentParameters, false);
                    final double mre = this.consensusMRE.meanRelativeError();
                    consensusTree.setName("Consensus_tree_" + this.replicatesDone + "_replicates" + " [MRE: " + Tools.doubleToPercent(mre, 2) + "]");
                    if (this.replicatesDone.get() > this.currentParameters.replicatesMinimum && mre < this.currentParameters.replicatesMRE) {
                        this.countMRE.incrementAndGet();
                        if (this.currentParameters.replicatesStopCondition == Parameters.ReplicatesStopCondition.MRE && this.countMRE.get() >= this.currentParameters.replicatesInterval) {
                            this.replicatesAvailable = this.replicatesTotal;
                            this.showText("MRE condition has been met, all remaining replicates have been cancelled.");
                        }
                    }
                    else {
                        this.countMRE.set(0);
                        this.consensusMRE.addConsensus(consensusTree, this.currentParameters, true);
                    }
                    this.monitors[0].updateConsensusTree(consensusTree);
                }
                // monitorexit(this.consensusMRE)
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double[] getBestSolutions() {
        final double[] bestSolutions = new double[this.monitors.length];
        for (int i = 0; i < this.monitors.length; ++i) {
            bestSolutions[i] = this.monitors[i].getBestLikelihood();
        }
        return bestSolutions;
    }
}

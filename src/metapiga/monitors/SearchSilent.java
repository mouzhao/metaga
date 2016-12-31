// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.JOptionPane;
import metapiga.utilities.Tools;
import metapiga.trees.Consensus;
import java.text.DateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Vector;
import metapiga.trees.ConsensusMRE;
import java.util.concurrent.CountDownLatch;
import metapiga.trees.Tree;
import java.util.List;
import metapiga.parameters.Parameters;
import javax.swing.DefaultListModel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;

public class SearchSilent implements Runnable
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
    private SearchSilentMonitor[] monitors;
    private CountDownLatch latch;
    private ConsensusMRE consensusMRE;
    private boolean stopGrid;
    
    public SearchSilent(final DefaultListModel parameters) {
        this.allSolutionTrees = new Vector<Tree>();
        this.consensusMRE = new ConsensusMRE();
        this.stopGrid = false;
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
            if (!dir.exists() && !this.currentParameters.gridReplicate) {
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
            this.monitors = new SearchSilentMonitor[this.nJobs];
            for (int j = 0; j < this.nJobs; ++j) {
                this.monitors[j] = new SearchSilentMonitor(this, this.currentParameters, this.dirPath);
            }
            try {
                final long startTime = System.currentTimeMillis();
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
    }
    
    private void end(final long startTime) {
        Tree consensusTree = null;
        if (this.currentParameters.hasManyReplicates()) {
            try {
                final Consensus consensus = new Consensus(this.allSolutionTrees, this.currentParameters.dataset);
                consensusTree = consensus.getConsensusTree(this.currentParameters);
                if (this.currentParameters.optimization == Parameters.Optimization.CONSENSUSTREE) {
                    final Tree optimizedConsensusTree = this.currentParameters.getOptimizer(consensusTree).getOptimizedTree();
                    consensusTree.cloneWithConsensus(optimizedConsensusTree);
                }
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, Tools.getErrorPanel("Cannot build consensus tree", ex), "Consensus tree Error", 0);
                System.out.println("Cannot build consensus tree : " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        if (!this.currentParameters.gridReplicate) {
            if (consensusTree != null) {
                consensusTree.setName(String.valueOf(this.runLabel) + " - " + consensusTree.getName());
            }
            for (final Tree t : this.allSolutionTrees) {
                t.setName(String.valueOf(this.runLabel) + " - " + t.getName());
            }
        }
        if (!this.currentParameters.gridReplicate) {
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
            }
        }
        else {
            final File output = new File(this.currentParameters.cloudOutput);
            try {
                final FileWriter fw = new FileWriter(output);
                final BufferedWriter bw = new BufferedWriter(fw);
                bw.write("#NEXUS");
                bw.newLine();
                bw.newLine();
                bw.write("Begin trees;  [Result trees]");
                bw.newLine();
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
            }
        }
    }
    
    public void addSolutionTree(final List<Tree> trees) {
        this.allSolutionTrees.addAll(trees);
        this.showReplicate();
    }
    
    public void endFromException(final Exception e) {
        e.printStackTrace();
    }
    
    public int getNextReplicate() {
        if (this.replicatesAvailable.get() < this.replicatesTotal.get()) {
            return this.replicatesAvailable.incrementAndGet();
        }
        return -1;
    }
    
    public double[] getBestSolutions() {
        final double[] bestSolutions = new double[this.monitors.length];
        for (int i = 0; i < this.monitors.length; ++i) {
            bestSolutions[i] = this.monitors[i].getBestLikelihood();
        }
        return bestSolutions;
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
    

    static /* synthetic */ void access$4(final SearchSilent searchSilent, final boolean stopGrid) {
        searchSilent.stopGrid = stopGrid;
    }
}

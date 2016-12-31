// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.monitors;

import metapiga.trees.Node;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import metapiga.utilities.Tools;
import metapiga.trees.Consensus;
import java.util.Iterator;
import metapiga.trees.Tree;
import java.util.List;
import metapiga.modelization.DistanceMatrix;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import java.io.Writer;
import javax.swing.text.DefaultEditorKit;
import java.io.IOException;
import java.util.Date;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import metapiga.parameters.Parameters;

public class PrintMonitor
{
    public static final String tab = "\t";
    public static final String endl = "\n";
    private Monitor monitor;
    Parameters parameters;
    String dirPath;
    boolean DATAwritten;
    boolean DISTwritten;
    FileWriter treeOpWriter;
    FileWriter statOpWriter;
    FileWriter searchLogWriter;
    FileWriter searchTreeWriter;
    FileWriter consensusWriter;
    FileWriter ancseqWriter;
    FileWriter perfWriter;
    private Map<String, Long> performanceTracking;
    private Map<String, String> operatorDetailsTracking;
    private Map<Integer, String> cpDetailsTracking;
    
    public PrintMonitor(final Monitor monitor, final String dirPath) {
        this.DATAwritten = false;
        this.DISTwritten = false;
        this.monitor = monitor;
        this.dirPath = dirPath;
    }
    
    public void setParameters(final Parameters parameters) {
        this.parameters = parameters;
        if (this.monitor.trackPerformances()) {
            this.performanceTracking = new HashMap<String, Long>();
        }
        if (this.monitor.trackOperators()) {
            this.operatorDetailsTracking = new HashMap<String, String>();
        }
        if (this.monitor.trackHeuristic()) {
            this.cpDetailsTracking = new HashMap<Integer, String>();
        }
    }
    
    public void initLogFiles(final int currentReplicate) {
        if (!this.parameters.logFiles.isEmpty()) {
            File dir = new File(this.dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (this.parameters.hasManyReplicates()) {
                dir = new File(String.valueOf(this.dirPath) + "/" + "Replicate " + currentReplicate);
                dir.mkdir();
            }
            try {
                if (this.parameters.logFiles.contains(Parameters.LogFile.OPDETAILS)) {
                    if (this.treeOpWriter != null) {
                        this.treeOpWriter.close();
                    }
                    this.treeOpWriter = new FileWriter(this.createOutputFile(Parameters.LogFile.OPDETAILS, currentReplicate));
                }
                if (this.parameters.logFiles.contains(Parameters.LogFile.OPSTATS)) {
                    if (this.statOpWriter != null) {
                        this.statOpWriter.close();
                    }
                    this.statOpWriter = new FileWriter(this.createOutputFile(Parameters.LogFile.OPSTATS, currentReplicate));
                }
                if (this.parameters.logFiles.contains(Parameters.LogFile.HEUR)) {
                    if (this.searchLogWriter != null) {
                        this.searchLogWriter.close();
                    }
                    this.searchLogWriter = new FileWriter(this.createOutputFile(Parameters.LogFile.HEUR, currentReplicate));
                    switch (this.parameters.heuristic) {
                        case HC: {
                            this.searchLogWriter.write("Step\tBest likelihood\tCurrent likelihood\tOperator\tImprovement\n");
                            break;
                        }
                        case SA: {
                            this.searchLogWriter.write("Step\tBest likelihood\tS0 likelihood\tCurrent likelihood\tOperator\tStatus\tTemperature acceptance\tTemperature\tSteps with same T°\tSuccesses\tFailures\tNbr of decrements without reheating\n");
                            break;
                        }
                        case GA: {
                            this.searchLogWriter.write("Step\t");
                            for (int i = 0; i < this.parameters.gaIndNum; ++i) {
                                this.searchLogWriter.write("Mutated individual " + i + "\t" + "Operator used on ind " + i + "\t");
                            }
                            this.searchLogWriter.write("Selection details\t");
                            for (int i = 0; i < this.parameters.gaIndNum; ++i) {
                                this.searchLogWriter.write("Selected individual " + i + "\t");
                            }
                            this.searchLogWriter.write("Best likelihood\n");
                            break;
                        }
                        case CP: {
                            this.searchLogWriter.write("Step\t");
                            for (int p = 0; p < this.parameters.cpPopNum; ++p) {
                                for (int j = 0; j < this.parameters.cpIndNum; ++j) {
                                    this.searchLogWriter.write("Mutated ind " + j + " of pop " + p + "\t" + "Operator used on ind " + j + " of pop " + p + "\t");
                                }
                            }
                            for (int p = 0; p < this.parameters.cpPopNum; ++p) {
                                this.searchLogWriter.write("Selection details on pop " + p + "\t");
                                for (int j = 0; j < this.parameters.cpIndNum; ++j) {
                                    this.searchLogWriter.write("Selected ind " + j + " of pop " + p + "\t");
                                }
                            }
                            this.searchLogWriter.write("Best likelihood\n");
                            break;
                        }
                    }
                }
                if (this.parameters.logFiles.contains(Parameters.LogFile.TREEHEUR)) {
                    if (this.searchTreeWriter != null) {
                        this.searchTreeWriter.close();
                    }
                    (this.searchTreeWriter = new FileWriter(this.createOutputFile(Parameters.LogFile.TREEHEUR, currentReplicate))).write("#NEXUS\n");
                    this.searchTreeWriter.write("\n");
                    this.searchTreeWriter.write("Begin trees;  [" + this.parameters.heuristic.name() + " started " + new Date(System.currentTimeMillis()).toString() + "]" + "\n");
                }
                if (this.parameters.logFiles.contains(Parameters.LogFile.CONSENSUS)) {
                    if (this.consensusWriter != null) {
                        this.consensusWriter.close();
                    }
                    this.consensusWriter = new FileWriter(this.createOutputFile(Parameters.LogFile.CONSENSUS, currentReplicate));
                }
                if (this.parameters.logFiles.contains(Parameters.LogFile.PERF)) {
                    if (this.perfWriter != null) {
                        this.perfWriter.close();
                    }
                    (this.perfWriter = new FileWriter(this.createOutputFile(Parameters.LogFile.PERF, currentReplicate))).write("Performances (expressed in nanoseconds)\n\n");
                }
                if (this.parameters.logFiles.contains(Parameters.LogFile.ANCSEQ)) {
                    if (this.ancseqWriter != null) {
                        this.ancseqWriter.close();
                    }
                    this.ancseqWriter = new FileWriter(this.createOutputFile(Parameters.LogFile.ANCSEQ, currentReplicate));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                this.monitor.showText("\n Error when creating log files for replicate " + currentReplicate);
                this.monitor.showText("\n Java exception : " + ex.getCause() + " (" + ex.getMessage() + ")");
                StackTraceElement[] stackTrace;
                for (int length = (stackTrace = ex.getStackTrace()).length, k = 0; k < length; ++k) {
                    final StackTraceElement el = stackTrace[k];
                    this.monitor.showText("\tat " + el.toString());
                }
            }
        }
    }
    
    private File createOutputFile(final Parameters.LogFile logFile, final int currentReplicate) {
        String name = "";
        switch (logFile) {
            case OPDETAILS: {
                name = "OperatorsDetails.log";
                break;
            }
            case OPSTATS: {
                name = "OperatorsStatistics.log";
                break;
            }
            case HEUR: {
                name = "Heuristic.log";
                break;
            }
            case TREEHEUR: {
                name = "Heuristic.tre";
                break;
            }
            case CONSENSUS: {
                name = "Consensus.log";
                break;
            }
            case PERF: {
                name = "Performances.log";
                break;
            }
            case ANCSEQ: {
                name = "AncestralSequences.log";
                break;
            }
        }
        File output;
        if (this.parameters.hasManyReplicates()) {
            output = new File(String.valueOf(this.dirPath) + "/" + "Replicate " + currentReplicate + "/" + name);
        }
        else {
            output = new File(String.valueOf(this.dirPath) + "/" + name);
        }
        return output;
    }
    
    public void closeOutputFiles() throws IOException {
        if (this.treeOpWriter != null) {
            this.treeOpWriter.close();
        }
        if (this.statOpWriter != null) {
            this.statOpWriter.close();
        }
        if (this.searchLogWriter != null) {
            this.searchLogWriter.close();
        }
        if (this.searchTreeWriter != null) {
            this.searchTreeWriter.close();
        }
        if (this.consensusWriter != null) {
            this.consensusWriter.close();
        }
        if (this.perfWriter != null) {
            this.perfWriter.close();
        }
        if (this.ancseqWriter != null) {
            this.ancseqWriter.close();
        }
    }
    
    public void printDataMatrix() {
        if (!this.DATAwritten) {
            try {
                File output = new File(this.dirPath);
                if (!output.exists()) {
                    output.mkdir();
                }
                output = new File(String.valueOf(this.dirPath) + "/" + "Dataset.log");
                final FileWriter fw = new FileWriter(output);
                final DefaultEditorKit kit = new DefaultEditorKit();
                final DefaultStyledDocument doc = this.parameters.showDataset();
                kit.write(fw, doc, 0, doc.getLength());
                fw.close();
                this.DATAwritten = true;
            }
            catch (Exception e) {
                e.printStackTrace();
                this.monitor.showText("\n Error when writing file Dataset.log");
                this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
                StackTraceElement[] stackTrace;
                for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                    final StackTraceElement el = stackTrace[i];
                    this.monitor.showText("\tat " + el.toString());
                }
            }
        }
    }
    
    public void printDistanceMatrix(final DistanceMatrix dm) {
        if (!this.DISTwritten) {
            try {
                final File dir = new File(this.dirPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                final File output = new File(String.valueOf(this.dirPath) + "/" + "Distances.log");
                final FileWriter fw = new FileWriter(output);
                final DefaultEditorKit kit = new DefaultEditorKit();
                final DefaultStyledDocument doc = dm.show();
                kit.write(fw, doc, 0, doc.getLength());
                fw.close();
                this.DISTwritten = true;
            }
            catch (Exception e) {
                e.printStackTrace();
                this.monitor.showText("\n Error when writing file Distances.log");
                this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
                StackTraceElement[] stackTrace;
                for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                    final StackTraceElement el = stackTrace[i];
                    this.monitor.showText("\tat " + el.toString());
                }
            }
        }
    }
    
    public void printStartingTrees(final List<Tree> startingTrees, final int currentReplicate) {
        try {
            File output = new File(this.dirPath);
            if (!output.exists()) {
                output.mkdir();
            }
            if (this.parameters.hasManyReplicates()) {
                output = new File(String.valueOf(this.dirPath) + "/" + "Replicate " + currentReplicate);
                if (!output.exists()) {
                    output.mkdir();
                }
                output = new File(String.valueOf(this.dirPath) + "/" + "Replicate " + currentReplicate + "/" + "StartingTrees.tre");
            }
            else {
                output = new File(String.valueOf(this.dirPath) + "/" + "StartingTrees.tre");
            }
            final FileWriter fw = new FileWriter(output);
            fw.write("#NEXUS\n");
            fw.write("\n");
            fw.write("Begin trees;  [Treefile created " + new Date(System.currentTimeMillis()).toString() + "]" + "\n");
            for (final Tree tree : startingTrees) {
                fw.write(String.valueOf(tree.toNewickLine(false, false)) + "\n");
            }
            fw.write("End;\n");
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing file StartingTrees.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public synchronized void printTreeBeforeOperator(final Tree tree, final Parameters.Operator operator, final boolean consensus) {
        try {
            final StringBuilder s = new StringBuilder();
            s.append("----------------------------------------------------------\n");
            s.append(String.valueOf(tree.getName()) + "\n");
            s.append("Tree before operator " + operator + (consensus ? " respecting consensus" : " without consensus") + " :" + "\n");
            s.append(String.valueOf(tree.toNewickLineWithML(new StringBuilder(String.valueOf(tree.getName().replace(this.parameters.heuristic.verbose(), this.parameters.heuristic.toString()).replace("population", "pop").replace("individual", "ind"))).append(" bef").append(operator).toString(), true, false)) + "\n");
            this.operatorDetailsTracking.put(tree.getName(), s.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file OperatorsDetails.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public synchronized void printOperatorInfos(final Tree tree, final String infos) {
        final String key = tree.getName();
        final String value = this.operatorDetailsTracking.get(key);
        this.operatorDetailsTracking.put(key, String.valueOf(value) + infos + "\n");
    }
    
    public synchronized void printOperatorInfos(final Tree tree, final String infos, final Consensus consensus) {
        final String key = tree.getName();
        String value = this.operatorDetailsTracking.get(key);
        value = String.valueOf(value) + infos + "\n";
        value = String.valueOf(value) + "Consensus :\n" + consensus.showConsensus() + "\n";
        this.operatorDetailsTracking.put(key, value);
    }
    
    public synchronized void printTreeAfterOperator(final Tree tree, final Parameters.Operator operator, final boolean consensus) {
        try {
            this.treeOpWriter.write(this.operatorDetailsTracking.remove(tree.getName()));
            this.treeOpWriter.write("Tree after operator " + operator + (consensus ? " respecting consensus" : " without consensus") + " :" + "\n");
            this.treeOpWriter.write(String.valueOf(tree.toNewickLineWithML(new StringBuilder(String.valueOf(tree.getName().replace(this.parameters.heuristic.verbose(), this.parameters.heuristic.toString()).replace("population", "pop").replace("individual", "ind"))).append(" aft").append(operator).toString(), true, false)) + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file OperatorsDetails.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printOperatorFrequenciesUpdate(final int currentStep, final Map<Parameters.Operator, Integer> use, final Map<Parameters.Operator, Double> scoreImprovements, final Map<Parameters.Operator, Long> performances) {
        try {
            this.statOpWriter.write("Likelihood improvements at step " + currentStep + " : " + "\n");
            this.statOpWriter.write("-------------------------------------\n");
            this.statOpWriter.write("Operator\tnbrUse\timprove ML\timprove rescaled" + (this.monitor.trackPerformances() ? "\tmean execution time (nanosec)" : "") + "\n" + "\n");
            for (final Parameters.Operator op : use.keySet()) {
                this.statOpWriter.write(op + "\t" + use.get(op) + "\t" + Tools.doubletoString(scoreImprovements.get(op), 4) + "\t" + Tools.doubletoString(scoreImprovements.get(op) / use.get(op), 4) + (this.monitor.trackPerformances() ? ("\t" + Math.round(performances.get(op) / use.get(op))) : "") + "\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file OperatorsStatistics.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printOperatorFrequenciesUpdate(final Map<Parameters.Operator, Double> frequencies) {
        try {
            this.statOpWriter.write("New Frequencies : \n");
            this.statOpWriter.write("------------------\n");
            for (final Map.Entry<Parameters.Operator, Double> e : frequencies.entrySet()) {
                this.statOpWriter.write(e.getKey() + " : " + Tools.doubletoString(e.getValue() * 100.0, 2) + "%" + "\n");
            }
            this.statOpWriter.write("----------------------------------------------------------\n");
        }
        catch (Exception e2) {
            e2.printStackTrace();
            this.monitor.showText("\n Error when writing in file OperatorsStatistics.log");
            this.monitor.showText("\n Java exception : " + e2.getCause() + " (" + e2.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e2.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printOperatorStatistics(final int numStep, final Map<Parameters.Operator, Integer> use, final Map<Parameters.Operator, Double> scoreImprovements, final Map<Parameters.Operator, Long> performances, final int outgroupTargeted, final int ingroupTargeted, final Map<Parameters.Operator, Map<Integer, Integer>> cancelByConsensus) {
        try {
            this.statOpWriter.write("Likelihood improvements at end (" + numStep + " steps) : " + "\n");
            this.statOpWriter.write("---------------------------------------------\n");
            this.statOpWriter.write("Operator\tnbrUse\timprove ML\timprove rescaled" + (this.monitor.trackPerformances() ? "\tmean execution time (nanosec)" : "") + "\n" + "\n");
            for (final Parameters.Operator op : use.keySet()) {
                this.statOpWriter.write(op + "\t" + use.get(op) + "\t" + Tools.doubletoString(scoreImprovements.get(op), 4) + "\t" + Tools.doubletoString(scoreImprovements.get(op) / use.get(op), 4) + (this.monitor.trackPerformances() ? ("\t" + Math.round(performances.get(op) / use.get(op))) : "") + "\t" + "\n");
            }
            this.statOpWriter.write("\n");
            this.statOpWriter.write("Number of times ingroup was targeted by an operator : " + ingroupTargeted + "\n");
            this.statOpWriter.write("Number of times outgroup was targeted by an operator : " + outgroupTargeted + "\n");
            this.statOpWriter.write("\n");
            this.statOpWriter.write("Number of mutations cancelled by consensus : \n");
            this.statOpWriter.write("---------------------------------------------\n");
            this.statOpWriter.write("Operator : \ttotal");
            Set<Integer> todo = new HashSet<Integer>();
            for (final Map<Integer, Integer> map : cancelByConsensus.values()) {
                todo.addAll(map.keySet());
            }
            int i = 0;
            while (!todo.isEmpty()) {
                todo.remove(i);
                this.statOpWriter.write("\t" + i * 100 + "-" + ((i + 1) * 100 - 1));
                ++i;
            }
            this.statOpWriter.write("\n");
            for (final Parameters.Operator operator : cancelByConsensus.keySet()) {
                this.statOpWriter.write(operator + " : " + "\t");
                int sum = 0;
                for (final int c : cancelByConsensus.get(operator).values()) {
                    sum += c;
                }
                this.statOpWriter.write("\t" + sum);
                todo = new HashSet<Integer>(cancelByConsensus.get(operator).keySet());
                int j = 0;
                while (!todo.isEmpty()) {
                    if (todo.remove(j)) {
                        this.statOpWriter.write("\t" + cancelByConsensus.get(operator).get(j));
                    }
                    else {
                        this.statOpWriter.write("\t0");
                    }
                    ++j;
                }
                this.statOpWriter.write("\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file OperatorsStatistics.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, k = 0; k < length; ++k) {
                final StackTraceElement el = stackTrace[k];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printDetailsHC(final int step, final double bestLikelihood, final double currentLikelihood, final Parameters.Operator operator, final boolean improvement) {
        try {
            this.searchLogWriter.write(String.valueOf(step) + "\t" + Tools.doubletoString(bestLikelihood, 4) + "\t" + Tools.doubletoString(currentLikelihood, 4) + "\t" + operator + "\t" + (improvement ? "YES" : "NO") + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printDetailsSA(final int step, final double bestLikelihood, final double S0Likelihood, final double currentLikelihood, final Parameters.Operator operator, final String status, final double tempAcceptance, final double temperature, final int coolingSteps, final int successes, final int failures, final int reheatingDecrements) {
        try {
            this.searchLogWriter.write(String.valueOf(step) + "\t" + Tools.doubletoString(bestLikelihood, 4) + "\t" + Tools.doubletoString(S0Likelihood, 4) + "\t" + Tools.doubletoString(currentLikelihood, 4) + "\t" + operator + "\t" + status + "\t" + Tools.doubletoString(tempAcceptance * 100.0, 2) + "%" + "\t" + Tools.doubletoString(temperature, 4) + "\t" + coolingSteps + "\t" + successes + "\t" + failures + "\t" + reheatingDecrements + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printDetailsGA(final int step, final Tree[] mutantML, final Parameters.Operator[] operator) {
        try {
            this.searchLogWriter.write(String.valueOf(step) + "\t");
            for (int i = 0; i < mutantML.length; ++i) {
                this.searchLogWriter.write(String.valueOf(Tools.doubletoString(mutantML[i].getEvaluation(), 4)) + "\t" + ((operator[i] == null) ? "No mutation" : operator[i]) + "\t");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printDetailsGA(final int step, final String selectionDetails, final Tree[] selectedML, final double bestLikelihood) {
        try {
            this.searchLogWriter.write(String.valueOf(selectionDetails) + "\t");
            for (int i = 0; i < selectedML.length; ++i) {
                this.searchLogWriter.write(String.valueOf(Tools.doubletoString(selectedML[i].getEvaluation(), 4)) + "\t");
            }
            this.searchLogWriter.write(String.valueOf(Tools.doubletoString(bestLikelihood, 4)) + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public synchronized void printDetailsCP(final Tree[] mutantML, final Parameters.Operator[] operator, final int currentPop) {
        try {
            final StringBuilder s = new StringBuilder();
            for (int i = 0; i < mutantML.length; ++i) {
                s.append(String.valueOf(Tools.doubletoString(mutantML[i].getEvaluation(), 4)) + "\t" + ((operator[i] == null) ? "No mutation" : operator[i]) + "\t");
            }
            this.cpDetailsTracking.put(currentPop, s.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printDetailsCP(final Tree[] hybridML, final String[] parents, final int currentPop) {
        try {
            final StringBuilder s = new StringBuilder();
            for (int i = 0; i < hybridML.length; ++i) {
                s.append(String.valueOf(Tools.doubletoString(hybridML[i].getEvaluation(), 4)) + "\t" + ((parents[i] == null) ? "No recombination" : parents[i]) + "\t");
            }
            this.cpDetailsTracking.put(currentPop, s.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printDetailsCP(final int step, final String[] selectionDetails, final Tree[][] selectedML, final double bestLikelihood) {
        try {
            this.searchLogWriter.write(String.valueOf(step) + "\t");
            for (int p = 0; p < selectionDetails.length; ++p) {
                this.searchLogWriter.write(this.cpDetailsTracking.remove(p));
            }
            for (int p = 0; p < selectionDetails.length; ++p) {
                this.searchLogWriter.write(String.valueOf(selectionDetails[p]) + "\t");
                for (int i = 0; i < selectedML[p].length; ++i) {
                    this.searchLogWriter.write(String.valueOf(Tools.doubletoString(selectedML[p][i].getEvaluation(), 4)) + "\t");
                }
            }
            this.searchLogWriter.write(String.valueOf(Tools.doubletoString(bestLikelihood, 4)) + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printTreesHC(final int step, final Tree bestTree, final Tree currentTree) {
        try {
            this.searchTreeWriter.write(String.valueOf(bestTree.toNewickLineWithML(new StringBuilder("HC_best_step").append(step).toString(), false, true)) + "\n");
            this.searchTreeWriter.write(String.valueOf(currentTree.toNewickLineWithML(new StringBuilder("HC_current_step").append(step).toString(), false, true)) + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printTreesSA(final int step, final Tree bestTree, final Tree S0Tree, final Tree currentTree) {
        try {
            this.searchTreeWriter.write(String.valueOf(bestTree.toNewickLineWithML(new StringBuilder("SA_best_step").append(step).toString(), false, true)) + "\n");
            this.searchTreeWriter.write(String.valueOf(currentTree.toNewickLineWithML(new StringBuilder("SA_S0_step").append(step).toString(), false, true)) + "\n");
            this.searchTreeWriter.write(String.valueOf(currentTree.toNewickLineWithML(new StringBuilder("SA_current_step").append(step).toString(), false, true)) + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printTreesGA(final int step, final Tree[] trees, final boolean selectionDone) {
        try {
            final String type = selectionDone ? "_selected" : "_mutant";
            for (int i = 0; i < trees.length; ++i) {
                this.searchTreeWriter.write(String.valueOf(trees[i].toNewickLineWithML(new StringBuilder("GA_step").append(step).append(type).append(i).toString(), false, true)) + "\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public synchronized void printTreesCP(final int step, final Tree[] trees, final int pop, final boolean recombined) {
        try {
            for (int i = 0; i < trees.length; ++i) {
                this.searchTreeWriter.write(String.valueOf(trees[i].toNewickLineWithML(new StringBuilder("CP_step").append(step).append("_pop").append(pop).append("_").append(recombined ? "hybrid" : "mutant").append(i).toString(), false, true)) + "\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printTreesCP(final int step, final Tree[][] trees) {
        try {
            for (int p = 0; p < trees.length; ++p) {
                for (int i = 0; i < trees[p].length; ++i) {
                    this.searchTreeWriter.write(String.valueOf(trees[p][i].toNewickLineWithML(new StringBuilder("CP_step").append(step).append("_pop").append(p).append("_selected").append(i).toString(), false, true)) + "\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, j = 0; j < length; ++j) {
                final StackTraceElement el = stackTrace[j];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printEndTreesHeuristic() {
        try {
            this.searchTreeWriter.write("End;  [Heuristic ended " + new Date(System.currentTimeMillis()).toString() + "]" + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Heuristic.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void printConsensus(final int step, final Consensus consensus) {
        try {
            this.consensusWriter.write("Step " + step + " consensus partitions : " + "\n");
            this.consensusWriter.write(String.valueOf(consensus.showConsensus()) + "\n");
            this.consensusWriter.write("----------------------------------------------------------\n");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file Consensus.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public synchronized void trackPerformances(final String action, final int level) {
        if (this.performanceTracking.containsKey(action)) {
            try {
                switch (level) {
                    case 0: {
                        this.perfWriter.write(String.valueOf(action) + "\t" + "\t" + "\t" + (System.nanoTime() - this.performanceTracking.remove(action)) + "\n");
                        break;
                    }
                    case 1: {
                        this.perfWriter.write("\t" + action + "\t" + (System.nanoTime() - this.performanceTracking.remove(action)) + "\t" + "\n");
                        break;
                    }
                    default: {
                        this.monitor.showText("Performance of level " + level + " is not supported (action = " + action + ").");
                        break;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                this.monitor.showText("\n Error when writing in file Performances.log");
                this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
                StackTraceElement[] stackTrace;
                for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                    final StackTraceElement el = stackTrace[i];
                    this.monitor.showText("\tat " + el.toString());
                }
            }
        }
        else {
            this.performanceTracking.put(action, System.nanoTime());
        }
    }
    
    public void printAncestralSequences(final List<Tree> trees) {
        try {
            for (final Tree tree : trees) {
                this.ancseqWriter.write("Ancestral sequences reconstruction for tree '" + tree.getName() + "':" + "\n");
                this.ancseqWriter.write("----------------------------------------------------------\n");
                this.ancseqWriter.write("Tree in Newick format with internal nodes labels : \n");
                this.ancseqWriter.write(String.valueOf(tree.toNewickLine(true, false)) + "\n" + "\n");
                for (final Node node : tree.getInodes()) {
                    final DefaultEditorKit kit = new DefaultEditorKit();
                    final DefaultStyledDocument doc = tree.printAncestralStates(node);
                    kit.write(this.ancseqWriter, doc, 0, doc.getLength());
                    this.ancseqWriter.write("\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing in file AncestralSequences.log");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
    
    public void updateConsensusTree(final Tree consensusTree) {
        try {
            File output = new File(this.dirPath);
            if (!output.exists()) {
                output.mkdir();
            }
            output = new File(String.valueOf(this.dirPath) + "/" + "ConsensusTree.tre");
            final FileWriter fw = new FileWriter(output);
            fw.write("#NEXUS\n");
            fw.write("\n");
            fw.write("Begin trees;  [Treefile created " + new Date(System.currentTimeMillis()).toString() + "]" + "\n");
            fw.write(String.valueOf(consensusTree.toNewickLine(false, true)) + "\n");
            fw.write("End;\n");
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            this.monitor.showText("\n Error when writing file ConsensusTree.tre");
            this.monitor.showText("\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")");
            StackTraceElement[] stackTrace;
            for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
                final StackTraceElement el = stackTrace[i];
                this.monitor.showText("\tat " + el.toString());
            }
        }
    }
}

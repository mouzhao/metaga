// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.parameters;

import metapiga.modelization.data.codons.tables.EuploidNuclearCode;
import metapiga.modelization.data.codons.tables.EchinodermFlatwormMitochCode;
import metapiga.modelization.data.codons.tables.CDHNuclearCode;
import metapiga.modelization.data.codons.tables.InvertebrateMitochondrialCode;
import metapiga.modelization.data.codons.tables.MoldProtoCoelMitochCode;
import metapiga.modelization.data.codons.tables.VertebrateMitochondrialCode;
import metapiga.utilities.CudaTools;
import metapiga.trees.exceptions.BranchNotFoundException;
import metapiga.optimization.GA;
import metapiga.optimization.Powell;
import metapiga.optimization.DFO;
import metapiga.optimization.Optimizer;
import java.util.HashMap;
import java.io.IOException;
import org.biojavax.bio.phylo.io.nexus.NexusFileFormat;
import java.io.Writer;
import javax.swing.text.BadLocationException;
import metapiga.ProgressHandling;
import java.util.BitSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.util.Hashtable;
import metapiga.MetaPIGA;
import javax.swing.text.DefaultStyledDocument;
import metapiga.modelization.data.DataType;
import metapiga.modelization.data.Codon;
import metapiga.modelization.data.Data;
import metapiga.modelization.data.codons.tables.CodonTransitionTableFactory;
import metapiga.exceptions.IncompatibleDataException;
import metapiga.exceptions.UnknownDataException;
import metapiga.exceptions.NexusInconsistencyException;
import metapiga.exceptions.CharsetIntersectionException;
import metapiga.io.ParseTreeException;
import metapiga.RateParameter;

import javax.swing.JOptionPane;
import metapiga.io.NewickReader;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.NullAncestorException;
import org.biojavax.bio.phylo.io.nexus.DataBlock;
import metapiga.modelization.data.EmpiricalModels;
import metapiga.trees.exceptions.UnknownTaxonException;
import metapiga.trees.exceptions.TooManyNeighborsException;
import metapiga.trees.exceptions.UncompatibleOutgroupException;
import metapiga.exceptions.OutgroupTooBigException;
import metapiga.monitors.InactiveMonitor;
import java.util.Iterator;
import java.util.Collection;
import org.biojavax.bio.phylo.io.nexus.MetapigaBlock;
import metapiga.utilities.Tools;
import metapiga.modelization.data.codons.tables.UniversalCodonTransitionTable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.TreeMap;
import java.util.EnumMap;
import metapiga.modelization.modeltest.ModelSampling;
import org.biojavax.bio.phylo.io.nexus.TreesBlock;
import metapiga.trees.Tree;
import metapiga.modelization.data.codons.tables.CodonTransitionTable;
import metapiga.modelization.CodonCharactersBlock;
import java.io.File;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import metapiga.modelization.Dataset;
import java.util.List;
import java.util.Set;
import metapiga.modelization.Charset;

import java.util.Map;
import java.awt.Color;

public class Parameters
{
    public static final String AMBIGUOUS_CODONS = "Ambiguous_codons";
    public static final String STOP_CODONS = "Stop_codons";
    public static final Color[] availableColors;
    public Heuristic heuristic;
    public int hcRestart;
    public SASchedule saSchedule;
    public double saScheduleParam;
    public double saLundyC;
    public double saLundyAlpha;
    public double saInitAccept;
    public double saFinalAccept;
    public SADeltaL saDeltaL;
    public double saDeltaLPercent;
    public SAReheating saReheatingType;
    public double saReheatingValue;
    public SACooling saCoolingType;
    public int saCoolingSteps;
    public int saCoolingSuccesses;
    public int saCoolingFailures;
    public int gaIndNum;
    public GASelection gaSelection;
    public double gaReplacementStrength;
    public double gaRecombination;
    public GAOperatorChange gaOperatorChange;
    public CPConsensus cpConsensus;
    public CPOperator cpOperator;
    public int cpPopNum;
    public int cpIndNum;
    public double cpTolerance;
    public double cpHybridization;
    public CPSelection cpSelection;
    public double cpReplacementStrength;
    public double cpRecombination;
    public CPOperatorChange cpOperatorChange;
    public int cpCoreNum;
    public EvaluationRate evaluationRate;
    public EvaluationModel evaluationModel;
    private Map<RateParameter, Double> evaluationRateParameters;
    public EvaluationStateFrequencies evaluationStateFrequencies;
    public EvaluationDistribution evaluationDistribution;
    public int evaluationDistributionSubsets;
    private double evaluationDistributionShape;
    private double evaluationPInv;
    private Map<Charset, Map<RateParameter, Double>> specificRateParameters;
    private Map<Charset, Double> specificDistributionShapes;
    private Map<Charset, Double> specificsPInvs;
    public Optimization optimization;
    public double optimizationUse;
    public OptimizationAlgorithm optimizationAlgorithm;
    public Set<OptimizationTarget> optimizationTargets;
    public StartingTreeGeneration startingTreeGeneration;
    public double startingTreeGenerationRange;
    public DistanceModel startingTreeModel;
    public StartingTreeDistribution startingTreeDistribution;
    public double startingTreeDistributionShape;
    public double startingTreePInv;
    public StartingTreePInvPi startingTreePInvPi;
    public List<Operator> operators;
    public Map<Operator, Integer> operatorsParameters;
    public Map<Operator, Double> operatorsFrequencies;
    public Set<Operator> operatorIsDynamic;
    public int dynamicInterval;
    public double dynamicMin;
    public ColumnRemoval columnRemoval;
    public String outputDir;
    public String label;
    public boolean useGrid;
    public String gridServer;
    public String gridClient;
    public String gridModule;
    public boolean useCloud;
    public String cloudServer;
    public String cloudClient;
    public String cloudModulething;
    public OperatorSelection operatorSelection;
    public Set<HeuristicStopCondition> sufficientStopConditions;
    public Set<HeuristicStopCondition> necessaryStopConditions;
    public int stopCriterionSteps;
    public double stopCriterionTime;
    public int stopCriterionAutoSteps;
    public double stopCriterionAutoThreshold;
    public double stopCriterionConsensusMRE;
    public int stopCriterionConsensusGeneration;
    public int stopCriterionConsensusInterval;
    public ReplicatesStopCondition replicatesStopCondition;
    public double replicatesMRE;
    public int replicatesNumber;
    public int replicatesMaximum;
    public int replicatesMinimum;
    public int replicatesInterval;
    public int replicatesParallel;
    public Set<String> outgroup;
    public Set<String> deletedTaxa;
    public CharsetsContainer charsets;
    public Map<Charset, Color> partitionColors;
    public Set<LogFile> logFiles;
    public boolean gridReplicate;
    public boolean cloudReplicate;
    public String cloudOutput;
    private LikelihoodCalculationType likelihoodCalculationType;
    private boolean hasGPU;
    public Dataset dataset;
    public CharactersBlock charactersBlock;
    public File nexusFile;
    private CodonCharactersBlock codonCharactersBlock;
    public CodonDomainDefinition codonDomain;
    private final String stopCodonsWarningMessage = "Your dataset, under current DNA code, contains stop codons. Characters with stop codons will be excluded.";
    private final String ambiguousCodonsWarningMessage = "Your dataset contains ambiguous codons. Characters with ambiguous codons will be excluded.";
    private CodonTransitionTable codonTransitionTable;
    private CodonTransitionTableType currentDNAtable;
    public List<Tree> startingTrees;
    public List<String> userSelectionTree;
    public Map<String, TreesBlock.NewickTreeString> loadedTrees;
    public Map<String, String> loadedTreesTranslation;
    public boolean treeLoaded;
    private Tree NJT;
    public final ModelSampling modelSampling;
    public String ptxFilePath;
    public List<Double> moweight;
    
    static {
        availableColors = new Color[] { new Color(145, 214, 134), new Color(255, 160, 122), new Color(127, 255, 212), new Color(224, 102, 255), new Color(135, 137, 211), new Color(216, 134, 134), new Color(138, 43, 226), new Color(255, 215, 0), new Color(133, 178, 209), new Color(255, 62, 150), new Color(127, 255, 0), new Color(103, 148, 255), new Color(0, 206, 209), new Color(206, 176, 134), new Color(30, 250, 100), new Color(255, 48, 48), new Color(134, 206, 189), new Color(0, 0, 255), new Color(255, 192, 203), new Color(0, 255, 255), new Color(255, 165, 0), new Color(219, 219, 112), new Color(255, 0, 255), new Color(255, 255, 0), new Color(0, 191, 255), new Color(139, 69, 19), new Color(0, 250, 154), new Color(255, 127, 0), new Color(133, 99, 99), new Color(187, 255, 255), new Color(0, 255, 0), new Color(255, 127, 36), new Color(194, 204, 134), new Color(255, 20, 147), new Color(202, 255, 112), new Color(221, 160, 221), new Color(46, 139, 87), new Color(205, 201, 165), new Color(16, 78, 139), new Color(139, 139, 0), new Color(152, 245, 255), new Color(255, 36, 0), new Color(60, 220, 220), new Color(125, 38, 205) };
    }
    
    public int calibrateLastDomainPosition(final int startPosition, final int endPosition) {
        return endPosition - this.domainSize(startPosition, endPosition) % 3;
    }
    
    private int domainSize(final int startPosition, final int endPosition) {
        return endPosition - startPosition + 1;
    }
    
    public Parameters(final String label) {
        this.evaluationRateParameters = new EnumMap<RateParameter, Double>(RateParameter.class);
        this.specificRateParameters = new TreeMap<Charset, Map<RateParameter, Double>>();
        this.specificDistributionShapes = new TreeMap<Charset, Double>();
        this.specificsPInvs = new TreeMap<Charset, Double>();
        this.optimizationTargets = EnumSet.noneOf(OptimizationTarget.class);
        this.operators = new ArrayList<Operator>();
        this.operatorsParameters = new EnumMap<Operator, Integer>(Operator.class);
        this.operatorsFrequencies = new EnumMap<Operator, Double>(Operator.class);
        this.operatorIsDynamic = EnumSet.noneOf(Operator.class);
        this.sufficientStopConditions = new HashSet<HeuristicStopCondition>();
        this.necessaryStopConditions = new HashSet<HeuristicStopCondition>();
        this.outgroup = new TreeSet<String>();
        this.deletedTaxa = new TreeSet<String>();
        this.charsets = new CharsetsContainer(this);
        this.partitionColors = new TreeMap<Charset, Color>();
        this.logFiles = EnumSet.noneOf(LogFile.class);
        this.hasGPU = false;
        this.codonCharactersBlock = null;
        this.codonDomain = null;
        this.currentDNAtable = CodonTransitionTableType.UNIVERSAL;
        this.startingTrees = new LinkedList<Tree>();
        this.userSelectionTree = new LinkedList<String>();
        this.loadedTrees = new LinkedHashMap<String, TreesBlock.NewickTreeString>();
        this.loadedTreesTranslation = new LinkedHashMap<String, String>();
        this.treeLoaded = false;
        this.NJT = null;
        this.modelSampling = new ModelSampling(this);
        this.ptxFilePath = "";
        this.label = label;
        this.setDefault();
    }
    

    
    public void resetDataset() {
        this.dataset = null;
        this.startingTrees.clear();
        this.userSelectionTree.clear();
        this.loadedTrees.clear();
        this.loadedTreesTranslation.clear();
        this.treeLoaded = false;
        this.charsets.clearAll();
        this.outgroup.clear();
        this.deletedTaxa.clear();
        this.partitionColors.clear();
        this.NJT = null;
    }
    
    public void resetAll() {
        this.dataset = null;
        this.startingTrees.clear();
        this.userSelectionTree.clear();
        this.loadedTrees.clear();
        this.loadedTreesTranslation.clear();
        this.treeLoaded = false;
        this.NJT = null;
        this.setDefault();
    }
    
    public void setDefault() {
        this.heuristic = Heuristic.CP;
        this.hcRestart = 20;
        this.saSchedule = SASchedule.LUNDY;
        this.saLundyC = 0.5;
        this.saLundyAlpha = 0.5;
        this.saInitAccept = 0.7;
        this.saFinalAccept = 0.01;
        this.saDeltaL = SADeltaL.BURNIN;
        this.saDeltaLPercent = 0.001;
        this.saReheatingType = SAReheating.DECREMENTS;
        this.saReheatingValue = 300.0;
        this.saCoolingType = SACooling.SF;
        this.saCoolingSteps = 20;
        this.saCoolingSuccesses = 10;
        this.saCoolingFailures = 100;
        this.gaIndNum = 8;
        this.gaSelection = GASelection.TOURNAMENT;
        this.gaReplacementStrength = 1.0;
        this.gaRecombination = 0.1;
        this.gaOperatorChange = GAOperatorChange.IND;
        this.cpConsensus = CPConsensus.STOCHASTIC;
        this.cpOperator = CPOperator.SUPERVISED;
        this.cpPopNum = 11;
        this.cpIndNum = 8;
        this.cpTolerance = 0.05;
        this.cpHybridization = 0.1;
        this.cpSelection = CPSelection.IMPROVE;
        this.cpReplacementStrength = 1.0;
        this.cpRecombination = 0.1;
        this.cpOperatorChange = CPOperatorChange.IND;
        this.cpCoreNum = 1;
        this.evaluationRate = EvaluationRate.TREE;
        if (this.dataset != null) {
            switch (this.dataset.getDataType()) {
                case DNA: {
                    this.evaluationModel = EvaluationModel.JC;
                    break;
                }
                case PROTEIN: {
                    this.evaluationModel = EvaluationModel.POISSON;
                    break;
                }
                case STANDARD: {
                    this.evaluationModel = EvaluationModel.GTR2;
                    break;
                }
                case CODON: {
                    this.evaluationModel = EvaluationModel.GY;
                    break;
                }
            }
        }
        else {
            this.evaluationModel = EvaluationModel.JC;
        }
        RateParameter[] values;
        for (int length = (values = RateParameter.values()).length, i = 0; i < length; ++i) {
            final RateParameter r = values[i];
            this.evaluationRateParameters.put(r, 0.5);
        }
        this.setCodonTransitionTable(new UniversalCodonTransitionTable());
        this.evaluationStateFrequencies = EvaluationStateFrequencies.EMPIRICAL;
        this.evaluationDistribution = EvaluationDistribution.NONE;
        this.evaluationDistributionSubsets = 4;
        this.evaluationDistributionShape = 1.0;
        this.evaluationPInv = 0.0;
        this.startingTreeGeneration = StartingTreeGeneration.LNJ;
        this.startingTreeGenerationRange = 0.1;
        if (this.dataset != null) {
            switch (this.dataset.getDataType()) {
                case DNA: {
                    this.startingTreeModel = DistanceModel.JC;
                    break;
                }
                case PROTEIN: {
                    this.startingTreeModel = DistanceModel.POISSON;
                    break;
                }
                case STANDARD: {
                    this.startingTreeModel = DistanceModel.GTR2;
                    break;
                }
                case CODON: {
                    this.startingTreeModel = DistanceModel.GY;
                    break;
                }
            }
        }
        else {
            this.startingTreeModel = DistanceModel.JC;
        }
        this.startingTreeDistribution = StartingTreeDistribution.NONE;
        this.startingTreeDistributionShape = 0.5;
        this.startingTreePInv = 0.0;
        this.startingTreePInvPi = StartingTreePInvPi.CONSTANT;
        this.optimization = Optimization.CONSENSUSTREE;
        this.optimizationUse = 0.0;
        this.optimizationAlgorithm = OptimizationAlgorithm.GA;
        this.optimizationTargets.clear();
        this.optimizationTargets.add(OptimizationTarget.BL);
        this.operators.clear();
        this.operatorsParameters.clear();
        this.operatorsFrequencies.clear();
        this.operatorIsDynamic.clear();
        this.operators.add(Operator.NNI);
        this.operators.add(Operator.SPR);
        this.operators.add(Operator.TBR);
        this.operators.add(Operator.TXS);
        this.operatorsParameters.put(Operator.TXS, 2);
        this.operators.add(Operator.STS);
        this.operatorsParameters.put(Operator.STS, 2);
        this.operators.add(Operator.BLM);
        this.operators.add(Operator.BLMINT);
        this.operatorSelection = OperatorSelection.RANDOM;
        this.dynamicInterval = 100;
        this.dynamicMin = 0.04;
        this.columnRemoval = ColumnRemoval.NONE;
        this.outputDir = Tools.getHomeDirectory() + "/MetaPIGA results";
        this.useGrid = false;
        this.gridServer = "";
        this.gridClient = "";
        this.gridModule = "";
        this.useCloud = false;
        this.charsets.clearAll();
        this.outgroup.clear();
        this.deletedTaxa.clear();
        this.partitionColors.clear();
        //this.sufficientStopConditions.add(HeuristicStopCondition.AUTO);
        //this.sufficientStopConditions.add(HeuristicStopCondition.CONSENSUS);
        this.sufficientStopConditions.add(HeuristicStopCondition.STEPS);
        this.stopCriterionSteps = 100;
        this.stopCriterionTime = 0.0;
        this.stopCriterionAutoSteps = 100;
        this.stopCriterionAutoThreshold = 1.0E-4;
        this.stopCriterionConsensusMRE = 0.05;
        this.stopCriterionConsensusGeneration = 5;
        this.stopCriterionConsensusInterval = 10;
        this.replicatesStopCondition = ReplicatesStopCondition.MRE;
        this.replicatesMRE = 0.05;
        this.replicatesNumber = 1;
        this.replicatesMinimum = 2;
        this.replicatesMaximum = 5;
        this.replicatesInterval = 10;
        this.replicatesParallel = 1;
        this.logFiles.clear();
        this.gridReplicate = false;
        this.cloudOutput = "";
        this.treeLoaded = false;
        this.likelihoodCalculationType = LikelihoodCalculationType.CLASSIC;
        this.currentDNAtable = CodonTransitionTableType.UNIVERSAL;
    }
    
    public void setParameters(final MetapigaBlock mp) {
        this.heuristic = mp.getHeuristic();
        this.hcRestart = mp.getHcRestart();
        this.saSchedule = mp.getSaSchedule();
        this.saScheduleParam = mp.getSaScheduleParam();
        this.saLundyC = mp.getSaLundyC();
        this.saLundyAlpha = mp.getSaLundyAlpha();
        this.saInitAccept = mp.getSaInitAccept();
        this.saFinalAccept = mp.getSaFinalAccept();
        this.saDeltaL = mp.getSaDeltaL();
        this.saDeltaLPercent = mp.getSaDeltaLPercent();
        this.saReheatingType = mp.getSaReheatingType();
        this.saReheatingValue = mp.getSaReheatingValue();
        this.saCoolingType = mp.getSaCoolingType();
        this.saCoolingSteps = mp.getSaCoolingSteps();
        this.saCoolingSuccesses = mp.getSaCoolingSuccesses();
        this.saCoolingFailures = mp.getSaCoolingFailures();
        this.gaIndNum = mp.getGaIndNum();
        this.gaSelection = mp.getGaSelection();
        this.gaReplacementStrength = mp.getGaReplacementStrength();
        this.gaRecombination = mp.getGaRecombination();
        this.gaOperatorChange = mp.getGaOperatorChange();
        this.cpConsensus = mp.getCpConsensus();
        this.cpOperator = mp.getCpOperator();
        this.cpPopNum = mp.getCpPopNum();
        this.cpIndNum = mp.getCpIndNum();
        this.cpTolerance = mp.getCpTolerance();
        this.cpHybridization = mp.getCpHybridization();
        this.cpSelection = mp.getCpSelection();
        this.cpReplacementStrength = mp.getCpReplacementStrength();
        this.cpRecombination = mp.getCpRecombination();
        this.cpOperatorChange = mp.getCpOperatorChange();
        this.cpCoreNum = mp.getCpCoreNum();
        this.evaluationRate = mp.getEvaluationRate();
        this.evaluationModel = mp.getEvaluationModel();
        final Map<RateParameter, Double> mpRateParams = mp.getRateParameter();
        for (final RateParameter r : mpRateParams.keySet()) {
            final Double value = mpRateParams.get(r);
            this.evaluationRateParameters.put(r, value);
        }
        this.evaluationStateFrequencies = mp.getEvaluationStateFrequencies();
        this.evaluationDistribution = mp.getEvaluationDistribution();
        this.evaluationDistributionSubsets = mp.getEvaluationDistributionSubsets();
        this.evaluationDistributionShape = mp.getEvaluationDistributionShape();
        this.evaluationPInv = mp.getEvaluationPInv();
        for (final Charset c : mp.getSpecificRateParameterCharsets()) {
            this.specificRateParameters.put(c, mp.getSpecificRateParameters(c));
        }
        this.specificDistributionShapes = mp.getSpecificDistributionShapes();
        this.specificsPInvs = mp.getSpecificPInvs();
        this.startingTreeGeneration = mp.getStartingTreeGeneration();
        this.startingTreeGenerationRange = mp.getStartingTreeGenerationRange();
        this.startingTreeModel = ((this.startingTreeGeneration == StartingTreeGeneration.RANDOM) ? DistanceModel.NONE : mp.getStartingTreeModel());
        this.startingTreeDistribution = mp.getStartingTreeDistribution();
        this.startingTreeDistributionShape = mp.getStartingTreeDistributionShape();
        this.startingTreePInv = mp.getStartingTreePInv();
        this.startingTreePInvPi = mp.getStartingTreePInvPi();
        this.optimization = mp.getOptimization();
        this.optimizationUse = mp.getOptimizationUse();
        this.optimizationAlgorithm = mp.getOptimizationAlgorithm();
        this.optimizationTargets = mp.getOptimizationTargets();
        if (this.optimization != Optimization.NEVER && this.optimizationTargets.isEmpty()) {
            this.optimizationTargets.add(OptimizationTarget.BL);
        }
        this.operators = mp.getOperators();
        this.operatorsParameters = mp.getOperatorsParameters();
        this.operatorsFrequencies = mp.getOperatorsFrequencies();
        this.operatorIsDynamic = mp.getOperatorIsDynamic();
        this.operatorSelection = mp.getOperatorSelection();
        this.dynamicInterval = mp.getDynamicInterval();
        this.dynamicMin = mp.getDynamicMin();
        this.columnRemoval = mp.getColumnRemoval();
        if (mp.getOutputDir() != null) {
            this.outputDir = mp.getOutputDir();
        }
        if (mp.getLabel() != null) {
            this.label = mp.getLabel();
        }
        this.outgroup = mp.getOutgroup();
        this.deletedTaxa = mp.getDeletedTaxa();
        for (final Charset ch : mp.getCharset()) {
            this.charsets.addCharset(ch.getLabel(), ch);
        }
        this.charsets.replaceExcludedCharsets(mp.getExcludedCharsets());
        this.charsets.replacePartitionCharsets(mp.getPartitions());
        this.assignPartitionColors();
        this.sufficientStopConditions = mp.getSufficientStopConditions();
        this.necessaryStopConditions = mp.getNecessaryStopConditions();
        this.stopCriterionSteps = mp.getStopCriterionSteps();
        this.stopCriterionTime = mp.getStopCriterionTime();
        this.stopCriterionAutoSteps = mp.getStopCriterionAutoSteps();
        this.stopCriterionAutoThreshold = mp.getStopCriterionAutoThreshold();
        this.stopCriterionConsensusMRE = mp.getStopCriterionConsensusMRE();
        this.stopCriterionConsensusGeneration = mp.getStopCriterionConsensusGeneration();
        this.stopCriterionConsensusInterval = mp.getStopCriterionConsensusInterval();
        this.replicatesStopCondition = mp.getReplicatesStopCondition();
        this.replicatesMRE = mp.getReplicatesMRE();
        this.replicatesNumber = mp.getReplicatesNumber();
        this.replicatesMinimum = mp.getReplicatesMinimum();
        this.replicatesMaximum = mp.getReplicatesMaximum();
        this.replicatesInterval = mp.getReplicatesInterval();
        this.replicatesParallel = mp.getReplicatesParallel();
        this.logFiles = mp.getLogFiles();
        this.gridReplicate = mp.getGridReplicate();
        this.cloudOutput = mp.getGridOutput();
        this.useGrid = mp.getUseGrid();
        this.gridServer = mp.getGridServer();
        this.gridClient = mp.getGridClient();
        this.gridModule = mp.getGridModule();
        this.useCloud = mp.getUseCloud();
        this.treeLoaded = false;
        this.likelihoodCalculationType = mp.getLikelihoodCalculationType();
        if (this.heuristic == Heuristic.BS) {
            this.startingTreeGeneration = StartingTreeGeneration.NJ;
        }
        if (this.sufficientStopConditions.isEmpty() && this.necessaryStopConditions.isEmpty() && this.heuristic != Heuristic.BS) {
            this.sufficientStopConditions.add(HeuristicStopCondition.AUTO);
            if (this.heuristic == Heuristic.CP) {
                this.sufficientStopConditions.add(HeuristicStopCondition.CONSENSUS);
            }
        }
        if (this.operators.isEmpty() && this.heuristic != Heuristic.BS) {
            this.operators.add(Operator.NNI);
            this.operators.add(Operator.SPR);
            this.operators.add(Operator.TBR);
            this.operators.add(Operator.TXS);
            this.operatorsParameters.put(Operator.TXS, 2);
            this.operators.add(Operator.STS);
            this.operatorsParameters.put(Operator.STS, 2);
            this.operators.add(Operator.BLM);
            this.operators.add(Operator.BLMINT);
            if (this.evaluationModel.getNumRateParameters() > 0) {
                this.operators.add(Operator.RPM);
                this.operatorsParameters.put(Operator.RPM, 1);
            }
            if (this.evaluationDistribution == EvaluationDistribution.GAMMA) {
                this.operators.add(Operator.GDM);
            }
            if (this.evaluationPInv > 0.0) {
                this.operators.add(Operator.PIM);
            }
            if (this.charsets.numPartitions() > 1) {
                this.operators.add(Operator.APRM);
            }
        }
        for (final Operator o : this.operators) {
            if (!this.operatorsFrequencies.containsKey(o)) {
                this.operatorsFrequencies.put(o, 0.0);
            }
        }
    }
    
    public Tree getNJT() throws OutgroupTooBigException, UncompatibleOutgroupException, TooManyNeighborsException, UnknownTaxonException {
        if (this.NJT == null) {
            (this.NJT = this.dataset.generateTree(new HashSet<String>(), StartingTreeGeneration.NJ, 0.1, this.startingTreeModel, this.startingTreeDistribution, this.startingTreeDistributionShape, this.startingTreePInv, this.startingTreePInvPi, this, new InactiveMonitor())).deleteLikelihoodComputation();
        }
        return this.NJT;
    }
    
    public void updateNJTParameters() {
        if (this.NJT != null) {
            this.NJT.setEvaluationParameters(this);
        }
    }
    
    public void assignPartitionColors() {
        this.partitionColors.clear();
        int i = 1;
        final Iterator<Charset> iterator = this.charsets.getPartitionIterator();
        while (iterator.hasNext()) {
            final Charset ch = iterator.next();
            if (i >= Parameters.availableColors.length) {
                i = 1;
            }
            this.partitionColors.put(ch, Parameters.availableColors[i]);
            ++i;
        }
    }
    
    public void setRateParameter(final String partition, final RateParameter r, final double value) {
        if (!partition.equals("FULL SET")) {
            try {
                final Charset c = this.dataset.getCharset(partition);
                Map<RateParameter, Double> map;
                if (this.specificRateParameters.containsKey(c)) {
                    map = this.specificRateParameters.get(c);
                }
                else {
                    map = new EnumMap<RateParameter, Double>(RateParameter.class);
                    RateParameter[] values;
                    for (int length = (values = RateParameter.values()).length, i = 0; i < length; ++i) {
                        final RateParameter rp = values[i];
                        map.put(rp, 0.5);
                    }
                }
                map.put(r, value);
                this.specificRateParameters.put(c, map);
            }
            catch (Exception e) {
                System.err.println("Unknown partition: " + partition);
            }
        }
        else {
            this.evaluationRateParameters.put(r, value);
        }
    }
    
    public void setLikelihoodCalcualtionType(final LikelihoodCalculationType type) {
        if (type == LikelihoodCalculationType.CLASSIC) {
            this.likelihoodCalculationType = type;
        }
        assert false : "Unknown calculation method";
    }
    
    public void setEmpiricalRates(final EvaluationModel model) {
        this.evaluationRateParameters.putAll((Map<? extends RateParameter, ? extends Double>) EmpiricalModels.getRateParameters(model));
        this.specificRateParameters.clear();
    }
    
    public LikelihoodCalculationType getLikelihoodCalculationType() {
        return this.likelihoodCalculationType;
    }
    
    public boolean hasGPU() {
        return this.hasGPU;
    }
    
    public Map<RateParameter, Double> getRateParameters(final Charset c) {
        if (this.specificRateParameters.containsKey(c)) {
            return this.specificRateParameters.get(c);
        }
        return this.evaluationRateParameters;
    }
    
    public void setEvaluationDistributionShape(final String partition, final double value) {
        if (!partition.equals("FULL SET")) {
            try {
                this.specificDistributionShapes.put(this.dataset.getCharset(partition), value);
            }
            catch (Exception e) {
                System.err.println("Unknown partition: " + partition);
            }
        }
        else {
            this.evaluationDistributionShape = value;
        }
    }
    
    public double getEvaluationDistributionShape(final Charset c) {
        if (this.specificDistributionShapes.containsKey(c)) {
            return this.specificDistributionShapes.get(c);
        }
        return this.evaluationDistributionShape;
    }
    
    public void setEvaluationPInv(final String partition, final double value) {
        if (!partition.equals("FULL SET")) {
            try {
                this.specificsPInvs.put(this.dataset.getCharset(partition), value);
            }
            catch (Exception e) {
                System.err.println("Unknown partition: " + partition);
            }
        }
        else {
            this.evaluationPInv = value;
        }
    }
    
    public double getEvaluationPInv(final Charset c) {
        if (this.specificsPInvs.containsKey(c)) {
            return this.specificsPInvs.get(c);
        }
        return this.evaluationPInv;
    }
    
    public boolean hasPInv() {
        if (this.charsets.numPartitions() == 0 || (this.charsets.numPartitions() == 1 && this.charsets.containsPartition("FULL SET"))) {
            return this.evaluationPInv > 0.0;
        }
        if (this.charsets.numPartitions() == this.specificsPInvs.keySet().size()) {
            for (final double pinv : this.specificsPInvs.values()) {
                if (pinv > 0.0) {
                    return true;
                }
            }
            return false;
        }
        if (this.evaluationPInv > 0.0) {
            return true;
        }
        for (final double pinv : this.specificsPInvs.values()) {
            if (pinv > 0.0) {
                return true;
            }
        }
        return false;
    }
    
    public void setParameters(final CharactersBlock cb) {
        this.charactersBlock = cb;
    }
    
    public void setParameters(final DataBlock cb) {
        this.charactersBlock = cb;
    }
    
    public void setParameters(final TreesBlock tb) {
        this.loadedTrees = (Map<String, TreesBlock.NewickTreeString>)tb.getTrees();
        this.loadedTreesTranslation = (Map<String, String>)tb.getTranslations();
        this.userSelectionTree = new ArrayList<String>(this.loadedTrees.keySet());
        this.setStartingTrees();
    }
    
    public void addStartingTrees(final TreesBlock tb) throws NullAncestorException, UnrootableTreeException {
        this.loadedTrees.putAll(tb.getTrees());
        this.loadedTreesTranslation.putAll(tb.getTranslations());
        this.userSelectionTree = new ArrayList<String>(this.loadedTrees.keySet());
        this.setStartingTrees();
    }
    
    public void addStartingTrees(final Collection<Tree> trees) throws NullAncestorException, UnrootableTreeException {
        for (final Tree tree : trees) {
            boolean treeIsCompatible = tree.getDataset() == this.dataset;
            if (!treeIsCompatible) {
                treeIsCompatible = (tree.getNumOfLeaves() == this.dataset.getNTax());
                for (final String taxon : this.dataset.getTaxa()) {
                    if (!tree.getDataset().getTaxa().contains(taxon)) {
                        treeIsCompatible = false;
                    }
                }
            }
            if (treeIsCompatible) {
                this.loadedTrees.put(tree.getName(), tree.toNewick(false, false));
            }
        }
        this.userSelectionTree = new ArrayList<String>(this.loadedTrees.keySet());
        this.setStartingTrees();
    }
    
    public void setStartingTrees() {
        this.startingTrees.clear();
        int neededTrees = 0;
        switch (this.heuristic) {
            case BS: {
                neededTrees = 0;
                break;
            }
            case HC: {
                neededTrees = Math.min(this.hcRestart + 1, this.userSelectionTree.size());
                break;
            }
            case SA: {
                neededTrees = 1;
                break;
            }
            case GA: {
                neededTrees = this.gaIndNum;
                break;
            }
            case CP: {
                neededTrees = this.cpPopNum;
                break;
            }
        }
        while (true) {
            while (neededTrees > 0 && this.userSelectionTree.size() > 0) {
                final Iterator<String> it = this.userSelectionTree.iterator();
                while (it.hasNext()) {
                    final String treeName = it.next();
                    if (neededTrees == 0) {
                        break;
                    }
                    final NewickReader nr = new NewickReader(this, treeName, this.loadedTrees.get(treeName).getTreeString(), this.loadedTreesTranslation);
                    try {
                        this.startingTrees.add(nr.parseNewick());
                        --neededTrees;
                    }
                    catch (ParseTreeException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, Tools.getErrorPanel("Error", e));
                        it.remove();
                        this.loadedTrees.remove(treeName);
                    }
                }
            }
            if (neededTrees > 0) {
                JOptionPane.showMessageDialog(null, "Cannot parse any given tree, set starting tree(s) to Loose Neighbor Joining");
                this.startingTreeGeneration = StartingTreeGeneration.LNJ;
                this.treeLoaded = false;
                continue;
            }
            else {
                this.treeLoaded = true;
                return;
            }
        }
    }
    
    public void buildDataset() throws CharsetIntersectionException, NexusInconsistencyException, UnknownDataException, IncompatibleDataException {
        this.charsets.removePartition("REMAINING");
        this.charsets.removePartition("FULL SET");
        final Iterator<Charset> iterator = this.charsets.getPartitionIterator();
        while (iterator.hasNext()) {
            final Charset c = iterator.next();
            if (c.isEmpty()) {
                if (this.charsets.contains(c.getLabel())) {
                    c.merge(this.charsets.getCharset(c.getLabel()));
                }
                else {
                    c.addRange(c.getLabel());
                    this.charsets.addCharset(c.getLabel(), c);
                }
            }
        }
        final Set<Charset> toTest = new HashSet<Charset>(this.charsets.getPartitions());
        Iterator<Charset> iterator2 = this.charsets.getPartitionIterator();
        while (iterator2.hasNext()) {
            final Charset c2 = iterator2.next();
            toTest.remove(c2);
            for (final Charset c3 : toTest) {
                if (c3.intersect(c2)) {
                    throw new CharsetIntersectionException(c3, c2, "Cannot be used as partitions.");
                }
            }
        }
        iterator2 = this.charsets.getExcludedCharsetIterator();
        while (iterator2.hasNext()) {
            final Charset c4 = iterator2.next();
            if (c4.isEmpty()) {
                if (this.charsets.contains(c4.getLabel())) {
                    c4.merge(this.charsets.getCharset(c4.getLabel()));
                }
                else {
                    c4.addRange(c4.getLabel());
                    this.charsets.addCharset(c4.getLabel(), c4);
                }
            }
        }
        this.assignPartitionColors();
        final boolean isCodons = this.areCodons();
        int dimensionsNChar = 0;
        if (!isCodons) {
            dimensionsNChar = this.charactersBlock.getDimensionsNChar();
        }
        else {
            dimensionsNChar = this.codonDomain.getDimensionsNChar();
        }
        if (this.charsets.isPartitionsEmpty()) {
            final Charset full = new Charset("FULL SET");
            full.addRange(1, dimensionsNChar);
            this.charsets.partitionCharset(full);
            this.partitionColors.put(full, Parameters.availableColors[0]);
        }
        else {
            final Charset remainingCharacters = new Charset("REMAINING");
            for (int i = 1; i <= dimensionsNChar; ++i) {
                boolean isInChar = false;
                final Iterator<Charset> iterator3 = this.charsets.getPartitionIterator();
                while (iterator3.hasNext()) {
                    final Charset c5 = iterator3.next();
                    if (c5.isInCharset(i)) {
                        isInChar = true;
                        break;
                    }
                }
                if (!isInChar) {
                    final int start = i;
                    for (isInChar = false; i <= dimensionsNChar && !isInChar; isInChar = true) {
                        ++i;
                        final Iterator<Charset> iterator4 = this.charsets.getPartitionIterator();
                        while (iterator4.hasNext()) {
                            final Charset c6 = iterator4.next();
                            if (c6.isInCharset(i)) {
                                break;
                            }
                        }
                    }
                    remainingCharacters.addRange(start, i - 1);
                }
            }
            if (!remainingCharacters.isEmpty()) {
                this.charsets.partitionCharset(remainingCharacters);
                this.charsets.addCharset(remainingCharacters.getLabel(), remainingCharacters);
                this.partitionColors.put(remainingCharacters, Parameters.availableColors[0]);
            }
        }
        if (isCodons) {
            this.codonCharactersBlock = new CodonCharactersBlock(this.charactersBlock, this.codonDomain);
            final Set<Charset> poorCodons = this.findStopAndAmbiguousCodons(this.codonCharactersBlock.getDataMatrix(), this.getCodonTransitionTable(), this.deletedTaxa);
            for (final Charset poorCharsetCods : poorCodons) {
                this.charsets.addCharset(poorCharsetCods.getLabel(), poorCharsetCods);
                this.charsets.excludeCharset(poorCharsetCods);
            }
            this.dataset = new Dataset(this.codonCharactersBlock, this.deletedTaxa, this.charsets.getExcludedCharsets(), this.charsets.getPartitions(), this.columnRemoval, this.getCodonTransitionTable());
        }
        else {
            this.dataset = new Dataset(this.charactersBlock, this.deletedTaxa, this.charsets.getExcludedCharsets(), this.charsets.getPartitions(), this.columnRemoval);
        }
    }
    
    public boolean defineTransitionCodonTable(final CodonTransitionTableType tabType) {
        if (this.currentDNAtable != tabType) {
            this.currentDNAtable = tabType;
            this.setCodonTransitionTable(CodonTransitionTableFactory.getInstance(tabType));
            return true;
        }
        return false;
    }
    
    private Set<Charset> findStopAndAmbiguousCodons(final Map<String, List<Data>> matrix, final CodonTransitionTable transTable, final Set<String> deletedTaxa) {
        final Charset stopCodonsSet = new Charset("Stop_codons");
        stopCodonsSet.setAsNonRecordable();
        final Charset ambiguousCodonsSet = new Charset("Ambiguous_codons");
        ambiguousCodonsSet.setAsNonRecordable();
        boolean isWarnedStopCods = false;
        boolean isWarnedAmbigCods = false;
        for (final Map.Entry<String, List<Data>> entry : matrix.entrySet()) {
            final String taxa = entry.getKey();
            if (!deletedTaxa.contains(taxa)) {
                for (int i = 0; i < entry.getValue().size(); ++i) {
                    final Codon codon = (Codon)entry.getValue().get(i);
                    try {
                        if (DataType.CODON.getUndeterminateData() == codon) {
                            ambiguousCodonsSet.addRange(new StringBuilder().append(i + 1).toString());
                            if (!isWarnedStopCods) {
                                Tools.showWarningMessage(null, "Your dataset contains ambiguous codons. Characters with ambiguous codons will be excluded.", "Warning");
                                isWarnedStopCods = true;
                            }
                        }
                        else if (transTable.isStopCodon(codon)) {
                            stopCodonsSet.addRange(new StringBuilder().append(i + 1).toString());
                            if (!isWarnedAmbigCods) {
                                Tools.showWarningMessage(null, "Your dataset, under current DNA code, contains stop codons. Characters with stop codons will be excluded.", "Warning");
                                isWarnedAmbigCods = true;
                            }
                        }
                    }
                    catch (UnknownDataException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
        }
        final HashSet<Charset> poorCodons = new HashSet<Charset>(2);
        if (!stopCodonsSet.isEmpty()) {
            poorCodons.add(stopCodonsSet);
        }
        if (!ambiguousCodonsSet.isEmpty()) {
            poorCodons.add(ambiguousCodonsSet);
        }
        return poorCodons;
    }
    
    public void checkParameters() {
        if (this.evaluationModel.getDataType() != this.dataset.getDataType()) {
            switch (this.dataset.getDataType()) {
                case DNA: {
                    this.evaluationModel = EvaluationModel.JC;
                    break;
                }
                case PROTEIN: {
                    this.evaluationModel = EvaluationModel.POISSON;
                    break;
                }
                case STANDARD: {
                    this.evaluationModel = EvaluationModel.GTR2;
                    break;
                }
                case CODON: {
                    this.evaluationModel = EvaluationModel.GY;
                    break;
                }
            }
        }
        if (this.startingTreeModel.getDataType() != null && this.startingTreeModel.getDataType() != this.dataset.getDataType()) {
            switch (this.dataset.getDataType()) {
                case DNA: {
                    this.startingTreeModel = DistanceModel.JC;
                    break;
                }
                case PROTEIN: {
                    this.startingTreeModel = DistanceModel.POISSON;
                    break;
                }
                case STANDARD: {
                    this.startingTreeModel = DistanceModel.GTR2;
                    break;
                }
                case CODON: {
                    this.startingTreeModel = DistanceModel.GY;
                    break;
                }
            }
        }
    }
    
    public boolean hasManyReplicates() {
        return (this.replicatesStopCondition == ReplicatesStopCondition.NONE && this.replicatesNumber > 1) || this.replicatesStopCondition == ReplicatesStopCondition.MRE;
    }
    
    public DefaultStyledDocument showNexusDataMatrix() throws BadLocationException, UnknownDataException {
        final ProgressHandling progress = MetaPIGA.progressHandling;
        progress.newSingleProgress(0, this.charactersBlock.getMatrixLabels().size(), "Preparing Nexus data matrix display");
        int prog = 0;
        final String NORMAL = "Normal";
        final String ITALIC = "Italic";
        final String BOLD = "Bold";
        final DefaultStyledDocument doc = new DefaultStyledDocument();
        final Hashtable<String, SimpleAttributeSet> paraStyles = new Hashtable<String, SimpleAttributeSet>();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, "courier");
        paraStyles.put("Normal", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, "courier");
        StyleConstants.setItalic(attr, true);
        paraStyles.put("Italic", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setBold(attr, true);
        StyleConstants.setFontFamily(attr, "courier");
        paraStyles.put("Bold", attr);
        final AttributeSet defaultStyle = paraStyles.get("Normal");
        final AttributeSet boldStyle = paraStyles.get("Bold");
        int longestTaxon = 0;
        for (final Object taxa : this.charactersBlock.getMatrixLabels()) {
            if (taxa.toString().length() > longestTaxon) {
                longestTaxon = taxa.toString().length();
            }
        }
        if ("Positions".toString().length() > longestTaxon) {
            longestTaxon = "Positions".toString().length();
        }
        int nchar = 0;
        for (final Dataset.Partition p : this.dataset.getPartitions()) {
            nchar += p.getNChar();
        }
        doc.insertString(doc.getLength(), "Character matrix from your nexus file :\n\n", boldStyle);
        doc.insertString(doc.getLength(), String.valueOf(this.dataset.getDataType().verbose()) + " frequencies: " + this.dataset.getNTax() + " taxa and " + nchar + " " + this.dataset.getDataType().verbose().toLowerCase() + "s.\n", defaultStyle);
        doc.insertString(doc.getLength(), "Red " + this.dataset.getDataType().verbose().toLowerCase() + "s will be deleted.\n", defaultStyle);
        doc.insertString(doc.getLength(), "Equates are replaced by their standard IUB code.\n", defaultStyle);
        final String matchSymbol = (this.charactersBlock.getMatchChar() == null) ? "." : this.charactersBlock.getMatchChar();
        doc.insertString(doc.getLength(), "'" + matchSymbol + "' will be replaced by " + this.dataset.getDataType().verbose().toLowerCase() + "s at the same position on the first line.\n", defaultStyle);
        String indeterminateString = "";
        Label_0654: {
            switch (this.dataset.getDataType()) {
                case DNA: {
                    indeterminateString = "N (A, C, G or T)";
                    break Label_0654;
                }
                case PROTEIN: {
                    indeterminateString = "X (any amino acid)";
                    break Label_0654;
                }
                case STANDARD: {
                    indeterminateString = "? (0 or 1)";
                    break Label_0654;
                }
                case CODON: {
                    indeterminateString = "__X (any nucleotide codon)";
                    break;
                }
            }
            indeterminateString = "the undeterminate state";
        }
        final String missingSymbol = (this.charactersBlock.getMissing() == null) ? "." : this.charactersBlock.getMissing();
        doc.insertString(doc.getLength(), "'" + missingSymbol + "' will be replaced by " + indeterminateString + ".\n", defaultStyle);
        final String gapSymbol = (this.charactersBlock.getGap() == null) ? "." : this.charactersBlock.getGap();
        if (this.columnRemoval == ColumnRemoval.NONE) {
            doc.insertString(doc.getLength(), "'" + gapSymbol + "' will be replaced by " + indeterminateString + ".\n", defaultStyle);
        }
        else {
            doc.insertString(doc.getLength(), "Column with gaps ('" + gapSymbol + "') will be deleted.\n", defaultStyle);
        }
        if (!this.charsets.isPartitionsEmpty()) {
            doc.insertString(doc.getLength(), "Partitions are colored this way : ", defaultStyle);
            for (final Map.Entry<Charset, Color> e : this.partitionColors.entrySet()) {
                StyleConstants.setBackground(attr, e.getValue());
                StyleConstants.setForeground(attr, Color.BLACK);
                doc.insertString(doc.getLength(), e.getKey().toString(), attr);
                StyleConstants.setBackground(attr, Color.BLACK);
                StyleConstants.setForeground(attr, Color.GREEN);
                doc.insertString(doc.getLength(), " ", attr);
            }
            doc.insertString(doc.getLength(), "\n", defaultStyle);
        }
        doc.insertString(doc.getLength(), "\n", defaultStyle);
        boolean nextLine = false;
        int line = 0;
        String ws = "Positions";
        for (int spaces = longestTaxon - ws.toString().length(), j = 0; j < spaces; ++j) {
            ws = String.valueOf(ws) + " ";
        }
        ws = String.valueOf(ws) + "    ";
        attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, "courier");
        final DataType dataType = this.dataset.getDataType();
        StyleConstants.setBackground(attr, Color.BLACK);
        StyleConstants.setForeground(attr, new Color(104, 221, 255));
        doc.insertString(doc.getLength(), ws, attr);
        final int numOfDigits = new StringBuilder().append(this.charactersBlock.getDimensionsNChar()).toString().length();
        int startingPosition = 1;
        int endPosition = this.charactersBlock.getDimensionsNChar();
        if (dataType == DataType.CODON) {
            startingPosition = this.codonDomain.startCodonDomainPosition;
            endPosition = this.codonDomain.endCodonDomainPosition;
        }
        final int grayness = 20;
        final Color notInDomainColor = new Color(grayness, grayness, grayness);
        for (int p2 = 1; p2 <= this.charactersBlock.getDimensionsNChar(); ++p2) {
            String s;
            for (s = new StringBuilder().append(p2).toString(); s.length() < numOfDigits; s = " " + s) {}
            if (s.length() > line + 1) {
                s = new StringBuilder().append(s.charAt(line)).toString();
                nextLine = true;
            }
            if (!s.equals(" ")) {
                final Iterator<Charset> iterator = this.charsets.getPartitionIterator();
                while (iterator.hasNext()) {
                    final Charset ch = iterator.next();
                    final int relativePosition = (p2 - startingPosition) / dataType.getRenderingSize() + 1;
                    if (p2 < startingPosition || p2 > endPosition) {
                        StyleConstants.setBackground(attr, notInDomainColor);
                        StyleConstants.setForeground(attr, Color.BLACK);
                        break;
                    }
                    if (ch.isInCharset(relativePosition)) {
                        StyleConstants.setBackground(attr, this.partitionColors.get(ch));
                        StyleConstants.setForeground(attr, Color.BLACK);
                        break;
                    }
                }
            }
            doc.insertString(doc.getLength(), s, attr);
        }
        doc.insertString(doc.getLength(), " ", defaultStyle);
        doc.insertString(doc.getLength(), "\n", defaultStyle);
        String empty = "";
        for (int i = 0; i < ws.length(); ++i) {
            empty = String.valueOf(empty) + " ";
        }
        while (nextLine) {
            ++line;
            nextLine = false;
            doc.insertString(doc.getLength(), empty, defaultStyle);
            attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, "courier");
            StyleConstants.setBackground(attr, Color.BLACK);
            StyleConstants.setForeground(attr, new Color(104, 221, 255));
            for (int p3 = 1; p3 <= this.charactersBlock.getDimensionsNChar(); ++p3) {
                String s2;
                for (s2 = new StringBuilder().append(p3).toString(); s2.length() < numOfDigits; s2 = " " + s2) {}
                if (s2.length() < line + 1) {
                    s2 = " ";
                }
                else if (s2.length() == line + 1) {
                    s2 = new StringBuilder().append(s2.charAt(line)).toString();
                }
                else if (s2.length() > line + 1) {
                    s2 = new StringBuilder().append(s2.charAt(line)).toString();
                    nextLine = true;
                }
                if (!s2.equals(" ")) {
                    final Iterator<Charset> iterator2 = this.charsets.getPartitionIterator();
                    while (iterator2.hasNext()) {
                        final Charset ch2 = iterator2.next();
                        final int relativePosition2 = (p3 - startingPosition) / dataType.getRenderingSize() + 1;
                        if (p3 < startingPosition || p3 > endPosition) {
                            StyleConstants.setBackground(attr, notInDomainColor);
                            StyleConstants.setForeground(attr, Color.BLACK);
                            break;
                        }
                        if (ch2.isInCharset(relativePosition2)) {
                            StyleConstants.setBackground(attr, this.partitionColors.get(ch2));
                            StyleConstants.setForeground(attr, Color.BLACK);
                            break;
                        }
                    }
                }
                doc.insertString(doc.getLength(), s2, attr);
            }
            doc.insertString(doc.getLength(), " ", defaultStyle);
            doc.insertString(doc.getLength(), "\n", defaultStyle);
        }
        doc.insertString(doc.getLength(), "\n", defaultStyle);
        for (final Object taxa2 : this.charactersBlock.getMatrixLabels()) {
            progress.setValue(prog++);
            final List<String> data = new LinkedList<String>();
            for (final Object obj : this.charactersBlock.getMatrixData(taxa2.toString())) {
                final String nucl = obj.toString();
                if (nucl.length() > 0) {
                    if (nucl.length() > 1) {
                        final BitSet bitSet = new BitSet(dataType.numOfStates());
                        char[] charArray;
                        for (int length = (charArray = nucl.toCharArray()).length, n = 0; n < length; ++n) {
                            final char c = charArray[n];
                            bitSet.set(dataType.getStateOf(new StringBuilder().append(c).toString()));
                        }
                        data.add(dataType.getData(bitSet).toString());
                    }
                    else if (nucl.equals(matchSymbol) || (this.charactersBlock.isRespectCase() && nucl.equalsIgnoreCase(matchSymbol))) {
                        data.add(matchSymbol);
                    }
                    else if (nucl.equals(missingSymbol) || (this.charactersBlock.isRespectCase() && nucl.equalsIgnoreCase(missingSymbol))) {
                        data.add(missingSymbol);
                    }
                    else if (nucl.equals(gapSymbol) || (this.charactersBlock.isRespectCase() && nucl.equalsIgnoreCase(gapSymbol))) {
                        data.add(gapSymbol);
                    }
                    else {
                        try {
                            data.add(nucl.toUpperCase());
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                            throw new UnknownDataException(nucl, taxa2.toString(), e2.getCause());
                        }
                    }
                }
            }
            attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, "courier");
            if (this.deletedTaxa.contains(taxa2.toString())) {
                StyleConstants.setForeground(attr, Color.RED);
            }
            else {
                StyleConstants.setForeground(attr, Color.GREEN);
            }
            String t = taxa2.toString();
            for (int spaces = longestTaxon - taxa2.toString().length(), k = 0; k < spaces; ++k) {
                t = String.valueOf(t) + " ";
            }
            t = String.valueOf(t) + "    ";
            doc.insertString(doc.getLength(), t, attr);
            for (int l = 1; l <= data.size(); ++l) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, "courier");
                final String s3 = data.get(l - 1);
                startingPosition = 1;
                endPosition = data.size();
                if (dataType == DataType.CODON) {
                    startingPosition = this.codonDomain.startCodonDomainPosition;
                    endPosition = this.codonDomain.endCodonDomainPosition;
                }
                final int relativePosition3 = (l - startingPosition) / dataType.getRenderingSize() + 1;
                StyleConstants.setForeground(attr, Color.BLACK);
                if (this.deletedTaxa.contains(taxa2.toString()) || (this.columnRemoval == ColumnRemoval.GAP && this.dataset.hasGapAtPos(l)) || (this.columnRemoval == ColumnRemoval.NGAP && this.dataset.hasGapOrNAtPos(l))) {
                    StyleConstants.setForeground(attr, Color.RED);
                }
                else {
                    final Iterator<Charset> iterator3 = this.charsets.getExcludedCharsetIterator();
                    while (iterator3.hasNext()) {
                        final Charset ch3 = iterator3.next();
                        if (ch3.isInCharset(relativePosition3)) {
                            StyleConstants.setForeground(attr, Color.RED);
                            break;
                        }
                    }
                }
                final Iterator<Charset> iterator3 = this.charsets.getPartitionIterator();
                while (iterator3.hasNext()) {
                    final Charset ch3 = iterator3.next();
                    if (l < startingPosition || l > endPosition) {
                        StyleConstants.setBackground(attr, notInDomainColor);
                        break;
                    }
                    if (ch3.isInCharset(relativePosition3)) {
                        StyleConstants.setBackground(attr, this.partitionColors.get(ch3));
                        break;
                    }
                }
                doc.insertString(doc.getLength(), s3, attr);
            }
            doc.insertString(doc.getLength(), "\n", defaultStyle);
        }
        return doc;
    }
    
    public DefaultStyledDocument showDataset() throws BadLocationException, UnknownDataException {
        final ProgressHandling progress = MetaPIGA.progressHandling;
        progress.newSingleProgress(0, this.dataset.getTaxa().size(), "Preparing dataset display");
        int prog = 0;
        final String endl = "\n";
        final String NORMAL = "Normal";
        final String ITALIC = "Italic";
        final String BOLD = "Bold";
        final DataType dataType = this.dataset.getDataType();
        final DefaultStyledDocument doc = new DefaultStyledDocument();
        final Hashtable<String, SimpleAttributeSet> paraStyles = new Hashtable<String, SimpleAttributeSet>();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, "courier");
        paraStyles.put("Normal", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, "courier");
        StyleConstants.setItalic(attr, true);
        paraStyles.put("Italic", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, "courier");
        StyleConstants.setBold(attr, true);
        paraStyles.put("Bold", attr);
        final AttributeSet defaultStyle = paraStyles.get("Normal");
        final AttributeSet boldStyle = paraStyles.get("Bold");
        int longestTaxon = 0;
        for (final String taxa : this.dataset.getTaxa()) {
            if (taxa.length() > longestTaxon) {
                longestTaxon = taxa.length();
            }
        }
        if ("Weights".toString().length() > longestTaxon) {
            longestTaxon = "Weights".toString().length();
        }
        doc.insertString(doc.getLength(), "Character matrices used in MetaPIGA :" + endl + endl, boldStyle);
        doc.insertString(doc.getLength(), "Your Nexus matrix has been compressed, you can see the weight of each column on the last line." + endl, defaultStyle);
        doc.insertString(doc.getLength(), String.valueOf(this.dataset.getNTax()) + " taxa where kept." + endl, defaultStyle);
        doc.insertString(doc.getLength(), String.valueOf(this.dataset.getDataType().verbose()) + " frequencies : " + this.dataset.getDataFrequenciesToString() + endl, defaultStyle);
        if (!this.charsets.isPartitionsEmpty()) {
            doc.insertString(doc.getLength(), "Partitions (each one is used separatly during computation) : " + endl, defaultStyle);
            for (final Map.Entry<Charset, Color> e : this.partitionColors.entrySet()) {
                StyleConstants.setBackground(attr, e.getValue());
                StyleConstants.setForeground(attr, Color.BLACK);
                doc.insertString(doc.getLength(), e.getKey().toString(), attr);
                doc.insertString(doc.getLength(), " : " + this.dataset.getPartition(e.getKey()).getNChar() + " characters (" + this.dataset.getPartition(e.getKey()).getCompression() + " compression giving " + this.dataset.getPartition(e.getKey()).getCompressedNChar() + " characters)" + " - Frequencies : " + this.dataset.getPartition(e.getKey()).getDataFrequenciesToString() + endl, defaultStyle);
            }
            doc.insertString(doc.getLength(), endl, defaultStyle);
        }
        doc.insertString(doc.getLength(), endl, defaultStyle);
        for (final String taxa : this.dataset.getTaxa()) {
            progress.setValue(prog++);
            final int spaces = longestTaxon - taxa.toString().length();
            String stax = taxa;
            for (int j = 0; j < spaces; ++j) {
                stax = String.valueOf(stax) + " ";
            }
            stax = String.valueOf(stax) + "    ";
            doc.insertString(doc.getLength(), stax, defaultStyle);
            for (final Charset ch : this.dataset.getPartitionCharsets()) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, "courier");
                StyleConstants.setBackground(attr, this.partitionColors.get(ch));
                StyleConstants.setForeground(attr, Color.BLACK);
                for (final Data data : this.dataset.getPartition(ch).getAllData(taxa)) {
                    String characterString = data.toString();
                    if (dataType.getRenderingSize() > 1) {
                        characterString = String.valueOf(characterString) + " ";
                    }
                    doc.insertString(doc.getLength(), characterString, attr);
                }
                doc.insertString(doc.getLength(), " ", defaultStyle);
            }
            doc.insertString(doc.getLength(), endl, defaultStyle);
        }
        doc.insertString(doc.getLength(), endl, defaultStyle);
        boolean nextLine = false;
        int line = 0;
        String ws = "Weights";
        for (int spaces2 = longestTaxon - ws.toString().length(), j = 0; j < spaces2; ++j) {
            ws = String.valueOf(ws) + " ";
        }
        ws = String.valueOf(ws) + "    ";
        doc.insertString(doc.getLength(), ws, defaultStyle);
        for (final Charset ch : this.dataset.getPartitionCharsets()) {
            attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, "courier");
            StyleConstants.setBackground(attr, this.partitionColors.get(ch));
            StyleConstants.setForeground(attr, Color.BLACK);
            int[] allWeights;
            for (int length = (allWeights = this.dataset.getPartition(ch).getAllWeights()).length, k = 0; k < length; ++k) {
                final int w = allWeights[k];
                String s = new StringBuilder().append(w).toString();
                if (s.length() > line + 1) {
                    s = new StringBuilder().append(s.charAt(line)).toString();
                    nextLine = true;
                }
                s = this.addSpacesToWeightValue(dataType, s);
                if (dataType.getRenderingSize() > 1) {
                    s = String.valueOf(s) + " ";
                }
                doc.insertString(doc.getLength(), s, attr);
            }
            doc.insertString(doc.getLength(), " ", defaultStyle);
        }
        doc.insertString(doc.getLength(), endl, defaultStyle);
        String empty = "";
        for (int i = 0; i < ws.length(); ++i) {
            empty = String.valueOf(empty) + " ";
        }
        while (nextLine) {
            ++line;
            nextLine = false;
            doc.insertString(doc.getLength(), empty, defaultStyle);
            for (final Charset ch2 : this.dataset.getPartitionCharsets()) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, "courier");
                StyleConstants.setBackground(attr, this.partitionColors.get(ch2));
                StyleConstants.setForeground(attr, Color.BLACK);
                int[] allWeights2;
                for (int length2 = (allWeights2 = this.dataset.getPartition(ch2).getAllWeights()).length, l = 0; l < length2; ++l) {
                    final int w2 = allWeights2[l];
                    String s2 = new StringBuilder().append(w2).toString();
                    if (s2.length() < line + 1) {
                        s2 = " ";
                    }
                    else if (s2.length() == line + 1) {
                        s2 = new StringBuilder().append(s2.charAt(line)).toString();
                    }
                    else if (s2.length() > line + 1) {
                        s2 = new StringBuilder().append(s2.charAt(line)).toString();
                        nextLine = true;
                    }
                    s2 = this.addSpacesToWeightValue(dataType, s2);
                    if (dataType.getRenderingSize() > 1) {
                        s2 = String.valueOf(s2) + " ";
                    }
                    doc.insertString(doc.getLength(), s2, attr);
                }
                doc.insertString(doc.getLength(), " ", defaultStyle);
            }
            doc.insertString(doc.getLength(), endl, defaultStyle);
        }
        return doc;
    }
    
    private String addSpacesToWeightValue(final DataType dataType, String s) {
        for (int renderingSpaces = 0; renderingSpaces < dataType.getRenderingSize() / 2; ++renderingSpaces) {
            s = " " + s + " ";
        }
        if (dataType.getRenderingSize() % 2 == 0) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
    
    public DataBlock getModifiedDataBlock() {
        final DataType dataType = this.dataset.getDataType();
        final DataBlock newBlock = new DataBlock();
        newBlock.setGap("-");
        newBlock.setMatchChar(".");
        newBlock.setMissing("?");
        newBlock.setDataType(dataType.name());
        newBlock.setDimensionsNTax(this.dataset.getNTax());
        newBlock.setDimensionsNChar(this.dataset.getNChar());
        final List<Integer> keepedRows = new ArrayList<Integer>();
        final List<Integer> deletedCharacters = new ArrayList<Integer>();
        final Iterator<Charset> iterator = this.charsets.getCharsetIterator();
        while (iterator.hasNext()) {
            final Charset charset = iterator.next();
            deletedCharacters.addAll(charset.getCharacters());
        }
        final char[][] data = new char[this.charactersBlock.getMatrixLabels().size()][this.charactersBlock.getDimensionsNChar()];
        final List<String> taxaList = new ArrayList<String>();
        final String matchSymbol = (this.charactersBlock.getMatchChar() == null) ? "." : this.charactersBlock.getMatchChar();
        final String missingSymbol = (this.charactersBlock.getMissing() == null) ? "?" : this.charactersBlock.getMissing();
        final String gapSymbol = (this.charactersBlock.getGap() == null) ? "-" : this.charactersBlock.getGap();
        int currentRow = 0;
        for (final Object taxa : this.charactersBlock.getMatrixLabels()) {
            if (!this.deletedTaxa.contains(taxa.toString())) {
                keepedRows.add(currentRow);
                taxaList.add(taxa.toString());
            }
            int currentCol = 0;
            for (final Object obj : this.charactersBlock.getMatrixData(taxa.toString())) {
                final String nucl = obj.toString();
                if (nucl.length() > 0) {
                    if (nucl.length() > 1) {
                        final BitSet bitSet = new BitSet(dataType.numOfStates());
                        char[] charArray;
                        for (int length = (charArray = nucl.toCharArray()).length, k = 0; k < length; ++k) {
                            final char c = charArray[k];
                            try {
                                bitSet.set(dataType.getStateOf(new StringBuilder().append(c).toString()));
                            }
                            catch (UnknownDataException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            data[currentRow][currentCol] = dataType.getData(bitSet).toChar();
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    else if (nucl.equals(matchSymbol) || (this.charactersBlock.isRespectCase() && nucl.equalsIgnoreCase(matchSymbol))) {
                        data[currentRow][currentCol] = ((currentRow > 0) ? data[currentRow - 1][currentCol] : '.');
                    }
                    else if (nucl.equals(missingSymbol) || (this.charactersBlock.isRespectCase() && nucl.equalsIgnoreCase(missingSymbol))) {
                        data[currentRow][currentCol] = '?';
                    }
                    else if (nucl.equals(gapSymbol) || (this.charactersBlock.isRespectCase() && nucl.equalsIgnoreCase(gapSymbol))) {
                        data[currentRow][currentCol] = '-';
                    }
                    else {
                        try {
                            data[currentRow][currentCol] = dataType.getData(nucl.toUpperCase()).toChar();
                        }
                        catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                    ++currentCol;
                }
            }
            ++currentRow;
        }
        final int rows = keepedRows.size();
        final int columns = this.charactersBlock.getDimensionsNChar() - deletedCharacters.size();
        final char[][] dataMatrix = new char[rows][columns];
        int curCol = 0;
        for (int c2 = 0; c2 < data[0].length; ++c2) {
            final int character = c2 + 1;
            if (!deletedCharacters.contains(character)) {
                int curRow = 0;
                for (final int r : keepedRows) {
                    dataMatrix[curRow][curCol] = data[r][c2];
                    ++curRow;
                }
                ++curCol;
            }
        }
        for (int i = 0; i < dataMatrix.length; ++i) {
            final String taxon = taxaList.get(i);
            newBlock.addMatrixEntry(taxon);
            for (int j = 0; j < dataMatrix[i].length; ++j) {
                newBlock.appendMatrixData(taxon, new StringBuilder().append(dataMatrix[i][j]).toString());
            }
        }
        return newBlock;
    }
    
    public MetapigaBlock getMetapigaBlock() {
        final MetapigaBlock mp = new MetapigaBlock();
        mp.setHeuristic(this.heuristic);
        mp.setHcRestart(this.hcRestart);
        mp.setSaSchedule(this.saSchedule);
        mp.setSaScheduleParam(this.saScheduleParam);
        mp.setSaLundyC(this.saLundyC);
        mp.setSaLundyAlpha(this.saLundyAlpha);
        mp.setSaInitAccept(this.saInitAccept);
        mp.setSaFinalAccept(this.saFinalAccept);
        mp.setSaDeltaL(this.saDeltaL);
        mp.setSaDeltaLPercent(this.saDeltaLPercent);
        mp.setSaReheatingType(this.saReheatingType);
        mp.setSaReheatingValue(this.saReheatingValue);
        mp.setSaCoolingType(this.saCoolingType);
        mp.setSaCoolingSteps(this.saCoolingSteps);
        mp.setSaCoolingSuccesses(this.saCoolingSuccesses);
        mp.setSaCoolingFailures(this.saCoolingFailures);
        mp.setGaIndNum(this.gaIndNum);
        mp.setGaSelection(this.gaSelection);
        mp.setGaReplacementStrength(this.gaReplacementStrength);
        mp.setGaRecombination(this.gaRecombination);
        mp.setGaOperatorChange(this.gaOperatorChange);
        mp.setCpConsensus(this.cpConsensus);
        mp.setCpOperator(this.cpOperator);
        mp.setCpPopNum(this.cpPopNum);
        mp.setCpIndNum(this.cpIndNum);
        mp.setCpTolerance(this.cpTolerance);
        mp.setCpHybridization(this.cpHybridization);
        mp.setCpSelection(this.cpSelection);
        mp.setCpReplacementStrength(this.cpReplacementStrength);
        mp.setCpRecombination(this.cpRecombination);
        mp.setCpOperatorChange(this.cpOperatorChange);
        mp.setCpCoreNum(this.cpCoreNum);
        mp.setEvaluationRate(this.evaluationRate);
        mp.setEvaluationModel(this.evaluationModel);
        for (final Map.Entry<RateParameter, Double> x : this.evaluationRateParameters.entrySet()) {
            mp.addRateParameter(x.getKey(), x.getValue());
        }
        mp.setEvaluationStateFrequencies(this.evaluationStateFrequencies);
        mp.setEvaluationDistribution(this.evaluationDistribution, this.evaluationDistributionSubsets);
        mp.setEvaluationDistributionShape(this.evaluationDistributionShape);
        mp.setEvaluationPInv(this.evaluationPInv);
        for (final Map.Entry<Charset, Map<RateParameter, Double>> y : this.specificRateParameters.entrySet()) {
            for (final Map.Entry<RateParameter, Double> x2 : y.getValue().entrySet()) {
                mp.addSpecificRateParameter(y.getKey(), x2.getKey(), x2.getValue());
            }
        }
        for (final Map.Entry<Charset, Double> x3 : this.specificDistributionShapes.entrySet()) {
            mp.addSpecificDistributionShape(x3.getKey(), x3.getValue());
        }
        for (final Map.Entry<Charset, Double> x3 : this.specificsPInvs.entrySet()) {
            mp.addSpecificPInv(x3.getKey(), x3.getValue());
        }
        mp.setStartingTreeGeneration(this.startingTreeGeneration);
        mp.setStartingTreeGenerationRange(this.startingTreeGenerationRange);
        mp.setStartingTreeModel(this.startingTreeModel);
        mp.setStartingTreeDistribution(this.startingTreeDistribution, this.startingTreeDistributionShape);
        mp.setStartingTreePInv(this.startingTreePInv);
        mp.setStartingTreePInvPi(this.startingTreePInvPi);
        mp.setOptimization(this.optimization);
        mp.setOptimizationUse(this.optimizationUse);
        mp.setOptimizationAlgorithm(this.optimizationAlgorithm);
        for (final OptimizationTarget x4 : this.optimizationTargets) {
            mp.addOptimizationTarget(x4);
        }
        for (final Operator x5 : this.operators) {
            mp.addOperator(x5);
        }
        for (final Map.Entry<Operator, Integer> x6 : this.operatorsParameters.entrySet()) {
            mp.addOperatorsParameter(x6.getKey(), x6.getValue());
        }
        for (final Map.Entry<Operator, Double> x7 : this.operatorsFrequencies.entrySet()) {
            mp.addOperatorsFrequency(x7.getKey(), x7.getValue());
        }
        for (final Operator x5 : this.operatorIsDynamic) {
            mp.addOperatorIsDynamic(x5);
        }
        mp.setOperatorSelection(this.operatorSelection);
        mp.setDynamicInterval(this.dynamicInterval);
        mp.setDynamicMin(this.dynamicMin);
        mp.setColumnRemoval(this.columnRemoval);
        mp.setOutputDir(this.outputDir);
        mp.setLabel(this.label);
        for (final String x8 : this.outgroup) {
            mp.addOutgroup(x8);
        }
        for (final String x8 : this.deletedTaxa) {
            mp.addDeletedTaxa(x8);
        }
        Iterator<Charset> iterator = this.charsets.getCharsetIterator();
        while (iterator.hasNext()) {
            final Charset x9 = iterator.next();
            mp.addCharset(x9);
        }
        iterator = this.charsets.getExcludedCharsetIterator();
        while (iterator.hasNext()) {
            final Charset x9 = iterator.next();
            mp.addExcludedCharset(x9);
        }
        iterator = this.charsets.getPartitionIterator();
        while (iterator.hasNext()) {
            final Charset x9 = iterator.next();
            mp.addPartition(x9);
        }
        for (final HeuristicStopCondition x10 : this.sufficientStopConditions) {
            mp.addSufficientStopCondition(x10);
        }
        for (final HeuristicStopCondition x10 : this.necessaryStopConditions) {
            mp.addNecessaryStopCondition(x10);
        }
        mp.setStopCriterionSteps(this.stopCriterionSteps);
        mp.setStopCriterionTime(this.stopCriterionTime);
        mp.setStopCriterionAutoSteps(this.stopCriterionAutoSteps);
        mp.setStopCriterionAutoThreshold(this.stopCriterionAutoThreshold);
        mp.setStopCriterionConsensusMRE(this.stopCriterionConsensusMRE);
        mp.setStopCriterionConsensusGeneration(this.stopCriterionConsensusGeneration);
        mp.setStopCriterionConsensusInterval(this.stopCriterionConsensusInterval);
        mp.setReplicatesStopCondition(this.replicatesStopCondition);
        mp.setReplicatesMRE(this.replicatesMRE);
        mp.setReplicatesNumber(this.replicatesNumber);
        mp.setReplicatesMinimum(this.replicatesMinimum);
        mp.setReplicatesMaximum(this.replicatesMaximum);
        mp.setReplicatesInterval(this.replicatesInterval);
        mp.setReplicatesParallel(this.replicatesParallel);
        for (final LogFile x11 : this.logFiles) {
            mp.addLogFile(x11);
        }
        mp.setGridReplicate(this.gridReplicate);
        mp.setGridOutput(this.cloudOutput);
        mp.setUseGrid(this.useGrid);
        mp.setGridServer(this.gridServer);
        mp.setGridClient(this.gridClient);
        mp.setGridModule(this.gridModule);
        mp.setLikelihoodCalculationType(this.likelihoodCalculationType);
        if (this.areCodons()) {
            mp.setDataType(this.dataset.getDataType());
            mp.setCodonDomainRange(this.codonDomain.getStartCodonDomainPosition(), this.codonDomain.getEndCodonDomainPosition());
            mp.setCodonTable(this.currentDNAtable);
        }
        return mp;
    }
    
    public void writeTreeBlock(final Writer writer) throws IOException, NullAncestorException, UnrootableTreeException {
        final String endl = NexusFileFormat.NEW_LINE;
        writer.write(String.valueOf(endl) + endl + "BEGIN TREES; [Starting trees]" + endl);
        for (final Tree tree : this.startingTrees) {
            writer.write(String.valueOf(tree.toNewickLine(false, false)) + endl);
        }
        writer.write("END;" + endl + endl);
    }
    
    @Override
    public String toString() {
        return this.label;
    }
    
    public String printParameters() {
        String s = "";
        s = String.valueOf(s) + "Current parameters are :\n";
        s = String.valueOf(s) + "Output directory : " + this.outputDir + "/" + this.label + "\n";
        s = String.valueOf(s) + "use heuristic : ";
        s = String.valueOf(s) + this.heuristic.verbose();
        switch (this.heuristic) {
            case HC: {
                if (this.hcRestart > 0) {
                    s = String.valueOf(s) + " (random-restart hill climbing with " + this.hcRestart + " restart)";
                    break;
                }
                s = String.valueOf(s) + " (stochastic hill climbing)";
                break;
            }
            case SA: {
                s = String.valueOf(s) + " using " + this.saSchedule + " cooling schedule";
                if (this.saSchedule == SASchedule.GEOM || this.saSchedule == SASchedule.RP) {
                    s = String.valueOf(s) + " (with parameter " + Tools.doubletoString(this.saScheduleParam, 6) + ")";
                }
                s = String.valueOf(s) + "\n";
                if (this.saSchedule == SASchedule.LUNDY) {
                    s = String.valueOf(s) + "Parameter c = " + this.saLundyC + " and parameter alpha = " + this.saLundyAlpha + "\n";
                }
                else {
                    s = String.valueOf(s) + "Inital maximum acceptance probability of " + Tools.doubleToPercent(this.saInitAccept, 2);
                    if (this.saSchedule == SASchedule.LIN || this.saSchedule == SASchedule.TRI || this.saSchedule == SASchedule.EXP || this.saSchedule == SASchedule.LOG || this.saSchedule == SASchedule.PER || this.saSchedule == SASchedule.SPER || this.saSchedule == SASchedule.COSH || this.saSchedule == SASchedule.TANH) {
                        s = String.valueOf(s) + " and final maximum acceptance probability of " + Tools.doubleToPercent(this.saFinalAccept, 2);
                    }
                }
                s = String.valueOf(s) + "\n";
                switch (this.saDeltaL) {
                    case BURNIN: {
                        s = String.valueOf(s) + "A burn-in period is done to compute delta L for starting temperature.\n";
                        break;
                    }
                    case PERCENT: {
                        s = String.valueOf(s) + "The delta L used in starting temperature is " + Tools.doubleToPercent(this.saDeltaLPercent, 4) + "% of the NJT.\n";
                        break;
                    }
                }
                switch (this.saReheatingType) {
                    case DECREMENTS: {
                        s = String.valueOf(s) + "Temperature is reset when it has been decreased " + (int)this.saReheatingValue + " times.";
                        break;
                    }
                    case NEVER: {
                        s = String.valueOf(s) + "Temperature is never reset.";
                        break;
                    }
                    case THRESHOLD: {
                        s = String.valueOf(s) + "Temperature is reset when it reach " + Tools.doubleToPercent(this.saReheatingValue, 6) + " of starting temperature.";
                        break;
                    }
                }
                switch (this.saCoolingType) {
                    case STEPS: {
                        s = String.valueOf(s) + " Temperature is decreased after " + this.saCoolingSteps + " steps.";
                        break;
                    }
                    case SF: {
                        s = String.valueOf(s) + " Temperature is decreased after " + this.saCoolingSuccesses + " successes or " + this.saCoolingFailures + " failures.";
                        break;
                    }
                }
                break;
            }
            case GA: {
                s = String.valueOf(s) + " with " + this.gaIndNum + " individuals and changing operator at each " + this.gaOperatorChange + "\n";
                s = String.valueOf(s) + "Selection used is " + this.gaSelection;
                if (this.gaSelection == GASelection.REPLACEMENT) {
                    s = String.valueOf(s) + " with a strength of " + this.gaReplacementStrength;
                }
                if (this.gaRecombination <= 0.0) {
                    s = String.valueOf(s) + " without recombination";
                    break;
                }
                s = String.valueOf(s) + " with " + Tools.doubleToPercent(this.gaRecombination, 0) + " recombination";
                break;
            }
            case CP: {
                s = String.valueOf(s) + "(" + this.cpConsensus + " - " + this.cpOperator + ") with " + this.cpPopNum + " populations, " + this.cpIndNum + " individuals, a tolerance of " + Tools.doubleToPercent(this.cpTolerance, 0) + ((this.cpHybridization <= 0.0) ? ", no interpopulation hybridization" : (", " + Tools.doubleToPercent(this.cpHybridization, 0) + " of interpopulation hybridization")) + " and changing operator at each " + this.cpOperatorChange + "\n";
                s = String.valueOf(s) + "Selection used is " + this.cpSelection;
                if (this.cpSelection == CPSelection.REPLACEMENT) {
                    s = String.valueOf(s) + " with a strength of " + this.cpReplacementStrength;
                }
                if (this.cpRecombination <= 0.0) {
                    s = String.valueOf(s) + " without recombination";
                }
                else {
                    s = String.valueOf(s) + " with " + Tools.doubleToPercent(this.cpRecombination, 0) + " recombination";
                }
                if (this.cpCoreNum > 1) {
                    s = String.valueOf(s) + "\n";
                    s = String.valueOf(s) + "Parallelization is active : populations are distributed on " + this.cpCoreNum + " cores";
                    break;
                }
                break;
            }
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Use evaluation criterion";
        s = String.valueOf(s) + " Maximum Likelihood with rate matrix R for " + this.evaluationRate;
        s = String.valueOf(s) + " using " + this.evaluationModel + " model";
        if (this.evaluationModel.isEmpirical()) {
            switch (this.evaluationStateFrequencies) {
                case EMPIRICAL: {
                    s = String.valueOf(s) + " (with empirical equilibrium amino acid frequencies)";
                    break;
                }
                case ESTIMATED: {
                    s = String.valueOf(s) + " (with estimated equilibrium amino acid frequencies)";
                    break;
                }
            }
        }
        s = String.valueOf(s) + " and " + this.evaluationDistribution + " distribution";
        if (this.evaluationDistribution == EvaluationDistribution.GAMMA || this.evaluationDistribution == EvaluationDistribution.VDP) {
            s = String.valueOf(s) + " (with " + this.evaluationDistributionSubsets + " subsets)";
        }
        s = String.valueOf(s) + ".\n";
        final Set<Charset> specifics = new TreeSet<Charset>();
        specifics.addAll(this.specificRateParameters.keySet());
        specifics.addAll(this.specificDistributionShapes.keySet());
        specifics.addAll(this.specificsPInvs.keySet());
        for (final Charset ch : specifics) {
            s = String.valueOf(s) + "Model parameters specifics to partition " + ch.getLabel() + ": ";
            if (this.specificRateParameters.containsKey(ch)) {
                switch (this.evaluationModel) {
                    case HKY85:
                    case K2P: {
                        s = String.valueOf(s) + "rate matrix parameter is ";
                        s = String.valueOf(s) + RateParameter.K.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.K) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case TN93: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        s = String.valueOf(s) + RateParameter.K1.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.K1) + ") and ";
                        s = String.valueOf(s) + RateParameter.K2.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.K2) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GTR: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        s = String.valueOf(s) + RateParameter.A.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.A) + "), ";
                        s = String.valueOf(s) + RateParameter.B.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.B) + "), ";
                        s = String.valueOf(s) + RateParameter.C.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.C) + "), ";
                        s = String.valueOf(s) + RateParameter.D.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.D) + ") and ";
                        s = String.valueOf(s) + RateParameter.E.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.E) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GTR20: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        final RateParameter[] rp = RateParameter.getParametersOfModel(EvaluationModel.GTR20);
                        for (int r = 0; r < rp.length; ++r) {
                            s = String.valueOf(s) + rp[r].verbose() + "(" + this.specificRateParameters.get(ch).get(rp[r]) + ")";
                            if (r < rp.length - 2) {
                                s = String.valueOf(s) + ", ";
                            }
                            else if (r == rp.length - 2) {
                                s = String.valueOf(s) + " and ";
                            }
                        }
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GY: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        s = String.valueOf(s) + RateParameter.KAPPA.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.KAPPA) + ") and ";
                        s = String.valueOf(s) + RateParameter.OMEGA.verbose() + "(" + this.specificRateParameters.get(ch).get(RateParameter.OMEGA) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GTR64: {
                        s = String.valueOf(s) + "There is too much matrix parameters too be  shown in this dialog, please check the matrix itself.";
                        break;
                    }
                }
            }
            else {
                switch (this.evaluationModel) {
                    case HKY85:
                    case K2P: {
                        s = String.valueOf(s) + "rate matrix parameter is ";
                        s = String.valueOf(s) + RateParameter.K.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.K) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case TN93: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        s = String.valueOf(s) + RateParameter.K1.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.K1) + ") and ";
                        s = String.valueOf(s) + RateParameter.K2.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.K2) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GTR: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        s = String.valueOf(s) + RateParameter.A.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.A) + "), ";
                        s = String.valueOf(s) + RateParameter.B.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.B) + "), ";
                        s = String.valueOf(s) + RateParameter.C.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.C) + "), ";
                        s = String.valueOf(s) + RateParameter.D.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.D) + ") and ";
                        s = String.valueOf(s) + RateParameter.E.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.E) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GTR20: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        final RateParameter[] rp = RateParameter.getParametersOfModel(EvaluationModel.GTR20);
                        for (int r = 0; r < rp.length; ++r) {
                            s = String.valueOf(s) + rp[r].verbose() + "(" + this.evaluationRateParameters.get(rp[r]) + ")";
                            if (r < rp.length - 2) {
                                s = String.valueOf(s) + ", ";
                            }
                            else if (r == rp.length - 2) {
                                s = String.valueOf(s) + " and ";
                            }
                        }
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GY: {
                        s = String.valueOf(s) + "rate matrix parameters are ";
                        s = String.valueOf(s) + RateParameter.KAPPA.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.KAPPA) + ") and ";
                        s = String.valueOf(s) + RateParameter.OMEGA.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.OMEGA) + ")";
                        s = String.valueOf(s) + ", ";
                        break;
                    }
                    case GTR64: {
                        s = String.valueOf(s) + "There is too much matrix parameters too be  shown in this dialog, please check the matrix itself.";
                        break;
                    }
                }
            }
            if (this.evaluationDistribution == EvaluationDistribution.GAMMA) {
                if (this.specificDistributionShapes.containsKey(ch)) {
                    s = String.valueOf(s) + "shape of the gamma distribution is " + this.specificDistributionShapes.get(ch) + ", ";
                }
                else {
                    s = String.valueOf(s) + "shape of the gamma distribution is " + this.evaluationDistributionShape + ", ";
                }
            }
            if (this.specificsPInvs.containsKey(ch)) {
                s = String.valueOf(s) + Tools.doubleToPercent(this.specificsPInvs.get(ch), 2) + " of invariant sites.\n";
            }
            else {
                s = String.valueOf(s) + Tools.doubleToPercent(this.evaluationPInv, 2) + " of invariant sites.\n";
            }
        }
        if (!specifics.isEmpty() && !specifics.containsAll(this.charsets.getPartitions())) {
            final Set<Charset> general = this.charsets.getPartitions();
            general.removeAll(specifics);
            s = String.valueOf(s) + "Model parameters of other partition" + ((general.size() > 1) ? "s" : "") + " " + general.toString() + ": ";
        }
        else if (specifics.isEmpty()) {
            s = String.valueOf(s) + "Model parameters: ";
        }
        if (this.charsets.isPartitionsEmpty() || !specifics.containsAll(this.charsets.getPartitions())) {
            switch (this.evaluationModel) {
                case HKY85:
                case K2P: {
                    s = String.valueOf(s) + "rate matrix parameter is ";
                    s = String.valueOf(s) + RateParameter.K.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.K) + ")";
                    s = String.valueOf(s) + ", ";
                    break;
                }
                case TN93: {
                    s = String.valueOf(s) + "rate matrix parameters are ";
                    s = String.valueOf(s) + RateParameter.K1.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.K1) + ") and ";
                    s = String.valueOf(s) + RateParameter.K2.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.K2) + ")";
                    s = String.valueOf(s) + ", ";
                    break;
                }
                case GTR: {
                    s = String.valueOf(s) + "rate matrix parameters are ";
                    s = String.valueOf(s) + RateParameter.A.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.A) + "), ";
                    s = String.valueOf(s) + RateParameter.B.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.B) + "), ";
                    s = String.valueOf(s) + RateParameter.C.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.C) + "), ";
                    s = String.valueOf(s) + RateParameter.D.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.D) + ") and ";
                    s = String.valueOf(s) + RateParameter.E.verbose() + "(" + this.evaluationRateParameters.get(RateParameter.E) + ")";
                    s = String.valueOf(s) + ", ";
                    break;
                }
                case GTR20: {
                    s = String.valueOf(s) + "rate matrix parameters are ";
                    final RateParameter[] rp2 = RateParameter.getParametersOfModel(EvaluationModel.GTR20);
                    for (int r2 = 0; r2 < rp2.length; ++r2) {
                        s = String.valueOf(s) + rp2[r2].verbose() + "(" + this.evaluationRateParameters.get(rp2[r2]) + ")";
                        if (r2 < rp2.length - 2) {
                            s = String.valueOf(s) + ", ";
                        }
                        else if (r2 == rp2.length - 2) {
                            s = String.valueOf(s) + " and ";
                        }
                    }
                    s = String.valueOf(s) + ", ";
                    break;
                }
            }
            if (this.evaluationDistribution == EvaluationDistribution.GAMMA) {
                s = String.valueOf(s) + "shape of the gamma distribution is " + this.evaluationDistributionShape + ", ";
            }
            s = String.valueOf(s) + Tools.doubleToPercent(this.evaluationPInv, 2) + " of invariant sites.\n";
        }
        if (this.evaluationRate == EvaluationRate.TREE || this.evaluationRate == EvaluationRate.BRANCH) {
            if (this.optimization == Optimization.NEVER) {
                s = String.valueOf(s) + "Intra-step optimization will never be performed.";
            }
            else {
                s = String.valueOf(s) + "Intra-step optimization will be performed ";
                switch (this.optimization) {
                    case CONSENSUSTREE: {
                        s = String.valueOf(s) + "only the consensus tree";
                        break;
                    }
                    case ENDSEARCH: {
                        s = String.valueOf(s) + "only at the end of (each) search";
                        break;
                    }
                    case STOCH: {
                        s = String.valueOf(s) + "on " + this.optimizationUse * 100.0 + "% of case";
                        break;
                    }
                    case DISC: {
                        s = String.valueOf(s) + "every " + this.optimizationUse + " steps";
                        break;
                    }
                }
                s = String.valueOf(s) + " using " + this.optimizationAlgorithm.verbose();
                s = String.valueOf(s) + ", on ";
                if (this.optimizationTargets.isEmpty()) {
                    s = String.valueOf(s) + "nothing ! (You should select at least a target)";
                }
                for (final OptimizationTarget target : this.optimizationTargets) {
                    s = String.valueOf(s) + target.verbose().toLowerCase() + ", ";
                }
            }
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Tree generation using " + this.startingTreeGeneration;
        if (this.startingTreeGeneration == StartingTreeGeneration.LNJ) {
            s = String.valueOf(s) + " (range " + Tools.doubleToPercent(this.startingTreeGenerationRange, 0) + ")";
        }
        if (this.startingTreeGeneration != StartingTreeGeneration.RANDOM) {
            s = String.valueOf(s) + " with " + this.startingTreeModel + " model";
            s = String.valueOf(s) + " and " + this.startingTreeDistribution + " distribution";
            if (this.startingTreeDistribution == StartingTreeDistribution.GAMMA || this.startingTreeDistribution == StartingTreeDistribution.VDP) {
                s = String.valueOf(s) + " (with parameter of " + this.startingTreeDistributionShape + ")";
            }
            s = String.valueOf(s) + " with " + Tools.doubleToPercent(this.startingTreePInv, 2) + " of invariant";
            if (this.startingTreePInv > 0.0) {
                s = String.valueOf(s) + " (base composition " + this.startingTreePInvPi + ")";
            }
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Operators that will be used on trees : ";
        for (final Operator op : this.operators) {
            s = String.valueOf(s) + op.toString();
            if (this.operatorsParameters.containsKey(op)) {
                s = String.valueOf(s) + "(";
                switch (this.operatorsParameters.get(op)) {
                    case 0: {
                        s = String.valueOf(s) + "ALL";
                        break;
                    }
                    case 1: {
                        if (op != Operator.RPM) {
                            s = String.valueOf(s) + "RANDOM";
                            break;
                        }
                        s = String.valueOf(s) + this.operatorsParameters.get(op);
                        break;
                    }
                    default: {
                        s = String.valueOf(s) + this.operatorsParameters.get(op);
                        break;
                    }
                }
                s = String.valueOf(s) + ")";
            }
            s = String.valueOf(s) + ", ";
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Operator selection " + this.operatorSelection;
        if (this.operatorSelection == OperatorSelection.FREQLIST) {
            s = String.valueOf(s) + " = (";
            for (final Operator op : this.operators) {
                s = String.valueOf(s) + Tools.doubleToPercent(this.operatorsFrequencies.get(op), 2);
                if (this.operatorIsDynamic.contains(op)) {
                    s = String.valueOf(s) + "(dyn)";
                }
                s = String.valueOf(s) + " ";
            }
            s = String.valueOf(s) + ")";
            s = String.valueOf(s) + "\n";
            s = String.valueOf(s) + "For operators dynamic frequencies use interval of " + this.dynamicInterval + " steps, with a minimum frequency of " + Tools.doubleToPercent(this.dynamicMin, 2) + "\n";
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Taxas selected as the outgroup : ";
        if (this.outgroup.isEmpty()) {
            s = String.valueOf(s) + "none !";
        }
        else {
            for (final String st : this.outgroup) {
                s = String.valueOf(s) + st + " ";
            }
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Taxas removed for the analysis : ";
        if (this.deletedTaxa.isEmpty()) {
            s = String.valueOf(s) + "none !";
        }
        else {
            for (final String st : this.deletedTaxa) {
                s = String.valueOf(s) + st + " ";
            }
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Defined charsets : ";
        if (this.charsets.isCharsetsEmpty()) {
            s = String.valueOf(s) + "none !";
        }
        else {
            final Iterator<Charset> iterator = this.charsets.getCharsetIterator();
            while (iterator.hasNext()) {
                final Charset st2 = iterator.next();
                s = String.valueOf(s) + st2.getLabel() + "={" + st2.getAllRanges() + "} ";
            }
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Characters excluded for the analysis : ";
        if (this.charsets.isExcludedCharsetsEmpty()) {
            s = String.valueOf(s) + "none !";
        }
        else {
            final Iterator<Charset> iterator = this.charsets.getExcludedCharsetIterator();
            while (iterator.hasNext()) {
                final Charset st2 = iterator.next();
                s = String.valueOf(s) + st2 + " ";
            }
        }
        s = String.valueOf(s) + "\n";
        s = String.valueOf(s) + "Partitionning analysis in different charsets : ";
        if (this.charsets.isPartitionsEmpty() || this.charsets.containsPartition("FULL SET")) {
            s = String.valueOf(s) + "no !";
        }
        else {
            final Iterator<Charset> iterator = this.charsets.getPartitionIterator();
            while (iterator.hasNext()) {
                final Charset st2 = iterator.next();
                s = String.valueOf(s) + st2 + " ";
            }
        }
        s = String.valueOf(s) + "\n";
        if (this.columnRemoval == ColumnRemoval.NONE) {
            s = String.valueOf(s) + "Gaps will be interpreted as N\n";
        }
        else if (this.columnRemoval == ColumnRemoval.GAP) {
            s = String.valueOf(s) + "Column containing gaps will be removed\n";
        }
        else if (this.columnRemoval == ColumnRemoval.NGAP) {
            s = String.valueOf(s) + "Column containing gaps or N will be removed\n";
        }
        if (this.sufficientStopConditions.isEmpty() && this.necessaryStopConditions.isEmpty()) {
            if (this.heuristic != Heuristic.BS) {
                s = String.valueOf(s) + "Heuristic will not start, only starting tree will be computed\n";
            }
        }
        else {
            if (!this.sufficientStopConditions.isEmpty()) {
                s = String.valueOf(s) + "Sufficient stopping conditions for heuristic: ";
                if (this.sufficientStopConditions.contains(HeuristicStopCondition.STEPS)) {
                    s = String.valueOf(s) + "stop after " + this.stopCriterionSteps + " steps, ";
                }
                if (this.sufficientStopConditions.contains(HeuristicStopCondition.TIME)) {
                    s = String.valueOf(s) + "stop after " + this.stopCriterionTime + " hours, ";
                }
                if (this.sufficientStopConditions.contains(HeuristicStopCondition.AUTO)) {
                    s = String.valueOf(s) + "automatic stop after " + this.stopCriterionAutoSteps + " steps without significative improvement of likelihood (" + Tools.doubleToPercent(this.stopCriterionAutoThreshold, 4) + "), ";
                }
                if (this.sufficientStopConditions.contains(HeuristicStopCondition.CONSENSUS)) {
                    s = String.valueOf(s) + "automatic stop when mean relative error of " + this.stopCriterionConsensusInterval + " consecutive consensus trees stay below " + Tools.doubleToPercent(this.stopCriterionConsensusMRE, 0) + " using trees sampled every " + this.stopCriterionConsensusGeneration + " generations, ";
                }
                s = String.valueOf(s) + "\n";
            }
            if (!this.necessaryStopConditions.isEmpty()) {
                s = String.valueOf(s) + "Necessary stopping conditions for heuristic: ";
                if (this.necessaryStopConditions.contains(HeuristicStopCondition.STEPS)) {
                    s = String.valueOf(s) + this.stopCriterionSteps + " steps, ";
                }
                if (this.necessaryStopConditions.contains(HeuristicStopCondition.TIME)) {
                    s = String.valueOf(s) + this.stopCriterionTime + " hours, ";
                }
                if (this.necessaryStopConditions.contains(HeuristicStopCondition.AUTO)) {
                    s = String.valueOf(s) + this.stopCriterionAutoSteps + " steps without significative improvement of likelihood (" + Tools.doubleToPercent(this.stopCriterionAutoThreshold, 15) + "), ";
                }
                if (this.necessaryStopConditions.contains(HeuristicStopCondition.CONSENSUS)) {
                    s = String.valueOf(s) + "mean relative error of " + this.stopCriterionConsensusInterval + " consecutive consensus trees stay below " + Tools.doubleToPercent(this.stopCriterionConsensusMRE, 0) + " using trees sampled every " + this.stopCriterionConsensusGeneration + " generations, ";
                }
                s = String.valueOf(s) + "\n";
            }
        }
        switch (this.replicatesStopCondition) {
            case NONE: {
                if (this.replicatesNumber <= 1) {
                    break;
                }
                s = String.valueOf(s) + "Make " + this.replicatesNumber + " replicates and compute majority-rule consensus tree" + "\n";
                if (this.replicatesParallel > 1) {
                    s = String.valueOf(s) + this.replicatesParallel + " replicates will run in parallel\n";
                    break;
                }
                break;
            }
            case MRE: {
                s = String.valueOf(s) + "Make between " + this.replicatesMinimum + " and " + this.replicatesMaximum + " replicates, stopping when mean relative error between at least " + this.replicatesInterval + " consecutive trees stay below " + Tools.doubleToPercent(this.replicatesMRE, 0) + ".\n";
                if (this.replicatesParallel > 1) {
                    s = String.valueOf(s) + this.replicatesParallel + " replicates will run in parallel\n";
                    break;
                }
                break;
            }
        }
        if (this.useGrid) {
            s = String.valueOf(s) + "MetaPIGA will run on the following GRID:\n";
            s = String.valueOf(s) + "Server address: " + this.gridServer + "\n";
            s = String.valueOf(s) + "Client id: " + this.gridClient + "\n";
            s = String.valueOf(s) + "MetaPIGA module id: " + this.gridModule + "\n";
            s = String.valueOf(s) + "log files are ignored on GRID.\n";
        }
        else {
            s = String.valueOf(s) + "log files : ";
            if (this.logFiles.isEmpty()) {
                s = String.valueOf(s) + "none !";
            }
            else {
                for (final LogFile st3 : this.logFiles) {
                    s = String.valueOf(s) + st3.verbose() + " ";
                }
            }
            s = String.valueOf(s) + "\n";
        }
        if (this.gridReplicate) {
            s = String.valueOf(s) + "This is a single replicate sent through the GRID that will be run on this machine. Output file wille be named '" + this.cloudOutput + "'.\n";
        }
        return s;
    }
    
    public Parameters duplicateButShareDataset() throws UnknownDataException, NexusInconsistencyException, CharsetIntersectionException, IncompatibleDataException {
        final Parameters P = new Parameters(this.label);
        P.setParameters(this.charactersBlock);
        this.applyParametersTo(P, false);
        return P;
    }
    
    public Parameters duplicate() throws UnknownDataException, NexusInconsistencyException, CharsetIntersectionException, IncompatibleDataException {
        final Parameters P = new Parameters(this.label);
        P.setParameters(this.charactersBlock);
        this.applyParametersTo(P);
        return P;
    }
    
    public void applyParametersTo(final Parameters P) throws UnknownDataException, NexusInconsistencyException, CharsetIntersectionException, IncompatibleDataException {
        this.applyParametersTo(P, true);
    }
    
    public void translateToCodonsInRange(final int startPos, int endPos, final CodonTransitionTableType tabType) {
        endPos = this.calibrateLastDomainPosition(startPos, endPos);
        this.charsets.translateToCodons(startPos, endPos);
        this.codonDomain = new CodonDomainDefinition(startPos, endPos);
        this.defineTransitionCodonTable(tabType);
    }
    
    public void setCodonsInRange(final int startPos, final int endPos, final CodonTransitionTableType tableType) {
        this.codonDomain = new CodonDomainDefinition(startPos, endPos);
        this.defineTransitionCodonTable(tableType);
    }
    
    public void revertCodons() {
        this.charsets.translateCodonToNucleotideCharsets();
        this.codonDomain = null;
        this.codonCharactersBlock = null;
        this.evaluationModel = EvaluationModel.JC;
        if (this.operators.contains(Operator.RPM)) {
            this.operators.remove(Operator.RPM);
        }
    }
    
    public boolean areCodons() {
        return this.codonDomain != null;
    }
    
    public CodonCharactersBlock getCodonCharactersBlock() {
        return this.codonCharactersBlock;
    }
    
    public void applyParametersTo(final Parameters P, final boolean dupDataset) throws UnknownDataException, NexusInconsistencyException, CharsetIntersectionException, IncompatibleDataException {
        P.heuristic = this.heuristic;
        P.hcRestart = this.hcRestart;
        P.saSchedule = this.saSchedule;
        P.saScheduleParam = this.saScheduleParam;
        P.saLundyC = this.saLundyC;
        P.saLundyAlpha = this.saLundyAlpha;
        P.saInitAccept = this.saInitAccept;
        P.saFinalAccept = this.saFinalAccept;
        P.saDeltaL = this.saDeltaL;
        P.saDeltaLPercent = this.saDeltaLPercent;
        P.saReheatingType = this.saReheatingType;
        P.saReheatingValue = this.saReheatingValue;
        P.saCoolingType = this.saCoolingType;
        P.saCoolingSteps = this.saCoolingSteps;
        P.saCoolingSuccesses = this.saCoolingSuccesses;
        P.saCoolingFailures = this.saCoolingFailures;
        P.gaIndNum = this.gaIndNum;
        P.gaSelection = this.gaSelection;
        P.gaReplacementStrength = this.gaReplacementStrength;
        P.gaRecombination = this.gaRecombination;
        P.gaOperatorChange = this.gaOperatorChange;
        P.cpConsensus = this.cpConsensus;
        P.cpOperator = this.cpOperator;
        P.cpPopNum = this.cpPopNum;
        P.cpIndNum = this.cpIndNum;
        P.cpTolerance = this.cpTolerance;
        P.cpHybridization = this.cpHybridization;
        P.cpSelection = this.cpSelection;
        P.cpReplacementStrength = this.cpReplacementStrength;
        P.cpRecombination = this.cpRecombination;
        P.cpOperatorChange = this.cpOperatorChange;
        P.cpCoreNum = this.cpCoreNum;
        P.evaluationRate = this.evaluationRate;
        P.evaluationModel = this.evaluationModel;
        P.evaluationRateParameters = new HashMap<RateParameter, Double>(this.evaluationRateParameters);
        P.evaluationStateFrequencies = this.evaluationStateFrequencies;
        P.evaluationDistribution = this.evaluationDistribution;
        P.evaluationDistributionSubsets = this.evaluationDistributionSubsets;
        P.evaluationDistributionShape = this.evaluationDistributionShape;
        P.evaluationPInv = this.evaluationPInv;
        P.startingTreeGeneration = this.startingTreeGeneration;
        P.startingTreeGenerationRange = this.startingTreeGenerationRange;
        P.startingTreeModel = this.startingTreeModel;
        P.startingTreeDistribution = this.startingTreeDistribution;
        P.startingTreeDistributionShape = this.startingTreeDistributionShape;
        P.startingTreePInv = this.startingTreePInv;
        P.startingTreePInvPi = this.startingTreePInvPi;
        P.optimization = this.optimization;
        P.optimizationUse = this.optimizationUse;
        P.optimizationAlgorithm = this.optimizationAlgorithm;
        P.optimizationTargets = new HashSet<OptimizationTarget>(this.optimizationTargets);
        P.operators = new ArrayList<Operator>(this.operators);
        P.operatorsParameters = new HashMap<Operator, Integer>(this.operatorsParameters);
        P.operatorsFrequencies = new HashMap<Operator, Double>(this.operatorsFrequencies);
        P.operatorIsDynamic = new HashSet<Operator>(this.operatorIsDynamic);
        P.operatorSelection = this.operatorSelection;
        P.dynamicInterval = this.dynamicInterval;
        P.dynamicMin = this.dynamicMin;
        P.columnRemoval = this.columnRemoval;
        P.outputDir = this.outputDir;
        P.sufficientStopConditions = new HashSet<HeuristicStopCondition>(this.sufficientStopConditions);
        P.necessaryStopConditions = new HashSet<HeuristicStopCondition>(this.necessaryStopConditions);
        P.stopCriterionSteps = this.stopCriterionSteps;
        P.stopCriterionTime = this.stopCriterionTime;
        P.stopCriterionAutoSteps = this.stopCriterionAutoSteps;
        P.stopCriterionAutoThreshold = this.stopCriterionAutoThreshold;
        P.stopCriterionConsensusMRE = this.stopCriterionConsensusMRE;
        P.stopCriterionConsensusGeneration = this.stopCriterionConsensusGeneration;
        P.stopCriterionConsensusInterval = this.stopCriterionConsensusInterval;
        P.replicatesStopCondition = this.replicatesStopCondition;
        P.replicatesMRE = this.replicatesMRE;
        P.replicatesNumber = this.replicatesNumber;
        P.replicatesMinimum = this.replicatesMinimum;
        P.replicatesMaximum = this.replicatesMaximum;
        P.replicatesInterval = this.replicatesInterval;
        P.replicatesParallel = this.replicatesParallel;
        P.logFiles = new HashSet<LogFile>(this.logFiles);
        P.gridReplicate = this.gridReplicate;
        P.cloudOutput = this.cloudOutput;
        P.useGrid = this.useGrid;
        P.gridServer = this.gridServer;
        P.gridClient = this.gridClient;
        P.gridModule = this.gridModule;
        if (this.charactersBlock == P.charactersBlock) {
            P.outgroup = new HashSet<String>(this.outgroup);
            P.deletedTaxa = new HashSet<String>(this.deletedTaxa);
            P.charsets.addCharsets(this.charsets.getCharsets());
            P.charsets.replaceExcludedCharsets(this.charsets.getExcludedCharsets());
            P.charsets.replacePartitionCharsets(this.charsets.getPartitions());
            for (final Charset key : this.specificRateParameters.keySet()) {
                final Map<RateParameter, Double> map = new TreeMap<RateParameter, Double>();
                map.putAll(this.specificRateParameters.get(key));
                P.specificRateParameters.put(key, map);
            }
            P.specificDistributionShapes = new HashMap<Charset, Double>(this.specificDistributionShapes);
            P.specificsPInvs = new HashMap<Charset, Double>(this.specificsPInvs);
            if (dupDataset) {
                P.assignPartitionColors();
                P.buildDataset();
            }
            else {
                P.dataset = this.dataset;
            }
            P.loadedTrees = new HashMap<String, TreesBlock.NewickTreeString>(this.loadedTrees);
            P.loadedTreesTranslation = new HashMap<String, String>(this.loadedTreesTranslation);
            P.userSelectionTree = new ArrayList<String>(this.userSelectionTree);
            if (P.startingTreeGeneration == StartingTreeGeneration.GIVEN) {
                P.setStartingTrees();
            }
        }
    }
    
    public boolean isDatasetModified() {
        return !this.deletedTaxa.isEmpty() || !this.charsets.isExcludedCharsetsEmpty();
    }
    
    public Optimizer getOptimizer(final Tree tree) throws BranchNotFoundException {
        Optimizer optimizer = null;
        switch (this.optimizationAlgorithm) {
            case DFO: {
                optimizer = new DFO();
                break;
            }
            case POWELL: {
                optimizer = new Powell(tree, tree.getBranches(), this.optimizationTargets);
                break;
            }
            default: {
                optimizer = new GA(tree, this.optimizationTargets);
                break;
            }
        }
        return optimizer;
    }
    

    
    private void preparePtxFile() {
        final String kernelFilePath = "/native_lib/likelihoodKernel.ptx";
        final String kernelFolderPath = System.getProperty("user.dir");
        this.ptxFilePath = "";
        try {
            this.ptxFilePath = CudaTools.preparePtxFile(String.valueOf(kernelFolderPath) + kernelFilePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public CodonTransitionTable getCodonTransitionTable() {
        return this.codonTransitionTable;
    }
    
    public CodonTransitionTableType getCodonTaransitionTableType() {
        final CodonTransitionTable tab = this.getCodonTransitionTable();
        if (tab instanceof UniversalCodonTransitionTable) {
            return CodonTransitionTableType.UNIVERSAL;
        }
        if (tab instanceof VertebrateMitochondrialCode) {
            return CodonTransitionTableType.VERTMITOCH;
        }
        if (tab instanceof MoldProtoCoelMitochCode) {
            return CodonTransitionTableType.MPCMMITOCH;
        }
        if (tab instanceof InvertebrateMitochondrialCode) {
            return CodonTransitionTableType.INVERTMITOCH;
        }
        if (tab instanceof CDHNuclearCode) {
            return CodonTransitionTableType.CDHNNUC;
        }
        if (tab instanceof EchinodermFlatwormMitochCode) {
            return CodonTransitionTableType.EFMITOCH;
        }
        if (tab instanceof EuploidNuclearCode) {
            return CodonTransitionTableType.EUPLOTIDNUC;
        }
        assert false : "Unknown codon transition table";
        return CodonTransitionTableType.NONE;
    }
    
    private void setCodonTransitionTable(final CodonTransitionTable codonTransitionTable) {
        this.codonTransitionTable = codonTransitionTable;
    }
    
    public CodonTransitionTableType getCurrentCodonTable() {
        return this.currentDNAtable;
    }
    
    static /* synthetic */ void access$1(final Parameters parameters, final boolean hasGPU) {
        parameters.hasGPU = hasGPU;
    }
    
    public enum FileFormat
    {
        NEXUS("NEXUS", 0), 
        FASTA("FASTA", 1);
        
        private FileFormat(final String s, final int n) {
        }
    }
    
    public enum ColumnRemoval
    {
        NONE("NONE", 0), 
        GAP("GAP", 1), 
        NGAP("NGAP", 2);
        
        private ColumnRemoval(final String s, final int n) {
        }
    }
    
    public enum DistanceModel
    {
        GTR2("GTR2", 0, "General-Time-Reversible model for standard binary data", DataType.STANDARD), 
        GTR20("GTR20", 1, "General-Time-Reversible model for proteins", DataType.PROTEIN), 
        POISSON("POISSON", 2, "Poisson model, (Bishop & Friday, in Molecules and morphology in evolution: conflict or compromise?, Cambridge University Press, Cambridge (1987))", DataType.PROTEIN), 
        GTR("GTR", 3, "General-Time-Reversible model for nucleotides", DataType.DNA), 
        TN93("TN93", 4, "Tamura-Nei 1993 model, (Tamura & Nei, Mol. Biol. Evol. 10:512-526 (1993))", DataType.DNA), 
        HKY85("HKY85", 5, "Hasegewa-Kishino-Yano 1985 model, (Hasegewa, Kishino & Yano, J. Mol. Evol. 22:160-174 (1985))", DataType.DNA), 
        K2P("K2P", 6, "Kimura's 2 Parameter model, (Kimura, J. Mol. Evol. 16:111-120 (1980))", DataType.DNA), 
        JC("JC", 7, "Jukes Cantor 1969 model, (Jukes & Cantor, in Mammalian protein metabolism, vol. III, Academic Press, New York (1969))", DataType.DNA), 
        UNCORRECTED("UNCORRECTED", 8, "Uncorrected distances (no substitution model)", (DataType)null), 
        ABSOLUTE("ABSOLUTE", 9, "Absolute number of differences (no substitution model)", (DataType)null), 
        GY("GY", 10, "Goldamn-Yang model for codons", DataType.CODON), 
        GTR64("GTR64", 11, "General-Time-Reversible model for codons", DataType.CODON), 
        NONE("NONE", 12, "Distances are not used", (DataType)null);
        
        private final String name;
        private final DataType dataType;
        
        private DistanceModel(final String s, final int n, final String name, final DataType dataType) {
            this.name = name;
            this.dataType = dataType;
        }
        
        public String verbose() {
            return this.name;
        }
        
        public DataType getDataType() {
            return this.dataType;
        }
    }
    
    public enum StartingTreeDistribution
    {
        NONE("NONE", 0), 
        GAMMA("GAMMA", 1), 
        VDP("VDP", 2);
        
        private StartingTreeDistribution(final String s, final int n) {
        }
    }
    
    public enum StartingTreeGeneration
    {
        NJ("NJ", 0, "Neighbor Joining"), 
        LNJ("LNJ", 1, "Loose Neighbor Joining"), 
        RANDOM("RANDOM", 2, "True Random"), 
        GIVEN("GIVEN", 3, "User tree(s)");
        
        private final String name;
        
        private StartingTreeGeneration(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum StartingTreePInvPi
    {
        EQUAL("EQUAL", 0), 
        ESTIMATED("ESTIMATED", 1), 
        CONSTANT("CONSTANT", 2);
        
        private StartingTreePInvPi(final String s, final int n) {
        }
    }
    
    public enum CPConsensus
    {
        STRICT("STRICT", 0), 
        STOCHASTIC("STOCHASTIC", 1);
        
        private CPConsensus(final String s, final int n) {
        }
    }
    
    public enum CPOperator
    {
        BLIND("BLIND", 0), 
        SUPERVISED("SUPERVISED", 1);
        
        private CPOperator(final String s, final int n) {
        }
    }
    
    public enum CPOperatorChange
    {
        STEP("STEP", 0), 
        POP("POP", 1), 
        IND("IND", 2);
        
        private CPOperatorChange(final String s, final int n) {
        }
    }
    
    public enum CPSelection
    {
        RANK("RANK", 0), 
        TOURNAMENT("TOURNAMENT", 1), 
        REPLACEMENT("REPLACEMENT", 2), 
        IMPROVE("IMPROVE", 3), 
        KEEPBEST("KEEPBEST", 4);
        
        private CPSelection(final String s, final int n) {
        }
    }
    
    public enum CodonTransitionTableType
    {
        UNIVERSAL("UNIVERSAL", 0, "Universal Codon Table"), 
        CDHNNUC("CDHNNUC", 1, "The Ciliate, Dasycladacean and Hexamita Nuclear Code"), 
        EFMITOCH("EFMITOCH", 2, "The Echinoderm and Flatworm Mitochondrial Code"), 
        EUPLOTIDNUC("EUPLOTIDNUC", 3, "The Euplotid Nuclear Code"), 
        INVERTMITOCH("INVERTMITOCH", 4, "The Invertebrate Mitochondrial Code"), 
        MPCMMITOCH("MPCMMITOCH", 5, "The Mold, Protozoan, Coelenterate Mitoch. & Myco/Spiroplasma Code"), 
        VERTMITOCH("VERTMITOCH", 6, "The Vertebrate Mitochondrial Code"), 
        NONE("NONE", 7, "None");
        
        private final String name;
        
        private CodonTransitionTableType(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum EvaluationDistribution
    {
        NONE("NONE", 0), 
        GAMMA("GAMMA", 1), 
        VDP("VDP", 2);
        
        private EvaluationDistribution(final String s, final int n) {
        }
    }
    
    public enum EvaluationModel
    {
        GTR2("GTR2", 0, "General-Time-Reversible model for standard binary data", 0, false, false, DataType.STANDARD), 
        GTR20("GTR20", 1, "General-Time-Reversible model for proteins", 189, false, false, DataType.PROTEIN), 
        GY("GY", 2, "GY model for codon substitution (Goldman, Nick, and Ziheng Yang. Molecular biology and evolution 11.5 (1994): 725-736)", 2, false, false, DataType.CODON), 
        GTR64("GTR64", 3, "General-Time-Reversible model for codons", 2015, false, false, DataType.CODON), 
        ECM("ECM", 4, "Empirical Codon Model, (Kosiol, Holmes & Goldman, Mol. Biol. Evol. 24(7):1464-1479 (2007)", 2015, false, true, DataType.CODON), 
        WAG("WAG", 5, "Wheland and Goldman model, (Wheland and Goldman, Mol. Biol. Evol. 18:691-699 (2001))", 189, false, true, DataType.PROTEIN), 
        JTT("JTT", 6, "Jones-Taylor-Thornton model, (Jones, Taylor & Thornton, Comput. Appl. Biosci. 8:275-282 (1992))", 189, false, true, DataType.PROTEIN), 
        DAYHOFF("DAYHOFF", 7, "Dayhoff model, (Dayhoff, Schwartz and Orcutt, in Atlas of protein sequence and structure. Vol. V, Suppl. 3, National Biomedical Research Foundation, Washington, D.C. (1978))", 189, false, true, DataType.PROTEIN), 
        VT("VT", 8, "Variable Time substitution matrix, (Muller and Vingron, J. Comp. Biol. 7:761-776 (2000))", 189, false, true, DataType.PROTEIN), 
        BLOSUM62("BLOSUM62", 9, "BLOSUM62 (BLOcks of amino acid SUbstitution Matrix) substitution matrix, (Henikoff and Henikoff, Proc. Natl. Acad. Sci., U.S.A. 89:10915-10919 (1992))", 189, false, true, DataType.PROTEIN), 
        CPREV("CPREV", 10, "General Reversible Chloroplast model, (Adachi, Waddell, Martin & Hasegawa, J. Mol. Evol. 50:348-358 (2000))", 189, false, true, DataType.PROTEIN), 
        MTREV("MTREV", 11, "General Reversible Mitochondrial model, (Adachi and Hasegawa, Computer Science Monographs of Institute of Statistical Mathematics 28:1-150 (1996))", 189, false, true, DataType.PROTEIN), 
        RTREV("RTREV", 12, "General Reverse Transcriptase model, (Dimmic, Rest, Mindell & Goldstein, J. Mol. Evol. 55:65-73 (2002))", 189, false, true, DataType.PROTEIN), 
        MTMAM("MTMAM", 13, "Mammal Mitochondrial model, (Yang, Nielsen & Hasegawa, Mol. Biol. Evol. 15(12):1600-11 (1998))", 189, false, true, DataType.PROTEIN), 
        POISSON("POISSON", 14, "Poisson model, (Bishop & Friday, in Molecules and morphology in evolution: conflict or compromise?, Cambridge University Press, Cambridge (1987))", 0, true, false, DataType.PROTEIN), 
        GTR("GTR", 15, "General-Time-Reversible model for nucleotides", 5, false, false, DataType.DNA), 
        TN93("TN93", 16, "Tamura-Nei 1993 model, (Tamura & Nei, Mol. Biol. Evol. 10:512-526 (1993))", 2, false, false, DataType.DNA), 
        HKY85("HKY85", 17, "Hasegewa-Kishino-Yano 1985 model, (Hasegewa, Kishino & Yano, J. Mol. Evol. 22:160-174 (1985))", 1, false, false, DataType.DNA), 
        K2P("K2P", 18, "Kimura's 2 Parameter model, (Kimura, J. Mol. Evol. 16:111-120 (1980))", 1, true, false, DataType.DNA), 
        JC("JC", 19, "Jukes Cantor 1969 model, (Jukes & Cantor, in Mammalian protein metabolism, vol. III, Academic Press, New York (1969))", 0, true, false, DataType.DNA);
        
        private final String name;
        private final int rateParameters;
        private final boolean hasEqualBaseFrequencies;
        private final boolean isEmpirical;
        private final DataType dataType;
        
        private EvaluationModel(final String s, final int n, final String name, final int rateParam, final boolean hasEqBF, final boolean isEmp, final DataType dataType) {
            this.name = name;
            this.rateParameters = rateParam;
            this.hasEqualBaseFrequencies = hasEqBF;
            this.isEmpirical = isEmp;
            this.dataType = dataType;
        }
        
        public int getNumRateParameters() {
            return this.rateParameters;
        }
        
        public boolean hasEqualBaseFrequencies() {
            return this.hasEqualBaseFrequencies;
        }
        
        public boolean isEmpirical() {
            return this.isEmpirical;
        }
        
        public DataType getDataType() {
            return this.dataType;
        }
        
        public static EvaluationModel[] availableModels(final DataType dataType) {
            final Set<EvaluationModel> set = new TreeSet<EvaluationModel>();
            EvaluationModel[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                final EvaluationModel p = values[i];
                if (p.dataType == dataType) {
                    set.add(p);
                }
            }
            if (set.contains(EvaluationModel.GTR64)) {
                set.remove(EvaluationModel.GTR64);
            }
            return set.toArray(new EvaluationModel[0]);
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum EvaluationRate
    {
        BRANCH("BRANCH", 0), 
        TREE("TREE", 1);
        
        private EvaluationRate(final String s, final int n) {
        }
    }
    
    public enum EvaluationStateFrequencies
    {
        EMPIRICAL("EMPIRICAL", 0), 
        ESTIMATED("ESTIMATED", 1);
        
        private EvaluationStateFrequencies(final String s, final int n) {
        }
    }
    
    public enum GAOperatorChange
    {
        STEP("STEP", 0), 
        IND("IND", 1);
        
        private GAOperatorChange(final String s, final int n) {
        }
    }
    
    public enum GASelection
    {
        RANK("RANK", 0), 
        TOURNAMENT("TOURNAMENT", 1), 
        REPLACEMENT("REPLACEMENT", 2), 
        IMPROVE("IMPROVE", 3), 
        KEEPBEST("KEEPBEST", 4);
        
        private GASelection(final String s, final int n) {
        }
    }
    
    public enum Heuristic
    {
        HC("HC", 0, "Hill Climbing"), 
        SA("SA", 1, "Simulated Annealing"), 
        GA("GA", 2, "Genetic Algorithm"), 
        CP("CP", 3, "Consensus Pruning (metaGA)"), 
        BS("BS", 4, "Bootstrapping");
        
        private final String name;
        
        private Heuristic(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum HeuristicStopCondition
    {
        STEPS("STEPS", 0), 
        TIME("TIME", 1), 
        AUTO("AUTO", 2), 
        CONSENSUS("CONSENSUS", 3);
        
        private HeuristicStopCondition(final String s, final int n) {
        }
    }
    
    public enum LikelihoodCalculationType
    {
        CLASSIC("CLASSIC", 0, "Classic likelihood calculation with loops"), 
        GPU("GPU", 1, "Likelihood calculation using GPU");
        
        private final String name;
        
        private LikelihoodCalculationType(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum LogFile
    {
        DATA("DATA", 0, "Working matrix log file"), 
        DIST("DIST", 1, "Distance matrix log file"), 
        TREESTART("TREESTART", 2, "Starting Tree log file"), 
        HEUR("HEUR", 3, "Heuristic search log file"), 
        TREEHEUR("TREEHEUR", 4, "Heuristic search tree file"), 
        CONSENSUS("CONSENSUS", 5, "Consensus log file"), 
        OPDETAILS("OPDETAILS", 6, "Operators log file"), 
        OPSTATS("OPSTATS", 7, "Operator statistics file"), 
        ANCSEQ("ANCSEQ", 8, "Ancestral sequences log file"), 
        PERF("PERF", 9, "Perfomances log file");
        
        private final String name;
        
        private LogFile(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum Operator
    {
        NNI("NNI", 0, "Nearest Neighbor Interchange"), 
        SPR("SPR", 1, "Subtree Pruning and Regrafting"), 
        TBR("TBR", 2, "Tree Bisection Reconnection"), 
        TXS("TXS", 3, "TaXa Swap"), 
        STS("STS", 4, "SubTree Swap"), 
        BLM("BLM", 5, "Branch Length Mutation"), 
        BLMINT("BLMINT", 6, "Branch Length Mutation on INTernal branches only"), 
        RPM("RPM", 7, "Rate Parameters Mutation"), 
        GDM("GDM", 8, "Gamma Distribution Mutation"), 
        PIM("PIM", 9, "Proportion of Invariant Mutation"), 
        APRM("APRM", 10, "Among-Partition Rate Mutation");
        
        private final String name;
        
        private Operator(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum OperatorSelection
    {
        RANDOM("RANDOM", 0), 
        ORDERED("ORDERED", 1), 
        FREQLIST("FREQLIST", 2);
        
        private OperatorSelection(final String s, final int n) {
        }
    }
    
    public enum Optimization
    {
        NEVER("NEVER", 0), 
        CONSENSUSTREE("CONSENSUSTREE", 1), 
        ENDSEARCH("ENDSEARCH", 2), 
        STOCH("STOCH", 3), 
        DISC("DISC", 4);
        
        private Optimization(final String s, final int n) {
        }
    }
    
    public enum OptimizationAlgorithm
    {
        GA("GA", 0, "Genetic Algorithm"), 
        POWELL("POWELL", 1, "Powell's method"), 
        DFO("DFO", 2, "Derivative-Free Optimization");
        
        private final String name;
        
        private OptimizationAlgorithm(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum OptimizationTarget
    {
        BL("BL", 0, "Branch lengths"), 
        R("R", 1, "Rate matrix parameters"), 
        GAMMA("GAMMA", 2, "Shape of Gamma distribution"), 
        PINV("PINV", 3, "Proportion of invariable sites"), 
        APRATE("APRATE", 4, "Among-partition rate variation");
        
        private final String name;
        
        private OptimizationTarget(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public enum ReplicatesStopCondition
    {
        NONE("NONE", 0), 
        MRE("MRE", 1);
        
        private ReplicatesStopCondition(final String s, final int n) {
        }
    }
    
    public enum SACooling
    {
        STEPS("STEPS", 0), 
        SF("SF", 1);
        
        private SACooling(final String s, final int n) {
        }
    }
    
    public enum SADeltaL
    {
        PERCENT("PERCENT", 0), 
        BURNIN("BURNIN", 1);
        
        private SADeltaL(final String s, final int n) {
        }
    }
    
    public enum SAReheating
    {
        DECREMENTS("DECREMENTS", 0), 
        THRESHOLD("THRESHOLD", 1), 
        NEVER("NEVER", 2);
        
        private SAReheating(final String s, final int n) {
        }
    }
    
    public enum SASchedule
    {
        LUNDY("LUNDY", 0, "Lundy"), 
        CAUCHY("CAUCHY", 1, "Fast Cauchy"), 
        BOLTZMANN("BOLTZMANN", 2, "Boltzmann"), 
        GEOM("GEOM", 3, "Geometric"), 
        RP("RP", 4, "Ratio-Percent"), 
        LIN("LIN", 5, "Linear"), 
        TRI("TRI", 6, "Triangular"), 
        POLY("POLY", 7, "Polynomial"), 
        EXP("EXP", 8, "Transcendental (exponential)"), 
        LOG("LOG", 9, "Transcendental (logarithmic)"), 
        PER("PER", 10, "Transcendental (periodic)"), 
        SPER("SPER", 11, "Transcendental (smoothed periodic)"), 
        TANH("TANH", 12, "Hyperbolic (tangent)"), 
        COSH("COSH", 13, "Hyperbolic (cosinus)");
        
        private final String name;
        
        private SASchedule(final String s, final int n, final String name) {
            this.name = name;
        }
        
        public String verbose() {
            return this.name;
        }
    }
    
    public class CodonDomainDefinition
    {
        private final int startCodonDomainPosition;
        private final int endCodonDomainPosition;
        private final int domainSize;
        private final int domainSizeInCodons;
        
        public CodonDomainDefinition(final int startPosition, final int endPosition) {
            if (startPosition + 2 > endPosition || startPosition < 1 || endPosition > Parameters.this.charactersBlock.getDimensionsNChar()) {
                throw new IndexOutOfBoundsException("Codon domain definitions out of bounds");
            }
            this.startCodonDomainPosition = startPosition;
            this.endCodonDomainPosition = Parameters.this.calibrateLastDomainPosition(startPosition, endPosition);
            this.domainSize = Parameters.this.domainSize(startPosition, endPosition);
            this.domainSizeInCodons = Parameters.this.domainSize(startPosition, endPosition) / 3;
        }
        
        public int getStartCodonDomainPosition() {
            return this.startCodonDomainPosition;
        }
        
        public int getEndCodonDomainPosition() {
            return this.endCodonDomainPosition;
        }
        
        public int getDomainSize() {
            return this.domainSize;
        }
        
        public int getDimensionsNChar() {
            return this.domainSizeInCodons;
        }
        
        public boolean isPositionInCodonDomain(final int pos) {
            return pos >= this.startCodonDomainPosition && pos <= this.endCodonDomainPosition;
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

import java.io.IOException;
import java.util.Iterator;
import metapiga.utilities.Tools;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import metapiga.modelization.data.EmpiricalModels;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.EnumSet;
import java.util.TreeMap;
import java.util.EnumMap;
import metapiga.modelization.data.DataType;
import java.util.List;
import java.util.Set;
import metapiga.modelization.Charset;
import metapiga.RateParameter;
import java.util.Map;
import metapiga.parameters.Parameters;

public class MetapigaBlock extends NexusBlock.Abstract
{
    public static final String METAPIGA_BLOCK = "METAPIGA";
    private boolean useComplexNum;
    private Parameters.Heuristic heuristic;
    private int hcRestart;
    private Parameters.SASchedule saSchedule;
    private double saScheduleParam;
    private double saLundyC;
    private double saLundyAlpha;
    private double saInitAccept;
    private double saFinalAccept;
    private Parameters.SADeltaL saDeltaL;
    private double saDeltaLPercent;
    private Parameters.SAReheating saReheatingType;
    private double saReheatingValue;
    private Parameters.SACooling saCoolingType;
    private int saCoolingSteps;
    private int saCoolingSuccesses;
    private int saCoolingFailures;
    private int gaIndNum;
    private Parameters.GASelection gaSelection;
    private double gaReplacementStrength;
    private double gaRecombination;
    private Parameters.GAOperatorChange gaOperatorChange;
    private Parameters.CPConsensus cpConsensus;
    private Parameters.CPOperator cpOperator;
    private int cpPopNum;
    private int cpIndNum;
    private double cpTolerance;
    private double cpHybridization;
    private Parameters.CPSelection cpSelection;
    private double cpReplacementStrength;
    private double cpRecombination;
    private Parameters.CPOperatorChange cpOperatorChange;
    private int cpCoreNum;
    private Parameters.EvaluationRate evaluationRate;
    private Parameters.EvaluationModel evaluationModel;
    private Map<RateParameter, Double> evaluationRateParameters;
    private Parameters.EvaluationStateFrequencies evaluationStateFrequencies;
    private Parameters.EvaluationDistribution evaluationDistribution;
    private double evaluationDistributionShape;
    private int evaluationDistributionSubsets;
    private double evaluationPInv;
    private Map<Charset, Map<RateParameter, Double>> specificRateParameters;
    private Map<Charset, Double> specificDistributionShapes;
    private Map<Charset, Double> specificsPInvs;
    private Parameters.Optimization optimization;
    private double optimizationUse;
    private Parameters.OptimizationAlgorithm optimizationAlgorithm;
    private Set<Parameters.OptimizationTarget> optimizationTargets;
    private Parameters.StartingTreeGeneration startingTreeGeneration;
    private double startingTreeGenerationRange;
    private Parameters.DistanceModel startingTreeModel;
    private Parameters.StartingTreeDistribution startingTreeDistribution;
    private double startingTreeDistributionShape;
    private double startingTreePInv;
    private Parameters.StartingTreePInvPi startingTreePInvPi;
    private List<Parameters.Operator> operators;
    private Map<Parameters.Operator, Integer> operatorsParameters;
    private Map<Parameters.Operator, Double> operatorsFrequencies;
    private Set<Parameters.Operator> operatorIsDynamic;
    private int dynamicInterval;
    private double dynamicMin;
    private Parameters.OperatorSelection operatorSelection;
    private Parameters.ColumnRemoval columnRemoval;
    private Parameters.ReplicatesStopCondition replicatesStopCondition;
    private double replicatesMRE;
    private int replicatesNum;
    private int replicatesMin;
    private int replicatesMax;
    private int replicatesInterval;
    private int replicatesParallel;
    private String label;
    private String outputDir;
    private Set<String> outgroup;
    private Set<String> deletedTaxa;
    private Set<Charset> charsets;
    private Set<Charset> excludedCharsets;
    private Set<Charset> partitions;
    private Set<Parameters.HeuristicStopCondition> sufficientStopConditions;
    private Set<Parameters.HeuristicStopCondition> necessaryStopConditions;
    private int stopCriterionSteps;
    private double stopCriterionTime;
    private int stopCriterionAutoSteps;
    private double stopCriterionAutoThreshold;
    private double stopCriterionConsensusMRE;
    private int stopCriterionConsensusGeneration;
    private int stopCriterionConsensusInterval;
    private boolean stopCriterionSet;
    private Set<Parameters.LogFile> logFiles;
    private boolean gridReplicate;
    private String gridOutput;
    public boolean useGrid;
    public String gridServer;
    public String gridClient;
    public String gridModule;
    public boolean cloudReplicate;
    public String cloudOutput;
    public boolean useCloud;
    public String cloudServer;
    public String cloudClient;
    public String cloudModule;
    private Parameters.LikelihoodCalculationType likelihoodCalculationType;
    private DataType dataType;
    private int startCodonDomainPosition;
    private int endCodonDomainPosition;
    private Parameters.CodonTransitionTableType codonTable;
    private String[] notRecordingCharsets;
    private List<NexusComment> comments;
    
    public MetapigaBlock() {
        super("METAPIGA");
        this.useComplexNum = false;
        this.heuristic = Parameters.Heuristic.CP;
        this.hcRestart = 0;
        this.saSchedule = Parameters.SASchedule.LUNDY;
        this.saScheduleParam = 0.5;
        this.saLundyC = 0.5;
        this.saLundyAlpha = 0.5;
        this.saInitAccept = 0.7;
        this.saFinalAccept = 0.01;
        this.saDeltaL = Parameters.SADeltaL.BURNIN;
        this.saDeltaLPercent = 0.001;
        this.saReheatingType = Parameters.SAReheating.DECREMENTS;
        this.saReheatingValue = 300.0;
        this.saCoolingType = Parameters.SACooling.SF;
        this.saCoolingSteps = 20;
        this.saCoolingSuccesses = 10;
        this.saCoolingFailures = 100;
        this.gaIndNum = 8;
        this.gaSelection = Parameters.GASelection.TOURNAMENT;
        this.gaReplacementStrength = 1.0;
        this.gaRecombination = 0.1;
        this.gaOperatorChange = Parameters.GAOperatorChange.IND;
        this.cpConsensus = Parameters.CPConsensus.STOCHASTIC;
        this.cpOperator = Parameters.CPOperator.SUPERVISED;
        this.cpPopNum = 4;
        this.cpIndNum = 4;
        this.cpTolerance = 0.05;
        this.cpHybridization = 0.1;
        this.cpSelection = Parameters.CPSelection.IMPROVE;
        this.cpReplacementStrength = 1.0;
        this.cpRecombination = 0.1;
        this.cpOperatorChange = Parameters.CPOperatorChange.IND;
        this.cpCoreNum = 1;
        this.evaluationRate = Parameters.EvaluationRate.TREE;
        this.evaluationModel = Parameters.EvaluationModel.JC;
        this.evaluationRateParameters = new EnumMap<RateParameter, Double>(RateParameter.class);
        this.evaluationStateFrequencies = Parameters.EvaluationStateFrequencies.EMPIRICAL;
        this.evaluationDistribution = Parameters.EvaluationDistribution.NONE;
        this.evaluationDistributionShape = 1.0;
        this.evaluationDistributionSubsets = 4;
        this.evaluationPInv = 0.0;
        this.specificRateParameters = new TreeMap<Charset, Map<RateParameter, Double>>();
        this.specificDistributionShapes = new TreeMap<Charset, Double>();
        this.specificsPInvs = new TreeMap<Charset, Double>();
        this.optimization = Parameters.Optimization.CONSENSUSTREE;
        this.optimizationUse = 0.0;
        this.optimizationAlgorithm = Parameters.OptimizationAlgorithm.GA;
        this.optimizationTargets = EnumSet.noneOf(Parameters.OptimizationTarget.class);
        this.startingTreeGeneration = Parameters.StartingTreeGeneration.LNJ;
        this.startingTreeGenerationRange = 0.1;
        this.startingTreeModel = Parameters.DistanceModel.JC;
        this.startingTreeDistribution = Parameters.StartingTreeDistribution.NONE;
        this.startingTreeDistributionShape = 0.5;
        this.startingTreePInv = 0.0;
        this.startingTreePInvPi = Parameters.StartingTreePInvPi.CONSTANT;
        this.operators = new LinkedList<Parameters.Operator>();
        this.operatorsParameters = new EnumMap<Parameters.Operator, Integer>(Parameters.Operator.class);
        this.operatorsFrequencies = new EnumMap<Parameters.Operator, Double>(Parameters.Operator.class);
        this.operatorIsDynamic = EnumSet.noneOf(Parameters.Operator.class);
        this.dynamicInterval = 100;
        this.dynamicMin = 0.04;
        this.operatorSelection = Parameters.OperatorSelection.RANDOM;
        this.columnRemoval = Parameters.ColumnRemoval.NONE;
        this.replicatesStopCondition = Parameters.ReplicatesStopCondition.MRE;
        this.replicatesMRE = 0.05;
        this.replicatesNum = 1;
        this.replicatesMin = 100;
        this.replicatesMax = 10000;
        this.replicatesInterval = 10;
        this.replicatesParallel = 1;
        this.label = null;
        this.outputDir = null;
        this.outgroup = new TreeSet<String>();
        this.deletedTaxa = new TreeSet<String>();
        this.charsets = new TreeSet<Charset>();
        this.excludedCharsets = new TreeSet<Charset>();
        this.partitions = new TreeSet<Charset>();
        this.sufficientStopConditions = new HashSet<Parameters.HeuristicStopCondition>();
        this.necessaryStopConditions = new HashSet<Parameters.HeuristicStopCondition>();
        this.stopCriterionSteps = 0;
        this.stopCriterionTime = 0.0;
        this.stopCriterionAutoSteps = 200;
        this.stopCriterionAutoThreshold = 1.0E-4;
        this.stopCriterionConsensusMRE = 0.05;
        this.stopCriterionConsensusGeneration = 5;
        this.stopCriterionConsensusInterval = 10;
        this.stopCriterionSet = false;
        this.logFiles = EnumSet.noneOf(Parameters.LogFile.class);
        this.gridReplicate = false;
        this.gridOutput = "";
        this.useGrid = false;
        this.gridServer = "";
        this.gridClient = "";
        this.gridModule = "";
        this.cloudReplicate = false;
        this.cloudOutput = "";
        this.useCloud = false;
        this.cloudServer = "";
        this.cloudClient = "";
        this.cloudModule = "";
        this.likelihoodCalculationType = Parameters.LikelihoodCalculationType.CLASSIC;
        this.codonTable = Parameters.CodonTransitionTableType.UNIVERSAL;
        this.notRecordingCharsets = new String[] { "Stop_codons", "Ambiguous_codons" };
        this.comments = new ArrayList<NexusComment>();
        RateParameter[] values;
        for (int length = (values = RateParameter.values()).length, i = 0; i < length; ++i) {
            final RateParameter r = values[i];
            this.evaluationRateParameters.put(r, 0.5);
        }
    }
    
    public void setUseComplexNum(final boolean useComplexNum) {
        this.useComplexNum = useComplexNum;
    }
    
    public boolean getUseComplexNum() {
        return this.useComplexNum;
    }
    
    public void setHeuristic(final Parameters.Heuristic heuristic) {
        this.heuristic = heuristic;
    }
    
    public Parameters.Heuristic getHeuristic() {
        return this.heuristic;
    }
    
    public void setHcRestart(final int hcRestart) {
        this.hcRestart = hcRestart;
    }
    
    public int getHcRestart() {
        return this.hcRestart;
    }
    
    public void setSaSchedule(final Parameters.SASchedule saSchedule) {
        this.saSchedule = saSchedule;
    }
    
    public Parameters.SASchedule getSaSchedule() {
        return this.saSchedule;
    }
    
    public void setSaScheduleParam(final double saScheduleParam) {
        this.saScheduleParam = saScheduleParam;
    }
    
    public double getSaScheduleParam() {
        return this.saScheduleParam;
    }
    
    public void setSaLundyC(final double saLundyC) {
        this.saLundyC = saLundyC;
    }
    
    public double getSaLundyC() {
        return this.saLundyC;
    }
    
    public void setSaLundyAlpha(final double saLundyAlpha) {
        this.saLundyAlpha = saLundyAlpha;
    }
    
    public double getSaLundyAlpha() {
        return this.saLundyAlpha;
    }
    
    public void setSaInitAccept(final double saInitAccept) {
        this.saInitAccept = saInitAccept;
    }
    
    public double getSaInitAccept() {
        return this.saInitAccept;
    }
    
    public void setSaFinalAccept(final double saFinalAccept) {
        this.saFinalAccept = saFinalAccept;
    }
    
    public double getSaFinalAccept() {
        return this.saFinalAccept;
    }
    
    public void setSaDeltaL(final Parameters.SADeltaL saDeltaL) {
        this.saDeltaL = saDeltaL;
    }
    
    public Parameters.SADeltaL getSaDeltaL() {
        return this.saDeltaL;
    }
    
    public void setSaDeltaLPercent(final double saDeltaLPercent) {
        this.saDeltaLPercent = saDeltaLPercent;
    }
    
    public double getSaDeltaLPercent() {
        return this.saDeltaLPercent;
    }
    
    public void setSaReheatingType(final Parameters.SAReheating saReheatingType) {
        this.saReheatingType = saReheatingType;
    }
    
    public Parameters.SAReheating getSaReheatingType() {
        return this.saReheatingType;
    }
    
    public void setSaReheatingValue(final double saReheatingValue) {
        this.saReheatingValue = saReheatingValue;
    }
    
    public double getSaReheatingValue() {
        return this.saReheatingValue;
    }
    
    public void setSaCoolingType(final Parameters.SACooling saCoolingType) {
        this.saCoolingType = saCoolingType;
    }
    
    public Parameters.SACooling getSaCoolingType() {
        return this.saCoolingType;
    }
    
    public void setSaCoolingSteps(final int saCoolingSteps) {
        this.saCoolingSteps = saCoolingSteps;
    }
    
    public int getSaCoolingSteps() {
        return this.saCoolingSteps;
    }
    
    public void setSaCoolingSuccesses(final int saCoolingSuccesses) {
        this.saCoolingSuccesses = saCoolingSuccesses;
    }
    
    public int getSaCoolingSuccesses() {
        return this.saCoolingSuccesses;
    }
    
    public void setSaCoolingFailures(final int saCoolingFailures) {
        this.saCoolingFailures = saCoolingFailures;
    }
    
    public int getSaCoolingFailures() {
        return this.saCoolingFailures;
    }
    
    public void setGaIndNum(final int gaIndNum) {
        this.gaIndNum = gaIndNum;
    }
    
    public int getGaIndNum() {
        return this.gaIndNum;
    }
    
    public void setGaSelection(final Parameters.GASelection gaSelection) {
        this.gaSelection = gaSelection;
    }
    
    public Parameters.GASelection getGaSelection() {
        return this.gaSelection;
    }
    
    public void setGaReplacementStrength(final double gaReplacementStrength) {
        this.gaReplacementStrength = gaReplacementStrength;
    }
    
    public double getGaReplacementStrength() {
        return this.gaReplacementStrength;
    }
    
    public void setGaRecombination(final double gaRecombination) {
        this.gaRecombination = gaRecombination;
    }
    
    public double getGaRecombination() {
        return this.gaRecombination;
    }
    
    public void setGaOperatorChange(final Parameters.GAOperatorChange gaOperatorChange) {
        this.gaOperatorChange = gaOperatorChange;
    }
    
    public Parameters.GAOperatorChange getGaOperatorChange() {
        return this.gaOperatorChange;
    }
    
    public void setCpConsensus(final Parameters.CPConsensus cpConsensus) {
        this.cpConsensus = cpConsensus;
    }
    
    public Parameters.CPConsensus getCpConsensus() {
        return this.cpConsensus;
    }
    
    public void setCpOperator(final Parameters.CPOperator cpOperator) {
        this.cpOperator = cpOperator;
    }
    
    public Parameters.CPOperator getCpOperator() {
        return this.cpOperator;
    }
    
    public void setCpPopNum(final int cpPopNum) {
        this.cpPopNum = cpPopNum;
    }
    
    public int getCpPopNum() {
        return this.cpPopNum;
    }
    
    public void setCpIndNum(final int cpIndNum) {
        this.cpIndNum = cpIndNum;
    }
    
    public int getCpIndNum() {
        return this.cpIndNum;
    }
    
    public void setCpTolerance(final double cpTolerance) {
        this.cpTolerance = cpTolerance;
    }
    
    public double getCpTolerance() {
        return this.cpTolerance;
    }
    
    public void setCpHybridization(final double cpHybridization) {
        this.cpHybridization = cpHybridization;
    }
    
    public double getCpHybridization() {
        return this.cpHybridization;
    }
    
    public void setCpSelection(final Parameters.CPSelection cpSelection) {
        this.cpSelection = cpSelection;
    }
    
    public Parameters.CPSelection getCpSelection() {
        return this.cpSelection;
    }
    
    public void setCpReplacementStrength(final double cpReplacementStrength) {
        this.cpReplacementStrength = cpReplacementStrength;
    }
    
    public double getCpReplacementStrength() {
        return this.cpReplacementStrength;
    }
    
    public void setCpRecombination(final double cpRecombination) {
        this.cpRecombination = cpRecombination;
    }
    
    public double getCpRecombination() {
        return this.cpRecombination;
    }
    
    public void setCpOperatorChange(final Parameters.CPOperatorChange cpOperatorChange) {
        this.cpOperatorChange = cpOperatorChange;
    }
    
    public Parameters.CPOperatorChange getCpOperatorChange() {
        return this.cpOperatorChange;
    }
    
    public void setCpCoreNum(final int cpCoreNum) {
        this.cpCoreNum = cpCoreNum;
    }
    
    public int getCpCoreNum() {
        return this.cpCoreNum;
    }
    
    public void setEvaluationRate(final Parameters.EvaluationRate evaluationRate) {
        this.evaluationRate = evaluationRate;
    }
    
    public Parameters.EvaluationRate getEvaluationRate() {
        return this.evaluationRate;
    }
    
    public void setEvaluationModel(final Parameters.EvaluationModel evaluationModel) {
        this.evaluationModel = evaluationModel;
    }
    
    public Parameters.EvaluationModel getEvaluationModel() {
        return this.evaluationModel;
    }
    
    public void addRateParameter(final RateParameter os, final double value) {
        this.evaluationRateParameters.put(os, value);
    }
    
    public void setEmpiricalRateParameters(final Parameters.EvaluationModel model) {
        if (model.isEmpirical()) {
            this.evaluationRateParameters = EmpiricalModels.getRateParameters(model);
        }
    }
    
    public void removeRateParameter(final RateParameter os) {
        this.evaluationRateParameters.remove(os);
    }
    
    public Map<RateParameter, Double> getRateParameter() {
        return new HashMap<RateParameter, Double>(this.evaluationRateParameters);
    }
    
    public void setEvaluationStateFrequencies(final Parameters.EvaluationStateFrequencies evaluationAAFrequency) {
        this.evaluationStateFrequencies = evaluationAAFrequency;
    }
    
    public Parameters.EvaluationStateFrequencies getEvaluationStateFrequencies() {
        return this.evaluationStateFrequencies;
    }
    
    public void setEvaluationDistribution(final Parameters.EvaluationDistribution evaluationDistribution, final int evaluationDistributionSubsets) {
        this.evaluationDistribution = evaluationDistribution;
        this.evaluationDistributionSubsets = evaluationDistributionSubsets;
    }
    
    public void setEvaluationDistribution(final Parameters.EvaluationDistribution evaluationDistribution) {
        this.evaluationDistribution = evaluationDistribution;
    }
    
    public Parameters.EvaluationDistribution getEvaluationDistribution() {
        return this.evaluationDistribution;
    }
    
    public int getEvaluationDistributionSubsets() {
        return this.evaluationDistributionSubsets;
    }
    
    public void setEvaluationDistributionShape(final double evaluationDistributionShape) {
        this.evaluationDistributionShape = evaluationDistributionShape;
    }
    
    public double getEvaluationDistributionShape() {
        return this.evaluationDistributionShape;
    }
    
    public void setEvaluationPInv(final double evaluationPInv) {
        this.evaluationPInv = evaluationPInv;
    }
    
    public double getEvaluationPInv() {
        return this.evaluationPInv;
    }
    
    public void addSpecificRateParameter(final Charset c, final RateParameter os, final double value) {
        Map<RateParameter, Double> map;
        if (this.specificRateParameters.containsKey(c)) {
            map = this.specificRateParameters.get(c);
        }
        else {
            map = new EnumMap<RateParameter, Double>(RateParameter.class);
            RateParameter[] values;
            for (int length = (values = RateParameter.values()).length, i = 0; i < length; ++i) {
                final RateParameter r = values[i];
                map.put(r, 0.5);
            }
        }
        map.put(os, value);
        this.specificRateParameters.put(c, map);
    }
    
    public Set<Charset> getSpecificRateParameterCharsets() {
        return this.specificRateParameters.keySet();
    }
    
    public Map<RateParameter, Double> getSpecificRateParameters(final Charset c) {
        return new HashMap<RateParameter, Double>(this.specificRateParameters.get(c));
    }
    
    public void addSpecificDistributionShape(final Charset c, final Double value) {
        this.specificDistributionShapes.put(c, value);
    }
    
    public Map<Charset, Double> getSpecificDistributionShapes() {
        return new HashMap<Charset, Double>(this.specificDistributionShapes);
    }
    
    public void addSpecificPInv(final Charset c, final Double value) {
        this.specificsPInvs.put(c, value);
    }
    
    public Map<Charset, Double> getSpecificPInvs() {
        return new HashMap<Charset, Double>(this.specificsPInvs);
    }
    
    public void setOptimization(final Parameters.Optimization optimization) {
        this.optimization = optimization;
    }
    
    public Parameters.Optimization getOptimization() {
        return this.optimization;
    }
    
    public void setOptimizationUse(final double optimizationUse) {
        this.optimizationUse = optimizationUse;
    }
    
    public double getOptimizationUse() {
        return this.optimizationUse;
    }
    
    public void setOptimizationAlgorithm(final Parameters.OptimizationAlgorithm optimizationAlgorithm) {
        this.optimizationAlgorithm = optimizationAlgorithm;
    }
    
    public Parameters.OptimizationAlgorithm getOptimizationAlgorithm() {
        return this.optimizationAlgorithm;
    }
    
    public void addOptimizationTarget(final Parameters.OptimizationTarget target) {
        this.optimizationTargets.add(target);
    }
    
    public void removeOptimizationTarget(final Parameters.OptimizationTarget target) {
        this.optimizationTargets.remove(target);
    }
    
    public Set<Parameters.OptimizationTarget> getOptimizationTargets() {
        return new HashSet<Parameters.OptimizationTarget>(this.optimizationTargets);
    }
    
    public void setStartingTreeGeneration(final Parameters.StartingTreeGeneration startingTreeGeneration) {
        this.startingTreeGeneration = startingTreeGeneration;
    }
    
    public Parameters.StartingTreeGeneration getStartingTreeGeneration() {
        return this.startingTreeGeneration;
    }
    
    public void setStartingTreeGenerationRange(final double startingTreeGenerationRange) {
        this.startingTreeGenerationRange = startingTreeGenerationRange;
    }
    
    public double getStartingTreeGenerationRange() {
        return this.startingTreeGenerationRange;
    }
    
    public void setStartingTreeModel(final Parameters.DistanceModel startingTreeModel) {
        this.startingTreeModel = startingTreeModel;
    }
    
    public Parameters.DistanceModel getStartingTreeModel() {
        return this.startingTreeModel;
    }
    
    public void setStartingTreeDistribution(final Parameters.StartingTreeDistribution startingTreeDistribution, final double startingTreeDistributionShape) {
        this.startingTreeDistribution = startingTreeDistribution;
        this.startingTreeDistributionShape = startingTreeDistributionShape;
    }
    
    public void setStartingTreeDistribution(final Parameters.StartingTreeDistribution startingTreeDistribution) {
        this.startingTreeDistribution = startingTreeDistribution;
    }
    
    public Parameters.StartingTreeDistribution getStartingTreeDistribution() {
        return this.startingTreeDistribution;
    }
    
    public double getStartingTreeDistributionShape() {
        return this.startingTreeDistributionShape;
    }
    
    public void setStartingTreePInv(final double startingTreePInv) {
        this.startingTreePInv = startingTreePInv;
    }
    
    public double getStartingTreePInv() {
        return this.startingTreePInv;
    }
    
    public void setStartingTreePInvPi(final Parameters.StartingTreePInvPi startingTreePInvPi) {
        this.startingTreePInvPi = startingTreePInvPi;
    }
    
    public Parameters.StartingTreePInvPi getStartingTreePInvPi() {
        return this.startingTreePInvPi;
    }
    
    public void addOperator(final Parameters.Operator operator) {
        this.operators.add(operator);
    }
    
    public void removeOperator(final Parameters.Operator operator) {
        this.operators.remove(operator);
    }
    
    public List<Parameters.Operator> getOperators() {
        return new ArrayList<Parameters.Operator>(this.operators);
    }
    
    public void addOperatorsParameter(final Parameters.Operator operator, final int parameter) {
        this.operatorsParameters.put(operator, parameter);
    }
    
    public void removeOperatorsParameter(final Parameters.Operator operator) {
        this.operatorsParameters.remove(operator);
    }
    
    public Map<Parameters.Operator, Integer> getOperatorsParameters() {
        return new HashMap<Parameters.Operator, Integer>(this.operatorsParameters);
    }
    
    public void addOperatorsFrequency(final Parameters.Operator operator, final double frequency) {
        this.operatorsFrequencies.put(operator, frequency);
    }
    
    public void removeOperatorsFrequency(final Parameters.Operator operator) {
        this.operatorsFrequencies.remove(operator);
    }
    
    public Map<Parameters.Operator, Double> getOperatorsFrequencies() {
        return new HashMap<Parameters.Operator, Double>(this.operatorsFrequencies);
    }
    
    public void addOperatorIsDynamic(final Parameters.Operator operator) {
        this.operatorIsDynamic.add(operator);
    }
    
    public void removeOperatorIsDynamic(final Parameters.Operator operator) {
        this.operatorIsDynamic.remove(operator);
    }
    
    public Set<Parameters.Operator> getOperatorIsDynamic() {
        return new HashSet<Parameters.Operator>(this.operatorIsDynamic);
    }
    
    public void setDynamicInterval(final int dynamicInterval) {
        this.dynamicInterval = dynamicInterval;
    }
    
    public int getDynamicInterval() {
        return this.dynamicInterval;
    }
    
    public void setDynamicMin(final double dynamicMin) {
        this.dynamicMin = dynamicMin;
    }
    
    public double getDynamicMin() {
        return this.dynamicMin;
    }
    
    public void setOperatorSelection(final Parameters.OperatorSelection operatorSelection) {
        this.operatorSelection = operatorSelection;
    }
    
    public Parameters.OperatorSelection getOperatorSelection() {
        return this.operatorSelection;
    }
    
    public void setColumnRemoval(final Parameters.ColumnRemoval columnRemoval) {
        this.columnRemoval = columnRemoval;
    }
    
    public Parameters.ColumnRemoval getColumnRemoval() {
        return this.columnRemoval;
    }
    
    public void setReplicatesStopCondition(final Parameters.ReplicatesStopCondition replicatesStopCondition) {
        this.replicatesStopCondition = replicatesStopCondition;
    }
    
    public Parameters.ReplicatesStopCondition getReplicatesStopCondition() {
        return this.replicatesStopCondition;
    }
    
    public void setReplicatesMRE(final double mre) {
        this.replicatesMRE = mre;
    }
    
    public double getReplicatesMRE() {
        return this.replicatesMRE;
    }
    
    public void setReplicatesNumber(final int replicatesNum) {
        this.replicatesNum = replicatesNum;
    }
    
    public int getReplicatesNumber() {
        return this.replicatesNum;
    }
    
    public void setReplicatesMinimum(final int replicatesMin) {
        this.replicatesMin = replicatesMin;
    }
    
    public int getReplicatesMinimum() {
        return this.replicatesMin;
    }
    
    public void setReplicatesMaximum(final int replicatesMax) {
        this.replicatesMax = replicatesMax;
    }
    
    public int getReplicatesMaximum() {
        return this.replicatesMax;
    }
    
    public void setReplicatesInterval(final int replicatesInterval) {
        this.replicatesInterval = replicatesInterval;
    }
    
    public int getReplicatesInterval() {
        return this.replicatesInterval;
    }
    
    public void setReplicatesParallel(final int replcatesParallel) {
        this.replicatesParallel = replcatesParallel;
    }
    
    public int getReplicatesParallel() {
        return this.replicatesParallel;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setOutputDir(final String outputDir) {
        this.outputDir = outputDir;
    }
    
    public String getOutputDir() {
        return this.outputDir;
    }
    
    public void addSufficientStopCondition(final Parameters.HeuristicStopCondition condition) {
        this.sufficientStopConditions.add(condition);
    }
    
    public void removeSufficientStopCondition(final Parameters.HeuristicStopCondition condition) {
        this.sufficientStopConditions.remove(condition);
    }
    
    public Set<Parameters.HeuristicStopCondition> getSufficientStopConditions() {
        return new HashSet<Parameters.HeuristicStopCondition>(this.sufficientStopConditions);
    }
    
    public void addNecessaryStopCondition(final Parameters.HeuristicStopCondition condition) {
        this.necessaryStopConditions.add(condition);
    }
    
    public void removeNecessaryStopCondition(final Parameters.HeuristicStopCondition condition) {
        this.necessaryStopConditions.remove(condition);
    }
    
    public Set<Parameters.HeuristicStopCondition> getNecessaryStopConditions() {
        return new HashSet<Parameters.HeuristicStopCondition>(this.necessaryStopConditions);
    }
    
    public void setStopCriterionSteps(final int stopCriterionSteps) {
        this.stopCriterionSteps = stopCriterionSteps;
        this.stopCriterionSet = true;
    }
    
    public int getStopCriterionSteps() {
        return this.stopCriterionSteps;
    }
    
    public void setStopCriterionTime(final double stopCriterionTime) {
        this.stopCriterionTime = stopCriterionTime;
        this.stopCriterionSet = true;
    }
    
    public double getStopCriterionTime() {
        return this.stopCriterionTime;
    }
    
    public void setStopCriterionAutoSteps(final int stopCriterionAuto) {
        this.stopCriterionAutoSteps = stopCriterionAuto;
        this.stopCriterionSet = true;
    }
    
    public int getStopCriterionAutoSteps() {
        if (this.stopCriterionSet) {
            return this.stopCriterionAutoSteps;
        }
        return 200;
    }
    
    public void setStopCriterionAutoThreshold(final double stopCriterionAutoThreshold) {
        this.stopCriterionAutoThreshold = stopCriterionAutoThreshold;
        this.stopCriterionSet = true;
    }
    
    public double getStopCriterionAutoThreshold() {
        return this.stopCriterionAutoThreshold;
    }
    
    public void setStopCriterionConsensusMRE(final double mre) {
        this.stopCriterionConsensusMRE = mre;
    }
    
    public double getStopCriterionConsensusMRE() {
        return this.stopCriterionConsensusMRE;
    }
    
    public void setStopCriterionConsensusGeneration(final int generation) {
        this.stopCriterionConsensusGeneration = generation;
    }
    
    public int getStopCriterionConsensusGeneration() {
        return this.stopCriterionConsensusGeneration;
    }
    
    public void setStopCriterionConsensusInterval(final int interval) {
        this.stopCriterionConsensusInterval = interval;
    }
    
    public int getStopCriterionConsensusInterval() {
        return this.stopCriterionConsensusInterval;
    }
    
    public void addOutgroup(final String taxa) {
        this.outgroup.add(taxa);
    }
    
    public void removeOutgroup(final String taxa) {
        this.outgroup.remove(taxa);
    }
    
    public Set<String> getOutgroup() {
        return new HashSet<String>(this.outgroup);
    }
    
    public void addDeletedTaxa(final String taxa) {
        this.deletedTaxa.add(taxa);
    }
    
    public void removeDeletedTaxa(final String taxa) {
        this.deletedTaxa.remove(taxa);
    }
    
    public Set<String> getDeletedTaxa() {
        return new HashSet<String>(this.deletedTaxa);
    }
    
    public void addCharset(final Charset charset) {
        this.charsets.add(charset);
    }
    
    public void removeCharset(final Charset charset) {
        this.charsets.remove(charset);
    }
    
    public Set<Charset> getCharset() {
        return new HashSet<Charset>(this.charsets);
    }
    
    public void addExcludedCharset(final Charset charset) {
        this.excludedCharsets.add(charset);
    }
    
    public void removeExcludedCharset(final Charset charset) {
        this.excludedCharsets.remove(charset);
    }
    
    public Set<Charset> getExcludedCharsets() {
        return new HashSet<Charset>(this.excludedCharsets);
    }
    
    public void addPartition(final Charset charset) {
        this.partitions.add(charset);
    }
    
    public void removePartition(final Charset charset) {
        this.partitions.remove(charset);
    }
    
    public Set<Charset> getPartitions() {
        return new HashSet<Charset>(this.partitions);
    }
    
    public void addLogFile(final Parameters.LogFile log) {
        this.logFiles.add(log);
    }
    
    public void removeLogFile(final Parameters.LogFile log) {
        this.logFiles.remove(log);
    }
    
    public Set<Parameters.LogFile> getLogFiles() {
        return new HashSet<Parameters.LogFile>(this.logFiles);
    }
    
    public void setGridReplicate(final boolean gridReplicate) {
        this.gridReplicate = gridReplicate;
    }
    
    public boolean getGridReplicate() {
        return this.gridReplicate;
    }
    
    public void setGridOutput(final String gridOutput) {
        this.gridOutput = gridOutput;
    }
    
    public String getGridOutput() {
        return this.gridOutput;
    }
    
    public void setUseGrid(final boolean useGrid) {
        this.useGrid = useGrid;
    }
    
    public boolean getUseGrid() {
        return this.useGrid;
    }
    
    public void setGridServer(final String gridServer) {
        this.gridServer = gridServer;
    }
    
    public String getGridServer() {
        return this.gridServer;
    }
    
    public void setGridClient(final String gridClient) {
        this.gridClient = gridClient;
    }
    
    public String getGridClient() {
        return this.gridClient;
    }
    
    public void setGridModule(final String gridModule) {
        this.gridModule = gridModule;
    }
    
    public String getGridModule() {
        return this.gridModule;
    }
    
    public void setCloudReplicate(final boolean cloudReplicate) {
        this.cloudReplicate = cloudReplicate;
    }
    
    public boolean getCloudReplicate() {
        return this.cloudReplicate;
    }
    
    public void setCloudOutput(final String cloudOutput) {
        this.cloudOutput = cloudOutput;
    }
    
    public String getCloudOutput() {
        return this.cloudOutput;
    }
    
    public void setUseCloud(final boolean useCloud) {
        this.useCloud = useCloud;
    }
    
    public boolean getUseCloud() {
        return this.useCloud;
    }
    
    public void setCloudServer(final String cloudServer) {
        this.cloudServer = cloudServer;
    }
    
    public String getCloudServer() {
        return this.cloudServer;
    }
    
    public void setCloudClient(final String cloudClient) {
        this.cloudClient = cloudClient;
    }
    
    public String getCloudClient() {
        return this.gridClient;
    }
    
    public void setCloudModule(final String cloudModule) {
        this.cloudModule = cloudModule;
    }
    
    public String getCloudModule() {
        return this.cloudModule;
    }
    
    public void setLikelihoodCalculationType(final Parameters.LikelihoodCalculationType type) {
        this.likelihoodCalculationType = type;
    }
    
    public Parameters.LikelihoodCalculationType getLikelihoodCalculationType() {
        return this.likelihoodCalculationType;
    }
    
    public DataType getDataType() {
        return this.dataType;
    }
    
    public void setDataType(final DataType dataType) {
        this.dataType = dataType;
    }
    
    public int getCodonDomainStartPosition() {
        return this.startCodonDomainPosition;
    }
    
    public int getCodonDomainEndPosition() {
        return this.endCodonDomainPosition;
    }
    
    public void setCodonDomainRange(final int startPosition, final int endPosition) {
        this.startCodonDomainPosition = startPosition;
        this.endCodonDomainPosition = endPosition;
    }
    
    public Parameters.CodonTransitionTableType getCodonTable() {
        return this.codonTable;
    }
    
    public void setCodonTable(final Parameters.CodonTransitionTableType codonTable) {
        this.codonTable = codonTable;
    }
    
    public void addComment(final NexusComment comment) {
        this.comments.add(comment);
    }
    
    public void removeComment(final NexusComment comment) {
        this.comments.remove(comment);
    }
    
    public List<NexusComment> getComments() {
        return this.comments;
    }
    
    @Override
    protected void writeBlockContents(final Writer fw) throws IOException {
        final String endl = NexusFileFormat.NEW_LINE;
        final Iterator<NexusComment> i = this.comments.iterator();
        while (i.hasNext()) {
            i.next().writeObject(fw);
            fw.write(endl);
        }
        fw.write("HEURISTIC " + this.heuristic);
        switch (this.heuristic) {
            case SA: {
                fw.write(" SCHEDULE=" + this.saSchedule);
                if (this.saSchedule == Parameters.SASchedule.RP || this.saSchedule == Parameters.SASchedule.GEOM) {
                    fw.write("(" + this.saScheduleParam + ")");
                }
                switch (this.saSchedule) {
                    case LUNDY: {
                        fw.write(" LUNC=" + this.saLundyC);
                        fw.write(" LUNALPHA=" + this.saLundyAlpha);
                        break;
                    }
                    case CAUCHY:
                    case BOLTZMANN:
                    case GEOM:
                    case RP: {
                        fw.write(" INITACCEPT=" + Tools.doubletoString(this.saInitAccept, 2));
                        break;
                    }
                    default: {
                        fw.write(" INITACCEPT=" + Tools.doubletoString(this.saInitAccept, 2));
                        fw.write(" FINALACCEPT=" + Tools.doubletoString(this.saFinalAccept, 2));
                        break;
                    }
                }
                fw.write(" DELTAL=" + this.saDeltaL);
                if (this.saDeltaL == Parameters.SADeltaL.PERCENT) {
                    fw.write("(" + this.saDeltaLPercent + ")");
                }
                fw.write(" REHEATING=" + this.saReheatingType);
                if (this.saReheatingType == Parameters.SAReheating.DECREMENTS) {
                    fw.write("(" + (int)this.saReheatingValue + ")");
                }
                else if (this.saReheatingType == Parameters.SAReheating.THRESHOLD) {
                    fw.write("(" + Tools.doubletoString(this.saReheatingValue, 6) + ")");
                }
                fw.write(" COOLING=" + this.saCoolingType);
                if (this.saCoolingType == Parameters.SACooling.STEPS) {
                    fw.write("(" + this.saCoolingSteps + ")");
                    break;
                }
                if (this.saCoolingType == Parameters.SACooling.SF) {
                    fw.write("(" + this.saCoolingSuccesses + "," + this.saCoolingFailures + ")");
                    break;
                }
                break;
            }
            case GA: {
                fw.write(" NIND=" + this.gaIndNum);
                fw.write(" SELECTION=" + this.gaSelection);
                if (this.gaSelection == Parameters.GASelection.REPLACEMENT) {
                    fw.write("(" + Tools.doubletoString(this.gaReplacementStrength, 4) + ")");
                }
                fw.write(" RECOMBINATION=" + this.gaRecombination);
                fw.write(" OPERATORAPPLIEDTO=" + this.gaOperatorChange);
                break;
            }
            case CP: {
                fw.write(" CONSENSUS=" + this.cpConsensus);
                fw.write(" OPERATOR=" + this.cpOperator);
                fw.write(" NPOP=" + this.cpPopNum);
                fw.write(" NIND=" + this.cpIndNum);
                fw.write(" TOLERANCE=" + this.cpTolerance);
                fw.write(" HYBRIDIZATION=" + this.cpHybridization);
                fw.write(" SELECTION=" + this.cpSelection);
                if (this.cpSelection == Parameters.CPSelection.REPLACEMENT) {
                    fw.write("(" + this.cpReplacementStrength + ")");
                }
                fw.write(" RECOMBINATION=" + this.cpRecombination);
                fw.write(" OPERATORAPPLIEDTO=" + this.cpOperatorChange);
                fw.write(" NCORE=" + this.cpCoreNum);
                break;
            }
            case HC: {
                if (this.hcRestart > 0) {
                    fw.write(" RESTART=" + this.hcRestart);
                    break;
                }
                break;
            }
        }
        fw.write(";" + endl);
        fw.write("EVALUATION RATE=" + this.evaluationRate);
        if (this.dataType == DataType.CODON) {
            fw.write(" DATATYPE=" + this.dataType.toString());
            fw.write(" CODONRANGE{");
            fw.write(" " + this.startCodonDomainPosition + "-" + this.endCodonDomainPosition + " }");
            fw.write(" DNACODE=" + this.codonTable.toString());
        }
        fw.write(" MODEL=" + this.evaluationModel);
        switch (this.evaluationModel) {
            case HKY85:
            case K2P: {
                fw.write(" RATEPARAM{ ");
                fw.write(RateParameter.K + "(" + this.evaluationRateParameters.get(RateParameter.K) + ")");
                fw.write("}");
                break;
            }
            case TN93: {
                fw.write(" RATEPARAM{ ");
                fw.write(RateParameter.K1 + "(" + this.evaluationRateParameters.get(RateParameter.K1) + ") ");
                fw.write(RateParameter.K2 + "(" + this.evaluationRateParameters.get(RateParameter.K2) + ")");
                fw.write("}");
                break;
            }
            case GTR: {
                fw.write(" RATEPARAM{ ");
                fw.write(RateParameter.A + "(" + this.evaluationRateParameters.get(RateParameter.A) + ") ");
                fw.write(RateParameter.B + "(" + this.evaluationRateParameters.get(RateParameter.B) + ") ");
                fw.write(RateParameter.C + "(" + this.evaluationRateParameters.get(RateParameter.C) + ") ");
                fw.write(RateParameter.D + "(" + this.evaluationRateParameters.get(RateParameter.D) + ") ");
                fw.write(RateParameter.E + "(" + this.evaluationRateParameters.get(RateParameter.E) + ")");
                fw.write("}");
                break;
            }
            case GTR20: {
                fw.write(" RATEPARAM{ ");
                RateParameter[] parametersOfModel;
                for (int length = (parametersOfModel = RateParameter.getParametersOfModel(Parameters.EvaluationModel.GTR20)).length, j = 0; j < length; ++j) {
                    final RateParameter r = parametersOfModel[j];
                    fw.write(r + "(" + this.evaluationRateParameters.get(r) + ") ");
                }
                fw.write("}");
                break;
            }
            case GY: {
                fw.write(" RATEPARAM{ ");
                fw.write(RateParameter.KAPPA + "(" + this.evaluationRateParameters.get(RateParameter.KAPPA) + ") ");
                fw.write(RateParameter.OMEGA + "(" + this.evaluationRateParameters.get(RateParameter.OMEGA) + ") ");
                fw.write("}");
                break;
            }
            case GTR64: {
                fw.write(" RATEPARAM{ ");
                RateParameter[] parametersOfModel2;
                for (int length2 = (parametersOfModel2 = RateParameter.getParametersOfModel(Parameters.EvaluationModel.GTR64)).length, k = 0; k < length2; ++k) {
                    final RateParameter r = parametersOfModel2[k];
                    fw.write(r + "(" + this.evaluationRateParameters.get(r) + ") ");
                }
                fw.write("}");
                break;
            }
        }
        if (this.evaluationModel.isEmpirical()) {
            fw.write(" AAFREQ=" + this.evaluationStateFrequencies);
        }
        fw.write(" DISTRIBUTION=" + this.evaluationDistribution);
        if (this.evaluationDistribution == Parameters.EvaluationDistribution.GAMMA || this.evaluationDistribution == Parameters.EvaluationDistribution.VDP) {
            fw.write("(" + this.evaluationDistributionSubsets + ")");
        }
        if (this.evaluationDistribution == Parameters.EvaluationDistribution.GAMMA) {
            fw.write(" DISTSHAPE=" + this.evaluationDistributionShape);
        }
        fw.write(" PINV=" + this.evaluationPInv);
        fw.write(";" + endl);
        final Set<Charset> specifics = new TreeSet<Charset>();
        specifics.addAll(this.specificRateParameters.keySet());
        specifics.addAll(this.specificDistributionShapes.keySet());
        specifics.addAll(this.specificsPInvs.keySet());
        for (final Charset c : specifics) {
            fw.write("SPECIFICPARTPARAM PARTNAME=" + c.getLabel().replace(' ', '_'));
            if (this.specificRateParameters.containsKey(c)) {
                switch (this.evaluationModel) {
                    case HKY85:
                    case K2P: {
                        fw.write(" RATEPARAM{ ");
                        fw.write(RateParameter.K + "(" + this.specificRateParameters.get(c).get(RateParameter.K) + ")");
                        fw.write("}");
                        break;
                    }
                    case TN93: {
                        fw.write(" RATEPARAM{ ");
                        fw.write(RateParameter.K1 + "(" + this.specificRateParameters.get(c).get(RateParameter.K1) + ") ");
                        fw.write(RateParameter.K2 + "(" + this.specificRateParameters.get(c).get(RateParameter.K2) + ")");
                        fw.write("}");
                        break;
                    }
                    case GTR: {
                        fw.write(" RATEPARAM{ ");
                        fw.write(RateParameter.A + "(" + this.specificRateParameters.get(c).get(RateParameter.A) + ") ");
                        fw.write(RateParameter.B + "(" + this.specificRateParameters.get(c).get(RateParameter.B) + ") ");
                        fw.write(RateParameter.C + "(" + this.specificRateParameters.get(c).get(RateParameter.C) + ") ");
                        fw.write(RateParameter.D + "(" + this.specificRateParameters.get(c).get(RateParameter.D) + ") ");
                        fw.write(RateParameter.E + "(" + this.specificRateParameters.get(c).get(RateParameter.E) + ")");
                        fw.write("}");
                        break;
                    }
                    case GTR20: {
                        fw.write(" RATEPARAM{ ");
                        RateParameter[] parametersOfModel3;
                        for (int length3 = (parametersOfModel3 = RateParameter.getParametersOfModel(Parameters.EvaluationModel.GTR20)).length, n = 0; n < length3; ++n) {
                            final RateParameter r2 = parametersOfModel3[n];
                            fw.write(r2 + "(" + this.specificRateParameters.get(c).get(r2) + ") ");
                        }
                        fw.write("}");
                        break;
                    }
                    case GY: {
                        fw.write(" RATEPARAM{ ");
                        fw.write(RateParameter.KAPPA + "(" + this.specificRateParameters.get(c).get(RateParameter.KAPPA) + ") ");
                        fw.write(RateParameter.OMEGA + "(" + this.specificRateParameters.get(c).get(RateParameter.OMEGA) + ")");
                        fw.write("}");
                        break;
                    }
                    case GTR64: {
                        fw.write(" RATEPARAM{ ");
                        RateParameter[] parametersOfModel4;
                        for (int length4 = (parametersOfModel4 = RateParameter.getParametersOfModel(Parameters.EvaluationModel.GTR64)).length, n2 = 0; n2 < length4; ++n2) {
                            final RateParameter r2 = parametersOfModel4[n2];
                            fw.write(r2 + "(" + this.specificRateParameters.get(c).get(r2) + ") ");
                        }
                        fw.write("}");
                        break;
                    }
                }
            }
            if (this.evaluationDistribution == Parameters.EvaluationDistribution.GAMMA && this.specificDistributionShapes.containsKey(c)) {
                fw.write(" DISTSHAPE=" + this.specificDistributionShapes.get(c));
            }
            if (this.specificsPInvs.containsKey(c)) {
                fw.write(" PINV=" + this.specificsPInvs.get(c));
            }
            fw.write(";" + endl);
        }
        if (this.evaluationRate == Parameters.EvaluationRate.TREE || this.evaluationRate == Parameters.EvaluationRate.BRANCH) {
            fw.write("OPTIMIZATION " + this.optimization);
            switch (this.optimization) {
                case NEVER: {}
                case CONSENSUSTREE: {}
                case STOCH: {
                    fw.write("(" + this.optimizationUse + ") ");
                    break;
                }
                case DISC: {
                    fw.write("(" + (int)this.optimizationUse + ") ");
                    break;
                }
            }
            if (this.optimization != Parameters.Optimization.NEVER) {
                fw.write(" ALGO=" + this.optimizationAlgorithm);
                if (!this.optimizationTargets.isEmpty()) {
                    fw.write(" TARGET{ ");
                    for (final Parameters.OptimizationTarget target : this.optimizationTargets) {
                        fw.write(target + " ");
                    }
                    fw.write("}");
                }
            }
            fw.write(" ;" + endl);
        }
        fw.write("STARTINGTREE GENERATION=" + this.startingTreeGeneration);
        if (this.startingTreeGeneration == Parameters.StartingTreeGeneration.LNJ) {
            fw.write("(" + this.startingTreeGenerationRange + ")");
        }
        if (this.startingTreeGeneration != Parameters.StartingTreeGeneration.RANDOM) {
            fw.write(" MODEL=" + this.startingTreeModel);
            fw.write(" DISTRIBUTION=" + this.startingTreeDistribution);
            if (this.startingTreeDistribution == Parameters.StartingTreeDistribution.GAMMA || this.startingTreeDistribution == Parameters.StartingTreeDistribution.VDP) {
                fw.write("(" + this.startingTreeDistributionShape + ")");
            }
            fw.write(" PINV=" + this.startingTreePInv);
            if (this.startingTreePInv > 0.0) {
                fw.write(" PI=" + this.startingTreePInvPi);
            }
        }
        fw.write(";" + endl);
        if (!this.operators.isEmpty()) {
            fw.write("OPERATORS { ");
            for (final Parameters.Operator op : this.operators) {
                fw.write(op.toString());
                if (this.operatorsParameters.containsKey(op)) {
                    fw.write("(");
                    switch (this.operatorsParameters.get(op)) {
                        case 0: {
                            fw.write("ALL");
                            break;
                        }
                        case 1: {
                            if (op != Parameters.Operator.RPM) {
                                fw.write("RANDOM");
                                break;
                            }
                            fw.write(new StringBuilder().append(this.operatorsParameters.get(op)).toString());
                            break;
                        }
                        default: {
                            fw.write(new StringBuilder().append(this.operatorsParameters.get(op)).toString());
                            break;
                        }
                    }
                    fw.write(")");
                }
                fw.write(" ");
            }
            fw.write("} SELECTION=" + this.operatorSelection);
            fw.write(";" + endl);
            if (this.operatorSelection == Parameters.OperatorSelection.FREQLIST) {
                fw.write("FREQUENCIES { ");
                for (final Parameters.Operator op : this.operators) {
                    fw.write(op + "(" + this.operatorsFrequencies.get(op) + ") ");
                }
                fw.write("};" + endl);
                if (!this.operatorIsDynamic.isEmpty()) {
                    fw.write("DYNAMICFREQ DYNOPERATORS{ ");
                    for (final Parameters.Operator op : this.operatorIsDynamic) {
                        fw.write(op + " ");
                    }
                    fw.write("}");
                    fw.write(" DINT=" + this.dynamicInterval);
                    fw.write(" DMIN=" + this.dynamicMin);
                    fw.write(";" + endl);
                }
            }
        }
        fw.write("SETTINGS ");
        fw.write(" REMOVECOL=" + this.columnRemoval);
        fw.write(" LABEL='" + this.label + "'");
        fw.write(" DIR='" + this.outputDir + "'");
        if (this.useGrid) {
            fw.write(" GRID");
            fw.write(" SERVER=" + this.gridServer);
            fw.write(" CLIENT=" + this.gridClient);
            fw.write(" MODULE=" + this.gridModule);
        }
        if (this.likelihoodCalculationType == Parameters.LikelihoodCalculationType.GPU) {
            fw.write(" LIKELIHOODCOMPUTATION=GPU");
        }
        fw.write(";" + endl);
        if (!this.sufficientStopConditions.isEmpty() || !this.necessaryStopConditions.isEmpty()) {
            fw.write("STOPAFTER");
            if (this.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.STEPS) || this.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.STEPS)) {
                fw.write(" STEPS=" + this.stopCriterionSteps);
            }
            if (this.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.TIME) || this.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.TIME)) {
                fw.write(" TIME=" + Tools.doubletoString(this.stopCriterionTime, 4));
            }
            if (this.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.AUTO) || this.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.AUTO)) {
                fw.write(" AUTO=" + this.stopCriterionAutoSteps);
                fw.write(" AUTOTHRESHOLD=" + Tools.doubletoString(this.stopCriterionAutoThreshold, 15));
            }
            if (this.sufficientStopConditions.contains(Parameters.HeuristicStopCondition.CONSENSUS) || this.necessaryStopConditions.contains(Parameters.HeuristicStopCondition.CONSENSUS)) {
                fw.write(" CONSENSUS");
                fw.write(" MRE=" + this.stopCriterionConsensusMRE);
                fw.write(" GENERATION=" + this.stopCriterionConsensusGeneration);
                fw.write(" INTERVAL=" + this.stopCriterionConsensusInterval);
            }
            if (!this.necessaryStopConditions.isEmpty()) {
                fw.write(" NECESSARY { ");
                for (final Parameters.HeuristicStopCondition condition : this.necessaryStopConditions) {
                    fw.write(condition + " ");
                }
                fw.write("}");
            }
            fw.write(";" + endl);
        }
        fw.write("REPLICATES");
        switch (this.replicatesStopCondition) {
            case NONE: {
                fw.write(" AUTOSTOP=NONE");
                fw.write(" RNUM=" + this.replicatesNum);
                break;
            }
            case MRE: {
                fw.write(" AUTOSTOP=MRE(" + Tools.doubletoString(this.replicatesMRE, 6) + ")");
                fw.write(" RMIN=" + this.replicatesMin);
                fw.write(" RMAX=" + this.replicatesMax);
                fw.write(" INTERVAL=" + this.replicatesInterval);
                break;
            }
        }
        fw.write(" PARALLEL=" + this.replicatesParallel);
        fw.write(";" + endl);
        if (!this.outgroup.isEmpty()) {
            fw.write("OUTGROUP { ");
            for (final String s : this.outgroup) {
                fw.write(String.valueOf(s.replace(' ', '_')) + " ");
            }
            fw.write("};" + endl);
        }
        if (!this.deletedTaxa.isEmpty()) {
            fw.write("DELETE { ");
            for (final String s : this.deletedTaxa) {
                fw.write(String.valueOf(s.replace(' ', '_')) + " ");
            }
            fw.write("};" + endl);
        }
        for (final Charset ch : this.charsets) {
            if (!ch.isEmpty()) {
                if (!ch.isRecordable()) {
                    continue;
                }
                fw.write("CHARSET");
                fw.write(" NAME=" + ch.getLabel().replace(' ', '_'));
                fw.write(" SET{ ");
                fw.write(ch.getAllRanges());
                fw.write(" };" + endl);
            }
        }
        if (!this.excludedCharsets.isEmpty()) {
            boolean isTokenWritten = false;
            for (final Charset charset : this.excludedCharsets) {
                if (!charset.isRecordable()) {
                    continue;
                }
                if (!isTokenWritten) {
                    fw.write("EXCLUDE { ");
                    isTokenWritten = true;
                }
                fw.write(String.valueOf(charset.getLabel().replace(' ', '_')) + " ");
            }
            if (isTokenWritten) {
                fw.write("};" + endl);
            }
        }
        if (!this.partitions.isEmpty() && (this.partitions.size() != 1 || !this.partitions.contains(new Charset("FULL SET")))) {
            fw.write("PARTITION { ");
            for (final Charset charset2 : this.partitions) {
                fw.write(String.valueOf(charset2.getLabel().replace(' ', '_')) + " ");
            }
            fw.write("};" + endl);
        }
        if (!this.logFiles.isEmpty()) {
            fw.write("LOG { ");
            for (final Parameters.LogFile l : this.logFiles) {
                fw.write(l + " ");
            }
            fw.write("};" + endl);
        }
        if (this.gridReplicate) {
            fw.write("GRIDREPLICATE OUTPUT=" + this.gridOutput + ";" + endl);
        }
    }
}

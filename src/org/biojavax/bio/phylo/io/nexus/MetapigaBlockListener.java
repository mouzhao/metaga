// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

import metapiga.modelization.data.DataType;
import java.util.Set;
import metapiga.modelization.Charset;
import metapiga.RateParameter;
import metapiga.parameters.Parameters;

public interface MetapigaBlockListener extends NexusBlockListener
{
    void setUseComplexNum(final boolean p0);
    
    void setHeuristic(final Parameters.Heuristic p0);
    
    void setHcRestart(final int p0);
    
    void setSaSchedule(final Parameters.SASchedule p0);
    
    void setSaScheduleParam(final double p0);
    
    void setSaLundyC(final double p0);
    
    void setSaLundyAlpha(final double p0);
    
    void setSaInitAccept(final double p0);
    
    void setSaFinalAccept(final double p0);
    
    void setSaDeltaL(final Parameters.SADeltaL p0);
    
    void setSaDeltaLPercent(final double p0);
    
    void setSaReheatingType(final Parameters.SAReheating p0);
    
    void setSaReheatingValue(final double p0);
    
    void setSaCoolingType(final Parameters.SACooling p0);
    
    void setSaCoolingSteps(final int p0);
    
    void setSaCoolingSuccesses(final int p0);
    
    void setSaCoolingFailures(final int p0);
    
    void setGaIndNum(final int p0);
    
    void setGaSelection(final Parameters.GASelection p0);
    
    void setGaReplacementStrength(final double p0);
    
    void setGaRecombination(final double p0);
    
    void setGaOperatorChange(final Parameters.GAOperatorChange p0);
    
    void setCpConsensus(final Parameters.CPConsensus p0);
    
    void setCpOperator(final Parameters.CPOperator p0);
    
    void setCpPopNum(final int p0);
    
    void setCpIndNum(final int p0);
    
    void setCpTolerance(final double p0);
    
    void setCpHybridization(final double p0);
    
    void setCpSelection(final Parameters.CPSelection p0);
    
    void setCpReplacementStrength(final double p0);
    
    void setCpRecombination(final double p0);
    
    void setCpOperatorChange(final Parameters.CPOperatorChange p0);
    
    void setCpCoreNum(final int p0);
    
    void setEvaluationRate(final Parameters.EvaluationRate p0);
    
    void setEvaluationModel(final Parameters.EvaluationModel p0);
    
    void addRateParameter(final RateParameter p0, final double p1);
    
    void setEmpiricalRateParameters(final Parameters.EvaluationModel p0);
    
    void setEvaluationAAFrequency(final Parameters.EvaluationStateFrequencies p0);
    
    void setEvaluationDistribution(final Parameters.EvaluationDistribution p0, final int p1);
    
    void setEvaluationDistribution(final Parameters.EvaluationDistribution p0);
    
    void setEvaluationDistributionShape(final double p0);
    
    void setEvaluationPInv(final double p0);
    
    void addSpecificRateParameter(final Charset p0, final RateParameter p1, final double p2);
    
    void addSpecificDistributionShape(final Charset p0, final Double p1);
    
    void addSpecificPInv(final Charset p0, final Double p1);
    
    void setOptimization(final Parameters.Optimization p0);
    
    void setOptimizationUse(final double p0);
    
    void setOptimizationAlgorithm(final Parameters.OptimizationAlgorithm p0);
    
    void addOptimizationTarget(final Parameters.OptimizationTarget p0);
    
    void setStartingTreeGeneration(final Parameters.StartingTreeGeneration p0);
    
    void setStartingTreeGenerationRange(final double p0);
    
    void setStartingTreeModel(final Parameters.DistanceModel p0);
    
    void setStartingTreeDistribution(final Parameters.StartingTreeDistribution p0, final double p1);
    
    void setStartingTreeDistribution(final Parameters.StartingTreeDistribution p0);
    
    void setStartingTreePInv(final double p0);
    
    void setStartingTreePInvPi(final Parameters.StartingTreePInvPi p0);
    
    void addOperator(final Parameters.Operator p0);
    
    void addOperatorsParameter(final Parameters.Operator p0, final int p1);
    
    void addOperatorsFrequency(final Parameters.Operator p0, final double p1);
    
    void addOperatorIsDynamic(final Parameters.Operator p0);
    
    void setDynamicInterval(final int p0);
    
    void setDynamicMin(final double p0);
    
    void setOperatorSelection(final Parameters.OperatorSelection p0);
    
    void setColumnRemoval(final Parameters.ColumnRemoval p0);
    
    void setLabel(final String p0);
    
    void setOutputDir(final String p0);
    
    void addSufficientStopCondition(final Parameters.HeuristicStopCondition p0);
    
    void removeSufficientStopCondition(final Parameters.HeuristicStopCondition p0);
    
    void addNecessaryStopCondition(final Parameters.HeuristicStopCondition p0);
    
    void setStopCriterionSteps(final int p0);
    
    void setStopCriterionTime(final double p0);
    
    void setStopCriterionAutoSteps(final int p0);
    
    void setStopCriterionAutoThreshold(final double p0);
    
    void setStopCriterionConsensusMRE(final double p0);
    
    void setStopCriterionConsensusGeneration(final int p0);
    
    void setStopCriterionConsensusInterval(final int p0);
    
    void setReplicatesStopCondition(final Parameters.ReplicatesStopCondition p0);
    
    void setReplicatesMRE(final double p0);
    
    void setReplicatesNumber(final int p0);
    
    void setReplicatesMinimum(final int p0);
    
    void setReplicatesMaximum(final int p0);
    
    void setReplicatesInterval(final int p0);
    
    void setReplicatesParallel(final int p0);
    
    void addOutgroup(final String p0);
    
    void addDeletedTaxa(final String p0);
    
    void addCharset(final Charset p0);
    
    Set<Charset> getCharset();
    
    void addExcludedCharset(final Charset p0);
    
    void addPartition(final Charset p0);
    
    void addLogFile(final Parameters.LogFile p0);
    
    void setGridReplicate(final boolean p0);
    
    void setGridOutput(final String p0);
    
    void setUseGrid(final boolean p0);
    
    void setGridServer(final String p0);
    
    void setGridClient(final String p0);
    
    void setGridModule(final String p0);
    
    void setUseCloud(final boolean p0);
    
    void setCloudServer(final String p0);
    
    void setCloudClient(final String p0);
    
    void setCloudModule(final String p0);
    
    void setLikelihoodCalculationType(final Parameters.LikelihoodCalculationType p0);
    
    void setDatatype(final DataType p0);
    
    void setCodonRange(final int p0, final int p1);
    
    void setCodonTransitionTable(final Parameters.CodonTransitionTableType p0);
}

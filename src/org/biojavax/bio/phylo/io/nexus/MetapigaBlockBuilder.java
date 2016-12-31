// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

import metapiga.modelization.data.DataType;
import java.util.Set;
import metapiga.modelization.Charset;
import metapiga.RateParameter;
import metapiga.parameters.Parameters;

public class MetapigaBlockBuilder extends NexusBlockBuilder.Abstract implements MetapigaBlockListener
{
    private MetapigaBlock block;
    
    @Override
    protected void addComment(final NexusComment comment) {
        this.block.addComment(comment);
    }
    
    protected MetapigaBlock makeNewBlock() {
        return new MetapigaBlock();
    }
    
    @Override
    protected NexusBlock startBlockObject() {
        this.block = this.makeNewBlock();
        this.resetStatus();
        return this.block;
    }
    
    protected void resetStatus() {
    }
    
    @Override
    public void endBlock() {
    }
    
    @Override
    public void endTokenGroup() {
    }
    
    @Override
    public void setUseComplexNum(final boolean useComplexNum) {
        this.block.setUseComplexNum(useComplexNum);
    }
    
    @Override
    public void setHeuristic(final Parameters.Heuristic heuristic) {
        this.block.setHeuristic(heuristic);
    }
    
    @Override
    public void setHcRestart(final int hcRestart) {
        this.block.setHcRestart(hcRestart);
    }
    
    @Override
    public void setSaSchedule(final Parameters.SASchedule saSchedule) {
        this.block.setSaSchedule(saSchedule);
    }
    
    @Override
    public void setSaScheduleParam(final double saScheduleParam) {
        this.block.setSaScheduleParam(saScheduleParam);
    }
    
    @Override
    public void setSaLundyC(final double saLundyC) {
        this.block.setSaLundyC(saLundyC);
    }
    
    @Override
    public void setSaLundyAlpha(final double saLundyAlpha) {
        this.block.setSaLundyAlpha(saLundyAlpha);
    }
    
    @Override
    public void setSaInitAccept(final double saInitAccept) {
        this.block.setSaInitAccept(saInitAccept);
    }
    
    @Override
    public void setSaFinalAccept(final double saFinalAccept) {
        this.block.setSaFinalAccept(saFinalAccept);
    }
    
    @Override
    public void setSaDeltaL(final Parameters.SADeltaL saDeltaL) {
        this.block.setSaDeltaL(saDeltaL);
    }
    
    @Override
    public void setSaDeltaLPercent(final double saDeltaLPercent) {
        this.block.setSaDeltaLPercent(saDeltaLPercent);
    }
    
    @Override
    public void setSaReheatingType(final Parameters.SAReheating saReheatingType) {
        this.block.setSaReheatingType(saReheatingType);
    }
    
    @Override
    public void setSaReheatingValue(final double saReheatingValue) {
        this.block.setSaReheatingValue(saReheatingValue);
    }
    
    @Override
    public void setSaCoolingType(final Parameters.SACooling saCoolingType) {
        this.block.setSaCoolingType(saCoolingType);
    }
    
    @Override
    public void setSaCoolingSteps(final int saCoolingSteps) {
        this.block.setSaCoolingSteps(saCoolingSteps);
    }
    
    @Override
    public void setSaCoolingSuccesses(final int saCoolingSuccesses) {
        this.block.setSaCoolingSuccesses(saCoolingSuccesses);
    }
    
    @Override
    public void setSaCoolingFailures(final int saCoolingFailures) {
        this.block.setSaCoolingFailures(saCoolingFailures);
    }
    
    @Override
    public void setGaIndNum(final int gaIndNum) {
        this.block.setGaIndNum(gaIndNum);
    }
    
    @Override
    public void setGaSelection(final Parameters.GASelection gaSelection) {
        this.block.setGaSelection(gaSelection);
    }
    
    @Override
    public void setGaReplacementStrength(final double gaReplacementStrength) {
        this.block.setGaReplacementStrength(gaReplacementStrength);
    }
    
    @Override
    public void setGaRecombination(final double gaRecombination) {
        this.block.setGaRecombination(gaRecombination);
    }
    
    @Override
    public void setGaOperatorChange(final Parameters.GAOperatorChange gaOperatorChange) {
        this.block.setGaOperatorChange(gaOperatorChange);
    }
    
    @Override
    public void setCpConsensus(final Parameters.CPConsensus cpConsensus) {
        this.block.setCpConsensus(cpConsensus);
    }
    
    @Override
    public void setCpOperator(final Parameters.CPOperator cpOperator) {
        this.block.setCpOperator(cpOperator);
    }
    
    @Override
    public void setCpPopNum(final int cpPopNum) {
        this.block.setCpPopNum(cpPopNum);
    }
    
    @Override
    public void setCpIndNum(final int cpIndNum) {
        this.block.setCpIndNum(cpIndNum);
    }
    
    @Override
    public void setCpTolerance(final double cpTolerance) {
        this.block.setCpTolerance(cpTolerance);
    }
    
    @Override
    public void setCpHybridization(final double cpHybridization) {
        this.block.setCpHybridization(cpHybridization);
    }
    
    @Override
    public void setCpSelection(final Parameters.CPSelection cpSelection) {
        this.block.setCpSelection(cpSelection);
    }
    
    @Override
    public void setCpReplacementStrength(final double cpReplacementStrength) {
        this.block.setCpReplacementStrength(cpReplacementStrength);
    }
    
    @Override
    public void setCpRecombination(final double cpRecombination) {
        this.block.setCpRecombination(cpRecombination);
    }
    
    @Override
    public void setCpOperatorChange(final Parameters.CPOperatorChange cpOperatorChange) {
        this.block.setCpOperatorChange(cpOperatorChange);
    }
    
    @Override
    public void setCpCoreNum(final int cpCoreNum) {
        this.block.setCpCoreNum(cpCoreNum);
    }
    
    @Override
    public void setEvaluationRate(final Parameters.EvaluationRate evaluationRate) {
        this.block.setEvaluationRate(evaluationRate);
    }
    
    @Override
    public void setEvaluationModel(final Parameters.EvaluationModel evaluationModel) {
        this.block.setEvaluationModel(evaluationModel);
    }
    
    @Override
    public void addRateParameter(final RateParameter os, final double value) {
        this.block.addRateParameter(os, value);
    }
    
    @Override
    public void setEmpiricalRateParameters(final Parameters.EvaluationModel model) {
        this.block.setEmpiricalRateParameters(model);
    }
    
    @Override
    public void setEvaluationAAFrequency(final Parameters.EvaluationStateFrequencies evaluationAAFrequency) {
        this.block.setEvaluationStateFrequencies(evaluationAAFrequency);
    }
    
    @Override
    public void setEvaluationDistribution(final Parameters.EvaluationDistribution evaluationDistribution, final int evaluationDistributionSubsets) {
        this.block.setEvaluationDistribution(evaluationDistribution, evaluationDistributionSubsets);
    }
    
    @Override
    public void setEvaluationDistribution(final Parameters.EvaluationDistribution evaluationDistribution) {
        this.block.setEvaluationDistribution(evaluationDistribution);
    }
    
    @Override
    public void setEvaluationDistributionShape(final double evaluationDistributionShape) {
        this.block.setEvaluationDistributionShape(evaluationDistributionShape);
    }
    
    @Override
    public void setEvaluationPInv(final double evaluationPInv) {
        this.block.setEvaluationPInv(evaluationPInv);
    }
    
    @Override
    public void addSpecificRateParameter(final Charset c, final RateParameter os, final double value) {
        this.block.addSpecificRateParameter(c, os, value);
    }
    
    @Override
    public void addSpecificDistributionShape(final Charset c, final Double value) {
        this.block.addSpecificDistributionShape(c, value);
    }
    
    @Override
    public void addSpecificPInv(final Charset c, final Double value) {
        this.block.addSpecificPInv(c, value);
    }
    
    @Override
    public void setOptimization(final Parameters.Optimization optimization) {
        this.block.setOptimization(optimization);
    }
    
    @Override
    public void setOptimizationUse(final double optimizationUse) {
        this.block.setOptimizationUse(optimizationUse);
    }
    
    @Override
    public void setOptimizationAlgorithm(final Parameters.OptimizationAlgorithm optimizationAlgorithm) {
        this.block.setOptimizationAlgorithm(optimizationAlgorithm);
    }
    
    @Override
    public void addOptimizationTarget(final Parameters.OptimizationTarget target) {
        this.block.addOptimizationTarget(target);
    }
    
    @Override
    public void setStartingTreeGeneration(final Parameters.StartingTreeGeneration startingTreeGeneration) {
        this.block.setStartingTreeGeneration(startingTreeGeneration);
    }
    
    @Override
    public void setStartingTreeGenerationRange(final double startingTreeGenerationRange) {
        this.block.setStartingTreeGenerationRange(startingTreeGenerationRange);
    }
    
    @Override
    public void setStartingTreeModel(final Parameters.DistanceModel startingTreeModel) {
        this.block.setStartingTreeModel(startingTreeModel);
    }
    
    @Override
    public void setStartingTreeDistribution(final Parameters.StartingTreeDistribution startingTreeDistribution, final double startingTreeDistributionShape) {
        this.block.setStartingTreeDistribution(startingTreeDistribution, startingTreeDistributionShape);
    }
    
    @Override
    public void setStartingTreeDistribution(final Parameters.StartingTreeDistribution startingTreeDistribution) {
        this.block.setStartingTreeDistribution(startingTreeDistribution);
    }
    
    @Override
    public void setStartingTreePInv(final double startingTreePInv) {
        this.block.setStartingTreePInv(startingTreePInv);
    }
    
    @Override
    public void setStartingTreePInvPi(final Parameters.StartingTreePInvPi startingTreePInvPi) {
        this.block.setStartingTreePInvPi(startingTreePInvPi);
    }
    
    @Override
    public void addOperator(final Parameters.Operator operator) {
        this.block.addOperator(operator);
    }
    
    @Override
    public void addOperatorsParameter(final Parameters.Operator operator, final int parameter) {
        this.block.addOperatorsParameter(operator, parameter);
    }
    
    @Override
    public void addOperatorsFrequency(final Parameters.Operator operator, final double frequency) {
        this.block.addOperatorsFrequency(operator, frequency);
    }
    
    @Override
    public void addOperatorIsDynamic(final Parameters.Operator operator) {
        this.block.addOperatorIsDynamic(operator);
    }
    
    @Override
    public void setDynamicInterval(final int dynamicInterval) {
        this.block.setDynamicInterval(dynamicInterval);
    }
    
    @Override
    public void setDynamicMin(final double dynamicMin) {
        this.block.setDynamicMin(dynamicMin);
    }
    
    @Override
    public void setOperatorSelection(final Parameters.OperatorSelection operatorSelection) {
        this.block.setOperatorSelection(operatorSelection);
    }
    
    @Override
    public void setColumnRemoval(final Parameters.ColumnRemoval columnRemoval) {
        this.block.setColumnRemoval(columnRemoval);
    }
    
    @Override
    public void setLabel(final String label) {
        this.block.setLabel(label);
    }
    
    @Override
    public void setOutputDir(final String outputDir) {
        this.block.setOutputDir(outputDir);
    }
    
    @Override
    public void addSufficientStopCondition(final Parameters.HeuristicStopCondition condition) {
        this.block.addSufficientStopCondition(condition);
    }
    
    @Override
    public void removeSufficientStopCondition(final Parameters.HeuristicStopCondition condition) {
        this.block.removeSufficientStopCondition(condition);
    }
    
    @Override
    public void addNecessaryStopCondition(final Parameters.HeuristicStopCondition condition) {
        this.block.addNecessaryStopCondition(condition);
    }
    
    @Override
    public void setStopCriterionSteps(final int stopCriterionSteps) {
        this.block.setStopCriterionSteps(stopCriterionSteps);
    }
    
    @Override
    public void setStopCriterionTime(final double stopCriterionTime) {
        this.block.setStopCriterionTime(stopCriterionTime);
    }
    
    @Override
    public void setStopCriterionAutoSteps(final int stopCriterionAutoSteps) {
        this.block.setStopCriterionAutoSteps(stopCriterionAutoSteps);
    }
    
    @Override
    public void setStopCriterionAutoThreshold(final double stopCriterionAutoThreshold) {
        this.block.setStopCriterionAutoThreshold(stopCriterionAutoThreshold);
    }
    
    @Override
    public void setStopCriterionConsensusMRE(final double mre) {
        this.block.setStopCriterionConsensusMRE(mre);
    }
    
    @Override
    public void setStopCriterionConsensusGeneration(final int generation) {
        this.block.setStopCriterionConsensusGeneration(generation);
    }
    
    @Override
    public void setStopCriterionConsensusInterval(final int interval) {
        this.block.setStopCriterionConsensusInterval(interval);
    }
    
    @Override
    public void setReplicatesStopCondition(final Parameters.ReplicatesStopCondition stopCondition) {
        this.block.setReplicatesStopCondition(stopCondition);
    }
    
    @Override
    public void setReplicatesMRE(final double mre) {
        this.block.setReplicatesMRE(mre);
    }
    
    @Override
    public void setReplicatesNumber(final int replicates) {
        this.block.setReplicatesNumber(replicates);
    }
    
    @Override
    public void setReplicatesMinimum(final int replicates) {
        this.block.setReplicatesMinimum(replicates);
    }
    
    @Override
    public void setReplicatesMaximum(final int replicates) {
        this.block.setReplicatesMaximum(replicates);
    }
    
    @Override
    public void setReplicatesInterval(final int interval) {
        this.block.setReplicatesInterval(interval);
    }
    
    @Override
    public void setReplicatesParallel(final int parallel) {
        this.block.setReplicatesParallel(parallel);
    }
    
    @Override
    public void addOutgroup(final String taxa) {
        this.block.addOutgroup(taxa);
    }
    
    @Override
    public void addDeletedTaxa(final String taxa) {
        this.block.addDeletedTaxa(taxa);
    }
    
    @Override
    public void addCharset(final Charset charset) {
        this.block.addCharset(charset);
    }
    
    @Override
    public Set<Charset> getCharset() {
        return this.block.getCharset();
    }
    
    @Override
    public void addExcludedCharset(final Charset charset) {
        this.block.addExcludedCharset(charset);
    }
    
    @Override
    public void addPartition(final Charset charset) {
        this.block.addPartition(charset);
    }
    
    @Override
    public void addLogFile(final Parameters.LogFile log) {
        this.block.addLogFile(log);
    }
    
    @Override
    public void setGridReplicate(final boolean gridReplicate) {
        this.block.setGridReplicate(gridReplicate);
    }
    
    @Override
    public void setGridOutput(final String gridOutput) {
        this.block.setGridOutput(gridOutput);
    }
    
    @Override
    public void setUseGrid(final boolean useGrid) {
        this.block.setUseGrid(useGrid);
    }
    
    @Override
    public void setGridServer(final String gridServer) {
        this.block.setGridServer(gridServer);
    }
    
    @Override
    public void setGridClient(final String gridClient) {
        this.block.setGridClient(gridClient);
    }
    
    @Override
    public void setGridModule(final String gridModule) {
        this.block.setGridModule(gridModule);
    }
    
    @Override
    public void setUseCloud(final boolean useCloud) {
        this.block.setUseCloud(useCloud);
    }
    
    @Override
    public void setCloudServer(final String cloudServer) {
        this.block.setCloudServer(cloudServer);
    }
    
    @Override
    public void setCloudClient(final String cloudClient) {
        this.block.setCloudClient(cloudClient);
    }
    
    @Override
    public void setCloudModule(final String cloudModule) {
        this.block.setCloudModule(cloudModule);
    }
    
    @Override
    public void setLikelihoodCalculationType(final Parameters.LikelihoodCalculationType type) {
        this.block.setLikelihoodCalculationType(type);
    }
    
    @Override
    public void setDatatype(final DataType dataType) {
        this.block.setDataType(dataType);
    }
    
    @Override
    public void setCodonRange(final int startPosition, final int endPosition) {
        this.block.setCodonDomainRange(startPosition, endPosition);
    }
    
    @Override
    public void setCodonTransitionTable(final Parameters.CodonTransitionTableType tableType) {
        this.block.setCodonTable(tableType);
    }
}

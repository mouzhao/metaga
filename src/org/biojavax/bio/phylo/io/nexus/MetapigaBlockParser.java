// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

import java.util.Iterator;
import metapiga.utilities.Tools;
import metapiga.RateParameter;
import metapiga.modelization.data.DataType;
import org.biojava.bio.seq.io.ParseException;
import metapiga.parameters.Parameters;
import metapiga.modelization.Charset;

public class MetapigaBlockParser extends NexusBlockParser.Abstract
{
    private Param heuristic;
    private Param hcRestart;
    private Param saSchedule;
    private Param saLundyC;
    private Param saLundyAlpha;
    private Param saInitAccept;
    private Param saFinalAccept;
    private Param saDeltaL;
    private Param saReheating;
    private Param saCooling;
    private Param gaNInd;
    private Param gaSelection;
    private Param gaRecombination;
    private Param gaOperatorChange;
    private Param cpConsensus;
    private Param cpOperator;
    private Param cpNPop;
    private Param cpNInd;
    private Param cpTolerance;
    private Param cpHybridization;
    private Param cpSelection;
    private Param cpRecombination;
    private Param cpOperatorChange;
    private Param cpNCore;
    private Param evaluation;
    private Param evaluationRate;
    private Param evaluationDatatype;
    private Param evaluationCodonRange;
    private Param evaluationCodonTransitionTable;
    private Param evaluationModel;
    private Param evaluationRateParameters;
    private Param evaluationAAFrequency;
    private Param evaluationDistribution;
    private Param evaluationDistributionShape;
    private Param evaluationPInv;
    private Param specific;
    private Param specificName;
    private Param optimization;
    private Param optimizationAlgorithm;
    private Param optimizationTarget;
    private Param startingTree;
    private Param startingTreeGeneration;
    private Param startingTreeModel;
    private Param startingTreeDistribution;
    private Param startingTreePInv;
    private Param startingTreePInvPi;
    private Param operators;
    private Param operatorsSelection;
    private Param frequencies;
    private Param dynamic;
    private Param dynamicOperators;
    private Param dynamicDInt;
    private Param dynamicMin;
    private Param settings;
    private Param settingsGaps;
    private Param settingsRemoveCol;
    private Param settingsLabel;
    private Param settingsOutputDir;
    private Param settingsGrid;
    private Param settingsGridServer;
    private Param settingsGridClient;
    private Param settingsGridModule;
    private Param settingsCloud;
    private Param settingsCloudServer;
    private Param settingsCloudClient;
    private Param settingsCloudModule;
    private Param settingsLikelihoodCalculationType;
    private Param outgroup;
    private Param delete;
    private Param charset;
    private Param charsetName;
    private Param charsetSet;
    private Param exclude;
    private Param partition;
    private Param stopAfer;
    private Param stopAferSteps;
    private Param stopAferTime;
    private Param stopAferAutoSteps;
    private Param stopAferAutoThreshold;
    private Param stopAferConsensus;
    private Param stopAferConsensusMRE;
    private Param stopAferConsensusGeneration;
    private Param stopAferConsensusInterval;
    private Param stopAferNecessaryConditions;
    private Param replicates;
    private Param replicatesStopCondition;
    private Param replicatesNumber;
    private Param replicatesMinimum;
    private Param replicatesMaximum;
    private Param replicatesInterval;
    private Param replicatesParallel;
    private Param log;
    private Param gridReplicate;
    private Param gridOutput;
    private Param cloudReplicate;
    private Param cloudOutput;
    private Charset currentCharset;
    private String currentSpecific;
    
    public MetapigaBlockParser(final MetapigaBlockListener blockListener) {
        super(blockListener);
        this.heuristic = new Param("HEURISTIC", ParamType.SIMPLE);
        this.hcRestart = new Param("RESTART", ParamType.EQUAL);
        this.saSchedule = new Param("SCHEDULE", ParamType.EQUAL);
        this.saLundyC = new Param("LUNC", ParamType.EQUAL);
        this.saLundyAlpha = new Param("LUNALPHA", ParamType.EQUAL);
        this.saInitAccept = new Param("INITACCEPT", ParamType.EQUAL);
        this.saFinalAccept = new Param("FINALACCEPT", ParamType.EQUAL);
        this.saDeltaL = new Param("DELTAL", ParamType.EQUAL);
        this.saReheating = new Param("REHEATING", ParamType.EQUAL);
        this.saCooling = new Param("COOLING", ParamType.EQUAL);
        this.gaNInd = new Param("NIND", ParamType.EQUAL);
        this.gaSelection = new Param("SELECTION", ParamType.EQUAL);
        this.gaRecombination = new Param("RECOMBINATION", ParamType.EQUAL);
        this.gaOperatorChange = new Param("OPERATORAPPLIEDTO", ParamType.EQUAL);
        this.cpConsensus = new Param("CONSENSUS", ParamType.EQUAL);
        this.cpOperator = new Param("OPERATOR", ParamType.EQUAL);
        this.cpNPop = new Param("NPOP", ParamType.EQUAL);
        this.cpNInd = new Param("NIND", ParamType.EQUAL);
        this.cpTolerance = new Param("TOLERANCE", ParamType.EQUAL);
        this.cpHybridization = new Param("HYBRIDIZATION", ParamType.EQUAL);
        this.cpSelection = new Param("SELECTION", ParamType.EQUAL);
        this.cpRecombination = new Param("RECOMBINATION", ParamType.EQUAL);
        this.cpOperatorChange = new Param("OPERATORAPPLIEDTO", ParamType.EQUAL);
        this.cpNCore = new Param("NCORE", ParamType.EQUAL);
        this.evaluation = new Param("EVALUATION", ParamType.KEYWORD);
        this.evaluationRate = new Param("RATE", ParamType.EQUAL);
        this.evaluationDatatype = new Param("DATATYPE", ParamType.EQUAL);
        this.evaluationCodonRange = new Param("CODONRANGE", ParamType.BRACES);
        this.evaluationCodonTransitionTable = new Param("DNACODE", ParamType.EQUAL);
        this.evaluationModel = new Param("MODEL", ParamType.EQUAL);
        this.evaluationRateParameters = new Param("RATEPARAM", ParamType.BRACES);
        this.evaluationAAFrequency = new Param("AAFREQ", ParamType.EQUAL);
        this.evaluationDistribution = new Param("DISTRIBUTION", ParamType.EQUAL);
        this.evaluationDistributionShape = new Param("DISTSHAPE", ParamType.EQUAL);
        this.evaluationPInv = new Param("PINV", ParamType.EQUAL);
        this.specific = new Param("SPECIFICPARTPARAM", ParamType.KEYWORD);
        this.specificName = new Param("PARTNAME", ParamType.EQUAL);
        this.optimization = new Param("OPTIMIZATION", ParamType.SIMPLE);
        this.optimizationAlgorithm = new Param("ALGO", ParamType.EQUAL);
        this.optimizationTarget = new Param("TARGET", ParamType.BRACES);
        this.startingTree = new Param("STARTINGTREE", ParamType.KEYWORD);
        this.startingTreeGeneration = new Param("GENERATION", ParamType.EQUAL);
        this.startingTreeModel = new Param("MODEL", ParamType.EQUAL);
        this.startingTreeDistribution = new Param("DISTRIBUTION", ParamType.EQUAL);
        this.startingTreePInv = new Param("PINV", ParamType.EQUAL);
        this.startingTreePInvPi = new Param("PI", ParamType.EQUAL);
        this.operators = new Param("OPERATORS", ParamType.BRACES);
        this.operatorsSelection = new Param("SELECTION", ParamType.EQUAL);
        this.frequencies = new Param("FREQUENCIES", ParamType.BRACES);
        this.dynamic = new Param("DYNAMICFREQ", ParamType.KEYWORD);
        this.dynamicOperators = new Param("DYNOPERATORS", ParamType.BRACES);
        this.dynamicDInt = new Param("DINT", ParamType.EQUAL);
        this.dynamicMin = new Param("DMIN", ParamType.EQUAL);
        this.settings = new Param("SETTINGS", ParamType.KEYWORD);
        this.settingsGaps = new Param("GAPS", ParamType.EQUAL);
        this.settingsRemoveCol = new Param("REMOVECOL", ParamType.EQUAL);
        this.settingsLabel = new Param("LABEL", ParamType.EQUAL);
        this.settingsOutputDir = new Param("DIR", ParamType.EQUAL);
        this.settingsGrid = new Param("GRID", ParamType.KEYWORD);
        this.settingsGridServer = new Param("SERVER", ParamType.EQUAL);
        this.settingsGridClient = new Param("CLIENT", ParamType.EQUAL);
        this.settingsGridModule = new Param("MODULE", ParamType.EQUAL);
        this.settingsCloud = new Param("CLOUD", ParamType.KEYWORD);
        this.settingsCloudServer = new Param("SERVER", ParamType.EQUAL);
        this.settingsCloudClient = new Param("CLIENT", ParamType.EQUAL);
        this.settingsCloudModule = new Param("MODULE", ParamType.EQUAL);
        this.settingsLikelihoodCalculationType = new Param("LIKELIHOODCOMPUTATION", ParamType.EQUAL);
        this.outgroup = new Param("OUTGROUP", ParamType.BRACES);
        this.delete = new Param("DELETE", ParamType.BRACES);
        this.charset = new Param("CHARSET", ParamType.KEYWORD);
        this.charsetName = new Param("NAME", ParamType.EQUAL);
        this.charsetSet = new Param("SET", ParamType.BRACES);
        this.exclude = new Param("EXCLUDE", ParamType.BRACES);
        this.partition = new Param("PARTITION", ParamType.BRACES);
        this.stopAfer = new Param("STOPAFTER", ParamType.KEYWORD);
        this.stopAferSteps = new Param("STEPS", ParamType.EQUAL);
        this.stopAferTime = new Param("TIME", ParamType.EQUAL);
        this.stopAferAutoSteps = new Param("AUTO", ParamType.EQUAL);
        this.stopAferAutoThreshold = new Param("AUTOTHRESHOLD", ParamType.EQUAL);
        this.stopAferConsensus = new Param("CONSENSUS", ParamType.KEYWORD);
        this.stopAferConsensusMRE = new Param("MRE", ParamType.EQUAL);
        this.stopAferConsensusGeneration = new Param("GENERATION", ParamType.EQUAL);
        this.stopAferConsensusInterval = new Param("INTERVAL", ParamType.EQUAL);
        this.stopAferNecessaryConditions = new Param("NECESSARY", ParamType.BRACES);
        this.replicates = new Param("REPLICATES", ParamType.KEYWORD);
        this.replicatesStopCondition = new Param("AUTOSTOP", ParamType.EQUAL);
        this.replicatesNumber = new Param("RNUM", ParamType.EQUAL);
        this.replicatesMinimum = new Param("RMIN", ParamType.EQUAL);
        this.replicatesMaximum = new Param("RMAX", ParamType.EQUAL);
        this.replicatesInterval = new Param("INTERVAL", ParamType.EQUAL);
        this.replicatesParallel = new Param("PARALLEL", ParamType.EQUAL);
        this.log = new Param("LOG", ParamType.BRACES);
        this.gridReplicate = new Param("GRIDREPLICATE", ParamType.KEYWORD);
        this.gridOutput = new Param("OUTPUT", ParamType.EQUAL);
        this.cloudReplicate = new Param("GRIDREPLICATE", ParamType.KEYWORD);
        this.cloudOutput = new Param("OUTPUT", ParamType.EQUAL);
        this.currentSpecific = null;
    }
    
    private void resetSubParams() {
        this.hcRestart.setExpected(false);
        this.saSchedule.setExpected(false);
        this.saLundyC.setExpected(false);
        this.saLundyAlpha.setExpected(false);
        this.saInitAccept.setExpected(false);
        this.saFinalAccept.setExpected(false);
        this.saDeltaL.setExpected(false);
        this.saReheating.setExpected(false);
        this.saCooling.setExpected(false);
        this.gaNInd.setExpected(false);
        this.gaSelection.setExpected(false);
        this.gaRecombination.setExpected(false);
        this.gaOperatorChange.setExpected(false);
        this.cpConsensus.setExpected(false);
        this.cpOperator.setExpected(false);
        this.cpNPop.setExpected(false);
        this.cpNInd.setExpected(false);
        this.cpTolerance.setExpected(false);
        this.cpHybridization.setExpected(false);
        this.cpSelection.setExpected(false);
        this.cpRecombination.setExpected(false);
        this.cpOperatorChange.setExpected(false);
        this.cpNCore.setExpected(false);
        this.evaluationRate.setExpected(false);
        this.evaluationModel.setExpected(false);
        this.evaluationRateParameters.setExpected(false);
        this.evaluationAAFrequency.setExpected(false);
        this.evaluationDistribution.setExpected(false);
        this.evaluationDistributionShape.setExpected(false);
        this.evaluationPInv.setExpected(false);
        this.specificName.setExpected(false);
        this.optimizationAlgorithm.setExpected(false);
        this.optimizationTarget.setExpected(false);
        this.startingTreeGeneration.setExpected(false);
        this.startingTreeModel.setExpected(false);
        this.startingTreeDistribution.setExpected(false);
        this.startingTreePInv.setExpected(false);
        this.startingTreePInvPi.setExpected(false);
        this.operatorsSelection.setExpected(false);
        this.dynamicOperators.setExpected(false);
        this.dynamicDInt.setExpected(false);
        this.dynamicMin.setExpected(false);
        this.settingsGaps.setExpected(false);
        this.settingsRemoveCol.setExpected(false);
        this.settingsLabel.setExpected(false);
        this.settingsOutputDir.setExpected(false);
        this.settingsGrid.setExpected(false);
        this.settingsGridClient.setExpected(false);
        this.settingsGridModule.setExpected(false);
        this.settingsGridServer.setExpected(false);
        this.settingsCloud.setExpected(false);
        this.settingsCloudClient.setExpected(false);
        this.settingsCloudModule.setExpected(false);
        this.settingsCloudServer.setExpected(false);
        this.settingsLikelihoodCalculationType.setExpected(false);
        this.charsetName.setExpected(false);
        this.charsetSet.setExpected(false);
        this.stopAferSteps.setExpected(false);
        this.stopAferTime.setExpected(false);
        this.stopAferAutoSteps.setExpected(false);
        this.stopAferAutoThreshold.setExpected(false);
        this.stopAferConsensus.setExpected(false);
        this.stopAferConsensusMRE.setExpected(false);
        this.stopAferConsensusGeneration.setExpected(false);
        this.stopAferConsensusInterval.setExpected(false);
        this.stopAferNecessaryConditions.setExpected(false);
        this.replicatesStopCondition.setExpected(false);
        this.replicatesNumber.setExpected(false);
        this.replicatesMinimum.setExpected(false);
        this.replicatesMaximum.setExpected(false);
        this.replicatesInterval.setExpected(false);
        this.replicatesParallel.setExpected(false);
        this.gridOutput.setExpected(false);
        this.cloudOutput.setExpected(false);
    }
    
    private void setMainParams(final boolean expected) {
        this.heuristic.setExpected(expected);
        this.evaluation.setExpected(expected);
        this.specific.setExpected(expected);
        this.optimization.setExpected(expected);
        this.startingTree.setExpected(expected);
        this.operators.setExpected(expected);
        this.frequencies.setExpected(expected);
        this.dynamic.setExpected(expected);
        this.settings.setExpected(expected);
        this.outgroup.setExpected(expected);
        this.delete.setExpected(expected);
        this.charset.setExpected(expected);
        this.exclude.setExpected(expected);
        this.partition.setExpected(expected);
        this.stopAfer.setExpected(expected);
        this.replicates.setExpected(expected);
        this.log.setExpected(expected);
        this.gridReplicate.setExpected(expected);
    }
    
    public void resetStatus() {
        this.setMainParams(true);
        this.resetSubParams();
    }
    
    private String extractValue(final String token) {
        final String[] parts = token.split("[\\(\\)]");
        return parts[0];
    }
    
    private String extractParameter(final String token) {
        final String[] parts = token.split("[\\(\\)]");
        return (parts.length > 1) ? parts[1] : null;
    }
    
    private boolean hasParameter(final String token) {
        return token.contains("(");
    }
    
    @Override
    public void parseToken(String token) throws ParseException {
        final String originalCaseToken = token;
        token = token.toUpperCase();
        try {
            if (token.trim().length() == 0) {
                return;
            }
            if (this.heuristic.expect(token)) {
                final String s = this.heuristic.parse(token);
                if (s != null) {
                    if (Parameters.Heuristic.SA.toString().equalsIgnoreCase(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setHeuristic(Parameters.Heuristic.SA);
                        this.saSchedule.setExpected(true);
                        this.saLundyC.setExpected(true);
                        this.saLundyAlpha.setExpected(true);
                        this.saInitAccept.setExpected(true);
                        this.saFinalAccept.setExpected(true);
                        this.saDeltaL.setExpected(true);
                        this.saReheating.setExpected(true);
                        this.saCooling.setExpected(true);
                    }
                    else if (Parameters.Heuristic.GA.toString().equalsIgnoreCase(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setHeuristic(Parameters.Heuristic.GA);
                        this.gaNInd.setExpected(true);
                        this.gaSelection.setExpected(true);
                        this.gaRecombination.setExpected(true);
                        this.gaOperatorChange.setExpected(true);
                    }
                    else if (Parameters.Heuristic.CP.toString().equalsIgnoreCase(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setHeuristic(Parameters.Heuristic.CP);
                        this.cpConsensus.setExpected(true);
                        this.cpOperator.setExpected(true);
                        this.cpNPop.setExpected(true);
                        this.cpNInd.setExpected(true);
                        this.cpTolerance.setExpected(true);
                        this.cpHybridization.setExpected(true);
                        this.cpSelection.setExpected(true);
                        this.cpRecombination.setExpected(true);
                        this.cpOperatorChange.setExpected(true);
                        this.cpNCore.setExpected(true);
                    }
                    else if (Parameters.Heuristic.HC.toString().equalsIgnoreCase(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setHeuristic(Parameters.Heuristic.HC);
                        this.hcRestart.setExpected(true);
                    }
                    else if (Parameters.Heuristic.BS.toString().equalsIgnoreCase(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setHeuristic(Parameters.Heuristic.BS);
                    }
                }
            }
            else if (this.hcRestart.expect(token)) {
                final String s = this.hcRestart.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setHcRestart(Integer.parseInt(s));
                }
            }
            else if (this.saSchedule.expect(token)) {
                final String s = this.saSchedule.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setSaSchedule(Parameters.SASchedule.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setSaSchedule(Parameters.SASchedule.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setSaScheduleParam(Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.saLundyC.expect(token)) {
                final String s = this.saLundyC.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setSaLundyC(Double.parseDouble(s));
                }
            }
            else if (this.saLundyAlpha.expect(token)) {
                final String s = this.saLundyAlpha.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setSaLundyAlpha(Double.parseDouble(s));
                }
            }
            else if (this.saInitAccept.expect(token)) {
                final String s = this.saInitAccept.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setSaInitAccept(Double.parseDouble(s));
                }
            }
            else if (this.saFinalAccept.expect(token)) {
                final String s = this.saFinalAccept.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setSaFinalAccept(Double.parseDouble(s));
                }
            }
            else if (this.saDeltaL.expect(token)) {
                final String s = this.saDeltaL.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setSaDeltaL(Parameters.SADeltaL.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setSaDeltaL(Parameters.SADeltaL.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setSaDeltaLPercent(Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.saReheating.expect(token)) {
                final String s = this.saReheating.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setSaReheatingType(Parameters.SAReheating.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setSaReheatingType(Parameters.SAReheating.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setSaReheatingValue(Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.saCooling.expect(token)) {
                final String s = this.saCooling.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setSaCoolingType(Parameters.SACooling.valueOf(s));
                    }
                    else {
                        final Parameters.SACooling cooling = Parameters.SACooling.valueOf(this.extractValue(s));
                        final String param = this.extractParameter(s);
                        ((MetapigaBlockListener)this.getBlockListener()).setSaCoolingType(cooling);
                        if (cooling == Parameters.SACooling.STEPS) {
                            ((MetapigaBlockListener)this.getBlockListener()).setSaCoolingSteps(Integer.parseInt(param));
                        }
                        else if (cooling == Parameters.SACooling.SF) {
                            final String[] params = param.split(",");
                            if (params.length < 2) {
                                throw new ParseException("Error for token : " + param + "\nCOOLING has been set to SF, but number of successes and failures has not been set.\nYou must give those parameters between parentheses and WITHOUT ANY BLANK before or within the parenthesis.\nExample : COOLING = SF(10,100)");
                            }
                            ((MetapigaBlockListener)this.getBlockListener()).setSaCoolingSuccesses(Integer.parseInt(params[0]));
                            ((MetapigaBlockListener)this.getBlockListener()).setSaCoolingFailures(Integer.parseInt(params[1]));
                        }
                    }
                }
            }
            else if (this.gaNInd.expect(token)) {
                final String s = this.gaNInd.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setGaIndNum(Integer.parseInt(s));
                }
            }
            else if (this.gaSelection.expect(token)) {
                final String s = this.gaSelection.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setGaSelection(Parameters.GASelection.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setGaSelection(Parameters.GASelection.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setGaReplacementStrength(Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.gaRecombination.expect(token)) {
                final String s = this.gaRecombination.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setGaRecombination(Double.parseDouble(s));
                }
            }
            else if (this.gaOperatorChange.expect(token)) {
                final String s = this.gaOperatorChange.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setGaOperatorChange(Parameters.GAOperatorChange.valueOf(s));
                }
            }
            else if (this.cpConsensus.expect(token)) {
                final String s = this.cpConsensus.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpConsensus(Parameters.CPConsensus.valueOf(s));
                }
            }
            else if (this.cpOperator.expect(token)) {
                final String s = this.cpOperator.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpOperator(Parameters.CPOperator.valueOf(s));
                }
            }
            else if (this.cpNPop.expect(token)) {
                final String s = this.cpNPop.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpPopNum(Integer.parseInt(s));
                }
            }
            else if (this.cpNInd.expect(token)) {
                final String s = this.cpNInd.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpIndNum(Integer.parseInt(s));
                }
            }
            else if (this.cpTolerance.expect(token)) {
                final String s = this.cpTolerance.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpTolerance(Double.parseDouble(s));
                }
            }
            else if (this.cpHybridization.expect(token)) {
                final String s = this.cpHybridization.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpHybridization(Double.parseDouble(s));
                }
            }
            else if (this.cpSelection.expect(token)) {
                final String s = this.cpSelection.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setCpSelection(Parameters.CPSelection.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setCpSelection(Parameters.CPSelection.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setCpReplacementStrength(Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.cpRecombination.expect(token)) {
                final String s = this.cpRecombination.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpRecombination(Double.parseDouble(s));
                }
            }
            else if (this.cpOperatorChange.expect(token)) {
                final String s = this.cpOperatorChange.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpOperatorChange(Parameters.CPOperatorChange.valueOf(s));
                }
            }
            else if (this.cpNCore.expect(token)) {
                final String s = this.cpNCore.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCpCoreNum(Integer.parseInt(s));
                }
            }
            else if (this.evaluation.expect(token)) {
                this.currentSpecific = null;
                this.resetSubParams();
                this.evaluation.setExpected(false);
                this.evaluationRate.setExpected(true);
                this.evaluationModel.setExpected(true);
                this.evaluationRateParameters.setExpected(true);
                this.evaluationAAFrequency.setExpected(true);
                this.evaluationDistribution.setExpected(true);
                this.evaluationDistributionShape.setExpected(true);
                this.evaluationPInv.setExpected(true);
                this.evaluationDatatype.setExpected(true);
            }
            else if (this.evaluationDatatype.expect(token)) {
                final String s = this.evaluationDatatype.parse(token);
                this.evaluationCodonRange.setExpected(true);
                this.evaluationCodonTransitionTable.setExpected(true);
                this.evaluationDatatype.setExpected(false);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setDatatype(DataType.valueOf(s));
                }
            }
            else if (this.evaluationCodonRange.expect(token)) {
                final String s = this.evaluationCodonRange.parse(token);
                if (s != null) {
                    final String[] values = s.split("-");
                    final int from = Integer.parseInt(values[0]);
                    final int to = Integer.parseInt(values[1]);
                    ((MetapigaBlockListener)this.getBlockListener()).setCodonRange(from, to);
                }
            }
            else if (this.evaluationCodonTransitionTable.expect(token)) {
                final String s = this.evaluationCodonTransitionTable.parse(token);
                this.evaluationCodonTransitionTable.setExpected(false);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setCodonTransitionTable(Parameters.CodonTransitionTableType.valueOf(s));
                }
            }
            else if (this.specific.expect(token)) {
                this.currentSpecific = "UNKNOWN";
                this.resetSubParams();
                this.specificName.setExpected(true);
                this.evaluationRateParameters.setExpected(true);
                this.evaluationDistributionShape.setExpected(true);
                this.evaluationPInv.setExpected(true);
            }
            else if (this.specificName.expect(token)) {
                final String s = this.specificName.parse(token);
                if (s != null) {
                    this.currentSpecific = s;
                }
            }
            else if (this.evaluationRate.expect(token)) {
                final String s = this.evaluationRate.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setEvaluationRate(Parameters.EvaluationRate.valueOf(s));
                }
            }
            else if (this.evaluationModel.expect(token)) {
                final String s = this.evaluationModel.parse(token);
                if (s != null) {
                    final Parameters.EvaluationModel model = Parameters.EvaluationModel.valueOf(s);
                    ((MetapigaBlockListener)this.getBlockListener()).setEvaluationModel(model);
                    if (model.isEmpirical()) {
                        ((MetapigaBlockListener)this.getBlockListener()).setEmpiricalRateParameters(model);
                    }
                }
            }
            else if (this.evaluationRateParameters.expect(token)) {
                final String s = this.evaluationRateParameters.parse(token);
                if (s != null) {
                    if (this.currentSpecific == null) {
                        ((MetapigaBlockListener)this.getBlockListener()).addRateParameter(RateParameter.valueOf(this.extractValue(s)), Double.parseDouble(this.extractParameter(s)));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).addSpecificRateParameter(new Charset(this.currentSpecific), RateParameter.valueOf(this.extractValue(s)), Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.evaluationAAFrequency.expect(token)) {
                final String s = this.evaluationAAFrequency.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setEvaluationAAFrequency(Parameters.EvaluationStateFrequencies.valueOf(s));
                }
            }
            else if (this.evaluationDistribution.expect(token)) {
                final String s = this.evaluationDistribution.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setEvaluationDistribution(Parameters.EvaluationDistribution.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setEvaluationDistribution(Parameters.EvaluationDistribution.valueOf(this.extractValue(s)), Integer.parseInt(this.extractParameter(s)));
                    }
                }
            }
            else if (this.evaluationDistributionShape.expect(token)) {
                final String s = this.evaluationDistributionShape.parse(token);
                if (s != null) {
                    if (this.currentSpecific == null) {
                        ((MetapigaBlockListener)this.getBlockListener()).setEvaluationDistributionShape(Double.parseDouble(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).addSpecificDistributionShape(new Charset(this.currentSpecific), Double.parseDouble(s));
                    }
                }
            }
            else if (this.evaluationPInv.expect(token)) {
                final String s = this.evaluationPInv.parse(token);
                if (s != null) {
                    if (this.currentSpecific == null) {
                        ((MetapigaBlockListener)this.getBlockListener()).setEvaluationPInv(Double.parseDouble(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).addSpecificPInv(new Charset(this.currentSpecific), Double.parseDouble(s));
                    }
                }
            }
            else if (this.optimization.expect(token)) {
                final String s = this.optimization.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setOptimization(Parameters.Optimization.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setOptimization(Parameters.Optimization.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setOptimizationUse(Double.parseDouble(this.extractParameter(s)));
                    }
                    this.optimization.setExpected(false);
                    this.optimizationAlgorithm.setExpected(true);
                    this.optimizationTarget.setExpected(true);
                }
            }
            else if (this.optimizationAlgorithm.expect(token)) {
                final String s = this.optimizationAlgorithm.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setOptimizationAlgorithm(Parameters.OptimizationAlgorithm.valueOf(s));
                }
            }
            else if (this.optimizationTarget.expect(token)) {
                final String s = this.optimizationTarget.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addOptimizationTarget(Parameters.OptimizationTarget.valueOf(s));
                }
            }
            else if (this.startingTree.expect(token)) {
                this.resetSubParams();
                this.startingTree.setExpected(false);
                this.startingTreeGeneration.setExpected(true);
                this.startingTreeModel.setExpected(true);
                this.startingTreeDistribution.setExpected(true);
                this.startingTreePInv.setExpected(true);
                this.startingTreePInvPi.setExpected(true);
            }
            else if (this.startingTreeGeneration.expect(token)) {
                final String s = this.startingTreeGeneration.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setStartingTreeGeneration(Parameters.StartingTreeGeneration.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setStartingTreeGeneration(Parameters.StartingTreeGeneration.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setStartingTreeGenerationRange(Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.startingTreeModel.expect(token)) {
                final String s = this.startingTreeModel.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStartingTreeModel(Parameters.DistanceModel.valueOf(s));
                }
            }
            else if (this.startingTreeDistribution.expect(token)) {
                final String s = this.startingTreeDistribution.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setStartingTreeDistribution(Parameters.StartingTreeDistribution.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setStartingTreeDistribution(Parameters.StartingTreeDistribution.valueOf(this.extractValue(s)), Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.startingTreePInv.expect(token)) {
                final String s = this.startingTreePInv.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStartingTreePInv(Double.parseDouble(s));
                }
            }
            else if (this.startingTreePInvPi.expect(token)) {
                final String s = this.startingTreePInvPi.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStartingTreePInvPi(Parameters.StartingTreePInvPi.valueOf(s));
                }
            }
            else if (this.operatorsSelection.expect(token)) {
                final String s = this.operatorsSelection.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setOperatorSelection(Parameters.OperatorSelection.valueOf(s));
                }
            }
            else if (this.operators.expect(token)) {
                if (token.startsWith(this.operators.keyword)) {
                    this.resetSubParams();
                }
                final String s = this.operators.parse(token);
                if (s != null) {
                    this.operatorsSelection.setExpected(true);
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).addOperator(Parameters.Operator.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).addOperator(Parameters.Operator.valueOf(this.extractValue(s)));
                        String param2 = this.extractParameter(s);
                        if (param2.equals("ALL")) {
                            param2 = String.valueOf(0);
                        }
                        if (param2.equals("RANDOM")) {
                            param2 = String.valueOf(1);
                        }
                        ((MetapigaBlockListener)this.getBlockListener()).addOperatorsParameter(Parameters.Operator.valueOf(this.extractValue(s)), Integer.parseInt(param2));
                    }
                }
            }
            else if (this.frequencies.expect(token)) {
                this.resetSubParams();
                final String s = this.frequencies.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addOperatorsFrequency(Parameters.Operator.valueOf(this.extractValue(s)), Double.parseDouble(this.extractParameter(s)));
                }
            }
            else if (this.dynamic.expect(token)) {
                this.resetSubParams();
                this.dynamic.setExpected(false);
                this.dynamicOperators.setExpected(true);
                this.dynamicDInt.setExpected(true);
                this.dynamicMin.setExpected(true);
            }
            else if (this.dynamicOperators.expect(token)) {
                final String s = this.dynamicOperators.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addOperatorIsDynamic(Parameters.Operator.valueOf(s));
                }
            }
            else if (this.dynamicDInt.expect(token)) {
                final String s = this.dynamicDInt.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setDynamicInterval(Integer.parseInt(s));
                }
            }
            else if (this.dynamicMin.expect(token)) {
                final String s = this.dynamicMin.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setDynamicMin(Double.parseDouble(s));
                }
            }
            else if (this.settings.expect(token)) {
                this.resetSubParams();
                this.settings.setExpected(false);
                this.settingsGaps.setExpected(true);
                this.settingsRemoveCol.setExpected(true);
                this.settingsLabel.setExpected(true);
                this.settingsOutputDir.setExpected(true);
                this.settingsGrid.setExpected(true);
                this.settingsCloud.setExpected(true);
                this.settingsLikelihoodCalculationType.setExpected(true);
            }
            else if (this.settingsGaps.expect(token)) {
                String s = this.settingsGaps.parse(token);
                if (s != null) {
                    if (s.equalsIgnoreCase("REMOVE")) {
                        s = "GAP";
                    }
                    else {
                        s = "NONE";
                    }
                    ((MetapigaBlockListener)this.getBlockListener()).setColumnRemoval(Parameters.ColumnRemoval.valueOf(s));
                }
            }
            else if (this.settingsRemoveCol.expect(token)) {
                final String s = this.settingsRemoveCol.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setColumnRemoval(Parameters.ColumnRemoval.valueOf(s));
                }
            }
            else if (this.settingsLabel.expect(token)) {
                final String s = this.settingsLabel.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setLabel(s);
                }
            }
            else if (this.settingsOutputDir.expect(token)) {
                final String s = this.settingsOutputDir.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setOutputDir(s);
                }
            }
            else if (this.settingsGrid.expect(token)) {
                ((MetapigaBlockListener)this.getBlockListener()).setUseGrid(true);
                this.settingsGrid.setExpected(false);
                this.settingsGridServer.setExpected(true);
                this.settingsGridClient.setExpected(true);
                this.settingsGridModule.setExpected(true);
            }
            else if (this.settingsGridServer.expect(token)) {
                final String s = this.settingsGridServer.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setGridServer(s);
                }
            }
            else if (this.settingsGridClient.expect(token)) {
                final String s = this.settingsGridClient.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setGridClient(s);
                }
            }
            else if (this.settingsGridModule.expect(token)) {
                final String s = this.settingsGridModule.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setGridModule(s);
                }
            }
            else if (this.settingsCloud.expect(token)) {
                ((MetapigaBlockListener)this.getBlockListener()).setUseCloud(true);
                this.settingsCloud.setExpected(false);
            }
            else if (this.settingsLikelihoodCalculationType.expect(token)) {
                final String typeValue = this.settingsLikelihoodCalculationType.parse(originalCaseToken);
                if (Parameters.LikelihoodCalculationType.CLASSIC.toString().equalsIgnoreCase(typeValue)) {
                    ((MetapigaBlockListener)this.getBlockListener()).setLikelihoodCalculationType(Parameters.LikelihoodCalculationType.CLASSIC);
                }
                else {
                    if (!Parameters.LikelihoodCalculationType.GPU.toString().equalsIgnoreCase(typeValue)) {
                        throw new ParseException("LIKELIHOODCALCULATION: '" + token + "' not apllicable for the likelihood calculation type");
                    }
                    ((MetapigaBlockListener)this.getBlockListener()).setLikelihoodCalculationType(Parameters.LikelihoodCalculationType.GPU);
                }
                this.settingsLikelihoodCalculationType.setExpected(false);
            }
            else if (this.stopAfer.expect(token)) {
                this.resetSubParams();
                this.stopAfer.setExpected(false);
                this.stopAferSteps.setExpected(true);
                this.stopAferTime.setExpected(true);
                this.stopAferAutoSteps.setExpected(true);
                this.stopAferAutoThreshold.setExpected(true);
                this.stopAferConsensus.setExpected(true);
                this.stopAferNecessaryConditions.setExpected(true);
            }
            else if (this.stopAferSteps.expect(token)) {
                final String s = this.stopAferSteps.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStopCriterionSteps(Integer.parseInt(s));
                    ((MetapigaBlockListener)this.getBlockListener()).addSufficientStopCondition(Parameters.HeuristicStopCondition.STEPS);
                }
            }
            else if (this.stopAferTime.expect(token)) {
                final String s = this.stopAferTime.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStopCriterionTime(Double.parseDouble(s));
                    ((MetapigaBlockListener)this.getBlockListener()).addSufficientStopCondition(Parameters.HeuristicStopCondition.TIME);
                }
            }
            else if (this.stopAferAutoSteps.expect(token)) {
                final String s = this.stopAferAutoSteps.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStopCriterionAutoSteps(Integer.parseInt(s));
                    ((MetapigaBlockListener)this.getBlockListener()).addSufficientStopCondition(Parameters.HeuristicStopCondition.AUTO);
                }
            }
            else if (this.stopAferAutoThreshold.expect(token)) {
                final String s = this.stopAferAutoThreshold.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStopCriterionAutoThreshold(Double.parseDouble(s));
                    ((MetapigaBlockListener)this.getBlockListener()).addSufficientStopCondition(Parameters.HeuristicStopCondition.AUTO);
                }
            }
            else if (this.stopAferConsensus.expect(token)) {
                ((MetapigaBlockListener)this.getBlockListener()).addSufficientStopCondition(Parameters.HeuristicStopCondition.CONSENSUS);
                this.stopAferConsensus.setExpected(false);
                this.stopAferConsensusMRE.setExpected(true);
                this.stopAferConsensusGeneration.setExpected(true);
                this.stopAferConsensusInterval.setExpected(true);
            }
            else if (this.stopAferConsensusMRE.expect(token)) {
                final String s = this.stopAferConsensusMRE.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStopCriterionConsensusMRE(Double.parseDouble(s));
                }
            }
            else if (this.stopAferConsensusGeneration.expect(token)) {
                final String s = this.stopAferConsensusGeneration.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStopCriterionConsensusGeneration(Integer.parseInt(s));
                }
            }
            else if (this.stopAferConsensusInterval.expect(token)) {
                final String s = this.stopAferConsensusInterval.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setStopCriterionConsensusInterval(Integer.parseInt(s));
                }
            }
            else if (this.stopAferNecessaryConditions.expect(token)) {
                final String s = this.stopAferNecessaryConditions.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addNecessaryStopCondition(Parameters.HeuristicStopCondition.valueOf(s));
                    ((MetapigaBlockListener)this.getBlockListener()).removeSufficientStopCondition(Parameters.HeuristicStopCondition.valueOf(s));
                }
            }
            else if (this.replicates.expect(token)) {
                this.resetSubParams();
                this.replicatesStopCondition.setExpected(true);
                this.replicatesNumber.setExpected(true);
                this.replicatesMinimum.setExpected(true);
                this.replicatesMaximum.setExpected(true);
                this.replicatesInterval.setExpected(true);
                this.replicatesParallel.setExpected(true);
            }
            else if (this.replicatesStopCondition.expect(token)) {
                final String s = this.replicatesStopCondition.parse(token);
                if (s != null) {
                    if (!this.hasParameter(s)) {
                        ((MetapigaBlockListener)this.getBlockListener()).setReplicatesStopCondition(Parameters.ReplicatesStopCondition.valueOf(s));
                    }
                    else {
                        ((MetapigaBlockListener)this.getBlockListener()).setReplicatesStopCondition(Parameters.ReplicatesStopCondition.valueOf(this.extractValue(s)));
                        ((MetapigaBlockListener)this.getBlockListener()).setReplicatesMRE(Double.parseDouble(this.extractParameter(s)));
                    }
                }
            }
            else if (this.replicatesNumber.expect(token)) {
                final String s = this.replicatesNumber.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setReplicatesNumber(Integer.parseInt(s));
                }
            }
            else if (this.replicatesMinimum.expect(token)) {
                final String s = this.replicatesMinimum.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setReplicatesMinimum(Integer.parseInt(s));
                }
            }
            else if (this.replicatesMaximum.expect(token)) {
                final String s = this.replicatesMaximum.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setReplicatesMaximum(Integer.parseInt(s));
                }
            }
            else if (this.replicatesInterval.expect(token)) {
                final String s = this.replicatesInterval.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setReplicatesInterval(Integer.parseInt(s));
                }
            }
            else if (this.replicatesParallel.expect(token)) {
                final String s = this.replicatesParallel.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setReplicatesParallel(Integer.parseInt(s));
                }
            }
            else if (this.outgroup.expect(token)) {
                final String s = this.outgroup.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addOutgroup(s);
                }
            }
            else if (this.delete.expect(token)) {
                final String s = this.delete.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addDeletedTaxa(s);
                }
            }
            else if (this.charset.expect(token)) {
                this.resetSubParams();
                this.currentCharset = new Charset("UNKNOWN CHARSET");
                this.charsetName.setExpected(true);
                this.charsetSet.setExpected(true);
            }
            else if (this.charsetName.expect(token)) {
                final String s = this.charsetName.parse(token);
                if (s != null) {
                    this.currentCharset.setLabel(s);
                }
                if (!this.charsetSet.isSomethingExpected() && !this.charsetName.isSomethingExpected()) {
                    ((MetapigaBlockListener)this.getBlockListener()).addCharset(this.currentCharset);
                }
            }
            else if (this.charsetSet.expect(token)) {
                final String s = this.charsetSet.parse(token);
                if (s != null) {
                    boolean foundDefinedCharset = false;
                    for (final Charset ch : ((MetapigaBlockListener)this.getBlockListener()).getCharset()) {
                        if (ch.getLabel().equals(s)) {
                            this.currentCharset.merge(ch);
                            foundDefinedCharset = true;
                            break;
                        }
                    }
                    if (!foundDefinedCharset) {
                        try {
                            this.currentCharset.addRange(s);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            throw new ParseException("'" + token + "' is not a valid charset position. \nIf it's another defined charset, you must define it before this one.");
                        }
                    }
                }
                if (!this.charsetSet.isSomethingExpected() && !this.charsetName.isSomethingExpected()) {
                    ((MetapigaBlockListener)this.getBlockListener()).addCharset(this.currentCharset);
                }
            }
            else if (this.exclude.expect(token)) {
                final String s = this.exclude.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addExcludedCharset(new Charset(s));
                }
            }
            else if (this.partition.expect(token)) {
                final String s = this.partition.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addPartition(new Charset(s));
                }
            }
            else if (this.log.expect(token)) {
                final String s = this.log.parse(token);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).addLogFile(Parameters.LogFile.valueOf(s));
                }
            }
            else if (this.gridReplicate.expect(token)) {
                ((MetapigaBlockListener)this.getBlockListener()).setGridReplicate(true);
                this.resetSubParams();
                this.gridOutput.setExpected(true);
            }
            else {
                if (!this.gridOutput.expect(token)) {
                    throw new ParseException(String.valueOf(this.expectedTokens()) + "but '" + token + "' was found.");
                }
                final String s = this.gridOutput.parse(originalCaseToken);
                if (s != null) {
                    ((MetapigaBlockListener)this.getBlockListener()).setGridOutput(s);
                }
            }
        }
        catch (NumberFormatException e2) {
            e2.printStackTrace();
            throw new ParseException("'" + token + "' is not a valid number.");
        }
        catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            throw new ParseException("'" + token + "' is not a valid argument.");
        }
        catch (Exception e4) {
            e4.printStackTrace();
            throw new ParseException(Tools.getErrorMessage(e4));
        }
    }
    
    private String expectedTokens() {
        String res = "";
        res = String.valueOf(res) + this.heuristic.toString();
        res = String.valueOf(res) + this.hcRestart.toString();
        res = String.valueOf(res) + this.saSchedule.toString();
        res = String.valueOf(res) + this.saLundyC.toString();
        res = String.valueOf(res) + this.saLundyAlpha.toString();
        res = String.valueOf(res) + this.saInitAccept.toString();
        res = String.valueOf(res) + this.saFinalAccept.toString();
        res = String.valueOf(res) + this.saDeltaL.toString();
        res = String.valueOf(res) + this.saReheating.toString();
        res = String.valueOf(res) + this.saCooling.toString();
        res = String.valueOf(res) + this.gaNInd.toString();
        res = String.valueOf(res) + this.gaSelection.toString();
        res = String.valueOf(res) + this.gaRecombination.toString();
        res = String.valueOf(res) + this.gaOperatorChange.toString();
        res = String.valueOf(res) + this.cpConsensus.toString();
        res = String.valueOf(res) + this.cpOperator.toString();
        res = String.valueOf(res) + this.cpNPop.toString();
        res = String.valueOf(res) + this.cpNInd.toString();
        res = String.valueOf(res) + this.cpTolerance.toString();
        res = String.valueOf(res) + this.cpHybridization.toString();
        res = String.valueOf(res) + this.cpSelection.toString();
        res = String.valueOf(res) + this.cpRecombination.toString();
        res = String.valueOf(res) + this.cpOperatorChange.toString();
        res = String.valueOf(res) + this.cpNCore.toString();
        res = String.valueOf(res) + this.evaluation.toString();
        res = String.valueOf(res) + this.evaluationRate.toString();
        res = String.valueOf(res) + this.evaluationModel.toString();
        res = String.valueOf(res) + this.evaluationDistribution.toString();
        res = String.valueOf(res) + this.evaluationPInv.toString();
        res = String.valueOf(res) + this.optimization.toString();
        res = String.valueOf(res) + this.optimizationAlgorithm.toString();
        res = String.valueOf(res) + this.optimizationTarget.toString();
        res = String.valueOf(res) + this.evaluationRateParameters.toString();
        res = String.valueOf(res) + this.startingTree.toString();
        res = String.valueOf(res) + this.startingTreeGeneration.toString();
        res = String.valueOf(res) + this.startingTreeModel.toString();
        res = String.valueOf(res) + this.startingTreeDistribution.toString();
        res = String.valueOf(res) + this.startingTreePInv.toString();
        res = String.valueOf(res) + this.startingTreePInvPi.toString();
        res = String.valueOf(res) + this.operators.toString();
        res = String.valueOf(res) + this.operatorsSelection.toString();
        res = String.valueOf(res) + this.frequencies.toString();
        res = String.valueOf(res) + this.dynamic.toString();
        res = String.valueOf(res) + this.dynamicOperators.toString();
        res = String.valueOf(res) + this.dynamicDInt.toString();
        res = String.valueOf(res) + this.dynamicMin.toString();
        res = String.valueOf(res) + this.settings.toString();
        res = String.valueOf(res) + this.settingsRemoveCol.toString();
        res = String.valueOf(res) + this.settingsLabel.toString();
        res = String.valueOf(res) + this.settingsOutputDir.toString();
        res = String.valueOf(res) + this.settingsGrid.toString();
        res = String.valueOf(res) + this.settingsGridServer.toString();
        res = String.valueOf(res) + this.settingsGridClient.toString();
        res = String.valueOf(res) + this.settingsGridModule.toString();
        res = String.valueOf(res) + this.outgroup.toString();
        res = String.valueOf(res) + this.delete.toString();
        res = String.valueOf(res) + this.charset.toString();
        res = String.valueOf(res) + this.charsetName.toString();
        res = String.valueOf(res) + this.charsetSet.toString();
        res = String.valueOf(res) + this.exclude.toString();
        res = String.valueOf(res) + this.partition.toString();
        res = String.valueOf(res) + this.stopAfer.toString();
        res = String.valueOf(res) + this.stopAferSteps.toString();
        res = String.valueOf(res) + this.stopAferTime.toString();
        res = String.valueOf(res) + this.stopAferAutoSteps.toString();
        res = String.valueOf(res) + this.stopAferAutoThreshold.toString();
        res = String.valueOf(res) + this.stopAferConsensus.toString();
        res = String.valueOf(res) + this.stopAferConsensusMRE.toString();
        res = String.valueOf(res) + this.stopAferConsensusGeneration.toString();
        res = String.valueOf(res) + this.stopAferConsensusInterval.toString();
        res = String.valueOf(res) + this.stopAferNecessaryConditions.toString();
        res = String.valueOf(res) + this.replicates.toString();
        res = String.valueOf(res) + this.replicatesStopCondition.toString();
        res = String.valueOf(res) + this.replicatesNumber.toString();
        res = String.valueOf(res) + this.replicatesMinimum.toString();
        res = String.valueOf(res) + this.replicatesMaximum.toString();
        res = String.valueOf(res) + this.replicatesInterval.toString();
        res = String.valueOf(res) + this.replicatesParallel.toString();
        res = String.valueOf(res) + this.log.toString();
        res = String.valueOf(res) + this.gridReplicate.toString();
        res = String.valueOf(res) + this.gridOutput.toString();
        return res;
    }
    
    public enum ParamType
    {
        KEYWORD("KEYWORD", 0), 
        SIMPLE("SIMPLE", 1), 
        EQUAL("EQUAL", 2), 
        BRACES("BRACES", 3);
        
        private ParamType(final String s, final int n) {
        }
    }
    
    public class Param
    {
        private ParamType type;
        private String keyword;
        private boolean keywordExpected;
        private boolean equalExpected;
        private boolean valueExpected;
        private boolean openingExpected;
        private boolean closingExpected;
        
        public Param(final String keyword, final ParamType type) {
            this.type = type;
            this.keyword = keyword;
            this.keywordExpected = false;
            this.equalExpected = false;
            this.valueExpected = false;
            this.openingExpected = false;
            this.closingExpected = false;
        }
        
        public void setExpected(final boolean status) {
            this.keywordExpected = status;
            this.equalExpected = false;
            this.valueExpected = false;
            this.openingExpected = false;
            this.closingExpected = false;
        }
        
        @Override
        public String toString() {
            if (this.keywordExpected) {
                return String.valueOf(this.keyword) + " expected \n";
            }
            if (this.equalExpected) {
                return String.valueOf(this.keyword) + " '=' expected \n";
            }
            if (this.valueExpected) {
                return String.valueOf(this.keyword) + " value expected \n";
            }
            if (this.openingExpected) {
                return String.valueOf(this.keyword) + " '(' expected \n";
            }
            if (this.closingExpected) {
                return String.valueOf(this.keyword) + " ')' expected \n";
            }
            return "";
        }
        
        public boolean expect(final String token) {
            switch (this.type) {
                case KEYWORD: {
                    return this.keywordExpected && this.keyword.equalsIgnoreCase(token);
                }
                case SIMPLE: {
                    return (this.keywordExpected && this.keyword.equalsIgnoreCase(token)) || this.valueExpected;
                }
                case EQUAL: {
                    return (this.keywordExpected && token.toUpperCase().startsWith(this.keyword)) || this.equalExpected || this.valueExpected;
                }
                case BRACES: {
                    return (this.keywordExpected && token.toUpperCase().startsWith(this.keyword)) || this.openingExpected || this.valueExpected || this.closingExpected;
                }
                default: {
                    return false;
                }
            }
        }
        
        public boolean isSomethingExpected() {
            return this.keywordExpected || this.equalExpected || this.valueExpected || this.openingExpected || this.closingExpected;
        }
        
        public String parse(final String token) {
            switch (this.type) {
                case KEYWORD: {
                    MetapigaBlockParser.this.resetSubParams();
                    break;
                }
                case SIMPLE: {
                    if (this.keywordExpected) {
                        MetapigaBlockParser.this.resetSubParams();
                        this.keywordExpected = false;
                        this.valueExpected = true;
                        break;
                    }
                    if (this.valueExpected) {
                        this.valueExpected = false;
                        return token;
                    }
                    break;
                }
                case EQUAL: {
                    if (this.keywordExpected) {
                        this.keywordExpected = false;
                        MetapigaBlockParser.this.setMainParams(false);
                        if (token.indexOf("=") < 0) {
                            this.equalExpected = true;
                            break;
                        }
                        final String[] parts = token.split("=");
                        if (parts.length > 1) {
                            MetapigaBlockParser.this.setMainParams(true);
                            return parts[1];
                        }
                        this.valueExpected = true;
                        break;
                    }
                    else if (this.equalExpected) {
                        this.equalExpected = false;
                        if (token.length() > 1) {
                            MetapigaBlockParser.this.setMainParams(true);
                            return token.substring(1);
                        }
                        this.valueExpected = true;
                        break;
                    }
                    else {
                        if (this.valueExpected) {
                            this.valueExpected = false;
                            MetapigaBlockParser.this.setMainParams(true);
                            return token;
                        }
                        break;
                    }
                }
                case BRACES: {
                    if (this.keywordExpected) {
                        this.keywordExpected = false;
                        if (token.endsWith("{")) {
                            this.valueExpected = true;
                            break;
                        }
                        final String[] parts = token.split("\\{");
                        if (parts.length <= 1) {
                            this.openingExpected = true;
                            break;
                        }
                        if (parts[1].endsWith("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            MetapigaBlockParser.this.setMainParams(true);
                            return parts[1].substring(0, parts[1].length() - 1);
                        }
                        this.valueExpected = true;
                        this.closingExpected = true;
                        return parts[1];
                    }
                    else if (this.openingExpected) {
                        this.openingExpected = false;
                        if (token.length() <= 1) {
                            this.valueExpected = true;
                            break;
                        }
                        if (token.endsWith("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            MetapigaBlockParser.this.setMainParams(true);
                            return token.substring(1, token.length() - 1);
                        }
                        this.valueExpected = true;
                        this.closingExpected = true;
                        return token.substring(1);
                    }
                    else if (this.valueExpected) {
                        if (this.closingExpected && token.equals("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            MetapigaBlockParser.this.setMainParams(true);
                            break;
                        }
                        if (token.endsWith("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            MetapigaBlockParser.this.setMainParams(true);
                            return token.substring(0, token.length() - 1);
                        }
                        this.closingExpected = true;
                        return token;
                    }
                    else {
                        if (this.closingExpected) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            MetapigaBlockParser.this.setMainParams(true);
                            break;
                        }
                        break;
                    }
                }
            }
            return null;
        }
    }
}

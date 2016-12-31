// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.modeltest;

import java.util.Set;
import metapiga.optimization.GA;
import java.util.HashSet;
import metapiga.modelization.data.EmpiricalModels;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.Tree;
import java.util.HashMap;
import javax.swing.JDialog;

import metapiga.modelization.Charset;
import java.util.Map;
import metapiga.parameters.Parameters;
import metapiga.RateParameter;

public class ModelSampling
{
    private Parameters P;
    private Map<String, Double> likelihoods;
    private Map<String, Integer> nbrParameters;
    private Map<String, Map<Charset, Map<RateParameter, Double>>> paramOfR;
    private Map<String, Map<Charset, Double>> gammaShapes;
    private Map<String, Map<Charset, Double>> invariants;
    private Map<String, Parameters.EvaluationStateFrequencies> stateFrequencies;
    private JDialog parentDialog;
    
    public ModelSampling(final Parameters parameters) {
        this.likelihoods = new HashMap<String, Double>();
        this.nbrParameters = new HashMap<String, Integer>();
        this.paramOfR = new HashMap<String, Map<Charset, Map<RateParameter, Double>>>();
        this.gammaShapes = new HashMap<String, Map<Charset, Double>>();
        this.invariants = new HashMap<String, Map<Charset, Double>>();
        this.stateFrequencies = new HashMap<String, Parameters.EvaluationStateFrequencies>();
        this.P = parameters;
    }
    
    public void setParentDialog(final JDialog parent) {
        this.parentDialog = parent;
    }
    
    private String getModelKey(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq) {
        String s = model.toString();
        if (withGamma) {
            s = String.valueOf(s) + "+G";
        }
        else {
            s = String.valueOf(s) + "-G";
        }
        if (withInvariant) {
            s = String.valueOf(s) + "+I";
        }
        else {
            s = String.valueOf(s) + "-I";
        }
        if (model.isEmpirical()) {
            switch (freq) {
                case ESTIMATED: {
                    s = String.valueOf(s) + "+F";
                    break;
                }
                case EMPIRICAL: {
                    s = String.valueOf(s) + "-F";
                    break;
                }
            }
        }
        return s;
    }
    
    private void addSampling(final Tree T, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq) throws NullAncestorException, UnrootableTreeException {
        final String key = this.getModelKey(T.getEvaluationModel(), T.getEvaluationDistribution() == Parameters.EvaluationDistribution.GAMMA, withInvariant, freq);
        T.setName(key);
        this.likelihoods.put(key, T.getEvaluation());
        this.stateFrequencies.put(key, freq);
        int nParam = 0;
        for (final Charset c : T.getPartitions()) {
            if (!T.getEvaluationModel().hasEqualBaseFrequencies()) {
                nParam += T.getEvaluationModel().getDataType().numOfStates() - 1;
            }
            Map<Charset, Map<RateParameter, Double>> map1;
            if (this.paramOfR.containsKey(key)) {
                map1 = this.paramOfR.get(key);
            }
            else {
                map1 = new HashMap<Charset, Map<RateParameter, Double>>();
            }
            map1.put(c, new HashMap<RateParameter, Double>((Map<? extends RateParameter, ? extends Double>) T.getEvaluationRateParameters(c)));
            this.paramOfR.put(key, map1);
            nParam += T.getEvaluationModel().getNumRateParameters();
            if (T.getEvaluationDistribution() == Parameters.EvaluationDistribution.GAMMA) {
                Map<Charset, Double> map2;
                if (this.gammaShapes.containsKey(key)) {
                    map2 = this.gammaShapes.get(key);
                }
                else {
                    map2 = new HashMap<Charset, Double>();
                }
                map2.put(c, T.getEvaluationGammaShape(c));
                this.gammaShapes.put(key, map2);
                ++nParam;
            }
            if (withInvariant) {
                Map<Charset, Double> map3;
                if (this.invariants.containsKey(key)) {
                    map3 = this.invariants.get(key);
                }
                else {
                    map3 = new HashMap<Charset, Double>();
                }
                map3.put(c, T.getEvaluationPInv(c));
                this.invariants.put(key, map3);
                ++nParam;
            }
        }
        this.nbrParameters.put(key, nParam);
        T.deleteLikelihoodComputation();
    }
    
    public void createSampling(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq, final int idSampling, final int maxSamplings) throws Exception {
        Tree NJT = this.P.getNJT().clone();
        NJT.setEvaluationModel(model);
        for (final Charset c : NJT.getPartitions()) {
            switch (model) {
                case GTR: {
                    NJT.setEvaluationRateParameter(c, RateParameter.A, 0.5);
                    NJT.setEvaluationRateParameter(c, RateParameter.B, 0.5);
                    NJT.setEvaluationRateParameter(c, RateParameter.C, 0.5);
                    NJT.setEvaluationRateParameter(c, RateParameter.D, 0.5);
                    NJT.setEvaluationRateParameter(c, RateParameter.E, 0.5);
                    break;
                }
                case TN93: {
                    NJT.setEvaluationRateParameter(c, RateParameter.K1, 0.5);
                    NJT.setEvaluationRateParameter(c, RateParameter.K2, 0.5);
                    break;
                }
                case HKY85: {
                    NJT.setEvaluationRateParameter(c, RateParameter.K, 0.5);
                    break;
                }
                case K2P: {
                    NJT.setEvaluationRateParameter(c, RateParameter.K, 0.5);
                    break;
                }
                case GTR20: {
                    RateParameter[] parametersOfModel;
                    for (int length = (parametersOfModel = RateParameter.getParametersOfModel(Parameters.EvaluationModel.GTR20)).length, i = 0; i < length; ++i) {
                        final RateParameter r = parametersOfModel[i];
                        NJT.setEvaluationRateParameter(c, r, 0.5);
                    }
                    break;
                }
                case GY: {
                    NJT.setEvaluationRateParameter(c, RateParameter.KAPPA, 0.5);
                    NJT.setEvaluationRateParameter(c, RateParameter.OMEGA, 0.5);
                    break;
                }
                case GTR64: {
                    RateParameter[] parametersOfModel2;
                    for (int length2 = (parametersOfModel2 = RateParameter.getParametersOfModel(Parameters.EvaluationModel.GTR64)).length, j = 0; j < length2; ++j) {
                        final RateParameter r = parametersOfModel2[j];
                        NJT.setEvaluationRateParameter(c, r, 0.5);
                    }
                    break;
                }
                case ECM:
                case WAG:
                case JTT:
                case DAYHOFF:
                case VT:
                case BLOSUM62:
                case CPREV:
                case MTREV:
                case RTREV:
                case MTMAM: {
                    for (final Map.Entry<RateParameter, Double> e : EmpiricalModels.getRateParameters(model).entrySet()) {
                        NJT.setEvaluationRateParameter(c, e.getKey(), e.getValue());
                    }
                }
                case GTR2: {}
                case JC: {}
            }
            NJT.setEvaluationDistributionShape(c, 1.0);
        }
        NJT.setEvaluationDistribution(withGamma ? Parameters.EvaluationDistribution.GAMMA : Parameters.EvaluationDistribution.NONE);
        NJT.setEvaluationStateFrequencies(freq);
        final Set<Parameters.OptimizationTarget> targets = new HashSet<Parameters.OptimizationTarget>();
        if (model.getNumRateParameters() > 0 && !model.isEmpirical()) {
            targets.add(Parameters.OptimizationTarget.R);
        }
        if (withGamma) {
            targets.add(Parameters.OptimizationTarget.GAMMA);
        }
        if (withInvariant) {
            targets.add(Parameters.OptimizationTarget.PINV);
        }
        if (targets.size() > 0) {
            final GA optimizer = new GA(NJT, targets, 200);
            String message = "Optimizing parameters values for " + model;
            if (model.isEmpirical()) {
                if (freq == Parameters.EvaluationStateFrequencies.EMPIRICAL) {
                    message = String.valueOf(message) + " (using empirical aa frequencies)";
                }
                else if (freq == Parameters.EvaluationStateFrequencies.ESTIMATED) {
                    message = String.valueOf(message) + " (using estimated aa frequencies)";
                }
            }
            message = String.valueOf(message) + (withGamma ? " with Gamma" : "") + (withInvariant ? (String.valueOf(withGamma ? " and" : "") + " with Invariant") : "");
            NJT = optimizer.getOptimizedTreeWithProgress(this.parentDialog, message, idSampling, maxSamplings);
        }
        this.addSampling(NJT, withInvariant, freq);
    }
    
    public void createSampling(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq) throws Exception {
        this.createSampling(model, withGamma, withInvariant, freq, 0, 1);
    }
    
    public void clear() {
        this.likelihoods.clear();
        this.nbrParameters.clear();
        this.paramOfR.clear();
        this.gammaShapes.clear();
        this.invariants.clear();
        this.stateFrequencies.clear();
    }
    
    public double getLikelihood(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq) throws Exception {
        final String key = this.getModelKey(model, withGamma, withInvariant, freq);
        if (!this.likelihoods.containsKey(key)) {
            this.createSampling(model, withGamma, withInvariant, freq);
        }
        return this.likelihoods.get(key);
    }
    
    public int getNbrParameters(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq) throws Exception {
        final String key = this.getModelKey(model, withGamma, withInvariant, freq);
        if (!this.nbrParameters.containsKey(key)) {
            this.createSampling(model, withGamma, withInvariant, freq);
        }
        return this.nbrParameters.get(key);
    }
    
    public Map<RateParameter, Double> getRateParameters(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq, final Charset c) throws Exception {
        final String key = this.getModelKey(model, withGamma, withInvariant, freq);
        if (!this.paramOfR.containsKey(key)) {
            this.createSampling(model, withGamma, withInvariant, freq);
        }
        if (!this.paramOfR.get(key).containsKey(c)) {
            throw new Exception("Charset " + c.getLabel() + " was not found.");
        }
        return this.paramOfR.get(key).get(c);
    }
    
    public double getDistributionShape(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq, final Charset c) throws Exception {
        final String key = this.getModelKey(model, withGamma, withInvariant, freq);
        if (!this.gammaShapes.containsKey(key)) {
            this.createSampling(model, withGamma, withInvariant, freq);
        }
        if (!this.gammaShapes.get(key).containsKey(c)) {
            throw new Exception("Charset " + c.getLabel() + " was not found.");
        }
        return this.gammaShapes.get(key).get(c);
    }
    
    public double getInvariant(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq, final Charset c) throws Exception {
        final String key = this.getModelKey(model, withGamma, withInvariant, freq);
        if (!this.invariants.containsKey(key)) {
            this.createSampling(model, withGamma, withInvariant, freq);
        }
        if (!this.invariants.get(key).containsKey(c)) {
            throw new Exception("Charset " + c.getLabel() + " was not found.");
        }
        return this.invariants.get(key).get(c);
    }
    
    public Parameters.EvaluationStateFrequencies getStateFrequencies(final Parameters.EvaluationModel model, final boolean withGamma, final boolean withInvariant, final Parameters.EvaluationStateFrequencies freq) throws Exception {
        final String key = this.getModelKey(model, withGamma, withInvariant, freq);
        if (!this.stateFrequencies.containsKey(key)) {
            this.createSampling(model, withGamma, withInvariant, freq);
        }
        return this.stateFrequencies.get(key);
    }
}

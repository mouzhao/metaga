// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import metapiga.modelization.Charset;
import java.util.ArrayList;
import metapiga.trees.exceptions.NoInclusionException;
import metapiga.trees.exceptions.TooManyNeighborsException;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.utilities.Tools;
import metapiga.RateParameter;

import java.util.HashSet;
import java.util.HashMap;
import java.util.EnumMap;
import metapiga.monitors.Monitor;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import metapiga.parameters.Parameters;
import java.util.List;

public class Operators
{
    public static final int RANDOM = 1;
    public static final int ALL = 0;
    private final List<Parameters.Operator> availables;
    private final int numAvailable;
    private final Map<Parameters.Operator, Integer> parameters;
    private final Map<Parameters.Operator, Double> frequencies;
    private final Set<Parameters.Operator> isDynamic;
    private final boolean useDynamicFreq;
    private final int freqInt;
    private final double freqMin;
    private final Parameters.OperatorSelection selection;
    private final double optimizationUse;
    private Parameters.Operator currentOperator;
    private Iterator<Parameters.Operator> orderedIterator;
    private int stepIncrement;
    private int currentStep;
    private Map<Parameters.Operator, Double> globalScoreImprovements;
    private Map<Parameters.Operator, Double> localScoreImprovements;
    private Map<Parameters.Operator, Integer> globalUse;
    private Map<Parameters.Operator, Integer> localUse;
    private Map<Parameters.Operator, Long> globalPerformances;
    private Map<Parameters.Operator, Long> localPerformances;
    private Map<Parameters.Operator, Map<Integer, Integer>> cancelByConsensus;
    private int outgroupTargeted;
    private int ingroupTargeted;
    private final Monitor monitor;
    private final boolean trackDetails;
    private final boolean trackStats;
    private final boolean trackPerf;
    
    public Operators(final List<Parameters.Operator> availableOperators, final Map<Parameters.Operator, Integer> operatorsParameters, final Map<Parameters.Operator, Double> operatorsFrequencies, final Set<Parameters.Operator> operatorIsDynamic, final int dynamicInterval, final double dynamicMin, final Parameters.OperatorSelection operatorSelection, final double optimizationUse, final int stepIncrement, final Monitor monitor) {
        this.monitor = monitor;
        this.trackDetails = monitor.trackOperators();
        this.trackStats = monitor.trackOperatorStats();
        this.trackPerf = monitor.trackPerformances();
        this.availables = availableOperators;
        this.numAvailable = availableOperators.size();
        this.parameters = operatorsParameters;
        this.frequencies = operatorsFrequencies;
        this.isDynamic = operatorIsDynamic;
        if (operatorIsDynamic.size() > 0) {
            this.useDynamicFreq = true;
        }
        else {
            this.useDynamicFreq = false;
        }
        this.freqInt = dynamicInterval;
        this.freqMin = dynamicMin;
        this.selection = operatorSelection;
        this.optimizationUse = optimizationUse;
        this.nextOperator();
        this.stepIncrement = stepIncrement;
        this.currentStep = 0;
        this.globalScoreImprovements = new EnumMap<Parameters.Operator, Double>(Parameters.Operator.class);
        this.localScoreImprovements = new EnumMap<Parameters.Operator, Double>(Parameters.Operator.class);
        this.globalUse = new EnumMap<Parameters.Operator, Integer>(Parameters.Operator.class);
        this.localUse = new EnumMap<Parameters.Operator, Integer>(Parameters.Operator.class);
        this.cancelByConsensus = new EnumMap<Parameters.Operator, Map<Integer, Integer>>(Parameters.Operator.class);
        if (this.trackPerf) {
            this.globalPerformances = new EnumMap<Parameters.Operator, Long>(Parameters.Operator.class);
            this.localPerformances = new EnumMap<Parameters.Operator, Long>(Parameters.Operator.class);
        }
        for (final Parameters.Operator op : this.availables) {
            this.globalScoreImprovements.put(op, 0.0);
            this.localScoreImprovements.put(op, 0.0);
            this.globalUse.put(op, 0);
            this.localUse.put(op, 0);
            this.cancelByConsensus.put(op, new HashMap<Integer, Integer>());
            if (this.trackPerf) {
                this.globalPerformances.put(op, 0L);
                this.localPerformances.put(op, 0L);
            }
        }
        this.outgroupTargeted = 0;
        this.ingroupTargeted = 0;
    }
    
    public synchronized Parameters.Operator getCurrentOperator() {
        return this.currentOperator;
    }
    
    public int getOperatorUse(final Parameters.Operator operator) {
        if (this.globalUse.containsKey(operator)) {
            return this.globalUse.get(operator);
        }
        return 0;
    }
    
    private synchronized void addCancellationByConsensus(final Parameters.Operator operator) {
        final int step = this.currentStep / this.stepIncrement / 100;
        if (!this.cancelByConsensus.get(operator).containsKey(step)) {
            this.cancelByConsensus.get(operator).put(step, 0);
        }
        final int num = this.cancelByConsensus.get(operator).get(step);
        this.cancelByConsensus.get(operator).put(step, num + 1);
    }
    
    private synchronized void ingroupTargeted() {
        ++this.ingroupTargeted;
    }
    
    private synchronized void outgroupTargeted() {
        ++this.outgroupTargeted;
    }
    
    public String getCancellationsByConsensus(final Parameters.Operator operator, final String separator) {
        String res = operator.toString();
        int sum = 0;
        for (final int c : this.cancelByConsensus.get(operator).values()) {
            sum += c;
        }
        res = String.valueOf(res) + separator + sum;
        final Set<Integer> todo = new HashSet<Integer>(this.cancelByConsensus.get(operator).keySet());
        int i = 0;
        while (!todo.isEmpty()) {
            if (todo.remove(i)) {
                res = String.valueOf(res) + separator + this.cancelByConsensus.get(operator).get(i);
            }
            else {
                res = String.valueOf(res) + separator + "0";
            }
            ++i;
        }
        return res;
    }
    
    public synchronized Parameters.Operator nextOperator() {
        switch (this.selection) {
            case ORDERED: {
                if (this.orderedIterator == null || !this.orderedIterator.hasNext()) {
                    this.orderedIterator = this.availables.iterator();
                }
                this.currentOperator = this.orderedIterator.next();
                break;
            }
            case FREQLIST: {
                double frequency;
                Iterator<Parameters.Operator> it;
                Parameters.Operator op;
                for (frequency = Math.random(), it = this.availables.iterator(), op = it.next(); frequency > this.frequencies.get(op) && it.hasNext(); frequency -= this.frequencies.get(op), op = it.next()) {}
                if (frequency > this.frequencies.get(op)) {
                    this.currentOperator = this.availables.get(Tools.randInt(this.numAvailable));
                    break;
                }
                this.currentOperator = op;
                break;
            }
            default: {
                this.currentOperator = this.availables.get(Tools.randInt(this.numAvailable));
                break;
            }
        }
        return this.currentOperator;
    }
    
    private void updateFrequencies() {
        final Set<Parameters.Operator> toUpdate = new HashSet<Parameters.Operator>(this.isDynamic);
        double totalImprovement = 0.0;
        for (final Parameters.Operator op : this.availables) {
            if (this.isDynamic.contains(op)) {
                if (this.localUse.get(op) != 0) {
                    this.localScoreImprovements.put(op, this.localScoreImprovements.get(op) / this.localUse.get(op));
                }
                if (this.localScoreImprovements.get(op) == 0.0) {
                    continue;
                }
                totalImprovement += this.localScoreImprovements.get(op);
            }
        }
        if (totalImprovement != 0.0) {
            for (final Parameters.Operator op : this.availables) {
                if (toUpdate.contains(op) && this.localScoreImprovements.get(op) == 0.0) {
                    if (this.localUse.get(op) != 0) {
                        this.frequencies.put(op, this.freqMin);
                    }
                    toUpdate.remove(op);
                }
            }
            double sumOfNonDynamic = 0.0;
            for (final Parameters.Operator op2 : this.availables) {
                if (!toUpdate.contains(op2)) {
                    sumOfNonDynamic += this.frequencies.get(op2);
                }
            }
            for (final Parameters.Operator op2 : this.availables) {
                if (toUpdate.contains(op2)) {
                    this.frequencies.put(op2, this.localScoreImprovements.get(op2) / totalImprovement * (1.0 - sumOfNonDynamic));
                }
            }
            for (final Parameters.Operator op2 : this.availables) {
                this.localScoreImprovements.put(op2, 0.0);
                this.localUse.put(op2, 0);
                if (this.trackPerf) {
                    this.localPerformances.put(op2, 0L);
                }
            }
        }
    }
    
    public void printStatistics() {
        if (this.trackStats) {
            this.monitor.printOperatorStatistics(this.currentStep / this.stepIncrement, this.globalUse, this.globalScoreImprovements, this.globalPerformances, this.outgroupTargeted, this.ingroupTargeted, this.cancelByConsensus);
        }
    }
    
    public void mutateTree(final Tree tree, final Parameters.Operator op, final Consensus consensus, final Parameters.CPOperator behaviour) throws UnrootableTreeException, NullAncestorException, TooManyNeighborsException, NoInclusionException {
        final double treeEv = tree.getEvaluation();
        final long startTime = this.trackPerf ? System.nanoTime() : 0L;
        switch (op) {
            case NNI: {
                if (consensus != null) {
                    this.NNI(tree, consensus, behaviour);
                    break;
                }
                this.NNI(tree);
                break;
            }
            case SPR: {
                if (consensus != null) {
                    this.SPR(tree, consensus, behaviour);
                    break;
                }
                this.SPR(tree);
                break;
            }
            case TBR: {
                if (consensus != null) {
                    this.TBR(tree, consensus, behaviour);
                    break;
                }
                this.TBR(tree);
                break;
            }
            case TXS: {
                if (consensus != null) {
                    this.TXS(tree, this.parameters.get(op), consensus, behaviour);
                    break;
                }
                this.TXS(tree, this.parameters.get(op));
                break;
            }
            case STS: {
                if (consensus != null) {
                    this.STS(tree, this.parameters.get(op), consensus, behaviour);
                    break;
                }
                this.STS(tree, this.parameters.get(op));
                break;
            }
            case BLM: {
                this.BLM(tree);
                break;
            }
            case BLMINT: {
                this.BLMint(tree);
                break;
            }
            case RPM: {
                this.RPM(tree, this.parameters.get(op));
                break;
            }
            case GDM: {
                this.GDM(tree);
                break;
            }
            case PIM: {
                this.PIM(tree);
                break;
            }
            case APRM: {
                this.APRM(tree);
                break;
            }
        }
        final double improvement = tree.getEvaluation() - treeEv;
        synchronized (this) {
            if (this.trackPerf) {
                this.globalPerformances.put(op, this.globalPerformances.get(op) + System.nanoTime() - startTime);
                this.localPerformances.put(op, this.localPerformances.get(op) + System.nanoTime() - startTime);
            }
            ++this.currentStep;
            this.globalUse.put(op, this.globalUse.get(op) + 1);
            this.localUse.put(op, this.localUse.get(op) + 1);
            if (improvement < 0.0) {
                this.globalScoreImprovements.put(op, this.globalScoreImprovements.get(op) + improvement);
                this.localScoreImprovements.put(op, this.localScoreImprovements.get(op) + improvement);
            }
            if (this.useDynamicFreq && this.currentStep % (this.freqInt * this.stepIncrement) == 0) {
                if (this.trackStats) {
                    this.monitor.printOperatorFrequenciesUpdate(this.currentStep / this.stepIncrement, this.localUse, this.localScoreImprovements, this.localPerformances);
                }
                this.updateFrequencies();
                if (this.trackStats) {
                    this.monitor.printOperatorFrequenciesUpdate(this.frequencies);
                }
            }
        }
    }
    
    public void mutateTree(final Tree tree, final Parameters.Operator operator) throws UnrootableTreeException, NullAncestorException, TooManyNeighborsException, NoInclusionException {
        this.mutateTree(tree, operator, null, null);
    }
    
    public void mutateTree(final Tree tree) throws UnrootableTreeException, NullAncestorException, TooManyNeighborsException, NoInclusionException {
        this.mutateTree(tree, this.currentOperator, null, null);
    }
    
    public void casualMutateTree(final Tree tree, final Parameters.Operator op) throws UnrootableTreeException, NullAncestorException, TooManyNeighborsException {
        switch (op) {
            case NNI: {
                this.NNI(tree);
                break;
            }
            case SPR: {
                this.SPR(tree);
                break;
            }
            case TBR: {
                this.TBR(tree);
                break;
            }
            case TXS: {
                this.TXS(tree, this.parameters.get(op));
                break;
            }
            case STS: {
                this.STS(tree, this.parameters.get(op));
                break;
            }
            case BLM: {
                this.BLM(tree);
                break;
            }
            case BLMINT: {
                this.BLMint(tree);
                break;
            }
            case RPM: {
                this.RPM(tree, this.parameters.get(op));
                break;
            }
            case GDM: {
                this.GDM(tree);
                break;
            }
            case PIM: {
                this.PIM(tree);
                break;
            }
            case APRM: {
                this.APRM(tree);
                break;
            }
        }
    }
    
    public void NNI(final Tree T) throws NullAncestorException, TooManyNeighborsException, UnrootableTreeException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.NNI, false);
        }
        if (T.getNumOfIngroupInodes() < 2) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "NNI not applied, ingroup contains less than 2 internal nodes, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.NNI, false);
            }
            return;
        }
        Node nniSon;
        do {
            nniSon = T.getIngroupInodes().get(Tools.randInt(T.getNumOfIngroupInodes()));
        } while (nniSon == T.getRoot());
        final Node nniParent = nniSon.getAncestorNode();
        final Node swapSon = nniSon.getChildren().get(Tools.randInt(2));
        T.unroot();
        final List<Node> availableSwapParent = new ArrayList<Node>();
        availableSwapParent.addAll(nniParent.getNeighborNodes());
        availableSwapParent.remove(nniSon);
        availableSwapParent.removeAll(T.getOutgroupInodes());
        final Node swapParent = availableSwapParent.get(Tools.randInt(availableSwapParent.size()));
        nniSon.removeNeighborButKeepBranchLength(swapSon);
        nniParent.removeNeighborButKeepBranchLength(swapParent);
        nniSon.addNeighborWithBranchLength(swapParent);
        nniParent.addNeighborWithBranchLength(swapSon);
        T.root();
        T.markNodeToReEvaluate(swapSon);
        T.markNodeToReEvaluate(swapParent);
        T.markNodeToReEvaluate(nniSon);
        T.markNodeToReEvaluate(nniParent);
        T.fireInodeStructureChange();
        this.ingroupTargeted();
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "NNI on branch " + nniParent + "-" + nniSon + " : swap branches " + nniParent + "-" + swapParent + " and " + nniSon + "-" + swapSon);
        }
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.NNI, false);
        }
    }
    
    public void NNI(final Tree T, final Consensus consensus, final Parameters.CPOperator behaviour) throws NullAncestorException, TooManyNeighborsException, UnrootableTreeException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.NNI, true);
        }
        if (T.getNumOfIngroupInodes() < 2) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "NNI not applied, ingroup contains less than 2 internal nodes, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.NNI, true);
            }
            return;
        }
        Node nniSon = null;
        Node nniParent = null;
        switch (behaviour) {
            case SUPERVISED: {
                final List<Branch> validCandidates = consensus.getNNIValidCandidates(T);
                if (validCandidates.size() == 0) {
                    this.addCancellationByConsensus(Parameters.Operator.NNI);
                    if (this.trackDetails) {
                        this.monitor.printOperatorInfos(T, "Supervised NNI canceled by consensus, no valid candidate found.", consensus);
                    }
                    if (this.trackDetails) {
                        this.monitor.printTreeAfterOperator(T, Parameters.Operator.NNI, true);
                    }
                    return;
                }
                final Branch b = validCandidates.get(Tools.randInt(validCandidates.size()));
                nniSon = b.getNode();
                nniParent = b.getOtherNode();
                break;
            }
            default: {
                do {
                    nniSon = T.getIngroupInodes().get(Tools.randInt(T.getNumOfIngroupInodes()));
                } while (nniSon == T.getRoot());
                nniParent = nniSon.getAncestorNode();
                if (!consensus.acceptNNI(new Branch(nniSon, nniSon.getAncestorKey()))) {
                    this.addCancellationByConsensus(Parameters.Operator.NNI);
                    if (this.trackDetails) {
                        this.monitor.printOperatorInfos(T, "NNI on branch " + nniParent + "-" + nniSon + " canceled by consensus.", consensus);
                    }
                    if (this.trackDetails) {
                        this.monitor.printTreeAfterOperator(T, Parameters.Operator.NNI, true);
                    }
                    return;
                }
                break;
            }
        }
        final Node swapSon = nniSon.getChildren().get(Tools.randInt(2));
        T.unroot();
        final List<Node> availableSwapParent = new ArrayList<Node>();
        availableSwapParent.addAll(nniParent.getNeighborNodes());
        availableSwapParent.remove(nniSon);
        availableSwapParent.removeAll(T.getOutgroupInodes());
        final Node swapParent = availableSwapParent.get(Tools.randInt(availableSwapParent.size()));
        nniSon.removeNeighborButKeepBranchLength(swapSon);
        nniParent.removeNeighborButKeepBranchLength(swapParent);
        nniSon.addNeighborWithBranchLength(swapParent);
        nniParent.addNeighborWithBranchLength(swapSon);
        T.root();
        T.markNodeToReEvaluate(swapSon);
        T.markNodeToReEvaluate(swapParent);
        T.markNodeToReEvaluate(nniSon);
        T.markNodeToReEvaluate(nniParent);
        T.fireInodeStructureChange();
        this.ingroupTargeted();
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "NNI on branch " + nniParent + "-" + nniSon + " : swap branches " + nniParent + "-" + swapParent + " and " + nniSon + "-" + swapSon);
        }
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.NNI, true);
        }
    }
    
    public void SPR(final Tree T) throws TooManyNeighborsException, UnrootableTreeException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.SPR, false);
        }
        final List<Branch> branches = new ArrayList<Branch>();
        for (final Node inode : T.getInodes()) {
            for (final Node.Neighbor neigh : inode.getNeighborKeys()) {
                branches.add(new Branch(inode, neigh));
            }
        }
        if (branches.size() < 6) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "SPR not applied, tree contains less than 6 subtrees, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, false);
            }
            return;
        }
        Branch candidate;
        List<Branch> targets;
        for (candidate = branches.remove(Tools.randInt(branches.size())), targets = this.getSPRValidTargets(T, candidate); targets.isEmpty(); targets = this.getSPRValidTargets(T, candidate)) {
            if (branches.isEmpty()) {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "SPR canceled because no valid branch candidate was found");
                }
                if (this.trackDetails) {
                    this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, false);
                }
                return;
            }
            candidate = branches.remove(Tools.randInt(branches.size()));
        }
        boolean outgroupCandidate = false;
        if (T.isInOutgroup(candidate.getNode())) {
            this.outgroupTargeted();
            outgroupCandidate = true;
        }
        else {
            this.ingroupTargeted();
        }
        T.unroot();
        final Branch oldCandidateAnchor = candidate.detach();
        Branch target;
        for (target = targets.remove(Tools.randInt(targets.size())); (outgroupCandidate && !T.isInOutgroup(target.getNode())) || (!outgroupCandidate && T.isInOutgroup(target.getNode())); target = targets.remove(Tools.randInt(targets.size()))) {
            if (targets.isEmpty()) {
                oldCandidateAnchor.graft(candidate);
                T.root();
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "SPR canceled because no valid branch target was found");
                }
                if (this.trackDetails) {
                    this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, false);
                }
                return;
            }
        }
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "SPR : branch " + candidate + " pruned and regrafted on branch " + target);
        }
        target.graft(candidate);
        T.fireInodeStructureChange();
        T.root();
        T.markAllNodesToReEvaluate();
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, false);
        }
    }
    
    public void SPR(final Tree T, final Consensus consensus, final Parameters.CPOperator behaviour) throws TooManyNeighborsException, UnrootableTreeException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.SPR, true);
        }
        final List<Branch> branches = new ArrayList<Branch>();
        for (final Node inode : T.getInodes()) {
            for (final Node.Neighbor neigh : inode.getNeighborKeys()) {
                branches.add(new Branch(inode, neigh));
            }
        }
        if (branches.size() < 6) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "SPR not applied, tree contains less than 6 subtrees, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, true);
            }
            return;
        }
        Branch candidate = branches.remove(Tools.randInt(branches.size()));
        List<Branch> targets = consensus.getSPRValidTargets(T, candidate);
        switch (behaviour) {
            case SUPERVISED: {
                while (targets.isEmpty()) {
                    if (branches.isEmpty()) {
                        this.addCancellationByConsensus(Parameters.Operator.SPR);
                        if (this.trackDetails) {
                            this.monitor.printOperatorInfos(T, "Supervised SPR canceled by consensus, no valid candidate found.", consensus);
                        }
                        if (this.trackDetails) {
                            this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, true);
                        }
                        return;
                    }
                    candidate = branches.remove(Tools.randInt(branches.size()));
                    targets = consensus.getSPRValidTargets(T, candidate);
                }
                break;
            }
            case BLIND: {
                if (targets.isEmpty()) {
                    this.addCancellationByConsensus(Parameters.Operator.SPR);
                    if (this.trackDetails) {
                        this.monitor.printOperatorInfos(T, "SPR on branch " + candidate + " canceled by consensus.", consensus);
                    }
                    if (this.trackDetails) {
                        this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, true);
                    }
                    return;
                }
                break;
            }
        }
        boolean outgroupCandidate = false;
        if (T.isInOutgroup(candidate.getNode())) {
            this.outgroupTargeted();
            outgroupCandidate = true;
        }
        else {
            this.ingroupTargeted();
        }
        T.unroot();
        final Branch oldCandidateAnchor = candidate.detach();
        Branch target;
        for (target = targets.remove(Tools.randInt(targets.size())); (outgroupCandidate && !T.isInOutgroup(target.getNode())) || (!outgroupCandidate && T.isInOutgroup(target.getNode())); target = targets.remove(Tools.randInt(targets.size()))) {
            if (targets.isEmpty()) {
                oldCandidateAnchor.graft(candidate);
                T.root();
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "SPR canceled because no valid branch target was found");
                }
                if (this.trackDetails) {
                    this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, false);
                }
                return;
            }
        }
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "SPR : branch " + candidate + " pruned and regrafted on branch " + target);
        }
        target.graft(candidate);
        T.fireInodeStructureChange();
        T.root();
        T.markAllNodesToReEvaluate();
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.SPR, true);
        }
    }
    
    public void TBR(final Tree T) throws TooManyNeighborsException, UnrootableTreeException, NullAncestorException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.TBR, false);
        }
        final List<Branch> branches = new ArrayList<Branch>();
        for (final Node inode : T.getIngroupInodes()) {
            if (inode != T.getRoot()) {
                branches.add(new Branch(inode, inode.getAncestorKey()));
            }
        }
        if (branches.size() < 3) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "TBR not applied, ingroup contains less than 3 internal branches, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.TBR, false);
            }
            return;
        }
        T.unroot();
        Branch candidate;
        Branch mirror;
        List<Branch> leftTargets;
        List<Branch> rightTargets;
        for (candidate = branches.remove(Tools.randInt(branches.size())), mirror = candidate.getMirrorBranch(), leftTargets = this.getSPRValidTargets(T, candidate), rightTargets = this.getSPRValidTargets(T, mirror); leftTargets.isEmpty() || rightTargets.isEmpty(); leftTargets = this.getSPRValidTargets(T, candidate), rightTargets = this.getSPRValidTargets(T, mirror)) {
            if (branches.isEmpty()) {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "TBR canceled because no valid branch candidate was found");
                }
                if (this.trackDetails) {
                    this.monitor.printTreeAfterOperator(T, Parameters.Operator.TBR, false);
                }
                T.root();
                return;
            }
            candidate = branches.remove(Tools.randInt(branches.size()));
            mirror = candidate.getMirrorBranch();
        }
        if (T.isInOutgroup(candidate.getNode())) {
            this.outgroupTargeted();
        }
        else {
            this.ingroupTargeted();
        }
        candidate.detach();
        mirror.detach();
        final Branch leftTarget = leftTargets.get(Tools.randInt(leftTargets.size()));
        final Branch rightTarget = rightTargets.get(Tools.randInt(rightTargets.size()));
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "TBR : branch " + candidate + " removed and reconnected on branches " + leftTarget + " and " + rightTarget);
        }
        leftTarget.graft(candidate);
        rightTarget.graft(mirror);
        T.fireInodeStructureChange();
        T.root();
        T.markAllNodesToReEvaluate();
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.TBR, false);
        }
    }
    
    public void TBR(final Tree T, final Consensus consensus, final Parameters.CPOperator behaviour) throws TooManyNeighborsException, UnrootableTreeException, NullAncestorException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.TBR, true);
        }
        final List<Branch> branches = new ArrayList<Branch>();
        for (final Node inode : T.getIngroupInodes()) {
            if (inode != T.getRoot()) {
                branches.add(new Branch(inode, inode.getAncestorKey()));
            }
        }
        if (branches.size() < 3) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "TBR not applied, ingroup contains less than 3 internal branches, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.TBR, true);
            }
            return;
        }
        T.unroot();
        Branch candidate = branches.remove(Tools.randInt(branches.size()));
        Branch mirror = candidate.getMirrorBranch();
        List<Branch> leftTargets = consensus.getSPRValidTargets(T, candidate);
        List<Branch> rightTargets = consensus.getSPRValidTargets(T, mirror);
        switch (behaviour) {
            case SUPERVISED: {
                while (leftTargets.isEmpty() || rightTargets.isEmpty()) {
                    if (branches.isEmpty()) {
                        this.addCancellationByConsensus(Parameters.Operator.TBR);
                        if (this.trackDetails) {
                            this.monitor.printOperatorInfos(T, "Supervised TBR canceled by consensus, no valid candidate found.", consensus);
                        }
                        if (this.trackDetails) {
                            this.monitor.printTreeAfterOperator(T, Parameters.Operator.TBR, true);
                        }
                        T.root();
                        return;
                    }
                    candidate = branches.remove(Tools.randInt(branches.size()));
                    mirror = candidate.getMirrorBranch();
                    leftTargets = consensus.getSPRValidTargets(T, candidate);
                    rightTargets = consensus.getSPRValidTargets(T, mirror);
                }
                break;
            }
            case BLIND: {
                if (leftTargets.isEmpty() || rightTargets.isEmpty()) {
                    this.addCancellationByConsensus(Parameters.Operator.TBR);
                    if (this.trackDetails) {
                        this.monitor.printOperatorInfos(T, "TBR on branch " + candidate + " canceled by consensus.", consensus);
                    }
                    if (this.trackDetails) {
                        this.monitor.printTreeAfterOperator(T, Parameters.Operator.TBR, true);
                    }
                    T.root();
                    return;
                }
                break;
            }
        }
        if (T.isInOutgroup(candidate.getNode())) {
            this.outgroupTargeted();
        }
        else {
            this.ingroupTargeted();
        }
        candidate.detach();
        mirror.detach();
        final Branch leftTarget = leftTargets.get(Tools.randInt(leftTargets.size()));
        final Branch rightTarget = rightTargets.get(Tools.randInt(rightTargets.size()));
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "TBR : branch " + candidate + " removed and reconnected on branches " + leftTarget + " and " + rightTarget);
        }
        leftTarget.graft(candidate);
        rightTarget.graft(mirror);
        T.fireInodeStructureChange();
        T.root();
        T.markAllNodesToReEvaluate();
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.TBR, true);
        }
    }
    
    public void TXS(final Tree T, int numToSwap) throws NullAncestorException, TooManyNeighborsException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.TXS, false);
        }
        final boolean applyOnOutgroup = T.getNumOfOutgroupLeaves() > 2 && Math.random() < T.getNumOfOutgroupLeaves() / T.getNumOfLeaves();
        final List<Node> leaves = applyOnOutgroup ? new ArrayList<Node>(T.getOutgroupLeaves()) : new ArrayList<Node>(T.getIngroupLeaves());
        if (leaves.size() < 3) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "TXS not applied, ingroup contains less than 3 nodes, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, false);
            }
            return;
        }
        if (numToSwap == 1) {
            numToSwap = Tools.randInt(leaves.size() - 1) + 2;
        }
        if (numToSwap == 0) {
            numToSwap = leaves.size();
        }
        final List<Node> toSwap = new ArrayList<Node>();
        while (toSwap.size() < numToSwap && leaves.size() > 0) {
            toSwap.add(leaves.remove(Tools.randInt(leaves.size())));
        }
        switch (toSwap.size()) {
            case 0:
            case 1: {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "This TXS instance don't find 2 taxas that can be legaly swapped, no change was done to the tree");
                }
                if (this.trackDetails) {
                    this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, false);
                }
                return;
            }
            case 2: {
                final Node nodeA = toSwap.get(0);
                Node nodeB;
                Node ancestorA;
                Node ancestorB;
                for (nodeB = toSwap.get(1), ancestorA = nodeA.getAncestorNode(), ancestorB = nodeB.getAncestorNode(); ancestorA == ancestorB; ancestorB = nodeB.getAncestorNode()) {
                    toSwap.remove(1);
                    toSwap.add(leaves.remove(Tools.randInt(leaves.size())));
                    nodeB = toSwap.get(1);
                }
                ancestorA.removeNeighborButKeepBranchLength(nodeA);
                ancestorB.removeNeighborButKeepBranchLength(nodeB);
                ancestorA.addNeighborWithBranchLength(nodeB);
                ancestorB.addNeighborWithBranchLength(nodeA);
                T.markNodeToReEvaluate(nodeA);
                T.markNodeToReEvaluate(nodeB);
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "TXS(" + numToSwap + ") on " + (applyOnOutgroup ? "outgroup" : "ingroup") + " : [" + nodeA + "," + nodeB + "]");
                    break;
                }
                break;
            }
            default: {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "TXS(" + numToSwap + ") on " + (applyOnOutgroup ? "outgroup" : "ingroup") + " : " + toSwap);
                }
                for (final Node node : toSwap) {
                    T.markNodeToReEvaluate(node);
                }
                final List<Node> ancestors = new ArrayList<Node>();
                for (final Node node2 : toSwap) {
                    final Node ancestor = node2.getAncestorNode();
                    ancestor.removeNeighborButKeepBranchLength(node2);
                    ancestors.add(ancestor);
                }
                for (final Node node2 : ancestors) {
                    node2.addNeighborWithBranchLength(toSwap.remove(Tools.randInt(toSwap.size())));
                }
                break;
            }
        }
        if (applyOnOutgroup) {
            this.outgroupTargeted();
        }
        else {
            this.ingroupTargeted();
        }
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, false);
        }
    }
    
    public void TXS(final Tree T, int numToSwap, final Consensus consensus, final Parameters.CPOperator behaviour) throws NullAncestorException, TooManyNeighborsException, NoInclusionException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.TXS, true);
        }
        final boolean applyOnOutgroup = T.getNumOfOutgroupLeaves() > 2 && Math.random() < T.getNumOfOutgroupLeaves() / T.getNumOfLeaves();
        List<Node> leaves = applyOnOutgroup ? new ArrayList<Node>(T.getOutgroupLeaves()) : new ArrayList<Node>(T.getIngroupLeaves());
        if (leaves.size() < 3) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "TXS not applied, ingroup contains less than 3 nodes, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, true);
            }
            return;
        }
        if (numToSwap == 1) {
            numToSwap = Tools.randInt(leaves.size() - 1) + 2;
        }
        if (numToSwap == 0) {
            numToSwap = leaves.size();
        }
        if (behaviour == Parameters.CPOperator.SUPERVISED) {
            leaves = consensus.getTXSValidCandidates(T, numToSwap, leaves);
        }
        final List<Node> toSwap = new ArrayList<Node>();
        while (toSwap.size() < numToSwap && leaves.size() > 0) {
            toSwap.add(leaves.remove(Tools.randInt(leaves.size())));
        }
        switch (toSwap.size()) {
            case 0:
            case 1: {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "This TXS instance don't find 2 taxas that can be legaly swapped, no change was done to the tree");
                }
                if (this.trackDetails) {
                    this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, true);
                }
                return;
            }
            case 2: {
                final Node nodeA = toSwap.get(0);
                Node nodeB;
                Node ancestorA;
                Node ancestorB;
                for (nodeB = toSwap.get(1), ancestorA = nodeA.getAncestorNode(), ancestorB = nodeB.getAncestorNode(); ancestorA == ancestorB; ancestorB = nodeB.getAncestorNode()) {
                    toSwap.remove(1);
                    toSwap.add(leaves.remove(Tools.randInt(leaves.size())));
                    nodeB = toSwap.get(1);
                }
                if (behaviour == Parameters.CPOperator.BLIND && !consensus.acceptTXS(T, toSwap)) {
                    this.addCancellationByConsensus(Parameters.Operator.TXS);
                    if (this.trackDetails) {
                        this.monitor.printOperatorInfos(T, "TXS on taxas " + toSwap + " canceled by consensus.", consensus);
                    }
                    if (this.trackDetails) {
                        this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, true);
                    }
                    return;
                }
                ancestorA.removeNeighborButKeepBranchLength(nodeA);
                ancestorB.removeNeighborButKeepBranchLength(nodeB);
                ancestorA.addNeighborWithBranchLength(nodeB);
                ancestorB.addNeighborWithBranchLength(nodeA);
                T.markNodeToReEvaluate(nodeA);
                T.markNodeToReEvaluate(nodeB);
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "TXS(" + numToSwap + ") on " + (applyOnOutgroup ? "outgroup" : "ingroup") + " : [" + nodeA + "," + nodeB + "]");
                    break;
                }
                break;
            }
            default: {
                if (behaviour == Parameters.CPOperator.BLIND && !consensus.acceptTXS(T, toSwap)) {
                    this.addCancellationByConsensus(Parameters.Operator.TXS);
                    if (this.trackDetails) {
                        this.monitor.printOperatorInfos(T, "TXS on taxas " + toSwap + " canceled by consensus.", consensus);
                    }
                    if (this.trackDetails) {
                        this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, true);
                    }
                    return;
                }
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "TXS(" + numToSwap + ") on " + (applyOnOutgroup ? "outgroup" : "ingroup") + " : " + toSwap);
                }
                for (final Node node : toSwap) {
                    T.markNodeToReEvaluate(node);
                }
                final List<Node> ancestors = new ArrayList<Node>();
                for (final Node node2 : toSwap) {
                    final Node ancestor = node2.getAncestorNode();
                    ancestor.removeNeighborButKeepBranchLength(node2);
                    ancestors.add(ancestor);
                }
                for (final Node node2 : ancestors) {
                    node2.addNeighborWithBranchLength(toSwap.remove(Tools.randInt(toSwap.size())));
                }
                break;
            }
        }
        if (applyOnOutgroup) {
            this.outgroupTargeted();
        }
        else {
            this.ingroupTargeted();
        }
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.TXS, true);
        }
    }
    
    public void STS(final Tree T, final int numToSwap) throws UnrootableTreeException, NullAncestorException, TooManyNeighborsException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.STS, false);
        }
        final List<Node> inodes = new ArrayList<Node>(T.getIngroupInodes());
        inodes.remove(T.getRoot());
        final List<Node> toSwap = new ArrayList<Node>();
        while (inodes.size() > 0 && (toSwap.size() != 2 || numToSwap != 2)) {
            Node n = inodes.get(Tools.randInt(inodes.size()));
            toSwap.add(n);
            inodes.removeAll(T.getPreorderTraversal(n));
            n = n.getAncestorNode();
            inodes.removeAll(n.getChildren());
            while (n != T.getRoot()) {
                inodes.remove(n);
                n = n.getAncestorNode();
            }
        }
        if (toSwap.size() < 2) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "This STS instance don't find 2 subtrees that can be legaly swapped, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.STS, false);
            }
            return;
        }
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "STS(" + ((numToSwap == 2) ? "2" : "RANDOM") + ") on ingroup : " + toSwap);
        }
        for (final Node node : toSwap) {
            T.markNodeToReEvaluate(node);
        }
        if (numToSwap == 2) {
            final Node nodeA = toSwap.get(0);
            final Node nodeB = toSwap.get(1);
            final Node ancestorA = nodeA.getAncestorNode();
            final Node ancestorB = nodeB.getAncestorNode();
            ancestorA.removeNeighborButKeepBranchLength(nodeA);
            ancestorB.removeNeighborButKeepBranchLength(nodeB);
            ancestorA.addNeighborWithBranchLength(nodeB);
            ancestorB.addNeighborWithBranchLength(nodeA);
        }
        else {
            final List<Node> ancestors = new ArrayList<Node>();
            for (final Node node2 : toSwap) {
                final Node ancestor = node2.getAncestorNode();
                ancestor.removeNeighborButKeepBranchLength(node2);
                ancestors.add(ancestor);
            }
            for (final Node node2 : ancestors) {
                node2.addNeighborWithBranchLength(toSwap.remove(Tools.randInt(toSwap.size())));
            }
        }
        T.fireInodeStructureChange();
        this.ingroupTargeted();
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.STS, false);
        }
    }
    
    public void STS(final Tree T, final int numToSwap, final Consensus consensus, final Parameters.CPOperator behaviour) throws UnrootableTreeException, NullAncestorException, TooManyNeighborsException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.STS, true);
        }
        final List<Node> inodes = new ArrayList<Node>(T.getIngroupInodes());
        inodes.remove(T.getRoot());
        final List<Node> toSwap = new ArrayList<Node>();
        while (inodes.size() > 0 && (toSwap.size() != 2 || numToSwap != 2)) {
            Node n = inodes.get(Tools.randInt(inodes.size()));
            toSwap.add(n);
            inodes.removeAll(T.getPreorderTraversal(n));
            n = n.getAncestorNode();
            inodes.removeAll(n.getChildren());
            while (n != T.getRoot()) {
                inodes.remove(n);
                n = n.getAncestorNode();
            }
        }
        if (toSwap.size() < 2) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "This STS instance don't find 2 subtrees that can be legaly swapped, no change was done to the tree");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.STS, true);
            }
            return;
        }
        if (!consensus.acceptSTS(T, toSwap)) {
            this.addCancellationByConsensus(Parameters.Operator.STS);
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "STS(" + ((numToSwap == 2) ? "2" : "RANDOM") + ") on nodes " + toSwap + " canceled by consensus.", consensus);
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.STS, true);
            }
            return;
        }
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "STS(" + ((numToSwap == 2) ? "2" : "RANDOM") + ") on ingroup : " + toSwap);
        }
        for (final Node node : toSwap) {
            T.markNodeToReEvaluate(node);
        }
        if (numToSwap == 2) {
            final Node nodeA = toSwap.get(0);
            final Node nodeB = toSwap.get(1);
            final Node ancestorA = nodeA.getAncestorNode();
            final Node ancestorB = nodeB.getAncestorNode();
            ancestorA.removeNeighborButKeepBranchLength(nodeA);
            ancestorB.removeNeighborButKeepBranchLength(nodeB);
            ancestorA.addNeighborWithBranchLength(nodeB);
            ancestorB.addNeighborWithBranchLength(nodeA);
        }
        else {
            final List<Node> ancestors = new ArrayList<Node>();
            for (final Node node2 : toSwap) {
                final Node ancestor = node2.getAncestorNode();
                ancestor.removeNeighborButKeepBranchLength(node2);
                ancestors.add(ancestor);
            }
            for (final Node node2 : ancestors) {
                node2.addNeighborWithBranchLength(toSwap.remove(Tools.randInt(toSwap.size())));
            }
        }
        T.fireInodeStructureChange();
        this.ingroupTargeted();
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.STS, true);
        }
    }
    
    public void BLM(final Tree T) throws NullAncestorException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.BLM, false);
        }
        final double mutation = Tools.exponentialMultiplierRand();
        final Node node = T.getInodes().get(Tools.randInt(T.getNumOfInodes()));
        switch (Tools.randInt(3)) {
            case 0: {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "Branch " + node + "-" + node.getNeighbor(Node.Neighbor.A) + " of length " + node.getBranchLength(Node.Neighbor.A) + " mutated to " + node.getBranchLength(Node.Neighbor.A) * mutation);
                }
                node.setBranchLength(Node.Neighbor.A, node.getBranchLength(Node.Neighbor.A) * mutation);
                if (node != T.getRoot() && node.getAncestorKey() == Node.Neighbor.A) {
                    T.markNodeToReEvaluate(node.getAncestorNode());
                    break;
                }
                T.markNodeToReEvaluate(node);
                break;
            }
            case 1: {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "Branch " + node + "-" + node.getNeighbor(Node.Neighbor.B) + " of length " + node.getBranchLength(Node.Neighbor.B) + " mutated to " + node.getBranchLength(Node.Neighbor.B) * mutation);
                }
                node.setBranchLength(Node.Neighbor.B, node.getBranchLength(Node.Neighbor.B) * mutation);
                if (node != T.getRoot() && node.getAncestorKey() == Node.Neighbor.B) {
                    T.markNodeToReEvaluate(node.getAncestorNode());
                    break;
                }
                T.markNodeToReEvaluate(node);
                break;
            }
            case 2: {
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "Branch " + node + "-" + node.getNeighbor(Node.Neighbor.C) + " of length " + node.getBranchLength(Node.Neighbor.C) + " mutated to " + node.getBranchLength(Node.Neighbor.C) * mutation);
                }
                node.setBranchLength(Node.Neighbor.C, node.getBranchLength(Node.Neighbor.C) * mutation);
                if (node != T.getRoot() && node.getAncestorKey() == Node.Neighbor.C) {
                    T.markNodeToReEvaluate(node.getAncestorNode());
                    break;
                }
                T.markNodeToReEvaluate(node);
                break;
            }
        }
        if (T.isInOutgroup(node)) {
            this.outgroupTargeted();
        }
        else {
            this.ingroupTargeted();
        }
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.BLM, false);
        }
    }
    
    public void BLMint(final Tree T) throws NullAncestorException {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.BLMINT, false);
        }
        final double mutation = Tools.exponentialMultiplierRand();
        final Node node = T.getInodes().get(Tools.randInt(T.getNumOfInodes()));
        final List<Node.Neighbor> neighbors = new ArrayList<Node.Neighbor>();
        if (node.getNeighbor(Node.Neighbor.A).isInode()) {
            neighbors.add(Node.Neighbor.A);
        }
        if (node.getNeighbor(Node.Neighbor.B).isInode()) {
            neighbors.add(Node.Neighbor.B);
        }
        if (node.getNeighbor(Node.Neighbor.C).isInode()) {
            neighbors.add(Node.Neighbor.C);
        }
        final Node.Neighbor randNeighbor = neighbors.get(Tools.randInt(neighbors.size()));
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "Branch " + node + "-" + node.getNeighbor(randNeighbor) + " of length " + node.getBranchLength(randNeighbor) + " mutated to " + node.getBranchLength(randNeighbor) * mutation);
        }
        node.setBranchLength(randNeighbor, node.getBranchLength(randNeighbor) * mutation);
        if (node != T.getRoot() && node.getAncestorKey() == randNeighbor) {
            T.markNodeToReEvaluate(node.getAncestorNode());
        }
        else {
            T.markNodeToReEvaluate(node);
        }
        if (T.isInOutgroup(node)) {
            this.outgroupTargeted();
        }
        else {
            this.ingroupTargeted();
        }
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.BLMINT, false);
        }
    }
    
    public void RPM(final Tree T, final int numParam) {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.RPM, false);
        }
        final List<Charset> L = T.getPartitions();
        final Charset C = L.get(Tools.randInt(L.size()));
        final Map<RateParameter, Double> param = T.getEvaluationRateParameters(C);
        switch (numParam) {
            case 1: {
                final RateParameter key = new ArrayList<RateParameter>(param.keySet()).get(Tools.randInt(param.size()));
                double p = param.get(key);
                final double mutation = Tools.exponentialMultiplierRand();
                p *= mutation;
                if (this.trackDetails) {
                    this.monitor.printOperatorInfos(T, "R parameter " + key + ((L.size() > 1) ? (" of partition " + C.getLabel()) : "") + " with value " + param.get(key) + " mutated to " + p);
                }
                T.setEvaluationRateParameter(C, key, p);
                break;
            }
            default: {
                for (final Map.Entry<RateParameter, Double> e : param.entrySet()) {
                    double p = e.getValue();
                    final double mutation = Tools.exponentialMultiplierRand();
                    p *= mutation;
                    if (this.trackDetails) {
                        this.monitor.printOperatorInfos(T, "R parameter " + e.getKey() + ((L.size() > 1) ? (" of partition " + C.getLabel()) : "") + " with value " + e.getValue() + " mutated to " + p);
                    }
                    T.setEvaluationRateParameter(C, e.getKey(), p);
                }
                break;
            }
        }
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.RPM, false);
        }
    }
    
    public void GDM(final Tree T) {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.GDM, false);
        }
        final List<Charset> L = T.getPartitions();
        final Charset C = L.get(Tools.randInt(L.size()));
        double shape = T.getEvaluationGammaShape(C);
        final double mutation = Tools.exponentialMultiplierRand();
        shape *= mutation;
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "Gamma dist. shape parameter of " + T.getEvaluationGammaShape(C) + " mutated to " + shape + ((L.size() > 1) ? (" in partition " + C.getLabel()) : ""));
        }
        T.setEvaluationDistributionShape(C, shape);
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.GDM, false);
        }
    }
    
    public void PIM(final Tree T) {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.PIM, false);
        }
        final List<Charset> L = T.getPartitions();
        final Charset C = L.get(Tools.randInt(L.size()));
        double pinv = T.getEvaluationPInv(C);
        final double mutation = Tools.positiveNormalRand();
        pinv *= mutation;
        if (pinv >= 1.0) {
            pinv = 0.99;
        }
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "P-Inv of " + T.getEvaluationPInv(C) + " mutated to " + pinv + ((L.size() > 1) ? (" in partition " + C.getLabel()) : ""));
        }
        T.setEvaluationPInv(C, pinv);
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.PIM, false);
        }
    }
    
    public void APRM(final Tree T) {
        if (this.trackDetails) {
            this.monitor.printTreeBeforeOperator(T, Parameters.Operator.APRM, false);
        }
        final List<Charset> L = T.getPartitions();
        if (L.size() < 2) {
            if (this.trackDetails) {
                this.monitor.printOperatorInfos(T, "Tree has only one partition, cannot mutate the among-partition rate.");
            }
            if (this.trackDetails) {
                this.monitor.printTreeAfterOperator(T, Parameters.Operator.APRM, true);
            }
            return;
        }
        final Charset C1 = L.get(Tools.randInt(L.size()));
        Charset C2;
        do {
            C2 = L.get(Tools.randInt(L.size()));
        } while (C2.toString().equals(C1.toString()));
        double apRate = T.getEvaluationAmongPartitionRate(C1);
        final double mutation = Tools.positiveNormalRand();
        apRate *= mutation;
        if (apRate <= 0.0) {
            apRate = 0.01;
        }
        if (this.trackDetails) {
            this.monitor.printOperatorInfos(T, "AP rate in partition " + C1.getLabel() + " of " + T.getEvaluationAmongPartitionRate(C1) + " mutated to " + apRate + ", and AP rate of partition " + C2.getLabel() + " is adjusted accordingly.");
        }
        T.setEvaluationAmongPartitionRate(C1, apRate, C2);
        if (this.trackDetails) {
            this.monitor.printTreeAfterOperator(T, Parameters.Operator.APRM, false);
        }
    }
    
    List<Branch> getSPRValidTargets(final Tree T, final Branch candidate) {
        final List<Branch> candidates = this.buildSPRValidTargets(T, candidate, !T.isInOutgroup(candidate.getNode()));
        final Iterator<Branch> it = candidates.iterator();
        while (it.hasNext()) {
            final Branch b = it.next();
            for (final Branch neigh : candidate.getAllNeighborBranches()) {
                if (b.equals(neigh)) {
                    it.remove();
                    break;
                }
            }
        }
        return candidates;
    }
    
    private List<Branch> buildSPRValidTargets(final Tree T, final Branch currentBranch, final boolean ingroup) {
        final List<Branch> candidates = new ArrayList<Branch>();
        for (final Branch b : currentBranch.getAllNeighborBranches()) {
            candidates.add(b);
            if (!ingroup || !T.isInOutgroup(b.getNode())) {
                candidates.addAll(this.buildSPRValidTargets(T, b, ingroup));
            }
        }
        return candidates;
    }
}

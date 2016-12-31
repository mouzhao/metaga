// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import java.util.BitSet;
import com.google.common.collect.BiMap;
import java.util.Stack;
import com.google.common.collect.HashBiMap;
import metapiga.trees.exceptions.BranchNotFoundException;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.utilities.Tools;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.exceptions.NoInclusionException;
import metapiga.trees.exceptions.UncompatibleOutgroupException;
import metapiga.trees.exceptions.TooManyNeighborsException;
import metapiga.trees.exceptions.UnknownTaxonException;
import java.util.Collections;
import java.util.HashSet;
import metapiga.parameters.Parameters;
import metapiga.RateParameter;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Arrays;
import metapiga.modelization.Dataset;
import metapiga.modelization.Charset;
import java.util.Set;
import java.util.Map;
import java.util.List;

public class Consensus
{
    private List<Map<String, BiPartition>> bipartitions;
    private Map<Node, Map<Node, BiPartition>> branches;
    private Map<String, Set<Tree>> bipartitionExistInTree;
    private Set<BiPartition> consensus;
    private int differentBipartitionCount;
    private int treeCount;
    private Map<String, Integer> taxonId;
    private Map<Integer, String> taxa;
    private final int numTaxa;
    private Map<Charset, Map<RateParameter, Double>> rateParameters;
    private Map<Charset, Double> evaluationDistributionShape;
    private Map<Charset, Double> evaluationPInv;
    
    public Consensus(final Tree tree, final Dataset dataset) {
        this(Arrays.asList(tree), dataset);
    }
    
    public Consensus(final Tree tree1, final Tree tree2, final Dataset dataset) {
        this(Arrays.asList(tree1, tree2), dataset);
    }
    
    public Consensus(final Collection<Tree> trees, final Dataset dataset) {
        this.taxonId = new HashMap<String, Integer>();
        this.taxa = new HashMap<Integer, String>();
        for (int i = 0; i < dataset.getNTax(); ++i) {
            this.taxonId.put(dataset.getTaxon(i), i);
            this.taxa.put(i, dataset.getTaxon(i));
        }
        this.numTaxa = this.taxa.size();
        this.differentBipartitionCount = 0;
        this.bipartitions = new ArrayList<Map<String, BiPartition>>(this.numTaxa);
        for (int i = 0; i < this.numTaxa; ++i) {
            this.bipartitions.add(new HashMap<String, BiPartition>());
        }
        this.branches = new HashMap<Node, Map<Node, BiPartition>>();
        this.bipartitionExistInTree = new HashMap<String, Set<Tree>>();
        this.treeCount = trees.size();
        this.rateParameters = new TreeMap<Charset, Map<RateParameter, Double>>();
        this.evaluationDistributionShape = new TreeMap<Charset, Double>();
        this.evaluationPInv = new TreeMap<Charset, Double>();
        for (final Charset c : dataset.getPartitionCharsets()) {
            final Map<RateParameter, Double> param = new TreeMap<RateParameter, Double>();
            for (final RateParameter r : trees.iterator().next().getEvaluationRateParameters(c).keySet()) {
                param.put(r, 0.0);
            }
            this.rateParameters.put(c, param);
            this.evaluationDistributionShape.put(c, 0.0);
            this.evaluationPInv.put(c, 0.0);
        }
        for (final Tree tree : trees) {
            final Node access = tree.getAccessNode();
            for (final Node.Neighbor neighbor : access.getNeighborKeys()) {
                final Branch b = new Branch(access, neighbor).getMirrorBranch();
                this.addBiPartition(this.buildBiPartition(tree, b), tree, b);
            }
            for (final Charset c2 : tree.getPartitions()) {
                for (final Map.Entry<RateParameter, Double> e : tree.getEvaluationRateParameters(c2).entrySet()) {
                    this.rateParameters.get(c2).put(e.getKey(), this.rateParameters.get(c2).get(e.getKey()) + e.getValue() / this.treeCount);
                }
                this.evaluationDistributionShape.put(c2, this.evaluationDistributionShape.get(c2) + tree.getEvaluationGammaShape(c2) / this.treeCount);
                this.evaluationPInv.put(c2, this.evaluationPInv.get(c2) + tree.getEvaluationPInv(c2) / this.treeCount);
            }
        }
        for (final Map<String, BiPartition> map : this.bipartitions) {
            for (final BiPartition p : map.values()) {
                access$2(p, p.count / this.treeCount);
                final BiPartition biPartition = p;
                access$4(biPartition, biPartition.branchLength / p.count);
            }
        }
    }
    
    public Consensus(final Collection<Tree> trees, final Dataset dataset, final Parameters.CPConsensus consensusType) {
        this(trees, dataset);
        this.setConsensusType(consensusType);
    }
    
    private void putBiPartitionInBranches(final Branch branch, final BiPartition bipartition) {
        final Node nodeA = branch.getNode();
        final Node nodeB = branch.getOtherNode();
        if (!this.branches.containsKey(nodeA)) {
            this.branches.put(nodeA, new HashMap<Node, BiPartition>());
        }
        Map<Node, BiPartition> subMap = this.branches.get(nodeA);
        subMap.put(nodeB, bipartition);
        if (!this.branches.containsKey(nodeB)) {
            this.branches.put(nodeB, new HashMap<Node, BiPartition>());
        }
        subMap = this.branches.get(nodeB);
        subMap.put(nodeA, bipartition);
    }
    
    public synchronized BiPartition getBiPartition(final Branch branch) {
        return this.branches.get(branch.getNode()).get(branch.getOtherNode());
    }
    
    private void putBiPartitionExistInTree(final BiPartition bipartition, final Tree tree) {
        final String key = bipartition.toString();
        if (!this.bipartitionExistInTree.containsKey(key)) {
            this.bipartitionExistInTree.put(key, new HashSet<Tree>());
        }
        this.bipartitionExistInTree.get(key).add(tree);
    }
    
    public boolean isBiPartitionExistsInTree(final BiPartition bipartition, final Tree tree) {
        return this.bipartitionExistInTree.get(bipartition.toString()).contains(tree);
    }
    
    public void setConsensusType(final Parameters.CPConsensus consensusType) {
        this.consensus = new HashSet<BiPartition>();
        for (final Map<String, BiPartition> map : this.bipartitions) {
            for (final BiPartition p : map.values()) {
                if (p.cardinality > 1) {
                    if (consensusType == Parameters.CPConsensus.STRICT) {
                        if (p.strength != 1.0) {
                            continue;
                        }
                        this.consensus.add(p);
                    }
                    else {
                        if (consensusType != Parameters.CPConsensus.STOCHASTIC || p.count <= 1 || Math.random() >= p.strength) {
                            continue;
                        }
                        this.consensus.add(p);
                    }
                }
            }
        }
    }
    
    public boolean isInConsensus(final Branch branch) {
        final BiPartition p = this.getBiPartition(branch);
        for (final BiPartition e : this.consensus) {
            if (e.equals(p)) {
                return true;
            }
        }
        return false;
    }
    
    private BiPartition buildBiPartition(final Tree tree, final Branch branch) {
        if (branch.getNode().isLeaf()) {
            return new BiPartition(branch.getNode());
        }
        final Set<BiPartition> set = new HashSet<BiPartition>();
        for (final Branch b : branch.getAllNeighborBranches()) {
            final BiPartition p = this.buildBiPartition(tree, b);
            this.addBiPartition(p, tree, b);
            set.add(p);
        }
        final BiPartition p2 = new BiPartition(set, branch.getLength());
        return p2;
    }
    
    private void addBiPartition(final BiPartition bipartition, final Tree tree, final Branch branch) {
        BiPartition p;
        if (bipartition.cardinality > this.numTaxa / 2) {
            p = bipartition.mirror();
        }
        else {
            p = bipartition;
        }
        this.putBiPartitionExistInTree(p, tree);
        final Map<String, BiPartition> map = this.bipartitions.get(p.cardinality);
        if (map.containsKey(p.toString())) {
            final BiPartition biPartition;
            final BiPartition q = biPartition = map.get(p.toString());
            access$7(biPartition, biPartition.count + 1);
            final BiPartition biPartition2 = q;
            access$4(biPartition2, biPartition2.branchLength + p.branchLength);
            this.putBiPartitionInBranches(branch, q);
        }
        else if (p.cardinality == this.numTaxa / 2 && map.containsKey(p.mirror().toString())) {
            final BiPartition biPartition3;
            final BiPartition q = biPartition3 = map.get(p.mirror().toString());
            access$7(biPartition3, biPartition3.count + 1);
            final BiPartition biPartition4 = q;
            access$4(biPartition4, biPartition4.branchLength + p.branchLength);
            this.putBiPartitionInBranches(branch, q);
        }
        else {
            ++this.differentBipartitionCount;
            this.putBiPartitionInBranches(branch, p);
            map.put(p.toString(), p);
        }
    }
    
    public Tree getConsensusTree(final Parameters parameters) throws UnknownTaxonException, TooManyNeighborsException, UncompatibleOutgroupException, NoInclusionException {
        final Tree tree = new Tree("Consensus tree", parameters);
        final TreeMap<Integer, Set<BiPartition>> P = new TreeMap<Integer, Set<BiPartition>>();
        final List<BiPartition> majoritaryP = new ArrayList<BiPartition>();
        final List<BiPartition> minoritaryP = new ArrayList<BiPartition>();
        for (int card = 0; card < this.numTaxa; ++card) {
            minoritaryP.addAll(this.bipartitions.get(card).values());
            final Set<BiPartition> setP = new HashSet<BiPartition>();
            for (final BiPartition bp : this.bipartitions.get(card).values()) {
                if (bp.strength > 0.5 || bp.strength == 1.0) {
                    setP.add(bp);
                    majoritaryP.add(bp);
                    minoritaryP.remove(bp);
                }
            }
            if (!setP.isEmpty()) {
                P.put(card, setP);
            }
        }
        Collections.sort(minoritaryP);
        while (!minoritaryP.isEmpty()) {
            final BiPartition a = minoritaryP.remove(0);
            boolean isCompatible = true;
            for (final BiPartition p : majoritaryP) {
                if (!a.isCompatible(p)) {
                    isCompatible = false;
                    break;
                }
            }
            if (isCompatible) {
                if (!P.containsKey(a.cardinality)) {
                    final Set<BiPartition> set = new HashSet<BiPartition>();
                    P.put(a.cardinality, set);
                }
                P.get(a.cardinality).add(a);
                final Iterator<BiPartition> it = minoritaryP.iterator();
                while (it.hasNext()) {
                    if (!a.isCompatible(it.next())) {
                        it.remove();
                    }
                }
            }
        }
        final BiPartition p2 = P.get(P.lastKey()).iterator().next();
        P.get(P.lastKey()).remove(p2);
        final ConsensusNode A = new ConsensusNode(p2);
        this.buildConsensusTree(tree, A, P, p2);
        final BiPartition q = p2.mirror();
        final ConsensusNode root = new ConsensusNode(q);
        final Node.Neighbor neighbor = root.addNeighbor(A);
        root.setBranchLength(neighbor, p2.branchLength);
        root.setBranchStrength(neighbor, p2.strength);
        this.buildConsensusTree(tree, root, P, q);
        tree.addNode(A);
        tree.addNode(root);
        tree.setAccessNode(root);
        tree.setOutgroup(parameters.outgroup);
        for (final Charset c : tree.getPartitions()) {
            for (final Map.Entry<RateParameter, Double> e : this.rateParameters.get(c).entrySet()) {
                tree.setEvaluationRateParameter(c, e.getKey(), e.getValue());
            }
            tree.setEvaluationDistributionShape(c, this.evaluationDistributionShape.get(c));
            tree.setEvaluationPInv(c, this.evaluationPInv.get(c));
        }
        return tree;
    }
    
    private void buildConsensusTree(final Tree tree, final Node father, final TreeMap<Integer, Set<BiPartition>> P, final BiPartition p) throws UnknownTaxonException, TooManyNeighborsException, NoInclusionException {
        if (p.cardinality <= 1) {
            return;
        }
        int key;
        for (int currentCardinality = p.cardinality; currentCardinality > 0; currentCardinality = key) {
            key = P.lowerKey(currentCardinality);
            for (final BiPartition q : P.get(key)) {
                if (q.isIncludedIn(p)) {
                    P.get(key).remove(q);
                    final ConsensusNode bigChild = new ConsensusNode(q);
                    Node.Neighbor neighbor = bigChild.addNeighbor(father);
                    bigChild.setBranchLength(neighbor, q.branchLength);
                    bigChild.setBranchStrength(neighbor, q.strength);
                    if (q.cardinality > 1) {
                        this.buildConsensusTree(tree, bigChild, P, q);
                    }
                    tree.addNode(bigChild);
                    currentCardinality = p.cardinality - q.cardinality;
                    if (P.containsKey(currentCardinality)) {
                        for (final BiPartition r : P.get(currentCardinality)) {
                            if (r.isIncludedIn(p)) {
                                P.get(currentCardinality).remove(r);
                                final ConsensusNode smallChild = new ConsensusNode(r);
                                neighbor = smallChild.addNeighbor(father);
                                smallChild.setBranchLength(neighbor, r.branchLength);
                                smallChild.setBranchStrength(neighbor, r.strength);
                                if (r.cardinality > 1) {
                                    this.buildConsensusTree(tree, smallChild, P, r);
                                }
                                tree.addNode(smallChild);
                                return;
                            }
                        }
                    }
                    final BiPartition fictiveBipartition = p.complement(q);
                    final ConsensusNode fictiveChild = new ConsensusNode(fictiveBipartition);
                    neighbor = fictiveChild.addNeighbor(father);
                    fictiveChild.setBranchLength(neighbor, 0.0);
                    fictiveChild.setBranchStrength(neighbor, 0.0);
                    if (fictiveBipartition.cardinality > 1) {
                        this.buildConsensusTree(tree, fictiveChild, P, fictiveBipartition);
                    }
                    tree.addNode(fictiveChild);
                    return;
                }
            }
        }
    }
    
    public String showPartitions() {
        String s = "";
        for (final Map<String, BiPartition> map : this.bipartitions) {
            for (final BiPartition p : map.values()) {
                s = String.valueOf(s) + p.toTaxa() + " - (" + p.cardinality + ") " + p.strength * 100.0 + "%" + "\n";
            }
        }
        return s;
    }
    
    public String showConsensus() {
        String s = "";
        for (final BiPartition p : this.consensus) {
            s = String.valueOf(s) + p.toTaxa() + " - (" + p.count + "/" + this.treeCount + ") " + p.strength * 100.0 + "%" + "\n";
        }
        return s;
    }
    
    public double getConsensusCoverage() {
        double sum = 0.0;
        for (final BiPartition p : this.consensus) {
            sum += p.strength;
        }
        return sum / (this.numTaxa - 3);
    }
    
    public boolean acceptNNI(final Branch candidate) {
        return !this.isInConsensus(candidate);
    }
    
    public List<Branch> getNNIValidCandidates(final Tree T) throws NullAncestorException {
        final List<Branch> candidates = new ArrayList<Branch>();
        for (final Node inode : T.getIngroupInodes()) {
            if (inode != T.getRoot()) {
                final Branch b = new Branch(inode, inode.getAncestorKey());
                if (this.isInConsensus(b)) {
                    continue;
                }
                candidates.add(b);
            }
        }
        return candidates;
    }
    
    public List<Branch> getSPRValidTargets(final Tree T, final Branch candidate) {
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
            if ((!ingroup || !T.isInOutgroup(b.getNode())) && !this.isInConsensus(b)) {
                candidates.addAll(this.buildSPRValidTargets(T, b, ingroup));
            }
        }
        return candidates;
    }
    
    public boolean acceptTXS(final Tree T, final Collection<Node> taxas) {
        final TaxaSet txs = new TaxaSet(taxas);
        for (final BiPartition p : this.consensus) {
            if (this.isBiPartitionExistsInTree(p, T) && !p.hasOnSamePartition(txs)) {
                return false;
            }
        }
        return true;
    }
    
    public List<Node> getTXSValidCandidates(final Tree T, final int txsParam, final List<Node> availableLeaves) throws NoInclusionException, NullAncestorException {
        final List<Node> nodes = new ArrayList<Node>();
        if (this.consensus.isEmpty()) {
            return availableLeaves;
        }
        final List<TaxaSet> taxasets = new ArrayList<TaxaSet>();
        for (final BiPartition p : this.consensus) {
            if (this.isBiPartitionExistsInTree(p, T)) {
                TaxaSet txs;
                int pos;
                for (txs = p.getBiggestPartition(), pos = 0; pos < taxasets.size() && txs.cardinality > taxasets.get(pos).cardinality; ++pos) {}
                taxasets.add(pos, txs);
                for (txs = p.getSmallestPartition(), pos = 0; pos < taxasets.size() && txs.cardinality > taxasets.get(pos).cardinality; ++pos) {}
                taxasets.add(pos, txs);
            }
        }
        for (int i = 0; i < taxasets.size(); ++i) {
            final TaxaSet txsi = taxasets.get(i);
            for (int j = i + 1; j < taxasets.size(); ++j) {
                final TaxaSet txsj = taxasets.get(j);
                if (txsi.isIncludedIn(txsj)) {
                    taxasets.set(j, txsj.complement(txsi));
                }
            }
        }
        int numTaxa = 0;
        final TaxaSet availableTaxas = new TaxaSet(availableLeaves);
        final Iterator<TaxaSet> it = taxasets.iterator();
        while (it.hasNext()) {
            final TaxaSet txs2 = it.next();
            txs2.keepTaxas(availableTaxas);
            if (txs2.cardinality < txsParam) {
                it.remove();
            }
            else if (txs2.cardinality == 2) {
                final List<Node> l = txs2.getTaxas(T);
                if (l.get(0).getAncestorNode() == l.get(1).getAncestorNode()) {
                    it.remove();
                }
                else {
                    numTaxa += txs2.cardinality;
                }
            }
            else {
                numTaxa += txs2.cardinality;
            }
        }
        if (taxasets.size() > 0) {
            final int rand = Tools.randInt(numTaxa);
            int count = 0;
            for (final TaxaSet txs3 : taxasets) {
                count += txs3.cardinality;
                if (rand < count) {
                    return txs3.getTaxas(T);
                }
            }
            return taxasets.get(Tools.randInt(taxasets.size())).getTaxas(T);
        }
        return nodes;
    }
    
    public boolean acceptSTS(final Tree T, final Collection<Node> inodes) throws UnrootableTreeException {
        final Set<Node> taxas = new HashSet<Node>();
        for (final Node inode : inodes) {
            for (final Node node : T.getPreorderTraversal(inode)) {
                if (node.isLeaf()) {
                    taxas.add(node);
                }
            }
        }
        final TaxaSet txs = new TaxaSet(taxas);
        for (final BiPartition p : this.consensus) {
            if (this.isBiPartitionExistsInTree(p, T) && !p.isIncludedIn(txs) && !p.hasOnSamePartition(txs)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean recombination(final Tree T, final Tree T2) throws BranchNotFoundException, UnrootableTreeException, NullAncestorException, TooManyNeighborsException {
        if (T != T2) {
            final List<Branch> branches = T.getBranches();
            Branch b = null;
            BiPartition P = null;
            while (b == null && !branches.isEmpty()) {
                b = branches.remove(Tools.randInt(branches.size()));
                if (b.isTipBranch() || T.isInOutgroup(b.getNode()) || T.isInOutgroup(b.getOtherNode())) {
                    b = null;
                }
                else {
                    P = this.getBiPartition(b);
                    if (P.count >= 2 && P.cardinality >= 3 && this.isBiPartitionExistsInTree(P, T2)) {
                        continue;
                    }
                    b = null;
                }
            }
            if (b != null) {
                this.recombine(T, T2, b, P);
                return true;
            }
        }
        return false;
    }
    
    public String hybridization(final Tree T, final Tree[] population) throws BranchNotFoundException, UnrootableTreeException, NullAncestorException, TooManyNeighborsException {
        final List<Branch> branches = T.getBranches();
        Branch b = null;
        BiPartition P = null;
        Tree T2 = null;
        while (T2 == null && !branches.isEmpty()) {
            b = branches.remove(Tools.randInt(branches.size()));
            if (!b.isTipBranch() && !T.isInOutgroup(b.getNode()) && !T.isInOutgroup(b.getOtherNode())) {
                P = this.getBiPartition(b);
                if (P.count <= 1 || P.cardinality <= 2) {
                    continue;
                }
                final Set<Tree> trees = this.bipartitionExistInTree.get(P.toString());
                for (final Tree tree : trees) {
                    if (T != tree) {
                        boolean insidePop = false;
                        for (int i = 0; i < population.length; ++i) {
                            if (tree == population[i]) {
                                insidePop = true;
                                break;
                            }
                        }
                        if (insidePop) {
                            continue;
                        }
                        T2 = tree;
                    }
                }
            }
        }
        if (T2 != null) {
            this.recombine(T, T2, b, P);
            return "Recombination with " + T2.getName();
        }
        return "No recombination possible";
    }
    
    private void recombine(final Tree T, final Tree T2, final Branch branchInT, final BiPartition P) throws BranchNotFoundException, UnrootableTreeException, NullAncestorException, TooManyNeighborsException {
        T.unroot();
        final List<Branch> branchesOfT2 = T2.getBranches();
        Branch branchInT2 = null;
        final String Pstring = P.toString();
        for (final Branch b : branchesOfT2) {
            if (!b.isTipBranch() && this.branches.get(b.getNode()).get(b.getOtherNode()).toString().equals(Pstring)) {
                branchInT2 = b;
                break;
            }
        }
        if (branchInT2 == null) {
            throw new BranchNotFoundException("Recombination", branchInT, T, T2);
        }
        Set<Node> set1 = T.getAllNodesUnderNeighbor(branchInT.getNode(), branchInT.getNeighbor());
        Set<Node> set2 = T.getAllNodesUnderNeighbor(branchInT.getOtherNode(), branchInT.getOtherNeighbor());
        Set<Node> nodesOfT;
        Node insideNodeT;
        Node outsideNodeT;
        if ((!T.hasOutgroup() && set1.size() <= set2.size()) || (T.hasOutgroup() && !set1.containsAll(T.getOutgroupLeaves()))) {
            nodesOfT = new HashSet<Node>(set1);
            insideNodeT = branchInT.getOtherNode();
            outsideNodeT = branchInT.getNode();
        }
        else {
            nodesOfT = new HashSet<Node>(set2);
            insideNodeT = branchInT.getNode();
            outsideNodeT = branchInT.getOtherNode();
        }
        set1.clear();
        set2.clear();
        set1 = T2.getAllNodesUnderNeighbor(branchInT2.getNode(), branchInT2.getNeighbor());
        set2 = T2.getAllNodesUnderNeighbor(branchInT2.getOtherNode(), branchInT2.getOtherNeighbor());
        Set<Node> nodesOfT2;
        Node insideNodeT2;
        Node outsideNodeT2;
        if ((!T.hasOutgroup() && set1.size() < set2.size()) || (T.hasOutgroup() && !set1.containsAll(T2.getOutgroupLeaves()))) {
            nodesOfT2 = new HashSet<Node>(set1);
            insideNodeT2 = branchInT2.getOtherNode();
            outsideNodeT2 = branchInT2.getNode();
        }
        else if (!T.hasOutgroup() && set1.size() == set2.size()) {
            Node leaf = null;
            for (final Node n : set1) {
                if (n.isLeaf()) {
                    leaf = n;
                    break;
                }
            }
            if (nodesOfT.contains(T.getNode(leaf.label))) {
                nodesOfT2 = new HashSet<Node>(set1);
                insideNodeT2 = branchInT2.getOtherNode();
                outsideNodeT2 = branchInT2.getNode();
            }
            else {
                nodesOfT2 = new HashSet<Node>(set2);
                insideNodeT2 = branchInT2.getNode();
                outsideNodeT2 = branchInT2.getOtherNode();
            }
        }
        else {
            nodesOfT2 = new HashSet<Node>(set2);
            insideNodeT2 = branchInT2.getNode();
            outsideNodeT2 = branchInT2.getOtherNode();
        }
        final BiMap<Node, Node> TxT2 = HashBiMap.create(nodesOfT2.size());
        TxT2.put(outsideNodeT, outsideNodeT2);
        TxT2.put(insideNodeT, insideNodeT2);
        final Stack<Node> stackT2 = new Stack<Node>();
        for (final Node n2 : nodesOfT2) {
            if (n2.isInode() && n2 != insideNodeT2) {
                stackT2.push(n2);
            }
        }
        for (final Node n2 : nodesOfT) {
            if (n2.isLeaf()) {
                TxT2.put(n2, T2.getNode(n2.label));
            }
            else {
                if (n2 == insideNodeT) {
                    continue;
                }
                TxT2.put(n2, stackT2.pop());
            }
        }
        insideNodeT.removeNeighborButKeepBranchLength(outsideNodeT);
        for (final Node n2 : nodesOfT) {
            n2.removeAllNeighbors();
        }
        for (final Node nT2 : nodesOfT2) {
            final Node nT3 = TxT2.inverse().get(nT2);
            final Set<Node> currentNeighborsT2 = new HashSet<Node>();
            for (final Node neighbor : nT3.getNeighborNodes()) {
                currentNeighborsT2.add(TxT2.get(neighbor));
            }
            Node.Neighbor[] values;
            for (int length = (values = Node.Neighbor.values()).length, i = 0; i < length; ++i) {
                final Node.Neighbor neigh = values[i];
                if (nT2.hasNeighbor(neigh)) {
                    final Node neighborNodeT2 = nT2.getNeighbor(neigh);
                    if (!currentNeighborsT2.contains(neighborNodeT2)) {
                        final Node.Neighbor neighThis = nT3.addNeighbor(TxT2.inverse().get(neighborNodeT2));
                        nT3.setBranchLength(neighThis, nT2.getBranchLength(neigh));
                    }
                }
            }
        }
        T.root();
        for (final Node n2 : nodesOfT) {
            T.markNodeToReEvaluate(n2);
        }
    }
    
    public final class BiPartition implements Comparable<BiPartition>
    {
        private final BitSet bits;
        private int cardinality;
        private int count;
        private double strength;
        private double branchLength;
        private String stringRepresentation;
        private final int ntax;
        
        public BiPartition() {
            this.stringRepresentation = null;
            this.ntax = Consensus.this.numTaxa;
            this.bits = new BitSet(this.ntax);
            this.cardinality = 0;
            this.strength = 0.0;
            this.branchLength = 0.0;
            this.count = 1;
        }
        
        public BiPartition(final Node leaf) {
            this.stringRepresentation = null;
            this.ntax = Consensus.this.numTaxa;
            (this.bits = new BitSet(this.ntax)).set(Consensus.this.taxonId.get(leaf.getLabel()));
            this.count = 1;
            this.strength = 1.0;
            this.branchLength = leaf.getBranchLength(leaf.getNeighborKeys().iterator().next());
            this.cardinality = 1;
        }
        
        public BiPartition(final Set<BiPartition> setToMerge, final double branchLength) {
            this.stringRepresentation = null;
            this.ntax = Consensus.this.numTaxa;
            this.bits = new BitSet(this.ntax);
            for (final BiPartition p : setToMerge) {
                this.bits.or(p.bits);
            }
            this.count = 1;
            this.strength = 1.0;
            this.branchLength = branchLength;
            this.cardinality = this.bits.cardinality();
        }
        
        @Override
        public String toString() {
            return this.bits.toString();
        }
        
        @Override
        public int compareTo(final BiPartition bp) {
            if (this.equals(bp)) {
                return 0;
            }
            if (bp.strength - this.strength < 0.0) {
                return -1;
            }
            if (bp.strength - this.strength > 0.0) {
                return 1;
            }
            return 0;
        }
        
        public String toTaxa() {
            if (this.stringRepresentation == null) {
                final StringBuilder A = new StringBuilder();
                final StringBuilder B = new StringBuilder();
                for (final int i : Consensus.this.taxa.keySet()) {
                    if (this.bits.get(i)) {
                        A.append(String.valueOf(Consensus.this.taxa.get(i)) + " ");
                    }
                    else {
                        B.append(String.valueOf(Consensus.this.taxa.get(i)) + " ");
                    }
                }
                this.stringRepresentation = String.valueOf(A.toString()) + " versus " + B.toString();
            }
            return this.stringRepresentation;
        }
        
        public int getCardinality() {
            return this.cardinality;
        }
        
        public double getStrength() {
            return this.strength;
        }
        
        public String getTaxa() {
            String s = "";
            for (final int i : Consensus.this.taxa.keySet()) {
                if (this.bits.get(i)) {
                    if (s.length() > 0) {
                        s = String.valueOf(s) + ", ";
                    }
                    s = String.valueOf(s) + Consensus.this.taxa.get(i);
                }
            }
            return s;
        }
        
        @Override
        public boolean equals(final Object obj) {
            final BiPartition p = (BiPartition)obj;
            if (this.bits.equals(p.bits)) {
                return true;
            }
            if (this.cardinality == this.ntax / 2 && this.cardinality == p.cardinality) {
                final BitSet comp = new BitSet(this.ntax);
                comp.flip(0, this.ntax);
                comp.andNot(((BiPartition)obj).bits);
                return this.bits.equals(comp);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int hash = 42;
            hash = 31 * hash + this.cardinality;
            return hash;
        }
        
        public boolean isIncludedIn(final BiPartition p) {
            if (this.cardinality < p.cardinality) {
                for (int i = 0; i < this.ntax; ++i) {
                    if (!p.bits.get(i) && this.bits.get(i)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        
        public boolean isCompatible(final BiPartition p) {
            if (!this.bits.intersects(p.bits)) {
                return true;
            }
            if (this.cardinality < p.cardinality) {
                return this.isIncludedIn(p);
            }
            return p.isIncludedIn(this);
        }
        
        public TaxaSet getSmallestPartition() {
            final TaxaSet txs = new TaxaSet();
            txs.bits.or(this.bits);
            access$1(txs, txs.bits.cardinality());
            return txs;
        }
        
        public TaxaSet getBiggestPartition() {
            final TaxaSet txs = new TaxaSet();
            txs.bits.or(this.bits);
            txs.bits.flip(0, this.ntax);
            access$1(txs, txs.bits.cardinality());
            return txs;
        }
        
        public boolean hasOnSamePartition(final TaxaSet txs) {
            if (this.bits.intersects(txs.bits)) {
                for (int i = 0; i < this.ntax; ++i) {
                    if (txs.bits.get(i) && !this.bits.get(i)) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        public boolean isIncludedIn(final TaxaSet txs) {
            boolean included = true;
            for (int i = this.bits.nextSetBit(0); i != -1; i = this.bits.nextSetBit(i + 1)) {
                if (!txs.bits.get(i)) {
                    included = false;
                    break;
                }
            }
            if (!included && this.bits.nextClearBit(0) != -1) {
                included = true;
                for (int i = this.bits.nextClearBit(0); i != -1; i = this.bits.nextClearBit(i + 1)) {
                    if (!txs.bits.get(i)) {
                        included = false;
                        break;
                    }
                }
            }
            return included;
        }
        
        public BiPartition complement(final BiPartition p) throws NoInclusionException {
            if (!p.isIncludedIn(this)) {
                throw new NoInclusionException(this.toTaxa(), p.toTaxa(), "Cannot take the complement");
            }
            final BiPartition q = new BiPartition();
            q.bits.or(this.bits);
            q.bits.xor(p.bits);
            q.cardinality = q.bits.cardinality();
            return q;
        }
        
        public BiPartition mirror() {
            final BiPartition p = new BiPartition();
            p.count = this.count;
            p.strength = this.strength;
            p.branchLength = this.branchLength;
            p.bits.or(this.bits);
            p.bits.flip(0, this.ntax);
            p.cardinality = p.bits.cardinality();
            return p;
        }
        

    }
    
    public class TaxaSet
    {
        private BitSet bits;
        private int cardinality;
        private final int nTax;
        
        public TaxaSet() {
            this.nTax = Consensus.this.numTaxa;
            this.bits = new BitSet(this.nTax);
            this.cardinality = 0;
        }
        
        public TaxaSet(final Collection<Node> leaves) {
            this.nTax = Consensus.this.numTaxa;
            this.bits = new BitSet(this.nTax);
            for (final Node leaf : leaves) {
                this.bits.set(Consensus.this.taxonId.get(leaf.getLabel()));
            }
            this.cardinality = this.bits.cardinality();
        }
        
        public int cardinality() {
            return this.cardinality;
        }
        
        @Override
        public String toString() {
            String s = "";
            for (int i = 0; i < this.nTax; ++i) {
                if (this.bits.get(i)) {
                    if (s.length() > 0) {
                        s = String.valueOf(s) + ", ";
                    }
                    s = String.valueOf(s) + Consensus.this.taxa.get(i);
                }
            }
            return s;
        }
        
        public List<Node> getTaxas(final Tree T) {
            final List<Node> list = new ArrayList<Node>();
            for (int i = 0; i < this.nTax; ++i) {
                if (this.bits.get(i)) {
                    list.add(T.getNode(Consensus.this.taxa.get(i)));
                }
            }
            return list;
        }
        
        public boolean isIncludedIn(final TaxaSet taxaset) {
            if (this.cardinality < taxaset.cardinality) {
                for (int i = 0; i < this.nTax; ++i) {
                    if (!taxaset.bits.get(i) && this.bits.get(i)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        
        public TaxaSet complement(final TaxaSet taxaset) throws NoInclusionException {
            if (!taxaset.isIncludedIn(this)) {
                throw new NoInclusionException(this.toString(), taxaset.toString(), "Cannot make the complement");
            }
            final TaxaSet txs = new TaxaSet();
            txs.bits.or(this.bits);
            txs.bits.xor(taxaset.bits);
            txs.cardinality = txs.bits.cardinality();
            return txs;
        }
        
        public void keepTaxas(final TaxaSet txs) {
            this.bits.and(txs.bits);
            this.cardinality = this.bits.cardinality();
        }
        

    }
    static /* synthetic */ void access$1(final TaxaSet set, final int cardinality) {
        set.cardinality = cardinality;
    }
    /* synthetic */
    static void access$2(final BiPartition biPartition, final double strength) {
        biPartition.strength = strength;
    }

    /* synthetic */
    static void access$4(final BiPartition biPartition, final double branchLength) {
        biPartition.branchLength = branchLength;
    }

    /* synthetic */
    static void access$7(final BiPartition biPartition, final int count) {
        biPartition.count = count;
    }
}

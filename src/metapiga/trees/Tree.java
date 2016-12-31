// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import metapiga.modelization.data.DataType;

import java.io.IOException;
import java.util.BitSet;
import javax.swing.text.AttributeSet;
import metapiga.utilities.Tools;

import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.util.Hashtable;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import org.biojavax.bio.phylo.io.nexus.TreesBlock;
import java.util.Stack;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.exceptions.BranchNotFoundException;
import metapiga.trees.exceptions.UnknownTaxonException;
import metapiga.trees.exceptions.UncompatibleOutgroupException;
import java.util.Collections;
import java.util.Set;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.modelization.likelihood.LikelihoodFactory;
import metapiga.trees.exceptions.TooManyNeighborsException;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;
import metapiga.modelization.Dataset;
import metapiga.modelization.likelihood.Likelihood;
import metapiga.modelization.Charset;
import metapiga.parameters.Parameters;
import metapiga.RateParameter;
import java.util.Map;
import java.util.List;
import metapiga.modelization.parsimony.Parsimony;
public class Tree implements Cloneable
{
    private String name;
    private Node root;
    private Node outgroupRoot;
    private Node accessNode;
    private List<Node> nodes;
    private List<Node> outgroup;
    private List<Node> ingroup;
    private List<Node> inodes;
    private List<Node> leaves;
    private List<Node> outInodes;
    private List<Node> inInodes;
    private List<Node> outLeaves;
    private List<Node> inLeaves;
    private Map<String, Node> labels;
    private final Map<Node, Integer> level;
    private final Map<Node, Integer> nbrOfLeaves;
    private Parameters.LikelihoodCalculationType likelihoodType;
    private double likelihoodValue;
    private int parsimonyValue;
    private Map<Charset, Likelihood> likelihood;
    private Parsimony parsimony;
    private Dataset dataset;
    private Parameters.EvaluationRate evaluationRate;
    private Parameters.EvaluationModel evaluationModel;
    private Parameters.EvaluationStateFrequencies evaluationStateFrequencies;
    private final Map<Charset, Map<RateParameter, Double>> rateParameters;
    private Parameters.EvaluationDistribution evaluationDistribution;
    private int evaluationDistributionSubsets;
    private final Map<Charset, Double> evaluationDistributionShape;
    private final Map<Charset, Double> evaluationPInv;
    private final Map<Charset, Double> evaluationAPRate;
    private boolean isUsingOneTimeGraphicMemory;
    public Parameters parameters;
    private double weight;
    private double MLMax;
    private double MPMax;

    public Tree() {
        this.nodes = new ArrayList<Node>();
        this.outgroup = new ArrayList<Node>();
        this.ingroup = new ArrayList<Node>();
        this.inodes = new ArrayList<Node>();
        this.leaves = new ArrayList<Node>();
        this.outInodes = new ArrayList<Node>();
        this.inInodes = new ArrayList<Node>();
        this.outLeaves = new ArrayList<Node>();
        this.inLeaves = new ArrayList<Node>();
        this.labels = new HashMap<String, Node>();
        this.level = new HashMap<Node, Integer>();
        this.nbrOfLeaves = new HashMap<Node, Integer>();
        this.likelihoodType = Parameters.LikelihoodCalculationType.CLASSIC;
        this.rateParameters = new TreeMap<Charset, Map<RateParameter, Double>>();
        this.evaluationDistributionShape = new TreeMap<Charset, Double>();
        this.evaluationPInv = new TreeMap<Charset, Double>();
        this.evaluationAPRate = new TreeMap<Charset, Double>();
        this.isUsingOneTimeGraphicMemory = false;
        this.resetLikelihoodValue();
        this.resetParsimonyValue();
    }
    
    public Tree(final String name, final Parameters parameters) {
        this.nodes = new ArrayList<Node>();
        this.outgroup = new ArrayList<Node>();
        this.ingroup = new ArrayList<Node>();
        this.inodes = new ArrayList<Node>();
        this.leaves = new ArrayList<Node>();
        this.outInodes = new ArrayList<Node>();
        this.inInodes = new ArrayList<Node>();
        this.outLeaves = new ArrayList<Node>();
        this.inLeaves = new ArrayList<Node>();
        this.labels = new HashMap<String, Node>();
        this.level = new HashMap<Node, Integer>();
        this.nbrOfLeaves = new HashMap<Node, Integer>();
        this.likelihoodType = Parameters.LikelihoodCalculationType.CLASSIC;
        this.rateParameters = new TreeMap<Charset, Map<RateParameter, Double>>();
        this.evaluationDistributionShape = new TreeMap<Charset, Double>();
        this.evaluationPInv = new TreeMap<Charset, Double>();
        this.evaluationAPRate = new TreeMap<Charset, Double>();
        this.isUsingOneTimeGraphicMemory = false;
        this.name = name;
        this.dataset = parameters.dataset;
        this.evaluationRate = parameters.evaluationRate;
        this.evaluationModel = parameters.evaluationModel;
        this.evaluationStateFrequencies = parameters.evaluationStateFrequencies;
        this.evaluationDistribution = parameters.evaluationDistribution;
        this.evaluationDistributionSubsets = parameters.evaluationDistributionSubsets;
        for (final Charset c : this.dataset.getPartitionCharsets()) {
            final Map<RateParameter, Double> map = new TreeMap<RateParameter, Double>();
            RateParameter[] parametersOfModel;
            for (int length = (parametersOfModel = RateParameter.getParametersOfModel(this.evaluationModel)).length, i = 0; i < length; ++i) {
                final RateParameter r = parametersOfModel[i];
                map.put(r, parameters.getRateParameters(c).get(r));
            }
            this.rateParameters.put(c, map);
            this.evaluationDistributionShape.put(c, parameters.getEvaluationDistributionShape(c));
            this.evaluationPInv.put(c, parameters.getEvaluationPInv(c));
            this.evaluationAPRate.put(c, 1.0);
        }
        this.root = null;
        this.outgroupRoot = null;
        this.accessNode = null;
        this.resetLikelihoodValue();
        this.resetParsimonyValue();
        this.likelihoodType = parameters.getLikelihoodCalculationType();
        this.parameters = parameters;
        this.MPMax = 1;
        this.MLMax = 1;
    }
    
    public Tree clone() {
        final Tree T = new Tree();
        T.name = this.name;
        T.parameters = this.parameters;
        for (final Node node : this.nodes) {
            final Node nodeClone = new Node(node.label);
            T.nodes.add(nodeClone);
            if (this.outgroup.contains(node)) {
                T.outgroup.add(nodeClone);
            }
            if (this.ingroup.contains(node)) {
                T.ingroup.add(nodeClone);
            }
            if (this.inodes.contains(node)) {
                T.inodes.add(nodeClone);
            }
            if (this.leaves.contains(node)) {
                T.leaves.add(nodeClone);
            }
            if (this.outInodes.contains(node)) {
                T.outInodes.add(nodeClone);
            }
            if (this.inInodes.contains(node)) {
                T.inInodes.add(nodeClone);
            }
            if (this.outLeaves.contains(node)) {
                T.outLeaves.add(nodeClone);
            }
            if (this.inLeaves.contains(node)) {
                T.inLeaves.add(nodeClone);
            }
            T.labels.put(nodeClone.label, nodeClone);
        }
        try {
            T.clone(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return T;
    }
    
    public void clone(final Tree T) throws IOException, ClassNotFoundException {
        this.root = null;
        this.level.clear();
        this.nbrOfLeaves.clear();
        for (final Node n : this.nodes) {
            n.removeAllNeighbors();
        }
        for (final Node n : T.nodes) {
            final Node m = this.labels.get(n.getLabel());
            final Set<String> currentNeighbors = new HashSet<String>();
            for (final Node neighbor : m.getNeighborNodes()) {
                currentNeighbors.add(neighbor.getLabel());
            }
            Node.Neighbor[] values;
            for (int length = (values = Node.Neighbor.values()).length, j = 0; j < length; ++j) {
                final Node.Neighbor neigh = values[j];
                if (n.hasNeighbor(neigh)) {
                    final String l = n.getNeighbor(neigh).getLabel();
                    if (!currentNeighbors.contains(l)) {
                        try {
                            final Node.Neighbor neighThis = m.addNeighbor(this.labels.get(l));
                            m.setBranchLength(neighThis, n.getBranchLength(neigh));
                        }
                        catch (TooManyNeighborsException e) {
                            System.out.println("Error in cloning of tree " + this.name + ": node " + l + " was NOT added as a neighbor of " + m);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (T.accessNode != null) {
            this.accessNode = this.labels.get(T.accessNode.getLabel());
        }
        else {
            this.accessNode = null;
        }
        if (T.outgroupRoot != null) {
            this.outgroupRoot = this.labels.get(T.outgroupRoot.getLabel());
        }
        else {
            this.outgroupRoot = null;
        }
        if (T.isRooted()) {
            this.root(this.labels.get(T.getRoot().getLabel()));
        }
        this.likelihoodValue = T.likelihoodValue;
        this.parsimonyValue = T.parsimonyValue;
        this.MLMax = T.MLMax;
        this.MPMax = T.MPMax;
        this.weight = T.weight;
        this.likelihoodType = T.likelihoodType;
        this.dataset = T.dataset;
        this.evaluationRate = T.evaluationRate;
        this.evaluationModel = T.evaluationModel;
        this.evaluationStateFrequencies = T.evaluationStateFrequencies;
        this.evaluationDistribution = T.evaluationDistribution;
        this.evaluationDistributionSubsets = T.evaluationDistributionSubsets;
        this.evaluationDistributionShape.clear();
        this.evaluationDistributionShape.putAll(T.evaluationDistributionShape);
        this.evaluationPInv.clear();
        this.evaluationPInv.putAll(T.evaluationPInv);
        this.evaluationAPRate.clear();
        this.evaluationAPRate.putAll(T.evaluationAPRate);
        this.rateParameters.clear();
        for (final Charset c : this.dataset.getPartitionCharsets()) {
            final Map<RateParameter, Double> map = new TreeMap<RateParameter, Double>();
            map.putAll(T.rateParameters.get(c));
            this.rateParameters.put(c, map);
        }
        if (T.likelihood != null) {
            try {
                if (this.likelihood != null) {
                    for (final Map.Entry<Charset, Likelihood> e2 : T.likelihood.entrySet()) {
                        this.likelihood.get(e2.getKey()).clone(e2.getValue());
                    }
                }
                else {
                    this.likelihood = new HashMap<Charset, Likelihood>();
                    for (final Charset c : T.likelihood.keySet()) {
                        final Likelihood i = LikelihoodFactory.makeLikelihoodCopy(T.likelihood.get(c), this);
                        this.likelihood.put(c, i);
                    }
                }
            }
            catch (UnrootableTreeException e3) {
                System.out.println("Error in cloning of tree " + this.name + ": clone is not root, CANNOT clone likelihood calculation");
                e3.printStackTrace();
                this.likelihood = null;
            }
        }
//        if(T.parsimony != null){
//            this.parsimony = (Parsimony) T.parsimony.Clone();
//        }
    }
    
    public void cloneWithConsensus(final Tree T) throws IOException, ClassNotFoundException {
        for (final Node n : T.nodes) {
            final Node m = this.labels.get(n.getLabel());
            final Set<String> currentNeighbors = new HashSet<String>();
            for (final Node neighbor : m.getNeighborNodes()) {
                currentNeighbors.add(neighbor.getLabel());
            }
            Node.Neighbor[] values;
            for (int length = (values = Node.Neighbor.values()).length, i = 0; i < length; ++i) {
                final Node.Neighbor neigh = values[i];
                if (n.hasNeighbor(neigh)) {
                    final String l = n.getNeighbor(neigh).getLabel();
                    Node.Neighbor neighThis = null;
                    for (final Node.Neighbor neighThis2 : m.getNeighborKeys()) {
                        if (m.getNeighbor(neighThis2).getLabel().equals(l)) {
                            neighThis = neighThis2;
                            break;
                        }
                    }
                    if (neighThis != null) {
                        m.setBranchLength(neighThis, n.getBranchLength(neigh));
                    }
                    else {
                        System.out.println("Error in consensus cloning of tree " + this.name + ": branch length between node " + l + " and " + m + " was not changed !");
                    }
                }
            }
        }
        this.likelihoodValue = T.likelihoodValue;
        this.parsimonyValue = T.parsimonyValue;
        this.dataset = T.dataset;
        this.evaluationRate = T.evaluationRate;
        this.evaluationModel = T.evaluationModel;
        this.evaluationStateFrequencies = T.evaluationStateFrequencies;
        this.evaluationDistribution = T.evaluationDistribution;
        this.evaluationDistributionSubsets = T.evaluationDistributionSubsets;
        this.evaluationDistributionShape.clear();
        this.evaluationDistributionShape.putAll(T.evaluationDistributionShape);
        this.evaluationPInv.clear();
        this.evaluationPInv.putAll(T.evaluationPInv);
        this.evaluationAPRate.clear();
        this.evaluationAPRate.putAll(T.evaluationAPRate);
        this.rateParameters.clear();
        for (final Charset c : this.dataset.getPartitionCharsets()) {
            final Map<RateParameter, Double> map = new TreeMap<RateParameter, Double>();
            map.putAll(T.rateParameters.get(c));
            this.rateParameters.put(c, map);
        }
        if (T.likelihood != null) {
            try {
                if (this.likelihood != null) {
                    for (final Map.Entry<Charset, Likelihood> e : T.likelihood.entrySet()) {
                        this.likelihood.get(e.getKey()).clone(e.getValue());
                    }
                }
                else {
                    this.likelihood = new HashMap<Charset, Likelihood>();
                    for (final Charset c : T.likelihood.keySet()) {
                        this.likelihood.put(c, LikelihoodFactory.makeLikelihoodCopy(T.likelihood.get(c), this));
                    }
                }
            }
            catch (UnrootableTreeException e2) {
                System.out.println("Error in consensus cloning of tree " + this.name + ": clone is not rooted, CANNOT clone likelihood calculation");
                e2.printStackTrace();
                this.likelihood = null;
            }
        }
//        if(T.parsimony != null){
//            this.parsimony = (Parsimony) T.parsimony.Clone();
//        }
    }
    
    public void setName(final String treeName) {
        this.name = treeName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Node getRoot() {
        return this.root;
    }
    
    public boolean isRooted() {
        return this.root != null;
    }
    
    public void root() throws UnrootableTreeException {
        if (this.hasOutgroup()) {
            this.root(this.outgroupRoot);
        }
        else {
            if (this.accessNode == null) {
                throw new UnrootableTreeException();
            }
            this.root(this.accessNode);
        }
    }
    
    public void root(final Node node) {
        this.unroot();
        node.setToRoot();
        this.root = node;
    }
    
    public void setAccessNode(final Node node) {
        this.accessNode = node;
    }
    
    public void setAccessNodeToOutgroupRoot() {
        if (this.outgroupRoot != null) {
            this.accessNode = this.outgroupRoot;
        }
    }
    
    public Node getAccessNode() {
        return this.accessNode;
    }
    
    public boolean hasOutgroup() {
        return this.outgroupRoot != null;
    }
    
    public boolean isCompatibleWithDataset() {
        final List<String> list = new ArrayList<String>();
        for (final Node n : this.leaves) {
            list.add(n.getLabel());
        }
        return this.dataset.getTaxa().containsAll(list) && list.containsAll(this.dataset.getTaxa());
    }
    
    public boolean isCompatibleWithOutgroup(final Set<String> outgroupToTest) {
        if (outgroupToTest.size() > 0) {
            final Set<Node> outRoot = new HashSet<Node>();
            for (final String taxa : outgroupToTest) {
                final Node leaf = this.getNode(taxa);
                outRoot.add(leaf);
            }
            final List<Node> tempOutgroup = new ArrayList<Node>();
            boolean loop;
            do {
                final Set<Node> temp = new HashSet<Node>();
                for (final Node n : outRoot) {
                    temp.addAll(n.getNeighborNodes());
                }
                temp.removeAll(tempOutgroup);
                temp.removeAll(outRoot);
                tempOutgroup.addAll(outRoot);
                outRoot.clear();
                outRoot.addAll(temp);
                loop = true;
                if (outRoot.size() <= 1) {
                    Node tempOutgroupRoot;
                    try {
                        tempOutgroupRoot = outRoot.iterator().next();
                    }
                    catch (Exception ex) {
                        return false;
                    }
                    int count = 0;
                    for (final Node n2 : tempOutgroupRoot.getNeighborNodes()) {
                        if (tempOutgroup.contains(n2)) {
                            ++count;
                        }
                    }
                    if (count != 1) {
                        continue;
                    }
                    loop = false;
                }
            } while (loop);
            for (final Node n3 : tempOutgroup) {
                if (n3.isLeaf() && !outgroupToTest.contains(n3.getLabel())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void setOutgroup(final Set<String> taxasInOutgroup) throws UncompatibleOutgroupException {
        if (!taxasInOutgroup.isEmpty()) {
            for (final String taxa : taxasInOutgroup) {
                final Node leaf = this.getNode(taxa);
                this.outgroup.add(leaf);
            }
            final InOutMap iom = new InOutMap();
            for (final Node inode : this.inodes) {
                iom.assignGroup(inode);
            }
        }
        for (final Node n : this.nodes) {
            if (!this.outgroup.contains(n)) {
                this.ingroup.add(n);
            }
        }
        this.outInodes = new ArrayList<Node>(this.outgroup);
        this.outLeaves = new ArrayList<Node>(this.outgroup);
        this.outInodes.retainAll(this.inodes);
        this.outLeaves.retainAll(this.leaves);
        this.inInodes = new ArrayList<Node>(this.inodes);
        this.inLeaves = new ArrayList<Node>(this.leaves);
        this.inInodes.removeAll(this.outgroup);
        this.inLeaves.removeAll(this.outgroup);
        this.labelizeTree();
        this.nodes = Collections.unmodifiableList((List<? extends Node>)this.nodes);
        this.labels = Collections.unmodifiableMap((Map<? extends String, ? extends Node>)this.labels);
        this.outgroup = Collections.unmodifiableList((List<? extends Node>)this.outgroup);
        this.ingroup = Collections.unmodifiableList((List<? extends Node>)this.ingroup);
        this.inodes = Collections.unmodifiableList((List<? extends Node>)this.inodes);
        this.leaves = Collections.unmodifiableList((List<? extends Node>)this.leaves);
        this.outInodes = Collections.unmodifiableList((List<? extends Node>)this.outInodes);
        this.outLeaves = Collections.unmodifiableList((List<? extends Node>)this.outLeaves);
        this.inInodes = Collections.unmodifiableList((List<? extends Node>)this.inInodes);
        this.inLeaves = Collections.unmodifiableList((List<? extends Node>)this.inLeaves);
    }
    
    public void labelizeTree() {
        if (this.outgroup.isEmpty()) {
            int i = 0;
            if (this.accessNode.getLabel() == null) {
                while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                    ++i;
                }
                this.accessNode.setLabel(new StringBuilder().append(i).toString());
                this.labels.put(this.accessNode.getLabel(), this.accessNode);
            }
            ++i;
            for (final Node n : this.leaves) {
                if (n.getLabel() == null) {
                    while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                        ++i;
                    }
                    n.setLabel(new StringBuilder().append(i).toString());
                    this.labels.put(n.getLabel(), n);
                    ++i;
                }
            }
            for (final Node n : this.inodes) {
                if (n.getLabel() == null) {
                    while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                        ++i;
                    }
                    n.setLabel(new StringBuilder().append(i).toString());
                    this.labels.put(n.getLabel(), n);
                    ++i;
                }
            }
        }
        else {
            this.labels.clear();
            int i = 0;
            if (this.outgroupRoot.getLabel() == null) {
                while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                    ++i;
                }
                this.outgroupRoot.setLabel(new StringBuilder().append(i).toString());
                this.labels.put(this.outgroupRoot.getLabel(), this.outgroupRoot);
            }
            ++i;
            for (final Node n : this.outLeaves) {
                if (n.getLabel() == null) {
                    while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                        ++i;
                    }
                    n.setLabel(new StringBuilder().append(i).toString());
                    ++i;
                }
                this.labels.put(n.getLabel(), n);
            }
            for (final Node n : this.inLeaves) {
                if (n.getLabel() == null) {
                    while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                        ++i;
                    }
                    n.setLabel(new StringBuilder().append(i).toString());
                    ++i;
                }
                this.labels.put(n.getLabel(), n);
            }
            for (final Node n : this.outInodes) {
                if (n.getLabel() == null) {
                    while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                        ++i;
                    }
                    n.setLabel(new StringBuilder().append(i).toString());
                    this.labels.put(n.getLabel(), n);
                    ++i;
                }
            }
            for (final Node n : this.inInodes) {
                if (n.getLabel() == null) {
                    while (this.labels.containsKey(new StringBuilder().append(i).toString())) {
                        ++i;
                    }
                    n.setLabel(new StringBuilder().append(i).toString());
                    this.labels.put(n.getLabel(), n);
                    ++i;
                }
            }
        }
    }
    
    public void unroot() {
        if (this.root == null) {
            return;
        }
        for (final Node n : this.root.getNeighborNodes()) {
            n.removeAncestor();
        }
        this.root = null;
        this.level.clear();
        this.nbrOfLeaves.clear();
    }
    
    public void addNode(final Node node) throws UnknownTaxonException {
        if (node.isLeaf() && !this.dataset.getTaxa().contains(node.getLabel())) {
            throw new UnknownTaxonException(node.getLabel());
        }
        if (!this.nodes.contains(node)) {
            this.nodes.add(node);
            if (node.isLeaf()) {
                this.leaves.add(node);
            }
            else {
                this.inodes.add(node);
            }
            if (!this.labels.containsKey(node.getLabel())) {
                this.labels.put(node.getLabel(), node);
            }
            else {
                node.setLabel(null);
            }
        }
    }

    public List<Branch> getBranches() {
        final Set<Node> checkedNodes = new HashSet<Node>();
        final List<Branch> branches = new ArrayList<Branch>();
        for (final Node node : this.inInodes) {
            checkedNodes.add(node);
            for (final Branch b : node.getBranches()) {
                if (!checkedNodes.contains(b.getOtherNode())) {
                    branches.add(b);
                }
            }
        }
        return branches;
    }
    
    public Branch getBranch(final Node node, final Node otherNode) throws BranchNotFoundException {
        Node.Neighbor neighbor = null;
        for (final Node.Neighbor n : node.getNeighborKeys()) {
            if (node.getNeighbor(n) == otherNode) {
                neighbor = n;
                break;
            }
        }
        if (neighbor != null) {
            return new Branch(node, neighbor);
        }
        throw new BranchNotFoundException(node, otherNode);
    }
    
    public final int getNumOfNodes() {
        return this.nodes.size();
    }
    
    public final List<Node> getInodes() {
        return this.inodes;
    }
    
    public final int getNumOfInodes() {
        return this.inodes.size();
    }
    
    public final List<Node> getLeaves() {
        return this.leaves;
    }
    
    public final int getNumOfLeaves() {
        return this.leaves.size();
    }
    
    public Node getNode(final String label) {
        return this.labels.get(label);
    }
    
    public boolean isInOutgroup(final Node node) {
        return this.outgroup.contains(node);
    }
    
    public final int getOutgroupSize() {
        return this.outgroup.size();
    }
    
    public final int getIngroupSize() {
        return this.ingroup.size();
    }
    
    public final List<Node> getOutgroupInodes() {
        return this.outInodes;
    }
    
    public final int getNumOfOutgroupInodes() {
        return this.outInodes.size();
    }
    
    public final List<Node> getOutgroupLeaves() {
        return this.outLeaves;
    }
    
    public final int getNumOfOutgroupLeaves() {
        return this.outLeaves.size();
    }
    
    public final List<Node> getIngroupInodes() {
        return this.inInodes;
    }
    
    public final int getNumOfIngroupInodes() {
        return this.inInodes.size();
    }
    
    public final List<Node> getIngroupLeaves() {
        return this.inLeaves;
    }
    
    public final int getNumOfIngroupLeaves() {
        return this.inLeaves.size();
    }

    private void initLikelihood() throws UnrootableTreeException {
        this.likelihood = new HashMap<Charset, Likelihood>();
        for (final Charset c : this.dataset.getPartitionCharsets()) {
            Likelihood l = null;
            if (this.likelihoodType == Parameters.LikelihoodCalculationType.CLASSIC) {
                l = LikelihoodFactory.makeLikelihoodClassic(this.dataset.getPartition(c), this.evaluationRate, this.evaluationModel, this.evaluationDistribution, this.evaluationDistributionShape.get(c), this.evaluationPInv.get(c), this.evaluationAPRate.get(c), this.rateParameters.get(c), this.evaluationStateFrequencies, this, this.evaluationDistributionSubsets);
            }
            this.likelihood.put(c, l);
        }
    }

    private void initParsimony() throws UnrootableTreeException {
        this.parsimony = new Parsimony(this.parameters,this);
    }

    private void createOneTimeGraphicMemeory() {
        if (this.parameters.getLikelihoodCalculationType() == Parameters.LikelihoodCalculationType.CLASSIC) {
            return;
        }
    }

    public synchronized List<Double> Evaluation() throws UnrootableTreeException, NullAncestorException {
        if (!this.hasLikelihoodValue()) {
            if (this.likelihood == null) {
                this.initLikelihood();
            }
            this.likelihoodValue = 0.0;
            for (final Likelihood l : this.likelihood.values()) {
                this.likelihoodValue += l.getLikelihoodValue();
            }
            this.likelihoodValue = -this.likelihoodValue;
        }
        if(!this.hasParsimonyValue()){
            if (this.parsimony == null) {
                this.initParsimony();
            }
            this.parsimonyValue = this.parsimony.getParsimonyValue();
        }
        List<Double> result = new ArrayList<>();
        result.add(this.likelihoodValue);
        result.add((double)this.parsimonyValue);
        return result;
    }

    public synchronized double getEvaluation() throws UnrootableTreeException, NullAncestorException {
        Evaluation();
        System.out.println("likelihoodValue:"+(-this.likelihoodValue));
        System.out.println("parsimonyValue:"+this.parsimonyValue);
        return (weight * (this.likelihoodValue/this.MLMax) + (1-weight) * (this.parsimonyValue/this.MPMax));
    }
    
    public void deleteLikelihoodComputation() {
        this.likelihood = null;
    }
    
    private void resetLikelihoodValue() {
        this.likelihoodValue = -1.0;
    }

    private void resetParsimonyValue() {
        this.parsimonyValue = -1;
    }
    
    private boolean hasLikelihoodValue() {
        return this.likelihoodValue != -1.0;
    }

    private boolean hasParsimonyValue() {
        return this.parsimonyValue != -1;
    }

    public boolean isBetterThan(final Tree t) throws UnrootableTreeException, NullAncestorException {
        return this.getEvaluation() < t.getEvaluation();
    }
    
    public boolean isBetterThan(final double evaluation) throws UnrootableTreeException, NullAncestorException {
        return this.getEvaluation() < evaluation;
    }
    
    public void markAllNodesToReEvaluate() {
        this.resetLikelihoodValue();
        this.resetParsimonyValue();
        if (this.likelihood != null) {
            for (final Likelihood l : this.likelihood.values()) {
                l.markAllInodesToUpdate();
            }
        }
        if (this.parsimony != null) {
            this.parsimony.markAllInodesToUpdate();
        }
    }
    
    public void markNodeToReEvaluate(Node node) throws NullAncestorException {
        this.resetLikelihoodValue();
        this.resetParsimonyValue();
        if (this.likelihood != null) {
            for (final Likelihood l : this.likelihood.values()) {
                l.markInodeToUpdate(node);
            }
            while (node != this.root) {
                node = node.getAncestorNode();
                for (final Likelihood l : this.likelihood.values()) {
                    l.markInodeToUpdate(node);
                }
            }
        }
        if (this.parsimony != null) {
            this.parsimony.markInodeToUpdate(node);
            while (node != this.root) {
                node = node.getAncestorNode();
                this.parsimony.markInodeToUpdate(node);

            }
        }
    }
    
    public List<Charset> getPartitions() {
        return new ArrayList<Charset>(this.dataset.getPartitionCharsets());
    }
    
    public void setEvaluationModel(final Parameters.EvaluationModel model) {
        this.evaluationModel = model;
        for (final Charset c : this.dataset.getPartitionCharsets()) {
            this.rateParameters.get(c).clear();
        }
        this.resetLikelihoodValue();
        this.likelihood = null;
    }
    
    public void setEvaluationStateFrequencies(final Parameters.EvaluationStateFrequencies freq) {
        this.evaluationStateFrequencies = freq;
        this.resetLikelihoodValue();
        this.likelihood = null;
    }
    
    public void setEvaluationRateParameter(final Charset c, final RateParameter param, final double newValue) {
        this.resetLikelihoodValue();
        if (this.likelihood != null) {
            this.likelihood.get(c).updateRateParameter(param, newValue);
        }
        this.rateParameters.get(c).put(param, newValue);
    }
    
    public void setEvaluationRate(final Parameters.EvaluationRate rate) {
        this.evaluationRate = rate;
        this.resetLikelihoodValue();
        this.likelihood = null;
    }
    
    public void setEvaluationDistribution(final Parameters.EvaluationDistribution distribution) {
        this.evaluationDistribution = distribution;
        this.resetLikelihoodValue();
        this.likelihood = null;
    }
    
    public void setEvaluationDistributionSubsets(final int nbrSubsets) {
        this.evaluationDistributionSubsets = nbrSubsets;
        this.resetLikelihoodValue();
        this.likelihood = null;
    }
    
    public void setEvaluationDistributionShape(final Charset c, final double shape) {
        this.resetLikelihoodValue();
        if (this.likelihood != null) {
            this.likelihood.get(c).updateGammaDistribution(shape);
        }
        this.evaluationDistributionShape.put(c, shape);
    }
    
    public void setEvaluationPInv(final Charset c, final double pInv) {
        this.resetLikelihoodValue();
        if (this.likelihood != null) {
            this.likelihood.get(c).updateInvariant(pInv);
        }
        this.evaluationPInv.put(c, pInv);
    }
    
    public void setEvaluationAmongPartitionRate(final Charset partition1, final double apRate1, final Charset partition2) {
        double mean = 1.0;
        final double nchar = this.dataset.getNChar();
        if (this.evaluationAPRate.size() > 2) {
            for (final Map.Entry<Charset, Double> e : this.evaluationAPRate.entrySet()) {
                if (!e.getKey().toString().equals(partition1.toString()) && !e.getKey().toString().equals(partition2.toString())) {
                    mean -= this.dataset.getPartition(e.getKey()).getNChar() / nchar * e.getValue();
                }
            }
        }
        mean -= this.dataset.getPartition(partition1).getNChar() / nchar * apRate1;
        final double apRate2 = mean / (this.dataset.getPartition(partition2).getNChar() / nchar);
        this.resetLikelihoodValue();
        if (this.likelihood != null) {
            this.likelihood.get(partition1).updateAmongPartitionRate(apRate1);
            this.likelihood.get(partition2).updateAmongPartitionRate(apRate2);
        }
        this.evaluationAPRate.put(partition1, apRate1);
        this.evaluationAPRate.put(partition2, apRate2);
    }
    
    public boolean setEvaluationAmongPartitionRate(final Map<Charset, Double> apr) {
        double mean = 0.0;
        final double nchar = this.dataset.getNChar();
        for (final Map.Entry<Charset, Double> e : apr.entrySet()) {
            mean += this.dataset.getPartition(e.getKey()).getNChar() / nchar * e.getValue();
        }
        if (mean == 1.0) {
            this.resetLikelihoodValue();
            if (this.likelihood != null) {
                for (final Map.Entry<Charset, Double> e : apr.entrySet()) {
                    this.likelihood.get(e.getKey()).updateAmongPartitionRate(e.getValue());
                }
            }
            this.evaluationAPRate.putAll(apr);
            return true;
        }
        return false;
    }
    
    public void setEvaluationParameters(final Parameters parameters) {
        this.dataset = parameters.dataset;
        this.evaluationRate = parameters.evaluationRate;
        this.evaluationModel = parameters.evaluationModel;
        this.evaluationStateFrequencies = parameters.evaluationStateFrequencies;
        this.evaluationDistribution = parameters.evaluationDistribution;
        this.evaluationDistributionSubsets = parameters.evaluationDistributionSubsets;
        this.rateParameters.clear();
        this.evaluationDistributionShape.clear();
        this.evaluationPInv.clear();
        this.evaluationAPRate.clear();
        for (final Charset c : this.dataset.getPartitionCharsets()) {
            final Map<RateParameter, Double> map = new HashMap<RateParameter, Double>();
            RateParameter[] parametersOfModel;
            for (int length = (parametersOfModel = RateParameter.getParametersOfModel(this.evaluationModel)).length, i = 0; i < length; ++i) {
                final RateParameter r = parametersOfModel[i];
                map.put(r, parameters.getRateParameters(c).get(r));
            }
            this.rateParameters.put(c, map);
            this.evaluationDistributionShape.put(c, parameters.getEvaluationDistributionShape(c));
            this.evaluationPInv.put(c, parameters.getEvaluationPInv(c));
            this.evaluationAPRate.put(c, 1.0);
        }
        this.resetLikelihoodValue();
        this.likelihood = null;
    }
    
    public Parameters.EvaluationModel getEvaluationModel() {
        return this.evaluationModel;
    }
    
    public Parameters.EvaluationStateFrequencies getEvaluationStateFrequencies() {
        return this.evaluationStateFrequencies;
    }
    
    public Parameters.EvaluationRate getEvaluationRate() {
        return this.evaluationRate;
    }
    
    public Map<RateParameter, Double> getEvaluationRateParameters(final Charset c) {
        return this.rateParameters.get(c);
    }
    
    public Parameters.EvaluationDistribution getEvaluationDistribution() {
        return this.evaluationDistribution;
    }
    
    public int getEvaluationDistributionSubsets() {
        return this.evaluationDistributionSubsets;
    }
    
    public double getEvaluationGammaShape(final Charset c) {
        return this.evaluationDistributionShape.get(c);
    }
    
    public double getEvaluationPInv(final Charset c) {
        return this.evaluationPInv.get(c);
    }
    
    public double getEvaluationAmongPartitionRate(final Charset c) {
        return this.evaluationAPRate.get(c);
    }
    
    public Dataset getDataset() {
        return this.dataset;
    }
    
    private void computeLevelAndNbrOfLeaves() throws UnrootableTreeException, NullAncestorException {
        if (!this.isRooted()) {
            this.root();
        }
        for (Node n : this.leaves) {
            final Node leaf = n;
            int currentLevel = 1;
            while (n != null) {
                if (!this.nbrOfLeaves.containsKey(n)) {
                    this.nbrOfLeaves.put(n, 1);
                }
                else {
                    this.nbrOfLeaves.put(n, this.nbrOfLeaves.get(n) + 1);
                }
                if (!this.level.containsKey(n)) {
                    this.level.put(n, currentLevel);
                }
                else if (this.level.get(n) < currentLevel) {
                    this.level.put(n, currentLevel);
                }
                if (n == this.root) {
                    n = null;
                }
                else {
                    if (!this.isRooted()) {
                        this.root();
                    }
                    n = n.getAncestorNode();
                }
                ++currentLevel;
            }
        }
    }
    
    public String getLongestTaxon() {
        return this.dataset.getLongestTaxon();
    }
    
    void fireInodeStructureChange() {
        this.level.clear();
        this.nbrOfLeaves.clear();
        if (this.outgroupRoot != null) {
            boolean hasOutgroupNeighbor = false;
            for (final Node n : this.outgroupRoot.getNeighborNodes()) {
                if (this.outgroup.contains(n)) {
                    hasOutgroupNeighbor = true;
                    break;
                }
            }
            if (!hasOutgroupNeighbor) {
                if (this.outInodes.size() > 0) {
                    for (final Node n : this.outInodes) {
                        for (final Node m : n.getNeighborNodes()) {
                            if (!this.outgroup.contains(m)) {
                                this.outgroupRoot = m;
                                this.markAllNodesToReEvaluate();
                                try {
                                    this.root();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                else {
                    for (final Node n : this.outgroup) {
                        for (final Node m : n.getNeighborNodes()) {
                            if (!this.outgroup.contains(m)) {
                                this.outgroupRoot = m;
                                this.markAllNodesToReEvaluate();
                                try {
                                    this.root();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public Parameters.LikelihoodCalculationType getLikelihoodCalculationType() {
        return this.likelihoodType;
    }
    
    public int getLevel(final Node node) throws UnrootableTreeException, NullAncestorException {
        if (this.level.isEmpty()) {
            this.computeLevelAndNbrOfLeaves();
        }
        return this.level.get(node);
    }
    
    public Map<Integer, List<Node>> getNodesInLevels() throws UnrootableTreeException, NullAncestorException {
        final Map<Integer, List<Node>> levelsOfNodes = new HashMap<Integer, List<Node>>();
        for (final Node node : this.nodes) {
            final int nodeLevel = this.getLevel(node);
            if (!levelsOfNodes.containsKey(nodeLevel)) {
                final List<Node> levelList = new ArrayList<Node>();
                levelList.add(node);
                levelsOfNodes.put(nodeLevel, levelList);
            }
            else {
                levelsOfNodes.get(nodeLevel).add(node);
            }
        }
        return levelsOfNodes;
    }
    
    public int getNumOfLeavesUnder(final Node node) throws UnrootableTreeException, NullAncestorException {
        if (this.nbrOfLeaves.isEmpty()) {
            this.computeLevelAndNbrOfLeaves();
        }
        return this.nbrOfLeaves.get(node);
    }
    
    public Set<Node> getAllNodesUnderNeighbor(final Node node, final Node.Neighbor neighbor) {
        final Set<Node> set = new HashSet<Node>();
        final Stack<Node> stack = new Stack<Node>();
        final Set<Node> excludedNeighbors = new HashSet<Node>();
        excludedNeighbors.add(node);
        stack.add(node.getNeighbor(neighbor));
        do {
            final Node current = stack.pop();
            if (!current.isLeaf()) {
                for (final Node n : current.getNeighborNodes()) {
                    if (!excludedNeighbors.contains(n)) {
                        stack.push(n);
                    }
                }
            }
            set.add(current);
            excludedNeighbors.add(current);
        } while (!stack.isEmpty());
        return set;
    }
    
    public List<Node> getPreorderTraversal(final Node node) throws UnrootableTreeException {
        if (!this.isRooted()) {
            this.root();
        }
        final List<Node> traversal = new ArrayList<Node>();
        final Stack<Node> stack = new Stack<Node>();
        stack.add(node);
        do {
            final Node current = stack.pop();
            if (!current.isLeaf()) {
                for (final Node n : current.getChildren()) {
                    stack.push(n);
                }
            }
            traversal.add(current);
        } while (!stack.isEmpty());
        return traversal;
    }
    
    public List<Node> getPostorderTraversal(final Node node) throws UnrootableTreeException {
        if (!this.isRooted()) {
            this.root();
        }
        final List<Node> traversal = new ArrayList<Node>();
        final Stack<Node> stack = new Stack<Node>();
        stack.add(node);
        do {
            final Node current = stack.peek();
            if (!current.isLeaf()) {
                final List<Node> children = current.getChildren();
                if (children.size() > 0 && !traversal.contains(children.get(0))) {
                    stack.push(children.get(0));
                }
                else if (children.size() > 1 && !traversal.contains(children.get(1))) {
                    stack.push(children.get(1));
                }
                else if (children.size() > 2 && !traversal.contains(children.get(2))) {
                    stack.push(children.get(2));
                }
                else {
                    traversal.add(current);
                    stack.pop();
                }
            }
            else {
                traversal.add(current);
                stack.pop();
            }
        } while (!stack.isEmpty());
        return traversal;
    }
    
    public TreesBlock.NewickTreeString toNewick(final boolean printINodes, final boolean printSupportValues) throws UnrootableTreeException, NullAncestorException {
        final TreesBlock.NewickTreeString newick = new TreesBlock.NewickTreeString();
        this.root();
        newick.setTreeString(this.generateNewick(this.root, printINodes, printSupportValues));
        newick.setRootType("R");
        newick.setStarred(false);
        return newick;
    }
    
    public String toNewickLine(final boolean printINodes, final boolean printSupportValues) throws UnrootableTreeException, NullAncestorException {
        String newick = "TREE '" + this.name + "' = [&R] ";
        this.root();
        if (this.root.getClass() == ConsensusNode.class) {
            newick = String.valueOf(newick) + "[&C] ";
        }
        newick = String.valueOf(newick) + this.generateNewick(this.root, printINodes, printSupportValues) + ";";
        return newick;
    }
    
    public String toNewickLineWithML(final String treeName, final boolean printINodes, final boolean printSupportValues) throws UnrootableTreeException, NullAncestorException, BadLocationException {
        final DefaultStyledDocument doc = this.getEvaluationString();
        String newick = "TREE '" + treeName + "' [" + doc.getText(0, doc.getLength()) + "]" + " = [&R] ";
        this.root();
        if (this.root.getClass() == ConsensusNode.class) {
            newick = String.valueOf(newick) + "[&C] ";
        }
        newick = String.valueOf(newick) + this.generateNewick(this.root, printINodes, printSupportValues) + ";";
        return newick;
    }
    
    public DefaultStyledDocument getEvaluationString() throws BadLocationException, UnrootableTreeException, NullAncestorException {
        final String NORMAL = "Normal";
        final String ITALIC = "Italic";
        final String BOLD = "Bold";
        final Hashtable<String, SimpleAttributeSet> paraStyles = new Hashtable<String, SimpleAttributeSet>();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        paraStyles.put("Normal", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setItalic(attr, true);
        paraStyles.put("Italic", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setBold(attr, true);
        paraStyles.put("Bold", attr);
        final AttributeSet defaultStyle = paraStyles.get("Normal");
        final AttributeSet boldStyle = paraStyles.get("Bold");
        final DefaultStyledDocument doc = new DefaultStyledDocument();
        final boolean multipart = this.dataset.getPartitionCharsets().size() > 1;
        doc.insertString(doc.getLength(), "weight vector : ", defaultStyle);
        doc.insertString(doc.getLength(), Tools.doubletoString(this.weight,4), boldStyle);
        doc.insertString(doc.getLength(), "Parsimony : ", defaultStyle);
        doc.insertString(doc.getLength(), Tools.doubletoString(this.Evaluation().get(1), 4), boldStyle);
        doc.insertString(doc.getLength(), "Likelihood of ", defaultStyle);
        doc.insertString(doc.getLength(), Tools.doubletoString(this.Evaluation().get(0), 4), boldStyle);
        doc.insertString(doc.getLength(), " computed using ", defaultStyle);
        doc.insertString(doc.getLength(), this.evaluationModel + " model", boldStyle);
        if (this.evaluationModel.isEmpirical()) {
            doc.insertString(doc.getLength(), " (with " + this.evaluationStateFrequencies + " equilibrium amino acid frequencies)", boldStyle);
        }
        doc.insertString(doc.getLength(), " with a R matrix for ", defaultStyle);
        doc.insertString(doc.getLength(), new StringBuilder().append(this.getEvaluationRate()).toString(), boldStyle);
        if (this.evaluationDistribution == Parameters.EvaluationDistribution.NONE) {
            doc.insertString(doc.getLength(), ", without rate heterogeneity", defaultStyle);
        }
        else {
            doc.insertString(doc.getLength(), ", with ", defaultStyle);
            doc.insertString(doc.getLength(), this.getEvaluationDistribution() + " distribution", boldStyle);
            if (this.evaluationDistribution == Parameters.EvaluationDistribution.GAMMA) {
                doc.insertString(doc.getLength(), " (" + this.evaluationDistributionSubsets + " subsets,", defaultStyle);
                for (final Charset C : this.dataset.getPartitionCharsets()) {
                    doc.insertString(doc.getLength(), " shape" + (multipart ? (" in " + C.getLabel()) : "") + " = " + Tools.doubletoString(this.evaluationDistributionShape.get(C), 4), defaultStyle);
                    doc.insertString(doc.getLength(), ",", defaultStyle);
                }
                doc.remove(doc.getLength() - 1, 1);
                doc.insertString(doc.getLength(), ")", defaultStyle);
            }
        }
        doc.insertString(doc.getLength(), " and", defaultStyle);
        for (final Charset C : this.dataset.getPartitionCharsets()) {
            if (this.evaluationPInv.get(C) < 1.0E-4) {
                doc.insertString(doc.getLength(), " without invariable sites" + (multipart ? (" for " + C.getLabel()) : ""), defaultStyle);
            }
            else {
                doc.insertString(doc.getLength(), " with ", defaultStyle);
                doc.insertString(doc.getLength(), String.valueOf(Tools.doubletoString(this.evaluationPInv.get(C) * 100.0, 2)) + "% P-Invariant", boldStyle);
                doc.insertString(doc.getLength(), multipart ? (" for " + C.getLabel()) : "", defaultStyle);
            }
            doc.insertString(doc.getLength(), ",", defaultStyle);
        }
        doc.remove(doc.getLength() - 1, 1);
        if (this.evaluationModel.getNumRateParameters() > 0 && !this.evaluationModel.isEmpirical()) {
            doc.insertString(doc.getLength(), ". Model parameters :", boldStyle);
            for (final Charset C : this.dataset.getPartitionCharsets()) {
                for (final RateParameter R : this.rateParameters.get(C).keySet()) {
                    doc.insertString(doc.getLength(), " " + R.verbose() + (multipart ? (" in " + C.getLabel()) : "") + " = " + Tools.doubletoString(this.rateParameters.get(C).get(R), 4), defaultStyle);
                    doc.insertString(doc.getLength(), ",", defaultStyle);
                }
            }
            doc.remove(doc.getLength() - 1, 1);
        }
        if (this.dataset.getPartitionCharsets().size() > 1) {
            doc.insertString(doc.getLength(), ". Among-Partition rate variation :", defaultStyle);
            for (final Charset C : this.dataset.getPartitionCharsets()) {
                doc.insertString(doc.getLength(), " rate of ", defaultStyle);
                doc.insertString(doc.getLength(), Tools.doubletoString(this.evaluationAPRate.get(C), 2), boldStyle);
                doc.insertString(doc.getLength(), multipart ? (" for " + C.getLabel()) : "", defaultStyle);
                doc.insertString(doc.getLength(), ",", defaultStyle);
            }
            doc.remove(doc.getLength() - 1, 1);
        }
        return doc;
    }
    
    public void parseEvaluationString(String comment) throws Exception {
        final boolean multipart = this.dataset.getPartitionCharsets().size() > 1;
        Charset C = this.dataset.getPartitionCharsets().iterator().next();
        String sub = comment.substring(comment.indexOf("Likelihood of ") + "Likelihood of ".length(), comment.indexOf(" computed using "));
        this.likelihoodValue = Tools.parseDouble(sub);
        sub = comment.substring(comment.indexOf(" computed using ") + " computed using ".length(), comment.indexOf(" model"));
        this.evaluationModel = Parameters.EvaluationModel.valueOf(sub);
        if (comment.indexOf(" equilibrium amino acid frequencies)") > 0) {
            sub = comment.substring(comment.indexOf(" model (with ") + " model (with ".length(), comment.indexOf(" equilibrium amino acid frequencies)"));
            this.evaluationStateFrequencies = Parameters.EvaluationStateFrequencies.valueOf(sub);
        }
        sub = comment.substring(comment.indexOf(" with a R matrix for ") + " with a R matrix for ".length(), comment.indexOf(", "));
        this.evaluationRate = Parameters.EvaluationRate.valueOf(sub);
        comment = comment.substring(comment.indexOf(", "));
        if (comment.startsWith(", without rate heterogeneity")) {
            this.evaluationDistribution = Parameters.EvaluationDistribution.NONE;
        }
        else {
            sub = comment.substring(comment.indexOf(", with ") + ", with ".length(), comment.indexOf(" distribution"));
            this.evaluationDistribution = Parameters.EvaluationDistribution.valueOf(sub);
            if (this.evaluationDistribution == Parameters.EvaluationDistribution.GAMMA) {
                String gamma = comment.substring(comment.indexOf(" (") + " (".length(), comment.indexOf(")"));
                sub = gamma.substring(0, gamma.indexOf(" subsets,"));
                this.evaluationDistributionSubsets = Integer.parseInt(sub);
                gamma = gamma.substring(gamma.indexOf(" subsets,") + " subsets,".length());
                final String[] gammaParams = gamma.split(",");
                String[] array;
                for (int length = (array = gammaParams).length, i = 0; i < length; ++i) {
                    final String p = array[i];
                    sub = p.substring(p.indexOf(" = ") + " = ".length());
                    if (multipart) {
                        C = this.dataset.getCharset(p.substring(p.indexOf(" in ") + " in ".length(), p.indexOf(" = ")));
                    }
                    this.evaluationDistributionShape.put(C, Tools.parseDouble(sub));
                }
            }
        }
        comment = comment.substring(comment.indexOf(" and") + " and".length());
        if (comment.contains(". Among-Partition rate variation :")) {
            final String[] arps = comment.substring(comment.indexOf(". Among-Partition rate variation :") + ". Among-Partition rate variation :".length()).split(",");
            comment = comment.substring(0, comment.indexOf(". Among-Partition rate variation :"));
            String[] array2;
            for (int length2 = (array2 = arps).length, j = 0; j < length2; ++j) {
                final String p2 = array2[j];
                if (multipart) {
                    sub = p2.substring(" rate of ".length(), p2.indexOf(" for "));
                    C = this.dataset.getCharset(p2.substring(p2.indexOf(" for ") + " for ".length()));
                }
                else {
                    sub = p2.substring(" rate of ".length());
                }
                this.evaluationAPRate.put(C, Tools.parseDouble(sub));
            }
        }
        if (comment.contains(". Model parameters :")) {
            final String[] models = comment.substring(comment.indexOf(". Model parameters :") + ". Model parameters :".length()).split(",");
            comment = comment.substring(0, comment.indexOf(". Model parameters :"));
            String[] array3;
            for (int length3 = (array3 = models).length, k = 0; k < length3; ++k) {
                final String p2 = array3[k];
                sub = p2.substring(p2.indexOf(" = ") + " = ".length());
                final RateParameter R = RateParameter.verboseValueOf(p2.substring(1, p2.indexOf(multipart ? " in " : " = ")));
                if (multipart) {
                    C = this.dataset.getCharset(p2.substring(p2.indexOf(" in ") + " in ".length(), p2.indexOf(" = ")));
                }
                this.rateParameters.get(C).put(R, Tools.parseDouble(sub));
            }
        }
        final String[] pinvs = comment.split(",");
        String[] array4;
        for (int length4 = (array4 = pinvs).length, l = 0; l < length4; ++l) {
            final String p2 = array4[l];
            double pinv;
            if (p2.startsWith(" without invariable sites")) {
                pinv = 0.0;
                if (multipart) {
                    C = this.dataset.getCharset(p2.substring(p2.indexOf(" for ") + " for ".length()));
                }
            }
            else {
                sub = p2.substring(" with ".length(), p2.indexOf("% P-Invariant"));
                pinv = Tools.parseDouble(sub);
                pinv /= 100.0;
                if (multipart) {
                    C = this.dataset.getCharset(p2.substring(p2.indexOf(" for ") + " for ".length()));
                }
            }
            this.evaluationPInv.put(C, pinv);
        }
    }
    
    private String generateNewick(final Node node, final boolean internalLabels, final boolean supportValues) throws NullAncestorException {
        String newick = "";
        if (node.isLeaf()) {
            newick = String.valueOf(newick) + node.getLabel().replace(' ', '_');
            if (node.getAncestorBranchLength() > 0.0) {
                newick = String.valueOf(newick) + ":" + node.getAncestorBranchLength();
            }
            return newick;
        }
        final List<Node> children = node.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            if (i == 0) {
                newick = String.valueOf(newick) + "(";
            }
            newick = String.valueOf(newick) + this.generateNewick(children.get(i), internalLabels, supportValues);
            if (i < children.size() - 1) {
                newick = String.valueOf(newick) + ",";
            }
        }
        newick = String.valueOf(newick) + ")";
        if (internalLabels) {
            if (node.getLabel() != null) {
                newick = String.valueOf(newick) + node.getLabel().replace(' ', '_');
            }
        }
        else if (supportValues && node != this.root && node.getClass() == ConsensusNode.class) {
            newick = String.valueOf(newick) + Tools.doubletoString(((ConsensusNode)node).getAncestorBranchStrength(), 2);
        }
        if (node != this.root && node.getAncestorBranchLength() > 0.0) {
            newick = String.valueOf(newick) + ":" + node.getAncestorBranchLength();
        }
        if (internalLabels && supportValues && node != this.root && node.getClass() == ConsensusNode.class) {
            newick = String.valueOf(newick) + "[C=" + Tools.doubleToPercent(((ConsensusNode)node).getAncestorBranchStrength(), 0) + "]";
        }
        return newick;
    }
    
    public double[][] getAncestralStates(final Node node) throws UnrootableTreeException, NullAncestorException {
        if (this.likelihood == null) {
            this.initLikelihood();
        }
        if (this.parsimony == null) {
            this.initParsimony();
        }
        this.createOneTimeGraphicMemeory();
        final int numOfStates = this.dataset.getDataType().numOfStates();
        final double[][] ancestralStates = new double[this.dataset.getFullNChar()][numOfStates];
        final BitSet existingPosition = new BitSet(this.dataset.getFullNChar());
        for (final Charset c : this.dataset.getPartitionCharsets()) {
            final Dataset.Partition p = this.dataset.getPartition(c);
            final double[][] asc = this.likelihood.get(c).getAncestralStates(node);
            for (int site = 0; site < asc.length; ++site) {
                for (int state = 0; state < asc[site].length; ++state) {
                    for (final int position : p.getDatasetPosition(site)) {
                        ancestralStates[position][state] = asc[site][state];
                        existingPosition.set(position);
                    }
                }
            }
        }
        final double[][] result = new double[existingPosition.cardinality()][numOfStates];
        int i = 0;
        int j = 0;
        while (i < ancestralStates.length) {
            if (existingPosition.get(i)) {
                System.arraycopy(ancestralStates[i], 0, result[j], 0, numOfStates);
                ++j;
            }
            ++i;
        }
        return result;
    }
    
    public String getMostProbableAncestralSequence(final Node node) throws Exception {
        final double[][] as = this.getAncestralStates(node);
        final DataType dataType = this.dataset.getDataType();
        final StringBuilder sb = new StringBuilder();
        for (int site = 0; site < as.length; ++site) {
            sb.append(dataType.getMostProbableData(as[site]).toString());
        }
        return sb.toString();
    }
    
    public DefaultStyledDocument printAncestralStates(final Node node) throws Exception {
        final double[][] as = this.getAncestralStates(node);
        final DataType dataType = this.dataset.getDataType();
        final String NORMAL = "Normal";
        final String ITALIC = "Italic";
        final String BOLD = "Bold";
        final DefaultStyledDocument doc = new DefaultStyledDocument();
        final Hashtable<String, SimpleAttributeSet> paraStyles = new Hashtable<String, SimpleAttributeSet>();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        paraStyles.put("Normal", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setItalic(attr, true);
        paraStyles.put("Italic", attr);
        attr = new SimpleAttributeSet();
        StyleConstants.setBold(attr, true);
        paraStyles.put("Bold", attr);
        final AttributeSet defaultStyle = paraStyles.get("Normal");
        final AttributeSet boldStyle = paraStyles.get("Bold");
        doc.insertString(doc.getLength(), "Most probable sequence for node " + node.getLabel() + " :\n\n", boldStyle);
        for (int site = 0; site < as.length; ++site) {
            doc.insertString(doc.getLength(), dataType.getMostProbableData(as[site]).toString(), defaultStyle);
        }
        doc.insertString(doc.getLength(), "\n\nConditional likelihood distribution by site for node " + node.getLabel() + " :\n\n", boldStyle);
        doc.insertString(doc.getLength(), "Site", boldStyle);
        doc.insertString(doc.getLength(), "\tProbable ancestral state", boldStyle);
        for (int state = 0; state < as[0].length; ++state) {
            doc.insertString(doc.getLength(), "\t" + dataType.getDataWithState(state) + " probability", boldStyle);
        }
        for (int state = 0; state < as[0].length; ++state) {
            doc.insertString(doc.getLength(), "\t" + dataType.getDataWithState(state) + " conditional likelihood", boldStyle);
        }
        doc.insertString(doc.getLength(), "\n", boldStyle);
        for (int site = 0; site < as.length; ++site) {
            doc.insertString(doc.getLength(), new StringBuilder().append(site + 1).toString(), defaultStyle);
            doc.insertString(doc.getLength(), "\t" + dataType.getMostProbableData(as[site]), defaultStyle);
            double sum = 0.0;
            for (int state2 = 0; state2 < as[site].length; ++state2) {
                sum += as[site][state2];
            }
            for (int state2 = 0; state2 < as[site].length; ++state2) {
                doc.insertString(doc.getLength(), "\t" + Tools.doubleToPercent(as[site][state2] / sum, 2), boldStyle);
            }
            for (int state2 = 0; state2 < as[site].length; ++state2) {
                doc.insertString(doc.getLength(), "\t" + Tools.doubletoString(as[site][state2], 2), boldStyle);
            }
            doc.insertString(doc.getLength(), "\n", boldStyle);
        }
        return doc;
    }

    public final List<Node> getNodes() {
        return this.nodes;
    }
    
    static /* synthetic */ void access$4(final Tree tree, final Node outgroupRoot) {
        tree.outgroupRoot = outgroupRoot;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    public double getLikelihoodValue() {
        return likelihoodValue;
    }

    public int getParsimonyValue() {
        return parsimonyValue;
    }


    public void setMPMax(double MPMax) {
        this.MPMax = MPMax;
    }

    public void setMLMax(double MLMax) {
        this.MLMax = MLMax;
    }


    private enum Status
    {
        IN("IN", 0), 
        OUT("OUT", 1), 
        MIX("MIX", 2);
        
        private Status(final String s, final int n) {
        }
    }
    
    private class InOutMap
    {
        Map<Node, Map<Node.Neighbor, Status>> status;
        
        public InOutMap() {
            this.status = new HashMap<Node, Map<Node.Neighbor, Status>>();
            for (final Node inode : Tree.this.inodes) {
                this.status.put(inode, new HashMap<Node.Neighbor, Status>());
            }
            for (final Node inode : Tree.this.inodes) {
                final Map<Node.Neighbor, Status> map = this.status.get(inode);
                for (final Node.Neighbor nei : inode.getNeighborKeys()) {
                    if (!map.containsKey(nei)) {
                        map.put(nei, this.getNeighborsStatus(inode, nei));
                    }
                }
            }
        }
        
        private Status getNeighborsStatus(final Node nodeFrom, final Node.Neighbor neiFrom) {
            final Node node = nodeFrom.getNeighbor(neiFrom);
            if (node.isLeaf()) {
                if (Tree.this.outgroup.contains(node)) {
                    return Status.OUT;
                }
                return Status.IN;
            }
            else {
                final Node.Neighbor n1 = nodeFrom.getNeighborKey(neiFrom);
                final Map<Node.Neighbor, Status> map = this.status.get(node);
                final Node.Neighbor n2 = (n1 != Node.Neighbor.A) ? Node.Neighbor.A : Node.Neighbor.C;
                final Node.Neighbor n3 = (n1 != Node.Neighbor.B) ? Node.Neighbor.B : Node.Neighbor.C;
                if (!map.containsKey(n2)) {
                    map.put(n2, this.getNeighborsStatus(node, n2));
                }
                if (!map.containsKey(n3)) {
                    map.put(n3, this.getNeighborsStatus(node, n3));
                }
                if (map.get(n2) != map.get(n3)) {
                    return Status.MIX;
                }
                return map.get(n2);
            }
        }
        
        public void assignGroup(final Node inode) throws UncompatibleOutgroupException {
            int i = 0;
            int o = 0;
            int m = 0;
            for (final Status s : this.status.get(inode).values()) {
                switch (s) {
                    default: {
                        continue;
                    }
                    case IN: {
                        ++i;
                        continue;
                    }
                    case OUT: {
                        ++o;
                        continue;
                    }
                    case MIX: {
                        ++m;
                        continue;
                    }
                }
            }
            if (o == 2) {
                Tree.this.outgroup.add(inode);
            }
            else {
                if (m == 2 || (i == 1 && o == 1 && m == 1)) {
                    throw new UncompatibleOutgroupException(Tree.this.name);
                }
                if (i == 2 && o == 1) {
                    Tree.access$4(Tree.this, inode);
                }
            }
        }
    }
}

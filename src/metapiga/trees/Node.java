// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import metapiga.modelization.Charset;
import metapiga.modelization.Dataset;
import metapiga.modelization.data.Data;
import metapiga.trees.exceptions.NullAncestorException;

import java.util.*;

import metapiga.trees.exceptions.UnknownNeighborException;
import metapiga.trees.exceptions.TooManyNeighborsException;

public class Node
{
    public static final double MINIMAL_BRANCH_LENGTH = 1.0E-4;
    public static final double MAXIMAL_BRANCH_LENGTH = 5.0;
    protected Map<Neighbor, Node> neighbors;
    protected Map<Neighbor, Double> branchLengths;
    protected String label;
    protected Neighbor ancestor;
    
    public Node()  {
        this.neighbors = new EnumMap<Neighbor, Node>(Neighbor.class);
        this.branchLengths = new EnumMap<Neighbor, Double>(Neighbor.class);
        this.label = null;
    }
    
    public Node(final String label) {
        this.neighbors = new EnumMap<Neighbor, Node>(Neighbor.class);
        this.branchLengths = new EnumMap<Neighbor, Double>(Neighbor.class);
        this.label = label;
    }
    
    public Node(final Node ancestor, double branchLength) {
        this.neighbors = new EnumMap<Neighbor, Node>(Neighbor.class);
        this.branchLengths = new EnumMap<Neighbor, Double>(Neighbor.class);
        if (branchLength <= 0.0) {
            branchLength = 1.0E-4;
        }
        if (branchLength > 5.0) {
            branchLength = 5.0;
        }
        this.neighbors.put(Neighbor.A, ancestor);
        this.branchLengths.put(Neighbor.A, branchLength);
        this.label = null;
        this.ancestor = Neighbor.A;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
    
    public Neighbor addNeighbor(final Node node) throws TooManyNeighborsException {
        if (!node.neighbors.containsKey(Neighbor.A)) {
            node.neighbors.put(Neighbor.A, this);
        }
        else if (!node.neighbors.containsKey(Neighbor.B)) {
            node.neighbors.put(Neighbor.B, this);
        }
        else {
            if (node.neighbors.containsKey(Neighbor.C)) {
                throw new TooManyNeighborsException(node);
            }
            node.neighbors.put(Neighbor.C, this);
        }
        if (!this.neighbors.containsKey(Neighbor.A)) {
            this.neighbors.put(Neighbor.A, node);
            return Neighbor.A;
        }
        if (!this.neighbors.containsKey(Neighbor.B)) {
            this.neighbors.put(Neighbor.B, node);
            return Neighbor.B;
        }
        if (!this.neighbors.containsKey(Neighbor.C)) {
            this.neighbors.put(Neighbor.C, node);
            return Neighbor.C;
        }
        throw new TooManyNeighborsException(this);
    }
    
    public Neighbor addNeighborWithBranchLength(final Node node) throws TooManyNeighborsException {
        final Neighbor keyInThis = this.addNeighbor(node);
        final Neighbor keyInNeighbor = this.getNeighborKey(keyInThis);
        this.branchLengths.put(keyInThis, node.getBranchLength(keyInNeighbor));
        return keyInThis;
    }
    
    public void removeNeighborButKeepBranchLength(final Node node) {
        for (final Map.Entry<Neighbor, Node> e : this.neighbors.entrySet()) {
            if (e.getValue() == node) {
                final Neighbor keyInNode = this.getNeighborKey(e.getKey());
                node.neighbors.remove(keyInNode);
                this.neighbors.remove(e.getKey());
                this.branchLengths.remove(e.getKey());
            }
        }
    }
    
    public void removeAllNeighbors() {
        this.neighbors.clear();
        this.branchLengths.clear();
        this.ancestor = null;
    }
    
    public void setNeighbor(final Neighbor neighbor, final Node node) {
        this.neighbors.put(neighbor, node);
    }
    
    public Neighbor replaceNeighbor(final Node oldNeighbor, final Node newNeighbor) throws UnknownNeighborException {
        for (final Map.Entry<Neighbor, Node> e : this.neighbors.entrySet()) {
            if (e.getValue() == oldNeighbor) {
                this.neighbors.put(e.getKey(), newNeighbor);
                return e.getKey();
            }
        }
        throw new UnknownNeighborException(this, oldNeighbor);
    }
    
    public boolean hasNeighbor(final Neighbor neighbor) {
        return this.neighbors.containsKey(neighbor);
    }
    
    public Node getNeighbor(final Neighbor neighbor) {
        return this.neighbors.get(neighbor);
    }
    
    public Neighbor getNeighborKey(final Neighbor neighbor) {
        for (final Map.Entry<Neighbor, Node> e : this.neighbors.get(neighbor).neighbors.entrySet()) {
            if (e.getValue() == this) {
                return e.getKey();
            }
        }
        return null;
    }
    
    public Set<Node> getNeighborNodes() {
        return new HashSet<Node>(this.neighbors.values());
    }
    
    public Set<Neighbor> getNeighborKeys() {
        return new HashSet<Neighbor>(this.neighbors.keySet());
    }
    
    public Set<Branch> getBranches() {
        final Set<Branch> branches = new HashSet<Branch>();
        for (final Neighbor n : this.neighbors.keySet()) {
            branches.add(new Branch(this, n));
        }
        return branches;
    }
    
    public void setToRoot() {
        this.ancestor = null;
        for (final Node n : this.getNeighborNodes()) {
            n.setAncestor(this);
        }
    }
    
    public void setAncestor(final Node node) {
        for (final Map.Entry<Neighbor, Node> e : this.neighbors.entrySet()) {
            if (e.getValue() != node) {
                e.getValue().setAncestor(this);
            }
            else {
                this.ancestor = e.getKey();
            }
        }
    }
    
    public void removeAncestor() {
        for (final Node n : this.neighbors.values()) {
            if (n != this.neighbors.get(this.ancestor)) {
                n.removeAncestor();
            }
        }
        this.ancestor = null;
    }
    
    public List<Node> getChildren() {
        if (this.ancestor == null) {
            return new ArrayList<Node>(this.neighbors.values());
        }
        final List<Node> list = new ArrayList<Node>(this.neighbors.values());
        list.remove(this.neighbors.get(this.ancestor));
        return list;
    }
    
    public Neighbor getAncestorKey() throws NullAncestorException {
        if (this.ancestor == null) {
            throw new NullAncestorException(this);
        }
        return this.ancestor;
    }
    
    public Node getAncestorNode() throws NullAncestorException {
        if (this.ancestor == null) {
            throw new NullAncestorException(this);
        }
        return this.neighbors.get(this.ancestor);
    }
    
    public double getAncestorBranchLength() throws NullAncestorException {
        if (this.ancestor == null) {
            throw new NullAncestorException(this);
        }
        if (this.branchLengths.containsKey(this.ancestor)) {
            return this.branchLengths.get(this.ancestor);
        }
        return 0.0;
    }
    
    public void setBranchLength(final Neighbor neighbor, double branchLength) {
        if (branchLength < 1.0E-4) {
            branchLength = 1.0E-4;
        }
        if (branchLength > 5.0) {
            branchLength = 5.0;
        }
        this.branchLengths.put(neighbor, branchLength);
        this.neighbors.get(neighbor).branchLengths.put(this.getNeighborKey(neighbor), branchLength);
    }
    
    public double getBranchLength(final Neighbor neighbor) {
        if (this.branchLengths.containsKey(neighbor)) {
            return this.branchLengths.get(neighbor);
        }
        return 0.0;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public boolean isLeaf() {
        return this.neighbors.size() == 1;
    }
    
    public boolean isInode() {
        return this.neighbors.size() > 1;
    }
    
    public enum Neighbor
    {
        A("A", 0), 
        B("B", 1), 
        C("C", 2);
        
        private Neighbor(final String s, final int n) {
        }
    }

//
//    public class Parsimony {
//        private int pValueForNode;
//        private int[] pValueForSite;
//        private boolean isChanged;
//        private List<Set<Data>> status;
//        private int nchar;
//        private Dataset dataset;
//
//        public Parsimony() {
//            pValueForNode = -1;
//            isChanged = true;
//            this.pValueForSite = new int[nchar];
//            this.status = new ArrayList<Set<Data>>();
//        }
//
//        public int getParsimonyValue(int site)  {
//            if(isChanged){
//                this.update();
//            }
//            return pValueForSite[site];
//        }
//
//        public int getParsimonyValue(Dataset dataset) {
//            if(pValueForNode == -1){
//                this.dataset = dataset;
//                this.nchar = dataset.getNChar();
//                for(int i=0;i<nchar;i++){
//                    this.status.add(new HashSet<Data>());
//                }
//                pValueForNode = 0;
//                for(int i=0;i<nchar;i++){
//                    pValueForNode = pValueForNode + getParsimonyValue(i);
//                }
//            }
//            return pValueForNode;
//        }
//
//        public void update()  {
//            if(isInode()){
//                List<Node> children = getChildren();
//                for(Node n :children ){
//                    if(n.parsimony.isChanged) n.parsimony.update();
//                }
//                for(int i=0;i<nchar;i++){
//                    generateStatus(i);
//                }
//
//            }else if (isLeaf()){
//                int index = 0;
//                try {
//                    for(Charset c : this.dataset.getPartitionCharsets()){
//                        List<Data> dSet = this.dataset.getPartition(c).getAllData(getLabel());
//                        if( dSet != null){
//                            for(Data d : dSet){
//                                status.get(index).add(d);
//                                index++;
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                for(int i=0;i<nchar;i++){
//                    pValueForSite[i] = 0;
//                }
//                this.isChanged = false;
//            }else{
//                int aaa;
//            }
//        }
//
//        public void generateStatus(int site){
//            List<Node> children = getChildren();
//            Map<Data,Integer> counter = new HashMap<>();
//            for(Node child:children){
//                Set<Data> s = child.parsimony.status.get(site);
//                for (Data d : s){
//                    if(counter.containsKey(d)){
//                        counter.replace(d,counter.get(d)+1);
//                    }else {
//                        counter.put(d,0);
//                    }
//                }
//            }
//            int max = 0;
//            for(Integer j : counter.values()){
//                if (j>max) max=j;
//            }
//            for(Data d : counter.keySet()){
//                if(counter.get(d) == max){
//                    status.get(site).add(d);
//                }
//            }
//            isChanged = false;
//        }
//    }
}

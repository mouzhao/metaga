// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import metapiga.trees.exceptions.TooManyNeighborsException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Branch
{
    private final Node node;
    private final Node.Neighbor neighbor;
    private Node otherNode;
    private Node.Neighbor otherNeighbor;
    
    public Branch(final Node node, final Node.Neighbor neighbor) {
        this.node = node;
        this.neighbor = neighbor;
        this.otherNode = node.getNeighbor(neighbor);
        this.otherNeighbor = node.getNeighborKey(neighbor);
    }
    
    @Override
    public boolean equals(final Object obj) {
        final Branch b = (Branch)obj;
        return (this.node == b.node && this.neighbor == b.neighbor) || (this.otherNode == b.node && b.node.getNeighbor(b.neighbor) == this.node);
    }
    
    @Override
    public int hashCode() {
        int hash = 42;
        hash = 31 * hash + (this.node.hashCode() + this.otherNode.hashCode());
        return hash;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.node.label) + " -> " + this.otherNode.label;
    }
    
    public String toMirrorString() {
        return String.valueOf(this.otherNode.label) + " -> " + this.node.label;
    }
    
    public Node getNode() {
        return this.node;
    }
    
    public Node getOtherNode() {
        return this.otherNode;
    }
    
    public Node.Neighbor getNeighbor() {
        return this.neighbor;
    }
    
    public Node.Neighbor getOtherNeighbor() {
        return this.otherNeighbor;
    }
    
    public double getLength() {
        return this.node.getBranchLength(this.neighbor);
    }
    
    public void setLength(final double branchLength) {
        this.node.setBranchLength(this.neighbor, branchLength);
    }
    
    public Branch getMirrorBranch() {
        return new Branch(this.otherNode, this.otherNeighbor);
    }
    
    public Branch getNeighborBranch(final Node.Neighbor neigh) {
        return new Branch(this.node, neigh).getMirrorBranch();
    }
    
    public List<Branch> getAllNeighborBranches() {
        final List<Branch> branches = new ArrayList<Branch>();
        for (final Node.Neighbor n : this.node.getNeighborKeys()) {
            if (n != this.neighbor) {
                branches.add(this.getNeighborBranch(n));
            }
        }
        return branches;
    }
    
    public boolean isTipBranch() {
        return this.node.isLeaf() || this.otherNode.isLeaf();
    }
    
    public Branch detach() throws TooManyNeighborsException {
        final List<Branch> neighBranch = this.getAllNeighborBranches();
        final Branch neigh1 = neighBranch.get(0);
        final Branch neigh2 = neighBranch.get(1);
        final double branchLength = neigh1.getLength() + neigh2.getLength();
        this.node.removeNeighborButKeepBranchLength(neigh1.node);
        this.node.removeNeighborButKeepBranchLength(neigh2.node);
        final Node.Neighbor key = neigh1.node.addNeighbor(neigh2.node);
        neigh1.node.setBranchLength(key, branchLength);
        return new Branch(neigh1.node, key);
    }
    
    public void graft(final Branch branch) throws TooManyNeighborsException {
        final double branchLength = this.getLength();
        this.node.removeNeighborButKeepBranchLength(this.otherNode);
        Node.Neighbor key = branch.node.addNeighbor(this.node);
        branch.node.setBranchLength(key, branchLength / 2.0);
        key = branch.node.addNeighbor(this.otherNode);
        branch.node.setBranchLength(key, branchLength / 2.0);
        this.otherNode = branch.node;
        this.otherNeighbor = this.node.getNeighborKey(this.neighbor);
    }
}

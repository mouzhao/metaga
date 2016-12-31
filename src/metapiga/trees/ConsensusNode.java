// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import metapiga.trees.exceptions.NullAncestorException;
import java.util.EnumMap;
import java.util.Map;

public class ConsensusNode extends Node
{
    private Map<Neighbor, Double> branchStrengths;
    
    public ConsensusNode() {
        this.branchStrengths = new EnumMap<Neighbor, Double>(Neighbor.class);
    }
    
    public ConsensusNode(final Consensus.BiPartition p) {
        this.branchStrengths = new EnumMap<Neighbor, Double>(Neighbor.class);
        if (p.getCardinality() == 1) {
            this.label = p.getTaxa();
        }
    }
    
    public void setBranchStrength(final Neighbor neighbor, final double branchStrength) {
        this.branchStrengths.put(neighbor, branchStrength);
        ((ConsensusNode)this.neighbors.get((Object)neighbor)).branchStrengths.put(this.getNeighborKey(neighbor), branchStrength);
    }
    
    @Override
    public void removeAllNeighbors() {
        this.neighbors.clear();
        this.branchLengths.clear();
        this.branchStrengths.clear();
        this.ancestor = null;
    }
    
    public double getBranchStrength(final Neighbor neighbor) {
        if (this.branchStrengths.containsKey(neighbor)) {
            return this.branchStrengths.get(neighbor);
        }
        return 0.0;
    }
    
    public double getAncestorBranchStrength() throws NullAncestorException {
        if (this.ancestor == null) {
            throw new NullAncestorException(this);
        }
        if (this.branchStrengths.containsKey(this.ancestor)) {
            return this.branchStrengths.get(this.ancestor);
        }
        return 0.0;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.exceptions.UnrootableTreeException;
import java.util.Iterator;
import metapiga.parameters.Parameters;
import java.util.HashMap;
import java.util.Map;

public class ConsensusMRE
{
    private final Map<String, Double> mainConsensus;
    private final Map<String, Double> consensus;
    
    public ConsensusMRE() {
        this.mainConsensus = new HashMap<String, Double>();
        this.consensus = new HashMap<String, Double>();
    }
    
    public void addConsensus(final Tree consensusTree, final Parameters parameters, final boolean main) throws UnrootableTreeException, NullAncestorException {
        final Map<String, Double> current = (main || this.mainConsensus.isEmpty()) ? this.mainConsensus : this.consensus;
        current.clear();
        consensusTree.root();
        final Consensus consensus = new Consensus(consensusTree, parameters.dataset);
        for (final Node n : consensusTree.getInodes()) {
            if (n != consensusTree.getRoot()) {
                final Branch b = new Branch(n, n.getAncestorKey());
                current.put(consensus.getBiPartition(b).toString(), ((ConsensusNode)n).getAncestorBranchStrength());
            }
        }
    }
    
    public double meanRelativeError() {
        if (this.consensus.isEmpty()) {
            return 1.0;
        }
        double mre = 0.0;
        final double nBranch = this.consensus.keySet().size();
        final Set<String> partitions = new HashSet<String>();
        partitions.addAll(this.mainConsensus.keySet());
        partitions.addAll(this.consensus.keySet());
        for (final String partition : partitions) {
            if (this.mainConsensus.containsKey(partition) && this.consensus.containsKey(partition)) {
                double score1 = this.mainConsensus.get(partition);
                double score2 = this.consensus.get(partition);
                if (score1 == 0.0 && score2 == 0.0) {
                    ++mre;
                }
                else {
                    final double norm = 1.0 / ((score1 > score2) ? score1 : score2);
                    score1 *= norm;
                    score2 *= norm;
                    mre += Math.abs(score1 - score2);
                }
            }
            else {
                ++mre;
            }
        }
        return mre / nBranch;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

import metapiga.trees.Tree;
import metapiga.trees.Branch;
import metapiga.trees.Node;

public class BranchNotFoundException extends Exception
{
    public BranchNotFoundException(final Node node, final Node otherNode) {
        super("Node " + node + " and " + otherNode + " are not on the same branch.");
    }
    
    public BranchNotFoundException(final Node node, final Node otherNode, final Throwable cause) {
        super("Node " + node + " and " + otherNode + " are not on the same branch.", cause);
    }
    
    public BranchNotFoundException(final Branch branch, final Tree T, final Tree T2) {
        super("Branch " + branch.toString() + " of " + T + " was not found in " + T2);
    }
    
    public BranchNotFoundException(final Branch branch, final Tree T, final Tree T2, final Throwable cause) {
        super("Branch " + branch.toString() + " of " + T + " was not found in " + T2, cause);
    }
    
    public BranchNotFoundException(final String message, final Branch branch, final Tree T, final Tree T2) {
        super(String.valueOf(message) + " - Branch " + branch.toString() + " of " + T + " was not found in " + T2);
    }
    
    public BranchNotFoundException(final String message, final Branch branch, final Tree T, final Tree T2, final Throwable cause) {
        super(String.valueOf(message) + " - Branch " + branch.toString() + " of " + T + " was not found in " + T2, cause);
    }
}

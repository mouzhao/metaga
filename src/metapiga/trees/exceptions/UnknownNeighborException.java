// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

import metapiga.trees.Node;

public class UnknownNeighborException extends Exception
{
    public UnknownNeighborException(final Node node, final Node neighbor) {
        super("Node " + neighbor + " is not a neighbor of node " + node);
    }
    
    public UnknownNeighborException(final Node node, final Node neighbor, final Throwable cause) {
        super("Node " + neighbor + " is not a neighbor of node " + node, cause);
    }
}

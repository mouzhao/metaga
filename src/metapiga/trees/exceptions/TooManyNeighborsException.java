// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

import metapiga.trees.Node;

public class TooManyNeighborsException extends Exception
{
    public TooManyNeighborsException(final Node node) {
        super("Cannot add a new neighbor, node (" + node + ") has already 3 neighbors.");
    }
    
    public TooManyNeighborsException(final Node node, final Throwable cause) {
        super("Cannot add a new neighbor, node (" + node + ") has already 3 neighbors.", cause);
    }
}

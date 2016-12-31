// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

import metapiga.trees.Node;

public class NullAncestorException extends Exception
{
    public NullAncestorException(final Node node) {
        super("Node " + node + " is the root or tree is unrooted, and ancestors are not defined");
    }
    
    public NullAncestorException(final Node node, final Throwable cause) {
        super("Node " + node + " is the root or tree is unrooted, and ancestors are not defined", cause);
    }
}

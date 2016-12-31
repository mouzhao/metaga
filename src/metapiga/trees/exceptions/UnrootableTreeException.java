// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

public class UnrootableTreeException extends Exception
{
    public UnrootableTreeException() {
        super("No outgroup and no access node, cannot root the tree.");
    }
    
    public UnrootableTreeException(final Throwable cause) {
        super("No outgroup and no access node, cannot root the tree.", cause);
    }
}

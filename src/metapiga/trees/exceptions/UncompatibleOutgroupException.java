// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

public class UncompatibleOutgroupException extends Exception
{
    public UncompatibleOutgroupException(final String treeName) {
        super("Defined outgroup is not compatible with topology of tree " + treeName);
    }
    
    public UncompatibleOutgroupException(final String treeName, final Throwable cause) {
        super("Defined outgroup is not compatible with topology of tree " + treeName);
    }
}

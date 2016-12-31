// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

public class NoInclusionException extends Exception
{
    public NoInclusionException(final String big, final String small) {
        super("Bipartition " + small + " is not included in " + big + ".");
    }
    
    public NoInclusionException(final String big, final String small, final Throwable cause) {
        super("Bipartition " + small + " is not included in " + big + ".", cause);
    }
    
    public NoInclusionException(final String big, final String small, final String message) {
        super("Bipartition " + small + " is not included in " + big + ". " + message);
    }
    
    public NoInclusionException(final String big, final String small, final String message, final Throwable cause) {
        super("Bipartition " + small + " is not included in " + big + ". " + message, cause);
    }
}

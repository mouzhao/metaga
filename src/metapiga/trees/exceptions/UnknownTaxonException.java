// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees.exceptions;

public class UnknownTaxonException extends Exception
{
    public UnknownTaxonException(final String taxa) {
        super("Unknown taxa : " + taxa);
    }
    
    public UnknownTaxonException(final String taxa, final Throwable cause) {
        super("Unknown taxa : " + taxa, cause);
    }
}

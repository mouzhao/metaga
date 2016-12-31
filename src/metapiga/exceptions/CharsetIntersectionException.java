// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.exceptions;

import metapiga.modelization.Charset;

public class CharsetIntersectionException extends Exception
{
    public CharsetIntersectionException(final Charset c1, final Charset c2) {
        super("Charset " + c1 + " intersection with charset " + c2 + " is not empty.");
    }
    
    public CharsetIntersectionException(final Charset c1, final Charset c2, final String message) {
        super("Charset " + c1 + " intersection with charset " + c2 + " is not empty. " + message);
    }
    
    public CharsetIntersectionException(final Charset c1, final Charset c2, final Throwable cause) {
        super("Charset " + c1 + " intersection with charset " + c2 + " is not empty.", cause);
    }
    
    public CharsetIntersectionException(final Charset c1, final Charset c2, final String message, final Throwable cause) {
        super("Charset " + c1 + " intersection with charset " + c2 + " is not empty. " + message, cause);
    }
}

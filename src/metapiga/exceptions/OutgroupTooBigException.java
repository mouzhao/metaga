// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.exceptions;

public class OutgroupTooBigException extends Exception
{
    public OutgroupTooBigException() {
        super("Outgroup is too big, ingroup must contain at least 2 taxas");
    }
    
    public OutgroupTooBigException(final Throwable cause) {
        super("Outgroup is too big, ingroup must contain at least 2 taxas", cause);
    }
}

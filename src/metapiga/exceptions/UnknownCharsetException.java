// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.exceptions;

import metapiga.modelization.Charset;

public class UnknownCharsetException extends Exception
{
    private static final long serialVersionUID = -7700608519028240174L;
    
    public UnknownCharsetException(final Charset charset) {
        super("Unknown charset:" + charset.getLabel());
    }
}

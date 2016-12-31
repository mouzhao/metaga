// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.exceptions;

import java.util.BitSet;

public class UnknownDataException extends Exception
{
    public UnknownDataException(final String dataType) {
        super("Unknown DataType: " + dataType);
    }
    
    public UnknownDataException(final BitSet bitset) {
        super("Unknown Data token from BitSet: " + bitset);
    }
    
    public UnknownDataException(final BitSet bitset, final Throwable cause) {
        super("Unknown Data token from BitSet: " + bitset, cause);
    }
    
    public UnknownDataException(final int state) {
        super("Unknown Data token from state: " + state);
    }
    
    public UnknownDataException(final int state, final Throwable cause) {
        super("Unknown Data token from state: " + state, cause);
    }
    
    public UnknownDataException(final String data, final String taxa) {
        super("Unknown Data token: " + data + " from taxa " + taxa);
    }
    
    public UnknownDataException(final String data, final String taxa, final Throwable cause) {
        super("Unknown Data token: " + data + " from taxa " + taxa, cause);
    }
}

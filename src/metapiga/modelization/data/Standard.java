// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data;

import metapiga.exceptions.UnknownDataException;
import java.awt.Color;
import java.util.BitSet;

public enum Standard implements Data
{
    _0("_0", 0, true, false, 0, Color.RED), 
    _1("_1", 1, false, true, 1, Color.BLUE), 
    X("X", 2, true, true, -1, new Color(255, 20, 147));
    
    private final BitSet bits;
    private final Color color;
    public final int state;
    
    private Standard(final String s, final int n, final boolean _0, final boolean _1, final int state, final Color color) {
        this.state = state;
        this.color = color;
        (this.bits = new BitSet(2)).set(0, _0);
        this.bits.set(1, _1);
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append(this.state).toString();
    }
    
    @Override
    public char toChar() {
        switch (this.state) {
            case 0: {
                return '0';
            }
            case 1: {
                return '1';
            }
            default: {
                return 'X';
            }
        }
    }
    
    @Override
    public final BitSet toBits() {
        return this.bits;
    }
    
    @Override
    public final int numOfStates() {
        return this.bits.cardinality();
    }
    
    @Override
    public final int getMaxStates() {
        return 2;
    }
    
    @Override
    public final int getState() {
        return this.state;
    }
    
    @Override
    public final Color getColor() {
        return this.color;
    }
    
    @Override
    public final boolean isState(final int state) {
        return this.bits.get(state);
    }
    
    @Override
    public final boolean isUndeterminate() {
        return false;
    }
    
    public static Standard getStandardWithState(final int state) throws UnknownDataException {
        Standard[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Standard standard = values[i];
            if (standard.state == state) {
                return standard;
            }
        }
        throw new UnknownDataException(state);
    }
    
    public static Standard getStandard(final BitSet bitSet) throws UnknownDataException {
        Standard[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Standard standard = values[i];
            if (standard.bits.equals(bitSet)) {
                return standard;
            }
        }
        throw new UnknownDataException(bitSet);
    }
    
    public static int getStateOf(final String standard) {
        try {
            return Integer.parseInt(standard);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
}

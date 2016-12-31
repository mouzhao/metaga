// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data;

import metapiga.exceptions.UnknownDataException;
import java.awt.Color;
import java.util.BitSet;

public enum DNA implements Data
{
    A("A", 0, true, false, false, false, 0, Color.GREEN), 
    C("C", 1, false, true, false, false, 1, Color.BLUE), 
    G("G", 2, false, false, true, false, 2, Color.YELLOW), 
    T("T", 3, false, false, false, true, 3, Color.RED), 
    R("R", 4, true, false, true, false, -1, new Color(255, 20, 147)), 
    Y("Y", 5, false, true, false, true, -1, new Color(255, 20, 147)), 
    W("W", 6, true, false, false, true, -1, new Color(255, 20, 147)), 
    S("S", 7, false, true, true, false, -1, new Color(255, 20, 147)), 
    M("M", 8, true, true, false, false, -1, new Color(255, 20, 147)), 
    K("K", 9, false, false, true, true, -1, new Color(255, 20, 147)), 
    B("B", 10, false, true, true, true, -1, new Color(255, 20, 147)), 
    D("D", 11, true, false, true, true, -1, new Color(255, 20, 147)), 
    H("H", 12, true, true, false, true, -1, new Color(255, 20, 147)), 
    V("V", 13, true, true, true, false, -1, new Color(255, 20, 147)), 
    N("N", 14, true, true, true, true, -1, new Color(255, 20, 147));
    
    private final BitSet bits;
    private final Color color;
    public final int state;
    
    private DNA(final String s, final int n, final boolean A, final boolean C, final boolean G, final boolean T, final int state, final Color color) {
        this.state = state;
        this.color = color;
        (this.bits = new BitSet(4)).set(0, A);
        this.bits.set(1, C);
        this.bits.set(2, G);
        this.bits.set(3, T);
    }
    
    @Override
    public String toString() {
        return this.name();
    }
    
    @Override
    public char toChar() {
        return this.name().charAt(0);
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
        return 4;
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
        return this == DNA.N;
    }
    
    public static DNA getDNAWithState(final int state) throws UnknownDataException {
        DNA[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final DNA dna = values[i];
            if (dna.state == state) {
                return dna;
            }
        }
        throw new UnknownDataException(state);
    }
    
    public static DNA getDNA(final BitSet bitSet) throws UnknownDataException {
        DNA[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final DNA dna = values[i];
            if (dna.bits.equals(bitSet)) {
                return dna;
            }
        }
        throw new UnknownDataException(bitSet);
    }
    
    public static int getStateOf(final String dna) {
        return valueOf(dna).state;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data;

import metapiga.exceptions.UnknownDataException;
import java.awt.Color;
import java.util.BitSet;

public enum Protein implements Data
{
    A("A", 0, 0, Color.BLUE), 
    R("R", 1, 1, Color.GREEN), 
    N("N", 2, 2, new Color(46, 139, 87)), 
    D("D", 3, 3, new Color(127, 255, 0)), 
    C("C", 4, 4, Color.YELLOW), 
    Q("Q", 5, 5, new Color(30, 250, 100)), 
    E("E", 6, 6, new Color(202, 255, 112)), 
    G("G", 7, 7, new Color(16, 78, 139)), 
    H("H", 8, 8, new Color(0, 250, 154)), 
    I("I", 9, 9, new Color(0, 191, 255)), 
    L("L", 10, 10, new Color(103, 148, 255)), 
    K("K", 11, 11, new Color(145, 214, 134)), 
    M("M", 12, 12, new Color(219, 219, 112)), 
    F("F", 13, 13, Color.RED), 
    P("P", 14, 14, Color.CYAN), 
    S("S", 15, 15, new Color(127, 255, 212)), 
    T("T", 16, 16, new Color(134, 206, 189)), 
    W("W", 17, 17, Color.ORANGE), 
    Y("Y", 18, 18, new Color(255, 127, 0)), 
    V("V", 19, 19, new Color(138, 43, 226)), 
    B("B", 20, -1, new Color(255, 20, 147)), 
    Z("Z", 21, -1, new Color(255, 20, 147)), 
    J("J", 22, -1, new Color(255, 20, 147)), 
    X("X", 23, -1, new Color(255, 20, 147));
    
    private final BitSet bits;
    private final Color color;
    public final int state;
    
    private Protein(final String s, final int n, final int state, final Color color) {
        this.state = state;
        this.color = color;
        this.bits = new BitSet(20);
        if (state >= 0) {
            this.bits.set(state);
        }
        else if (this.name().equals("X")) {
            this.bits.set(0, 20);
        }
        else if (this.name().equals("B")) {
            this.bits.set(2);
            this.bits.set(3);
        }
        else if (this.name().equals("Z")) {
            this.bits.set(5);
            this.bits.set(6);
        }
        else if (this.name().equals("J")) {
            this.bits.set(9);
            this.bits.set(10);
        }
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
        return 20;
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
        return this == Protein.X;
    }
    
    public static Protein getProteinWithState(final int state) throws UnknownDataException {
        Protein[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Protein protein = values[i];
            if (protein.state == state) {
                return protein;
            }
        }
        throw new UnknownDataException(state);
    }
    
    public static Protein getProtein(final BitSet bitSet) throws UnknownDataException {
        Protein[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Protein protein = values[i];
            if (protein.bits.equals(bitSet)) {
                return protein;
            }
        }
        throw new UnknownDataException(bitSet);
    }
    
    public static int getStateOf(final String protein) {
        return valueOf(protein).state;
    }
}

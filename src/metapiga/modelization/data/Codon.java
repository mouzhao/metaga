// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data;

import metapiga.exceptions.UnknownDataException;
import java.awt.Color;
import java.util.BitSet;

public enum Codon implements Data
{
    TTT("TTT", 0, 0, Color.getHSBColor(0.0f, 1.0f, 1.0f)), 
    TTC("TTC", 1, 1, Color.getHSBColor(0.015625f, 1.0f, 1.0f)), 
    TTA("TTA", 2, 2, Color.getHSBColor(0.03125f, 1.0f, 1.0f)), 
    TTG("TTG", 3, 3, Color.getHSBColor(0.046875f, 1.0f, 1.0f)), 
    TCT("TCT", 4, 4, Color.getHSBColor(0.0625f, 1.0f, 1.0f)), 
    TCC("TCC", 5, 5, Color.getHSBColor(0.078125f, 1.0f, 1.0f)), 
    TCA("TCA", 6, 6, Color.getHSBColor(0.09375f, 1.0f, 1.0f)), 
    TCG("TCG", 7, 7, Color.getHSBColor(0.109375f, 1.0f, 1.0f)), 
    TAT("TAT", 8, 8, Color.getHSBColor(0.125f, 1.0f, 1.0f)), 
    TAC("TAC", 9, 9, Color.getHSBColor(0.140625f, 1.0f, 1.0f)), 
    TAA("TAA", 10, 10, Color.getHSBColor(0.15625f, 1.0f, 1.0f)), 
    TAG("TAG", 11, 11, Color.getHSBColor(0.171875f, 1.0f, 1.0f)), 
    TGT("TGT", 12, 12, Color.getHSBColor(0.1875f, 1.0f, 1.0f)), 
    TGC("TGC", 13, 13, Color.getHSBColor(0.203125f, 1.0f, 1.0f)), 
    TGA("TGA", 14, 14, Color.getHSBColor(0.21875f, 1.0f, 1.0f)), 
    TGG("TGG", 15, 15, Color.getHSBColor(0.234375f, 1.0f, 1.0f)), 
    CTT("CTT", 16, 16, Color.getHSBColor(0.25f, 1.0f, 1.0f)), 
    CTC("CTC", 17, 17, Color.getHSBColor(0.265625f, 1.0f, 1.0f)), 
    CTA("CTA", 18, 18, Color.getHSBColor(0.28125f, 1.0f, 1.0f)), 
    CTG("CTG", 19, 19, Color.getHSBColor(0.296875f, 1.0f, 1.0f)), 
    CCT("CCT", 20, 20, Color.getHSBColor(0.3125f, 1.0f, 1.0f)), 
    CCC("CCC", 21, 21, Color.getHSBColor(0.328125f, 1.0f, 1.0f)), 
    CCA("CCA", 22, 22, Color.getHSBColor(0.34375f, 1.0f, 1.0f)), 
    CCG("CCG", 23, 23, Color.getHSBColor(0.359375f, 1.0f, 1.0f)), 
    CAT("CAT", 24, 24, Color.getHSBColor(0.375f, 1.0f, 1.0f)), 
    CAC("CAC", 25, 25, Color.getHSBColor(0.390625f, 1.0f, 1.0f)), 
    CAA("CAA", 26, 26, Color.getHSBColor(0.40625f, 1.0f, 1.0f)), 
    CAG("CAG", 27, 27, Color.getHSBColor(0.421875f, 1.0f, 1.0f)), 
    CGT("CGT", 28, 28, Color.getHSBColor(0.4375f, 1.0f, 1.0f)), 
    CGC("CGC", 29, 29, Color.getHSBColor(0.453125f, 1.0f, 1.0f)), 
    CGA("CGA", 30, 30, Color.getHSBColor(0.46875f, 1.0f, 1.0f)), 
    CGG("CGG", 31, 31, Color.getHSBColor(0.484375f, 1.0f, 1.0f)), 
    ATT("ATT", 32, 32, Color.getHSBColor(0.5f, 1.0f, 1.0f)), 
    ATC("ATC", 33, 33, Color.getHSBColor(0.515625f, 1.0f, 1.0f)), 
    ATA("ATA", 34, 34, Color.getHSBColor(0.53125f, 1.0f, 1.0f)), 
    ATG("ATG", 35, 35, Color.getHSBColor(0.546875f, 1.0f, 1.0f)), 
    ACT("ACT", 36, 36, Color.getHSBColor(0.5625f, 1.0f, 1.0f)), 
    ACC("ACC", 37, 37, Color.getHSBColor(0.578125f, 1.0f, 1.0f)), 
    ACA("ACA", 38, 38, Color.getHSBColor(0.59375f, 1.0f, 1.0f)), 
    ACG("ACG", 39, 39, Color.getHSBColor(0.609375f, 1.0f, 1.0f)), 
    AAT("AAT", 40, 40, Color.getHSBColor(0.625f, 1.0f, 1.0f)), 
    AAC("AAC", 41, 41, Color.getHSBColor(0.640625f, 1.0f, 1.0f)), 
    AAA("AAA", 42, 42, Color.getHSBColor(0.65625f, 1.0f, 1.0f)), 
    AAG("AAG", 43, 43, Color.getHSBColor(0.671875f, 1.0f, 1.0f)), 
    AGT("AGT", 44, 44, Color.getHSBColor(0.6875f, 1.0f, 1.0f)), 
    AGC("AGC", 45, 45, Color.getHSBColor(0.703125f, 1.0f, 1.0f)), 
    AGA("AGA", 46, 46, Color.getHSBColor(0.71875f, 1.0f, 1.0f)), 
    AGG("AGG", 47, 47, Color.getHSBColor(0.734375f, 1.0f, 1.0f)), 
    GTT("GTT", 48, 48, Color.getHSBColor(0.75f, 1.0f, 1.0f)), 
    GTC("GTC", 49, 49, Color.getHSBColor(0.765625f, 1.0f, 1.0f)), 
    GTA("GTA", 50, 50, Color.getHSBColor(0.78125f, 1.0f, 1.0f)), 
    GTG("GTG", 51, 51, Color.getHSBColor(0.796875f, 1.0f, 1.0f)), 
    GCT("GCT", 52, 52, Color.getHSBColor(0.8125f, 1.0f, 1.0f)), 
    GCC("GCC", 53, 53, Color.getHSBColor(0.828125f, 1.0f, 1.0f)), 
    GCA("GCA", 54, 54, Color.getHSBColor(0.84375f, 1.0f, 1.0f)), 
    GCG("GCG", 55, 55, Color.getHSBColor(0.859375f, 1.0f, 1.0f)), 
    GAT("GAT", 56, 56, Color.getHSBColor(0.875f, 1.0f, 1.0f)), 
    GAC("GAC", 57, 57, Color.getHSBColor(0.890625f, 1.0f, 1.0f)), 
    GAA("GAA", 58, 58, Color.getHSBColor(0.90625f, 1.0f, 1.0f)), 
    GAG("GAG", 59, 59, Color.getHSBColor(0.921875f, 1.0f, 1.0f)), 
    GGT("GGT", 60, 60, Color.getHSBColor(0.9375f, 1.0f, 1.0f)), 
    GGC("GGC", 61, 61, Color.getHSBColor(0.953125f, 1.0f, 1.0f)), 
    GGA("GGA", 62, 62, Color.getHSBColor(0.96875f, 1.0f, 1.0f)), 
    GGG("GGG", 63, 63, Color.getHSBColor(0.984375f, 1.0f, 1.0f)), 
    __X("__X", 64, -1, new Color(255, 20, 147));
    
    private final BitSet bits;
    private final Color color;
    public final int state;
    
    private Codon(final String s, final int n, final int state, final Color color) {
        this.state = state;
        this.color = color;
        this.bits = new BitSet(64);
        if (state >= 0) {
            this.bits.set(state);
        }
        else if (this.name().equals("__X")) {
            this.bits.set(0, 64);
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
        return 64;
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
        return this == Codon.__X;
    }
    
    public final DNA[] getNucleotides() {
        final DNA[] nucleotides = new DNA[3];
        if (this.equals(Codon.__X)) {
            for (int i = 0; i < nucleotides.length; ++i) {
                nucleotides[i] = DNA.N;
            }
            return nucleotides;
        }
        final String stringCodon = this.toString();
        final String[] stringNucleotides = stringCodon.split("");
        int j = 0;
        String[] array;
        for (int length = (array = stringNucleotides).length, k = 0; k < length; ++k) {
            final String s = array[k];
            if (!s.contentEquals("")) {
                nucleotides[j] = DNA.valueOf(s);
                ++j;
            }
        }
        return nucleotides;
    }
    
    public static Codon getCodonWithState(final int state) throws UnknownDataException {
        Codon[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Codon codon = values[i];
            if (codon.state == state) {
                return codon;
            }
        }
        throw new UnknownDataException(state);
    }
    
    public static Codon getCodon(final BitSet bitSet) throws UnknownDataException {
        Codon[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Codon codon = values[i];
            if (codon.bits.equals(bitSet)) {
                return codon;
            }
        }
        throw new UnknownDataException(bitSet);
    }
    
    public static int getStateOf(final String codon) {
        return valueOf(codon).state;
    }
}

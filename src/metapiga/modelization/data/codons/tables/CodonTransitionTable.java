// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data.codons.tables;

import metapiga.modelization.data.Codon;

public abstract class CodonTransitionTable
{
    public abstract boolean isSynonymous(final Codon p0, final Codon p1);
    
    public abstract boolean isStopCodon(final Codon p0);
    
    public boolean isTransition(final Codon fromCodon, final Codon toCodon) {
        if (this.isDifferentMoreThanOneNucleotide(fromCodon, toCodon)) {
            return false;
        }
        final char[] fnstr = fromCodon.toString().toCharArray();
        final char[] tnstr = toCodon.toString().toCharArray();
        int diffIdx = -1;
        for (int i = 0; i < tnstr.length; ++i) {
            if (fnstr[i] != tnstr[i]) {
                diffIdx = i;
            }
        }
        return fromCodon != toCodon && ((fnstr[diffIdx] == 'C' && tnstr[diffIdx] == 'T') || (fnstr[diffIdx] == 'T' && tnstr[diffIdx] == 'C') || (fnstr[diffIdx] == 'A' && tnstr[diffIdx] == 'G') || (fnstr[diffIdx] == 'G' && tnstr[diffIdx] == 'A'));
    }
    
    public boolean isTransversion(final Codon fromCodon, final Codon toCodon) {
        return fromCodon != toCodon && !this.isDifferentMoreThanOneNucleotide(fromCodon, toCodon) && !this.isTransition(fromCodon, toCodon);
    }
    
    public boolean isDifferentMoreThanOneNucleotide(final Codon fromCodon, final Codon toCodon) {
        final char[] fnstr = fromCodon.toString().toCharArray();
        final char[] tnstr = toCodon.toString().toCharArray();
        int diff = 0;
        if (fromCodon == Codon.__X || toCodon == Codon.__X) {
            return true;
        }
        for (int i = 0; i < tnstr.length; ++i) {
            if (fnstr[i] != tnstr[i]) {
                ++diff;
            }
        }
        return diff > 1;
    }
}

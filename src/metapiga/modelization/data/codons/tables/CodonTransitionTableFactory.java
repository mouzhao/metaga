// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data.codons.tables;

import metapiga.parameters.Parameters;

public class CodonTransitionTableFactory
{
    public static CodonTransitionTable getInstance(final Parameters.CodonTransitionTableType type) {
        switch (type) {
            case UNIVERSAL: {
                return new UniversalCodonTransitionTable();
            }
            case CDHNNUC: {
                return new CDHNuclearCode();
            }
            case EFMITOCH: {
                return new EchinodermFlatwormMitochCode();
            }
            case EUPLOTIDNUC: {
                return new EuploidNuclearCode();
            }
            case INVERTMITOCH: {
                return new InvertebrateMitochondrialCode();
            }
            case VERTMITOCH: {
                return new VertebrateMitochondrialCode();
            }
            case MPCMMITOCH: {
                return new MoldProtoCoelMitochCode();
            }
            default: {
                assert false : "Unknown codon table";
                return new UniversalCodonTransitionTable();
            }
        }
    }
}

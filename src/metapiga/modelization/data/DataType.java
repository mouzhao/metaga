//
// Decompiled by Procyon v0.5.30
//

package metapiga.modelization.data;

import java.util.BitSet;
import metapiga.exceptions.UnknownDataException;

public enum DataType
{
    DNA("DNA", 0, 4, "Nucleotide"),
    PROTEIN("PROTEIN", 1, 20, "Amino acid"),
    STANDARD("STANDARD", 2, 2, "Standard binary data"),
    CODON("CODON", 3, 64, "Nucleotide codons");

    private final int numOfStates;
    private final String verbose;

    private DataType(final String s, final int n, final int numOfStates, final String verbose) {
        this.numOfStates = numOfStates;
        this.verbose = verbose;
    }

    public final int numOfStates() {
        return this.numOfStates;
    }

    public final String verbose() {
        return this.verbose;
    }

    public final Data getDataWithState(final int state) throws UnknownDataException {
        switch (this) {
            case DNA: {
                return metapiga.modelization.data.DNA.getDNAWithState(state);
            }
            case CODON: {
                return Codon.getCodonWithState(state);
            }
            case PROTEIN: {
                return Protein.getProteinWithState(state);
            }
            case STANDARD: {
                return Standard.getStandardWithState(state);
            }
            default: {
                throw new UnknownDataException(this.toString());
            }
        }
    }

    public final Data getData(final String data) throws UnknownDataException {
        switch (this) {
            case DNA: {
                return metapiga.modelization.data.DNA.valueOf(data);
            }
            case CODON: {
                return Codon.valueOf(data);
            }
            case PROTEIN: {
                return Protein.valueOf(data);
            }
            case STANDARD: {
                try {
                    return Standard.getStandardWithState(Integer.parseInt(data));
                }
                catch (NumberFormatException e) {
                    return Standard.X;
                }
            }
        }
        throw new UnknownDataException(this.toString());
    }

    public final Data getData(final BitSet bitSet) throws UnknownDataException {
        switch (this) {
            case DNA: {
                return metapiga.modelization.data.DNA.getDNA(bitSet);
            }
            case PROTEIN: {
                return Protein.getProtein(bitSet);
            }
            case STANDARD: {
                return Standard.getStandard(bitSet);
            }
            case CODON: {
                return Codon.getCodon(bitSet);
            }
            default: {
                throw new UnknownDataException(this.toString());
            }
        }
    }

    public final int getStateOf(final String data) throws UnknownDataException {
        switch (this) {
            case DNA: {
                return metapiga.modelization.data.DNA.getStateOf(data);
            }
            case PROTEIN: {
                return Protein.getStateOf(data);
            }
            case STANDARD: {
                return Standard.getStateOf(data);
            }
            case CODON: {
                return Codon.getStateOf(data);
            }
            default: {
                throw new UnknownDataException(this.toString());
            }
        }
    }

    public final Data getUndeterminateData() throws UnknownDataException {
        switch (this) {
            case DNA: {
                return metapiga.modelization.data.DNA.N;
            }
            case CODON: {
                return Codon.__X;
            }
            case PROTEIN: {
                return Protein.X;
            }
            case STANDARD: {
                return Standard.X;
            }
            default: {
                throw new UnknownDataException(this.toString());
            }
        }
    }

    public final Data getMostProbableData(final double[] probabilities) throws Exception {
        if (probabilities.length != this.numOfStates) {
            throw new Exception("Array probabilities must have " + this.numOfStates + " components, it has only " + probabilities.length);
        }
        final BitSet b = new BitSet(this.numOfStates);
        double best = probabilities[0];
        b.set(0);
        for (int state = 1; state < this.numOfStates; ++state) {
            if (probabilities[state] > best) {
                b.clear();
                best = probabilities[state];
                b.set(state, true);
            }
            else if (probabilities[state] == best) {
                b.set(state, true);
            }
        }
        if (best == 0.0) {
            return null;
        }
        try {
            return this.getData(b);
        }
        catch (UnknownDataException e) {
            return this.getUndeterminateData();
        }
    }

    public final int getRenderingSize() {
        switch (this) {
            case DNA: {
                return 1;
            }
            case STANDARD: {
                return 1;
            }
            case PROTEIN: {
                return 1;
            }
            case CODON: {
                return 3;
            }
            default: {
                return 1;
            }
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import metapiga.exceptions.NexusInconsistencyException;
import java.util.LinkedList;
import metapiga.exceptions.IncompatibleDataException;
import metapiga.exceptions.UnknownDataException;
import metapiga.modelization.data.DataType;
import java.util.LinkedHashMap;
import metapiga.parameters.Parameters;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import java.util.BitSet;
import metapiga.modelization.data.Data;
import java.util.List;
import java.util.Map;

public class CodonCharactersBlock
{
    final Map<String, List<Data>> matrix;
    private final BitSet gaps;
    private final BitSet ngaps;
    private final int fullNChar;
    private final int nucleotidesInCodon = 3;
    
    public CodonCharactersBlock(final CharactersBlock block, final Parameters.CodonDomainDefinition definitionInterval) throws UnknownDataException, NexusInconsistencyException, IncompatibleDataException {
        this.matrix = new LinkedHashMap<String, List<Data>>();
        DataType dataType = null;
        if (block.getDataType().toUpperCase().equals("NUCLEOTIDES")) {
            dataType = DataType.CODON;
        }
        else if (block.getDataType().toUpperCase().equals("CODONS")) {
            dataType = DataType.CODON;
        }
        else if (block.getDataType().toUpperCase().equals("DNA")) {
            dataType = DataType.CODON;
        }
        else {
            if (block.getDataType().toUpperCase().equals("RNA")) {
                throw new UnknownDataException("RNA");
            }
            dataType = DataType.valueOf(block.getDataType().toUpperCase());
        }
        if (dataType != DataType.CODON) {
            throw new IncompatibleDataException(DataType.DNA, dataType);
        }
        this.fullNChar = block.getDimensionsNChar();
        final String matchSymbol = (block.getMatchChar() == null) ? "." : block.getMatchChar();
        final String missingSymbol = (block.getMissing() == null) ? "_" : block.getMissing();
        final String gapSymbol = (block.getGap() == null) ? "-" : block.getGap();
        this.gaps = new BitSet();
        this.ngaps = new BitSet();
        String firstTaxon = null;
        for (final Object taxon : block.getMatrixLabels()) {
            final List<Data> seq = new LinkedList<Data>();
            if (firstTaxon == null) {
                firstTaxon = taxon.toString();
            }
            int positionCounter = 0;
            String codon = "";
            for (final Object obj : block.getMatrixData(taxon.toString())) {
                final String nucl = obj.toString();
                if (nucl.length() > 0) {
                    if (nucl.length() > 1) {
                        char[] charArray;
                        for (int length = (charArray = nucl.toCharArray()).length, i = 0; i < length; ++i) {
                            final char n = charArray[i];
                            if (++positionCounter >= definitionInterval.getStartCodonDomainPosition()) {
                                if (positionCounter > definitionInterval.getEndCodonDomainPosition()) {
                                    break;
                                }
                                codon = String.valueOf(codon) + n;
                                codon = this.isCodonComplete(dataType, seq, codon);
                            }
                        }
                    }
                    else {
                        if (++positionCounter < definitionInterval.getStartCodonDomainPosition()) {
                            continue;
                        }
                        if (positionCounter > definitionInterval.getEndCodonDomainPosition()) {
                            break;
                        }
                        Label_0730: {
                            if (nucl.equals(matchSymbol) || (block.isRespectCase() && nucl.equalsIgnoreCase(matchSymbol))) {
                                if (firstTaxon == null) {
                                    throw new NexusInconsistencyException("You cannot use MATCHCHAR symbol on first line !");
                                }
                                final Data fd = this.matrix.get(firstTaxon).get(seq.size());
                                final String firstTaxonCodon = fd.toString();
                                codon = String.valueOf(codon) + firstTaxonCodon.toCharArray()[codon.length()];
                                try {
                                    codon = this.isCodonComplete(dataType, seq, codon);
                                    break Label_0730;
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    throw new UnknownDataException(nucl, taxon.toString());
                                }
                            }
                            if (nucl.equals(missingSymbol) || (block.isRespectCase() && nucl.equalsIgnoreCase(missingSymbol))) {
                                codon = String.valueOf(codon) + "-";
                            }
                            else if (nucl.equals(gapSymbol) || (block.isRespectCase() && nucl.equalsIgnoreCase(gapSymbol))) {
                                codon = String.valueOf(codon) + "-";
                            }
                            else {
                                codon = String.valueOf(codon) + nucl;
                            }
                            try {
                                codon = this.isCodonComplete(dataType, seq, codon);
                            }
                            catch (UnknownDataException e2) {
                                e2.printStackTrace();
                                throw new UnknownDataException(nucl, taxon.toString());
                            }
                        }
                    }
                }
            }
            this.matrix.put(taxon.toString(), seq);
            if (this.matrix.get(firstTaxon).size() != seq.size()) {
                throw new NexusInconsistencyException("Line " + taxon + " has a size of " + seq.size() + ", and should have " + this.matrix.get(firstTaxon).size() + " as first line");
            }
        }
    }
    
    private String isCodonComplete(final DataType dataType, final List<Data> seq, final String codon) throws UnknownDataException {
        if (codon.length() == 3) {
            final Data d = this.completeCodon(codon, dataType);
            if (d.isUndeterminate()) {
                this.ngaps.set(seq.size());
                this.gaps.set(seq.size());
            }
            seq.add(d);
            return "";
        }
        return codon;
    }
    
    private Data completeCodon(final String codon, final DataType dataType) throws UnknownDataException {
        if (this.isCodonUndeterminate(codon)) {
            return dataType.getUndeterminateData();
        }
        return dataType.getData(codon.toUpperCase());
    }
    
    private boolean isCodonUndeterminate(final String codon) {
        return codon.lastIndexOf("-") != -1 || codon.lastIndexOf("R") != -1 || codon.lastIndexOf("Y") != -1 || codon.lastIndexOf("W") != -1 || codon.lastIndexOf("S") != -1 || codon.lastIndexOf("M") != -1 || codon.lastIndexOf("K") != -1 || codon.lastIndexOf("B") != -1 || codon.lastIndexOf("D") != -1 || codon.lastIndexOf("H") != -1 || codon.lastIndexOf("V") != -1 || codon.lastIndexOf("N") != -1;
    }
    
    public int getDimensionsNChar() {
        final Iterator<Map.Entry<String, List<Data>>> iterator = this.matrix.entrySet().iterator();
        if (iterator.hasNext()) {
            final Map.Entry<String, List<Data>> entry = iterator.next();
            final int numChars = this.matrix.get(entry.getKey()).size();
            return numChars;
        }
        return 0;
    }
    
    public Map<String, List<Data>> getDataMatrix() {
        return this.matrix;
    }
    
    public Collection<String> getMatrixLabels() {
        return Collections.unmodifiableSet(this.matrix.keySet());
    }
    
    public List<Data> getMatrixData(final String taxa) {
        return this.matrix.get(taxa);
    }
}

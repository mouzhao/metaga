// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data.codons.tables;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.ArrayList;
import metapiga.modelization.data.Codon;
import java.util.Set;

public class MoldProtoCoelMitochCode extends CodonTransitionTable
{
    protected Set<Codon> stopCodons;
    protected Set<Codon> phenylalanine;
    protected Set<Codon> leucine;
    protected Set<Codon> isoleucine;
    protected Set<Codon> methionine;
    protected Set<Codon> valine;
    protected Set<Codon> serine;
    protected Set<Codon> proline;
    protected Set<Codon> threonine;
    protected Set<Codon> alanine;
    protected Set<Codon> tyrosine;
    protected Set<Codon> histidine;
    protected Set<Codon> glutamine;
    protected Set<Codon> asparagine;
    protected Set<Codon> lysine;
    protected Set<Codon> asparticAcid;
    protected Set<Codon> glutamicAcid;
    protected Set<Codon> cysteine;
    protected Set<Codon> tryptophan;
    protected Set<Codon> arginine;
    protected Set<Codon> glycine;
    protected Set<Codon> opal;
    protected Set<Codon> ochre;
    protected Set<Codon> amber;
    protected ArrayList<Set<Codon>> aminoacids;
    
    public MoldProtoCoelMitochCode() {
        this.stopCodons = EnumSet.of(Codon.TAA, Codon.TAG);
        this.aminoacids = new ArrayList<Set<Codon>>();
        this.phenylalanine = EnumSet.of(Codon.TTT, Codon.TTC);
        (this.leucine = EnumSet.of(Codon.CTT, Codon.CTC, Codon.CTA, Codon.CTG)).add(Codon.TTA);
        this.leucine.add(Codon.TTG);
        this.isoleucine = EnumSet.of(Codon.ATT, Codon.ATC, Codon.ATA);
        this.methionine = EnumSet.of(Codon.ATG);
        this.valine = EnumSet.of(Codon.GTT, Codon.GTC, Codon.GTA, Codon.GTG);
        this.serine = EnumSet.of(Codon.TCT, Codon.TCC, Codon.TCA, Codon.TCG);
        this.proline = EnumSet.of(Codon.CCT, Codon.CCA, Codon.CCC, Codon.CCG);
        this.threonine = EnumSet.of(Codon.ACT, Codon.ACC, Codon.ACA, Codon.ACG);
        this.alanine = EnumSet.of(Codon.GCT, Codon.GCC, Codon.GCA, Codon.GCG);
        this.tyrosine = EnumSet.of(Codon.TAT, Codon.TAC);
        this.ochre = EnumSet.of(Codon.TAA);
        this.amber = EnumSet.of(Codon.TAG);
        this.histidine = EnumSet.of(Codon.CAT, Codon.CAC);
        this.glutamine = EnumSet.of(Codon.CAA, Codon.CAG);
        this.asparagine = EnumSet.of(Codon.AAT, Codon.AAC);
        this.lysine = EnumSet.of(Codon.AAA, Codon.AAG);
        this.asparticAcid = EnumSet.of(Codon.GAT, Codon.GAC);
        this.glutamicAcid = EnumSet.of(Codon.GAA, Codon.GAG);
        this.cysteine = EnumSet.of(Codon.TGT, Codon.TGC);
        this.tryptophan = EnumSet.of(Codon.TGG, Codon.TGA);
        this.arginine = EnumSet.of(Codon.CGT, Codon.CGC, Codon.CGA, Codon.CGG);
        this.serine.add(Codon.AGT);
        this.serine.add(Codon.AGC);
        this.arginine.add(Codon.AGA);
        this.arginine.add(Codon.AGG);
        this.glycine = EnumSet.of(Codon.GGT, Codon.GGC, Codon.GGA, Codon.GGG);
        this.aminoacids.add(this.alanine);
        this.aminoacids.add(this.arginine);
        this.aminoacids.add(this.asparagine);
        this.aminoacids.add(this.cysteine);
        this.aminoacids.add(this.glutamine);
        this.aminoacids.add(this.glycine);
        this.aminoacids.add(this.histidine);
        this.aminoacids.add(this.isoleucine);
        this.aminoacids.add(this.leucine);
        this.aminoacids.add(this.lysine);
        this.aminoacids.add(this.methionine);
        this.aminoacids.add(this.phenylalanine);
        this.aminoacids.add(this.proline);
        this.aminoacids.add(this.serine);
        this.aminoacids.add(this.threonine);
        this.aminoacids.add(this.tyrosine);
        this.aminoacids.add(this.valine);
        this.aminoacids.add(this.asparticAcid);
        this.aminoacids.add(this.glutamicAcid);
        this.aminoacids.add(this.tryptophan);
    }
    
    @Override
    public boolean isSynonymous(final Codon fromCodon, final Codon toCodon) {
        for (final Set<Codon> aminoacid : this.aminoacids) {
            if (aminoacid.contains(fromCodon) && aminoacid.contains(toCodon)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isStopCodon(final Codon codon) {
        return this.stopCodons.contains(codon);
    }
}

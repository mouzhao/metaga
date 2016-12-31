// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data;

import org.junit.Test;
import org.junit.Assert;
import metapiga.modelization.data.codons.tables.UniversalCodonTransitionTable;

public class UniversalCodonTransitionTableTest
{
    private UniversalCodonTransitionTable codonTable;
    
    public UniversalCodonTransitionTableTest() {
        this.codonTable = new UniversalCodonTransitionTable();
    }
    
    @Test
    public void testIsStopCodonTest() {
        Assert.assertTrue(this.codonTable.isStopCodon(Codon.TGA) || this.codonTable.isStopCodon(Codon.TAA) || this.codonTable.isStopCodon(Codon.TAG));
    }
    
    @Test
    public void testIsNotStopCodonTest() {
        Assert.assertFalse(this.codonTable.isStopCodon(Codon.AAA) || this.codonTable.isStopCodon(Codon.GAT) || this.codonTable.isStopCodon(Codon.__X) || this.codonTable.isStopCodon(Codon.AGA));
    }
    
    @Test
    public void testIsSynonimousTest() {
        Assert.assertTrue(this.codonTable.isSynonymous(Codon.CTC, Codon.CTG) || this.codonTable.isSynonymous(Codon.AGT, Codon.AGC) || this.codonTable.isSynonymous(Codon.GGT, Codon.GGG) || this.codonTable.isSynonymous(Codon.TTG, Codon.CTT));
    }
    
    @Test
    public void testIsNotSynonimousTest() {
        Assert.assertFalse(this.codonTable.isSynonymous(Codon.TTC, Codon.GGT) || this.codonTable.isSynonymous(Codon.CCG, Codon.ACT) || this.codonTable.isSynonymous(Codon.TAA, Codon.TAG) || this.codonTable.isSynonymous(Codon.__X, Codon.CGC) || this.codonTable.isSynonymous(Codon.ATA, Codon.ATG));
    }
    
    @Test
    public void isDifferentMoreThanOneNucleotideTest() {
        Assert.assertTrue(this.codonTable.isDifferentMoreThanOneNucleotide(Codon.AAA, Codon.TAC) || this.codonTable.isDifferentMoreThanOneNucleotide(Codon.CAG, Codon.AGC) || this.codonTable.isDifferentMoreThanOneNucleotide(Codon.TGA, Codon.ATG) || this.codonTable.isDifferentMoreThanOneNucleotide(Codon.__X, Codon.TAC));
    }
    
    @Test
    public void isNotDifferentMoreThanOneNucleotideTest() {
        Assert.assertFalse(this.codonTable.isDifferentMoreThanOneNucleotide(Codon.AAA, Codon.TAA) || this.codonTable.isDifferentMoreThanOneNucleotide(Codon.TAA, Codon.TCA) || this.codonTable.isDifferentMoreThanOneNucleotide(Codon.TCA, Codon.TCC) || this.codonTable.isDifferentMoreThanOneNucleotide(Codon.TCC, Codon.ACC));
    }
    
    @Test
    public void isTransitionTest() {
        Assert.assertTrue(this.codonTable.isTransition(Codon.AAA, Codon.GAA) || this.codonTable.isTransition(Codon.GAA, Codon.GGA) || this.codonTable.isTransition(Codon.GGA, Codon.AGA) || this.codonTable.isTransition(Codon.ACC, Codon.ACT));
    }
    
    @Test
    public void isNotTransitionTest() {
        Assert.assertFalse(this.codonTable.isTransition(Codon.AAA, Codon.CAA) || this.codonTable.isTransition(Codon.GTA, Codon.GGA) || this.codonTable.isTransition(Codon.GGA, Codon.CGA) || this.codonTable.isTransition(Codon.ACG, Codon.ACT) || this.codonTable.isTransition(Codon.ACG, Codon.__X) || this.codonTable.isTransition(Codon.ACG, Codon.AAA) || this.codonTable.isTransition(Codon.AAA, Codon.AAA));
    }
    
    @Test
    public void isTransversionTest() {
        Assert.assertTrue(this.codonTable.isTransversion(Codon.AAA, Codon.CAA) || this.codonTable.isTransversion(Codon.GAA, Codon.GTA) || this.codonTable.isTransversion(Codon.GGA, Codon.CGA) || this.codonTable.isTransversion(Codon.ACC, Codon.ACA));
    }
    
    @Test
    public void isNotTransversionTest() {
        Assert.assertFalse(this.codonTable.isTransversion(Codon.AAA, Codon.GAA) || this.codonTable.isTransversion(Codon.GTA, Codon.GCA) || this.codonTable.isTransversion(Codon.GGA, Codon.AGA) || this.codonTable.isTransversion(Codon.ACG, Codon.ACA) || this.codonTable.isTransversion(Codon.ACG, Codon.__X) || this.codonTable.isTransition(Codon.ACG, Codon.AAA) || this.codonTable.isTransition(Codon.AAA, Codon.AAA));
    }
}

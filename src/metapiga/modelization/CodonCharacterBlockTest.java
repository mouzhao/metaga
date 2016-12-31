// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization;

import org.junit.Test;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import java.util.List;
import metapiga.modelization.data.Codon;
import metapiga.modelization.data.Data;
import metapiga.parameters.Parameters;
import org.mockito.Matchers;
import java.util.ArrayList;
import org.mockito.Mockito;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;

public class CodonCharacterBlockTest
{
    @Test
    public void codonCharacterBlockTestNormal() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(10);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("A");
        seqList.add("C");
        seqList.add("A");
        seqList.add("T");
        seqList.add("A");
        seqList.add("G");
        seqList.add("T");
        seqList.add("A");
        seqList.add("C");
        seqList.add("C");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData(Matchers.anyString())).thenReturn(seqList);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(0);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(10);
        final Data[] expecteds = { Codon.ACA, Codon.TAG, Codon.TAC };
        final Data[] actuals = new Data[3];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            final Iterator<String> iterator = m.keySet().iterator();
            if (iterator.hasNext()) {
                final String s = iterator.next();
                m.get(s).toArray(actuals);
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
    
    @Test
    public void codonCharacterBlockTestSmallerDatasetThatDomain() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(15);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("A");
        seqList.add("C");
        seqList.add("A");
        seqList.add("T");
        seqList.add("A");
        seqList.add("G");
        seqList.add("T");
        seqList.add("A");
        seqList.add("C");
        seqList.add("C");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData(Matchers.anyString())).thenReturn(seqList);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(0);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(10);
        final Data[] expecteds = { Codon.ACA, Codon.TAG, Codon.TAC };
        final Data[] actuals = new Data[3];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            final Iterator<String> iterator = m.keySet().iterator();
            if (iterator.hasNext()) {
                final String s = iterator.next();
                m.get(s).toArray(actuals);
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
    
    @Test
    public void codonCharacterBlockTestDataClump() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(10);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("AC");
        seqList.add("A");
        seqList.add("T");
        seqList.add("AG");
        seqList.add("T");
        seqList.add("ACT");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData(Matchers.anyString())).thenReturn(seqList);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(0);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(10);
        final Data[] expecteds = { Codon.ACA, Codon.TAG, Codon.TAC };
        final Data[] actuals = new Data[3];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            final Iterator<String> iterator = m.keySet().iterator();
            if (iterator.hasNext()) {
                final String s = iterator.next();
                m.get(s).toArray(actuals);
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
    
    @Test
    public void codonCharacterBlockTestDash() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(10);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("A-");
        seqList.add("A");
        seqList.add("-");
        seqList.add("AG");
        seqList.add("T");
        seqList.add("ACT");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData(Matchers.anyString())).thenReturn(seqList);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(0);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(10);
        final Data[] expecteds = { Codon.__X, Codon.__X, Codon.TAC };
        final Data[] actuals = new Data[3];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            final Iterator<String> iterator = m.keySet().iterator();
            if (iterator.hasNext()) {
                final String s = iterator.next();
                m.get(s).toArray(actuals);
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
    
    @Test
    public void codonCharacterBlockTestScope() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(10);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("AG");
        seqList.add("A");
        seqList.add("TGC");
        seqList.add("AG");
        seqList.add("T");
        seqList.add("ATACC");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData(Matchers.anyString())).thenReturn(seqList);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(2);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(17);
        final Data[] expecteds = { Codon.GAT, Codon.GCA, Codon.GTA, Codon.TAC };
        final Data[] actuals = new Data[4];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            final Iterator<String> iterator = m.keySet().iterator();
            if (iterator.hasNext()) {
                final String s = iterator.next();
                m.get(s).toArray(actuals);
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
    
    @Test
    public void codonCharacterBlockTestMatchSign() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(10);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("AG");
        seqList.add("A");
        seqList.add("TGC");
        seqList.add("AG");
        seqList.add("T");
        seqList.add("ATACC");
        final ArrayList<String> seqList2 = new ArrayList<String>();
        seqList2.add("A.");
        seqList2.add("A");
        seqList2.add(".GC");
        seqList2.add("A.");
        seqList2.add(".");
        seqList2.add("ATACC");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax1")).thenReturn(seqList);
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax2")).thenReturn(seqList2);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(2);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(17);
        final Data[] expecteds = { Codon.GAT, Codon.GCA, Codon.GTA, Codon.TAC };
        final Data[] actuals = new Data[4];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            final Iterator<String> iterator = m.keySet().iterator();
            if (iterator.hasNext()) {
                final String s = iterator.next();
                m.get(s).toArray(actuals);
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
    
    @Test
    public void codonCharacterBlockTestStopCodons() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(10);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        taxList.add("tax3");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("TGA");
        seqList.add("ACT");
        seqList.add("AAC");
        seqList.add("A");
        seqList.add("C");
        seqList.add("C");
        seqList.add("C");
        seqList.add("G");
        seqList.add("A");
        final ArrayList<String> seqList2 = new ArrayList<String>();
        seqList2.add("C");
        seqList2.add("A");
        seqList2.add("A");
        seqList2.add("TCC");
        seqList2.add("TAA");
        seqList2.add("GCA");
        seqList2.add("C");
        seqList2.add("A");
        seqList2.add("G");
        final ArrayList<String> seqList3 = new ArrayList<String>();
        seqList3.add("C");
        seqList3.add("C");
        seqList3.add("C");
        seqList3.add("A");
        seqList3.add("A");
        seqList3.add("A");
        seqList3.add("GCA");
        seqList3.add("TAC");
        seqList3.add("TAG");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax1")).thenReturn(seqList);
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax2")).thenReturn(seqList2);
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax3")).thenReturn(seqList3);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(2);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(17);
        final Data[] expecteds = { Codon.ACT, Codon.ACC };
        final Data[] actuals = new Data[5];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            for (final String s : m.keySet()) {
                if (s.equalsIgnoreCase("tax1")) {
                    m.get(s).toArray(actuals);
                    break;
                }
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
    
    @Test
    public void codonCharacterBlockTestAmbiguous() {
        final CharactersBlock mockBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockBlock.getDataType()).thenReturn("DNA");
        Mockito.when(mockBlock.getDimensionsNChar()).thenReturn(10);
        final ArrayList<String> taxList = new ArrayList<String>();
        taxList.add("tax1");
        taxList.add("tax2");
        taxList.add("tax3");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixLabels()).thenReturn(taxList);
        final ArrayList<String> seqList = new ArrayList<String>();
        seqList.add("TNA");
        seqList.add("ACT");
        seqList.add("AAC");
        seqList.add("A");
        seqList.add("C");
        seqList.add("C");
        seqList.add("C");
        seqList.add("G");
        seqList.add("A");
        final ArrayList<String> seqList2 = new ArrayList<String>();
        seqList2.add("C");
        seqList2.add("A");
        seqList2.add("A");
        seqList2.add("TCC");
        seqList2.add("MAA");
        seqList2.add("GCA");
        seqList2.add("C");
        seqList2.add("A");
        seqList2.add("G");
        final ArrayList<String> seqList3 = new ArrayList<String>();
        seqList3.add("C");
        seqList3.add("C");
        seqList3.add("C");
        seqList3.add("A");
        seqList3.add("A");
        seqList3.add("A");
        seqList3.add("GCA");
        seqList3.add("TAC");
        seqList3.add("TA-");
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax1")).thenReturn(seqList);
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax2")).thenReturn(seqList2);
        Mockito.when((ArrayList<String>)mockBlock.getMatrixData("tax3")).thenReturn(seqList3);
        Mockito.when(mockBlock.isRespectCase()).thenReturn(false);
        final Parameters.CodonDomainDefinition domain = Mockito.mock(Parameters.CodonDomainDefinition.class);
        Mockito.when(domain.getStartCodonDomainPosition()).thenReturn(2);
        Mockito.when(domain.getEndCodonDomainPosition()).thenReturn(17);
        final Data[] expecteds = { Codon.ACT, Codon.ACC };
        final Data[] actuals = new Data[5];
        try {
            final CodonCharactersBlock cb = new CodonCharactersBlock(mockBlock, domain);
            final Map<String, List<Data>> m = cb.getDataMatrix();
            for (final String s : m.keySet()) {
                if (s.equalsIgnoreCase("tax1")) {
                    m.get(s).toArray(actuals);
                    break;
                }
            }
            Assert.assertEquals(expecteds, actuals);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("exception thrown @ CodonCharacterBlock invocation" + e.getMessage());
        }
    }
}

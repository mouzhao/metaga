// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization;

import org.junit.Test;
import org.mockito.Mockito;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import metapiga.parameters.Parameters;

public class CodonDomainTest
{
    @Test
    public void negaiveValueTest() {
        final Parameters testParam = new Parameters("TestParam");
        final CharactersBlock mockCharBlock = Mockito.mock(CharactersBlock.class);
        Mockito.when(mockCharBlock.getDimensionsNChar()).thenReturn(10);
        testParam.charactersBlock = mockCharBlock;
    }
}

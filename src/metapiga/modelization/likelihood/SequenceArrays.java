// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.likelihood;

public interface SequenceArrays
{
    void setElement(final float p0, final int p1, final int p2, final int p3, final int p4);
    
    float getElement(final int p0, final int p1, final int p2, final int p3);
    
    SequenceArrays clone();
    
    void clone(final SequenceArrays p0);
    
    int getNodeCount();
    
    int getCategoryCount();
    
    int getCharacterCountNoPadding();
    
    int getStateCount();
}

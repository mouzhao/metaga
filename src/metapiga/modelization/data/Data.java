// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.data;

import java.awt.Color;
import java.util.BitSet;

public interface Data
{
    char toChar();
    
    BitSet toBits();
    
    int numOfStates();
    
    int getMaxStates();
    
    int getState();
    
    Color getColor();
    
    boolean isState(final int p0);
    
    boolean isUndeterminate();
}

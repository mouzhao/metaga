// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

public interface BatchBlockListener extends NexusBlockListener
{
    void addLabel(final String p0);
    
    void addData(final String p0, final String p1);
    
    void addParam(final String p0, final String p1);
    
    void addTree(final String p0, final String p1);
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

public class MyNexusFileBuilder extends NexusFileBuilder
{
    @Override
    public void setDefaultBlockParsers() {
        super.setDefaultBlockParsers();
        this.setBlockParser("METAPIGA", new MetapigaBlockParser(new MetapigaBlockBuilder()));
        this.setBlockParser("BATCH", new BatchBlockParser(new BatchBlockBuilder()));
    }
}

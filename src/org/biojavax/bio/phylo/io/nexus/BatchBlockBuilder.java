// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

public class BatchBlockBuilder extends NexusBlockBuilder.Abstract implements BatchBlockListener
{
    private BatchBlock block;
    
    @Override
    protected void addComment(final NexusComment comment) {
        this.block.addComment(comment);
    }
    
    protected BatchBlock makeNewBlock() {
        return new BatchBlock();
    }
    
    @Override
    protected NexusBlock startBlockObject() {
        this.block = this.makeNewBlock();
        this.resetStatus();
        return this.block;
    }
    
    protected void resetStatus() {
    }
    
    @Override
    public void endBlock() {
    }
    
    @Override
    public void endTokenGroup() {
    }
    
    @Override
    public void addLabel(final String runLabel) {
        this.block.addLabel(runLabel);
    }
    
    @Override
    public void addData(final String runLabel, final String dataBlockLabel) {
        this.block.addData(runLabel, dataBlockLabel);
    }
    
    @Override
    public void addParam(final String runLabel, final String metapigaBlockLabel) {
        this.block.addParam(runLabel, metapigaBlockLabel);
    }
    
    @Override
    public void addTree(final String runLabel, final String treeBlockLabel) {
        this.block.addTree(runLabel, treeBlockLabel);
    }
}

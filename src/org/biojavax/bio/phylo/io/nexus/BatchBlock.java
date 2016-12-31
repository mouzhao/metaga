// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

import java.io.IOException;
import java.util.Iterator;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class BatchBlock extends NexusBlock.Abstract
{
    public static final String BATCH_BLOCK = "BATCH";
    private List<String> runLabels;
    private Map<String, String> runData;
    private Map<String, String> runParam;
    private Map<String, String> runTrees;
    private List<NexusComment> comments;
    
    public BatchBlock() {
        super("BATCH");
        this.runLabels = new ArrayList<String>();
        this.runData = new HashMap<String, String>();
        this.runParam = new HashMap<String, String>();
        this.runTrees = new HashMap<String, String>();
        this.comments = new ArrayList<NexusComment>();
    }
    
    public void addLabel(final String runLabel) {
        this.runLabels.add(runLabel);
    }
    
    public List<String> getRunLabels() {
        return new ArrayList<String>(this.runLabels);
    }
    
    public void addData(final String runLabel, final String dataBlockLabel) {
        this.runData.put(runLabel, dataBlockLabel);
    }
    
    public Map<String, String> getRunData() {
        return new HashMap<String, String>(this.runData);
    }
    
    public void addParam(final String runLabel, final String metapigaBlockLabel) {
        this.runParam.put(runLabel, metapigaBlockLabel);
    }
    
    public Map<String, String> getRunParam() {
        return new HashMap<String, String>(this.runParam);
    }
    
    public void addTree(final String runLabel, final String treeBlockLabel) {
        this.runTrees.put(runLabel, treeBlockLabel);
    }
    
    public Map<String, String> getRunTrees() {
        return new HashMap<String, String>(this.runTrees);
    }
    
    public void addComment(final NexusComment comment) {
        this.comments.add(comment);
    }
    
    public void removeComment(final NexusComment comment) {
        this.comments.remove(comment);
    }
    
    public List<NexusComment> getComments() {
        return this.comments;
    }
    
    @Override
    protected void writeBlockContents(final Writer fw) throws IOException {
        final String endl = NexusFileFormat.NEW_LINE;
        final Iterator<NexusComment> i = this.comments.iterator();
        while (i.hasNext()) {
            i.next().writeObject(fw);
            fw.write(endl);
        }
        for (final String run : this.runLabels) {
            fw.write("RUN LABEL='" + run + "' DATA=" + this.runData.get(run).replace(' ', '_') + " PARAM=" + this.runParam.get(run).replace(' ', '_'));
            if (this.runTrees.containsKey(run)) {
                fw.write(" TREES=" + this.runTrees.get(run).replace(' ', '_'));
            }
            fw.write(";" + endl);
        }
    }
}

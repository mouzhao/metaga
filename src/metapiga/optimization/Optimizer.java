// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.optimization;

import javax.swing.JDialog;
import metapiga.trees.exceptions.UnrootableTreeException;
import metapiga.trees.exceptions.NullAncestorException;
import metapiga.trees.Tree;

public interface Optimizer
{
    Tree getOptimizedTree() throws NullAncestorException, UnrootableTreeException;
    
    Tree getOptimizedTreeWithProgress(final JDialog p0, final String p1, final int p2, final int p3) throws NullAncestorException, UnrootableTreeException;
    
    Tree getOptimizedTreeWithProgress(final JDialog p0, final String p1) throws NullAncestorException, UnrootableTreeException;
    
    void stop();
}

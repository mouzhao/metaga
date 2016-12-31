// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.io;

import org.biojavax.bio.phylo.io.nexus.DataBlock;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import metapiga.trees.Tree;
import org.biojavax.bio.phylo.io.nexus.NexusComment;

import java.io.FileWriter;
import java.util.Map;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import org.biojavax.bio.phylo.io.nexus.MetapigaBlock;
import java.util.HashMap;
import org.biojavax.bio.phylo.io.nexus.BatchBlock;
import java.io.IOException;
import java.io.File;
import javax.swing.ListModel;
import java.util.ArrayList;
import org.biojavax.bio.phylo.io.nexus.NexusFileFormat;
import metapiga.parameters.Parameters;
import metapiga.WaitingLogo;
import java.util.List;

import javax.swing.SwingWorker;

public class NexusWriter extends SwingWorker<WaitingLogo.Status, Object>
{
    private String endl;
    String filename;
    List<Parameters> P;
    boolean saveModified;
    
    public NexusWriter(final String filename, final Parameters parameters) {
        this.endl = NexusFileFormat.NEW_LINE;
        this.saveModified = false;
        this.filename = filename;
        (this.P = new ArrayList<Parameters>()).add(parameters);
    }
    
    public NexusWriter(final String filename, final Parameters parameters, final boolean saveModified) {
        this.endl = NexusFileFormat.NEW_LINE;
        this.saveModified = false;
        this.filename = filename;
        this.saveModified = saveModified;
        (this.P = new ArrayList<Parameters>()).add(parameters);
    }
    
    public NexusWriter(final String filename, final ListModel parameters) {
        this.endl = NexusFileFormat.NEW_LINE;
        this.saveModified = false;
        this.filename = filename;
        this.P = new ArrayList<Parameters>();
        for (int i = 0; i < parameters.getSize(); ++i) {
            this.P.add((Parameters) parameters.getElementAt(i));
        }
    }
    
    public WaitingLogo.Status doInBackground() {
        File tempOutput = null;
        try {
            tempOutput = File.createTempFile("nexus", ".temp");
            tempOutput.deleteOnExit();
        }
        catch (IOException ex1) {
            System.err.println("Cannot create temporary file");
            ex1.printStackTrace();
        }
        if (this.P.size() > 1) {
            final BatchBlock batchBlock = new BatchBlock();
            final Map<String, MetapigaBlock> metapigaBlocks = new HashMap<String, MetapigaBlock>();
            final Map<String, CharactersBlock> dataBlocks = new HashMap<String, CharactersBlock>();
            final Map<String, Parameters> treesBlocks = new HashMap<String, Parameters>();
            int mc = 1;
            int dc = 1;
            int tc = 1;
            for (final Parameters p : this.P) {
                batchBlock.addLabel(p.label);
                final MetapigaBlock mb = p.getMetapigaBlock();
                String key = "param_" + mc;
                ++mc;
                metapigaBlocks.put(key, mb);
                batchBlock.addParam(p.label, key);
                if (p.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
                    key = "trees_" + tc;
                    ++tc;
                    treesBlocks.put(key, p);
                    batchBlock.addTree(p.label, key);
                }
                final CharactersBlock cb = p.charactersBlock;
                key = null;
                for (final Map.Entry<String, CharactersBlock> E : dataBlocks.entrySet()) {
                    if (cb == E.getValue()) {
                        key = E.getKey();
                    }
                }
                if (key == null) {
                    key = "data_" + dc;
                    ++dc;
                    dataBlocks.put(key, cb);
                }
                batchBlock.addData(p.label, key);
            }
            try {
                final FileWriter fw = new FileWriter(tempOutput);
                fw.write("#NEXUS" + this.endl);
                fw.write("[Metapiga 2 - LANE (Laboratory of Artificial and Natural Evolution, University of Geneva)]" + this.endl);
                fw.write(this.endl);
                batchBlock.writeObject(fw);
                fw.write(this.endl);
                for (final Map.Entry<String, MetapigaBlock> E2 : metapigaBlocks.entrySet()) {
                    final NexusComment comment = new NexusComment();
                    comment.addCommentText("BATCHLABEL=" + E2.getKey().replace(' ', '_'));
                    E2.getValue().addComment(comment);
                    E2.getValue().writeObject(fw);
                    fw.write(this.endl);
                }
                for (final Map.Entry<String, CharactersBlock> E3 : dataBlocks.entrySet()) {
                    final Iterator<NexusComment> com = E3.getValue().getComments().iterator();
                    while (com.hasNext()) {
                        boolean remove = false;
                        final Iterator<String> subCom = (Iterator<String>)com.next().commentIterator();
                        while (subCom.hasNext()) {
                            final String s = subCom.next();
                            if (s.toUpperCase().contains("BATCHLABEL")) {
                                remove = true;
                                break;
                            }
                        }
                        if (remove) {
                            com.remove();
                        }
                    }
                    final NexusComment comment = new NexusComment();
                    comment.addCommentText("BATCHLABEL=" + E3.getKey().replace(' ', '_'));
                    E3.getValue().addComment(comment);
                    E3.getValue().writeObject(fw);
                    fw.write(this.endl);
                }
                for (final Map.Entry<String, Parameters> E4 : treesBlocks.entrySet()) {
                    fw.write("BEGIN TREES;" + this.endl);
                    fw.write("[BATCHLABEL=" + E4.getKey().replace(' ', '_') + "]" + this.endl);
                    for (final Tree tree : E4.getValue().startingTrees) {
                        fw.write(String.valueOf(tree.toNewickLine(false, false)) + this.endl);
                    }
                    fw.write("END;" + this.endl);
                }
                fw.close();
            }
            catch (Exception e) {
                System.err.println("Can't retrieve source nexus file information");
                e.printStackTrace();
            }
        }
        else {
            final Parameters p2 = this.P.get(0);
            if (p2.nexusFile != null && !this.saveModified) {
                try {
                    final FileReader fr = new FileReader(p2.nexusFile);
                    final FileWriter fw2 = new FileWriter(tempOutput);
                    final BufferedReader br = new BufferedReader(fr);
                    fw2.write("#NEXUS" + this.endl);
                    fw2.write("[Metapiga 2 - LANE (Laboratory of Artificial and Natural Evolution, University of Geneva)]" + this.endl);
                    fw2.write("[You can paste this METAPIGA block in any Nexus file to use it in metapiga with this parameters]" + this.endl + this.endl);
                    p2.getMetapigaBlock().writeObject(fw2);
                    fw2.write(this.endl);
                    if (p2.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
                        p2.writeTreeBlock(fw2);
                    }
                    fw2.write(this.endl);
                    String line;
                    while ((line = br.readLine()) != null) {
                        while (line != null && !line.toUpperCase().startsWith("BEGIN METAPIGA") && (!line.toUpperCase().startsWith("BEGIN TREES") || p2.startingTreeGeneration != Parameters.StartingTreeGeneration.GIVEN)) {
                            if (!line.startsWith("[Metapiga 2 - LANE (Laboratory of Artificial and Natural Evolution, University of Geneva)]") && !line.startsWith("[You can paste this METAPIGA block in any Nexus file to use it in metapiga with this parameters]") && !line.toUpperCase().startsWith("#NEXUS") && line.length() != 0) {
                                if (line.startsWith("END")) {
                                    fw2.write(String.valueOf(line) + this.endl + this.endl);
                                }
                                else {
                                    fw2.write(line);
                                }
                                fw2.write(this.endl);
                            }
                            line = br.readLine();
                        }
                        if (line != null) {
                            while (!line.toUpperCase().startsWith("END")) {
                                line = br.readLine();
                            }
                        }
                    }
                    br.close();
                    fr.close();
                    fw2.close();
                }
                catch (Exception e2) {
                    System.err.println("Can't retrieve source nexus file information");
                    e2.printStackTrace();
                }
            }
            else {
                try {
                    final FileWriter fw3 = new FileWriter(tempOutput);
                    fw3.write("#NEXUS" + this.endl);
                    fw3.write("[Metapiga 2 - LANE (Laboratory of Artificial and Natural Evolution, University of Geneva)]" + this.endl);
                    fw3.write("[You can paste this METAPIGA block in any Nexus file to use it in metapiga with this parameters]" + this.endl + this.endl);
                    if (this.saveModified) {
                        final Parameters newP = p2.duplicate();
                        newP.deletedTaxa.clear();
                        newP.charsets.clearAll();
                        newP.getMetapigaBlock().writeObject(fw3);
                        fw3.write(this.endl);
                        final DataBlock dataBlock = p2.getModifiedDataBlock();
                        dataBlock.writeObject(fw3);
                    }
                    else {
                        p2.getMetapigaBlock().writeObject(fw3);
                        fw3.write(this.endl);
                        p2.charactersBlock.writeObject(fw3);
                    }
                    fw3.write(this.endl);
                    if (p2.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
                        p2.writeTreeBlock(fw3);
                    }
                    fw3.close();
                }
                catch (Exception e2) {
                    System.err.println("Can't retrieve source nexus file information");
                    e2.printStackTrace();
                }
            }
        }
        File nexusOutput;
        if (this.filename == null) {
            nexusOutput = new File("default.nex");
        }
        else {
            nexusOutput = new File(this.filename);
        }
        try {
            nexusOutput.delete();
            FileUtils.moveFile(tempOutput, nexusOutput);
            if (this.P.size() == 1 && this.P.get(0).nexusFile != null) {
                this.P.get(0).nexusFile = new File(nexusOutput.getPath());
            }
            return WaitingLogo.Status.NEXUS_FILE_SAVED;
        }
        catch (Exception ex2) {
            final WaitingLogo.Status nexus_FILE_NOT_SAVED;
            final WaitingLogo.Status error = nexus_FILE_NOT_SAVED = WaitingLogo.Status.NEXUS_FILE_NOT_SAVED;
            nexus_FILE_NOT_SAVED.text = String.valueOf(nexus_FILE_NOT_SAVED.text) + this.endl + ex2.getMessage();
            return error;
        }
    }
    
    public void done() {

    }
}

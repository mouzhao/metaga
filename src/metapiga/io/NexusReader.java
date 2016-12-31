// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.io;

import org.biojavax.bio.phylo.io.nexus.NexusComment;
import java.util.List;
import java.util.HashMap;
import metapiga.exceptions.IncompatibleDataException;
import metapiga.exceptions.CharsetIntersectionException;
import metapiga.exceptions.UnknownDataException;
import metapiga.exceptions.NexusInconsistencyException;
import metapiga.trees.Tree;
import org.biojavax.bio.phylo.io.nexus.DataBlock;
import metapiga.modelization.data.DataType;
import org.biojavax.bio.phylo.io.nexus.NexusFile;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import metapiga.utilities.Tools;
import org.biojavax.bio.phylo.io.nexus.NexusBlock;
import org.biojavax.bio.phylo.io.nexus.NexusFileFormat;
import org.biojavax.bio.phylo.io.nexus.MyNexusFileBuilder;
import org.biojavax.bio.phylo.io.nexus.TreesBlock;
import org.biojavax.bio.phylo.io.nexus.MetapigaBlock;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import java.util.Map;
import org.biojavax.bio.phylo.io.nexus.BatchBlock;
import javax.swing.DefaultListModel;
import metapiga.parameters.Parameters;
import java.io.InputStream;
import java.io.File;
import metapiga.MetaPIGA;
import metapiga.WaitingLogo;

import javax.swing.SwingWorker;

public class NexusReader extends SwingWorker<WaitingLogo.Status, Object>
{
    private MetaPIGA metapiga;
    File nexus;
    private InputStream is;
    private boolean isBatch;
    private Parameters parameters;
    private DefaultListModel parametersList;
    private BatchBlock batchBlock;
    private Map<String, CharactersBlock> dataBlocks;
    private Map<String, MetapigaBlock> metapigaBlocks;
    private Map<String, TreesBlock> treesBlocks;
    
    public NexusReader(final File nexusFile, final MetaPIGA metapiga) {
        this.nexus = null;
        this.is = null;
        this.metapiga = metapiga;
        this.parametersList = metapiga.parameters;
        this.nexus = nexusFile;
    }
    
    public NexusReader(final InputStream nexusInputStream, final MetaPIGA metapiga) {
        this.nexus = null;
        this.is = null;
        this.metapiga = metapiga;
        this.parametersList = metapiga.parameters;
        this.is = nexusInputStream;
    }
    
    public NexusReader(final DefaultListModel listOfParams, final File nexusFile) {
        this.nexus = null;
        this.is = null;
        this.metapiga = null;
        this.parametersList = listOfParams;
        this.nexus = nexusFile;
    }
    
    public boolean isBatch() {
        return this.isBatch;
    }
    
    public WaitingLogo.Status doInBackground() {
        try {
            this.checkNexusFile();
            final MyNexusFileBuilder builder = new MyNexusFileBuilder();
            if (this.nexus != null) {
                NexusFileFormat.parseFile(builder, this.nexus);
                String name = "";
                final String[] split = this.nexus.getName().split("\\.");
                for (int i = 0; i < split.length - 1; ++i) {
                    name = String.valueOf(name) + split[i];
                    if (i + 1 < split.length - 1) {
                        name = String.valueOf(name) + ".";
                    }
                }
                this.parameters = new Parameters(name);
            }
            else {
                NexusFileFormat.parseInputStream(builder, this.is);
                this.parameters = new Parameters("default");
            }
            this.isBatch = false;
            final Iterator it = builder.getNexusFile().blockIterator();
            while (it.hasNext()) {
                final NexusBlock block = (NexusBlock) it.next();
                if (block.getBlockName().equals("BATCH")) {
                    this.isBatch = true;
                    this.batchBlock = (BatchBlock)block;
                }
            }
            if (this.isBatch) {
                return this.batchRunExtraction(builder.getNexusFile());
            }
            return this.singleRunExtraction(builder.getNexusFile());
        }
        catch (OutOfMemoryError e2) {
            final WaitingLogo.Status data_FILE_NOT_LOADED;
            final WaitingLogo.Status error = data_FILE_NOT_LOADED = WaitingLogo.Status.DATA_FILE_NOT_LOADED;
            data_FILE_NOT_LOADED.text = String.valueOf(data_FILE_NOT_LOADED.text) + "\nOut of memory: please, assign more RAM to MetaPIGA. You can easily do so by using the menu 'Tools --> Memory settings'.";
            return error;
        }
        catch (Exception e) {
            final WaitingLogo.Status data_FILE_NOT_LOADED2;
            final WaitingLogo.Status error = data_FILE_NOT_LOADED2 = WaitingLogo.Status.DATA_FILE_NOT_LOADED;
            data_FILE_NOT_LOADED2.text = String.valueOf(data_FILE_NOT_LOADED2.text) + "\n" + Tools.getErrorMessage(e);
            return error;
        }
    }
    
    private void checkNexusFile() throws Exception {
        BufferedReader br = null;
        FileReader fr = null;
        InputStreamReader isr = null;
        if (this.nexus != null) {
            fr = new FileReader(this.nexus);
            br = new BufferedReader(fr);
        }
        else {
            isr = new InputStreamReader(this.is);
            br = new BufferedReader(isr);
        }
        String line;
        while ((line = br.readLine()) != null) {
            if (line.toUpperCase().contains("ENDBLOCK")) {
                throw new Exception("'ENDBLOCK' is not a valid token.\nPlease replace it by 'END'.");
            }
        }
        if (br != null) {
            br.close();
        }
        if (fr != null) {
            fr.close();
        }
        if (isr != null) {
            isr.close();
        }
    }
    
    private WaitingLogo.Status singleRunExtraction(final NexusFile nexusFile) throws ParseNexusException, NexusInconsistencyException, UnknownDataException, CharsetIntersectionException, IncompatibleDataException {
        TreesBlock tb = null;
        boolean isConvertingToCodons = false;
        int startCodons = 0;
        int endCodons = 0;
        Parameters.CodonTransitionTableType codonTableType = null;
        final Iterator it = nexusFile.blockIterator();
        while (it.hasNext()) {
            final NexusBlock block = (NexusBlock) it.next();
            if (block.getBlockName().equals("METAPIGA")) {
                final MetapigaBlock mp = (MetapigaBlock)block;
                this.parameters.setParameters(mp);
                if (mp.getDataType() != DataType.CODON) {
                    continue;
                }
                isConvertingToCodons = true;
                startCodons = mp.getCodonDomainStartPosition();
                endCodons = mp.getCodonDomainEndPosition();
                codonTableType = mp.getCodonTable();
            }
            else if (block.getBlockName().equals("CHARACTERS")) {
                final CharactersBlock cb = (CharactersBlock)block;
                if (cb.getGap() == null) {
                    cb.setGap("-");
                }
                this.parameters.setParameters(cb);
            }
            else if (block.getBlockName().equals("DATA")) {
                final DataBlock db = (DataBlock)block;
                if (db.getGap() == null) {
                    db.setGap("-");
                }
                this.parameters.setParameters(db);
            }
            else if (block.getBlockName().equals("TREES")) {
                tb = (TreesBlock)block;
            }
            else if (block.getBlockName().equals("SETS")) {
                System.out.println("SETS block is not used in MetaPIGA. \nIf you want to define charsets, you can do it with the CHARSET command in the METAPIGA block.");
            }
            else if (block.getBlockName().equals("TAXA")) {
                System.out.println("TAXA block is not used in MetaPIGA. \nMetaPiga uses taxas found in DATA or CHARACTER block.");
            }
            else {
                System.out.println(String.valueOf(block.getBlockName()) + " block is not used in MetaPIGA.");
            }
        }
        this.parameters.nexusFile = this.nexus;
        if (isConvertingToCodons) {
            this.parameters.setCodonsInRange(startCodons, endCodons, codonTableType);
        }
        this.parameters.buildDataset();
        this.parameters.checkParameters();
        if (tb != null) {
            this.parameters.setParameters(tb);
            if (this.parameters.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
                for (final Tree tree : this.parameters.startingTrees) {
                    if (!tree.isCompatibleWithOutgroup(this.parameters.outgroup)) {
                        throw new ParseNexusException("Topology of tree " + tree.getName() + " is NOT compatible with defined outgroup.");
                    }
                }
            }
        }
        this.parametersList.addElement(this.parameters);
        return WaitingLogo.Status.DATA_FILE_LOADED;
    }
    
    private WaitingLogo.Status batchRunExtraction(final NexusFile nexusFile) throws ParseNexusException, NexusInconsistencyException, UnknownDataException, CharsetIntersectionException, IncompatibleDataException {
        this.dataBlocks = new HashMap<String, CharactersBlock>();
        this.metapigaBlocks = new HashMap<String, MetapigaBlock>();
        this.treesBlocks = new HashMap<String, TreesBlock>();
        final Iterator it = nexusFile.blockIterator();
        while (it.hasNext()) {
            final NexusBlock block = (NexusBlock) it.next();
            if (block.getBlockName().equals("METAPIGA")) {
                final MetapigaBlock mp = (MetapigaBlock)block;
                final String label = this.extractLabelFromComment(mp.getComments());
                if (label == null) {
                    continue;
                }
                this.metapigaBlocks.put(label, mp);
            }
            else if (block.getBlockName().equals("CHARACTERS")) {
                final CharactersBlock cb = (CharactersBlock)block;
                final String label = this.extractLabelFromComment(cb.getComments());
                if (label == null) {
                    continue;
                }
                this.dataBlocks.put(label, cb);
            }
            else if (block.getBlockName().equals("DATA")) {
                final DataBlock db = (DataBlock)block;
                final String label = this.extractLabelFromComment(db.getComments());
                if (label == null) {
                    continue;
                }
                this.dataBlocks.put(label, db);
            }
            else {
                if (!block.getBlockName().equals("TREES")) {
                    continue;
                }
                final TreesBlock tb = (TreesBlock)block;
                final String label = this.extractLabelFromComment(tb.getComments());
                if (label == null) {
                    continue;
                }
                this.treesBlocks.put(label, tb);
            }
        }
        for (final String label2 : this.batchBlock.getRunLabels()) {
            if (!this.batchBlock.getRunData().containsKey(label2)) {
                throw new ParseNexusException("RUN " + label2 + " has no DATA associated with it !");
            }
            if (!this.batchBlock.getRunParam().containsKey(label2)) {
                throw new ParseNexusException("RUN " + label2 + " has no PARAM associated with it !");
            }
            final String data = this.batchBlock.getRunData().get(label2);
            final String param = this.batchBlock.getRunParam().get(label2);
            final String trees = this.batchBlock.getRunTrees().containsKey(label2) ? this.batchBlock.getRunTrees().get(label2) : null;
            if (!this.dataBlocks.containsKey(data)) {
                throw new ParseNexusException("No DATA or CHARACTER block labelled " + data + " was found in the Nexus file.");
            }
            if (!this.metapigaBlocks.containsKey(param)) {
                throw new ParseNexusException("No METAPIGA block labelled " + param + " was found in the Nexus file.");
            }
            if (trees != null && !this.treesBlocks.containsKey(trees)) {
                throw new ParseNexusException("No TREES block labelled " + trees + " was found in the Nexus file.");
            }
            final Parameters P = new Parameters(label2);
            P.setParameters(this.metapigaBlocks.get(param));
            P.label = label2;
            P.setParameters(this.dataBlocks.get(data));
            P.buildDataset();
            P.checkParameters();
            if (trees != null) {
                P.setParameters(this.treesBlocks.get(trees));
                if (P.startingTreeGeneration == Parameters.StartingTreeGeneration.GIVEN) {
                    for (final Tree tree : P.startingTrees) {
                        if (!tree.isCompatibleWithOutgroup(P.outgroup)) {
                            throw new ParseNexusException("RUN " + label2 + " : Topology of tree " + tree.getName() + " is NOT compatible with defined outgroup.");
                        }
                    }
                }
            }
            this.parametersList.addElement(P);
        }
       return WaitingLogo.Status.DATA_BATCH_LOADED;
    }
    
    private String extractLabelFromComment(final List comments) {
        for (final Object comment : comments) {
            final Iterator it = ((NexusComment)comment).commentIterator();
            while (it.hasNext()) {
                final String comString = it.next().toString().toUpperCase();
                if (comString.contains("BATCHLABEL")) {
                    return comString.split("=")[1].replace('_', ' ').trim();
                }
            }
        }
        return null;
    }
    
    @Override
    protected void done() {
        this.metapiga.busy = false;
    }
}

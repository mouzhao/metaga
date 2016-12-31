/*
 * Decompiled with CFR 0_115.
 */
package metapiga.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import metapiga.MetaPIGA;
import metapiga.WaitingLogo;
import metapiga.modelization.data.DataType;
import metapiga.parameters.Parameters;
import metapiga.utilities.Tools;
import org.biojavax.bio.phylo.io.nexus.DataBlock;

public class FastaReader
        extends SwingWorker<WaitingLogo.Status, Object> {
    private MetaPIGA metapiga;
    File fasta = null;
    private InputStream is = null;
    private Parameters parameters;
    private DefaultListModel parametersList;
    private static /* synthetic */ int[] $SWITCH_TABLE$metapiga$modelization$data$DataType;

    public FastaReader(File fastaFile, MetaPIGA metapiga) {
        this.metapiga = metapiga;
        this.parametersList = metapiga.parameters;
        this.fasta = fastaFile;
    }

    public FastaReader(InputStream fastaInputStream, MetaPIGA metapiga) {
        this.metapiga = metapiga;
        this.parametersList = metapiga.parameters;
        this.is = fastaInputStream;
    }

    @Override
    public WaitingLogo.Status doInBackground() {
        try {
            String line;
            BufferedReader br = null;
            FileReader fr = null;
            InputStreamReader isr = null;
            if (this.fasta != null) {
                String name = "";
                String[] split = this.fasta.getName().split("\\.");
                int i = 0;
                while (i < split.length - 1) {
                    name = String.valueOf(name) + split[i];
                    if (i + 1 < split.length - 1) {
                        name = String.valueOf(name) + ".";
                    }
                    ++i;
                }
                this.parameters = new Parameters(name);
                fr = new FileReader(this.fasta);
                br = new BufferedReader(fr);
            } else {
                this.parameters = new Parameters("default");
                isr = new InputStreamReader(this.is);
                br = new BufferedReader(isr);
            }
            String taxon = "";
            ArrayList<String> taxa = new ArrayList<String>();
            HashMap<String, String> sequences = new HashMap<String, String>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith(">")) {
                    taxon = line.substring(1);
                    taxa.add(taxon);
                    sequences.put(taxon, "");
                    continue;
                }
                sequences.put(taxon, ((String)sequences.get(taxon)).concat(line.toUpperCase()));
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
            DataBlock dataBlock = new DataBlock();
            dataBlock.setGap("-");
            dataBlock.setMatchChar(".");
            dataBlock.setMissing("?");
            int nTot = 0;
            int nACGT = 0;
            int n01 = 0;
            for (String tax : taxa) {
                dataBlock.addMatrixEntry(this.formatForNexus(tax));
                char[] arrc = ((String)sequences.get(tax)).toCharArray();
                int n = arrc.length;
                int n2 = 0;
                while (n2 < n) {
                    char c = arrc[n2];
                    if (c == 'A' || c == 'C' || c == 'G' || c == 'T' || c == 'N') {
                        ++nTot;
                        ++nACGT;
                    } else if (c == '0' || c == '1') {
                        ++n01;
                        ++nTot;
                    } else if (c == 'R' || c == 'D' || c == 'Q' || c == 'E' || c == 'H' || c == 'I' || c == 'L' || c == 'K' || c == 'M' || c == 'F' || c == 'P' || c == 'S' || c == 'W' || c == 'Y' || c == 'V' || c == 'B' || c == 'Z' || c == 'J' || c == 'X') {
                        ++nTot;
                    }
                    ++n2;
                }
            }
            DataType dataType = (double)nACGT / (double)nTot > 0.8 ? DataType.DNA : ((double)n01 / (double)nTot > 0.8 ? DataType.STANDARD : DataType.PROTEIN);
            dataBlock.setDataType(dataType.name());
            int nchar = 0;
            for (Map.Entry e : sequences.entrySet()) {
                int nchart = 0;
                char[] arrc = ((String)e.getValue()).toCharArray();
                int n = arrc.length;
                int n3 = 0;
                while (n3 < n) {
                    char c = arrc[n3];
                    block1 : switch (FastaReader.$SWITCH_TABLE$metapiga$modelization$data$DataType()[dataType.ordinal()]) {
                        case 1: {
                            switch (c) {
                                case '-':
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'G':
                                case 'H':
                                case 'K':
                                case 'M':
                                case 'N':
                                case 'R':
                                case 'S':
                                case 'T':
                                case 'V':
                                case 'W':
                                case 'Y': {
                                    dataBlock.appendMatrixData(this.formatForNexus((String)e.getKey()), "" + c);
                                    ++nchart;
                                    break block1;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (c) {
                                case '-':
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                case 'G':
                                case 'H':
                                case 'I':
                                case 'J':
                                case 'K':
                                case 'L':
                                case 'M':
                                case 'N':
                                case 'P':
                                case 'Q':
                                case 'R':
                                case 'S':
                                case 'T':
                                case 'V':
                                case 'W':
                                case 'X':
                                case 'Y':
                                case 'Z': {
                                    dataBlock.appendMatrixData(this.formatForNexus((String)e.getKey()), "" + c);
                                    ++nchart;
                                    break block1;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (c) {
                                case '-':
                                case '0':
                                case '1':
                                case 'X': {
                                    dataBlock.appendMatrixData(this.formatForNexus((String)e.getKey()), "" + c);
                                    ++nchart;
                                    break block1;
                                }
                            }
                        }
                    }
                    ++n3;
                }
                if (nchar == 0) {
                    nchar = nchart;
                    continue;
                }
                if (nchart == nchar) continue;
                WaitingLogo.Status error = WaitingLogo.Status.DATA_FILE_NOT_LOADED;
                error.text = String.valueOf(error.text) + "\nCannot load fasta file: all sequences don't have the same length (" + (String)taxa.get(0) + " has a size of " + nchar + " and " + (String)e.getKey() + " has a size of " + nchart + ").";
                return error;
            }
            dataBlock.setDimensionsNTax(taxa.size());
            dataBlock.setDimensionsNChar(nchar);
            this.parameters.setParameters(dataBlock);
            this.parameters.buildDataset();
            this.parameters.checkParameters();
            this.parametersList.addElement(this.parameters);
            return WaitingLogo.Status.DATA_FILE_LOADED;
        }
        catch (OutOfMemoryError e) {
            WaitingLogo.Status error = WaitingLogo.Status.DATA_FILE_NOT_LOADED;
            error.text = String.valueOf(error.text) + "\nOut of memory: please, assign more RAM to MetaPIGA. You can easily do so by using the menu 'Tools --> Memory settings'.";
            return error;
        }
        catch (Exception e) {
            e.printStackTrace();
            WaitingLogo.Status error = WaitingLogo.Status.DATA_FILE_NOT_LOADED;
            error.text = String.valueOf(error.text) + "\n" + Tools.getErrorMessage(e);
            return error;
        }
    }

    private String formatForNexus(String taxon) {
        return taxon.replace('\'', '!').replace('(', '!').replace(')', '!').replace('[', '!').replace(']', '!').replace(',', '!').replace(':', '!').replace('\u00a7', '!').replace(';', '!').replace('_', ' ');
    }

    @Override
    protected void done() {
        this.metapiga.busy = false;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$metapiga$modelization$data$DataType() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$metapiga$modelization$data$DataType;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[DataType.values().length];
        try {
            arrn[DataType.CODON.ordinal()] = 4;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[DataType.DNA.ordinal()] = 1;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[DataType.PROTEIN.ordinal()] = 2;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[DataType.STANDARD.ordinal()] = 3;
        }
        catch (NoSuchFieldError v4) {}
        $SWITCH_TABLE$metapiga$modelization$data$DataType = arrn;
        return $SWITCH_TABLE$metapiga$modelization$data$DataType;
    }
}


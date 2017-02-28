// 
// Decompiled by Procyon v0.5.30
// 

package metapiga;

import javax.swing.UIManager;
import java.util.ArrayList;

import metapiga.io.NexusReader;
import metapiga.io.FastaReader;

import java.io.BufferedReader;
import java.io.FileReader;
import metapiga.parameters.Parameters;
import metapiga.monitors.SearchSilent;
import metapiga.monitors.SearchConsole;
import javax.swing.JProgressBar;
import java.util.Locale;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;

public class MetaPIGA
{
    public static final String version = "3.1";
    public static ProgressHandling progressHandling;
    public DefaultListModel parameters;
    public static UI ui;
    public boolean busy;
    boolean packFrame;

    
    public MetaPIGA(final UI ui, final List<File> dataFiles, final int consoleWidth,List<Double> weights) {
        this.parameters = new DefaultListModel();
        this.busy = false;
        this.packFrame = false;
        MetaPIGA.ui = ui;
        Locale.setDefault(new Locale("en", "US"));
        ProgressHandling.consoleWidth = consoleWidth - 1;
        if (ui == UI.CONSOLE) {
            (MetaPIGA.progressHandling = new ProgressHandling(new JProgressBar())).setUI(UI.CONSOLE);
        }
        else {
            (MetaPIGA.progressHandling = new ProgressHandling(new JProgressBar())).setUI(UI.SILENT);
        }
        for (final File dataFile : dataFiles) {
            try {
                if (ui == UI.CONSOLE) {
                    System.out.println("Found " + dataFile);
                }
                this.loadDataFile(dataFile);
                do {
                    Thread.sleep(500L);
                } while (this.busy);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!this.parameters.isEmpty()) {
            for (int i = 0; i < this.parameters.getSize(); ++i) {
                if(((Parameters)this.parameters.get(i)).cpPopNum < this.parameters.getSize()){
                    ((Parameters)this.parameters.get(i)).cpPopNum = this.parameters.getSize();
                }
                //((Parameters)this.parameters.get(i)).moweight = weights;
            }
            switch (ui) {
                case CONSOLE: {
                    final Runnable search = new SearchConsole(this.parameters);
                    final Thread thread = new Thread(search, "ConsoleUI-Search");
                    thread.start();
                    try {
                        thread.join();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                    break;
                }
                case SILENT: {
                    final Runnable search = new SearchSilent(this.parameters);
                    final Thread thread = new Thread(search, "Silent-Search");
                    thread.start();
                    try {
                        thread.join();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                    break;
                }
            }
        }
    }
    
    public void loadDataFile(final File dataFile) {
        this.busy = true;
        Parameters.FileFormat format = Parameters.FileFormat.NEXUS;
        try {
            final FileReader fr = new FileReader(dataFile);
            final BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0 && line.startsWith(">")) {
                    format = Parameters.FileFormat.FASTA;
                    break;
                }
            }
            br.close();
            fr.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        switch (format) {
            case FASTA: {
                final FastaReader fastaReader = new FastaReader(dataFile, this);
                fastaReader.execute();
                break;
            }
            default: {
                final NexusReader nexusReader = new NexusReader(dataFile, this);
                nexusReader.execute();
                break;
            }
        }
    }

    public static void main(final String[] args) {
        try {
            final List<String> fileArgs = new ArrayList<String>();
            boolean updateCheck = true;
            UI ui = UI.CONSOLE;
            int consoleWidth = 80;
            List<Double> weights = new ArrayList<Double>();
            weights.add((double)0);
            weights.add((double)0.25);
            weights.add((double)0.5);
            weights.add((double)1);

            for (final String arg : args) {
                if (arg.equals("noupdate")) {
                    updateCheck = false;
                }
                else if (arg.equals("nogui") && ui != UI.SILENT) {
                    ui = UI.CONSOLE;
                }
                else if (arg.equals("silent")) {
                    ui = UI.SILENT;
                }
                else if (arg.startsWith("width=")) {
                    consoleWidth = Integer.parseInt(arg.substring(6));
                }else if (arg.startsWith("weights=")){
                    weights.clear();
                    for(String w : arg.substring(8).split(",")) {
                        weights.add(Double.parseDouble(w));
                    }
                }else {
                    fileArgs.add(arg);
                }
            }
//            if (Util.isLinux()) {
//                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            final List<File> nexusFiles = new ArrayList<File>();
            for (final String filename : fileArgs) {
                nexusFiles.add(new File(filename));
            }
            new MetaPIGA(ui, nexusFiles, consoleWidth,weights);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public enum UI {
        CONSOLE("CONSOLE", 1),
        SILENT("SILENT", 2);

        private UI(final String s, final int n) {
        }
    }
}

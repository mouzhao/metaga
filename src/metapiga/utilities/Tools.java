// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.utilities;

import javax.swing.JFileChooser;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.lang.reflect.Method;
import metapiga.parameters.Parameters;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import cern.jet.stat.Probability;
import cern.jet.stat.Gamma;
import cern.jet.random.Exponential;
import cern.jet.random.Normal;
import Jama.Matrix;
import java.text.DecimalFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import metapiga.MetaPIGA;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.border.Border;
import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class Tools
{
    private static final double normalMean = 1.0;
    private static final double normalSD = 0.5;
    private static final String ZIP_EXTENSION = ".zip";
    private static final int DEFAULT_LEVEL_COMPRESSION = 9;
    
    public static String getErrorMessage(final Exception e) {
        String message = e.getMessage();
        message = String.valueOf(message) + "\n Java exception : " + e.getCause() + " (" + e.getMessage() + ")";
        StackTraceElement[] stackTrace;
        for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
            final StackTraceElement el = stackTrace[i];
            message = String.valueOf(message) + "\n\tat " + el.toString();
        }
        return message;
    }
    
    public static JPanel getErrorPanel(final String message, final Exception e) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JTextArea label = new JTextArea(message);
        final Color bg = panel.getBackground();
        label.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
        label.setBorder(null);
        label.setEditable(false);
        panel.add(label, "North");
        final JTextArea textArea = new JTextArea();
        textArea.setText(String.valueOf(e.getMessage()) + "\n");
        textArea.append("Java exception : " + e.getCause());
        StackTraceElement[] stackTrace;
        for (int length = (stackTrace = e.getStackTrace()).length, i = 0; i < length; ++i) {
            final StackTraceElement el = stackTrace[i];
            textArea.append("\n  " + el.toString());
        }
        textArea.setCaretPosition(0);
        textArea.setEditable(false);
        final JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, "Center");
        panel.setPreferredSize(new Dimension(500, 300));
        return panel;
    }
    
    public static void ShowErrorMessage(final Component parent, final String message, final String title) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JTextArea textArea = new JTextArea();
        textArea.setText(message);
        textArea.setCaretPosition(0);
        textArea.setEditable(false);
        final JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, "Center");
        panel.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(parent, panel, title, 0);
    }
    
    public static void showWarningMessage(Component parent, final String message, final String title) {
        final MetaPIGA.UI ui = MetaPIGA.ui;
        if (ui == MetaPIGA.UI.CONSOLE) {
            System.out.println(String.valueOf(title) + ":");
            System.out.println(message);
        }
        else {
            final MetaPIGA.UI silent = MetaPIGA.UI.SILENT;
        }
    }
    
    public static ImageIcon getScaledIcon(final ImageIcon icon, final int size) {
        return new ImageIcon(icon.getImage().getScaledInstance(size, size, 4));
    }
    
    public static String doubletoString(final double x, final int d) {
        if (x == 0.0 || Math.abs(x) >= Math.pow(10.0, -d)) {
            final NumberFormat fmt = NumberFormat.getInstance(Locale.US);
            if (fmt instanceof DecimalFormat) {
                final DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.US);
                symb.setGroupingSeparator(' ');
                ((DecimalFormat)fmt).setDecimalFormatSymbols(symb);
                ((DecimalFormat)fmt).setMaximumFractionDigits(d);
                ((DecimalFormat)fmt).setGroupingUsed(true);
            }
            final String s = fmt.format(x);
            return s;
        }
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return new StringBuilder().append(x).toString();
        }
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(',');
        String card = "";
        for (int i = 0; i < d; ++i) {
            card = String.valueOf(card) + "#";
        }
        final NumberFormat formatter = new DecimalFormat("0." + card + "E0", dfs);
        return formatter.format(x);
    }
    
    public static double parseDouble(final String s) {
        final NumberFormat fmt = NumberFormat.getInstance(Locale.US);
        if (fmt instanceof DecimalFormat) {
            final DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.US);
            symb.setGroupingSeparator(' ');
            ((DecimalFormat)fmt).setDecimalFormatSymbols(symb);
            ((DecimalFormat)fmt).setGroupingUsed(true);
        }
        try {
            final Number n = fmt.parse(s);
            return n.doubleValue();
        }
        catch (Exception e) {
            System.err.println("Cannot parse double '" + s + "'");
            return 0.0;
        }
    }
    
    public static String doubleToPercent(double x, final int d) {
        x *= 100.0;
        final NumberFormat fmt = NumberFormat.getInstance(Locale.US);
        if (fmt instanceof DecimalFormat) {
            final DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.US);
            symb.setGroupingSeparator(' ');
            ((DecimalFormat)fmt).setDecimalFormatSymbols(symb);
            ((DecimalFormat)fmt).setMaximumFractionDigits(d);
            ((DecimalFormat)fmt).setGroupingUsed(true);
        }
        final String s = String.valueOf(fmt.format(x)) + "%";
        return s;
    }
    
    public static boolean isIdentity(final Matrix M) {
        for (int i = 0; i < M.getRowDimension(); ++i) {
            for (int j = 0; j < M.getColumnDimension(); ++j) {
                if (i == j) {
                    if (M.get(i, j) != 1.0) {
                        return false;
                    }
                }
                else if (M.get(i, j) != 0.0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static int randInt(final int max) {
        return (int)Math.floor(Math.random() * max);
    }
    
    public static double positiveNormalRand() {
        double rand = 1.0;
        do {
            rand = Normal.staticNextDouble(1.0, 0.5);
        } while (rand <= 0.4);
        return rand;
    }
    
    public static double exponentialMultiplierRand() {
        return Exponential.staticNextDouble(2.0) + 0.5;
    }
    
    public static double percentagePointChi2(final double prob, final double v) {
        final double e = 5.0E-7;
        final double aa = 0.6931471805;
        final double small = 1.0E-6;
        double a = 0.0;
        double q = 0.0;
        double p1 = 0.0;
        double p2 = 0.0;
        double t = 0.0;
        double x = 0.0;
        double b = 0.0;
        if (prob < small) {
            return 0.0;
        }
        if (prob > 1.0 - small) {
            return 9999.0;
        }
        if (v <= 0.0) {
            return -1.0;
        }
        final double g = Gamma.logGamma(v / 2.0);
        final double xx = v / 2.0;
        final double c = xx - 1.0;
        double ch;
        if (v < -1.24 * Math.log(prob)) {
            ch = Math.pow(prob * xx * Math.exp(g + xx * aa), 1.0 / xx);
            if (ch - e < 0.0) {
                return ch;
            }
        }
        else if (v <= 0.32) {
            ch = 0.4;
            a = Math.log(1.0 - prob);
            do {
                q = ch;
                p1 = 1.0 + ch * (4.67 + ch);
                p2 = ch * (6.73 + ch * (6.66 + ch));
                t = -0.5 + (4.67 + 2.0 * ch) / p1 - (6.73 + ch * (13.32 + 3.0 * ch)) / p2;
                ch -= (1.0 - Math.exp(a + g + 0.5 * ch + c * aa) * p2 / p1) / t;
            } while (Math.abs(q / ch - 1.0) - 0.01 > 0.0);
        }
        else {
            x = Probability.normalInverse(prob);
            p1 = 0.222222 / v;
            ch = v * Math.pow(x * Math.sqrt(p1) + 1.0 - p1, 3.0);
            if (ch > 2.2 * v + 6.0) {
                ch = -2.0 * (Math.log(1.0 - prob) - c * Math.log(0.5 * ch) + g);
            }
        }
        do {
            q = ch;
            p1 = 0.5 * ch;
            if ((t = Gamma.incompleteGamma(xx, p1)) < 0.0) {
                return -1.0;
            }
            p2 = prob - t;
            t = p2 * Math.exp(xx * aa + g + p1 - c * Math.log(ch));
            b = t / ch;
            a = 0.5 * t - b * c;
            final double s1 = (210.0 + a * (140.0 + a * (105.0 + a * (84.0 + a * (70.0 + 60.0 * a))))) / 420.0;
            final double s2 = (420.0 + a * (735.0 + a * (966.0 + a * (1141.0 + 1278.0 * a)))) / 2520.0;
            final double s3 = (210.0 + a * (462.0 + a * (707.0 + 932.0 * a))) / 2520.0;
            final double s4 = (252.0 + a * (672.0 + 1182.0 * a) + c * (294.0 + a * (889.0 + 1740.0 * a))) / 5040.0;
            final double s5 = (84.0 + 264.0 * a + c * (175.0 + 606.0 * a)) / 2520.0;
            final double s6 = (120.0 + c * (346.0 + 127.0 * c)) / 5040.0;
            ch += t * (1.0 + 0.5 * t * s1 - b * c * (s1 - b * (s2 - b * (s3 - b * (s4 - b * (s5 - b * s6))))));
        } while (Math.abs(q / ch - 1.0) > e);
        return ch;
    }
    

    public static long estimateNecessaryMemory(final Parameters p) {
        double mem = 0.0;
        double T = 1.0;
        switch (p.heuristic) {
            case CP: {
                switch (p.cpSelection) {
                    case REPLACEMENT:
                    case IMPROVE:
                    case KEEPBEST: {
                        T = p.cpPopNum * p.cpIndNum + 1;
                        break;
                    }
                    case RANK:
                    case TOURNAMENT: {
                        T = (p.cpPopNum + ((p.cpCoreNum > 1) ? p.cpPopNum : 1)) * p.cpIndNum + 1;
                        break;
                    }
                }
                break;
            }
            case GA: {
                switch (p.gaSelection) {
                    case REPLACEMENT:
                    case IMPROVE:
                    case KEEPBEST: {
                        T = p.gaIndNum + 1;
                        break;
                    }
                    case RANK:
                    case TOURNAMENT: {
                        T = p.gaIndNum * 2 + 1;
                        break;
                    }
                }
                break;
            }
            case SA: {
                T = 3.0;
                break;
            }
            case HC: {
                T = 3.0;
                break;
            }
            case BS: {
                T = 1.0;
                break;
            }
        }
        final double N = 2 * p.dataset.getNTax() - 1;
        final double D = p.dataset.getCompressedNChar();
        final double C = (p.evaluationDistribution != Parameters.EvaluationDistribution.NONE) ? p.evaluationDistributionSubsets : 1;
        final double S = p.dataset.getDataType().numOfStates();
        final double Prr = p.replicatesParallel;
        final double precision = 4.0;
        final double state = S * 2.0 / 8.0;
        mem += T;
        mem /= 1024.0;
        mem /= 1024.0;
        mem *= N;
        mem *= D;
        mem *= C;
        mem *= S;
        mem *= precision;
        mem *= Prr;
        mem += p.dataset.getNTax() * (D + 1.0) * state / 1024.0 / 1024.0;
        mem *= 1.3;
        return (long)mem;
    }
    
    public static void openURL(final String url) {
        final String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                final Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                final Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);
                openURL.invoke(null, url);
            }
            else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
            else {
                final String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; ++count) {
                    if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find supported web browser");
                }
                Runtime.getRuntime().exec(new String[] { browser, url });
            }
        }
        catch (Exception e) {
            ShowErrorMessage(null, "Cannot open web browser:\n" + e.getLocalizedMessage(), "Opening web browser");
        }
    }
    
    public static void compressSinglefile(final String file, final String target) throws IOException {
        compressSinglefile(new File(file), new File(target), 9);
    }
    
    public static void decompress(final File file, final File folder, final boolean deleteZipAfter) throws IOException {
        final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file.getCanonicalFile())));
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                final File f = new File(folder.getCanonicalPath(), ze.getName());
                if (f.exists()) {
                    f.delete();
                }
                if (ze.isDirectory()) {
                    f.mkdirs();
                }
                else {
                    f.getParentFile().mkdirs();
                    final OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
                    try {
                        try {
                            final byte[] buf = new byte[8192];
                            int bytesRead;
                            while (-1 != (bytesRead = zis.read(buf))) {
                                fos.write(buf, 0, bytesRead);
                            }
                        }
                        finally {
                            fos.close();
                        }
                        fos.close();
                    }
                    catch (IOException ioe) {
                        f.delete();
                        throw ioe;
                    }
                }
            }
        }
        finally {
            zis.close();
        }
        zis.close();
        if (deleteZipAfter) {
            file.delete();
        }
    }
    
    private static void compressSinglefile(final File file, final File target, final int compressionLevel) throws IOException {
        final File source = file.getCanonicalFile();
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(getZipTypeFile(source, target.getCanonicalFile())));
        out.setMethod(8);
        out.setLevel(compressionLevel);
        compressFile(out, "", file);
        out.close();
    }
    
    private static final void compressFile(final ZipOutputStream out, final String parentFolder, final File file) throws IOException {
        final String zipName = parentFolder + file.getName() + (file.isDirectory() ? '/' : "");
        final ZipEntry entry = new ZipEntry(zipName);
        entry.setSize(file.length());
        entry.setTime(file.lastModified());
        out.putNextEntry(entry);
        if (file.isDirectory()) {
            File[] listFiles;
            for (int length = (listFiles = file.listFiles()).length, i = 0; i < length; ++i) {
                final File f = listFiles[i];
                compressFile(out, zipName.toString(), f);
            }
            return;
        }
        final InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            final byte[] buf = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = in.read(buf))) {
                out.write(buf, 0, bytesRead);
            }
        }
        finally {
            in.close();
        }
        in.close();
    }
    
    private static File getZipTypeFile(final File source, final File target) throws IOException {
        if (target.getName().toLowerCase().endsWith(".zip")) {
            return target;
        }
        final String tName = target.isDirectory() ? source.getName() : target.getName();
        final int index = tName.lastIndexOf(46);
        return new File((target.isDirectory() ? target.getCanonicalPath() : target.getParentFile().getCanonicalPath()) + File.separatorChar + ((index < 0) ? tName : tName.substring(0, index)) + ".zip");
    }
    
    public static File getHomeDirectory() {
        try {
            return new JFileChooser().getFileSystemView().getDefaultDirectory();
        }
        catch (Error err) {
            return new File(System.getProperty("user.home"));
        }
        catch (Exception e) {
            return new File(System.getProperty("user.home"));
        }
    }
}

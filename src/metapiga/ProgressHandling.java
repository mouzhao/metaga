// 
// Decompiled by Procyon v0.5.30
// 

package metapiga;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import metapiga.utilities.Tools;
import javax.swing.JProgressBar;

public class ProgressHandling
{
    private static final char[] indeterminateChars;
    public static int consoleWidth;
    private JProgressBar[] progressBar;
    private int nbrProgress;
    private MetaPIGA.UI ui;
    private String[] currentText;
    private int[] currentValue;
    private long[] currentTime;
    private String indeterminateText;
    private boolean[] showPercent;
    private boolean[] showTime;
    private boolean waitForEndMessage;
    private boolean finished;
    private int lastLineLength;
    private int[] currentIndChar;
    
    static {
        indeterminateChars = new char[] { '|', '/', '-', '\\' };
        ProgressHandling.consoleWidth = 79;
    }
    
    public ProgressHandling(final JProgressBar progressBar) {
        this.ui = MetaPIGA.UI.CONSOLE;
        this.nbrProgress = 1;
        (this.progressBar = new JProgressBar[this.nbrProgress])[0] = progressBar;
        this.currentText = new String[this.nbrProgress];
        this.currentTime = new long[this.nbrProgress];
        this.currentValue = new int[this.nbrProgress];
        this.showPercent = new boolean[this.nbrProgress];
        this.showTime = new boolean[this.nbrProgress];
        this.currentIndChar = new int[this.nbrProgress];
    }
    
    public ProgressHandling(final int progressNumber) {
        this.ui = MetaPIGA.UI.CONSOLE;
        this.nbrProgress = progressNumber;
        this.progressBar = new JProgressBar[progressNumber];
        for (int i = 0; i < progressNumber; ++i) {
            this.progressBar[i] = new JProgressBar();
        }
        this.currentText = new String[this.nbrProgress];
        this.currentTime = new long[this.nbrProgress];
        this.currentValue = new int[this.nbrProgress];
        this.showPercent = new boolean[this.nbrProgress];
        this.showTime = new boolean[this.nbrProgress];
        this.currentIndChar = new int[this.nbrProgress];
    }
    
    public void setUI(final MetaPIGA.UI ui) {
        this.ui = ui;
    }
    
    public MetaPIGA.UI getUI() {
        return this.ui;
    }
    
    public void newIndeterminateProgress(final String text) {
        this.waitForEndMessage = false;
        this.finished = false;
        this.showPercent[0] = false;
        this.showTime[0] = false;
        this.progressBar[0].setIndeterminate(true);
        this.indeterminateText = text;
        this.progressBar[0].setString(text);
        this.progressBar[0].setStringPainted(true);
        this.lastLineLength = 0;
        this.currentIndChar[0] = 0;
        if (this.ui == MetaPIGA.UI.CONSOLE) {
            System.out.println(text);
        }
    }
    
    public void newSingleProgress(final int minimum, final int maximum, final String text) {
        this.waitForEndMessage = false;
        this.finished = false;
        this.showPercent[0] = true;
        this.showTime[0] = false;
        this.progressBar[0].setIndeterminate(false);
        this.progressBar[0].setMinimum(minimum);
        this.progressBar[0].setMaximum(maximum);
        this.lastLineLength = 0;
        this.currentIndChar[0] = 0;
        this.currentText[0] = text;
        this.setValue(0);
    }
    
    public void newMultiProgress(final int replicate, final int minimum, final int maximum, final String text) {
        this.waitForEndMessage = true;
        this.finished = false;
        this.showPercent[replicate] = true;
        this.showTime[replicate] = false;
        this.progressBar[replicate].setIndeterminate(false);
        this.progressBar[replicate].setMinimum(minimum);
        this.progressBar[replicate].setMaximum(maximum);
        this.lastLineLength = 0;
        this.currentIndChar[replicate] = 0;
        this.currentText[replicate] = text;
        this.setValue(replicate, 0);
    }
    
    public synchronized void newSearchProgress(final int replicate, final int maxSteps, final long maxTime, final String startLikelihood) {
        this.waitForEndMessage = true;
        this.showPercent[replicate] = (maxSteps > 0);
        this.showTime[replicate] = (maxTime > 0L);
        this.progressBar[replicate].setIndeterminate(false);
        this.progressBar[replicate].setMinimum(0);
        this.progressBar[replicate].setMaximum(maxSteps);
        this.currentTime[replicate] = maxTime;
        this.currentText[replicate] = startLikelihood;
        this.lastLineLength = 0;
        this.currentIndChar[replicate] = 0;
        this.finished = false;
        this.setValue(replicate, 0);
    }
    
    public void setValue(final int replicate, final int value) {
        this.currentValue[replicate] = value + 1;
        this.progressBar[replicate].setValue(this.currentValue[replicate]);
        this.showProgress();
    }
    
    public void setTime(final int replicate, final long time) {
        this.currentTime[replicate] = time;
        this.showProgress();
    }
    
    public void setText(final int replicate, final String text) {
        this.currentText[replicate] = text;
        this.showProgress();
    }
    
    public void setValue(final int value) {
        this.setValue(0, value);
    }
    
    public void setTime(final long time) {
        this.setTime(0, time);
    }
    
    public void setText(final String text) {
        this.setText(0, text);
    }
    
    private void showProgress() {
        if (this.ui != MetaPIGA.UI.SILENT) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!ProgressHandling.this.finished) {
                        final StringBuilder text = new StringBuilder(ProgressHandling.consoleWidth);
                        for (int p = 0; p < ProgressHandling.this.nbrProgress; ++p) {
                            if (p > 0) {
                                text.append(" | ");
                            }
                            if (ProgressHandling.this.currentText[p] == null) {
                                text.append("-");
                            }
                            else {
                                text.append(ProgressHandling.this.currentText[p]);
                            }
                            if (ProgressHandling.this.showPercent[p]) {
                                final double completed = ProgressHandling.this.progressBar[p].getPercentComplete();
                                text.append(" -- " + Tools.doubleToPercent(completed, 0));
                                if (ProgressHandling.this.ui == MetaPIGA.UI.CONSOLE && ProgressHandling.this.nbrProgress == 1) {
                                    text.append(" [");
                                    for (int i = 0; i < 25; ++i) {
                                        if (completed * 100.0 < (i + 1) * 4) {
                                            text.append(".");
                                        }
                                        else {
                                            text.append("=");
                                        }
                                    }
                                    text.append("]");
                                }
                            }
                            if (ProgressHandling.this.showTime[p]) {
                                long sec = ProgressHandling.this.currentTime[p] / 1000L;
                                final long h = sec / 3600L;
                                sec -= h * 3600L;
                                final long min = sec / 60L;
                                sec -= min * 60L;
                                text.append(" -- " + (int)h + "h " + (int)min + "m " + (int)sec + "s" + " left");
                            }
                            if (!ProgressHandling.this.showPercent[p] && !ProgressHandling.this.showTime[p] && ProgressHandling.this.ui == MetaPIGA.UI.CONSOLE) {
                                text.append(" -- " + ProgressHandling.this.nextIndeterminateChar(p));
                            }
                            ProgressHandling.this.progressBar[p].setString(text.toString());
                        }
                        if (ProgressHandling.this.ui == MetaPIGA.UI.CONSOLE) {
                            for (int p = 0; p < ProgressHandling.this.nbrProgress; ++p) {
                                ProgressHandling.this.progressBar[p].setStringPainted(true);
                            }
                        }
                        else {
                            for (int j = text.length(); j < ProgressHandling.this.lastLineLength; ++j) {
                                text.append(" ");
                            }
                            if (text.length() > ProgressHandling.consoleWidth) {
                                text.delete(0, text.length());
                                for (int p = 0; p < ProgressHandling.this.nbrProgress; ++p) {
                                    if (p > 0) {
                                        text.append(" | ");
                                    }
                                    if (ProgressHandling.this.currentText[p] == null) {
                                        text.append("-");
                                    }
                                    else if (ProgressHandling.this.currentText[p].startsWith("Best ML")) {
                                        text.append(ProgressHandling.this.currentText[p].split("\\.")[0].replace("Best ML : ", "ML:").replace(' ', ','));
                                    }
                                    else if (ProgressHandling.this.currentText[p].startsWith("Creating metapopulation")) {
                                        text.append("Pop");
                                    }
                                    else if (ProgressHandling.this.currentText[p].startsWith("Building distance matrix")) {
                                        text.append("DM");
                                    }
                                    else if (ProgressHandling.this.currentText[p].startsWith("Creating population")) {
                                        text.append("Pop");
                                    }
                                    else if (ProgressHandling.this.currentText[p].startsWith("Optimization")) {
                                        text.append("Opti");
                                    }
                                    else if (ProgressHandling.this.currentText[p].startsWith("Setting temperature")) {
                                        text.append("Temp");
                                    }
                                    else if (ProgressHandling.this.currentText[p].startsWith("Building starting tree")) {
                                        text.append("StartTree");
                                    }
                                    if (ProgressHandling.this.showPercent[p]) {
                                        final double completed = ProgressHandling.this.progressBar[p].getPercentComplete();
                                        text.append(" " + Tools.doubleToPercent(completed, 0));
                                    }
                                    if (ProgressHandling.this.showTime[p]) {
                                        long sec = ProgressHandling.this.currentTime[p] / 1000L;
                                        final long h = sec / 3600L;
                                        sec -= h * 3600L;
                                        final long min = sec / 60L;
                                        sec -= min * 60L;
                                        text.append(" " + (int)h + ":" + (int)min + ":" + (int)sec);
                                    }
                                    if (!ProgressHandling.this.showPercent[p] && !ProgressHandling.this.showTime[p] && ProgressHandling.this.ui == MetaPIGA.UI.CONSOLE) {
                                        text.append(" " + ProgressHandling.this.nextIndeterminateChar(p));
                                    }
                                    ProgressHandling.this.progressBar[p].setString(text.toString());
                                }
                                text.setLength(ProgressHandling.consoleWidth);
                            }
                            System.out.print("\r" + text.toString());
                            ProgressHandling.access$10(ProgressHandling.this, text.length());
                        }
                        ProgressHandling.access$11(ProgressHandling.this, true);
                        for (int p = 0; p < ProgressHandling.this.nbrProgress; ++p) {
                            if ((ProgressHandling.this.showPercent[p] && ProgressHandling.this.currentValue[p] >= ProgressHandling.this.progressBar[p].getMaximum()) || (ProgressHandling.this.showTime[p] && ProgressHandling.this.currentTime[p] == 0L)) {
                                ProgressHandling.this.progressBar[p].setIndeterminate(true);
                                ProgressHandling.this.progressBar[p].setString(ProgressHandling.this.indeterminateText);
                            }
                            else {
                                ProgressHandling.access$11(ProgressHandling.this, false);
                            }
                        }
                        if (ProgressHandling.this.finished && ProgressHandling.this.ui == MetaPIGA.UI.CONSOLE && !ProgressHandling.this.waitForEndMessage) {
                            System.out.println();
                        }
                    }
                }
            });
        }
    }
    
    public synchronized void displayEndMessage(final List<String> textLines) {
        if (this.ui != MetaPIGA.UI.SILENT) {
            final String message = textLines.remove(0);
            final StringBuilder s = new StringBuilder("\r" + message);
            for (int i = message.length(); i < this.lastLineLength; ++i) {
                s.append(" ");
            }
            for (final String st : textLines) {
                s.append("\n" + st);
            }
            System.out.println(s.toString());
        }
    }
    
    private char nextIndeterminateChar(final int replicate) {
        if (this.currentIndChar[replicate] >= ProgressHandling.indeterminateChars.length) {
            this.currentIndChar[replicate] = 0;
        }
        return ProgressHandling.indeterminateChars[this.currentIndChar[replicate]++];
    }
    
    public void setVisible(final boolean isVisible) {
        if (this.ui == MetaPIGA.UI.CONSOLE) {
            this.progressBar[0].setVisible(isVisible);
        }
    }
    
    public void printText(final String text) {
        if (this.ui == MetaPIGA.UI.CONSOLE) {
            System.out.println(text);
        }
    }
    
    static /* synthetic */ void access$10(final ProgressHandling progressHandling, final int lastLineLength) {
        progressHandling.lastLineLength = lastLineLength;
    }
    
    static /* synthetic */ void access$11(final ProgressHandling progressHandling, final boolean finished) {
        progressHandling.finished = finished;
    }
}

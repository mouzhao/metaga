/*
 * Decompiled with CFR 0_115.
 */
package metapiga;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.LayoutManager;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import metapiga.MetaPIGA;
import metapiga.ProgressHandling;

public class WaitingLogo  implements Runnable {
//    public static final ImageIcon imageRest = new ImageIcon(MainFrame.class.getResource("resources/splash/rest.png"));
//    public static final ImageIcon imageAnimation0 = new ImageIcon(MainFrame.class.getResource("resources/splash/animation_0.png"));
//    public static final ImageIcon imageAnimation1 = new ImageIcon(MainFrame.class.getResource("resources/splash/animation_1.png"));
//    public static final ImageIcon imageAnimation2 = new ImageIcon(MainFrame.class.getResource("resources/splash/animation_2.png"));
//    public static final ImageIcon imageAnimation3 = new ImageIcon(MainFrame.class.getResource("resources/splash/animation_3.png"));
//    public static final ImageIcon imageAnimation4 = new ImageIcon(MainFrame.class.getResource("resources/splash/animation_4.png"));
//    public static final ImageIcon imageAnimation5 = new ImageIcon(MainFrame.class.getResource("resources/splash/animation_5.png"));
//    public static final ImageIcon imageAnimation6 = new ImageIcon(MainFrame.class.getResource("resources/splash/animation_6.png"));
    CardLayout logoCardLayout;
   // JPanel logoPanel = MainFrame.splashPanel;
    Component component;
    //JLabel statusBar = MainFrame.statusBar;
    ProgressHandling progressBar = MetaPIGA.progressHandling;
    boolean waiting;
    int numImages = 7;
    String text;
    boolean indeterminate;

    public WaitingLogo(Component parentComponent, Status status) {
        this.component = parentComponent;
        this.text = status.text;
        this.indeterminate = status.indeterminate;
        //this.logoPanel.removeAll();
        this.logoCardLayout = new CardLayout();
//        this.logoPanel.setLayout(this.logoCardLayout);
//        this.logoPanel.add((Component)new JLabel(imageRest), "rest");
//        this.logoPanel.add((Component)new JLabel(imageAnimation0), "0");
//        this.logoPanel.add((Component)new JLabel(imageAnimation1), "1");
//        this.logoPanel.add((Component)new JLabel(imageAnimation2), "2");
//        this.logoPanel.add((Component)new JLabel(imageAnimation3), "3");
//        this.logoPanel.add((Component)new JLabel(imageAnimation4), "4");
//        this.logoPanel.add((Component)new JLabel(imageAnimation5), "5");
//        this.logoPanel.add((Component)new JLabel(imageAnimation6), "6");
        this.waiting = false;
    }

    public void stop(Status status) {
        this.text = status.text;
        this.indeterminate = status.indeterminate;
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                WaitingLogo.this.component.setCursor(Cursor.getPredefinedCursor(0));
              //  WaitingLogo.this.statusBar.setVisible(true);
                WaitingLogo.this.progressBar.setVisible(false);
                //WaitingLogo.this.statusBar.setText(WaitingLogo.this.text);
                WaitingLogo.this.progressBar.printText(WaitingLogo.this.text);
            }
        });
        this.waiting = false;
    }

    @Override
    public void run() {
        this.waiting = true;
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                WaitingLogo.this.component.setCursor(Cursor.getPredefinedCursor(3));
               // WaitingLogo.this.statusBar.setVisible(false);
                WaitingLogo.this.progressBar.newIndeterminateProgress(WaitingLogo.this.text);
                WaitingLogo.this.progressBar.setVisible(true);
            }
        });
        int current = 0;
        while (this.waiting) {
            if (++current == this.numImages) {
                current = 0;
            }
            final int icurrent = current;
//            SwingUtilities.invokeLater(new Runnable(){
//
//                @Override
//                public void run() {
//                    WaitingLogo.this.logoCardLayout.show(WaitingLogo.this.logoPanel, String.valueOf(icurrent));
//                }
//            });
            try {
                Thread.sleep(200);
                continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        SwingUtilities.invokeLater(new Runnable(){
//
//            @Override
//            public void run() {
//                WaitingLogo.this.logoCardLayout.show(WaitingLogo.this.logoPanel, "rest");
//            }
//        });
    }

    public static enum Status {
        LOAD_DATA_FILE("Loading a data file", false, true),
        DATA_FILE_LOADED("Data file successfully loaded", true, false),
        DATA_BATCH_LOADED("Nexus batch successfully loaded", true, false),
        DATA_FILE_NOT_LOADED("Data file NOT loaded", true, false),
        SAVE_NEXUS_FILE("Saving parameters to a Nexus file", false, true),
        NEXUS_FILE_SAVED("Nexus file successfully saved", true, false),
        NEXUS_FILE_NOT_SAVED("Nexus file NOT saved", true, false),
        SAVING_PARAMETERS("Saving changes", false, true),
        PARAMETERS_NOT_SAVED("Cannot rebuild the dataset with new parameters", true, false),
        PARAMETERS_SAVED("Settings changes saved", true, false),
        DUPLICATION("Duplicating Nexus file", false, true),
        DUPLICATION_DONE("Nexus file duplicated", true, false),
        DUPLICATION_NOT_DONE("Nexus file NOT duplicated", true, false),
        CHECK_DATASET("Testing dataset", false, true),
        CHECK_DATASET_DONE("Dataset fully tested", true, true),
        CHECK_DATASET_NOT_DONE("The dataset was NOT fully tested", true, false),
        TREE_GENERATION("Generating tree(s)", false, false),
        TREE_GENERATION_DONE("Tree(s) generated", true, false),
        COMPUTING_DISTANCES("Computing distances", false, true),
        COMPUTING_DISTANCES_DONE("Distances computed", true, false);
        
        public String text;
        public final boolean enable;
        public final boolean indeterminate;

        private Status(String text, boolean enable, boolean indeterminate) {
            this.text = text;
            this.enable = enable;
            this.indeterminate = indeterminate;
        }
    }

}


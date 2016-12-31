// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.trees;

import metapiga.ProgressHandling;
import java.util.List;
import metapiga.utilities.Tools;
import metapiga.monitors.InactiveMonitor;
import metapiga.MetaPIGA;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import javax.swing.JOptionPane;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SpinnerNumberModel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import metapiga.parameters.Parameters;
import metapiga.WaitingLogo;

import javax.swing.JFrame;

public class TreeGenerator extends JFrame
{
    private final Parameters P;
    private JPanel evaluationPanel;
    private GridBagLayout gridBagLayout2;
    private JPanel treeGenerationPanel;
    private TitledBorder titledBorder1;
    private TitledBorder titledBorder2;
    private TitledBorder titledBorder3;
    private TitledBorder titledBorder4;
    private JPanel modelPanel;
    JRadioButton modelTN93RadioButton;
    JRadioButton modelK2PRadioButton;
    private GridBagLayout gridBagLayout28;
    JRadioButton modelGTRRadioButton;
    JRadioButton modelGTR2RadioButton;
    JRadioButton modelGTR20RadioButton;
    JRadioButton modelPoissonRadioButton;
    JRadioButton modelHKY85RadioButton;
    JRadioButton modelJCRadioButton;
    JRadioButton distributionGammaRadioButton;
    private JPanel distributionPanel;
    JRadioButton distributionVDPRadioButton;
    JRadioButton distributionNoneRadioButton;
    private GridBagLayout gridBagLayout24;
    private GridBagLayout gridBagLayout3;
    private ButtonGroup treeButtonGroup;
    JRadioButton treeNJTRadioButton;
    JRadioButton treeNJTRandomRadioButton;
    JRadioButton treeTrueRandomRadioButton;
    private ButtonGroup modelButtonGroup;
    private ButtonGroup distributionButtonGroup;
    private ButtonGroup pinvButtonGroup;
    private ButtonGroup piButtonGroup;
    JRadioButton modelNoneRadioButton;
    JSpinner distributionVdpSpinner;
    private JPanel pinvPanel;
    JRadioButton pinvNoneRadioButton;
    JRadioButton pinvValueRadioButton;
    JSpinner pinvSpinner;
    private JPanel gammaValuesPanel;
    private JLabel distributionGammaShapeLabel;
    JTextField distributionGammaShapeTextField;
    private JPanel proportionPanel;
    JLabel pinvProportionLabel;
    JLabel pinvPiLabel;
    JRadioButton pinvEqualRadioButton;
    JRadioButton pinvEstimatedRadioButton;
    JRadioButton pinvConstantRadioButton;
    private JPanel treeGenerationRangePanel;
    private JLabel treeGenerationRangeLabel;
    JSpinner treeGenerationRangeSpinner;
    final JSpinner numberOfTreesSpinner;
    private final JPanel outgroupPanel;
    private final JTextArea txtrUseDatasetSettings;
    private JScrollPane evaluationScroll;
    
    public TreeGenerator(final Parameters parameters) {
        this.evaluationPanel = new JPanel();
        this.gridBagLayout2 = new GridBagLayout();
        this.treeGenerationPanel = new JPanel();
        this.modelPanel = new JPanel();
        this.modelTN93RadioButton = new JRadioButton();
        this.modelK2PRadioButton = new JRadioButton();
        this.gridBagLayout28 = new GridBagLayout();
        this.modelGTRRadioButton = new JRadioButton();
        this.modelGTR2RadioButton = new JRadioButton();
        this.modelGTR20RadioButton = new JRadioButton();
        this.modelPoissonRadioButton = new JRadioButton();
        this.modelHKY85RadioButton = new JRadioButton();
        this.modelJCRadioButton = new JRadioButton();
        this.distributionGammaRadioButton = new JRadioButton();
        this.distributionPanel = new JPanel();
        this.distributionVDPRadioButton = new JRadioButton();
        this.distributionNoneRadioButton = new JRadioButton();
        this.gridBagLayout24 = new GridBagLayout();
        this.gridBagLayout3 = new GridBagLayout();
        this.treeButtonGroup = new ButtonGroup();
        this.treeNJTRadioButton = new JRadioButton();
        this.treeNJTRandomRadioButton = new JRadioButton();
        this.treeTrueRandomRadioButton = new JRadioButton();
        this.modelButtonGroup = new ButtonGroup();
        this.distributionButtonGroup = new ButtonGroup();
        this.pinvButtonGroup = new ButtonGroup();
        this.piButtonGroup = new ButtonGroup();
        this.modelNoneRadioButton = new JRadioButton();
        this.distributionVdpSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 32, 1));
        this.pinvPanel = null;
        this.pinvNoneRadioButton = null;
        this.pinvValueRadioButton = null;
        this.pinvSpinner = null;
        this.gammaValuesPanel = null;
        this.distributionGammaShapeLabel = null;
        this.distributionGammaShapeTextField = null;
        this.proportionPanel = null;
        this.pinvProportionLabel = null;
        this.pinvPiLabel = null;
        this.pinvEqualRadioButton = null;
        this.pinvEstimatedRadioButton = null;
        this.pinvConstantRadioButton = null;
        this.treeGenerationRangePanel = null;
        this.treeGenerationRangeLabel = null;
        this.treeGenerationRangeSpinner = null;
        this.numberOfTreesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        this.outgroupPanel = new JPanel();
        this.txtrUseDatasetSettings = new JTextArea();
        this.evaluationScroll = new JScrollPane();
        this.P = parameters;
        this.setTitle("Generate tree using dataset " + this.P.label);
        this.setDataType();
        final GridBagConstraints gridBagConstraints11 = new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0);
        gridBagConstraints11.gridy = 3;
        final GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 0;
        gridBagConstraints12.anchor = 17;
        gridBagConstraints12.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints12.gridy = 2;
        final GridBagConstraints gridBagConstraints13 = new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0);
        gridBagConstraints13.gridy = 4;
        final GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
        gridBagConstraints14.gridx = 0;
        gridBagConstraints14.fill = 2;
        gridBagConstraints14.weightx = 1.0;
        gridBagConstraints14.gridy = 2;
        final GridBagConstraints gridBagConstraints15 = new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, 17, 0, new Insets(0, 25, 0, 0), 0, 0);
        gridBagConstraints15.insets = new Insets(0, 25, 0, 20);
        gridBagConstraints15.gridy = 5;
        gridBagConstraints15.fill = 2;
        final GridBagConstraints gridBagConstraints16 = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 11, 2, new Insets(0, 5, 5, 5), 0, 0);
        gridBagConstraints16.gridheight = 1;
        final GridBagConstraints gridBagConstraints17 = new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, 11, 2, new Insets(0, 0, 5, 5), 0, 0);
        gridBagConstraints17.weighty = 0.0;
        final GridBagConstraints gridBagConstraints18 = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0);
        gridBagConstraints18.gridx = 3;
        gridBagConstraints18.anchor = 11;
        gridBagConstraints18.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints18.fill = 2;
        gridBagConstraints18.weightx = 1.0;
        gridBagConstraints18.gridy = 0;
        final GridBagConstraints gridBagConstraints19 = new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, 11, 2, new Insets(0, 0, 5, 5), 0, 0);
        gridBagConstraints19.gridx = 1;
        gridBagConstraints19.weighty = 0.0;
        gridBagConstraints19.fill = 2;
        gridBagConstraints19.anchor = 11;
        gridBagConstraints19.gridy = 0;
        this.titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151)), "Tree generation");
        this.titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151)), "Distance matrix");
        this.titledBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151)), "Distribution");
        this.titledBorder4 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151)), "Outgroup");
        this.gridBagLayout2.rowWeights = new double[] { 0.0, 1.0 };
        this.gridBagLayout2.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0 };
        this.evaluationPanel.setLayout(this.gridBagLayout2);
        this.evaluationScroll.setViewportView(this.evaluationPanel);
        this.getContentPane().add(this.evaluationScroll);
        this.treeGenerationPanel.setBorder(this.titledBorder1);
        this.treeGenerationPanel.setLayout(this.gridBagLayout3);
        this.modelPanel.setLayout(this.gridBagLayout28);
        this.modelTN93RadioButton.setText("TN93");
        this.modelK2PRadioButton.setToolTipText("");
        this.modelK2PRadioButton.setText("K2P");
        this.modelGTRRadioButton.setText("GTR");
        this.modelGTR2RadioButton.setText("GTR2");
        this.modelGTR20RadioButton.setText("GTR20");
        this.modelPoissonRadioButton.setText("Poisson");
        this.modelHKY85RadioButton.setText("HKY85");
        this.modelJCRadioButton.setText("JC");
        this.distributionGammaRadioButton.setText("Discrete Gamma");
        this.distributionGammaRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                TreeGenerator.this.distributionGammaRadioButton_itemStateChanged(e);
            }
        });
        this.distributionPanel.setLayout(this.gridBagLayout24);
        this.distributionVDPRadioButton.setText("Van de Peer");
        this.distributionVDPRadioButton.setVisible(false);
        this.distributionVDPRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                TreeGenerator.this.distributionVDPRadioButton_itemStateChanged(e);
            }
        });
        this.distributionNoneRadioButton.setSelected(true);
        this.distributionNoneRadioButton.setText("None");
        this.modelPanel.setBorder(this.titledBorder2);
        this.distributionPanel.setBorder(this.titledBorder3);
        this.treeNJTRadioButton.setSelected(true);
        this.treeNJTRadioButton.setText(Parameters.StartingTreeGeneration.NJ.verbose());
        this.treeNJTRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                TreeGenerator.this.treeNJTRadioButton_itemStateChanged(e);
            }
        });
        this.treeNJTRandomRadioButton.setText(Parameters.StartingTreeGeneration.LNJ.verbose());
        this.treeNJTRandomRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                TreeGenerator.this.treeNJTRandomRadioButton_itemStateChanged(e);
            }
        });
        this.treeTrueRandomRadioButton.setText(Parameters.StartingTreeGeneration.RANDOM.verbose());
        this.treeTrueRandomRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                TreeGenerator.this.treeTrueRandomRadioButton_itemStateChanged(e);
            }
        });
        this.modelNoneRadioButton.setEnabled(false);
        this.modelNoneRadioButton.setText("None");
        this.distributionVdpSpinner.setEnabled(false);
        this.distributionVdpSpinner.setPreferredSize(new Dimension(70, 18));
        this.distributionVdpSpinner.setVisible(false);
        this.modelPanel.add(this.modelGTRRadioButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, 18, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelTN93RadioButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelHKY85RadioButton, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelK2PRadioButton, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelJCRadioButton, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelGTR2RadioButton, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelGTR20RadioButton, new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelPoissonRadioButton, new GridBagConstraints(0, 7, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.modelPanel.add(this.modelNoneRadioButton, new GridBagConstraints(0, 8, 1, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.evaluationPanel.add(this.treeGenerationPanel, gridBagConstraints16);
        this.evaluationPanel.add(this.modelPanel, gridBagConstraints19);
        this.evaluationPanel.add(this.distributionPanel, gridBagConstraints17);
        this.evaluationPanel.add(this.getPinvPanel(), gridBagConstraints18);
        this.distributionPanel.add(this.distributionNoneRadioButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.distributionPanel.add(this.distributionGammaRadioButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.distributionPanel.add(this.distributionVDPRadioButton, gridBagConstraints13);
        this.distributionPanel.add(this.distributionVdpSpinner, gridBagConstraints15);
        this.distributionPanel.add(this.getGammaValuesPanel(), gridBagConstraints14);
        this.treeGenerationPanel.add(this.treeNJTRadioButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.treeGenerationPanel.add(this.treeNJTRandomRadioButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.treeGenerationPanel.add(this.treeTrueRandomRadioButton, gridBagConstraints11);
        this.treeGenerationPanel.add(this.getTreeGenerationRangePanel(), gridBagConstraints12);
        this.treeButtonGroup.add(this.treeNJTRadioButton);
        this.treeButtonGroup.add(this.treeNJTRandomRadioButton);
        this.treeButtonGroup.add(this.treeTrueRandomRadioButton);
        this.modelButtonGroup.add(this.modelGTRRadioButton);
        this.modelButtonGroup.add(this.modelTN93RadioButton);
        this.modelButtonGroup.add(this.modelHKY85RadioButton);
        this.modelButtonGroup.add(this.modelK2PRadioButton);
        this.modelButtonGroup.add(this.modelJCRadioButton);
        this.modelButtonGroup.add(this.modelGTR2RadioButton);
        this.modelButtonGroup.add(this.modelGTR20RadioButton);
        this.modelButtonGroup.add(this.modelPoissonRadioButton);
        this.modelButtonGroup.add(this.modelNoneRadioButton);
        this.distributionButtonGroup.add(this.distributionNoneRadioButton);
        this.distributionButtonGroup.add(this.distributionGammaRadioButton);
        this.distributionButtonGroup.add(this.distributionVDPRadioButton);
        this.pinvButtonGroup.add(this.pinvNoneRadioButton);
        this.pinvButtonGroup.add(this.pinvValueRadioButton);
        this.piButtonGroup.add(this.pinvEqualRadioButton);
        this.piButtonGroup.add(this.pinvEstimatedRadioButton);
        this.piButtonGroup.add(this.pinvConstantRadioButton);
        this.outgroupPanel.setBorder(this.titledBorder4);
        final GridBagConstraints gbc_outgroupPanel = new GridBagConstraints();
        gbc_outgroupPanel.fill = 1;
        gbc_outgroupPanel.gridwidth = 4;
        gbc_outgroupPanel.anchor = 11;
        gbc_outgroupPanel.insets = new Insets(0, 5, 0, 5);
        gbc_outgroupPanel.gridx = 0;
        gbc_outgroupPanel.gridy = 1;
        this.evaluationPanel.add(this.outgroupPanel, gbc_outgroupPanel);
        this.outgroupPanel.setLayout(new BorderLayout(0, 0));
        this.txtrUseDatasetSettings.setLineWrap(true);
        this.txtrUseDatasetSettings.setFont(new Font("Tahoma", 0, 11));
        this.txtrUseDatasetSettings.setWrapStyleWord(true);
        String outgroup = "Use dataset settings to change outgroup and excluded taxa.";
        if (this.P.outgroup.size() > 0) {
            outgroup = String.valueOf(outgroup) + " Current outgroup: ";
            final Iterator<String> it = this.P.outgroup.iterator();
            while (it.hasNext()) {
                outgroup = String.valueOf(outgroup) + it.next();
                if (it.hasNext()) {
                    outgroup = String.valueOf(outgroup) + ", ";
                }
            }
            outgroup = String.valueOf(outgroup) + ".";
        }
        else {
            outgroup = String.valueOf(outgroup) + " No outgroup is currently defined.";
        }
        this.txtrUseDatasetSettings.setText(outgroup);
        this.txtrUseDatasetSettings.setBackground(UIManager.getColor("Panel.background"));
        this.txtrUseDatasetSettings.setEditable(false);
        this.outgroupPanel.add(this.txtrUseDatasetSettings, "North");
        final JPanel panel = new JPanel();
        this.getContentPane().add(panel, "South");
        final JButton generateTreeButton = new JButton();
        generateTreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                TreeGenerator.this.dispose();
                final GenerateTrees save = new GenerateTrees(Integer.parseInt(TreeGenerator.this.numberOfTreesSpinner.getModel().getValue().toString()));
                save.execute();
            }
        });
        generateTreeButton.setText("Generate");
        panel.add(generateTreeButton);
        this.numberOfTreesSpinner.setPreferredSize(new Dimension(70, 18));
        panel.add(this.numberOfTreesSpinner);
        final JLabel treesLabel = new JLabel();
        treesLabel.setText("trees");
        panel.add(treesLabel);
    }
    
    private void setDataType() {
        switch (this.P.dataset.getDataType()) {
            case DNA: {
                this.modelTN93RadioButton.setVisible(true);
                this.modelK2PRadioButton.setVisible(true);
                this.modelGTRRadioButton.setVisible(true);
                this.modelHKY85RadioButton.setVisible(true);
                this.modelJCRadioButton.setVisible(true);
                this.modelGTR20RadioButton.setVisible(false);
                this.modelGTR2RadioButton.setVisible(false);
                this.modelPoissonRadioButton.setVisible(false);
                this.modelJCRadioButton.setSelected(true);
                break;
            }
            case PROTEIN: {
                this.modelTN93RadioButton.setVisible(false);
                this.modelK2PRadioButton.setVisible(false);
                this.modelGTRRadioButton.setVisible(false);
                this.modelHKY85RadioButton.setVisible(false);
                this.modelJCRadioButton.setVisible(false);
                this.modelGTR20RadioButton.setVisible(true);
                this.modelGTR2RadioButton.setVisible(false);
                this.modelPoissonRadioButton.setVisible(true);
                this.modelPoissonRadioButton.setSelected(true);
                break;
            }
            case STANDARD: {
                this.modelTN93RadioButton.setVisible(false);
                this.modelK2PRadioButton.setVisible(false);
                this.modelGTRRadioButton.setVisible(false);
                this.modelHKY85RadioButton.setVisible(false);
                this.modelJCRadioButton.setVisible(false);
                this.modelGTR20RadioButton.setVisible(false);
                this.modelGTR2RadioButton.setVisible(true);
                this.modelPoissonRadioButton.setVisible(false);
                this.modelGTR2RadioButton.setSelected(true);
                break;
            }
        }
    }
    
    private JPanel getPinvPanel() {
        if (this.pinvPanel == null) {
            final GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
            gridBagConstraints51.gridx = 0;
            gridBagConstraints51.insets = new Insets(0, 25, 0, 0);
            gridBagConstraints51.fill = 2;
            gridBagConstraints51.gridy = 6;
            final GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
            gridBagConstraints52.gridx = 0;
            gridBagConstraints52.insets = new Insets(0, 25, 0, 0);
            gridBagConstraints52.fill = 2;
            gridBagConstraints52.gridy = 5;
            final GridBagConstraints gridBagConstraints53 = new GridBagConstraints();
            gridBagConstraints53.gridx = 0;
            gridBagConstraints53.fill = 2;
            gridBagConstraints53.insets = new Insets(0, 25, 0, 0);
            gridBagConstraints53.gridy = 4;
            final GridBagConstraints gridBagConstraints54 = new GridBagConstraints();
            gridBagConstraints54.gridx = 0;
            gridBagConstraints54.insets = new Insets(5, 20, 0, 0);
            gridBagConstraints54.fill = 2;
            gridBagConstraints54.weightx = 0.0;
            gridBagConstraints54.gridy = 3;
            (this.pinvPiLabel = new JLabel()).setText("Base composition");
            this.pinvPiLabel.setEnabled(false);
            this.pinvPiLabel.setDisplayedMnemonic(0);
            final GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
            gridBagConstraints55.gridx = 0;
            gridBagConstraints55.anchor = 17;
            gridBagConstraints55.gridy = 2;
            final GridBagConstraints gridBagConstraints56 = new GridBagConstraints(0, 8, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 20, 0, 0), 0, 0);
            gridBagConstraints56.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints56.gridy = 1;
            gridBagConstraints56.gridx = 0;
            (this.pinvPanel = new JPanel()).setLayout(new GridBagLayout());
            this.pinvPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 163, 151)), "Invariable sites"));
            this.pinvPanel.add(this.getPinvNoneRadioButton(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
            this.pinvPanel.add(this.getPinvValueRadioButton(), gridBagConstraints56);
            this.pinvPanel.add(this.getProportionPanel(), gridBagConstraints55);
            this.pinvPanel.add(this.pinvPiLabel, gridBagConstraints54);
            this.pinvPanel.add(this.getPinvEqualRadioButton(), gridBagConstraints53);
            this.pinvPanel.add(this.getPinvEstimatedRadioButton(), gridBagConstraints52);
            this.pinvPanel.add(this.getPinvConstantRadioButton(), gridBagConstraints51);
        }
        return this.pinvPanel;
    }
    
    private JRadioButton getPinvNoneRadioButton() {
        if (this.pinvNoneRadioButton == null) {
            (this.pinvNoneRadioButton = new JRadioButton()).setSelected(true);
            this.pinvNoneRadioButton.setText("None");
        }
        return this.pinvNoneRadioButton;
    }
    
    private JRadioButton getPinvValueRadioButton() {
        if (this.pinvValueRadioButton == null) {
            (this.pinvValueRadioButton = new JRadioButton()).setText("P-Invariant");
            this.pinvValueRadioButton.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(final ItemEvent e) {
                    if (TreeGenerator.this.pinvValueRadioButton.isSelected()) {
                        TreeGenerator.this.pinvSpinner.setEnabled(true);
                        TreeGenerator.this.pinvProportionLabel.setEnabled(true);
                        TreeGenerator.this.pinvPiLabel.setEnabled(true);
                        TreeGenerator.this.pinvConstantRadioButton.setEnabled(true);
                        TreeGenerator.this.pinvEstimatedRadioButton.setEnabled(true);
                        TreeGenerator.this.pinvEqualRadioButton.setEnabled(true);
                    }
                    else {
                        TreeGenerator.this.pinvSpinner.setEnabled(false);
                        TreeGenerator.this.pinvProportionLabel.setEnabled(false);
                        TreeGenerator.this.pinvPiLabel.setEnabled(false);
                        TreeGenerator.this.pinvConstantRadioButton.setEnabled(false);
                        TreeGenerator.this.pinvEstimatedRadioButton.setEnabled(false);
                        TreeGenerator.this.pinvEqualRadioButton.setEnabled(false);
                    }
                }
            });
        }
        return this.pinvValueRadioButton;
    }
    
    private JSpinner getPinvSpinner() {
        if (this.pinvSpinner == null) {
            (this.pinvSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1))).setEnabled(false);
            this.pinvSpinner.setPreferredSize(new Dimension(70, 18));
        }
        return this.pinvSpinner;
    }
    
    private JPanel getGammaValuesPanel() {
        if (this.gammaValuesPanel == null) {
            final GridBagConstraints gridBagConstraints8 = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 5, 0, 5), 0, 0);
            gridBagConstraints8.fill = 2;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.ipadx = 0;
            gridBagConstraints8.insets = new Insets(0, 5, 0, 20);
            final GridBagConstraints gridBagConstraints9 = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 5, 0, 5), 0, 0);
            gridBagConstraints9.anchor = 17;
            gridBagConstraints9.gridy = 1;
            gridBagConstraints9.insets = new Insets(0, 25, 0, 5);
            (this.distributionGammaShapeLabel = new JLabel()).setEnabled(false);
            this.distributionGammaShapeLabel.setPreferredSize(new Dimension(40, 15));
            this.distributionGammaShapeLabel.setText("shape");
            this.distributionGammaShapeLabel.setMaximumSize(new Dimension(260, 15));
            (this.gammaValuesPanel = new JPanel()).setLayout(new GridBagLayout());
            this.gammaValuesPanel.add(this.distributionGammaShapeLabel, gridBagConstraints9);
            this.gammaValuesPanel.add(this.getDistributionGammaStartTextField(), gridBagConstraints8);
        }
        return this.gammaValuesPanel;
    }
    
    private JTextField getDistributionGammaStartTextField() {
        if (this.distributionGammaShapeTextField == null) {
            (this.distributionGammaShapeTextField = new JTextField()).setEnabled(false);
            this.distributionGammaShapeTextField.setText("0.5");
            this.distributionGammaShapeTextField.setPreferredSize(new Dimension(70, 20));
            this.distributionGammaShapeTextField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(final FocusEvent e) {
                    try {
                        final Double d = Double.parseDouble(TreeGenerator.this.distributionGammaShapeTextField.getText());
                        if (d <= 0.0) {
                            throw new NumberFormatException(d + " is a not a non-zero positive number");
                        }
                    }
                    catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error : " + TreeGenerator.this.distributionGammaShapeTextField.getText() + " is not a valid positive number. \nShape parameter is reset to 0.5.", "Gamma shape parameter", 0);
                        TreeGenerator.this.distributionGammaShapeTextField.setText("0.5");
                    }
                }
            });
        }
        return this.distributionGammaShapeTextField;
    }
    
    private JPanel getProportionPanel() {
        if (this.proportionPanel == null) {
            (this.pinvProportionLabel = new JLabel()).setText("Proportion");
            this.pinvProportionLabel.setEnabled(false);
            final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(0, 20, 0, 5);
            gridBagConstraints5.gridy = 0;
            final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = 2;
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.insets = new Insets(0, 0, 0, 5);
            this.proportionPanel = new JPanel();
            final GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[3];
            this.proportionPanel.setLayout(gridBagLayout);
            this.proportionPanel.add(this.pinvProportionLabel, gridBagConstraints5);
            this.proportionPanel.add(this.getPinvSpinner(), gridBagConstraints6);
            final JLabel percentPinvLabel = new JLabel();
            percentPinvLabel.setText("%");
            final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.gridx = 2;
            this.proportionPanel.add(percentPinvLabel, gridBagConstraints7);
        }
        return this.proportionPanel;
    }
    
    private JRadioButton getPinvEqualRadioButton() {
        if (this.pinvEqualRadioButton == null) {
            (this.pinvEqualRadioButton = new JRadioButton()).setText("Equal");
            this.pinvEqualRadioButton.setEnabled(false);
        }
        return this.pinvEqualRadioButton;
    }
    
    private JRadioButton getPinvEstimatedRadioButton() {
        if (this.pinvEstimatedRadioButton == null) {
            (this.pinvEstimatedRadioButton = new JRadioButton()).setText("Estimated");
            this.pinvEstimatedRadioButton.setEnabled(false);
        }
        return this.pinvEstimatedRadioButton;
    }
    
    private JRadioButton getPinvConstantRadioButton() {
        if (this.pinvConstantRadioButton == null) {
            (this.pinvConstantRadioButton = new JRadioButton()).setText("Constant");
            this.pinvConstantRadioButton.setEnabled(false);
            this.pinvConstantRadioButton.setSelected(true);
        }
        return this.pinvConstantRadioButton;
    }
    
    private JPanel getTreeGenerationRangePanel() {
        if (this.treeGenerationRangePanel == null) {
            final GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 1;
            gridBagConstraints16.insets = new Insets(0, 5, 0, 5);
            gridBagConstraints16.fill = 2;
            gridBagConstraints16.gridy = 0;
            final GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.insets = new Insets(0, 20, 0, 0);
            gridBagConstraints17.gridy = 0;
            (this.treeGenerationRangeLabel = new JLabel()).setText("Range");
            this.treeGenerationRangePanel = new JPanel();
            final GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[3];
            this.treeGenerationRangePanel.setLayout(gridBagLayout);
            this.treeGenerationRangePanel.add(this.treeGenerationRangeLabel, gridBagConstraints17);
            this.treeGenerationRangePanel.add(this.getTreeGenerationRangeSpinner(), gridBagConstraints16);
            final JLabel rangeLabel = new JLabel();
            rangeLabel.setText("%");
            final GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridy = 0;
            gridBagConstraints18.gridx = 2;
            this.treeGenerationRangePanel.add(rangeLabel, gridBagConstraints18);
        }
        return this.treeGenerationRangePanel;
    }
    
    private JSpinner getTreeGenerationRangeSpinner() {
        if (this.treeGenerationRangeSpinner == null) {
            (this.treeGenerationRangeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 5))).setEnabled(false);
            this.treeGenerationRangeSpinner.setPreferredSize(new Dimension(70, 18));
        }
        return this.treeGenerationRangeSpinner;
    }
    
    void disableModelOnCriterion() {
        if (!this.treeTrueRandomRadioButton.isSelected()) {
            this.modelGTRRadioButton.setEnabled(true);
            this.modelHKY85RadioButton.setEnabled(true);
            this.modelTN93RadioButton.setEnabled(true);
            this.modelJCRadioButton.setEnabled(true);
            this.modelK2PRadioButton.setEnabled(true);
            this.modelNoneRadioButton.setEnabled(false);
            if (this.modelNoneRadioButton.isSelected()) {
                this.modelJCRadioButton.setSelected(true);
            }
            this.distributionGammaRadioButton.setEnabled(true);
            this.distributionVDPRadioButton.setEnabled(true);
            this.pinvValueRadioButton.setEnabled(true);
            if (this.pinvValueRadioButton.isSelected()) {
                this.pinvSpinner.setEnabled(true);
            }
        }
        else {
            this.modelGTRRadioButton.setEnabled(false);
            this.modelHKY85RadioButton.setEnabled(false);
            this.modelTN93RadioButton.setEnabled(false);
            this.modelJCRadioButton.setEnabled(false);
            this.modelK2PRadioButton.setEnabled(false);
            this.modelNoneRadioButton.setEnabled(true);
            this.modelNoneRadioButton.setSelected(true);
            this.distributionGammaRadioButton.setEnabled(false);
            this.distributionVDPRadioButton.setEnabled(false);
            this.pinvValueRadioButton.setEnabled(false);
            this.pinvSpinner.setEnabled(false);
            this.distributionNoneRadioButton.setSelected(true);
            this.pinvNoneRadioButton.setSelected(true);
        }
    }
    
    void treeNJTRadioButton_itemStateChanged(final ItemEvent e) {
        this.disableModelOnCriterion();
    }
    
    void treeNJTRandomRadioButton_itemStateChanged(final ItemEvent e) {
        this.disableModelOnCriterion();
        if (this.treeNJTRandomRadioButton.isSelected()) {
            this.treeGenerationRangeLabel.setEnabled(true);
            this.treeGenerationRangeSpinner.setEnabled(true);
        }
        else {
            this.treeGenerationRangeLabel.setEnabled(false);
            this.treeGenerationRangeSpinner.setEnabled(false);
        }
    }
    
    void treeTrueRandomRadioButton_itemStateChanged(final ItemEvent e) {
        this.disableModelOnCriterion();
    }
    
    void distributionGammaRadioButton_itemStateChanged(final ItemEvent e) {
        if (this.distributionGammaRadioButton.isSelected()) {
            this.distributionGammaShapeLabel.setEnabled(true);
            this.distributionGammaShapeTextField.setEnabled(true);
        }
        else {
            this.distributionGammaShapeTextField.setEnabled(false);
            this.distributionGammaShapeLabel.setEnabled(false);
        }
    }
    
    void distributionVDPRadioButton_itemStateChanged(final ItemEvent e) {
        if (this.distributionVDPRadioButton.isSelected()) {
            this.distributionVdpSpinner.setEnabled(true);
        }
        else {
            this.distributionVdpSpinner.setEnabled(false);
        }
    }
    
    private class GenerateTrees extends SwingWorker<WaitingLogo.Status, Object>
    {
        private final int treeNum;
        
        public GenerateTrees(final int treeNum) {
            this.treeNum = treeNum;
        }
        
        public WaitingLogo.Status doInBackground() {
            Parameters.StartingTreeGeneration generation = Parameters.StartingTreeGeneration.NJ;
            double startingTreeRange = 0.1;
            Parameters.DistanceModel model = Parameters.DistanceModel.GTR;
            Parameters.StartingTreeDistribution distribution = Parameters.StartingTreeDistribution.NONE;
            double distributionShape = 1.0;
            double pinv = 0.0;
            Parameters.StartingTreePInvPi pi = Parameters.StartingTreePInvPi.ESTIMATED;
            if (TreeGenerator.this.treeNJTRadioButton.isSelected()) {
                generation = Parameters.StartingTreeGeneration.NJ;
            }
            else if (TreeGenerator.this.treeNJTRandomRadioButton.isSelected()) {
                generation = Parameters.StartingTreeGeneration.LNJ;
                startingTreeRange = Double.parseDouble(TreeGenerator.this.getTreeGenerationRangeSpinner().getModel().getValue().toString()) / 100.0;
            }
            else if (TreeGenerator.this.treeTrueRandomRadioButton.isSelected()) {
                generation = Parameters.StartingTreeGeneration.RANDOM;
            }
            if (TreeGenerator.this.modelGTRRadioButton.isSelected()) {
                model = Parameters.DistanceModel.GTR;
            }
            else if (TreeGenerator.this.modelGTR2RadioButton.isSelected()) {
                model = Parameters.DistanceModel.GTR2;
            }
            else if (TreeGenerator.this.modelGTR20RadioButton.isSelected()) {
                model = Parameters.DistanceModel.GTR20;
            }
            else if (TreeGenerator.this.modelPoissonRadioButton.isSelected()) {
                model = Parameters.DistanceModel.POISSON;
            }
            else if (TreeGenerator.this.modelTN93RadioButton.isSelected()) {
                model = Parameters.DistanceModel.TN93;
            }
            else if (TreeGenerator.this.modelHKY85RadioButton.isSelected()) {
                model = Parameters.DistanceModel.HKY85;
            }
            else if (TreeGenerator.this.modelK2PRadioButton.isSelected()) {
                model = Parameters.DistanceModel.K2P;
            }
            else if (TreeGenerator.this.modelJCRadioButton.isSelected()) {
                model = Parameters.DistanceModel.JC;
            }
            else if (TreeGenerator.this.modelNoneRadioButton.isSelected()) {
                model = Parameters.DistanceModel.NONE;
            }
            if (TreeGenerator.this.distributionNoneRadioButton.isSelected()) {
                distribution = Parameters.StartingTreeDistribution.NONE;
            }
            else if (TreeGenerator.this.distributionGammaRadioButton.isSelected()) {
                distribution = Parameters.StartingTreeDistribution.GAMMA;
                distributionShape = new Double(TreeGenerator.this.distributionGammaShapeTextField.getText());
            }
            else if (TreeGenerator.this.distributionVDPRadioButton.isSelected()) {
                distribution = Parameters.StartingTreeDistribution.VDP;
                distributionShape = new Integer(TreeGenerator.this.distributionVdpSpinner.getModel().getValue().toString());
            }
            if (TreeGenerator.this.pinvValueRadioButton.isSelected()) {
                pinv = Double.parseDouble(TreeGenerator.this.pinvSpinner.getValue().toString()) / 100.0;
            }
            else if (TreeGenerator.this.pinvNoneRadioButton.isSelected()) {
                pinv = 0.0;
            }
            if (TreeGenerator.this.pinvEqualRadioButton.isSelected()) {
                pi = Parameters.StartingTreePInvPi.EQUAL;
            }
            else if (TreeGenerator.this.pinvEstimatedRadioButton.isSelected()) {
                pi = Parameters.StartingTreePInvPi.ESTIMATED;
            }
            else if (TreeGenerator.this.pinvConstantRadioButton.isSelected()) {
                pi = Parameters.StartingTreePInvPi.CONSTANT;
            }
            final List<Tree> trees = new ArrayList<Tree>();
            final ProgressHandling progress = MetaPIGA.progressHandling;
            progress.newSingleProgress(0, this.treeNum, "Generating tree");
            for (int i = 0; i < this.treeNum; ++i) {
                progress.setValue(i + 1);
                try {
                    final Tree tree = TreeGenerator.this.P.dataset.generateTree(TreeGenerator.this.P.outgroup, generation, startingTreeRange, model, distribution, distributionShape, pinv, pi, TreeGenerator.this.P, new InactiveMonitor());
                    if (this.treeNum > 1) {
                        tree.setName(String.valueOf(tree.getName()) + "_" + (i + 1));
                    }
                    trees.add(tree);
                    //MetaPIGA.treeViewer.addTree(tree, TreeGenerator.this.P);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, Tools.getErrorPanel("Cannot build tree", e), "Tree building Error", 0);
                }
            }
           // MetaPIGA.treeViewer.setSelectedTrees(trees);
            //MetaPIGA.treeViewer.setVisible(true);
            return WaitingLogo.Status.TREE_GENERATION_DONE;
        }
        
        public void done() {

        }
    }
}

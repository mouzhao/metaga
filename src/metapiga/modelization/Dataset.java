package metapiga.modelization;

import java.util.TreeSet;
import java.util.HashMap;
import javax.swing.JProgressBar;
import java.util.Arrays;
import java.awt.Point;
import metapiga.trees.exceptions.TooManyNeighborsException;
import metapiga.trees.exceptions.UnknownTaxonException;
import metapiga.trees.exceptions.UncompatibleOutgroupException;
import java.awt.Component;
import javax.swing.JOptionPane;
import cern.jet.random.Exponential;
import metapiga.trees.Node;
import metapiga.exceptions.OutgroupTooBigException;
import metapiga.trees.Tree;
import metapiga.monitors.Monitor;
import java.util.HashSet;
import metapiga.ProgressHandling;
import java.util.TreeMap;
import metapiga.MetaPIGA;
import metapiga.utilities.Tools;
import metapiga.exceptions.IncompatibleDataException;
import java.util.Collection;
import metapiga.modelization.data.codons.tables.CodonTransitionTable;
import java.util.Iterator;
import metapiga.exceptions.NexusInconsistencyException;
import java.util.LinkedList;
import metapiga.modelization.data.Data;
import metapiga.exceptions.UnknownDataException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import metapiga.parameters.Parameters;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import metapiga.modelization.data.DataType;

public class Dataset
{
    private boolean hasSaturation;
    private final DataType dataType;
    private final Map<Charset, Partition> data;
    private final List<String> taxas;
    private final String longestTaxon;
    private final BitSet gaps;
    private final BitSet ngaps;
    private final int fullNChar;
    private DistanceMatrix D;
    Parameters.DistanceModel DMmodel;
    Parameters.StartingTreeDistribution DMdistribution;
    double DMdistributionShape;
    double DMpinv;
    Parameters.StartingTreePInvPi DMpi;

    public Dataset(final CharactersBlock block, final Set<String> deletedTaxa, final Set<Charset> excludedCharsets, final Set<Charset> partitions, final Parameters.ColumnRemoval columnRemoval) throws UnknownDataException, NexusInconsistencyException {
        this.hasSaturation = false;
        this.data = new LinkedHashMap<Charset, Partition>();
        this.taxas = new ArrayList<String>();
        this.D = null;
        if (block.getDataType().toUpperCase().equals("NUCLEOTIDES")) {
            this.dataType = DataType.DNA;
        }
        else {
            if (block.getDataType().toUpperCase().equals("RNA")) {
                throw new UnknownDataException("RNA");
            }
            this.dataType = DataType.valueOf(block.getDataType().toUpperCase());
        }
        this.fullNChar = block.getDimensionsNChar();
        final String matchSymbol = (block.getMatchChar() == null) ? "." : block.getMatchChar();
        final String missingSymbol = (block.getMissing() == null) ? "." : block.getMissing();
        final String gapSymbol = (block.getGap() == null) ? "." : block.getGap();
        final Map<String, List<Data>> matrix = new LinkedHashMap<String, List<Data>>();
        this.gaps = new BitSet();
        this.ngaps = new BitSet();
        String firstTaxon = null;
        for (final Object taxon : block.getMatrixLabels()) {
            final List<Data> seq = new LinkedList<Data>();
            if (firstTaxon == null) {
                firstTaxon = taxon.toString();
            }
            for (final Object obj : block.getMatrixData(taxon.toString())) {
                final String nucl = obj.toString();
                if (nucl.length() > 0) {
                    if (nucl.length() > 1) {
                        final BitSet bitSet = new BitSet(this.dataType.numOfStates());
                        char[] charArray;
                        for (int length = (charArray = nucl.toCharArray()).length, i = 0; i < length; ++i) {
                            final char c = charArray[i];
                            bitSet.set(this.dataType.getStateOf(new StringBuilder().append(c).toString()));
                        }
                        seq.add(this.dataType.getData(bitSet));
                    }
                    else if (nucl.equals(matchSymbol) || (block.isRespectCase() && nucl.equalsIgnoreCase(matchSymbol))) {
                        if (firstTaxon == null) {
                            throw new NexusInconsistencyException("You cannot use MATCHCHAR symbol on first line !");
                        }
                        final Data d = matrix.get(firstTaxon).get(seq.size());
                        if (d.isUndeterminate()) {
                            this.ngaps.set(seq.size());
                        }
                        seq.add(d);
                    }
                    else if (nucl.equals(missingSymbol) || (block.isRespectCase() && nucl.equalsIgnoreCase(missingSymbol))) {
                        this.ngaps.set(seq.size());
                        seq.add(this.dataType.getUndeterminateData());
                    }
                    else if (nucl.equals(gapSymbol) || (block.isRespectCase() && nucl.equalsIgnoreCase(gapSymbol))) {
                        this.gaps.set(seq.size());
                        this.ngaps.set(seq.size());
                        seq.add(this.dataType.getUndeterminateData());
                    }
                    else {
                        try {
                            final Data d = this.dataType.getData(nucl.toUpperCase());
                            if (d.isUndeterminate()) {
                                this.ngaps.set(seq.size());
                            }
                            seq.add(d);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            throw new UnknownDataException(nucl, taxon.toString());
                        }
                    }
                }
            }
            matrix.put(taxon.toString(), seq);
            if (matrix.get(firstTaxon).size() != seq.size()) {
                throw new NexusInconsistencyException("Line " + taxon + " has a size of " + seq.size() + ", and should have " + matrix.get(firstTaxon).size() + " as first line");
            }
        }
        this.handleDataPartitioning(deletedTaxa, excludedCharsets, partitions, columnRemoval, matrix);
        String temp = "";
        for (final String taxa : this.taxas) {
            if (taxa.length() > temp.length()) {
                temp = taxa;
            }
        }
        this.longestTaxon = temp;
    }

    private void handleDataPartitioning(final Set<String> deletedTaxa, final Set<Charset> excludedCharsets, final Set<Charset> partitions, final Parameters.ColumnRemoval columnRemoval, final Map<String, List<Data>> matrix) {
        for (final Charset charset : partitions) {
            final Map<String, List<Data>> temp = new LinkedHashMap<String, List<Data>>();
            final boolean needMapping = true;
            final List<Integer> mapping = new LinkedList<Integer>();
            for (final Map.Entry<String, List<Data>> e : matrix.entrySet()) {
                final String taxa = e.getKey();
                if (!deletedTaxa.contains(taxa)) {
                    final List<Data> seq = new LinkedList<Data>();
                    for (int i = 0; i < e.getValue().size(); ++i) {
                        boolean exclude = false;
                        if (columnRemoval == Parameters.ColumnRemoval.GAP) {
                            exclude = this.gaps.get(i);
                        }
                        else if (columnRemoval == Parameters.ColumnRemoval.NGAP) {
                            exclude = this.ngaps.get(i);
                        }
                        if (!exclude) {
                            for (final Charset ch : excludedCharsets) {
                                if (ch.isInCharset(i + 1)) {
                                    exclude = true;
                                    break;
                                }
                            }
                        }
                        if (!exclude && charset.isInCharset(i + 1)) {
                            seq.add(e.getValue().get(i));
                            if (needMapping) {
                                mapping.add(i);
                            }
                        }
                    }
                    temp.put(taxa, seq);
                    if (this.taxas.contains(taxa)) {
                        continue;
                    }
                    this.taxas.add(taxa);
                }
            }
            this.data.put(charset, new Partition(this.dataType, this.taxas, temp, mapping));
        }
    }

    public Dataset(final CodonCharactersBlock block, final Set<String> deletedTaxa, final Set<Charset> excludedCharsets, final Set<Charset> partitions, final Parameters.ColumnRemoval columnRemoval, final CodonTransitionTable transTable) throws UnknownDataException, NexusInconsistencyException {
        this.hasSaturation = false;
        this.data = new LinkedHashMap<Charset, Partition>();
        this.taxas = new ArrayList<String>();
        this.dataType = DataType.CODON;
        this.fullNChar = block.getDimensionsNChar();
        this.gaps = new BitSet();
        this.ngaps = new BitSet();
        this.handleDataPartitioning(deletedTaxa, excludedCharsets, partitions, columnRemoval, block.matrix);
        String temp = "";
        for (final String taxa : this.taxas) {
            if (taxa.length() > temp.length()) {
                temp = taxa;
            }
        }
        this.longestTaxon = temp;
    }

    public Dataset(final Dataset D) {
        this.hasSaturation = false;
        this.dataType = D.dataType;
        this.data = new LinkedHashMap<Charset, Partition>();
        for (final Map.Entry<Charset, Partition> e : D.data.entrySet()) {
            this.data.put(e.getKey(), new Partition(e.getValue()));
        }
        (this.taxas = new ArrayList<String>()).addAll(D.taxas);
        this.longestTaxon = D.longestTaxon;
        (this.gaps = new BitSet(D.gaps.length())).or(D.gaps);
        (this.ngaps = new BitSet(D.ngaps.length())).or(D.ngaps);
        this.fullNChar = D.fullNChar;
    }

    private Dataset(final Dataset D, final int firstCodonPosition, final int lastCodonPostition) throws IncompatibleDataException {
        this.hasSaturation = false;
        if (D.dataType != DataType.DNA) {
            throw new IncompatibleDataException(DataType.DNA, D.dataType);
        }
        this.dataType = DataType.CODON;
        (this.taxas = new ArrayList<String>()).addAll(D.taxas);
        this.longestTaxon = D.longestTaxon;
        (this.gaps = new BitSet(D.gaps.length())).or(D.gaps);
        (this.ngaps = new BitSet(D.ngaps.length())).or(D.ngaps);
        this.fullNChar = D.fullNChar;
        this.data = new LinkedHashMap<Charset, Partition>();
    }

    public Dataset makeCodonDataset(final Parameters.CodonDomainDefinition codonDefinition) {
        try {
            return new Dataset(this, codonDefinition.getStartCodonDomainPosition(), codonDefinition.getEndCodonDomainPosition());
        }
        catch (IncompatibleDataException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public String getTaxon(final int index) {
        return this.taxas.get(index);
    }

    public String getLongestTaxon() {
        return this.longestTaxon;
    }

    public boolean hasGapAtPos(final int pos) {
        return this.gaps.get(pos);
    }

    public boolean hasGapOrNAtPos(final int pos) {
        return this.ngaps.get(pos);
    }

    public List<String> getTaxa() {
        return this.taxas;
    }

    public int getNTax() {
        return this.taxas.size();
    }

    public Partition getPartition(final Charset partition) {
        return this.data.get(partition);
    }

    public Collection<Partition> getPartitions() {
        return this.data.values();
    }

    public Set<Charset> getPartitionCharsets() {
        return this.data.keySet();
    }

    public Charset getCharset(final String label) throws Exception {
        for (final Charset c : this.data.keySet()) {
            if (c.getLabel().equals(label)) {
                return c;
            }
        }
        throw new Exception("Unknown charset: " + label);
    }

    public int getFullNChar() {
        return this.fullNChar;
    }

    public int getNChar() {
        int nchar = 0;
        for (final Partition p : this.data.values()) {
            nchar += p.getNChar();
        }
        return nchar;
    }

    public int getCompressedNChar() {
        int nchar = 0;
        for (final Partition p : this.data.values()) {
            nchar += p.getCompressedNChar();
        }
        return nchar;
    }

    public double[] getDataFrequencies() {
        final double[] frequencies = new double[this.dataType.numOfStates()];
        for (int i = 0; i < this.dataType.numOfStates(); ++i) {
            frequencies[i] = 0.0;
        }
        final int nchar = this.getNChar();
        for (final Partition p : this.data.values()) {
            final double[] charsetFreq = p.getDataFrequencies();
            for (int j = 0; j < this.dataType.numOfStates(); ++j) {
                final double[] array = frequencies;
                final int n = j;
                array[n] += charsetFreq[j] * p.nchar / nchar;
            }
        }
        return frequencies;
    }

    public String getDataFrequenciesToString() throws UnknownDataException {
        final double[] freq = this.getDataFrequencies();
        String s = "";
        for (int i = 0; i < this.dataType.numOfStates(); ++i) {
            s = String.valueOf(s) + this.dataType.getDataWithState(i).toString() + "[" + Tools.doubleToPercent(freq[i], 2) + "]";
            if (i < this.dataType.numOfStates() - 1) {
                s = String.valueOf(s) + ", ";
            }
        }
        return s;
    }

    public double getSequenceQuality(final String taxon) {
        double ambiguities = this.getNChar();
        for (final Partition P : this.getPartitions()) {
            for (int pos = 0; pos < P.getCompressedNChar(); ++pos) {
                final Data d = P.getData(taxon, pos);
                if (d.numOfStates() > 1) {
                    ambiguities -= d.numOfStates() / d.getMaxStates() * P.getWeight(pos);
                }
            }
        }
        ambiguities /= this.getNChar();
        return ambiguities;
    }

    public Map<String, Double> getAmbiguousSequences(final double threshold) {
        final ProgressHandling progress = MetaPIGA.progressHandling;
        progress.newSingleProgress(0, this.getNTax() * this.getCompressedNChar(), "Testing for ambiguities");
        int p = 0;
        final Map<String, Double> ambigous = new TreeMap<String, Double>();
        for (final String taxon : this.getTaxa()) {
            double ambiguities = 0.0;
            for (final Partition P : this.getPartitions()) {
                for (int pos = 0; pos < P.getCompressedNChar(); ++pos) {
                    progress.setValue(++p);
                    if (P.getData(taxon, pos).numOfStates() > 1) {
                        ambiguities += P.getWeight(pos);
                    }
                }
            }
            ambiguities /= this.getNChar();
            if (ambiguities >= threshold) {
                ambigous.put(taxon, ambiguities);
            }
        }
        return ambigous;
    }

    public Set<Set<String>> getIdenticalSequences() {
        final DistanceMatrix DM = this.getDistanceMatrix(Parameters.DistanceModel.ABSOLUTE, Parameters.StartingTreeDistribution.NONE, 0.5, 0.0, Parameters.StartingTreePInvPi.CONSTANT);
        for (int i = 0; i < DM.ntax; ++i) {
            for (int j = 0; j < i; ++j) {
                DM.set(i, j, DM.get(j, i));
            }
        }
        final Set<Set<Integer>> idSeq = new HashSet<Set<Integer>>();
        final ProgressHandling progress = MetaPIGA.progressHandling;
        final int T = DM.ntax;
        progress.newSingleProgress(0, T * (T - 1) / 2 + T, "Testing for identical sequences");
        int p = 0;
        for (int k = 0; k < DM.ntax; ++k) {
            for (int l = 0; l < k; ++l) {
                progress.setValue(++p);
                if (DM.get(l, k) == 0.0) {
                    final Set<Integer> set = new HashSet<Integer>();
                    final Iterator<Set<Integer>> it = idSeq.iterator();
                    while (it.hasNext()) {
                        final Set<Integer> idSet = it.next();
                        if (idSet.contains(k) || idSet.contains(l)) {
                            boolean equivalent = true;
                            for (final int m : idSet) {
                                if (m != k && DM.get(m, k) != 0.0) {
                                    equivalent = false;
                                    break;
                                }
                                if (m != l && DM.get(m, l) != 0.0) {
                                    equivalent = false;
                                    break;
                                }
                            }
                            if (!equivalent) {
                                continue;
                            }
                            for (final int m : idSet) {
                                set.add(m);
                            }
                            it.remove();
                        }
                    }
                    set.add(k);
                    set.add(l);
                    idSeq.add(set);
                }
            }
        }
        final Set<Set<String>> result = new HashSet<Set<String>>();
        for (final Set<Integer> set2 : idSeq) {
            final Set<String> s = new HashSet<String>();
            for (final int id : set2) {
                s.add(this.taxas.get(id));
            }
            result.add(s);
        }
        return result;
    }

    public DistanceMatrix getDistanceMatrix(final Parameters.DistanceModel model, final Parameters.StartingTreeDistribution distribution, final double distributionShape, final double pinv, final Parameters.StartingTreePInvPi pi) {
        if (this.D == null || model != this.DMmodel || distribution != this.DMdistribution || distributionShape != this.DMdistributionShape || pinv != this.DMpinv || pi != this.DMpi) {
            this.D = new DistanceMatrix(this.dataType, new HashSet<Partition>(this.getPartitions()), this.getTaxa(), model, distribution, distributionShape, pinv, pi);
            this.DMmodel = model;
            this.DMdistribution = distribution;
            this.DMdistributionShape = distributionShape;
            this.DMpinv = pinv;
            this.DMpi = pi;
            this.hasSaturation = this.D.hasSaturation();
        }
        return this.D;
    }

    public Tree generateTree(final Set<String> outgroup, final Parameters.StartingTreeGeneration generation, final double startingTreeRange, final Parameters.DistanceModel model, final Parameters.StartingTreeDistribution distribution, final double gammaShape, final double pinv, final Parameters.StartingTreePInvPi pi, final Parameters datasetAndEvaluationParam, final Monitor monitor) throws OutgroupTooBigException, UncompatibleOutgroupException, UnknownTaxonException, TooManyNeighborsException {
        if (outgroup.size() > this.taxas.size() - 2) {
            throw new OutgroupTooBigException();
        }
        Tree tree = new Tree(generation.toString(), datasetAndEvaluationParam);
        int treeRange = (int)(this.getNTax() * (this.getNTax() - 1) / 2 * startingTreeRange);
        if (treeRange < 1) {
            treeRange = 1;
        }
        switch (generation) {
            case RANDOM: {
                final List<String> inTaxa = new ArrayList<String>(this.taxas);
                final Node root = new Node();
                tree.addNode(root);
                if (outgroup.size() == 1) {
                    final String name = outgroup.iterator().next();
                    inTaxa.remove(name);
                    final Node leaf = new Node(name);
                    final Node.Neighbor key = root.addNeighbor(leaf);
                    root.setBranchLength(key, Exponential.staticNextDouble(1.0) + 0.001);
                    tree.addNode(leaf);
                }
                else if (outgroup.size() > 1) {
                    final List<String> outTaxa = new ArrayList<String>(outgroup);
                    inTaxa.removeAll(outgroup);
                    final Node outRoot = new Node();
                    tree.addNode(outRoot);
                    final Node.Neighbor key = root.addNeighbor(outRoot);
                    root.setBranchLength(key, Exponential.staticNextDouble(1.0) + 0.001);
                    this.generateRandomSubTree(tree, outRoot, outTaxa);
                }
                this.generateRandomSubTree(tree, root, inTaxa);
                tree.setAccessNode(root);
                tree.setOutgroup(outgroup);
                tree.setAccessNodeToOutgroupRoot();
                break;
            }
            case NJ: {
                treeRange = 1;
                try {
                    this.generateNeighborJoiningTree(tree, treeRange, new HashSet<String>(), model, distribution, gammaShape, pinv, pi, monitor);
                    tree.setOutgroup(outgroup);
                }
                catch (UncompatibleOutgroupException ex) {
                    System.out.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, String.valueOf(ex.getMessage()) + "\nA loosing neighbor joining tree will be used instead, with a topology compatible with your outgroup.");
                    tree = new Tree(generation.toString(), datasetAndEvaluationParam);
                    this.generateNeighborJoiningTree(tree, treeRange, outgroup, model, distribution, gammaShape, pinv, pi, monitor);
                    tree.setOutgroup(outgroup);
                }
                tree.setAccessNodeToOutgroupRoot();
                break;
            }
            default: {
                this.generateNeighborJoiningTree(tree, treeRange, outgroup, model, distribution, gammaShape, pinv, pi, monitor);
                tree.setOutgroup(outgroup);
                tree.setAccessNodeToOutgroupRoot();
                break;
            }
        }
        return tree;
    }

    private void generateRandomSubTree(final Tree tree, final Node root, final List<String> taxaList) throws UnknownTaxonException, TooManyNeighborsException {
        final List<String> remainingTaxa = new ArrayList<String>(taxaList);
        int nInodes = taxaList.size() - 3 + root.getNeighborNodes().size();
        int freeSlots = 3 - root.getNeighborNodes().size();
        final List<Node> inodes = new ArrayList<Node>();
        inodes.add(root);
        while (inodes.size() > 0) {
            final Node current = inodes.get(0);
            inodes.remove(0);
            while (current.getNeighborNodes().size() < 3) {
                final boolean addLeaf = (freeSlots != 1 || nInodes <= 0) && (nInodes == 0 || Math.random() < 0.5);
                if (addLeaf) {
                    final int rand = Tools.randInt(remainingTaxa.size());
                    final String name = remainingTaxa.get(rand);
                    remainingTaxa.remove(rand);
                    final Node leaf = new Node(name);
                    final Node.Neighbor key = current.addNeighbor(leaf);
                    current.setBranchLength(key, Exponential.staticNextDouble(1.0) + 0.001);
                    tree.addNode(leaf);
                    --freeSlots;
                }
                else {
                    final Node inode = new Node();
                    final Node.Neighbor key2 = current.addNeighbor(inode);
                    current.setBranchLength(key2, Exponential.staticNextDouble(1.0) + 0.001);
                    inodes.add(inode);
                    ++freeSlots;
                    --nInodes;
                }
            }
            tree.addNode(current);
        }
    }

    private void generateNeighborJoiningTree(final Tree tree, final int randomRange, final Set<String> outgroup, final Parameters.DistanceModel model, final Parameters.StartingTreeDistribution distribution, final double distributionShape, final double pinv, final Parameters.StartingTreePInvPi pi, final Monitor monitor) throws UnknownTaxonException, TooManyNeighborsException {
        final int taxaNumber = this.getNTax();
        final int matLength = 2 * taxaNumber - 2;
        final double[][] DM = new double[matLength][matLength];
        for (int i = 0; i < DM.length; ++i) {
            for (int j = 0; j < DM[i].length; ++j) {
                if (i < taxaNumber && j < taxaNumber) {
                    DM[i][j] = 0.0;
                }
                else {
                    DM[i][j] = Double.MAX_VALUE;
                }
            }
        }
        if (this.D == null || model != this.DMmodel || distribution != this.DMdistribution || distributionShape != this.DMdistributionShape || pinv != this.DMpinv || pi != this.DMpi) {
            this.D = new DistanceMatrix(this.dataType, new HashSet<Partition>(this.getPartitions()), this.getTaxa(), model, distribution, distributionShape, pinv, pi);
            this.DMmodel = model;
            this.DMdistribution = distribution;
            this.DMdistributionShape = distributionShape;
            this.DMpinv = pinv;
            this.DMpi = pi;
            this.hasSaturation = this.D.hasSaturation();
            if (this.hasSaturation) {
                monitor.showText("\n");
                monitor.showText("-------------------------------------------------------------------------------------------------------------------");
                monitor.showText("-------------------------------------------------------------------------------------------------------------------");
                monitor.showText("-------------------------------------------------------------------------------------------------------------------");
                monitor.showText("WARNING: MetaPIGA has encountered saturations in ML distances.");
                monitor.showText("This can generate artifacts in your final result or cause ML computation to fail, hence metaPIGA to crash.");
                monitor.showText("We suggest you remove the highly divergent sequence(s) and realign the remaining sequences.");
                monitor.showText("-------------------------------------------------------------------------------------------------------------------");
                monitor.showText("-------------------------------------------------------------------------------------------------------------------");
                monitor.showText("-------------------------------------------------------------------------------------------------------------------");
                monitor.showText("\n");
            }
            if (monitor.trackDistances()) {
                monitor.printDistanceMatrix(this.D);
            }
        }
        for (int x = 0; x < taxaNumber; ++x) {
            for (int y = x + 1; y < taxaNumber; ++y) {
                DM[x][y] = (DM[y][x] = this.D.get(x, y));
            }
        }
        final BitSet mask = new BitSet(matLength);
        final BitSet selectable = new BitSet(matLength);
        int N = taxaNumber;
        final double[] U = new double[matLength];
        final Node[] nodes = new Node[matLength];
        for (int k = 0; k < matLength; ++k) {
            U[k] = Double.MAX_VALUE;
            if (k < taxaNumber) {
                nodes[k] = new Node(this.getTaxon(k));
                mask.set(k);
                if (!outgroup.contains(this.getTaxon(k))) {
                    selectable.set(k);
                }
            }
            else {
                nodes[k] = new Node();
            }
        }
        for (int k = taxaNumber; k < matLength; ++k) {
            for (int l = 0; l < matLength; ++l) {
                if (mask.get(l)) {
                    U[l] = 0.0;
                    for (int m = 0; m < matLength; ++m) {
                        if (mask.get(m) && l != m) {
                            final double[] array = U;
                            final int n = l;
                            array[n] += DM[l][m];
                        }
                    }
                    final double[] array2 = U;
                    final int n2 = l;
                    array2[n2] /= N - 2;
                }
            }
            Node node1 = null;
            Node node2 = null;
            int pos1 = -1;
            int pos2 = -1;
            final List<Point> mins = new ArrayList<Point>();
            for (int i2 = 0; i2 < matLength; ++i2) {
                if (mask.get(i2) && selectable.get(i2)) {
                    for (int j2 = i2 + 1; j2 < matLength; ++j2) {
                        if (mask.get(j2) && selectable.get(j2)) {
                            final double thisVal = DM[i2][j2] - U[i2] - U[j2];
                            boolean add = false;
                            if (mins.size() < randomRange) {
                                add = true;
                            }
                            else {
                                final Point p = mins.get(mins.size() - 1);
                                final double val = DM[p.x][p.y] - U[p.x] - U[p.y];
                                if (thisVal < val) {
                                    mins.remove(mins.size() - 1);
                                    add = true;
                                }
                            }
                            if (add) {
                                int pos3 = 0;
                                for (int m2 = 0; m2 < mins.size(); ++m2) {
                                    final Point p2 = mins.get(m2);
                                    final double val2 = DM[p2.x][p2.y] - U[p2.x] - U[p2.y];
                                    if (thisVal < val2) {
                                        break;
                                    }
                                    ++pos3;
                                }
                                mins.add(pos3, new Point(i2, j2));
                            }
                        }
                    }
                }
            }
            final int rand = (mins.size() == 1) ? 0 : Tools.randInt(mins.size());
            final Point p3 = mins.get(rand);
            node1 = nodes[p3.x];
            pos1 = p3.x;
            node2 = nodes[p3.y];
            pos2 = p3.y;
            final Node inode = nodes[k];
            Node.Neighbor key = inode.addNeighbor(node1);
            inode.setBranchLength(key, DM[pos1][pos2] / 2.0 + (U[pos1] - U[pos2]) / 2.0);
            key = inode.addNeighbor(node2);
            inode.setBranchLength(key, DM[pos1][pos2] / 2.0 + (U[pos2] - U[pos1]) / 2.0);
            tree.addNode(node1);
            tree.addNode(node2);
            for (int i3 = 0; i3 < k; ++i3) {
                if (i3 != pos1 && i3 != pos2) {
                    DM[k][i3] = (DM[pos1][i3] + DM[pos2][i3] - DM[pos1][pos2]) / 2.0;
                    DM[i3][k] = DM[k][i3];
                }
            }
            mask.clear(pos1);
            selectable.clear(pos1);
            mask.clear(pos2);
            selectable.clear(pos2);
            mask.set(k);
            selectable.set(k);
            --N;
            if (selectable.cardinality() == 1 && N > 2) {
                for (int taxa = 0; taxa < taxaNumber; ++taxa) {
                    if (outgroup.contains(this.getTaxon(taxa))) {
                        selectable.set(taxa);
                    }
                }
            }
        }
        for (int i4 = 0; i4 < matLength; ++i4) {
            if (mask.get(i4)) {
                final Node.Neighbor key2 = nodes[matLength - 1].addNeighbor(nodes[i4]);
                nodes[matLength - 1].setBranchLength(key2, DM[i4][matLength - 1]);
                tree.addNode(nodes[i4]);
                tree.addNode(nodes[matLength - 1]);
                tree.setAccessNode(nodes[matLength - 1]);
                break;
            }
        }
    }

    public Dataset randomSampling() {
        final Dataset D = new Dataset(this);
        for (final Partition P : D.data.values()) {
            final int[] pos = new int[P.nchar];
            int i = 0;
            for (int p = 0; p < P.weights.length; ++p) {
                for (int j = 0; j < P.weights[p]; ++j) {
                    pos[i] = p;
                    ++i;
                }
            }
            Arrays.fill(P.weights, 0);
            for (int k = 0; k < P.nchar; ++k) {
                final int[] access$1 = P.weights;
                final int n = pos[Tools.randInt(P.nchar)];
                ++access$1[n];
            }
        }
        return D;
    }

    @Override
    public String toString() {
        final String endl = "\n";
        final StringBuilder doc = new StringBuilder();
        int longestTaxon = 0;
        for (final String taxa : this.getTaxa()) {
            if (taxa.length() > longestTaxon) {
                longestTaxon = taxa.length();
            }
        }
        if ("Weights".toString().length() > longestTaxon) {
            longestTaxon = "Weights".toString().length();
        }
        doc.append("Character matrices used in MetaPIGA :" + endl + endl);
        doc.append("Your Nexus matrix has been compressed, you can see the weight of each column on the last line." + endl);
        doc.append(String.valueOf(this.getNTax()) + " taxa where kept." + endl);
        doc.append(String.valueOf(this.getDataType().verbose()) + " frequencies : ");
        try {
            doc.append(this.getDataFrequenciesToString());
        }
        catch (UnknownDataException ex) {
            doc.append(ex.getMessage());
        }
        doc.append(endl);
        if (!this.data.isEmpty()) {
            doc.append("Partitions (each one is used separatly during computation) : " + endl);
            for (final Map.Entry<Charset, Partition> e : this.data.entrySet()) {
                doc.append(e.getKey().toString());
                doc.append(" : " + this.getPartition(e.getKey()).getNChar() + " characters (" + this.getPartition(e.getKey()).getCompression() + " compression giving " + this.getPartition(e.getKey()).getCompressedNChar() + " characters)" + " - Frequencies : ");
                try {
                    doc.append(this.getPartition(e.getKey()).getDataFrequenciesToString());
                }
                catch (UnknownDataException ex2) {
                    doc.append(ex2.getMessage());
                }
                doc.append(endl);
            }
            doc.append(endl);
        }
        doc.append(endl);
        for (final String taxa : this.getTaxa()) {
            final int spaces = longestTaxon - taxa.toString().length();
            String stax = taxa;
            for (int j = 0; j < spaces; ++j) {
                stax = String.valueOf(stax) + " ";
            }
            stax = String.valueOf(stax) + "    ";
            doc.append(stax);
            for (final Charset ch : this.getPartitionCharsets()) {
                for (final Data data : this.getPartition(ch).getAllData(taxa)) {
                    doc.append(data.toString());
                }
                doc.append(" ");
            }
            doc.append(endl);
        }
        doc.append(endl);
        boolean nextLine = false;
        int line = 0;
        String ws = "Weights";
        for (int spaces2 = longestTaxon - ws.toString().length(), j = 0; j < spaces2; ++j) {
            ws = String.valueOf(ws) + " ";
        }
        ws = String.valueOf(ws) + "    ";
        doc.append(ws);
        for (final Charset ch : this.getPartitionCharsets()) {
            int[] allWeights;
            for (int length = (allWeights = this.getPartition(ch).getAllWeights()).length, k = 0; k < length; ++k) {
                final int w = allWeights[k];
                String s = new StringBuilder().append(w).toString();
                if (s.length() > line + 1) {
                    s = new StringBuilder().append(s.charAt(line)).toString();
                    nextLine = true;
                }
                doc.append(s);
            }
            doc.append(" ");
        }
        doc.append(endl);
        String empty = "";
        for (int i = 0; i < ws.length(); ++i) {
            empty = String.valueOf(empty) + " ";
        }
        while (nextLine) {
            ++line;
            nextLine = false;
            doc.append(empty);
            for (final Charset ch2 : this.getPartitionCharsets()) {
                int[] allWeights2;
                for (int length2 = (allWeights2 = this.getPartition(ch2).getAllWeights()).length, l = 0; l < length2; ++l) {
                    final int w2 = allWeights2[l];
                    String s2 = new StringBuilder().append(w2).toString();
                    if (s2.length() < line + 1) {
                        s2 = " ";
                    }
                    else if (s2.length() == line + 1) {
                        s2 = new StringBuilder().append(s2.charAt(line)).toString();
                    }
                    else if (s2.length() > line + 1) {
                        s2 = new StringBuilder().append(s2.charAt(line)).toString();
                        nextLine = true;
                    }
                    doc.append(s2);
                }
                doc.append(" ");
            }
            doc.append(endl);
        }
        return doc.toString();
    }

    public static final class Partition
    {
        private final DataType dataType;
        private final Map<String, List<Data>> data;
        private final List<String> taxa;
        private final int nchar;
        private final int[] weights;
        private final List<Set<Integer>> datasetMapping;

        public Partition(final DataType dataType, final List<String> taxas, final Map<String, List<Data>> seq, final List<Integer> mapping) {
            this.dataType = dataType;
            this.taxa = taxas;
            this.nchar = seq.get(taxas.get(0)).size();
            ProgressHandling progress = MetaPIGA.progressHandling;
            if (progress == null) {
                progress = new ProgressHandling(new JProgressBar());
                progress.setUI(MetaPIGA.UI.SILENT);
            }
            progress.newSingleProgress(0, this.nchar, "Compressing dataset");
            final List<Integer> tempList = new LinkedList<Integer>();
            for (int i = 0; i < this.nchar; ++i) {
                tempList.add(1);
            }
            final Map<String, Integer> difmap = new HashMap<String, Integer>();
            final Map<Integer, Set<Integer>> tempMapping = new TreeMap<Integer, Set<Integer>>();
            for (int j = 0; j < this.nchar; ++j) {
                progress.setValue(j);
                String site = "";
                for (final String taxa : taxas) {
                    site = String.valueOf(site) + seq.get(taxa).get(j);
                }
                if (!difmap.containsKey(site)) {
                    difmap.put(site, j);
                    final Set<Integer> set = new TreeSet<Integer>();
                    set.add(mapping.get(j));
                    tempMapping.put(j, set);
                }
                else {
                    final int k = difmap.get(site);
                    tempList.set(k, tempList.get(k) + 1);
                    tempList.set(j, 0);
                    tempMapping.get(k).add(mapping.get(j));
                }
            }
            this.data = new LinkedHashMap<String, List<Data>>();
            for (final String taxa2 : taxas) {
                this.data.put(taxa2, new LinkedList<Data>());
            }
            progress.newSingleProgress(0, this.nchar, "Building data matrix");
            for (int j = 0; j < this.nchar; ++j) {
                progress.setValue(j);
                if (tempList.get(j) > 0) {
                    for (final String taxa3 : taxas) {
                        this.data.get(taxa3).add(seq.get(taxa3).get(j));
                    }
                }
            }
            final List<Integer> weightsList = new LinkedList<Integer>();
            this.datasetMapping = new LinkedList<Set<Integer>>();
            for (int l = 0; l < this.nchar; ++l) {
                if (tempList.get(l) > 0) {
                    weightsList.add(tempList.get(l));
                    this.datasetMapping.add(tempMapping.get(l));
                }
            }
            final int size = weightsList.size();
            this.weights = new int[size];
            for (int m = 0; m < size; ++m) {
                this.weights[m] = weightsList.get(m);
            }
        }

        public Partition(final Partition P) {
            this.dataType = P.dataType;
            this.data = new LinkedHashMap<String, List<Data>>();
            for (final String taxa : P.data.keySet()) {
                this.data.put(taxa, new LinkedList<Data>(P.data.get(taxa)));
            }
            this.taxa = new ArrayList<String>(P.taxa);
            this.nchar = P.nchar;
            this.weights = new int[P.weights.length];
            System.arraycopy(P.weights, 0, this.weights, 0, P.weights.length);
            this.datasetMapping = new LinkedList<Set<Integer>>();
            for (final Set<Integer> set : P.datasetMapping) {
                this.datasetMapping.add(new TreeSet<Integer>(set));
            }
        }

        public DataType getDataType() {
            return this.dataType;
        }

        public String getTaxon(final int index) {
            return this.taxa.get(index);
        }

        public List<String> getTaxa() {
            return this.taxa;
        }

        public int getNTax() {
            return this.taxa.size();
        }

        public int getNChar() {
            return this.nchar;
        }

        public int getCompressedNChar() {
            return this.weights.length;
        }

        public Data getData(final String taxon, final int position) {
            return this.data.get(taxon).get(position);
        }

        public Data getData(final int taxaIndex, final int position) {
            return this.data.get(this.taxa.get(taxaIndex)).get(position);
        }

        public List<Data> getAllData(final String taxon) {
            return this.data.get(taxon);
        }

        public int getWeight(final int position) {
            return this.weights[position];
        }

        public int[] getAllWeights() {
            return this.weights;
        }

        public String getCompression() {
            return String.valueOf(100 - (int)(this.getCompressedNChar() / this.nchar * 100.0)) + "%";
        }

        public Set<Integer> getDatasetPosition(final int position) {
            return this.datasetMapping.get(position);
        }

        public double[] getDataFrequencies() {
            final double[] frequencies = new double[this.dataType.numOfStates()];
            for (final List<Data> list : this.data.values()) {
                for (int k = 0; k < list.size(); ++k) {
                    final Data d = list.get(k);
                    for (int i = 0; i < this.dataType.numOfStates(); ++i) {
                        final double[] array = frequencies;
                        final int n = i;
                        array[n] += (d.isState(i) ? (this.weights[k] / d.numOfStates()) : 0.0);
                    }
                }
            }
            for (int j = 0; j < frequencies.length; ++j) {
                final double[] array2 = frequencies;
                final int n2 = j;
                array2[n2] /= this.getNChar() * this.getNTax();
            }
            return frequencies;
        }

        public String getDataFrequenciesToString() throws UnknownDataException {
            final double[] freq = this.getDataFrequencies();
            String s = "";
            for (int i = 0; i < this.dataType.numOfStates(); ++i) {
                s = String.valueOf(s) + this.dataType.getDataWithState(i).toString() + "[" + Tools.doubleToPercent(freq[i], 2) + "]";
                if (i < this.dataType.numOfStates() - 1) {
                    s = String.valueOf(s) + ", ";
                }
            }
            return s;
        }
    }
}

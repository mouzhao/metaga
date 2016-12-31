// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.utilities;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import Jama.Matrix;
import java.util.Iterator;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import metapiga.trees.Node;
import java.util.Map;
import metapiga.trees.Tree;
import metapiga.modelization.likelihood.SequenceArrays;

public class SequenceDebugging
{
    public static void printSequence(final SequenceArrays s, final String toFileName, final Tree tree, final Map<Node, Integer> nodeIdxs) {
        PrintWriter outFile = null;
        System.err.println("Debugging function");
        try {
            outFile = new PrintWriter(toFileName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final int numNodes = s.getNodeCount();
        final int numCats = s.getCategoryCount();
        final int numSites = s.getCharacterCountNoPadding();
        final int numStates = s.getStateCount();
        String text = "";
        final List<Node> nodes = tree.getInodes();
        for (final Node nod : nodes) {
            final int n = nodeIdxs.get(nod);
            String catText = "";
            for (int cat = 0; cat < numCats; ++cat) {
                String siteText = "";
                for (int site = 0; site < numSites; ++site) {
                    String stateText = "";
                    for (int state = 0; state < numStates; ++state) {
                        final float val = s.getElement(n, cat, site, state);
                        if (!stateText.contentEquals("")) {
                            stateText = String.valueOf(stateText) + "," + val;
                        }
                        else {
                            stateText = String.valueOf(stateText) + val;
                        }
                    }
                    if (!siteText.contentEquals("")) {
                        siteText = String.valueOf(siteText) + "I" + stateText;
                    }
                    else {
                        siteText = String.valueOf(siteText) + stateText;
                    }
                }
                if (!catText.contentEquals("")) {
                    catText = String.valueOf(catText) + ":" + siteText;
                }
                else {
                    catText = String.valueOf(catText) + siteText;
                }
            }
            if (!text.contentEquals("")) {
                text = String.valueOf(text) + "/" + catText;
            }
            else {
                text = String.valueOf(text) + catText;
            }
        }
        outFile.println(text);
        outFile.close();
    }
    
    public static void printJamaMatrixForMatlab(final Matrix matrix, final String toFileName, final String matName) {
        PrintWriter outFile = null;
        System.err.println("Debugging function");
        try {
            outFile = new PrintWriter(toFileName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final int r = matrix.getRowDimension();
        final int c = matrix.getColumnDimension();
        outFile.println(String.valueOf(matName) + "=[");
        String rowString = "";
        for (int row = 0; row < r; ++row) {
            rowString = "[";
            for (int column = 0; column < c; ++column) {
                if (rowString.contentEquals("[")) {
                    rowString = String.valueOf(rowString) + matrix.get(row, column);
                }
                else {
                    rowString = String.valueOf(rowString) + ", " + matrix.get(row, column);
                }
            }
            if (row == r - 1) {
                rowString = String.valueOf(rowString) + "]";
            }
            else {
                rowString = String.valueOf(rowString) + "],";
            }
            outFile.println(rowString);
        }
        outFile.println("];");
        outFile.flush();
        outFile.close();
    }
    
    public static void printArrayMatrixForMatlab(final double[][] matrix, final String toFileName, final String matrixName) {
        PrintWriter outFile = null;
        System.err.println("Debugging function");
        try {
            outFile = new PrintWriter(toFileName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final int r = matrix.length;
        final int c = matrix[0].length;
        outFile.println(String.valueOf(matrixName) + "=[");
        String rowString = "";
        for (int row = 0; row < r; ++row) {
            rowString = "[";
            for (int column = 0; column < c; ++column) {
                if (rowString.contentEquals("[")) {
                    rowString = String.valueOf(rowString) + matrix[row][column];
                }
                else {
                    rowString = String.valueOf(rowString) + ", " + matrix[row][column];
                }
            }
            if (row == r - 1) {
                rowString = String.valueOf(rowString) + "]";
            }
            else {
                rowString = String.valueOf(rowString) + "],";
            }
            outFile.println(rowString);
        }
        outFile.println("];");
        outFile.flush();
        outFile.close();
    }
    
    public static void printArrayMatrixForMatlab(final float[][] matrix, final String toFileName, final String matrixName) {
        PrintWriter outFile = null;
        System.err.println("Debugging function");
        try {
            outFile = new PrintWriter(toFileName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final int r = matrix.length;
        final int c = matrix[0].length;
        outFile.println(String.valueOf(matrixName) + "=[");
        String rowString = "";
        for (int row = 0; row < r; ++row) {
            rowString = "[";
            for (int column = 0; column < c; ++column) {
                if (rowString.contentEquals("[")) {
                    rowString = String.valueOf(rowString) + matrix[row][column];
                }
                else {
                    rowString = String.valueOf(rowString) + ", " + matrix[row][column];
                }
            }
            if (row == r - 1) {
                rowString = String.valueOf(rowString) + "]";
            }
            else {
                rowString = String.valueOf(rowString) + "],";
            }
            outFile.println(rowString);
        }
        outFile.println("];");
        outFile.flush();
        outFile.close();
    }
    
    public static void printArrayVectorForMatlab(final double[] matrix, final String toFileName, final String matrixName) {
        PrintWriter outFile = null;
        System.err.println("Debugging function");
        try {
            outFile = new PrintWriter(toFileName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final int r = matrix.length;
        outFile.println(String.valueOf(matrixName) + "=[");
        String rowString = "";
        for (int row = 0; row < r; ++row) {
            rowString = String.valueOf(rowString) + matrix[row];
            if (row != r - 1) {
                rowString = String.valueOf(rowString) + ",";
            }
        }
        outFile.println(rowString);
        outFile.println("];");
        outFile.flush();
        outFile.close();
    }
    
    public static void calculateSequenceDiff(final String seq1, final String seq2, final String result, final boolean equals, final float delta) {
        String line = "";
        String line2 = "";
        PrintWriter outFile = null;
        System.err.println("Debugging function");
        try {
            outFile = new PrintWriter(result);
            final InputStream fis1 = new FileInputStream(seq1);
            final InputStream fis2 = new FileInputStream(seq2);
            final BufferedReader br1 = new BufferedReader(new InputStreamReader(fis1, Charset.forName("UTF-8")));
            final BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2, Charset.forName("UTF-8")));
            line = br1.readLine();
            line2 = br2.readLine();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        final String[] nodes1 = line.split("/");
        final String[] nodes2 = line2.split("/");
        if (nodes1.length != nodes2.length) {
            System.out.println("Unequal number of nodes");
            return;
        }
        for (int n = 0; n < nodes2.length; ++n) {
            final String nodeLine1 = nodes1[n];
            final String nodeLine2 = nodes2[n];
            final String[] cats1 = nodeLine1.split(":");
            final String[] cats2 = nodeLine2.split(":");
            if (cats1.length != cats2.length) {
                System.out.println("Unequal number of cats in node " + n);
                return;
            }
            for (int c = 0; c < cats2.length; ++c) {
                final String catLine1 = cats1[c];
                final String catLine2 = cats2[c];
                final String[] sites1 = catLine1.split("I");
                final String[] sites2 = catLine2.split("I");
                if (sites1.length != sites2.length) {
                    System.out.println("Unequal number of sites in node " + n + "and cat " + c);
                    return;
                }
                for (int site = 0; site < sites2.length; ++site) {
                    final String siteLine1 = sites1[site];
                    final String siteLine2 = sites2[site];
                    final String[] states1 = siteLine1.split(",");
                    final String[] states2 = siteLine2.split(",");
                    if (states1.length != states2.length) {
                        System.out.println("Unequal number of sites in node " + n + "and cat " + c + " and site " + site);
                        return;
                    }
                    for (int state = 0; state < states2.length; ++state) {
                        final float val1 = Float.parseFloat(states1[state]);
                        final float val2 = Float.parseFloat(states2[state]);
                        if (!equals) {
                            if (val1 != val2) {
                                outFile.println("Diff in [" + n + "," + c + "," + site + "," + state + "]");
                                outFile.println("val1 = " + val1);
                                outFile.println("val2 = " + val2);
                            }
                        }
                        else if (val1 == val2) {
                            outFile.println("Equal in [" + n + "," + c + "," + site + "," + state + "]");
                            outFile.println("val1 = " + val1);
                            outFile.println("val2 = " + val2);
                        }
                    }
                }
            }
        }
        outFile.flush();
        outFile.close();
    }
    
    public static void main(final String[] args0) {
        calculateSequenceDiff("C:\\Users\\calavera\\Desktop\\debugPrints\\node2gpu.txt", "C:\\Users\\calavera\\Desktop\\debugPrints\\node2classic.txt", "C:\\Users\\calavera\\Desktop\\debugPrints\\diffSeq.txt", false, 0.001f);
    }
}

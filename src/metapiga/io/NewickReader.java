// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.io;

import metapiga.trees.ConsensusNode;
import metapiga.trees.exceptions.UnknownNeighborException;
import metapiga.trees.exceptions.TooManyNeighborsException;
import metapiga.trees.exceptions.UnknownTaxonException;
import metapiga.utilities.Tools;
import metapiga.trees.Node;
import java.util.LinkedHashMap;
import metapiga.parameters.Parameters;
import java.util.Map;
import metapiga.trees.Tree;

public class NewickReader
{
    Tree tree;
    NewickParser parser;
    boolean isRooted;
    boolean isConsensus;
    Map<String, String> translation;
    
    public NewickReader(final Parameters parameters, final String treeName, final String newickTree, final Map<String, String> translation) {
        this.tree = new Tree(treeName.trim(), parameters);
        this.translation = translation;
        this.parser = new NewickParser(newickTree.trim());
    }
    
    public NewickReader(final Parameters parameters, final String newickString) {
        int c = 0;
        boolean found = false;
        boolean comment = false;
        while (c < newickString.length() && !found) {
            if (newickString.charAt(c) == '[') {
                comment = true;
            }
            else if (newickString.charAt(c) == ']') {
                comment = false;
            }
            else if (!comment && newickString.charAt(c) == '=') {
                found = true;
            }
            ++c;
        }
        String name;
        String newick;
        if (!found) {
            name = "Unnamed tree";
            newick = newickString;
        }
        else {
            name = newickString.substring(0, c - 1).trim();
            newick = newickString.substring(c).trim();
        }
        if (name.toUpperCase().startsWith("TREE ")) {
            name = name.substring(4).trim();
        }
        name = name.replace('_', ' ');
        this.tree = new Tree(name.trim(), parameters);
        this.translation = new LinkedHashMap<String, String>();
        this.parser = new NewickParser(newick.trim());
    }
    
    public Tree parseNewick() throws ParseTreeException {
        try {
            Node accessNode = this.parseNext();
            if (this.parser.isRooted() && accessNode.getNeighborNodes().size() == 2) {
                final Node A = accessNode.getNeighbor(Node.Neighbor.A);
                final Node B = accessNode.getNeighbor(Node.Neighbor.B);
                final Node.Neighbor keyA = A.replaceNeighbor(accessNode, B);
                final Node.Neighbor keyB = B.replaceNeighbor(accessNode, A);
                A.setBranchLength(keyA, A.getBranchLength(keyA) + B.getBranchLength(keyB));
                accessNode = A;
            }
            else {
                this.tree.addNode(accessNode);
            }
            this.tree.setAccessNode(accessNode);
            this.tree.labelizeTree();
            return this.tree;
        }
        catch (UnknownTaxonException e) {
            throw new ParseTreeException("Cannot parse the tree " + this.tree.getName() + ": " + Tools.getErrorMessage(e), e.getCause());
        }
        catch (TooManyNeighborsException e2) {
            throw new ParseTreeException("Cannot parse the tree " + this.tree.getName() + ": " + Tools.getErrorMessage(e2), e2.getCause());
        }
        catch (UnknownNeighborException e3) {
            throw new ParseTreeException("Cannot parse the tree " + this.tree.getName() + ": " + Tools.getErrorMessage(e3), e3.getCause());
        }
    }
    
    public Node parseNext() throws TooManyNeighborsException, UnknownTaxonException, ParseTreeException {
        final Node currentNode = this.isConsensus ? new ConsensusNode() : new Node();
        while (this.parser.hasNext()) {
            Token token = this.parser.next();
            if (token.isOP()) {
                final Node firstNeighbor = this.parseNext();
                final Node.Neighbor key = currentNode.addNeighbor(firstNeighbor);
                this.tree.addNode(firstNeighbor);
                if (this.parser.seeNext().isConsensus()) {
                    token = this.parser.next();
                    token = this.parser.next();
                    ((ConsensusNode)currentNode).setBranchStrength(key, token.toDouble());
                }
                if (this.parser.seeNext().isColon()) {
                    token = this.parser.next();
                    token = this.parser.next();
                    currentNode.setBranchLength(key, token.toDouble());
                }
                if (!this.parser.seeNext().isConsensus()) {
                    continue;
                }
                token = this.parser.next();
                token = this.parser.next();
                ((ConsensusNode)currentNode).setBranchStrength(key, token.toDouble());
            }
            else if (token.isComma()) {
                final Node nextNeighbor = this.parseNext();
                final Node.Neighbor key = currentNode.addNeighbor(nextNeighbor);
                this.tree.addNode(nextNeighbor);
                if (this.parser.seeNext().isConsensus()) {
                    token = this.parser.next();
                    token = this.parser.next();
                    ((ConsensusNode)currentNode).setBranchStrength(key, token.toDouble());
                }
                if (this.parser.seeNext().isColon()) {
                    token = this.parser.next();
                    token = this.parser.next();
                    currentNode.setBranchLength(key, token.toDouble());
                }
                if (!this.parser.seeNext().isConsensus()) {
                    continue;
                }
                token = this.parser.next();
                token = this.parser.next();
                ((ConsensusNode)currentNode).setBranchStrength(key, token.toDouble());
            }
            else {
                if (token.isString()) {
                    String label = token.toString();
                    if (this.translation.containsKey(label)) {
                        label = this.translation.get(label);
                    }
                    currentNode.setLabel(label.replace('_', ' '));
                    return currentNode;
                }
                if (token.isCP()) {
                    if (!this.isConsensus && this.parser.seeNext().isString()) {
                        token = this.parser.next();
                        String label = token.toString();
                        if (this.translation.containsKey(label)) {
                            label = this.translation.get(label);
                        }
                        currentNode.setLabel(label.replace('_', ' '));
                    }
                    return currentNode;
                }
                if (token.isSemiColon()) {
                    return currentNode;
                }
                throw new ParseTreeException(String.valueOf(token.toString()) + " found where it should not !");
            }
        }
        throw new ParseTreeException("terminal ';' was not found");
    }
    
    private class NewickParser
    {
        String newickString;
        int currentPos;
        
        public NewickParser(final String newickTree) {
            this.currentPos = 0;
            this.newickString = newickTree;
            if (!this.newickString.toUpperCase().contains("[&")) {
                NewickReader.this.isConsensus = (this.newickString.toUpperCase().contains(")0.") || this.newickString.toUpperCase().indexOf(")1:") != this.newickString.toUpperCase().lastIndexOf(")1:"));
            }
            else {
                NewickReader.this.isRooted = this.newickString.toUpperCase().contains("[&R]");
                NewickReader.this.isConsensus = this.newickString.toUpperCase().contains("[&C]");
            }
            this.newickString = this.removeComments(this.newickString).trim();
            if (!this.newickString.endsWith(";")) {
                this.newickString = String.valueOf(this.newickString) + ";";
            }
        }
        
        public String removeComments(final String newick) {
            boolean commentConsensus = false;
            String res = "";
            for (int c = 0; c < newick.length(); ++c) {
                if (newick.charAt(c) == '[') {
                    String comment = "";
                    ++c;
                    while (c < newick.length() && newick.charAt(c) != ']') {
                        comment = String.valueOf(comment) + newick.charAt(c);
                        ++c;
                    }
                    if (comment.startsWith("C=")) {
                        commentConsensus = true;
                        if (comment.endsWith("%")) {
                            final double d = Double.parseDouble(comment.substring(2, comment.length() - 1));
                            res = String.valueOf(res) + "§" + d / 100.0;
                        }
                        else {
                            res = String.valueOf(res) + "§" + comment.substring(2);
                        }
                    }
                    ++c;
                }
                res = String.valueOf(res) + ((newick.charAt(c) == '§') ? "|" : newick.charAt(c));
            }
            if (NewickReader.this.isConsensus && !commentConsensus) {
                res = res.replace(")", ")§");
                res = res.replace(")§,", "),");
                res = res.replace(")§)", "))");
                res = res.replace(")§;", ");");
                res = res.replace(")§:", "):");
            }
            return res;
        }
        
        public boolean hasNext() {
            return this.currentPos < this.newickString.length();
        }
        
        public Token next() {
            String s = "";
            while (s.trim().length() == 0) {
                if (this.isToken(this.newickString.charAt(this.currentPos))) {
                    ++this.currentPos;
                    return new Token(new StringBuilder().append(this.newickString.charAt(this.currentPos - 1)).toString());
                }
                do {
                    s = String.valueOf(s) + this.newickString.charAt(this.currentPos);
                    ++this.currentPos;
                } while (!this.isToken(this.newickString.charAt(this.currentPos)));
            }
            return new Token(s);
        }
        
        public Token seeNext() {
            int pos = this.currentPos;
            String s = "";
            while (s.trim().length() == 0) {
                if (this.isToken(this.newickString.charAt(pos))) {
                    ++pos;
                    return new Token(new StringBuilder().append(this.newickString.charAt(pos - 1)).toString());
                }
                do {
                    s = String.valueOf(s) + this.newickString.charAt(pos);
                    ++pos;
                } while (!this.isToken(this.newickString.charAt(pos)));
            }
            return new Token(s);
        }
        
        private boolean isToken(final char c) {
            return c == '(' || c == ')' || c == ',' || c == ':' || c == '§' || c == ';';
        }
        
        public boolean isRooted() {
            return NewickReader.this.isRooted;
        }
        
        public boolean isConsensus() {
            return NewickReader.this.isConsensus;
        }
    }
    
    private static class Token
    {
        String token;
        
        public Token(final String s) {
            this.token = s;
        }
        
        public boolean isOP() {
            return this.token.equals("(");
        }
        
        public boolean isCP() {
            return this.token.equals(")");
        }
        
        public boolean isComma() {
            return this.token.equals(",");
        }
        
        public boolean isColon() {
            return this.token.equals(":");
        }
        
        public boolean isConsensus() {
            return this.token.equals("§");
        }
        
        public boolean isSemiColon() {
            return this.token.equals(";");
        }
        
        public boolean isNumber() {
            try {
                Double.parseDouble(this.token);
                return true;
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        
        public boolean isString() {
            return !this.isOP() && !this.isCP() && !this.isComma() && !this.isColon() && !this.isConsensus() && !this.isSemiColon();
        }
        
        @Override
        public String toString() {
            return this.token;
        }
        
        public double toDouble() {
            return Double.parseDouble(this.token);
        }
    }
}

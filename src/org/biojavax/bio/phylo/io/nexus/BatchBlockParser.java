// 
// Decompiled by Procyon v0.5.30
// 

package org.biojavax.bio.phylo.io.nexus;

import metapiga.utilities.Tools;
import org.biojava.bio.seq.io.ParseException;

public class BatchBlockParser extends NexusBlockParser.Abstract
{
    private Param run;
    private Param label;
    private Param data;
    private Param param;
    private Param trees;
    private String currentLabel;
    private String currentData;
    private String currentParam;
    private String currentTrees;
    
    public BatchBlockParser(final BatchBlockListener blockListener) {
        super(blockListener);
        this.run = new Param("RUN", ParamType.KEYWORD);
        this.label = new Param("LABEL", ParamType.EQUAL);
        this.data = new Param("DATA", ParamType.EQUAL);
        this.param = new Param("PARAM", ParamType.EQUAL);
        this.trees = new Param("TREES", ParamType.EQUAL);
    }
    
    private void resetSubParams() {
        this.label.setExpected(false);
        this.data.setExpected(false);
        this.param.setExpected(false);
        this.trees.setExpected(false);
    }
    
    private void setMainParams(final boolean expected) {
        this.run.setExpected(expected);
    }
    
    public void resetStatus() {
        this.setMainParams(true);
        this.resetSubParams();
    }
    
    private String extractValue(final String token) {
        final String[] parts = token.split("[\\(\\)]");
        return parts[0];
    }
    
    private String extractParameter(final String token) {
        final String[] parts = token.split("[\\(\\)]");
        return (parts.length > 1) ? parts[1] : null;
    }
    
    private boolean hasParameter(final String token) {
        return token.contains("(");
    }
    
    private void addCurrentRun() {
        if (this.currentLabel != null) {
            if (this.currentData != null) {
                ((BatchBlockListener)this.getBlockListener()).addData(this.currentLabel, this.currentData);
                this.currentData = null;
            }
            if (this.currentParam != null) {
                ((BatchBlockListener)this.getBlockListener()).addParam(this.currentLabel, this.currentParam);
                this.currentParam = null;
            }
            if (this.currentTrees != null) {
                ((BatchBlockListener)this.getBlockListener()).addTree(this.currentLabel, this.currentTrees);
                this.currentTrees = null;
            }
        }
    }
    
    @Override
    public void parseToken(String token) throws ParseException {
        token = token.toUpperCase();
        try {
            if (token.trim().length() == 0) {
                return;
            }
            if (this.run.expect(token)) {
                this.resetSubParams();
                this.label.setExpected(true);
                this.data.setExpected(true);
                this.param.setExpected(true);
                this.trees.setExpected(true);
            }
            else if (this.label.expect(token)) {
                final String s = this.label.parse(token);
                if (s != null) {
                    this.currentLabel = s;
                    ((BatchBlockListener)this.getBlockListener()).addLabel(this.currentLabel);
                    this.label.setExpected(false);
                }
                this.addCurrentRun();
            }
            else if (this.data.expect(token)) {
                final String s = this.data.parse(token);
                if (s != null) {
                    this.currentData = s;
                    this.data.setExpected(false);
                }
                this.addCurrentRun();
            }
            else if (this.param.expect(token)) {
                final String s = this.param.parse(token);
                if (s != null) {
                    this.currentParam = s;
                    this.param.setExpected(false);
                }
                this.addCurrentRun();
            }
            else {
                if (!this.trees.expect(token)) {
                    throw new ParseException(String.valueOf(this.expectedTokens()) + "but '" + token + "' was found.");
                }
                final String s = this.trees.parse(token);
                if (s != null) {
                    this.currentTrees = s;
                    this.trees.setExpected(false);
                }
                this.addCurrentRun();
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            throw new ParseException("'" + token + "' is not a valid number.");
        }
        catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            throw new ParseException("'" + token + "' is not a valid argument.");
        }
        catch (Exception e3) {
            e3.printStackTrace();
            throw new ParseException(Tools.getErrorMessage(e3));
        }
    }
    
    private String expectedTokens() {
        String res = "";
        res = String.valueOf(res) + this.run.toString();
        res = String.valueOf(res) + this.label.toString();
        res = String.valueOf(res) + this.data.toString();
        res = String.valueOf(res) + this.param.toString();
        res = String.valueOf(res) + this.trees.toString();
        return res;
    }
    
    public enum ParamType
    {
        KEYWORD("KEYWORD", 0), 
        SIMPLE("SIMPLE", 1), 
        EQUAL("EQUAL", 2), 
        BRACES("BRACES", 3);
        
        private ParamType(final String s, final int n) {
        }
    }
    
    public class Param
    {
        private ParamType type;
        private String keyword;
        private boolean keywordExpected;
        private boolean equalExpected;
        private boolean valueExpected;
        private boolean openingExpected;
        private boolean closingExpected;
        
        public Param(final String keyword, final ParamType type) {
            this.type = type;
            this.keyword = keyword;
            this.keywordExpected = false;
            this.equalExpected = false;
            this.valueExpected = false;
            this.openingExpected = false;
            this.closingExpected = false;
        }
        
        public void setExpected(final boolean status) {
            this.keywordExpected = status;
            this.equalExpected = false;
            this.valueExpected = false;
            this.openingExpected = false;
            this.closingExpected = false;
        }
        
        @Override
        public String toString() {
            if (this.keywordExpected) {
                return String.valueOf(this.keyword) + " expected \n";
            }
            if (this.equalExpected) {
                return String.valueOf(this.keyword) + " '=' expected \n";
            }
            if (this.valueExpected) {
                return String.valueOf(this.keyword) + " value expected \n";
            }
            if (this.openingExpected) {
                return String.valueOf(this.keyword) + " '(' expected \n";
            }
            if (this.closingExpected) {
                return String.valueOf(this.keyword) + " ')' expected \n";
            }
            return "";
        }
        
        public boolean expect(final String token) {
            switch (this.type) {
                case KEYWORD: {
                    return this.keywordExpected && this.keyword.equalsIgnoreCase(token);
                }
                case SIMPLE: {
                    return (this.keywordExpected && this.keyword.equalsIgnoreCase(token)) || this.valueExpected;
                }
                case EQUAL: {
                    return (this.keywordExpected && token.toUpperCase().startsWith(this.keyword)) || this.equalExpected || this.valueExpected;
                }
                case BRACES: {
                    return (this.keywordExpected && token.toUpperCase().startsWith(this.keyword)) || this.openingExpected || this.valueExpected || this.closingExpected;
                }
                default: {
                    return false;
                }
            }
        }
        
        public boolean isSomethingExpected() {
            return this.keywordExpected || this.equalExpected || this.valueExpected || this.openingExpected || this.closingExpected;
        }
        
        public String parse(final String token) {
            switch (this.type) {
                case KEYWORD: {
                    BatchBlockParser.this.resetSubParams();
                    break;
                }
                case SIMPLE: {
                    if (this.keywordExpected) {
                        BatchBlockParser.this.resetSubParams();
                        this.keywordExpected = false;
                        this.valueExpected = true;
                        break;
                    }
                    if (this.valueExpected) {
                        this.valueExpected = false;
                        return token;
                    }
                    break;
                }
                case EQUAL: {
                    if (this.keywordExpected) {
                        this.keywordExpected = false;
                        BatchBlockParser.this.setMainParams(false);
                        if (token.indexOf("=") < 0) {
                            this.equalExpected = true;
                            break;
                        }
                        final String[] parts = token.split("=");
                        if (parts.length > 1) {
                            BatchBlockParser.this.setMainParams(true);
                            return parts[1];
                        }
                        this.valueExpected = true;
                        break;
                    }
                    else if (this.equalExpected) {
                        this.equalExpected = false;
                        if (token.length() > 1) {
                            BatchBlockParser.this.setMainParams(true);
                            return token.substring(1);
                        }
                        this.valueExpected = true;
                        break;
                    }
                    else {
                        if (this.valueExpected) {
                            this.valueExpected = false;
                            BatchBlockParser.this.setMainParams(true);
                            return token;
                        }
                        break;
                    }
                }
                case BRACES: {
                    if (this.keywordExpected) {
                        this.keywordExpected = false;
                        if (token.endsWith("{")) {
                            this.valueExpected = true;
                            break;
                        }
                        final String[] parts = token.split("\\{");
                        if (parts.length <= 1) {
                            this.openingExpected = true;
                            break;
                        }
                        if (parts[1].endsWith("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            BatchBlockParser.this.setMainParams(true);
                            return parts[1].substring(0, parts[1].length() - 1);
                        }
                        this.valueExpected = true;
                        this.closingExpected = true;
                        return parts[1];
                    }
                    else if (this.openingExpected) {
                        this.openingExpected = false;
                        if (token.length() <= 1) {
                            this.valueExpected = true;
                            break;
                        }
                        if (token.endsWith("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            BatchBlockParser.this.setMainParams(true);
                            return token.substring(1, token.length() - 1);
                        }
                        this.valueExpected = true;
                        this.closingExpected = true;
                        return token.substring(1);
                    }
                    else if (this.valueExpected) {
                        if (this.closingExpected && token.equals("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            BatchBlockParser.this.setMainParams(true);
                            break;
                        }
                        if (token.endsWith("}")) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            BatchBlockParser.this.setMainParams(true);
                            return token.substring(0, token.length() - 1);
                        }
                        this.closingExpected = true;
                        return token;
                    }
                    else {
                        if (this.closingExpected) {
                            this.valueExpected = false;
                            this.closingExpected = false;
                            BatchBlockParser.this.setMainParams(true);
                            break;
                        }
                        break;
                    }
                }
            }
            return null;
        }
    }
}

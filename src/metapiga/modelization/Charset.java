// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization;

import javax.swing.text.BadLocationException;
import java.awt.Dimension;
import metapiga.modelization.data.DataType;
import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import metapiga.exceptions.UnknownDataException;
import java.util.BitSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.util.Hashtable;
import javax.swing.text.DefaultStyledDocument;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JDialog;
import metapiga.utilities.Tools;

import java.awt.Frame;
import metapiga.parameters.Parameters;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;

public class Charset implements Comparable<Charset>
{
    public static final String FULL_SET = "FULL SET";
    public static final String REMAINING = "REMAINING";
    private String label;
    private List<Range> characters;
    private boolean isGoingToNexus;
    
    public Charset(final String label) {
        this.isGoingToNexus = true;
        this.label = label;
        this.characters = new LinkedList<Range>();
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public void addRange(final int start, final int end) {
        final Set<Integer> current = new TreeSet<Integer>();
        for (final Range r : this.characters) {
            for (int i = r.start; i <= r.end; ++i) {
                current.add(i);
            }
        }
        for (int j = start; j <= end; ++j) {
            current.add(j);
        }
        this.characters.clear();
        int currentStart = -1;
        int currentEnd = -1;
        for (final int i : current) {
            if (i > 0) {
                if (currentStart == -1) {
                    currentStart = i;
                    currentEnd = i;
                }
                else if (i == currentEnd + 1) {
                    currentEnd = i;
                }
                else {
                    this.characters.add(new Range(currentStart, currentEnd));
                    currentStart = i;
                    currentEnd = i;
                }
            }
        }
        if (currentStart != -1) {
            this.characters.add(new Range(currentStart, currentEnd));
        }
    }
    
    public void removeRange(final int start, final int end) {
        final Set<Integer> current = new TreeSet<Integer>();
        for (final Range r : this.characters) {
            for (int i = r.start; i <= r.end; ++i) {
                current.add(i);
            }
        }
        for (int j = start; j <= end; ++j) {
            current.remove(j);
        }
        this.characters.clear();
        int currentStart = -1;
        int currentEnd = -1;
        for (final int i : current) {
            if (i > 0) {
                if (currentStart == -1) {
                    currentStart = i;
                    currentEnd = i;
                }
                else if (i == currentEnd + 1) {
                    currentEnd = i;
                }
                else {
                    this.characters.add(new Range(currentStart, currentEnd));
                    currentStart = i;
                    currentEnd = i;
                }
            }
        }
        if (currentStart != -1) {
            this.characters.add(new Range(currentStart, currentEnd));
        }
    }
    
    public void addRange(String range) {
        range = range.trim();
        final int slash = range.indexOf("/");
        final int tiret = range.indexOf("-");
        String r = range;
        int step = 1;
        if (slash > 0) {
            final String[] s = range.split("/");
            r = s[0];
            step = Integer.parseInt(s[1]);
        }
        int start;
        int end;
        if (tiret == r.length() - 1) {
            start = Integer.parseInt(r.substring(0, tiret));
            end = Integer.parseInt(r.substring(0, tiret));
        }
        else if (tiret == 0) {
            start = 1;
            end = Integer.parseInt(r.substring(tiret + 1));
        }
        else if (tiret > 0) {
            start = Integer.parseInt(r.substring(0, tiret));
            end = Integer.parseInt(r.substring(tiret + 1));
        }
        else {
            start = Integer.parseInt(r);
            end = Integer.parseInt(r);
        }
        if (step == 1) {
            this.addRange(start, end);
        }
        else {
            for (int i = start; i <= end; i += step) {
                this.addRange(i, i);
            }
        }
    }
    
    public void removeRange(String range) {
        range = range.trim();
        final int slash = range.indexOf("/");
        final int tiret = range.indexOf("-");
        String r = range;
        int step = 1;
        if (slash > 0) {
            final String[] s = range.split("/");
            r = s[0];
            step = Integer.parseInt(s[1]);
        }
        int start;
        int end;
        if (tiret == r.length() - 1) {
            start = Integer.parseInt(r.substring(0, tiret));
            end = Integer.parseInt(r.substring(0, tiret));
        }
        else if (tiret == 0) {
            start = 1;
            end = Integer.parseInt(r.substring(tiret + 1));
        }
        else if (tiret > 0) {
            start = Integer.parseInt(r.substring(0, tiret));
            end = Integer.parseInt(r.substring(tiret + 1));
        }
        else {
            start = Integer.parseInt(r);
            end = Integer.parseInt(r);
        }
        if (step == 1) {
            this.removeRange(start, end);
        }
        else {
            for (int i = start; i <= end; i += step) {
                this.removeRange(i, i);
            }
        }
    }
    
    public void merge(final Charset charset) {
        this.characters.addAll(charset.characters);
    }
    
    public boolean isEmpty() {
        return this.characters.isEmpty();
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public int getSize() {
        int size = 0;
        for (final Range p : this.characters) {
            size += p.end - p.start + 1;
        }
        return size;
    }
    
    public boolean isInCharset(final int character) {
        for (final Range p : this.characters) {
            if (character >= p.start && character <= p.end) {
                return true;
            }
        }
        return false;
    }
    
    public List<Integer> getCharacters() {
        final List<Integer> chars = new ArrayList<Integer>();
        for (final Range p : this.characters) {
            for (int c = p.start; c <= p.end; ++c) {
                chars.add(c);
            }
        }
        return chars;
    }
    
    public boolean intersect(final Charset charset) {
        for (final Range r1 : charset.characters) {
            for (final Range r2 : this.characters) {
                if (r1.start >= r2.start && r1.start <= r2.end) {
                    return true;
                }
                if (r2.start >= r1.start && r2.start <= r1.end) {
                    return true;
                }
                if (r1.end >= r2.start && r1.end <= r2.end) {
                    return true;
                }
                if (r2.end >= r1.start && r2.end <= r1.end) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getAllRanges() {
        String res = "";
        for (final Range p : this.characters) {
            res = String.valueOf(res) + p.toString() + " ";
        }
        return res.trim();
    }
    
    @Override
    public String toString() {
        return this.label;
    }
    
    @Override
    public int compareTo(final Charset c) {
        return this.label.compareTo(c.label);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && obj.getClass() == this.getClass() && this.label.equals(obj.toString()));
    }
    
    @Override
    public int hashCode() {
        int hash = 42;
        hash = 31 * hash + ((this.label == null) ? 0 : this.label.hashCode());
        return hash;
    }
    
    public void setAsNonRecordable() {
        this.isGoingToNexus = false;
    }
    
    public boolean isRecordable() {
        return this.isGoingToNexus;
    }

    private static class Range
    {
        public int start;
        public int end;
        
        public Range(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
        
        public boolean isInRange(final int character) {
            return character >= this.start && character <= this.end;
        }
        
        @Override
        public String toString() {
            return (this.start != this.end) ? (String.valueOf(this.start) + "-" + this.end) : new StringBuilder(String.valueOf(this.start)).toString();
        }
    }
}

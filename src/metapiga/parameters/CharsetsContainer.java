// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.parameters;

import java.awt.Component;
import metapiga.utilities.Tools;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Set;
import metapiga.modelization.Charset;
import java.util.Map;

public class CharsetsContainer
{
    private Map<String, Charset> charsets;
    private Map<String, Charset> binnedCharsets;
    private Set<Charset> excludedCharsets;
    private Set<Charset> partitions;
    private Charset fullDataset;
    private Charset remainingCharset;
    boolean isCodonMode;
    private final Parameters P;
    
    public CharsetsContainer(final Parameters inP) {
        this.charsets = new HashMap<String, Charset>();
        this.binnedCharsets = new HashMap<String, Charset>();
        this.excludedCharsets = new TreeSet<Charset>();
        this.partitions = new TreeSet<Charset>();
        this.fullDataset = null;
        this.remainingCharset = null;
        this.isCodonMode = false;
        this.P = inP;
    }
    
    public void addCharset(final String key, final Charset charset) {
        if (key.contentEquals("REMAINING")) {
            this.remainingCharset = charset;
        }
        else if (key.contentEquals("FULL SET")) {
            this.fullDataset = charset;
        }
        else {
            this.charsets.put(key, charset);
        }
    }
    
    public void addCharsets(final Map<String, Charset> charsetsToAdd) {
        if (charsetsToAdd.containsKey("REMAINING")) {
            this.remainingCharset = charsetsToAdd.get("REMAINING");
            charsetsToAdd.remove("REMAINING");
        }
        else if (charsetsToAdd.containsKey("FULL SET")) {
            this.fullDataset = charsetsToAdd.get("FULL SET");
            charsetsToAdd.remove("FULL SET");
        }
        this.charsets.putAll(charsetsToAdd);
    }
    
    public void binCharset(final String key, final Charset ch) {
        this.binnedCharsets.put(key, ch);
    }
    
    public Map<String, Charset> getCharsets() {
        return new HashMap<String, Charset>(this.charsets);
    }
    
    public Map<String, Charset> getBinned() {
        return new HashMap<String, Charset>(this.charsets);
    }
    
    public Charset getCharset(final String key) {
        Charset retCharset = null;
        if (key.contentEquals("REMAINING")) {
            retCharset = this.remainingCharset;
        }
        else if (key.contentEquals("FULL SET")) {
            retCharset = this.fullDataset;
        }
        else {
            retCharset = this.charsets.get(key);
        }
        return retCharset;
    }
    
    public Set<Charset> getPartitions() {
        return new TreeSet<Charset>(this.partitions);
    }
    
    public Set<Charset> getExcludedCharsets() {
        return new TreeSet<Charset>(this.excludedCharsets);
    }
    
    public Iterator<Charset> getCharsetIterator() {
        return this.charsets.values().iterator();
    }
    
    public Iterator<Charset> getPartitionIterator() {
        return this.partitions.iterator();
    }
    
    public Iterator<Charset> getExcludedCharsetIterator() {
        return this.excludedCharsets.iterator();
    }
    
    public Iterator<Charset> getBinnedCharsetsIterator() {
        return this.binnedCharsets.values().iterator();
    }
    
    public void removePartition(final String removeLabel) {
        if (removeLabel.contentEquals("REMAINING")) {
            final Charset removeCharset = this.remainingCharset;
            this.remainingCharset = null;
            if (removeCharset != null) {
                this.partitions.remove(removeCharset);
            }
        }
        else if (removeLabel.contentEquals("FULL SET")) {
            final Charset removeCharset = this.fullDataset;
            this.fullDataset = null;
            if (removeCharset != null) {
                this.partitions.remove(removeCharset);
            }
        }
        else {
            final Charset removeCharset = this.getCharset(removeLabel);
            this.partitions.remove(removeCharset);
        }
    }
    
    public void removeExcluded(final String removeLabel) {
        final Charset remCharset = this.getCharset(removeLabel);
        this.excludedCharsets.remove(remCharset);
    }
    
    public void excludeCharset(final Charset exCharset) {
        if (this.charsets.containsValue(exCharset)) {
            this.excludedCharsets.add(exCharset);
        }
        else {
            this.unknownCharset();
        }
    }
    
    public boolean contains(final String label) {
        return this.charsets.containsKey(label);
    }
    
    public boolean containsPartition(final String label) {
        final Charset queryCharset = this.getCharset(label);
        if (queryCharset == null) {
            if (label.contentEquals("REMAINING")) {
                if (this.remainingCharset != null && this.partitions.contains(this.remainingCharset)) {
                    return true;
                }
                if (this.fullDataset != null && this.partitions.contains(this.fullDataset)) {
                    return true;
                }
            }
            return false;
        }
        return this.partitions.contains(queryCharset);
    }
    
    public boolean containsPartition(final Charset ch) {
        return this.partitions.contains(ch);
    }
    
    public boolean containsExcluded(final String label) {
        final Charset queryCharset = this.charsets.get(label);
        return queryCharset != null && this.excludedCharsets.contains(queryCharset);
    }
    
    public boolean containsExcluded(final Charset ch) {
        return this.excludedCharsets.contains(ch);
    }
    
    public void excludeCharset(final String charsetLabel) {
        final Charset exCharset = this.getCharset(charsetLabel);
        if (exCharset == null) {
            this.unknownCharset();
        }
        this.excludedCharsets.add(exCharset);
    }
    
    public void replaceExcludedCharsets(final Collection<Charset> exCharsets) {
        this.excludedCharsets.clear();
        this.addExcludedCharsets(exCharsets);
    }
    
    public void addExcludedCharsets(final Collection<Charset> exCharsets) {
        for (final Charset charset : exCharsets) {
            if (this.charsets.containsValue(charset)) {
                this.excludeCharset(charset);
            }
            else {
                this.unknownCharset();
            }
        }
    }
    
    public void partitionCharset(final Charset partCharset) {
        if (partCharset.getLabel().contentEquals("FULL SET")) {
            this.fullDataset = partCharset;
            this.partitions.add(this.fullDataset);
            return;
        }
        if (partCharset.getLabel().contentEquals("REMAINING")) {
            this.remainingCharset = partCharset;
            this.partitions.add(this.remainingCharset);
            return;
        }
        if (this.charsets.containsValue(partCharset)) {
            this.partitions.add(partCharset);
        }
        else {
            this.unknownCharset();
        }
    }
    
    public void partitionCharset(final String label) {
        final Charset partCharset = this.getCharset(label);
        this.partitions.add(partCharset);
    }
    
    public void replacePartitionCharsets(final Collection<Charset> partCharsets) {
        this.partitions.clear();
        this.addPartitionCharsets(partCharsets);
    }
    
    public void addPartitionCharsets(final Collection<Charset> partCharsets) {
        for (final Charset charset : partCharsets) {
            if (this.charsets.containsValue(charset) || charset.getLabel().contentEquals("FULL SET") || charset.getLabel().contentEquals("REMAINING")) {
                this.partitionCharset(charset);
            }
            else {
                this.unknownCharset();
            }
        }
    }
    
    private void unknownCharset() {
        System.err.println("Unknown charset");
    }
    
    public void removeCharset(final String key) {
        final Charset charsetToRemove = this.charsets.get(key);
        if (this.excludedCharsets.contains(charsetToRemove)) {
            this.excludedCharsets.remove(charsetToRemove);
        }
        if (this.partitions.contains(charsetToRemove)) {
            this.partitions.remove(charsetToRemove);
        }
        if (this.binnedCharsets.containsKey(charsetToRemove)) {
            this.binnedCharsets.remove(charsetToRemove);
        }
        if (key.contentEquals("REMAINING")) {
            this.remainingCharset = null;
        }
        else if (key.contentEquals("FULL SET")) {
            this.fullDataset = null;
        }
        else {
            this.charsets.remove(key);
        }
    }
    
    public void clearAll() {
        this.charsets.clear();
        this.partitions.clear();
        this.excludedCharsets.clear();
        this.binnedCharsets.clear();
        this.remainingCharset = null;
        this.fullDataset = null;
    }
    
    public void clearPartitions() {
        this.partitions.clear();
        this.fullDataset = null;
        this.remainingCharset = null;
    }
    
    public void clearExcludedCharsets() {
        this.excludedCharsets.clear();
    }
    
    public int numPartitions() {
        return this.partitions.size();
    }
    
    public int numCharsets() {
        final int numCharsets = this.charsets.size();
        return numCharsets;
    }
    
    public int numExcludedCharsets() {
        return this.excludedCharsets.size();
    }
    
    public boolean isPartitionsEmpty() {
        return this.partitions.isEmpty();
    }
    
    public boolean isExcludedCharsetsEmpty() {
        return this.excludedCharsets.isEmpty();
    }
    
    public boolean isCharsetsEmpty() {
        return this.charsets.isEmpty();
    }
    
    void translateCodonToNucleotideCharsets() {
        int startOfCodonDomain;
        if (this.P.codonDomain != null) {
            startOfCodonDomain = this.P.codonDomain.getStartCodonDomainPosition();
        }
        else {
            startOfCodonDomain = -1;
        }
        for (final String charsetKey : this.charsets.keySet()) {
            final Charset oldCharset = this.charsets.get(charsetKey);
            final Charset newCharset = this.translateBackTheCharsets(startOfCodonDomain, oldCharset);
            if (this.partitions.contains(oldCharset)) {
                this.partitions.remove(oldCharset);
                this.partitions.add(newCharset);
            }
            if (this.excludedCharsets.contains(oldCharset)) {
                this.excludedCharsets.remove(oldCharset);
                this.excludedCharsets.add(newCharset);
            }
            this.charsets.put(charsetKey, newCharset);
        }
        final Collection<String> keySet = new ArrayList<String>(this.binnedCharsets.keySet());
        for (final String key : keySet) {
            final Charset c = this.binnedCharsets.get(key);
            this.binnedCharsets.remove(key);
            this.charsets.put(key, c);
        }
    }
    
    void translateToCodons(final int newFirstPosition, final int newLastPosition) {
        int currentStartPosition;
        boolean areAlreadyCodons;
        if (this.P.codonDomain == null) {
            currentStartPosition = -1;
            areAlreadyCodons = false;
        }
        else {
            currentStartPosition = this.P.codonDomain.getStartCodonDomainPosition();
            areAlreadyCodons = true;
        }
        this.translateToCodonCharsets(newFirstPosition, newLastPosition, currentStartPosition, areAlreadyCodons);
        this.reEvaluateBinnedCharsets(newFirstPosition, newLastPosition, currentStartPosition);
    }
    
    private void binInconsistentCharsets(final int firstPosition, final int lastPosition, final int currentFirstPosition, final boolean areAllreadyCodons) {
        boolean anyNewBinned = false;
        if (this.charsets.isEmpty()) {
            return;
        }
        final Collection<String> keySet = new ArrayList<String>(this.charsets.keySet());
        for (final String key : keySet) {
            Charset charset = this.charsets.get(key);
            final String s = charset.getAllRanges();
            final String[] ranges = s.split(" ");
            String[] array;
            for (int length = (array = ranges).length, i = 0; i < length; ++i) {
                final String range = array[i];
                final String[] startAndEnd = range.split("-");
                assert startAndEnd.length <= 2 : "invalid length";
                int end;
                int start = end = Integer.parseInt(startAndEnd[0]);
                if (startAndEnd.length == 2) {
                    end = Integer.parseInt(startAndEnd[1]);
                }
                assert end >= start : "Error in start end positions";
                if (areAllreadyCodons) {
                    start = this.translateStartPositionCodToNuc(currentFirstPosition, start);
                    end = this.translateEndPositionCodToNuc(currentFirstPosition, end);
                }
                if (start < firstPosition || end > lastPosition || start > lastPosition || end < firstPosition || (start - firstPosition) % 3 != 0 || (end - firstPosition + 1) % 3 != 0) {
                    this.charsets.remove(key);
                    if (this.partitions.contains(charset)) {
                        this.partitions.remove(charset);
                    }
                    if (this.excludedCharsets.contains(charset)) {
                        this.excludedCharsets.remove(charset);
                    }
                    if (areAllreadyCodons) {
                        charset = this.translateBackTheCharsets(this.P.codonDomain.getStartCodonDomainPosition(), charset);
                    }
                    this.binnedCharsets.put(key, charset);
                    anyNewBinned = true;
                    break;
                }
            }
        }
        if (anyNewBinned) {
            final String binnedMessage = "Charset limits are in collision with codons, i.e. some of charset limits are splitting a codon in half. These charsets will be unavailable in the codon mode of the analysis";
            Tools.showWarningMessage(null, binnedMessage, "Warning");
        }
    }
    
    private void reEvaluateBinnedCharsets(final int newFirstPosition, final int newLastPosition, final int currentFirstPosition) {
        if (this.binnedCharsets.isEmpty()) {
            return;
        }
        final Collection<String> keySet = new ArrayList<String>(this.binnedCharsets.keySet());
        for (final String key : keySet) {
            final Charset binned = this.binnedCharsets.get(key);
            final String rangesString = binned.getAllRanges();
            final String[] ranges = rangesString.split(" ");
            String[] array;
            for (int length = (array = ranges).length, i = 0; i < length; ++i) {
                final String range = array[i];
                final String[] startAndEnd = range.split("-");
                assert startAndEnd.length <= 2 : "invalid length";
                int end;
                final int start = end = Integer.parseInt(startAndEnd[0]);
                if (startAndEnd.length == 2) {
                    end = Integer.parseInt(startAndEnd[1]);
                }
                assert end >= start : "Error in start end positions";
                if (start >= newFirstPosition && end <= newLastPosition && start <= newLastPosition && end >= newFirstPosition && (start - newFirstPosition) % 3 == 0 && (end - newFirstPosition + 1) % 3 == 0) {
                    this.binnedCharsets.remove(key);
                    final Charset translatedBinned = this.translateCharsetToCodonCharset(newFirstPosition, currentFirstPosition, false, binned);
                    this.charsets.put(key, translatedBinned);
                }
            }
        }
    }
    
    private void translateToCodonCharsets(final int firstPosition, final int lastPosition, final int currentFirstPosition, final boolean areAllreadyCodons) {
        this.binInconsistentCharsets(firstPosition, lastPosition, currentFirstPosition, areAllreadyCodons);
        for (final String key : this.charsets.keySet()) {
            final Charset charset = this.charsets.get(key);
            final Charset translatedCharset = this.translateCharsetToCodonCharset(firstPosition, currentFirstPosition, areAllreadyCodons, charset);
            this.charsets.put(key, translatedCharset);
            if (this.partitions.contains(charset)) {
                this.partitions.remove(charset);
                this.partitions.add(translatedCharset);
            }
            if (this.excludedCharsets.contains(charset)) {
                this.excludedCharsets.remove(charset);
                this.excludedCharsets.add(translatedCharset);
            }
        }
    }
    
    private Charset translateCharsetToCodonCharset(final int firstPosition, final int currentFirstPosition, final boolean areAllreadyCodons, final Charset charset) throws NumberFormatException {
        final Charset translatedCharset = new Charset(charset.getLabel());
        final String s = charset.getAllRanges();
        final String[] allRanges = s.split(" ");
        String[] array;
        for (int length = (array = allRanges).length, i = 0; i < length; ++i) {
            final String range = array[i];
            final String[] startAndEnd = range.split("-");
            assert startAndEnd.length <= 2 : "invalid length";
            int end;
            int start = end = Integer.parseInt(startAndEnd[0]);
            if (startAndEnd.length == 2) {
                end = Integer.parseInt(startAndEnd[1]);
            }
            if (areAllreadyCodons) {
                start = this.translateStartPositionCodToNuc(currentFirstPosition, start);
                end = this.translateEndPositionCodToNuc(currentFirstPosition, end);
            }
            assert start <= end : "invalid start & end position";
            start = (start - firstPosition) / 3 + 1;
            end = (end - firstPosition + 1) / 3;
            final String translatedRange = String.valueOf(start) + "-" + end;
            translatedCharset.addRange(translatedRange);
        }
        return translatedCharset;
    }
    
    private Charset translateBackTheCharsets(final int startPositionReference, final Charset charset) {
        final Charset translatedCharset = new Charset(charset.getLabel());
        final String rangesStringSet = charset.getAllRanges();
        final String[] ranges = rangesStringSet.split(" ");
        String[] array;
        for (int length = (array = ranges).length, i = 0; i < length; ++i) {
            final String rangeString = array[i];
            final String[] startAndEnd = rangeString.split("-");
            assert startAndEnd.length <= 2 : "invalid length";
            int end;
            int start = end = Integer.parseInt(startAndEnd[0]);
            if (startAndEnd.length == 2) {
                end = Integer.parseInt(startAndEnd[1]);
            }
            start = this.translateStartPositionCodToNuc(startPositionReference, start);
            end = this.translateEndPositionCodToNuc(startPositionReference, end);
            final String newRange = start + "-" + end;
            translatedCharset.addRange(newRange);
        }
        return translatedCharset;
    }
    
    private int translateStartPositionCodToNuc(final int startPos, final int start) {
        return startPos + (start - 1) * 3;
    }
    
    private int translateEndPositionCodToNuc(final int startPos, final int end) {
        return startPos + (end - 1) * 3 + 2;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.likelihood;

public class SequenceLinearArray implements SequenceArrays
{
    private final float[][] sequence;
    private final int numOfCats;
    private final int numOfNodes;
    private final int numCharComponents;
    private final int numCharComponentsWithPadding;
    private final int numOfStates;
    private final int GPUwarpSize;
    
    public SequenceLinearArray(final int numberOfNodes, final int numRateHeterogenityCats, final int numCharComponents, final int numOfStates, final int warpSize) {
        this.GPUwarpSize = warpSize;
        this.numOfCats = numRateHeterogenityCats;
        this.numOfNodes = numberOfNodes;
        this.numCharComponents = numCharComponents;
        this.numCharComponentsWithPadding = (int)(Math.ceil(numCharComponents / this.GPUwarpSize) * this.GPUwarpSize);
        this.numOfStates = numOfStates;
        this.sequence = new float[numberOfNodes][numOfStates * this.numCharComponentsWithPadding * numRateHeterogenityCats];
    }
    
    @Override
    public float getElement(final int node, final int cat, final int character, final int state) {
        final int seqIndex = cat * this.numOfStates * this.numCharComponentsWithPadding + state * this.numCharComponentsWithPadding + character;
        return this.sequence[node][seqIndex];
    }
    
    @Override
    public void setElement(final float value, final int node, final int cat, final int character, final int state) {
        this.sequence[node][cat * this.numOfStates * this.numCharComponentsWithPadding + state * this.numCharComponentsWithPadding + character] = value;
    }
    
    @Override
    public SequenceArrays clone() {
        final SequenceLinearArray seq = new SequenceLinearArray(this.numOfNodes, this.numOfCats, this.numCharComponents, this.numOfStates, this.GPUwarpSize);
        for (int node = 0; node < this.numOfNodes; ++node) {
            System.arraycopy(this.sequence[node], 0, seq.sequence[node], 0, this.numCharComponentsWithPadding * this.numOfCats * this.numOfStates);
        }
        return seq;
    }
    
    @Override
    public void clone(final SequenceArrays seq) {
        if (seq.getCharacterCountNoPadding() != this.numCharComponents || seq.getCategoryCount() != this.numOfCats || seq.getNodeCount() != this.numOfNodes || seq.getStateCount() != this.numOfStates) {
            throw new IndexOutOfBoundsException("SequenceArrays sizes mismatch");
        }
        if (seq instanceof SequenceLinearArray) {
            final SequenceLinearArray s = (SequenceLinearArray)seq;
            for (int node = 0; node < this.numOfNodes; ++node) {
                System.arraycopy(s.sequence[node], 0, this.sequence[node], 0, this.numCharComponentsWithPadding * this.numOfCats * this.numOfStates);
            }
        }
        else {
            for (int cat = 0; cat < this.numOfCats; ++cat) {
                for (int node = 0; node < this.numOfNodes; ++node) {
                    for (int state = 0; state < this.numOfStates; ++state) {
                        for (int character = 0; character < this.numCharComponents; ++character) {
                            this.setElement(seq.getElement(node, cat, character, state), node, cat, character, state);
                        }
                    }
                }
            }
        }
    }
    
    public float[] getSequenceAtNode(final int nodeIdx) {
        return this.sequence[nodeIdx];
    }
    
    public void setSequenceAtNode(final float[] s, final int nodeIndex) {
        final int a = this.sequence[nodeIndex].length;
        final int b = s.length;
        if (s.length != this.sequence[nodeIndex].length) {
            throw new IndexOutOfBoundsException("SequenceArrays sizes mismatch");
        }
        this.sequence[nodeIndex] = s;
    }
    
    public float[] getSubsequenceAtNode(final int nodeIdx, final int charSequenceOffset, final int charSubsetSize) {
        final float[] subsequence = new float[charSubsetSize * this.numOfCats * this.numOfStates];
        final float[] wholeSequence = this.getSequenceAtNode(nodeIdx);
        for (int cat = 0; cat < this.numOfCats; ++cat) {
            for (int state = 0; state < this.numOfStates; ++state) {
                final int seqOffset = cat * this.numOfStates * this.numCharComponentsWithPadding + state * this.numCharComponentsWithPadding + charSequenceOffset;
                final int subsetOffset = cat * this.numOfStates * charSubsetSize + state * charSubsetSize;
                System.arraycopy(wholeSequence, seqOffset, subsequence, subsetOffset, charSubsetSize);
            }
        }
        return subsequence;
    }
    
    public void setSubsequenceAtNode(final int nodeIdx, final int charSequenceOffset, final int charSubsetSize, final float[] subsequence) {
        if (charSubsetSize == this.numCharComponentsWithPadding && charSequenceOffset == 0) {
            this.setSequenceAtNode(subsequence, nodeIdx);
        }
        final float[] wholeSequence = this.getSequenceAtNode(nodeIdx);
        for (int cat = 0; cat < this.numOfCats; ++cat) {
            for (int state = 0; state < this.numOfStates; ++state) {
                final int seqOffset = cat * this.numOfStates * this.numCharComponentsWithPadding + state * this.numCharComponentsWithPadding + charSequenceOffset;
                final int subsetOffset = cat * this.numOfStates * charSubsetSize + state * charSubsetSize;
                System.arraycopy(subsequence, subsetOffset, wholeSequence, seqOffset, charSubsetSize);
            }
        }
    }
    
    @Override
    public int getCategoryCount() {
        return this.numOfCats;
    }
    
    @Override
    public int getCharacterCountNoPadding() {
        return this.numCharComponents;
    }
    
    public int getCharacterCountWithPadding() {
        return this.numCharComponentsWithPadding;
    }
    
    @Override
    public int getNodeCount() {
        return this.numOfNodes;
    }
    
    @Override
    public int getStateCount() {
        return this.numOfStates;
    }
}

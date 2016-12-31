// 
// Decompiled by Procyon v0.5.30
// 

package metapiga.modelization.likelihood;

public class SequenceArrays4Dimension implements SequenceArrays
{
    private final float[][][][] sequence;
    private final int numOfCats;
    private final int numOfNodes;
    private final int numCharComponents;
    private final int numOfStates;
    
    public SequenceArrays4Dimension(final int numberOfNodes, final int numRateHeterogenityCats, final int numCharComponents, final int numOfStates) {
        this.sequence = new float[numRateHeterogenityCats][numberOfNodes][numOfStates][numCharComponents];
        this.numOfCats = numRateHeterogenityCats;
        this.numOfNodes = numberOfNodes;
        this.numCharComponents = numCharComponents;
        this.numOfStates = numOfStates;
    }
    
    @Override
    public float getElement(final int node, final int cat, final int character, final int state) {
        return this.sequence[cat][node][state][character];
    }
    
    @Override
    public void setElement(final float value, final int node, final int cat, final int character, final int state) {
        this.sequence[cat][node][state][character] = value;
    }
    
    @Override
    public SequenceArrays clone() {
        final SequenceArrays4Dimension copySequence = new SequenceArrays4Dimension(this.numOfNodes, this.numOfCats, this.numCharComponents, this.numOfStates);
        for (int i = 0; i < this.numOfCats; ++i) {
            for (int j = 0; j < this.numOfNodes; ++j) {
                for (int k = 0; k < this.numOfStates; ++k) {
                    System.arraycopy(this.sequence[i][j][k], 0, copySequence.sequence[i][j][k], 0, this.numCharComponents);
                }
            }
        }
        return copySequence;
    }
    
    @Override
    public void clone(final SequenceArrays seq) {
        if (seq.getCharacterCountNoPadding() != this.numCharComponents || seq.getCategoryCount() != this.numOfCats || seq.getNodeCount() != this.numOfNodes || seq.getStateCount() != this.numOfStates) {
            throw new IndexOutOfBoundsException("SequenceArrays sizes mismatch");
        }
        if (seq instanceof SequenceArrays4Dimension) {
            final SequenceArrays4Dimension s = (SequenceArrays4Dimension)seq;
            for (int cat = 0; cat < this.numOfCats; ++cat) {
                for (int nodes = 0; nodes < this.numOfNodes; ++nodes) {
                    for (int state = 0; state < this.numOfStates; ++state) {
                        System.arraycopy(s.sequence[cat][nodes][state], 0, this.sequence[cat][nodes][state], 0, this.numCharComponents);
                    }
                }
            }
        }
        else {
            for (int cat2 = 0; cat2 < this.numOfCats; ++cat2) {
                for (int node = 0; node < this.numOfNodes; ++node) {
                    for (int state2 = 0; state2 < this.numOfStates; ++state2) {
                        for (int character = 0; character < this.numCharComponents; ++character) {
                            this.sequence[cat2][node][state2][character] = seq.getElement(node, cat2, character, state2);
                        }
                    }
                }
            }
        }
    }
    
    public final float[][] getSequenceAtCategoryAndNode(final int cat, final int node) {
        return this.sequence[cat][node];
    }
    
    public final void setSequenceAtNodeInCategory(final float[][] seq, final int cat, final int node) {
        this.sequence[cat][node] = seq;
    }
    
    @Override
    public int getCategoryCount() {
        return this.numOfCats;
    }
    
    @Override
    public int getCharacterCountNoPadding() {
        return this.numCharComponents;
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

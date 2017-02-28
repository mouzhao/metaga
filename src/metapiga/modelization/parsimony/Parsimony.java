package metapiga.modelization.parsimony;

import metapiga.modelization.Charset;
import metapiga.trees.Node;

import java.util.*;
import java.io.*;
import metapiga.modelization.data.Data;
import metapiga.modelization.Dataset;
import metapiga.parameters.Parameters;
import metapiga.trees.Tree;
import metapiga.trees.exceptions.NullAncestorException;

/**
 * Created by zhao on 2016/11/17.
 */
public class Parsimony {

    private Parameters parameters;
    protected final Set<Node> toUpdate;
    protected final Tree tree;
    protected final int numOfStates;
    protected final int numOfChar;
    private final int numOfNodes;
    protected int parsimonyValue;
    
    protected String[][] seqParStatus;
    protected int[][] seqParValue;
    protected Map<Node,Integer> nodeIndex;

    protected final Dataset.Partition allTaxa;

    public Parsimony(Parameters p,Tree tree) {
        this.parameters = p;
        this.tree = tree;
        this.numOfChar = this.parameters.dataset.getCompressedNChar();
        this.numOfStates = this.parameters.dataset.getDataType().numOfStates();
        this.numOfNodes = tree.getNumOfNodes();
        this.nodeIndex = new HashMap<Node,Integer>();
        this.seqParStatus = new String[this.numOfNodes][this.numOfChar];
        this.seqParValue = new int[this.numOfNodes][this.numOfChar];
        this.toUpdate = new HashSet<Node>();
        this.allTaxa = this.parameters.dataset.getPartition(new Charset("FULL SET"));

        int nodeCounter = 0;
        for(Node node:this.tree.getNodes()){
            this.nodeIndex.put(node,nodeCounter);
            if(node.isLeaf()){
                for(int i=0;i<this.numOfChar;i++){
                    this.seqParStatus[nodeCounter][i]= allTaxa.getAllData(node.getLabel()).get(i).getState()+"";
                    this.seqParValue[nodeCounter][i] = 0;
                }
            }else{
                toUpdate.add(node);
            }
            nodeCounter++;
        }
    }

    public int getParsimonyValue() throws NullAncestorException {
        this.markAllInodesToUpdate();
        if (!this.toUpdate.isEmpty()) {
            this.update(this.tree.getRoot());
            this.parsimonyValue = this.calParsimonyValueForTree(this.tree.getRoot());
        }
        return this.parsimonyValue;
    }

    public void update(final Node node) throws NullAncestorException {
        for (final Node child : node.getChildren()) {
            if (this.toUpdate.contains(child)) {
                this.update(child);
            }
        }
        this.calParsimonyValueForNode(node);
        this.toUpdate.remove(node);
    }

    protected void calParsimonyValueForNode(Node node){
        int indexOfNode = this.nodeIndex.get(node);
        for(int i=0;i<this.numOfChar;i++){
            Set<Integer> intersection = this.intersection(node.getChildren(),i);
            if(intersection.isEmpty()){
                this.seqParStatus[indexOfNode][i] = setToString(union(node.getChildren(),i));
                this.seqParValue[indexOfNode][i] = (this.sumOfPValue(node.getChildren(),i)+1);
            }else{
                this.seqParStatus[indexOfNode][i] = setToString(intersection);
                this.seqParValue[indexOfNode][i] = this.sumOfPValue(node.getChildren(),i);
            }
        }
    }

    protected int calParsimonyValueForTree(Node node){
        int pValue =0;
        for(int i=0;i<this.numOfChar;i++){
            pValue = pValue + this.seqParValue[nodeIndex.get(node)][i]*this.allTaxa.getWeight(i);
        }
        return pValue;
    }

    protected Set<Integer> intersection(List<Node> nodeList,int site){
        Set<Integer> intersection = new HashSet<Integer>();
        intersection.addAll(stringToSet(this.seqParStatus[this.nodeIndex.get(nodeList.get(0))][site]));
        for(int i=1;i<nodeList.size();i++){
            intersection.retainAll(stringToSet(this.seqParStatus[this.nodeIndex.get(nodeList.get(i))][site]));
        }
        return intersection;
    }

    protected Set<Integer> union(List<Node> nodeList,int site){
        Set<Integer> union = new HashSet<>();
        for(Node node : nodeList){
            union.addAll(stringToSet(this.seqParStatus[this.nodeIndex.get(node)][site]));
        }
        return union;
    }

    protected Set<Integer> stringToSet(String status){
        Set<Integer> statusSet = new HashSet<Integer>();
        String[] s = status.split(",");
        for(String a : s){
            statusSet.add(Integer.parseInt(a));
        }
        return statusSet;
    }

    protected String setToString(Set<Integer> statusSet){
        String status = "";

        Iterator it =  statusSet.iterator();
        while(it.hasNext()){
            String s = (int)it.next()+"";
            status = status + s + ",";
        }
        return status.substring(0,status.length()-1);
    }

    protected int sumOfPValue(List<Node> nodeList,int site){
        int pValue = 0;
        for(Node node : nodeList){
            pValue = pValue + this.seqParValue[nodeIndex.get(node)][site];
        }
        return pValue;
    }

    public void markInodeToUpdate(final Node node) {
        if (node.isInode()) {
            this.toUpdate.add(node);
        }
    }

    public void markAllInodesToUpdate() {
        for (final Node n : this.tree.getInodes()) {
            if (n.isInode()) {
                this.toUpdate.add(n);
            }
        }
    }
}

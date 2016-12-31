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
//public class Parsimony implements Serializable {
public class Parsimony {

//    private int pValueForNode;
//    private int[] pValueForSite;
//
//
//    private boolean isChanged;
//    private Node node;
//    public List<Set<Data>> status;
//    private int nchar;
//    private Dataset dataset;

    private Parameters parameters;
    protected final Set<Node> toUpdate;
    protected final Tree tree;
    protected final int numStates;
    protected final int numChar;
    protected int parsimonyValue;

    //for each node
    //protected final Map<Integer,Set<Integer>> siteStatus;
    protected final Map<Integer,Integer> sitePValue;
    //for tree
    protected final Map<Node,Map<Integer,Set<Integer>>> nodeStatus;
    protected final Map<Node,Map<Integer,Integer>> nodePValue;

    protected final Dataset.Partition allTaxa;

    public Parsimony(Parameters p,Tree tree) {
        this.parameters = p;
        this.tree = tree;
        this.numChar = this.parameters.dataset.getNChar();
        this.numStates = this.parameters.dataset.getDataType().numOfStates();

        this.toUpdate = new HashSet<Node>();

        this.sitePValue = new HashMap<Integer,Integer>();
        this.nodeStatus = new HashMap<Node,Map<Integer,Set<Integer>>>();
        this.nodePValue = new HashMap<Node,Map<Integer,Integer>>();
        this.allTaxa = this.parameters.dataset.getPartition(new Charset("FULL SET"));

        for(Node node:this.tree.getNodes()){
            if(node.isLeaf()){
                Map<Integer,Set<Integer>> siteStatus = new HashMap<Integer,Set<Integer>>();
                for(int i=0;i<this.numChar;i++){
                    Set<Integer> statusSet = new HashSet<Integer>();
                    int compIndex = 0;
                    while(compIndex<numChar){
                        if(allTaxa.getDatasetPosition(compIndex).contains(i)){
                            break;
                        }
                        compIndex++;
                    }
                    statusSet.add(allTaxa.getAllData(node.getLabel()).get(compIndex).getState());
                    siteStatus.put(i,statusSet);
                    this.sitePValue.put(i,0);
                }
                this.nodeStatus.put(node,siteStatus);
                this.nodePValue.put(node,sitePValue);
            }else{
                toUpdate.add(node);
            }
        }
    }

    public int getParsimonyValue() throws NullAncestorException {
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
                this.toUpdate.remove(child);
            }
        }
        this.calParsimonyValueForNode(node);
    }

    protected void calParsimonyValueForNode(Node node){
        Map<Integer,Set<Integer>> statusSet = new HashMap<Integer,Set<Integer>>();
        Map<Integer,Integer> pValueSet = new HashMap<>();
        for(int i=0;i<this.numChar;i++){
            Set<Integer> intersection = this.intersection(node.getChildren(),i);
            if(intersection.isEmpty()){
                statusSet.put(i,union(node.getChildren(),i));
                pValueSet.put(i,this.sumOfPValue(node.getChildren(),i));
            }else{
                statusSet.put(i,intersection);
                pValueSet.put(i,this.sumOfPValue(node.getChildren(),i)+1);
            }
        }
        this.nodeStatus.put(node,statusSet);
        this.nodePValue.put(node,pValueSet);

    }

    protected int calParsimonyValueForTree(Node node){
        int pValue =0;
        for(int i=0;i<this.numChar;i++){
            pValue = pValue + this.nodePValue.get(node).get(i);
        }
        return pValue;
    }

    protected Set<Integer> intersection(List<Node> nodeList,int site){
        Set<Integer> intersection = new HashSet<Integer>();
        intersection.addAll(this.nodeStatus.get(nodeList.get(0)).get(site));
        for(int i=1;i<nodeList.size();i++){
            intersection.retainAll(this.nodeStatus.get(nodeList.get(i)).get(site));
        }
        return intersection;
    }

    protected Set<Integer> union(List<Node> nodeList,int site){
        Set<Integer> uion = new HashSet<>();
        for(Node node : nodeList){
            uion.addAll(this.nodeStatus.get(node).get(site));
        }
        return uion;
    }

    protected int sumOfPValue(List<Node> nodeList,int site){
        int pValue = 0;
        for(Node node : nodeList){
            pValue = pValue + this.nodePValue.get(node).get(site);
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
//    public Object Clone() throws IOException, ClassNotFoundException {
//        //将对象写到流里
//        ByteArrayOutputStream bo = new ByteArrayOutputStream();
//        ObjectOutputStream oo = new ObjectOutputStream(bo);
//        oo.writeObject(this);
//        //从流里读出来
//        ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
//        ObjectInputStream oi=new ObjectInputStream(bi);
//        return(oi.readObject());
//    }
}

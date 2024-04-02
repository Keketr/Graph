import java.util.ArrayList;

public class Node {
    private int id;         // id of the node
    private int weight;     // time needed to complete the task
    private int rank;       // rank of the node
    private int numOfPredecessors; // number of predecessors of the node
    private ArrayList<Integer> predecessors; // list of all the task required to be completed before this task can be
                                            // started
    private ArrayList<Integer> successors;   // list of all the tasks that can be started after this task is completed
    private int earliestStart;  // earliest start time of the task
    private int latestStart; // latest start time of the task
    public Node(int id, int weight, ArrayList<Integer> predecessors) { // constructor
        this.id = id;
        this.weight = weight;
        this.predecessors = predecessors;
        this.successors = new ArrayList<Integer>();
        this.rank = 0;
        this.numOfPredecessors = predecessors.size();
        this.earliestStart = 0;
        this.latestStart = Integer.MAX_VALUE;
    }


    public int getId() {
        return id;
    }
    public int getWeight() {
        return weight;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public int getEarliestStart() {
        return earliestStart;
    }
    public void setEarliestStart(int earliestStart) {
        this.earliestStart = earliestStart;
    }
    public int getLatestStart() {
        return latestStart;
    }
    public void setLatestStart(int latestStart) {
        this.latestStart = latestStart;
    }
    public int getNumOfPredecessors() {
        return numOfPredecessors;
    }
    public void setNumOfPredecessors(int numOfPredecessors) {
        this.numOfPredecessors = numOfPredecessors;
    }
    public int getMargin(){
        return latestStart - earliestStart;
    }
    public ArrayList<Integer> getPredecessors() {
        return predecessors;
    }
    public void removePredecessor(int predecessor){
        this.predecessors.remove(predecessor);
    }
    public ArrayList<Integer> getSuccessors() {
        return successors;
    }
    public void addSuccessor(int successor){
        this.successors.add(successor);
    }

    public String toString(){
        return "id: " + id + " weight: " + weight + " rank: " + rank + " predecessors: " + predecessors +
                " successors: " + successors + " earliestStart: " + earliestStart + " latestStart: " + latestStart
                + " margin: " + getMargin();
    }

}

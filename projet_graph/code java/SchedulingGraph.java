import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.LinkedList;
import java.util.List;

public class SchedulingGraph {
    private ArrayList<Node> nodes; // list of all the nodes in the
    private int maxRank; // maximum rank of the graph
    private boolean cyclic,negativeWeight; // boolean to know if the graph is cyclic or if there is a negative weight

    public SchedulingGraph(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
    public SchedulingGraph() {
        this.nodes = new ArrayList<Node>();
    }
    public SchedulingGraph(SchedulingGraph SG){
        this.nodes = new ArrayList<Node>();
        for(Node node : SG.getNodes()){
            this.nodes.add(new Node(node.getId(), node.getWeight(), node.getPredecessors()));
        }
    }
    public String toString(){
        String result = "";
        for(Node node : nodes) {
            result += node.toString() + "\n";
        }
        return result;
    }
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public boolean getCyclic() {
        return cyclic;
    }
    public boolean getNegativeWeight() {
        return negativeWeight;
    }
    public Node getNode(int id){
        for(Node node : nodes){
            if (node.getId() == id){
                return node;
            }
        }
        return null;
    }
    public void addNode(Node node) {
        this.nodes.add(node);
    }
    public void addSuccessor(){
        for (Node node : nodes){
            for (Node node2 : nodes){
                if (node2.getPredecessors().contains(node.getId()) && !node.getSuccessors().contains(node2.getId())){
                    node.addSuccessor(node2.getId());
                }
            }
        }
    }
    public void addEntryAndExitNodes(){
        addSuccessor();

        /** Entry node **/

        for(Node node : nodes){
            if (node.getPredecessors().size() == 0){
                node.getPredecessors().add(0);       // add the entry node as a predecessor of the node
            }
        }
        nodes.add(0, new Node(0,0,new ArrayList<>())); // add the entry node to the list of nodes

        /** Exit node **/
        ArrayList<Integer> predecessorsOfExit = new ArrayList<Integer>(); // list of all the predecessors of the exit node
        for(Node node : nodes){
            if (node.getSuccessors().size() == 0 && node.getId()!=0){ // we look for nodes that have no successors and that are not the entry node
                predecessorsOfExit.add(node.getId()); // add the node to the list of predecessors of the exit node
            }
        }
        nodes.add(new Node(nodes.size(),0,predecessorsOfExit)); // add the exit node to the list of nodes

        addSuccessor();

    }



    public void calculateNumOfPredecessors(){
        for(Node node : nodes){
            node.setNumOfPredecessors(node.getPredecessors().size());
        }
    }
    public boolean checkNegativeWeight(){  //return false if there is a negative weight
        for(Node node : nodes){
            if (node.getWeight() < 0){
                negativeWeight = true;
                return false;
            }
        }
        negativeWeight = false;
        return true;
    }
    public ArrayList<Node> listOfEntry(){
        ArrayList<Node> result = new ArrayList<Node>();
        for(Node node : nodes){
            if (node.getNumOfPredecessors()==0){
                result.add(node);
            }
        }
        return result;
    }

    private void dispRemainingNodes(){
        String result = "Remaining nodes : ";
        boolean empty = true;
        for(Node node : nodes){
            if(node.getNumOfPredecessors() != -1){
                empty = false;
                result += node.getId() + " ";
            }
        }
        if(!empty){
            System.out.println(result);
        }
    }
    private void dispEntryNodes(){
        String result = "Entry Nodes : ";
        boolean empty = true;
        for(Node node : listOfEntry()){
            empty = false;
            result += node.getId() + " ";
        }
        if(!empty){
            System.out.println(result);
        }
    }

    public boolean calculateRank(){     // return false if the graph is cyclic
        calculateNumOfPredecessors();
        int k = 0; //rank
        int n = 0; //keep tracks of the number of nodes to be able to tell if the graph is cyclic
        dispEntryNodes(); /** to understand the algorithm **/
        while (!listOfEntry().isEmpty()){
            System.out.println("deleting Entry nodes"); /** to understand the algorithm **/
            for(Node node : listOfEntry()){
                n++;
                node.setRank(k);
                node.setNumOfPredecessors(-1); // we set the number of predecessors to -1 to avoid counting it again
                for (Integer successor : node.getSuccessors()) {
                    getNode(successor).setNumOfPredecessors(getNode(successor).getNumOfPredecessors() - 1);
                }
            }
            k++;
            dispRemainingNodes(); /** to understand the algorithm **/
            dispEntryNodes(); /** to understand the algorithm **/

        }
        maxRank = k-1;
        if(n != nodes.size()){
            System.out.println("The graph is cyclic");
            cyclic = true;
            return false;
        }
        System.out.println("The Rank have been calculated");
        cyclic = false;
        return true;
    }

    public void calculateEarliestStart(){
        orderByRank();
        for(Node node : nodes){
            int max = node.getEarliestStart();
            for(Integer predecessor : node.getPredecessors()){
                if (getNode(predecessor).getEarliestStart() + getNode(predecessor).getWeight() > max){
                    max = getNode(predecessor).getEarliestStart() + getNode(predecessor).getWeight();
                }
            }
            node.setEarliestStart(max);
            if(node.getId()== nodes.size()-1){
                node.setLatestStart(max);
            }
        }
    }

    public void calculateLatestStart(){
        orderByRank();
        for(int i = nodes.size()-1; i >= 0; i--){
                int min = nodes.get(i).getLatestStart();
                for(Integer successor : nodes.get(i).getSuccessors()){
                    if (getNode(successor).getLatestStart() - nodes.get(i).getWeight() < min){
                        min = getNode(successor).getLatestStart() - nodes.get(i).getWeight();
                    }
                }
                nodes.get(i).setLatestStart(min);
        }
    }

    public void orderByRank(){
        ArrayList<Node> result = new ArrayList<Node>();
        for(int i = 0; i <= maxRank; i++){
            for(Node node : nodes){
                if (node.getRank() == i){
                    result.add(node);
                }
            }
        }
        this.nodes=result;
    }

    //DEPRECATED NEED TO BE REWRITTEN
    //parcours en profondeur si la file n'est pas vide il y a une autre chaine
    //on s'arrete quand tous les sommets de marge 0 sont parcourues

    //node.getSuccesors()
    //node.getMargin()
    //
    //getNode()  node from id
    //
    public int totalDuration(List<List<Integer>> allPaths){
        int totalDuration = 0;
        for(List<Integer> numNode : allPaths){
            int temp = 0;
            for(Integer weight : numNode){
                temp += getNode(weight).getWeight();
            }
            if(temp > totalDuration){
                totalDuration = temp;
            }
        }
        return totalDuration;
    }

    public List<Integer> isNewPath(List<Integer> currentPath, List<Integer> passedNodes){
        for(Integer node : currentPath){
            if(!passedNodes.contains(node)){
                passedNodes.add(node);
            }
        }
        return passedNodes;
    }

    public List<List<Integer>> getAllPaths(int start, int end) {
        List<List<Integer>> allPaths = new ArrayList<>();
        List<List<Integer>> criticalPaths = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(start);
        getAllPathsRecursive(start, end, currentPath, allPaths);
        int longest = totalDuration(allPaths);
        List<Integer> passedNodes = new ArrayList<>();
        Integer node_number = passedNodes.size();
        for(List<Integer> numNode : allPaths){
            int temp = 0;
            for(Integer weight : numNode){
                temp += getNode(weight).getWeight();
            }
            if(temp == longest){
                passedNodes = isNewPath(numNode, passedNodes);
                if(node_number != passedNodes.size()){
                    node_number = passedNodes.size();
                    criticalPaths.add(numNode);}
            }
        }
            return criticalPaths;
    }

    private void getAllPathsRecursive(int current, int end, List<Integer> currentPath, List<List<Integer>> allPaths) {
        if (current == end) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }
        Node node = getNode(current);
        List<Integer> successors = node.getSuccessors();

        for (Integer successor : successors){
            if (getNode(successor).getEarliestStart() == getNode(successor).getLatestStart()){
                currentPath.add(successor);
                getAllPathsRecursive(successor, end, currentPath, allPaths);
                currentPath.remove(successor);
            }


        }
    }


    public String dispCriticalPath(){
        String res= "";
        List<List<Integer>> critpath = getAllPaths(0,this.nodes.size()-1);
        //for(int i = 0; i < critpath.size()-1; i++){
        //    res += critpath.get(i).getId() + " -> " + critpath.get(i+1).getId() + "\n";
        //}

        for (List<Integer> list : critpath ){
            for (Integer ID : list){
                res += Integer.toString(ID);
                if (ID != this.nodes.size()-1) {
                    res += "->";
                }
            }
            res += "\n-------------------------------- \n";
        }

        return res;
    }


    public void dispGraphInfo(){
        System.out.println("Number of nodes: " + nodes.size());
        int nbEdges = 0;
        for (Node node : nodes){
            nbEdges += node.getPredecessors().size();
        }
        System.out.println("Number of edges: " + nbEdges);
        for (Node node : nodes){
            for (Integer successor : node.getSuccessors()){
                System.out.println(node.getId() + " -> " + getNode(successor).getId() + " = " + node.getWeight());
            }
        }
    }

    public String[][] toTable(){
        String[] columnNames = {"ID","Weight","Predecessors","Rank","Earliest Start","Latest Start","Margin"};
        String[][] result = new String[nodes.size()+1][7];
        result[0] = columnNames;
        for(int i = 1; i < nodes.size()+1; i++){
            result[i][0] = Integer.toString(nodes.get(i-1).getId());
            result[i][1] = Integer.toString(nodes.get(i-1).getWeight());
            result[i][2] = nodes.get(i-1).getPredecessors().toString();
            result[i][3] = Integer.toString(nodes.get(i-1).getRank());
            result[i][4] = Integer.toString(nodes.get(i-1).getEarliestStart());
            result[i][5] = Integer.toString(nodes.get(i-1).getLatestStart());
            result[i][6] = Integer.toString(nodes.get(i-1).getMargin());
        }
        return result;
    }
}

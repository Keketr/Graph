import java.util.ArrayList;

public class SchedulingMatrix {
    private ArrayList<ArrayList<Integer>> matrix; // matrix of the graph


    public SchedulingMatrix(SchedulingGraph SG) {
        this.matrix = new ArrayList<ArrayList<Integer>>(); // initialize the matrix
        for (int i = 0; i < SG.getNodes().size(); i++) {        // we use the size of the list of nodes to know the size of the matrix
            ArrayList<Integer> row = new ArrayList<Integer>();
            for (int j = 0; j < SG.getNodes().size(); j++) {
                row.add(-1);        // fill the matrix with -1. -1 means that there is no edge between the two nodes
                                    // it is replaced by "*" in the toString method
            }
            matrix.add(row);
        }
        for (Node node : SG.getNodes()) {
            for (int predecessor : node.getPredecessors()) {
                matrix.get(predecessor).set(node.getId(), SG.getNode(predecessor).getWeight());
                // we set the weight of the edge between the predecessor and the node
            }
        }
    }

    private String printInt(int i){ // method to print an integer in the matrix with the right number of spaces to keep the matrix aligned
        if (i > 9){
            return i + " ";
        } else if(i == -1){
            return "*  ";
        } else {
            return i + "  " ;
        }
    }
    public String toString() {
        String result = "   ";
        for (int i = 0; i < matrix.size(); i++) {
            result += printInt(i);
        }
        result += "\n";
        int j=0;
        for (ArrayList<Integer> row : matrix) {
            result += printInt(j);
            j++;
            for (int i : row) {
                result += printInt(i);
            }
            result += "\n";
        }
        return result;
    }

    public ArrayList<ArrayList<Integer>> getMatrix() {
        return matrix;
    }

    public String[][] toTable(){
        String[][] table = new String[matrix.size()+1][matrix.size()+1];
        table[0][0] = " ";
        for (int i = 0; i < matrix.size(); i++) {
            table[0][i+1] = Integer.toString(i);
            table[i+1][0] = Integer.toString(i);
        }
        int j=0;
        for (ArrayList<Integer> row : matrix) {
            j++;
            for (int i = 0; i < row.size(); i++) {
                if(row.get(i) == -1){
                    table[j][i+1] = "*";
                } else {
                    table[j][i+1] = Integer.toString(row.get(i));
                }
            }
        }
        return table;
    }

}

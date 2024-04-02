import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// Class designed to read and process a graph structure from a file.

public class GraphReader {

    // Reads a graph from a file, constructing a SchedulingGraph object from the data.
  
    public static SchedulingGraph readFile(String fileLocation){
        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
            // Try-with-resources ensures the BufferedReader is closed after use, preventing resource leaks.
            String line;
            SchedulingGraph SG = new SchedulingGraph(); 
            while ((line = br.readLine()) != null) { 
                String[] lineSplit = line.split(" "); 
                int id = Integer.parseInt(lineSplit[0]); // The first element is the node's ID.
                int weight = Integer.parseInt(lineSplit[1]); // The second element represents the node's weight.
                ArrayList<Integer> predecessors = new ArrayList<Integer>(); 
                
                for (int i = 2; i < lineSplit.length; i++){
                    predecessors.add(Integer.parseInt(lineSplit[i])); // Adds each predecessor ID to the list.
                }
                // Adds a new node to the graph with the parsed data.
                SG.addNode(new Node(id, weight, predecessors));
            }

            
            System.out.println("Graph read from file:");
            SG.addSuccessor(); // Constructs a list of successors for each node based on the read predecessors.
            SG.dispGraphInfo(); // Displays basic information about the graph, such as number of nodes and edges.
            SG.addEntryAndExitNodes(); // Adds synthetic entry and exit nodes to simplify certain graph algorithms.
            System.out.println("we add the entry and exit node:");
            SG.dispGraphInfo(); // Displays the graph info again, now including the entry and exit nodes.
            SG.checkNegativeWeight(); // Checks for negative weights, which could indicate an error in scheduling contexts.
            SG.calculateRank(); // Calculates a ranking for nodes that can be used for scheduling or ordering.
            SG.calculateEarliestStart(); // Calculates the earliest start time for each node based on dependencies.
            SG.calculateLatestStart(); // Calculates the latest start time each node can begin without delaying the project.
            SG.dispCriticalPath(); // Displays the critical path, the longest path through the graph which determines the minimum project duration.
            SG.orderByRank(); // Orders nodes by their rank, potentially for processing or scheduling.

            return SG; 
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        return null; 
    }
}

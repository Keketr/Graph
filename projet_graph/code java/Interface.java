import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.TableColumn;

public class Interface {
    // Declaration of GUI components
    private JFrame frame;
    private JTextArea textArea, textArea2; // Text areas for displaying data
    private File selectedFile; // File selected by the user
    private SchedulingGraph SG; // Representation of the scheduling graph
    private JTable nodeTable; // Table to display node information
    private JTable nodeMatrix; // Table to display the adjacency matrix
    // Panels to contain the tables, making it easier to update them
    private JPanel nodeTablePanel;
    private JPanel nodeMatrixPanel;

    public Interface() {
        frame = new JFrame("Graph Reader"); // Initializing the main window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        frame.setSize(1400, 800); // Window size
        frame.setLayout(null); // No layout manager, using absolute positioning

        textArea = new JTextArea(); // Initializing text area for file content
        textArea.setEditable(false); // Make text area non-editable
        JScrollPane scrollPane = new JScrollPane(textArea); 
        scrollPane.setBounds(20, 20, 250, 400);
        frame.add(scrollPane); 

        JMenuBar menuBar = new JMenuBar(); 
        JMenu fileMenu = new JMenu("File"); 
        JMenuItem openItem = new JMenuItem("Open"); // Creating an open item for file menu
        JButton runButton = new JButton("Run"); // Button to process the graph
        runButton.setBounds(20, 450, 250, 30); 
        frame.add(runButton); 

        // Adding action listener to the run button
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) { // Check if a file has been selected
                    SG = GraphReader.readFile(selectedFile.getAbsolutePath()); // Read and process the file
                    if (SG.getCyclic() || SG.getNegativeWeight()) { // Check for errors in the graph
                        JOptionPane.showMessageDialog(frame, "The graph is cyclic or contains negative weight, please input another file");
                        return;
                    }

                    // Remove previous components if present
                    if (nodeTablePanel != null) {
                        frame.remove(nodeTablePanel);
                    }
                    if (nodeMatrixPanel != null) {
                        frame.remove(nodeMatrixPanel);
                    }
                    if (textArea2 != null) {
                        frame.remove(textArea2);
                    }

                    // Creating and adding new components with updated data
                    updateNodeTable();
                    updateNodeMatrix();
                    updateCriticalPathTextArea();

                    frame.revalidate(); // Revalidating the frame to include new components
                    frame.repaint(); // Repainting the frame to show updates
                }
            }
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Setting the look and feel
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adding action listener to the open item in file menu
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(); // File chooser for selecting a file
                // Setting the directory to the user's home directory
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Documents"));
                int result = fileChooser.showOpenDialog(frame); // Showing the file chooser dialog
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile(); 
                    if (selectedFile.getName().endsWith(".txt")) { // Check if the file is a .txt file
                        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                            String line;
                            textArea.setText(""); // Clearing the text area
                            while ((line = reader.readLine()) != null) {
                                textArea.append(line + "\n"); // Displaying the file content in text area
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(frame, "An error occurred while reading the file", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select a .txt file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        fileMenu.add(openItem); // Adding open item to the file menu
        menuBar.add(fileMenu); // Adding file menu to the menu bar
        frame.setJMenuBar(menuBar); 
        frame.setVisible(true); 
    }

    // Method to update the node table with current graph data
    private void updateNodeTable() {
        String[] columnNames = {"ID", "Weight", "Predecessors", "Rank", "Earliest Start", "Latest Start", "Margin"};
        nodeTable = new JTable(SG.toTable(), columnNames); // Creating a new table with graph data
        nodeTablePanel = new JPanel();
        nodeTablePanel.setBounds(300, 20, 520, 300); // Positioning the panel
        nodeTablePanel.add(new JScrollPane(nodeTable)); // Adding the table to the panel
        frame.add(nodeTablePanel); 
    }

    // Method to update the node matrix with current graph data
    private void updateNodeMatrix() {
        String[] ColumnNames = new String[SG.getNodes().size() + 1];
        for (int i = 0; i < SG.getNodes().size() + 1; i++) {
            ColumnNames[i] = " ";
        }
        nodeMatrix = new JTable(new SchedulingMatrix(SG).toTable(), ColumnNames); // Creating a new matrix table
        nodeMatrixPanel = new JPanel(); // New panel for the matrix table
        nodeMatrixPanel.setBounds(300, 350, 480, 400); // Positioning the panel
        nodeMatrixPanel.add(new JScrollPane(nodeMatrix)); // Adding the matrix table to the panel
        frame.add(nodeMatrixPanel); 
    }

    // Method to update the critical path text area with current graph data
    private void updateCriticalPathTextArea() {
        textArea2 = new JTextArea(SG.dispCriticalPath()); // Creating a new text area for critical path
        textArea2.setBounds(850, 20, 500, 350); 
        frame.add(textArea2); 
    }

    public static void main(String[] args) {
        new Interface(); 
    }
}

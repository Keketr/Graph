// Importing necessary packages for the GUI, events, file reading, etc.
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

// Declaration of the main class of the user interface.
public class Interface{
    // Declaration of GUI components.
    private JFrame frame; // The main window.
    private JTextArea textArea, textArea2; // Text areas for displaying data.

    private File selectedFile; 

    private SchedulingGraph SG; // An instance of SchedulingGraph class to handle the graphs.
    private JTable nodeTable; 

    private JTable nodeMatrix; 

    
    public Interface() {
        // Initialization and setup of the main window.
        frame = new JFrame("Graph Reader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setLayout(null);
        frame.setBackground(new Color(255, 255, 255));

        // Setup of the text area to display the file's content.
        textArea = new JTextArea();
        textArea.setEditable(false);

        // Adding a scroll pane to the text area.
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 20, 250, 400);
        frame.add(scrollPane);

        // Creation and setup of the menu bar.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JButton runButton = new JButton("Run"); 
        runButton.setBounds(20, 450, 250, 30);
        frame.add(runButton);

        // Adding functionality to the Run button.
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Actions to perform when the Run button is clicked.
                if (selectedFile != null) {
                    // Reading and analyzing the selected file.
                    SG = GraphReader.readFile(selectedFile.getAbsolutePath());
                    // Basic checks on the read graph.
                    if (SG.getCyclic() || SG.getNegativeWeight()) {
                        // Display errors if necessary.
                        JOptionPane.showMessageDialog(frame, "The graph is cyclic or contains negative weight, please input another file");
                        return;
                    }
                    // Updating the interface with graph information.
                    String[] columnNames = {"ID", "Weight", "Predecessors", "Rank", "Earliest Start", "Latest Start", "Margin"};
                    nodeTable = new JTable(SG.toTable(), columnNames);
                    nodeTable.setBounds(300, 20, 520, 300);
                    frame.add(nodeTable);

                    // Preparing and displaying the node matrix.
                    String[] ColumnNames = new String[SG.getNodes().size() + 1];
                    for (int i = 0; i < SG.getNodes().size() + 1; i++) {
                        ColumnNames[i] = " ";
                    }
                    nodeMatrix = new JTable(new SchedulingMatrix(SG).toTable(), ColumnNames);

                    for (int i = 0; i < nodeMatrix.getColumnCount(); i++) {
                        TableColumn column = nodeMatrix.getColumnModel().getColumn(i);
                        column.setWidth(20);
                    }
                    nodeMatrix.setBounds(300, 350, 480, 400);

                    frame.add(nodeMatrix);

                    // Displaying the critical path.
                    textArea2 = new JTextArea(SG.dispCriticalPath());
                    textArea2.setBounds(850, 20, 500, 350);
                    frame.add(textArea2);
                    frame.repaint();
                }
            }
        });

        // Attempt to set the system look and feel for the UI.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup of the Open menu action.
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Creating and setting up the file chooser dialog.
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Documents"));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // Actions to perform when the user selects a file.
                    selectedFile = fileChooser.getSelectedFile();
                    // Reading and displaying the file content in the text area.
                    if (selectedFile.getName().endsWith(".txt")) {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                            String line;
                            textArea.setText("");
                            while ((line = reader.readLine()) != null) {
                                textArea.append(line + "\n");
                            }
                            reader.close();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(frame, "An error occurred while reading the file", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select a .txt file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }
}

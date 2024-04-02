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
    private JFrame frame;
    private JTextArea textArea, textArea2;
    private File selectedFile;
    private SchedulingGraph SG;
    private JTable nodeTable;
    private JTable nodeMatrix;
    private JPanel nodeTablePanel; // Conteneur pour nodeTable pour faciliter la suppression/mise à jour
    private JPanel nodeMatrixPanel; // Conteneur pour nodeMatrix pour faciliter la suppression/mise à jour

    public Interface() {
        frame = new JFrame("Graph Reader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setLayout(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 20, 250, 400);
        frame.add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JButton runButton = new JButton("Run");
        runButton.setBounds(20, 450, 250, 30);
        frame.add(runButton);

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    SG = GraphReader.readFile(selectedFile.getAbsolutePath());
                    if (SG.getCyclic() || SG.getNegativeWeight()) {
                        JOptionPane.showMessageDialog(frame, "The graph is cyclic or contains negative weight, please input another file");
                        return;
                    }

                    // Suppression des anciens composants si présents
                    if (nodeTablePanel != null) {
                        frame.remove(nodeTablePanel);
                    }
                    if (nodeMatrixPanel != null) {
                        frame.remove(nodeMatrixPanel);
                    }
                    if (textArea2 != null) {
                        frame.remove(textArea2);
                    }

                    // Création et ajout de nouveaux composants
                    updateNodeTable();
                    updateNodeMatrix();
                    updateCriticalPathTextArea();

                    frame.revalidate();
                    frame.repaint();
                }
            }
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Documents"));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile.getName().endsWith(".txt")) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                            String line;
                            textArea.setText("");
                            while ((line = reader.readLine()) != null) {
                                textArea.append(line + "\n");
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

        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    private void updateNodeTable() {
        String[] columnNames = {"ID", "Weight", "Predecessors", "Rank", "Earliest Start", "Latest Start", "Margin"};
        nodeTable = new JTable(SG.toTable(), columnNames);
        nodeTablePanel = new JPanel();
        nodeTablePanel.setBounds(300, 20, 520, 300);
        nodeTablePanel.add(new JScrollPane(nodeTable));
        frame.add(nodeTablePanel);
    }

    private void updateNodeMatrix() {
        String[] ColumnNames = new String[SG.getNodes().size() + 1];
        for (int i = 0; i < SG.getNodes().size() + 1; i++) {
            ColumnNames[i] = " ";
        }
        nodeMatrix = new JTable(new SchedulingMatrix(SG).toTable(), ColumnNames);
        nodeMatrixPanel = new JPanel();
        nodeMatrixPanel.setBounds(300, 350, 480, 400);
        nodeMatrixPanel.add(new JScrollPane(nodeMatrix));
        frame.add(nodeMatrixPanel);
    }

    private void updateCriticalPathTextArea() {
        textArea2 = new JTextArea(SG.dispCriticalPath());
        textArea2.setBounds(850, 20, 500, 350);
        frame.add(textArea2);
    }

    public static void main(String[] args) {
        new Interface();
    }
}

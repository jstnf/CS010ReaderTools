package dev.justinf.zybooks2elearn;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;

public class AppFrame {

    private final Z2EApp app;
    private JTextArea textAreaConsole;
    private JPanel panelConsole;
    private JScrollPane scrollPaneTextAreaConsole;
    private JPanel panelMain;
    private JLabel labelPanelConsole;
    private JTabbedPane tabbedPane;
    private JPanel panelGrading;
    private JPanel panelLoad;
    private JComboBox comboBoxZybooks;
    private JComboBox comboBoxElearn;
    private JLabel labelZybooksAssignment;
    private JLabel labelElearnAssignment;
    private JPanel panelGradingAssignments;
    private JButton buttonTransfer;
    private JTextField textFieldZybooksFilePath;
    private JTextField textFieldElearnFilePath;
    private JButton buttonZybooksFileSelect;
    private JButton buttonElearnFileSelect;
    private JLabel labelZybooksFileSelect;
    private JLabel labelElearnFileSelect;

    public AppFrame(Z2EApp app) {
        this.app = app;

        buttonTransfer.addActionListener(e -> {
            app.transferAndExport(comboBoxZybooks.getSelectedItem().toString(), comboBoxElearn.getSelectedItem().toString());
        });

        buttonZybooksFileSelect.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select file containing zyBooks Assignment Report...");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Comma-separated values (*.csv)", "csv"));
            fileChooser.setMultiSelectionEnabled(false);
            int status = fileChooser.showOpenDialog(panelLoad);
            if (status == JFileChooser.APPROVE_OPTION) {
                app.attemptZybooksFileRead(fileChooser.getSelectedFile());
            }
        });

        buttonElearnFileSelect.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select file containing eLearn Gradebook...");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Comma-separated values (*.csv)", "csv"));
            fileChooser.setMultiSelectionEnabled(false);
            int status = fileChooser.showOpenDialog(panelLoad);
            if (status == JFileChooser.APPROVE_OPTION) {
                app.attemptElearnFileRead(fileChooser.getSelectedFile());
            }
        });

        // Auto-scroll console text area
        ((DefaultCaret) textAreaConsole.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void update() {
        // zyBooks Assignments
        if (app.getAssignmentReport().getAssignments() == null) {
            comboBoxZybooks.setModel(new DefaultComboBoxModel<String>(new String[]{ "No zyBooks Report Loaded" }));
            comboBoxZybooks.setSelectedIndex(0);
            comboBoxZybooks.setEnabled(false);
        } else {
            comboBoxZybooks.setModel(new DefaultComboBoxModel<String>(app.getAssignmentReport().getAssignments().toArray(new String[0])));
            comboBoxZybooks.setSelectedIndex(0);
            comboBoxZybooks.setEnabled(true);
        }

        if (app.getZyBooksFile() == null) {
            textFieldZybooksFilePath.setText("Not loaded");
        } else {
            textFieldZybooksFilePath.setText(app.getZyBooksFile().getPath());
        }

        // eLearn Assignments
        if (app.getGradebook().getAssignments() == null) {
            comboBoxElearn.setModel(new DefaultComboBoxModel<String>(new String[]{ "No eLearn Gradebook Loaded" }));
            comboBoxElearn.setSelectedIndex(0);
            comboBoxElearn.setEnabled(false);
        } else {
            comboBoxElearn.setModel(new DefaultComboBoxModel<String>(app.getGradebook().getAssignments().toArray(new String[0])));
            comboBoxElearn.setSelectedIndex(0);
            comboBoxElearn.setEnabled(true);
        }

        if (app.geteLearnFile() == null) {
            textFieldElearnFilePath.setText("Not loaded");
        } else {
            textFieldElearnFilePath.setText(app.geteLearnFile().getPath());
        }
    }

    public void log(Object o) {
        textAreaConsole.append("\n" + o.toString());
    }

    /* getset */
    public JPanel getPanelMain() {
        return panelMain;
    }
}
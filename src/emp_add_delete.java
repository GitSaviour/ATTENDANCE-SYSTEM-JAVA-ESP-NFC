import com.fazecast.jSerialComm.SerialPort;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class emp_add_delete extends JFrame {

    // --- Configuration (Functional) ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendance";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Vansh@8138";
    private static final String COM_PORT = "COM3";

    // --- Visual Style Constants (Aesthetic) ---
    private static final Color COLOR_BACKGROUND = new Color(43, 43, 43);
    private static final Color COLOR_PANEL = new Color(49, 51, 53);
    private static final Color COLOR_TEXT = new Color(220, 220, 220);
    private static final Color COLOR_ACCENT = new Color(0, 120, 215);
    private static final Color COLOR_DISABLED = new Color(128, 128, 128);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // --- GUI Components ---
    private JTextField eidField, nameField, deptField, tagIdField, eidToDeleteField;
    private JTextField updateEidField, newTagIdField; // New fields for the update feature
    private JButton scanButton, addButton, deleteButton;
    private JButton scanNewTagButton, updateTagButton; // New buttons for the update feature
    private JLabel statusLabel;

    public emp_add_delete() {
        super("Employee Management System");

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 700); // Increased height for the new panel
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);

        // --- Main Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // --- Add Employee Panel ---
        JPanel addPanel = createStyledPanel("Add New Employee");
        setupAddPanel(addPanel);
        mainPanel.add(addPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        // --- NEW: Update Lost Tag Panel ---
        JPanel updatePanel = createStyledPanel("Update Lost Tag");
        setupUpdatePanel(updatePanel);
        mainPanel.add(updatePanel);

        mainPanel.add(Box.createVerticalStrut(20));

        // --- Delete Employee Panel ---
        JPanel deletePanel = createStyledPanel("Delete Employee");
        setupDeletePanel(deletePanel);
        mainPanel.add(deletePanel);

        // --- Status Bar ---
        statusLabel = new JLabel("Welcome! Please fill the form to begin.", SwingConstants.CENTER);
        statusLabel.setForeground(COLOR_TEXT);
        statusLabel.setFont(FONT_LABEL);
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        scanButton.addActionListener(e -> scanForTag(tagIdField)); // Pass the target field
        addButton.addActionListener(this::addEmployee);
        deleteButton.addActionListener(this::deleteEmployee);
        scanNewTagButton.addActionListener(e -> scanForTag(newTagIdField)); // Pass the other target field
        updateTagButton.addActionListener(this::updateEmployeeTag);
    }
    
    // --- Panel Setup Methods ---
    private void setupAddPanel(JPanel panel) {
        // ... (This method is unchanged)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; panel.add(createStyledLabel("EID (Employee ID):"), gbc);
        gbc.gridy = 1; panel.add(createStyledLabel("NAME:"), gbc);
        gbc.gridy = 2; panel.add(createStyledLabel("DEPT (Department):"), gbc);
        gbc.gridy = 3; panel.add(createStyledLabel("TAGID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridy = 0; eidField = createStyledTextField(); panel.add(eidField, gbc);
        gbc.gridy = 1; nameField = createStyledTextField(); panel.add(nameField, gbc);
        gbc.gridy = 2; deptField = createStyledTextField(); panel.add(deptField, gbc);
        gbc.gridy = 3; tagIdField = createStyledTextField(); tagIdField.setEditable(false); tagIdField.setForeground(COLOR_DISABLED); panel.add(tagIdField, gbc);
        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        scanButton = createStyledButton("Scan for Tag");
        addButton = createStyledButton("Add Employee");
        buttonPanel.add(scanButton);
        buttonPanel.add(addButton);
        panel.add(buttonPanel, gbc);
    }

    private void setupUpdatePanel(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createStyledLabel("EID of Employee:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        updateEidField = createStyledTextField();
        panel.add(updateEidField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(createStyledLabel("New TAGID:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        newTagIdField = createStyledTextField();
        newTagIdField.setEditable(false);
        newTagIdField.setForeground(COLOR_DISABLED);
        panel.add(newTagIdField, gbc);

        gbc.gridy = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        scanNewTagButton = createStyledButton("Scan New Tag");
        updateTagButton = createStyledButton("Update Tag");
        buttonPanel.add(scanNewTagButton);
        buttonPanel.add(updateTagButton);
        panel.add(buttonPanel, gbc);
    }

    private void setupDeletePanel(JPanel panel) {
        // ... (This method is unchanged)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(createStyledLabel("EID to Delete:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        eidToDeleteField = createStyledTextField();
        panel.add(eidToDeleteField, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        deleteButton = createStyledButton("Delete Employee");
        panel.add(deleteButton, gbc);
    }
    
    // --- Helper methods for creating styled components (unchanged) ---
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_ACCENT), title, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, FONT_BUTTON, COLOR_TEXT));
        return panel;
    }
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(COLOR_TEXT);
        return label;
    }
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(FONT_LABEL);
        textField.setCaretColor(COLOR_ACCENT);
        return textField;
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(COLOR_ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BACKGROUND, 1), BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        return button;
    }
    
    // --- Reusable and New Functional Methods ---
    
    /**
     * Reusable method to scan for a TAGID and update a specified text field.
     * @param targetField The JTextField to populate with the scanned UID.
     */
    private void scanForTag(JTextField targetField) {
        statusLabel.setText("Scanning... Please tap RFID tag now.");
        targetField.setText(""); // Clear the specific field

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                SerialPort port = SerialPort.getCommPort(COM_PORT);
                port.setBaudRate(115200);
                if (!port.openPort()) throw new IllegalStateException("Failed to open port '" + COM_PORT + "'. Is it in use?");
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0);
                try (java.util.Scanner scanner = new java.util.Scanner(port.getInputStream())) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim();
                        if (!line.isEmpty() && !line.equalsIgnoreCase("Waiting for UID...")) return line.toUpperCase();
                    }
                } finally {
                    port.closePort();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    String uid = get();
                    if (uid != null && !uid.isEmpty()) {
                        targetField.setText(uid); // Update the correct field
                        statusLabel.setText("Scan successful! New TAGID captured.");
                    } else {
                        statusLabel.setText("Scan timed out. Please try again.");
                        JOptionPane.showMessageDialog(emp_add_delete.this, "Scan timed out. No data received.", "Scan Error", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error during scan!");
                    JOptionPane.showMessageDialog(emp_add_delete.this, "An error occurred: " + ex.getCause().getMessage(), "Scan Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateEmployeeTag(ActionEvent e) {
        String eidStr = updateEidField.getText();
        String newTagId = newTagIdField.getText();

        if (eidStr.isEmpty() || newTagId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an EID and scan a new tag.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int eid;
        try {
            eid = Integer.parseInt(eidStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "EID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE emp SET TAGID = ? WHERE EID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, newTagId);
            pst.setInt(2, eid);

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Tag ID updated successfully for EID: " + eid, "Success", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Tag for EID '" + eid + "' was updated.");
                updateEidField.setText("");
                newTagIdField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with EID: " + eid, "Not Found", JOptionPane.WARNING_MESSAGE);
                statusLabel.setText("Update failed: EID not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Failed to update tag.");
        }
    }

    // --- UNCHANGED FUNCTIONAL CODE (Add/Delete) ---
    private void addEmployee(ActionEvent e) {
        // ... (This method is unchanged)
        String eidStr = eidField.getText();
        String name = nameField.getText();
        String dept = deptField.getText();
        String tagId = tagIdField.getText();
        if (eidStr.isEmpty() || name.isEmpty() || dept.isEmpty() || tagId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled, including a scanned TAGID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int eid;
        try { eid = Integer.parseInt(eidStr); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "EID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "INSERT INTO emp (EID, NAME, DEPT, TAGID) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, eid);
            pst.setString(2, name);
            pst.setString(3, dept);
            pst.setString(4, tagId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            statusLabel.setText("Employee '" + name + "' added.");
            eidField.setText("");
            nameField.setText("");
            deptField.setText("");
            tagIdField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Failed to add employee.");
        }
    }
    private void deleteEmployee(ActionEvent e) {
        // ... (This method is unchanged)
        String eidStr = eidToDeleteField.getText();
        if (eidStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the EID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int eid;
        try { eid = Integer.parseInt(eidStr); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "EID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete employee with EID: " + eid + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM emp WHERE EID = ?";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, eid);
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Employee with EID '" + eid + "' deleted.");
                eidToDeleteField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with that EID.", "Not Found", JOptionPane.WARNING_MESSAGE);
                statusLabel.setText("Delete failed: EID not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Failed to delete employee.");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF. Using default.");
        }
        SwingUtilities.invokeLater(() -> new emp_add_delete().setVisible(true));
    }
}
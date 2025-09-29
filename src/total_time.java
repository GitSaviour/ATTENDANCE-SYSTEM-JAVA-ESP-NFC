import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class total_time extends JFrame {

    // --- Configuration (Functional) ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendance";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Vansh@8138";

    // --- Visual Style Constants (Aesthetic) ---
    private static final Color COLOR_BACKGROUND = new Color(43, 43, 43);
    private static final Color COLOR_PANEL = new Color(49, 51, 53);
    private static final Color COLOR_TEXT = new Color(220, 220, 220);
    private static final Color COLOR_ACCENT = new Color(0, 120, 215);
    private static final Color COLOR_SUCCESS = new Color(45, 157, 101);
    private static final Color COLOR_WARNING = new Color(218, 165, 32);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_RESULT = new Font("Segoe UI", Font.BOLD, 16);

    // --- GUI Components ---
    private JTextField dailyEidField, monthlyEidField;
    private JFormattedTextField dateField;
    private JComboBox<String> monthComboBox;
    private JSpinner yearSpinner;
    private JButton dailyCalcButton, monthlyCalcButton;
    private JLabel dailyResultLabel, dailyWarningLabel, monthlyResultLabel, monthlyWarningLabel; // Added monthlyWarningLabel

    public total_time() {
        super("Employee Time Query");

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600); // Increased height for the new warning label
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);

        // --- Main Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // --- Daily Hours Panel ---
        JPanel dailyPanel = createStyledPanel("Daily Hours Query");
        setupDailyPanel(dailyPanel);
        mainPanel.add(dailyPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        // --- Monthly Hours Panel ---
        JPanel monthlyPanel = createStyledPanel("Monthly Hours Query");
        setupMonthlyPanel(monthlyPanel);
        mainPanel.add(monthlyPanel);

        // --- Action Listeners ---
        dailyCalcButton.addActionListener(this::calculateDailyHours);
        monthlyCalcButton.addActionListener(this::calculateMonthlyHours);
    }

    private void setupDailyPanel(JPanel panel) {
        // ... (This method is unchanged)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createStyledLabel("Employee ID (EID):"), gbc);
        dailyEidField = createStyledTextField();
        panel.add(dailyEidField, gbc);
        gbc.gridy = 1;
        panel.add(createStyledLabel("Date (ddMMyyyy):"), gbc);
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateField = new JFormattedTextField(dateMask);
            dateField.setFont(FONT_LABEL);
            dateField.setColumns(15);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        panel.add(dateField, gbc);
        gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dailyCalcButton = createStyledButton("Calculate Daily Hours");
        panel.add(dailyCalcButton, gbc);
        gbc.gridy = 3;
        dailyResultLabel = createResultLabel("Total Time: --");
        panel.add(dailyResultLabel, gbc);
        gbc.gridy = 4;
        dailyWarningLabel = createResultLabel("");
        dailyWarningLabel.setForeground(COLOR_WARNING);
        panel.add(dailyWarningLabel, gbc);
    }

    private void setupMonthlyPanel(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inputs
        panel.add(createStyledLabel("Employee ID (EID):"), gbc);
        monthlyEidField = createStyledTextField();
        panel.add(monthlyEidField, gbc);

        gbc.gridy = 1;
        panel.add(createStyledLabel("Month and Year:"), gbc);
        
        JPanel monthYearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        monthYearPanel.setOpaque(false);
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setFont(FONT_LABEL);
        
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        SpinnerModel yearModel = new SpinnerNumberModel(currentYear, currentYear - 10, currentYear + 10, 1);
        yearSpinner = new JSpinner(yearModel);
        yearSpinner.setFont(FONT_LABEL);
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
        
        monthYearPanel.add(monthComboBox);
        monthYearPanel.add(Box.createHorizontalStrut(10));
        monthYearPanel.add(yearSpinner);
        panel.add(monthYearPanel, gbc);
        
        // Button
        gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        monthlyCalcButton = createStyledButton("Calculate Monthly Hours");
        panel.add(monthlyCalcButton, gbc);

        // Result Labels
        gbc.gridy = 3;
        monthlyResultLabel = createResultLabel("Total Time: --");
        panel.add(monthlyResultLabel, gbc);

        // *** NEW: Add the warning label for the monthly panel ***
        gbc.gridy = 4;
        monthlyWarningLabel = createResultLabel("");
        monthlyWarningLabel.setForeground(COLOR_WARNING);
        monthlyWarningLabel.setFont(FONT_LABEL); // Use slightly smaller font for the list of dates
        panel.add(monthlyWarningLabel, gbc);
    }

    private void calculateDailyHours(ActionEvent e) {
        // ... (This method is unchanged)
        String eidStr = dailyEidField.getText();
        String dateStr = dateField.getText().replace("/", "");
        dailyWarningLabel.setText("");
        if (eidStr.isEmpty() || dateStr.trim().length() != 8) {
            JOptionPane.showMessageDialog(this, "Please enter a valid EID and a full date (ddMMyyyy).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int eid = Integer.parseInt(eidStr);
            SimpleDateFormat parser = new SimpleDateFormat("ddMMyyyy");
            parser.setLenient(false);
            java.util.Date utilDate = parser.parse(dateStr);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            List<Timestamp> logs = getLogsForDay(eid, sqlDate);
            long totalMillis = 0;
            if (logs.size() < 2) {
                dailyResultLabel.setText("Total Time: Not enough logs to calculate.");
                return;
            }
            if (logs.size() % 2 == 0) {
                for (int i = 0; i < logs.size(); i += 2) {
                    totalMillis += (logs.get(i + 1).getTime() - logs.get(i).getTime());
                }
            } else {
                dailyWarningLabel.setText("Warning: Irregular logs found. Using first and last entry.");
                totalMillis += (logs.get(logs.size() - 1).getTime() - logs.get(0).getTime());
            }
            dailyResultLabel.setText("Total Time: " + formatDuration(totalMillis));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "EID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use ddMMyyyy.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateMonthlyHours(ActionEvent e) {
        monthlyWarningLabel.setText(""); // Clear previous warnings
        String eidStr = monthlyEidField.getText();
        if (eidStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid EID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int eid = Integer.parseInt(eidStr);
            int month = monthComboBox.getSelectedIndex() + 1;
            int year = (Integer) yearSpinner.getValue();
            
            YearMonth yearMonth = YearMonth.of(year, month);
            long totalMonthlyMillis = 0;
            List<String> warningDates = new ArrayList<>(); // List to store dates with odd logs

            for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                java.sql.Date sqlDate = java.sql.Date.valueOf(yearMonth.atDay(day));
                List<Timestamp> logs = getLogsForDay(eid, sqlDate);

                if (logs.size() >= 2) {
                    if (logs.size() % 2 == 0) { // Even
                        for (int i = 0; i < logs.size(); i += 2) {
                            totalMonthlyMillis += (logs.get(i + 1).getTime() - logs.get(i).getTime());
                        }
                    } else { // Odd
                        totalMonthlyMillis += (logs.get(logs.size() - 1).getTime() - logs.get(0).getTime());
                        // *** NEW: Add the date to our warning list ***
                        warningDates.add(sqlDate.toString());
                    }
                }
            }

            // *** NEW: Display warnings if any dates were found ***
            if (!warningDates.isEmpty()) {
                // Use HTML for basic word wrapping in the JLabel
                String warningMessage = "<html>Warning: Irregular logs on: " + String.join(", ", warningDates) + "</html>";
                monthlyWarningLabel.setText(warningMessage);
            }

            monthlyResultLabel.setText("Total Time: " + formatDuration(totalMonthlyMillis));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "EID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) { //are agar ye error/exception aaya toh dhikkar hai bhai
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Timestamp> getLogsForDay(int eid, java.sql.Date date) throws SQLException {
        List<Timestamp> logs = new ArrayList<>();
        String sql = "SELECT TIMESTAMP FROM final WHERE EID = ? AND DATE(TIMESTAMP) = ? ORDER BY TIMESTAMP ASC";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, eid);
            pst.setDate(2, date);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                logs.add(rs.getTimestamp("TIMESTAMP"));
            }
        }
        return logs;
    }

    private String formatDuration(long millis) {
        if (millis < 0) return "Invalid duration";
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }
    
    // --- Helper methods for creating styled components ---
    // ... (These methods are unchanged)
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
    private JLabel createResultLabel(String text) {
        JLabel label = createStyledLabel(text);
        label.setFont(FONT_RESULT);
        label.setForeground(COLOR_SUCCESS);
        return label;
    }
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF. Using default.");
        }
        SwingUtilities.invokeLater(() -> new total_time().setVisible(true));
    }
}
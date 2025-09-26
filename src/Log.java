import com.fazecast.jSerialComm.SerialPort;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class Log extends JFrame {

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
    private static final Color COLOR_SUCCESS = new Color(45, 157, 101);
    private static final Color COLOR_DISABLED = new Color(128, 128, 128);
    private static final Font FONT_STATUS = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_INFO = new Font("Segoe UI", Font.PLAIN, 14);

    // --- GUI Components ---
    private final JLabel statusLabel;
    private final JLabel lastLogLabel;
    private final JLabel instructionLabel;

    public Log() {
        super("Attendance Logger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 250);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_PANEL);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Connecting to Reader...", SwingConstants.CENTER);
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setForeground(COLOR_ACCENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        instructionLabel = new JLabel("Hold RFID tag near the scanner.", SwingConstants.CENTER);
        instructionLabel.setFont(FONT_INFO);
        instructionLabel.setForeground(COLOR_TEXT);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lastLogLabel = new JLabel("Last Log: ---", SwingConstants.CENTER);
        lastLogLabel.setFont(FONT_INFO);
        lastLogLabel.setForeground(COLOR_DISABLED);
        lastLogLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(instructionLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lastLogLabel);

        startScanner();
    }

    private void startScanner() {
        SwingWorker<Void, String> scannerWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                while (!isCancelled()) {
                    publish("Ready to Scan...");

                    SerialPort port = SerialPort.getCommPort(COM_PORT);
                    port.setBaudRate(115200);
                    if (!port.openPort()) {
                        publish("ERROR: Port Busy. Retrying...");
                        Thread.sleep(5000);
                        continue;
                    }
                    port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0);

                    try (java.util.Scanner scanner = new java.util.Scanner(port.getInputStream())) {
                        // *** THE FIX IS HERE: This was 'if', now it's 'while' to listen continuously ***
                        while (scanner.hasNextLine()) {
                            String tagId = scanner.nextLine().trim().toUpperCase();

                            if (tagId.matches("^[0-9A-F]+$")) {
                                publish("Logging: " + tagId);
                                String resultMessage = logTagToDatabase(tagId);
                                publish(resultMessage);
                                Thread.sleep(2600);
                                break; // Exit inner loop to reconnect cleanly for the next scan
                            }
                        }
                    } catch (Exception e) {
                        publish("ERROR: " + e.getMessage());
                    } finally {
                        port.closePort();
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    if (message.startsWith("Log successful")) {
                        statusLabel.setForeground(COLOR_SUCCESS);
                        statusLabel.setText("Success!");
                        lastLogLabel.setText(message);
                        lastLogLabel.setForeground(COLOR_TEXT);

                        Timer timer = new Timer(2600, e -> {
                            statusLabel.setForeground(COLOR_ACCENT);
                            statusLabel.setText("Ready to Scan...");
                            lastLogLabel.setText("Last Log: ---");
                            lastLogLabel.setForeground(COLOR_DISABLED);
                        });
                        timer.setRepeats(false);
                        timer.start();

                    } else if (message.startsWith("ERROR")) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText(message);
                    } else {
                        statusLabel.setText(message);
                    }
                }
            }
        };
        scannerWorker.execute();
    }

    private String logTagToDatabase(String tagId) throws SQLException {
        String insertSql = "INSERT INTO log (TAGID) VALUES (?)";
        String selectSql = "SELECT TIMESTAMP FROM log WHERE TAGID = ? ORDER BY TIMESTAMP DESC LIMIT 1";
        
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            try (PreparedStatement pstInsert = con.prepareStatement(insertSql)) {
                pstInsert.setString(1, tagId);
                pstInsert.executeUpdate();
            }

            try (PreparedStatement pstSelect = con.prepareStatement(selectSql)) {
                pstSelect.setString(1, tagId);
                ResultSet rs = pstSelect.executeQuery();
                if (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("TIMESTAMP");
                    return String.format("Log successful for %s at %s", tagId, timestamp.toString().substring(0, 19));
                }
            }
        }
        return "ERROR: Could not verify log entry.";
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF. Using default.");
        }
        
        SwingUtilities.invokeLater(() -> new Log().setVisible(true));
    }
}
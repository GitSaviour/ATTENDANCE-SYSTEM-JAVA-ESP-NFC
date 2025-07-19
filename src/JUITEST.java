
import javax.swing.*;
import java.awt.*;
import java.sql.*;
//a simple program to execute SQL queries using a GUI
//it uses Swing for the GUI and JDBC for database connectivity
public class JUITEST extends JFrame {
    JTextArea queryArea, resultArea;
    JButton executeButton;

    public JUITEST() {
        setTitle("SQL Query Executor");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setBackground(Color.gray);
        queryArea = new JTextArea(5, 60);
        queryArea.setBorder(BorderFactory.createTitledBorder("Enter SQL Query"));

        resultArea = new JTextArea(15, 60);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createTitledBorder("Result"));

        
        executeButton = new JButton("Execute SQL");
        executeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        executeButton.addActionListener(e -> runQuery(queryArea.getText()));

        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(new JScrollPane(queryArea));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(executeButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new JScrollPane(resultArea));

        add(mainPanel);
        setVisible(true);
    }

    public void runQuery(String query) {
        resultArea.setText(""); 
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demodb", "root", "Vansh@8138");
             Statement st = con.createStatement()) {

            boolean hasResultSet = false;;
            ResultSet rs = null;

            try {
                rs = st.executeQuery(query);
                hasResultSet = true;
            } catch (SQLException e) {
                st.execute(query);
                resultArea.setText("Query executed successfully (no result set).");
                return;
            }

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            StringBuilder sb = new StringBuilder();

        
            for (int i = 1; i <= columnCount; i++) {
                sb.append(rsmd.getColumnName(i)).append("\t");
            }
            sb.append("\n");

    
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    sb.append(rs.getString(i)).append("\t");
                }
                sb.append("\n");
            }

            resultArea.setText(sb.toString());
            rs.close();

        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JUITEST());
    }
}


import java.sql.*;
import java.util.Scanner;
// A simple program to execute SQL queries without a GUI
// It uses JDBC for database connectivity
public class NONGUI {
    public static void output(String query) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demodb", "root", "Vansh@8138");
            Statement st = con.createStatement();
            ResultSet rs = null;
            boolean hasResultSet = false;

            try {
                rs = st.executeQuery(query);
                hasResultSet = true;
            } catch (SQLException e) {
                st.execute(query); 
            }

            if (hasResultSet) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

               
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rsmd.getColumnName(i) + "\t");
                }
                System.out.println();

                
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
                rs.close();
            }

            st.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Type \"EXIT\" to exit ");
            System.out.print("Enter SQL Query to execute: ");
            String query = sc.nextLine();
            if (query.equalsIgnoreCase("EXIT")) break;

            System.out.println("x-x-x-x-x-x-x-x-x-x-OUTPUT BELOW-x-x-x-x-x-x-x-x-x-x");
            System.out.println();
            output(query);
            System.out.println("\nQuery executed successfully.\n");
        }
        sc.close();
    }
}
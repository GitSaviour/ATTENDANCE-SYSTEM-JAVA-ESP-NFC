
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class jdbctest {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/attendance";
        String userName = "root";
        String pass = "Vansh@8138";
/*
 * driver
 * connection
 * statement
 * query 
 */
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcURL, userName, pass);
            Statement statement = connection.createStatement();
            // String sql = "truncate table t1";
            // statement.execute(sql);
            String sql = "select * from log";

            ResultSet result = statement.executeQuery(sql);
            // System.out.println(result);
            // Get metadata
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            // System.out.println(columnCount);


            // Process rows
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = result.getString(i);
                    System.out.print(columnName + ": " + columnValue + "  ");
                }
                System.out.println(); // new line for each row
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

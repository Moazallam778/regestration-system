package regestrationsystem;

import java.sql.Connection;
import java.sql.DriverManager;

public class dbConn {

    public static Connection DBConnection() {
        Connection conn = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "adv", "moaz");
            System.out.println("success");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return conn;
    }

    public static void main(String[] args) {
        DBConnection();
    }
}

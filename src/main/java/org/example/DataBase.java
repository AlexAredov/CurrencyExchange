package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DataBase {
    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;

    public DataBase() {
        connection = null;
    }

    public static void connect() {
        try {
            connection = null;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/aleksejaredov/IdeaProjects/CurrencyExchange/identifier.sqlite");
            System.out.println();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void WriteDB(String code, String fullName, String sign) throws SQLException {
        //try {
            //String sql = "INSERT INTO 'table' ('Code', 'FullName', 'Sign') VALUES (" + code + "," + fullName + "," + sign + ");";
            //statement.execute(sql);
        //}
        //catch (SQLException e) {
        //    throw new RuntimeException(e);
        //}
    }

    public static void ReadDB() {
        //String sql = ";
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM \"table\"");
            while(resultSet.next()) {
                String  code = resultSet.getString("name");
                System.out.println( "Code = " + code );
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void CloseDB() {
        try {
            connection.close();
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
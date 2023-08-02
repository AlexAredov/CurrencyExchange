package org.example;
import org.json.JSONArray;
import org.json.JSONObject;

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
            //connection = DriverManager.getConnection("jdbc:sqlite:/Users/aleksejaredov/IdeaProjects/CurrencyExchange/identifier.sqlite");
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/aared/OneDrive/Документы/GitHub/CurrencyExchange/identifier.sqlite");
            System.out.println();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void WriteDB(String code, String fullName, String sign) throws SQLException {
        statement = connection.createStatement();
        String sql = "INSERT INTO \"currencies\" ('Code', 'FullName', 'Sign') VALUES (\"" + code + "\",\"" + fullName + "\",\"" + sign + "\");";
        statement.execute(sql);
    }

    public static JSONArray ReadDB() {
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM \"currencies\"");
            JSONArray jsonArray = new JSONArray();
            while(resultSet.next()) {
                JSONObject jsonObject = new JSONObject();
                String  id = resultSet.getString("ID");
                String  code = resultSet.getString("Code");
                String  fullName = resultSet.getString("FullName");
                String  sign = resultSet.getString("Sign");
                jsonObject.put("ID", id);
                jsonObject.put("Code", code);
                jsonObject.put("FullName", fullName);
                jsonObject.put("Sign", sign);
                jsonArray.put(jsonObject);
            }
            return jsonArray;
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
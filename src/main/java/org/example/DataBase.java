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

    public static void connect() throws ClassNotFoundException, SQLException {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        //connection = DriverManager.getConnection("jdbc:sqlite:/Users/aleksejaredov/IdeaProjects/CurrencyExchange/identifier.sqlite");
        connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/aared/OneDrive/Документы/GitHub/CurrencyExchange/identifier.sqlite");
        System.out.println();
    }

    public static JSONObject WriteDB(String code, String fullName, String sign) {
        try {
            statement = connection.createStatement();
            String sql = "INSERT INTO \"currencies\" ('Code', 'FullName', 'Sign') VALUES (\"" + code + "\",\"" + fullName + "\",\"" + sign + "\");";
            statement.execute(sql);
            statement = connection.createStatement();
            String id = statement.executeQuery("SELECT \"ID\" FROM \"currencies\" WHERE \"Code\"=\"" + code + "\";").getString("ID");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", id);
            jsonObject.put("Code", code);
            jsonObject.put("FullName", fullName);
            jsonObject.put("Sign", sign);
            return jsonObject;
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }

    public static JSONArray ReadDB() throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM \"currencies\"");
        JSONArray jsonArray = new JSONArray();
        while(resultSet.next()) {
            jsonArray.put(formResultToJSON(resultSet));
        }
        return jsonArray;
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

    public static JSONObject GetByCode(String codeNew) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM \"currencies\" WHERE \"Code\"=\"" + codeNew + "\";");
        if (!resultSet.isClosed()) {
            return formResultToJSON(resultSet);
        } else {
            return null;
        }
    }

    private static JSONObject formResultToJSON(ResultSet resultSet) throws SQLException {
        JSONObject jsonObject = new JSONObject();
        String  id = resultSet.getString("ID");
        String  code = resultSet.getString("Code");
        String  fullName = resultSet.getString("FullName");
        String  sign = resultSet.getString("Sign");
        jsonObject.put("ID", id);
        jsonObject.put("Code", code);
        jsonObject.put("FullName", fullName);
        jsonObject.put("Sign", sign);
        return jsonObject;
    }
}
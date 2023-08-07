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

    public static JSONObject WriteCurrencies(Currency currency) {
        try {
            String code = currency.getCode();
            String fullName = currency.getName();
            String sign = currency.getSign();
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

    public static JSONArray ReadCurrencies() throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM \"currencies\"");
        JSONArray jsonArray = new JSONArray();
        while(resultSet.next()) {
            jsonArray.put(formResultToJSONcurrencies(resultSet));
        }
        return jsonArray;
    }

    public static JSONArray ReadExchangeRates() throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM \"exchange_rates\"");
        JSONArray jsonArray = new JSONArray();
        while(resultSet.next()) {
            jsonArray.put(fromResultToJSONexchange(resultSet));
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
        ResultSet resultSetCode = statement.executeQuery("SELECT * FROM \"currencies\" WHERE \"Code\"=\"" + codeNew + "\";");
        if (!resultSetCode.isClosed()) {
            return formResultToJSONcurrencies(resultSetCode);
        } else {
            return null;
        }
    }

    public static JSONObject GetById(String id) throws SQLException {
        statement = connection.createStatement();
         ResultSet resultSetId = statement.executeQuery("SELECT * FROM \"currencies\" WHERE \"ID\"=" + Integer.parseInt(id) + ";");
        if (!resultSetId.isClosed()) {
            return formResultToJSONcurrencies(resultSetId);
        } else {
            return null;
        }
    }

    private static JSONObject formResultToJSONcurrencies(ResultSet resultSet) throws SQLException {
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

    private static JSONObject fromResultToJSONexchange(ResultSet resultSet) throws SQLException {
        JSONObject jsonObject = new JSONObject();
        String id = resultSet.getString("ID");
        String baseCurrencyId = resultSet.getString("BaseCurrencyId");
        String targetCurrencyId = resultSet.getString("TargetCurrencyId");
        Double rate = Double.valueOf(resultSet.getString("Rate"));
        jsonObject.put("id", id);
        jsonObject.put("baseCurrency", GetById(baseCurrencyId));
        jsonObject.put("targetCurrency", GetById(targetCurrencyId));
        jsonObject.put("rate", rate);
        return jsonObject;
    }

    public static JSONObject GetRateByCodes(String codeBaseCurrency, String codeTargetCurrency) throws SQLException {
        Integer idBaseCurrency = Integer.parseInt(GetByCode(codeBaseCurrency).getString("ID"));
        Integer idTargetCurrency = Integer.parseInt(GetByCode(codeTargetCurrency).getString("ID"));
        System.out.println(idBaseCurrency);
        System.out.println(idTargetCurrency);
        JSONObject jsonObject = new JSONObject();
        //TODO
        return jsonObject;
    }
}
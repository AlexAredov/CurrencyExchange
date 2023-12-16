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
        //connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/aared/OneDrive/Документы/GitHub/CurrencyExchange/identifier.sqlite");
        connection = DriverManager.getConnection("jdbc:sqlite:/Users/aleksejaredov/Documents/GitHub/CurrencyExchange/identifier.sqlite");
        statement = connection.createStatement();
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
        if (resultSetCode.next()) {
            return formResultToJSONcurrencies(resultSetCode);
        } else {
            return null;
        }
    }

    public static JSONObject GetById(String id) throws SQLException {
        statement = connection.createStatement();
         ResultSet resultSetId = statement.executeQuery("SELECT * FROM \"currencies\" WHERE \"ID\"=" + Integer.parseInt(id) + ";");
        if (resultSetId.next()) {
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
        String baseCurrencyId = resultSet.getString("BaseCurrencyId");
        String targetCurrencyId = resultSet.getString("TargetCurrencyId");
        Double rate = Double.valueOf(resultSet.getString("Rate"));
        jsonObject.put("baseCurrency", GetById(baseCurrencyId));
        jsonObject.put("targetCurrency", GetById(targetCurrencyId));
        jsonObject.put("rate", rate);
        return jsonObject;
    }

    private static JSONObject fromResultToJSONexchangeWithoutRate(ResultSet resultSet) throws SQLException {
        JSONObject jsonObject = new JSONObject();
        String baseCurrencyId = resultSet.getString("BaseCurrencyId");
        String targetCurrencyId = resultSet.getString("TargetCurrencyId");
        jsonObject.put("baseCurrency", GetById(baseCurrencyId));
        jsonObject.put("targetCurrency", GetById(targetCurrencyId));
        return jsonObject;
    }

    public static JSONObject GetRateByCodes(String codeBaseCurrency, String codeTargetCurrency) throws SQLException {
        Integer idBaseCurrency = Integer.parseInt(GetByCode(codeBaseCurrency).getString("ID"));
        Integer idTargetCurrency = Integer.parseInt(GetByCode(codeTargetCurrency).getString("ID"));
        System.out.println(idBaseCurrency);
        System.out.println(idTargetCurrency);
        Statement statement1 = connection.createStatement();
        ResultSet resultSetCode = statement1.executeQuery("SELECT * FROM \"exchange_rates\" WHERE \"BaseCurrencyId\"=\"" + idBaseCurrency + "\" AND \"TargetCurrencyId\"=\"" + idTargetCurrency + "\";");
        if (resultSetCode.next()) {
            return fromResultToJSONexchange(resultSetCode);
        } else {
            return null;
        }
    }

    public static JSONObject WriteExchangeRate(String baseCurrencyCode, String targetCurrencyCode, float rate) throws SQLException {
        statement = connection.createStatement();
        Integer idBaseCurrency = Integer.parseInt(GetByCode(baseCurrencyCode).getString("ID"));
        Integer idTargetCurrency = Integer.parseInt(GetByCode(targetCurrencyCode).getString("ID"));
        String sql = "INSERT INTO \"exchange_rates\" ('BaseCurrencyId', 'TargetCurrencyId', 'Rate') VALUES (\"" + idBaseCurrency + "\",\"" + idTargetCurrency + "\",\"" + rate + "\");";
        statement.execute(sql);
        return GetRateByCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public static void PatchRateByCodes(String codeBaseCurrency, String codeTargetCurrency, float rate) throws SQLException {
        //statement1 = connection.createStatement();
        Integer idBaseCurrency = Integer.parseInt(GetByCode(codeBaseCurrency).getString("ID"));
        Integer idTargetCurrency = Integer.parseInt(GetByCode(codeTargetCurrency).getString("ID"));
        String sql = "UPDATE \"exchange_rates\" SET \"Rate\" = \"" + rate + "\" WHERE \"BaseCurrencyId\"=\"" + idBaseCurrency + "\" AND \"TargetCurrencyId\"=\"" + idTargetCurrency + "\";";
        statement.execute(sql);
    }

    public static JSONObject ExchangeByCodes(String fromCurrencyCode, String toCurrencyCode, float amount) throws SQLException {
        Integer idBaseCurrency = Integer.parseInt(GetByCode(fromCurrencyCode).getString("ID"));
        Integer idTargetCurrency = Integer.parseInt(GetByCode(toCurrencyCode).getString("ID"));
        System.out.println(idBaseCurrency);
        System.out.println(idTargetCurrency);
        Statement statement = connection.createStatement();
        JSONObject jsonObject = new JSONObject();
        ResultSet resultSetCode = statement.executeQuery("SELECT * FROM \"exchange_rates\" WHERE \"BaseCurrencyId\"=\"" + idBaseCurrency + "\" AND \"TargetCurrencyId\"=\"" + idTargetCurrency + "\";");
        if (resultSetCode.next()) {
            Double rate = Double.valueOf(resultSetCode.getString("Rate"));
            jsonObject = fromResultToJSONexchange(resultSetCode);
            jsonObject.put("amount", amount);
            jsonObject.put("convertedAmount", amount*rate);
            return jsonObject;
        } else {
            statement = connection.createStatement();
            ResultSet resultSetCode3 = statement.executeQuery("SELECT * FROM \"exchange_rates\" WHERE \"BaseCurrencyId\"=\"" + idTargetCurrency + "\" AND \"TargetCurrencyId\"=\"" + idBaseCurrency + "\";");
            if (resultSetCode3.next()) {
                jsonObject = fromResultToJSONexchangeWithoutRate(resultSetCode3);
                Double rate = 1/Double.parseDouble(resultSetCode3.getString("Rate"));
                jsonObject.put("rate", rate);
                jsonObject.put("amount", amount);
                jsonObject.put("convertedAmount", amount*rate);
                return jsonObject;
            }
            else {
                statement = connection.createStatement();
                ResultSet resultSetCode1 = statement.executeQuery("SELECT * FROM \"exchange_rates\" WHERE \"BaseCurrencyId\"=\"" + idBaseCurrency + "\" AND \"TargetCurrencyId\"=\"" + 4 + "\";");
                statement = connection.createStatement();
                ResultSet resultSetCode2 = statement.executeQuery("SELECT * FROM \"exchange_rates\" WHERE \"BaseCurrencyId\"=\"" + 4 + "\" AND \"TargetCurrencyId\"=\"" + idTargetCurrency + "\";");
                System.out.println(resultSetCode1.getString("Rate"));
                System.out.println(resultSetCode2.getString("Rate"));
                if (resultSetCode1.next() && resultSetCode2.next()) {
                    Double rate = Double.valueOf(resultSetCode1.getString("Rate"));
                    Double rate1 = Double.valueOf(resultSetCode2.getString("Rate"));
                    rate = rate * rate1;
                    String baseCurrencyId = resultSetCode1.getString("BaseCurrencyId");
                    String targetCurrencyId = resultSetCode2.getString("TargetCurrencyId");
                    jsonObject.put("baseCurrency", GetById(baseCurrencyId));
                    jsonObject.put("targetCurrency", GetById(targetCurrencyId));
                    jsonObject.put("rate", rate);
                    jsonObject.put("amount", amount);
                    jsonObject.put("convertedAmount", amount*rate);
                    return jsonObject;
                }
                else {
                    return null;
                }
            }
        }
    }

}
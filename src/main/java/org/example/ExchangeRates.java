package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchangeRates")
public class ExchangeRates extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DataBase.connect();
            JSONArray jsonArray = DataBase.ReadExchangeRates();
            response.getWriter().print(jsonArray);
        } catch (ClassNotFoundException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DataBase.connect();
            if (request.getParameter("baseCurrencyCode") != null) {
                String baseCurrencyCode = request.getParameter("baseCurrencyCode");
                String targetCurrencyCode = request.getParameter("targetCurrencyCode");
                float rate = Float.parseFloat(request.getParameter("rate"));
                if (DataBase.GetRateByCodes(baseCurrencyCode, targetCurrencyCode) == null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonObject = DataBase.WriteExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
                    response.getWriter().print(jsonObject);
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().print("Currency pair with this code already exists");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Required form field is missing");
            }
        } catch (ClassNotFoundException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}

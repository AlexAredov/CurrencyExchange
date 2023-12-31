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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DataBase.connect();
            response.setStatus(HttpServletResponse.SC_OK);
            JSONArray jsonArray = DataBase.ReadCurrencies();
            response.getWriter().print(jsonArray);
            DataBase.CloseDB();
        } catch (ClassNotFoundException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DataBase.connect();
            if (request.getParameter("name") != null) {
                String name = request.getParameter("name");
                String code = request.getParameter("code");
                String sign = request.getParameter("sign");
                if (DataBase.GetByCode(code) == null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    Currency currency = new Currency(code, name, sign);
                    JSONObject jsonObject = DataBase.WriteCurrencies(currency);
                    response.getWriter().print(jsonObject);
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().print("Currency with this code already exists");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Required form field is missing");
            }
            DataBase.CloseDB();
        } catch (ClassNotFoundException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}

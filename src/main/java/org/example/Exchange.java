package org.example;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchange/*")
public class Exchange extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DataBase.connect();
            if (request.getParameter("from") != null) {
                String fromCurrencyCode = request.getParameter("from");
                String toCurrencyCode = request.getParameter("to");
                float amount = Float.parseFloat(request.getParameter("amount"));
                if (DataBase.ExchangeByCodes(fromCurrencyCode, toCurrencyCode, amount) != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonObject = DataBase.ExchangeByCodes(fromCurrencyCode, toCurrencyCode, amount);
                    response.getWriter().print(jsonObject);
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().print("Currency pair doesn't found");
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

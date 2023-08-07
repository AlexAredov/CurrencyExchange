package org.example;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchangeRate/*")
public class ExchangeRate extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DataBase.connect();
            String codes = request.getPathInfo().substring(1);
            String codeBaseCurrency = codes.substring(0, 3);
            String codeTargetCurrency = codes.substring(3, 6);
            System.out.println(codeBaseCurrency);
            System.out.println(codeTargetCurrency);
            if (!codes.isEmpty()) {
                if (DataBase.GetRateByCodes(codeBaseCurrency, codeTargetCurrency) != null) {
                    JSONObject jsonObject = DataBase.GetRateByCodes(codeBaseCurrency, codeTargetCurrency);
                    response.getWriter().print(jsonObject);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print("Currency not found");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("The currency code is not in the address");
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}

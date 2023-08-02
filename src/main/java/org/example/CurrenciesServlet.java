package org.example;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataBase.connect();
        JSONArray jsonArray = DataBase.ReadDB();
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(jsonArray.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataBase.connect();
        PrintWriter writer = response.getWriter();

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        try {
            DataBase.WriteDB(code, name, sign);
            writer.println("success");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            writer.close();
        }
    }
}

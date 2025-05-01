package ar.com.game.web.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet("/api/hello")
public class Test extends HttpServlet {

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"message\": \"Hello from Servlet!\"}");
    }
}

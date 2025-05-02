package ar.com.game.web.controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;


import java.io.IOException;

@WebServlet("/api/hello")
public class Test extends HttpServlet {

	private static final long serialVersionUID = -7399854612292801537L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"message\": \"Hello from Servlet!\"}");
    }
}

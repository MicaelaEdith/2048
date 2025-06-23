package ar.com.game.web;

import ar.com.game.domain.UserStatsDTO;
import ar.com.game.services.UserStatsService;
import ar.com.game.repository.DatabaseConnector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty; 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "UserStatsServlet", urlPatterns = "/api/user/stats")
public class UserStatsServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false, \"message\":\"No autorizado\"}");
            return;
        }

        long userId = (long) session.getAttribute("userId");

        try (var conn = DatabaseConnector.getConnection()) {
            UserStatsService service = new UserStatsService(conn);
            UserStatsDTO stats = service.getUserStats(userId);

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);

            var result = new Object() {
                public final boolean success = true;

                @com.fasterxml.jackson.annotation.JsonProperty("stats")
                public final UserStatsDTO statsField = stats;
            };

            objectMapper.writeValue(resp.getWriter(), result);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":false,\"message\":\"Error interno\"}");
            e.printStackTrace();
        }
    }
}

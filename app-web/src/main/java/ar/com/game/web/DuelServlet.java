package ar.com.game.web;

import ar.com.game.domain.User;
import ar.com.game.services.DuelService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/api/duel/submit")
public class DuelServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final DuelService duelService = new DuelService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "No hay sesión activa."));
            return;
        }

        User user = (User) session.getAttribute("user");
        Map<String, Object> data = mapper.readValue(req.getInputStream(), Map.class);
        int score = (int) data.get("score");

        boolean result = duelService.submitDuelScore(user.getId(), score);

        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), Map.of("success", true));
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of("success", false, "message", "Error registrando puntaje."));
        }
    }
}

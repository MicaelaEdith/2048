package ar.com.game.web;

import ar.com.game.services.Game2048;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/move", "/api/state"})
public class GameServlet extends HttpServlet {

    private static final long serialVersionUID = 5317537938199851509L;
    private final Game2048 game = new Game2048();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (req.getRequestURI().endsWith("/state")) {
            Map<String, Object> response = new HashMap<>();
            response.put("board", game.getBoard());
            response.put("score", game.getScore());
            response.put("gameOver", game.isGameOver());

            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), response);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (req.getRequestURI().endsWith("/move")) {
            Map<String, String> data = mapper.readValue(req.getInputStream(), Map.class);
            String direction = data.get("direction");

            game.move(direction);

            Map<String, Object> response = new HashMap<>();
            response.put("board", game.getBoard());
            response.put("score", game.getScore());
            response.put("gameOver", game.isGameOver());

            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), response);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

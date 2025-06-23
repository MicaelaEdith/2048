package ar.com.game.web;

import ar.com.game.domain.Tournament;
import ar.com.game.domain.User;
import ar.com.game.repository.DatabaseConnector;
import ar.com.game.repository.TournamentParticipantsRepository;
import ar.com.game.repository.TournamentParticipantsRepositoryImpl;
import ar.com.game.repository.TournamentRepository;
import ar.com.game.repository.TournamentRepositoryImpl;
import ar.com.game.services.TournamentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {
    "/api/tournament/list",
    "/api/tournament/create",
    "/api/tournament/join"
})
public class TournamentServlet extends HttpServlet {

    private static final long serialVersionUID = 7195793083073292071L;

    private TournamentService tournamentService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        try {
            Connection connection = DatabaseConnector.getConnection();
            TournamentRepository tournamentRepo = new TournamentRepositoryImpl(connection);
            TournamentParticipantsRepository participantsRepo = new TournamentParticipantsRepositoryImpl(connection);
            this.tournamentService = new TournamentService(tournamentRepo, participantsRepo);
        } catch (Exception e) {
            throw new ServletException("Error inicializando TournamentService", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(resp.getWriter(), Map.of(
                "success", false,
                "message", "No hay sesión activa."
            ));
            return;
        }

        List<Tournament> allTournaments = tournamentService.getAllTournaments();
        List<Map<String, Object>> tournamentsDTO = allTournaments.stream()
            .map(t -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", t.getId());
                m.put("name", t.getName());
                return m;
            })
            .collect(Collectors.toList());

        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), Map.of(
            "success", true,
            "tournaments", tournamentsDTO
        ));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        HttpSession session = req.getSession(false);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(resp.getWriter(), Map.of(
                "success", false,
                "message", "No hay sesión activa."
            ));
            return;
        }

        Map<String, Object> data;
        try {
            data = mapper.readValue(req.getInputStream(), Map.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), Map.of(
                "success", false,
                "message", "JSON inválido: " + e.getMessage()
            ));
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            if (path.endsWith("/create")) {
                String name = (String) data.get("name");
                Tournament created = tournamentService.createTournament(name, user.getId());

                resp.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(resp.getWriter(), Map.of(
                    "success", true,
                    "id", created.getId(),
                    "name", created.getName()
                ));
            } else if (path.endsWith("/join")) {
                Object rawId = data.get("tournamentId");
                int id;

                // Puede venir como Integer o String, manejar ambos casos
                if (rawId instanceof Integer) {
                    id = (Integer) rawId;
                } else if (rawId instanceof String) {
                    id = Integer.parseInt((String) rawId);
                } else {
                    throw new IllegalArgumentException("ID de torneo inválido");
                }

                if (tournamentService.isTournamentFull(id)) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    mapper.writeValue(resp.getWriter(), Map.of(
                        "success", false,
                        "message", "El torneo está lleno."
                    ));
                    return;
                }

                boolean joined = tournamentService.joinTournament(id, user.getId());
                if (!joined) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    mapper.writeValue(resp.getWriter(), Map.of(
                        "success", false,
                        "message", "No se pudo unir al torneo."
                    ));
                    return;
                }

                resp.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(resp.getWriter(), Map.of(
                    "success", true,
                    "message", "Unido correctamente al torneo"
                ));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), Map.of(
                "success", false,
                "message", "Error interno: " + e.getMessage()
            ));
        }
    }
}

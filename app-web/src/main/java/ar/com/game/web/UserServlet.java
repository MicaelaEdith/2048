package ar.com.game.web;

import ar.com.game.services.UserService;
import ar.com.game.services.DuelService;
import ar.com.game.services.ServiceResponse;
import ar.com.game.domain.User;
import ar.com.game.domain.Duel;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {
    "/api/user/register",
    "/api/user/login",
    "/api/user/logout",
    "/api/user/me",
    "/api/user/contacts",
    "/api/user/send-duel",
    "/api/user/update-points",
    "/api/user/duel/pending",
    "/api/user/duel/last",
    "/api/user/update-profile",
    "/api/user/delete-account"

})
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService = new UserService();
    private final DuelService duelService = new DuelService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        HttpSession session = req.getSession(false);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (path.endsWith("/me")) {
                if (session != null && session.getAttribute("user") != null) {
                    User user = (User) session.getAttribute("user");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    writeJson(resp, Map.of(
                        "success", true,
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail()
                    ));
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of(
                        "success", false,
                        "message", "No hay sesión activa."
                    ));
                }
            } else if (path.endsWith("/logout")) {
                if (session != null) {
                    session.invalidate();
                }
                resp.setStatus(HttpServletResponse.SC_OK);
                writeJson(resp, Map.of(
                    "success", true,
                    "message", "Logout exitoso."
                ));
            } else if (path.endsWith("/contacts")) {
                if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of(
                        "success", false,
                        "message", "No hay sesión activa."
                    ));
                    return;
                }
                User user = (User) session.getAttribute("user");
                List<User> allUsers = userService.getAllUsers();
                List<Map<String, Object>> contactsDto = allUsers.stream()
                	    .filter(u -> !u.getId().equals(user.getId()))
                	    .map(u -> {
                	        Map<String, Object> m = new HashMap<>();
                	        m.put("id", u.getId());
                	        m.put("name", u.getName());
                	        m.put("email", u.getEmail());
                	        return m;
                	    })
                	    .collect(Collectors.toList());

                resp.setStatus(HttpServletResponse.SC_OK);
                writeJson(resp, Map.of(
                    "success", true,
                    "contacts", contactsDto
                ));
            } else if (path.endsWith("/duel/pending")) {
                if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of("success", false, "message", "No hay sesión activa."));
                    return;
                }

                User user = (User) session.getAttribute("user");
                Duel duel = duelService.getPendingDuelForUser(user.getId());

                if (duel != null) {
                    int opponentId = duel.getPlayer1Id() == user.getId() ? duel.getPlayer2Id() : duel.getPlayer1Id();
                    User opponent = userService.findById((long)opponentId);
                    String opponentName = opponent != null ? opponent.getName() : "oponente";
                    Integer opponentScore = null;

                    if (duel.getPlayer1Id() == user.getId()) {
                        opponentScore = duel.getPlayer2Points();
                    } else {
                        opponentScore = duel.getPlayer1Points();
                    }

                    writeJson(resp, Map.of(
                        "success", true,
                        "duelPending", true,
                        "opponentName", opponentName,
                        "opponentScore", opponentScore != null ? opponentScore : 0
                    ));

                } else {
                    writeJson(resp, Map.of("success", true, "duelPending", false));
                }
            } 
            
         else if (path.endsWith("/duel/last")) {
            if (session == null || session.getAttribute("user") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writeJson(resp, Map.of("success", false, "message", "No hay sesión activa."));
                return;
            }

            User user = (User) session.getAttribute("user");
            Duel lastDuel = duelService.getLastDuelForUser(user.getId());

            if (lastDuel != null) {
                Integer p1 = lastDuel.getPlayer1Points();
                Integer p2 = lastDuel.getPlayer2Points();

                boolean isPlayer1 = lastDuel.getPlayer1Id() == user.getId();
                Integer ownPoints = isPlayer1 ? p1 : p2;
                Integer opponentPoints = isPlayer1 ? p2 : p1;

                User opponent = userService.findById((long)(isPlayer1 ? lastDuel.getPlayer2Id() : lastDuel.getPlayer1Id()));
                String opponentName = opponent != null ? opponent.getName() : "oponente";

                if (ownPoints != null && opponentPoints != null) {
                    String result = ownPoints > opponentPoints ? "Ganaste" :
                                    ownPoints < opponentPoints ? "Perdiste" :
                                    "Empate";
                    writeJson(resp, Map.of(
                        "success", true,
                        "duelFound", true,
                        "message", String.format("Último duelo contra %s: %s (%d vs %d)",
                            opponentName, result, ownPoints, opponentPoints)
                    ));
                } else {
                    writeJson(resp, Map.of(
                        "success", true,
                        "duelFound", true,
                        "message", String.format("Último duelo contra %s: el oponente aún no ha jugado su partida.",
                            opponentName)
                    ));
                }
            } else {
                writeJson(resp, Map.of("success", true, "duelFound", false));
            }
        } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, Map.of(
                "success", false,
                "message", "Error en el servidor: " + e.getMessage()
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        Map<String, String> data;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            data = mapper.readValue(req.getInputStream(), Map.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, Map.of(
                "success", false,
                "message", "JSON inválido: " + e.getMessage()
            ));
            return;
        }

        HttpSession session = req.getSession();

        try {
            if (path.endsWith("/register")) {
                String name = data.get("name");
                String email = data.get("email");
                String password = data.get("password");

                if (name == null || email == null || password == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, Map.of(
                        "success", false,
                        "message", "Faltan campos obligatorios: name, email y password."
                    ));
                    return;
                }

                ServiceResponse result = userService.registerUser(name, email, password);
                boolean ok = result.isSuccess();

                if (ok) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    if (result.getMessage().contains("Error al registrar el usuario:")) {
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    } else if (result.getMessage().contains("ya existe")) {
                        resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }

                writeJson(resp, Map.of(
                    "success", ok,
                    "message", result.getMessage()
                ));

            } else if (path.endsWith("/login")) {
                String email = data.get("email");
                String password = data.get("password");
                if (email == null || password == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, Map.of(
                        "success", false,
                        "message", "Email y password son obligatorios."
                    ));
                    return;
                }

                ServiceResponse loginResp = userService.loginUser(email, password);
                if (loginResp.isSuccess()) {
                    User user = (User) loginResp.getData();
                    session.setAttribute("user", user);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    writeJson(resp, Map.of(
                        "success", true,
                        "message", loginResp.getMessage(),
                        "userId", user.getId()
                    ));
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of(
                        "success", false,
                        "message", loginResp.getMessage()
                    ));
                }

            }  else if (path.endsWith("/send-duel")) {
                if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of(
                        "success", false,
                        "message", "No hay sesión activa."
                    ));
                    return;
                }

                User user = (User) session.getAttribute("user");

                Object rawOpponentId = data.get("opponentId");

                if (rawOpponentId == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, Map.of("success", false, "message", "Falta opponentId."));
                    return;
                }

                int opponentId;
                if (rawOpponentId instanceof Integer) {
                    opponentId = (Integer) rawOpponentId;
                } else {
                    opponentId = Integer.parseInt(rawOpponentId.toString());
                }

                if (opponentId == user.getId()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, Map.of("success", false, "message", "No podés enviarte un duelo a vos mismo."));
                    return;
                }

                boolean created = duelService.createDuel(user.getId(), opponentId);
                if (created) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    writeJson(resp, Map.of("success", true, "message", "Solicitud de duelo enviada."));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeJson(resp, Map.of("success", false, "message", "Error al crear duelo."));
                }
            

            } else if (path.endsWith("/update-points")) {
            	if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of("success", false, "message", "No hay sesión activa."));
                    return;
                }

                User user = (User) session.getAttribute("user");
                Object scoreRaw = data.get("score");
                Object duelIdRaw = data.get("duelId");

                if (scoreRaw == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, Map.of("success", false, "message", "Falta el puntaje."));
                    return;
                }

                int score;
                if (scoreRaw instanceof Integer) {
                    score = (Integer) scoreRaw;
                } else {
                    try {
                        score = Integer.parseInt(scoreRaw.toString());
                    } catch (NumberFormatException e) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, Map.of("success", false, "message", "Formato de puntaje inválido."));
                        return;
                    }
                }

                userService.updateTotalPointsIfHigher(user.getId(), score);

                if (duelIdRaw != null) {
                    int duelId;
                    try {
                        if (duelIdRaw instanceof Integer) {
                            duelId = (Integer) duelIdRaw;
                        } else {
                            duelId = Integer.parseInt(duelIdRaw.toString());
                        }
                    } catch (NumberFormatException e) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, Map.of("success", false, "message", "Formato de ID de duelo inválido."));
                        return;
                    }

                    ServiceResponse duelUpdateResp = userService.updateUserStatsAfterDuel((long)user.getId(), duelId, score);

                    if (!duelUpdateResp.isSuccess()) {
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        writeJson(resp, Map.of("success", false, "message", duelUpdateResp.getMessage()));
                        return;
                    }
                }

                resp.setStatus(HttpServletResponse.SC_OK);
                writeJson(resp, Map.of("success", true, "message", "Puntaje y estadísticas actualizadas correctamente."));
            } else if (path.endsWith("/update-profile")) {
                if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of("success", false, "message", "No hay sesión activa."));
                    return;
                }

                User currentUser = (User) session.getAttribute("user");

                String newName = data.get("name");
                String currentPassword = data.get("currentPassword"); 
                String newPassword = data.get("password");

                if (newName == null || newName.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, Map.of("success", false, "message", "El nombre es obligatorio."));
                    return;
                }
                
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    if (currentPassword == null || currentPassword.trim().isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, Map.of("success", false, "message", "Debe ingresar la contraseña actual para cambiarla."));
                        return;
                    }

                    ServiceResponse validPass = userService.loginUser(currentUser.getEmail(), currentPassword);
                    if (!validPass.isSuccess()) {
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        writeJson(resp, Map.of("success", false, "message", "Contraseña actual incorrecta."));
                        return;
                    }
                }

                try {
                    User userToUpdate = userService.findById((long)currentUser.getId());
                    userToUpdate.setName(newName.trim());

                    if (newPassword != null && !newPassword.trim().isEmpty()) {
                        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(newPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
                        userToUpdate.setPassword(hashed);
                    }

                    ServiceResponse updateResult = userService.updateUser(userToUpdate);

                    if (updateResult.isSuccess()) {
                        session.setAttribute("user", userToUpdate);

                        resp.setStatus(HttpServletResponse.SC_OK);
                        writeJson(resp, Map.of("success", true, "message", updateResult.getMessage()));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, Map.of("success", false, "message", updateResult.getMessage()));
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeJson(resp, Map.of("success", false, "message", "Error interno: " + e.getMessage()));
                }
            } else if (path.endsWith("/delete-account")) {
                if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of("success", false, "message", "No hay sesión activa."));
                    return;
                }

                User currentUser = (User) session.getAttribute("user");

                ServiceResponse deleteResult = userService.deleteUserById((long)currentUser.getId());

                if (deleteResult.isSuccess()) {
                    session.invalidate();
                    resp.setStatus(HttpServletResponse.SC_OK);
                    writeJson(resp, Map.of("success", true, "message", deleteResult.getMessage()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeJson(resp, Map.of("success", false, "message", deleteResult.getMessage()));
                }
                } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, Map.of(
                "success", false,
                "message", "Error interno: " + e.getMessage()
            ));
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        mapper.writeValue(resp.getWriter(), data);
    }
}

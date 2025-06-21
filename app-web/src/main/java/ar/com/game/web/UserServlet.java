package ar.com.game.web;

import ar.com.game.services.UserService;
import ar.com.game.services.DuelService;
import ar.com.game.services.ServiceResponse;
import ar.com.game.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {
    "/api/user/register",
    "/api/user/login",
    "/api/user/logout",
    "/api/user/me",
    "/api/user/contacts",
    "/api/user/send-duel"
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
                List<User> contacts = allUsers.stream()
                                             .filter(u -> !u.getId().equals(user.getId()))
                                             .collect(Collectors.toList());
                resp.setStatus(HttpServletResponse.SC_OK);
                writeJson(resp, Map.of("success", true, "contacts", contacts));
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

            } else if (path.endsWith("/send-duel")) {
                if (session == null || session.getAttribute("user") == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJson(resp, Map.of(
                        "success", false,
                        "message", "No hay sesión activa."
                    ));
                    return;
                }
                User user = (User) session.getAttribute("user");

                String opponentIdStr = data.get("opponentId");
                if (opponentIdStr == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, Map.of("success", false, "message", "Falta opponentId."));
                    return;
                }
                int opponentId = Integer.parseInt(opponentIdStr);

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

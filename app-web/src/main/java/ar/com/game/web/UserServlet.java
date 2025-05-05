package ar.com.game.web;

import ar.com.game.services.UserService;
import ar.com.game.services.ServiceResponse;
import ar.com.game.domain.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
    "/api/user/register",
    "/api/user/login",
    "/api/user/logout",
    "/api/user/me"
})
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        Map<String, String> data;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Parse JSON body and handle malformed JSON
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

                // Set response status based on result
                if (ok) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    // If internal server error (message contains 'Error al registrar el usuario:')
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

            } else if (path.endsWith("/logout")) {
                session.invalidate();
                resp.setStatus(HttpServletResponse.SC_OK);
                writeJson(resp, Map.of(
                    "success", true,
                    "message", "Logout exitoso."
                ));

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (path.endsWith("/me")) {
            HttpSession session = req.getSession(false);
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
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        mapper.writeValue(resp.getWriter(), data);
    }
}

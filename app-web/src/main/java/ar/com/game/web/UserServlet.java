package ar.com.game.web;

import ar.com.game.services.UserService;
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
        Map<String, String> data = mapper.readValue(req.getInputStream(), Map.class);
        HttpSession session = req.getSession();

        if (path.endsWith("/register")) {
            String name = data.get("name");
            String email = data.get("email");
            String password = data.get("password");

            String registrationMessage = userService.registerUser(name, email, password);
            if (registrationMessage.equals("Usuario registrado con éxito.")) {
                writeJson(resp, Map.of("success", true, "message", registrationMessage));
            } else {
                writeJson(resp, Map.of("success", false, "message", registrationMessage));
            }

        } else if (path.endsWith("/login")) {
            String email = data.get("email");
            String password = data.get("password");

            String loginMessage = userService.loginUser(email, password);
            if (loginMessage.equals("Login exitoso.")) {
                User user = userService.findById(Long.parseLong(data.get("userId")));
                session.setAttribute("user", user);
                writeJson(resp, Map.of("success", true, "message", loginMessage, "userId", user.getId()));
            } else {
                writeJson(resp, Map.of("success", false, "message", loginMessage));
            }

        } else if (path.endsWith("/logout")) {
            session.invalidate();
            writeJson(resp, Map.of("success", true, "message", "Logout exitoso."));

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        if (path.endsWith("/me")) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                writeJson(resp, Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail()
                ));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writeJson(resp, Map.of("error", "No active session"));
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), data);
    }
}

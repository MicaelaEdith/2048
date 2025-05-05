package ar.com.game.services;

import ar.com.game.domain.User;
import ar.com.game.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class UserService {

    private final UserRepository repository;

    public UserService() {
        this.repository = new UserRepository();
    }

    public ServiceResponse registerUser(String name, String email, String password) {
        // Validaciones previas
        if (name == null || name.trim().length() < 2) {
            return new ServiceResponse(false, "El nombre debe tener al menos 2 caracteres.");
        }
        // Regex corregido para email
        if (email == null || !Pattern.matches("^\\S+@\\S+\\.\\S+$", email)) {
            return new ServiceResponse(false, "El correo electrónico no es válido.");
        }
        if (password == null || password.length() < 6) {
            return new ServiceResponse(false, "La contraseña debe tener al menos 6 caracteres.");
        }

        try {
            // Verificar si ya existe el email
            User existing = repository.findByEmail(email);
            if (existing != null) {
                return new ServiceResponse(false, "El usuario con este correo electrónico ya existe.");
            }

            // Hash de contraseña
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.toLowerCase());
            user.setPassword(hashed);
            user.setTotalPoints(0);
            user.setLevel(1);
            user.setCompetitiveMatches(0);
            user.setWins(0);
            user.setLosses(0);
            user.setContacts("");
            user.setCurrentSkin("default");
            user.setAvailableSkins("default");

            // Intentar creación de usuario
            boolean created = repository.createUser(user);
            if (created) {
                return new ServiceResponse(true, "Usuario registrado con éxito.");
            } else {
                return new ServiceResponse(false, "Error al registrar el usuario. Intente nuevamente.");
            }
        } catch (SQLException e) {
            // Propagar mensaje de excepción al frontend
            return new ServiceResponse(false, "Error al registrar el usuario: " + e.getMessage());
        }
    }

    public ServiceResponse loginUser(String email, String password) {
        try {
            User user = repository.findByEmail(email);
            if (user == null) {
                return new ServiceResponse(false, "El correo electrónico no está registrado.");
            }
            if (BCrypt.checkpw(password, user.getPassword())) {
                return new ServiceResponse(true, "Login exitoso.", user);
            } else {
                return new ServiceResponse(false, "La contraseña es incorrecta.");
            }
        } catch (SQLException e) {
            return new ServiceResponse(false, "Error en login: " + e.getMessage());
        }
    }

    public ServiceResponse updateUser(User user) {
        try {
            boolean ok = repository.updateUser(user);
            return new ServiceResponse(ok, ok ? "Usuario actualizado con éxito." : "Error al actualizar el usuario. Intente nuevamente.");
        } catch (SQLException e) {
            return new ServiceResponse(false, "Error al actualizar el usuario: " + e.getMessage());
        }
    }

    public ServiceResponse deleteUserById(Long id) {
        try {
            boolean ok = repository.deleteUserById(id);
            return new ServiceResponse(ok, ok ? "Usuario eliminado con éxito." : "Error al eliminar el usuario. Intente nuevamente.");
        } catch (SQLException e) {
            return new ServiceResponse(false, "Error al eliminar el usuario: " + e.getMessage());
        }
    }

    public User findById(Long id) throws SQLException {
        return repository.findById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return repository.getAllUsers();
    }
}

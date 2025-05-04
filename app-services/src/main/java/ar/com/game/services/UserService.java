package ar.com.game.services;

import ar.com.game.domain.User;
import ar.com.game.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.regex.Pattern;

public class UserService {

    private final UserRepository repository;

    public UserService() {
        this.repository = new UserRepository();
    }


    public String registerUser(String name, String email, String password) {

        if (!isValidName(name)) {
            return "El nombre debe tener al menos 2 caracteres.";
        }
        if (!isValidEmail(email)) {
            return "El correo electrónico no es válido.";
        }
        if (!isValidPassword(password)) {
            return "La contraseña debe tener al menos 6 caracteres.";
        }

        if (repository.findByEmail(email) != null) {
            return "El usuario con este correo electrónico ya existe.";
        }

        String hashedPassword = hashPassword(password);

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.toLowerCase());
        user.setPassword(hashedPassword);
        user.setTotalPoints(0);
        user.setLevel(1);
        user.setCompetitiveMatches(0);
        user.setWins(0);
        user.setLosses(0);
        user.setContacts("");
        user.setCurrentSkin("default");
        user.setAvailableSkins("default");

        if (repository.createUser(user)) {
            return "Usuario registrado con éxito.";
        } else {
            return "Error al registrar el usuario. Intente nuevamente.";
        }
    }

    public String loginUser(String email, String password) {
        User user = repository.findByEmail(email);
        if (user == null) {
            return "El correo electrónico no está registrado.";
        }

        if (checkPassword(password, user.getPassword())) {
            return "Login exitoso.";
        } else {
            return "La contraseña es incorrecta.";
        }
    }

    public String updateUser(User user) {
        if (repository.updateUser(user)) {
            return "Usuario actualizado con éxito.";
        } else {
            return "Error al actualizar el usuario. Intente nuevamente.";
        }
    }

    public String deleteUserById(Long id) {
        if (repository.deleteUserById(id)) {
            return "Usuario eliminado con éxito.";
        } else {
            return "Error al eliminar el usuario. Intente nuevamente.";
        }
    }

    public User findById(Long id) {
        return repository.findById(id);
    }

    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }

    private boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }

    private boolean isValidEmail(String email) {
        return email != null && Pattern.matches("^\\S+@\\S+\\.\\S+$", email);
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}

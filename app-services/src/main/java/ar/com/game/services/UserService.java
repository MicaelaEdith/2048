package ar.com.game.services;

import ar.com.game.domain.User;
import ar.com.game.domain.Duel;
import ar.com.game.repository.UserRepository;
import ar.com.game.repository.UserRankingDTO;
import ar.com.game.repository.DuelRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

            boolean created = repository.createUser(user);
            if (created) {
                return new ServiceResponse(true, "Usuario registrado con éxito.");
            } else {
                return new ServiceResponse(false, "Error al registrar el usuario. Intente nuevamente.");
            }
        } catch (SQLException e) {
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
    

    
    public List<UserRankingDTO> getTop10Players() throws SQLException {
        return repository.getTop10Players();
    }

    public ServiceResponse loginUserWithDuelCheck(String email, String password) {
        try {
            User user = repository.findByEmail(email);
            if (user == null) {
                return new ServiceResponse(false, "El correo electrónico no está registrado.");
            }

            if (!BCrypt.checkpw(password, user.getPassword())) {
                return new ServiceResponse(false, "La contraseña es incorrecta.");
            }

            DuelRepository duelRepo = new DuelRepository();
            Duel pendingDuel = duelRepo.findPendingDuelForUser(user.getId());

            if (pendingDuel != null) {
                return new ServiceResponse(true, "Login exitoso. Tienes un duelo pendiente.", user, pendingDuel);
            } else {
                return new ServiceResponse(true, "Login exitoso.", user);
            }
        } catch (SQLException e) {
            return new ServiceResponse(false, "Error en login: " + e.getMessage());
        }
    }
    

    public List<String> getAvailableSkins(int userId) {
        try {
            User user = repository.findById((long) userId);
            if (user == null || user.getAvailableSkins() == null || user.getAvailableSkins().trim().isEmpty()) {
                return List.of();
            }
            return Arrays.stream(user.getAvailableSkins().split(","))
                         .map(String::trim)
                         .filter(s -> !s.isEmpty())
                         .collect(Collectors.toList());

        } catch (SQLException e) {
            return List.of();
        }
    }

    public ServiceResponse updateCurrentSkin(int userId, String newSkin) {
        if (newSkin == null || newSkin.trim().isEmpty()) {
            return new ServiceResponse(false, "Campo vacío.");
        }
        try {
            User user = repository.findById((long) userId);
            if (user == null) {
                return new ServiceResponse(false, "Usuario no encontrado.");
            }
            List<String> availableSkins = getAvailableSkins(userId);
            if (!availableSkins.contains(newSkin)) {
                return new ServiceResponse(false, "Skin no disponible.");
            }

            user.setCurrentSkin(newSkin);
            boolean updated = repository.updateUser(user);
            if (updated) {
                return new ServiceResponse(true, "Skin actualizado correctamente.");
            } else {
                return new ServiceResponse(false, "No se pudo actualizar.");
            }
        } catch (SQLException e) {
            return new ServiceResponse(false, "Error al actualizar: " + e.getMessage());
        }
    }

    /**
     * Devuelve una lista simplificada de usuarios para el dropdown de contactos,
     * excluyendo al usuario cuyo id se pasa por parámetro.
     */
    public List<ContactDto> getContactsExcept(int excludeUserId) {
        try {
            List<User> allUsers = repository.getAllUsers();
            return allUsers.stream()
                    .filter(u -> u.getId() != excludeUserId)
                    .map(u -> new ContactDto(u.getId(), u.getName()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of(); // Devuelve lista vacía en caso de error
        }
    }

    /**
     * DTO simple para enviar al frontend solo id y nombre del usuario.
     */
    public static class ContactDto {
        private int id;
        private String name;

        public ContactDto(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters necesarios para serialización JSON
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
    
    public void updateTotalPointsIfHigher(int userId, int score) {
        try {
            repository.updateTotalPointsIfHigher(userId, score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ServiceResponse updateUserStatsAfterDuel(Long userId, int duelId, int score) {
        try {
        	DuelRepository duelRepo = new DuelRepository();
        	Duel duel = duelRepo.findById(duelId);

            if (duel == null) {
                return new ServiceResponse(false, "Duelo no encontrado.");
            }

            Integer winnerId = duel.getWinnerId();

            boolean won = winnerId != null && winnerId.equals(userId);
            boolean lost = winnerId != null && !winnerId.equals(userId);

            boolean ok = repository.updateUserStatsAfterDuel(userId, won, lost, score);

            if (!ok) {
                return new ServiceResponse(false, "Error al actualizar estadísticas de usuario.");
            }

            return new ServiceResponse(true, "Estadísticas de usuario actualizadas correctamente.");
        } catch (SQLException e) {
            return new ServiceResponse(false, "Error al actualizar estadísticas: " + e.getMessage());
        }
    }

    public ServiceResponse updateStatsForBothPlayersFromDuel(int duelId) {
        try {
            boolean ok = repository.updateStatsForBothPlayersFromDuel(duelId);
            if (!ok) {
                return new ServiceResponse(false, "Error al actualizar estadísticas.");
            }
            return new ServiceResponse(true, "Estadísticas actualizadas correctamente.");
        } catch (SQLException e) {
            return new ServiceResponse(false, "Error: " + e.getMessage());
        }
    }

}


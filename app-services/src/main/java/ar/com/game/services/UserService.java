package ar.com.game.services;

import ar.com.game.domain.User;
import ar.com.game.repository.UserRepository;

public class UserService {

    private final UserRepository repository;

    public UserService() {
        this.repository = new UserRepository();
    }

    public boolean registerUser(String name, String email, String password) {
        if (name == null || email == null || password == null) return false;
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return repository.createUser(user);
    }

    public boolean updateUserNameAndPassword(String email, String newName, String newPassword) {
        User user = repository.findByEmail(email);
        if (user == null) return false;
        user.setName(newName);
        user.setPassword(newPassword);
        return repository.updateUser(user);
    }

    public boolean updateUserContacts(int userId, String contacts) {
        return repository.updateContacts(userId, contacts);
    }

    public boolean updateUserStats(User updatedStats) {
        return repository.updateStats(updatedStats);
    }
}

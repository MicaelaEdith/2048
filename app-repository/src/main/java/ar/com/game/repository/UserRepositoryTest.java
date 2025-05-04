package ar.com.game.repository;

import ar.com.game.domain.User;

public class UserRepositoryTest {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();

        User testUser = new User();
        testUser.setName("testUser2");
        testUser.setPassword("1234");
        testUser.setEmail("test22@example.com");
        testUser.setContacts("[]");
        testUser.setAvailableSkins("[]");

        boolean registered = userRepository.createUser(testUser);

        if (registered) {
            System.out.println("Usuario registrado correctamente.");
        } else {
            System.out.println("Fallo al registrar el usuario.");
        }
    }
}

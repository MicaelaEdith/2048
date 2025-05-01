package ar.com.game.service;

import ar.com.game.domain.User;

public class UserServiceTest {

    public static void main(String[] args) {
        UserService service = new UserService();

        boolean created = service.registerUser("user user a", "useruser@example.com", "123");
        System.out.println("Usuario creado: " + created);

        boolean updated = service.updateUserNameAndPassword("useruser@example.com", "user user b", "new_123");
        System.out.println("Nombre y contraseña cambiados: " + updated);

        boolean contactUpdated = service.updateUserContacts(1, "2,3,4");
        System.out.println("Contactos actualizados: " + contactUpdated);

        User partialStats = new User();
        partialStats.setId(1);
        partialStats.setTotalPoints(1800);
        partialStats.setWins(5);
        boolean statsUpdated = service.updateUserStats(partialStats);
        System.out.println("Stats actualizadas: " + statsUpdated);
    }
}

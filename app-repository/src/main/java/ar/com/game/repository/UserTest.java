package ar.com.game.repository;

import java.sql.SQLException;

public class UserTest {

    public static void main(String[] args) {
        UserRepository repo = new UserRepository();

        int userId = 1;

        try {
            String contactos = repo.getContactsByUserId(userId);
            System.out.println("Contactos actuales: " + contactos);

            String nuevoContacto = "amigo2@example.com";
            boolean agregado = repo.addContactToUser(userId, nuevoContacto);

            if (agregado) {
                System.out.println("Contacto agregado: " + nuevoContacto);
            } else {
                System.out.println("El contacto ya existía o falló la operación.");
            }

            String contactosActualizados = repo.getContactsByUserId(userId);
            System.out.println("Contactos actualizados: " + contactosActualizados);

        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

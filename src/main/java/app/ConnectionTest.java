package app;

import app.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionTest {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT VERSION()")) {

            System.out.println("Conexión exitosa a la base de datos.");
            if (result.next()) {
                System.out.println("Versión del servidor: " + result.getString(1));
            }

        } catch (SQLException e) {
            System.out.println("No se pudo conectar: " + e.getMessage());
        }
    }
}
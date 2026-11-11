package app.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String CONFIG_FILE = "/database.properties";

    private static final Properties properties = loadProperties();

    // Carga el archivo database.properties una sola vez, cuando se usa la clase por primera vez
    private static Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = DatabaseConnection.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(
                        "No se encontró src/main/resources/database.properties. "
                                + "Copie database.properties.example con ese nombre y complete sus datos.");
            }
            props.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer database.properties: " + e.getMessage());
        }

        return props;
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password", "");

        return DriverManager.getConnection(url, user, password);
    }
}
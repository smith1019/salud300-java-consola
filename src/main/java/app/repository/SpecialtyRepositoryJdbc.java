package app.repository;

import app.config.DatabaseConnection;
import app.config.DatabaseException;
import app.domain.Specialty;
import app.service.outputports.SpecialtyRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialtyRepositoryJdbc implements SpecialtyRepositoryInterface {

    @Override
    public Specialty saveSpecialty(Specialty specialty) {
        String sql = "INSERT INTO specialty (id, name) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, specialty.getId());
            statement.setString(2, specialty.getName());
            statement.executeUpdate();
            return specialty;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo guardar la especialidad: " + e.getMessage(), e);
        }
    }

    @Override
    public Specialty selectSpecialtyById(int id) {
        String sql = "SELECT id, name FROM specialty WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapSpecialty(result);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo buscar la especialidad: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Specialty> selectAllSpecialties() {
        String sql = "SELECT id, name FROM specialty ORDER BY id";
        List<Specialty> specialties = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                specialties.add(mapSpecialty(result));
            }
            return specialties;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudieron listar las especialidades: " + e.getMessage(), e);
        }
    }

    @Override
    public Specialty updateSpecialty(Specialty specialty) {
        String sql = "UPDATE specialty SET name = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, specialty.getName());
            statement.setInt(2, specialty.getId());
            int rows = statement.executeUpdate();
            return rows > 0 ? specialty : null;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo actualizar la especialidad: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSpecialtyById(int id) {
        String sql = "DELETE FROM specialty WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo borrar la especialidad: " + e.getMessage(), e);
        }
    }

    // Convierte la fila actual del ResultSet en un objeto Specialty
    private Specialty mapSpecialty(ResultSet result) throws SQLException {
        return new Specialty(result.getInt("id"), result.getString("name"));
    }
}
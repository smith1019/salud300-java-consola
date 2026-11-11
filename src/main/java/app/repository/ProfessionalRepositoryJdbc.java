package app.repository;

import app.config.DatabaseConnection;
import app.config.DatabaseException;
import app.domain.Professional;
import app.domain.Specialty;
import app.service.outputports.ProfessionalRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfessionalRepositoryJdbc implements ProfessionalRepositoryInterface {

    // JOIN: une cada profesional con su especialidad para reconstruir ambos objetos en una sola consulta
    private static final String SELECT_BASE =
            "SELECT p.id, p.identification, p.name, p.last_name, p.license_number, p.email, "
                    + "s.id AS specialty_id, s.name AS specialty_name "
                    + "FROM professional p JOIN specialty s ON p.specialty_id = s.id ";

    @Override
    public Professional saveProfessional(Professional professional) {
        String sql = "INSERT INTO professional (id, identification, name, last_name, license_number, email, specialty_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, professional.getId());
            statement.setString(2, professional.getIdentification());
            statement.setString(3, professional.getName());
            statement.setString(4, professional.getLastName());
            statement.setString(5, professional.getLicenseNumber());
            statement.setString(6, professional.getEmail());
            statement.setInt(7, professional.getSpecialty().getId());
            statement.executeUpdate();
            return professional;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo guardar el profesional: " + e.getMessage(), e);
        }
    }

    @Override
    public Professional selectProfessionalById(int id) {
        String sql = SELECT_BASE + "WHERE p.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapProfessional(result);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo buscar el profesional: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Professional> selectAllProfessionals() {
        String sql = SELECT_BASE + "ORDER BY p.id";
        List<Professional> professionals = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                professionals.add(mapProfessional(result));
            }
            return professionals;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudieron listar los profesionales: " + e.getMessage(), e);
        }
    }

    @Override
    public Professional updateProfessional(Professional professional) {
        String sql = "UPDATE professional SET identification = ?, name = ?, last_name = ?, license_number = ?, "
                + "email = ?, specialty_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, professional.getIdentification());
            statement.setString(2, professional.getName());
            statement.setString(3, professional.getLastName());
            statement.setString(4, professional.getLicenseNumber());
            statement.setString(5, professional.getEmail());
            statement.setInt(6, professional.getSpecialty().getId());
            statement.setInt(7, professional.getId());
            int rows = statement.executeUpdate();
            return rows > 0 ? professional : null;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo actualizar el profesional: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProfessionalById(int id) {
        String sql = "DELETE FROM professional WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo borrar el profesional: " + e.getMessage(), e);
        }
    }

    // Convierte la fila actual (profesional + especialidad) en un objeto Professional
    private Professional mapProfessional(ResultSet result) throws SQLException {
        Specialty specialty = new Specialty(result.getInt("specialty_id"), result.getString("specialty_name"));

        return new Professional(
                result.getInt("id"),
                result.getString("identification"),
                result.getString("name"),
                result.getString("last_name"),
                result.getString("license_number"),
                result.getString("email"),
                specialty);
    }
}

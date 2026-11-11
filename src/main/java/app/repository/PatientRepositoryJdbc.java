package app.repository;

import app.config.DatabaseConnection;
import app.config.DatabaseException;
import app.domain.Gender;
import app.domain.Patient;
import app.service.outputports.PatientRepositoryInterface;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryJdbc implements PatientRepositoryInterface {

    private static final String SELECT_BASE =
            "SELECT id, identification, name, last_name, gender, date_of_birth, phone FROM patient ";

    @Override
    public Patient savePatient(Patient patient) {
        String sql = "INSERT INTO patient (id, identification, name, last_name, gender, date_of_birth, phone) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, patient.getId());
            statement.setString(2, patient.getIdentification());
            statement.setString(3, patient.getName());
            statement.setString(4, patient.getLastName());
            statement.setString(5, patient.getGender().name());
            statement.setDate(6, Date.valueOf(patient.getDateOfBirth()));
            statement.setString(7, patient.getPhone());
            statement.executeUpdate();
            return patient;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo guardar el paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public Patient selectPatientById(int id) {
        String sql = SELECT_BASE + "WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapPatient(result);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo buscar el paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> selectAllPatients() {
        String sql = SELECT_BASE + "ORDER BY id";
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                patients.add(mapPatient(result));
            }
            return patients;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudieron listar los pacientes: " + e.getMessage(), e);
        }
    }

    @Override
    public Patient updatePatient(Patient patient) {
        String sql = "UPDATE patient SET identification = ?, name = ?, last_name = ?, gender = ?, "
                + "date_of_birth = ?, phone = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, patient.getIdentification());
            statement.setString(2, patient.getName());
            statement.setString(3, patient.getLastName());
            statement.setString(4, patient.getGender().name());
            statement.setDate(5, Date.valueOf(patient.getDateOfBirth()));
            statement.setString(6, patient.getPhone());
            statement.setInt(7, patient.getId());
            int rows = statement.executeUpdate();
            return rows > 0 ? patient : null;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo actualizar el paciente: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePatientById(int id) {
        String sql = "DELETE FROM patient WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo borrar el paciente: " + e.getMessage(), e);
        }
    }

    // Convierte la fila actual del ResultSet en un objeto Patient
    private Patient mapPatient(ResultSet result) throws SQLException {
        return new Patient(
                result.getInt("id"),
                result.getString("identification"),
                result.getString("name"),
                result.getString("last_name"),
                Gender.valueOf(result.getString("gender")),
                result.getDate("date_of_birth").toLocalDate(),
                result.getString("phone"));
    }
}
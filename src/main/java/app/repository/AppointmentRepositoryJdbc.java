package app.repository;

import app.config.DatabaseConnection;
import app.config.DatabaseException;
import app.domain.Appointment;
import app.domain.AppointmentStatus;
import app.service.outputports.AppointmentRepositoryInterface;
import app.service.outputports.PatientRepositoryInterface;
import app.service.outputports.ProfessionalRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepositoryJdbc implements AppointmentRepositoryInterface {

    private static final String SELECT_BASE =
            "SELECT id, patient_id, professional_id, appointment_date, status FROM appointment ";

    // Inyección de dependencias: para armar la cita completa se reutilizan los repositorios de paciente y profesional
    private final PatientRepositoryInterface patientRepository;
    private final ProfessionalRepositoryInterface professionalRepository;

    public AppointmentRepositoryJdbc(PatientRepositoryInterface patientRepository,
                                     ProfessionalRepositoryInterface professionalRepository) {
        this.patientRepository = patientRepository;
        this.professionalRepository = professionalRepository;
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointment (id, patient_id, professional_id, appointment_date, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, appointment.getId());
            statement.setInt(2, appointment.getPatient().getId());
            statement.setInt(3, appointment.getProfessional().getId());
            statement.setTimestamp(4, Timestamp.valueOf(appointment.getDate()));
            statement.setString(5, appointment.getStatus().name());
            statement.executeUpdate();
            return appointment;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo guardar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public Appointment selectAppointmentById(int id) {
        String sql = SELECT_BASE + "WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapAppointment(result);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo buscar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appointment> selectAllAppointments() {
        String sql = SELECT_BASE + "ORDER BY id";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                appointments.add(mapAppointment(result));
            }
            return appointments;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudieron listar las citas: " + e.getMessage(), e);
        }
    }

    @Override
    public Appointment updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointment SET patient_id = ?, professional_id = ?, appointment_date = ?, status = ? "
                + "WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, appointment.getPatient().getId());
            statement.setInt(2, appointment.getProfessional().getId());
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getDate()));
            statement.setString(4, appointment.getStatus().name());
            statement.setInt(5, appointment.getId());
            int rows = statement.executeUpdate();
            return rows > 0 ? appointment : null;

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo actualizar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAppointmentById(int id) {
        String sql = "DELETE FROM appointment WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("No se pudo borrar la cita: " + e.getMessage(), e);
        }
    }

    // Convierte la fila actual en un objeto Appointment, buscando su paciente y su profesional
    private Appointment mapAppointment(ResultSet result) throws SQLException {
        return new Appointment(
                result.getInt("id"),
                patientRepository.selectPatientById(result.getInt("patient_id")),
                professionalRepository.selectProfessionalById(result.getInt("professional_id")),
                result.getTimestamp("appointment_date").toLocalDateTime(),
                AppointmentStatus.valueOf(result.getString("status")));
    }
}
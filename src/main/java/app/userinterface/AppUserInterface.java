package app.userinterface;

import app.config.DatabaseException;
import app.utils.FormRuleValidator;
import app.view.AppointmentView;
import app.view.PatientView;
import app.view.ProfessionalView;
import app.view.SpecialtyView;

public class AppUserInterface {

    // Inyección de dependencias
    private final SpecialtyView specialtyView;
    private final ProfessionalView professionalView;
    private final PatientView patientView;
    private final AppointmentView appointmentView;

    public AppUserInterface(SpecialtyView specialtyView, ProfessionalView professionalView,
                            PatientView patientView, AppointmentView appointmentView) {
        this.specialtyView = specialtyView;
        this.professionalView = professionalView;
        this.patientView = patientView;
        this.appointmentView = appointmentView;
    }

    public void startApp() {
        boolean running = true;

        while (running) {
            int option = FormRuleValidator.validateInt("""
                    
                    ===== SALUD300 =====
                    1. Crear especialidad
                    2. Listar especialidades
                    3. Crear profesional
                    4. Listar profesionales
                    5. Crear paciente
                    6. Listar pacientes
                    7. Crear cita
                    8. Listar citas
                    0. Salir
                    Seleccione una opción:""");

            try {
                switch (option) {
                    case 1:
                        specialtyView.createSpecialty();
                        break;
                    case 2:
                        specialtyView.showSpecialties();
                        break;
                    case 3:
                        professionalView.createProfessional();
                        break;
                    case 4:
                        professionalView.showProfessionals();
                        break;
                    case 5:
                        patientView.createPatient();
                        break;
                    case 6:
                        patientView.showPatients();
                        break;
                    case 7:
                        appointmentView.createAppointment();
                        break;
                    case 8:
                        appointmentView.showAppointments();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Hasta pronto.");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (DatabaseException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        }
    }
}
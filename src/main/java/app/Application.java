package app;

import app.repository.AppointmentRepositoryJdbc;
import app.repository.PatientRepositoryJdbc;
import app.repository.ProfessionalRepositoryJdbc;
import app.repository.SpecialtyRepositoryJdbc;
import app.service.AppointmentServiceImpl;
import app.service.PatientServiceImpl;
import app.service.ProfessionalServiceImpl;
import app.service.SpecialtyServiceImpl;
import app.service.outputports.AppointmentRepositoryInterface;
import app.service.outputports.PatientRepositoryInterface;
import app.service.outputports.ProfessionalRepositoryInterface;
import app.service.outputports.SpecialtyRepositoryInterface;
import app.userinterface.AppUserInterface;
import app.view.AppointmentView;
import app.view.PatientView;
import app.view.ProfessionalView;
import app.view.SpecialtyView;

public class Application {

    public static void main(String[] args) {
        // Repositorios (ahora guardan en la base de datos MariaDB)
        SpecialtyRepositoryInterface specialtyRepository = new SpecialtyRepositoryJdbc();
        ProfessionalRepositoryInterface professionalRepository = new ProfessionalRepositoryJdbc();
        PatientRepositoryInterface patientRepository = new PatientRepositoryJdbc();
        AppointmentRepositoryInterface appointmentRepository =
                new AppointmentRepositoryJdbc(patientRepository, professionalRepository);

        // Servicios
        SpecialtyServiceImpl specialtyService = new SpecialtyServiceImpl(specialtyRepository);
        ProfessionalServiceImpl professionalService = new ProfessionalServiceImpl(professionalRepository, specialtyRepository);
        PatientServiceImpl patientService = new PatientServiceImpl(patientRepository);
        AppointmentServiceImpl appointmentService = new AppointmentServiceImpl(appointmentRepository, patientRepository, professionalRepository);

        // Vistas
        SpecialtyView specialtyView = new SpecialtyView(specialtyService);
        ProfessionalView professionalView = new ProfessionalView(professionalService, specialtyService);
        PatientView patientView = new PatientView(patientService);
        AppointmentView appointmentView = new AppointmentView(appointmentService, patientService, professionalService);

        // Menú principal
        AppUserInterface appUserInterface = new AppUserInterface(specialtyView, professionalView, patientView, appointmentView);
        appUserInterface.startApp();
    }
}
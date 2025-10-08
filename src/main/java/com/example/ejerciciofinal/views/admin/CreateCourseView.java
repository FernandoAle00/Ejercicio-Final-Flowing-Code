package com.example.ejerciciofinal.views.admin;

import com.example.ejerciciofinal.dtos.CreateCourseDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.security.AdminOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@AdminOnly
@Route(value = "admin/create-course", layout = MainLayout.class)
@PageTitle("Crear Curso | Sistema Académico")
public class CreateCourseView extends SecureView {

    private final CourseService courseService;
    private final UserService userService;
    private final TextField nameField = new TextField("Nombre del curso");
    private final NumberField seatsAmmount = new NumberField("Cantidad de cupos");
    private final ComboBox<ProfessorDTO> professorComboBox = new ComboBox<>("Profesor");

    private final Binder<CreateCourseDTO> courseBinder = new Binder<>(CreateCourseDTO.class);

    private CreateCourseDTO newCourse = new CreateCourseDTO();

    public final Button saveButton = new Button("Guardar");


    public CreateCourseView(CourseService courseService, UserService userService){

        this.userService = userService;
        this.courseService = courseService;

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");

        H2 title = new H2("Crear nuevo curso");

        add(title);
        
        nameField.setWidthFull();
        nameField.setRequired(true);
        nameField.setPlaceholder("Ingrese el nombre del curso");

        seatsAmmount.setWidthFull();
        seatsAmmount.setRequired(true);
        seatsAmmount.setPlaceholder("Ingrese la cantidad de cupos");
        seatsAmmount.setMin(1);
        seatsAmmount.setStep(1);

        professorComboBox.setWidthFull();
        professorComboBox.setRequired(true);
        professorComboBox.setItemLabelGenerator(ProfessorDTO::getName);
        professorComboBox.setPlaceholder("Seleccione un profesor");
        professorComboBox.setItems(userService.getAllProfessors());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveCourse());

        configureBinder();

        add(nameField, seatsAmmount, professorComboBox, saveButton);
    }

    private void configureBinder(){
        courseBinder.forField(nameField)
                .asRequired("El nombre del curso es obligatorio")
                .bind(CreateCourseDTO::getName, CreateCourseDTO::setName);

        courseBinder.forField(seatsAmmount)
                .asRequired("La cantidad de cupos es obligatoria")
                .withConverter(
                    // Double -> Integer (model value)
                    value -> value != null ? value.intValue() : null,
                    // Integer -> Double (presentation value)
                    value -> value != null ? value.doubleValue() : null,
                    "Debe ser un número entero"
                )
                .withValidator(seats -> seats != null && seats > 0, "La cantidad de cupos debe ser mayor a 0")
                .bind(CreateCourseDTO::getSeatsAmmount, CreateCourseDTO::setSeatsAmmount);

        courseBinder.forField(professorComboBox)
                .asRequired("El profesor es obligatorio")
                .bind(CreateCourseDTO::getProfessor, CreateCourseDTO::setProfessor);

        courseBinder.setBean(newCourse);
    }

    private void saveCourse() {
        if (courseBinder.validate().isOk()) {
            try {
                courseService.createCourse(newCourse);
                clearForm();
                Notification.show("Curso creado exitosamente", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (IllegalArgumentException e) {
                Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Por favor complete todos los campos requeridos", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void clearForm() {
        newCourse = new CreateCourseDTO();
        courseBinder.setBean(newCourse);
        nameField.clear();
        seatsAmmount.clear();
        professorComboBox.clear();
    }
}

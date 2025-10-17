package com.example.ejerciciofinal.views.admin;

import com.example.ejerciciofinal.dtos.AddressDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.PersonDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.ProfessorDTO;
import com.example.ejerciciofinal.dtos.CreateUserDTO.StudentDTO;
import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin/create-user", layout = MainLayout.class)
@PageTitle("Crear Usuario | Sistema Académico")
public class CreateUserView extends VerticalLayout {

    private final UserService userService;

    private final Binder<CreateUserDTO> userBinder = new Binder<>(CreateUserDTO.class);
    private final Binder<AddressDTO> addressBinder = new Binder<>(AddressDTO.class);

    // Objetos que almacenan los datos del formulario
    private CreateUserDTO currentUserDTO = new CreateUserDTO();
    private AddressDTO currentAddressDTO = new AddressDTO();

    // Campos de User, siempre visibles
    private final TextField usernameField = new TextField("Nombre de usuario");
    private final PasswordField passwordField = new PasswordField("Contraseña");
    private final ComboBox<Role> roleComboBox = new ComboBox<>("Rol");

    // Campos de Person, solo visibles para student o professor
    private final VerticalLayout personSection = new VerticalLayout();
    private final H3 personTitle = new H3("Datos Personales");
    private final TextField nameField = new TextField("Nombre completo");
    private final EmailField emailField = new EmailField("Correo electrónico");
    private final TextField phoneField = new TextField("Teléfono");

    // Sección de Address, visible para student o professor
    private final VerticalLayout addressSection = new VerticalLayout();
    private final H3 addressTitle = new H3("Dirección");
    private final TextField streetField = new TextField("Calle");
    private final TextField cityField = new TextField("Ciudad");
    private final TextField stateField = new TextField("Provincia/Estado");
    private final TextField countryField = new TextField("País");

    // Campos específicos por rol
    // Sección de estudiante
    private final VerticalLayout studentSection = new VerticalLayout();
    private final H3 studentTitle = new H3("Datos de Estudiante");
    private final TextField studentNumberField = new TextField("Número de estudiante");
    private final NumberField avgMarkField = new NumberField("Promedio de notas");

    // Sección de profesor
    private final VerticalLayout professorSection = new VerticalLayout();
    private final H3 professorTitle = new H3("Datos de Profesor");
    private final NumberField salaryField = new NumberField("Salario");

    private final Button saveButton = new Button("Guardar Usuario");

    public CreateUserView(UserService userService) { // auto inyectado por spring
        this.userService = userService;

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");

        // Títutlo principal
        H2 title = new H2("Crear Nuevo Usuario");

        // Campos de user
        usernameField.setWidthFull();
        usernameField.setRequired(true);
        usernameField.setPlaceholder("Ingrese el nombre de usuario");

        passwordField.setWidthFull();
        passwordField.setRequired(true);
        passwordField.setPlaceholder("Ingrese la contraseña");

        roleComboBox.setItems(Role.values());
        roleComboBox.setWidthFull();
        roleComboBox.setItemLabelGenerator(Role::name);
        roleComboBox.setRequired(true);
        roleComboBox.setPlaceholder("Seleccione el rol del usuario");

        // Listener para mostrar/ocultar secciones según el rol
        roleComboBox.addValueChangeListener(event -> updateFormBasedOnRole(event.getValue()));

        // Sección person
        personSection.setPadding(false);
        personSection.setSpacing(false);
        personSection.setVisible(false); // inicialmente oculta

        nameField.setWidthFull();
        nameField.setRequired(true);
        emailField.setWidthFull();
        emailField.setRequired(true);
        phoneField.setWidthFull();
        phoneField.setRequired(true);

        personSection.add(personTitle, nameField, emailField, phoneField);

        // Seccion Address
        addressSection.setPadding(false);
        addressSection.setSpacing(false);
        addressSection.setVisible(false); // inicialmente oculta

        streetField.setWidthFull();
        streetField.setRequired(true);
        cityField.setWidthFull();
        cityField.setRequired(true);
        stateField.setWidthFull();
        stateField.setRequired(true);
        countryField.setWidthFull();
        countryField.setRequired(true);

        addressSection.add(addressTitle, streetField, cityField, stateField, countryField);

        // Sección estudiante
        studentSection.setPadding(false);
        studentSection.setSpacing(false);
        studentSection.setVisible(false); // inicialmente oculta

        studentNumberField.setWidthFull();
        studentNumberField.setRequired(true);
        studentNumberField.setPlaceholder("Legajo de 6 dígitos");

        avgMarkField.setWidthFull();
        avgMarkField.setMin(0);
        avgMarkField.setMax(10);
        avgMarkField.setValue(0.0);
        avgMarkField.setStep(0.1);

        studentSection.add(studentTitle, studentNumberField, avgMarkField);

        // Sección profesor
        professorSection.setPadding(false);
        professorSection.setSpacing(false);
        professorSection.setVisible(false); // inicialmente oculta

        salaryField.setWidthFull();
        salaryField.setMin(0);
        salaryField.setValue(0.0);
        salaryField.setStep(1000.0);
        salaryField.setRequired(true);
        salaryField.setPrefixComponent(new Span("$"));

        professorSection.add(professorTitle, salaryField);

        // Botón guardar
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveUser());

        // Configuración de binders, vinculación de campos a DTOs
        configureBinders();

        add(usernameField, passwordField, roleComboBox, personSection, addressSection, studentSection, professorSection, saveButton);
    }

    private void updateFormBasedOnRole(Role selectedRole) {

        // Si no hay un rol seleccionado, solo deben verse los campos de user
        if (selectedRole == null) {
            personSection.setVisible(false);
            addressSection.setVisible(false);
            studentSection.setVisible(false);
            professorSection.setVisible(false);
            return;
        }

        switch (selectedRole) {
            case ADMIN:
                personSection.setVisible(false);
                addressSection.setVisible(false);
                studentSection.setVisible(false);
                professorSection.setVisible(false);
                break;
            case PROFESSOR:
                personSection.setVisible(true);
                addressSection.setVisible(true);
                studentSection.setVisible(false);
                professorSection.setVisible(true);
                break;
            case STUDENT:
                personSection.setVisible(true);
                addressSection.setVisible(true);
                studentSection.setVisible(true);
                professorSection.setVisible(false);
                break;
        }
    }

    private void saveUser() {
        try {

            CreateUserDTO userDTO = new CreateUserDTO();

            // writeBeanIfValid retorna true si todas las validaciones pasan
            if (!userBinder.writeBeanIfValid(userDTO)) {
                showError("Por favor corrija los errores en el formulario");
                return;
            }

            Role role = userDTO.getRole();

            if (role != Role.ADMIN) {
                if (nameField.isEmpty() || emailField.isEmpty() || phoneField.isEmpty()) {
                    showError("Por favor complete todos los campos personales");
                    return;
                }

                if (!emailField.getValue().contains("@")) {
                    showError("Por favor ingrese un correo electrónico válido");
                    return;
                }

                AddressDTO addressDTO = new AddressDTO();
                if (!addressBinder.writeBeanIfValid(addressDTO)) {
                    showError("Por favor corrija los errores en la sección de dirección");
                    return;
                }

                PersonDTO personDTO;

                if (role == Role.STUDENT) {
                    if (studentNumberField.isEmpty() || avgMarkField.isEmpty()) {
                        showError("Por favor complete todos los campos de estudiante");
                        return;
                    }

                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setName(nameField.getValue());
                    studentDTO.setEmail(emailField.getValue());
                    studentDTO.setPhone(phoneField.getValue());
                    studentDTO.setAddress(addressDTO);
                    studentDTO.setStudentNumber(studentNumberField.getValue());
                    studentDTO.setAvgMark(avgMarkField.getValue() != null ? avgMarkField.getValue() : 0.0);

                    personDTO = studentDTO;
                } else {
                    if (salaryField.getValue() == null || salaryField.getValue() <= 0) {
                        showError("El salario debe ser mayor a 0");
                        return;
                    }

                    ProfessorDTO professorDTO = new ProfessorDTO();
                    professorDTO.setName(nameField.getValue());
                    professorDTO.setEmail(emailField.getValue());
                    professorDTO.setPhone(phoneField.getValue());
                    professorDTO.setAddress(addressDTO);
                    professorDTO.setSalary(salaryField.getValue());

                    personDTO = professorDTO;
                }

                userDTO.setPerson(personDTO);
            }

            userService.createUser(userDTO);

            showSuccess("Usuario creado exitosamente");
            // Limpiar el formulario para un nuevo ingreso
            clearForm();

        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            showError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void configureBinders() {
        // === BINDER DE USER (username, password, role) ===
        userBinder.forField(usernameField)
                .asRequired("El nombre de usuario es obligatorio")
                .withValidator(
                        username -> username.length() >= 3,
                        "El usuario debe tener al menos 3 caracteres"
                )
                .bind(CreateUserDTO::getUserName, CreateUserDTO::setUserName);

        userBinder.forField(passwordField)
                .asRequired("La contraseña es obligatoria")
                .withValidator(
                        password -> password.length() >= 6,
                        "La contraseña debe tener al menos 6 caracteres"
                )
                .bind(CreateUserDTO::getPassword, CreateUserDTO::setPassword);

        userBinder.forField(roleComboBox)
                .asRequired("Debe seleccionar un rol")
                .bind(CreateUserDTO::getRole, CreateUserDTO::setRole);

        // === BINDER DE ADDRESS (street, city, state, country) ===
        addressBinder.forField(streetField)
                .asRequired("La calle es obligatoria")
                .bind(AddressDTO::getStreet, AddressDTO::setStreet);

        addressBinder.forField(cityField)
                .asRequired("La ciudad es obligatoria")
                .bind(AddressDTO::getCity, AddressDTO::setCity);

        addressBinder.forField(stateField)
                .asRequired("El estado/provincia es obligatorio")
                .bind(AddressDTO::getState, AddressDTO::setState);

        addressBinder.forField(countryField)
                .asRequired("El país es obligatorio")
                .bind(AddressDTO::getCountry, AddressDTO::setCountry);

        // Vincular los Binders con los objetos DTO
        userBinder.setBean(currentUserDTO);
        addressBinder.setBean(currentAddressDTO);
    }

    private void clearForm() {
        // Crear nuevos DTOs vacíos y asignarlos a los Binders
        // Esto limpia automáticamente TODOS los campos vinculados
        currentUserDTO = new CreateUserDTO();
        currentAddressDTO = new AddressDTO();

        userBinder.setBean(currentUserDTO);
        addressBinder.setBean(currentAddressDTO);

        // Limpiar campos no vinculados (Person-specific)
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        studentNumberField.clear();
        avgMarkField.clear();
        salaryField.clear();
    }
}

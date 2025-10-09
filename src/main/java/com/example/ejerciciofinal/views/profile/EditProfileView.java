package com.example.ejerciciofinal.views.profile;

import com.example.ejerciciofinal.model.Person;
import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.security.AuthService;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Vista para editar datos personales y de dirección
 * Solo accesible por usuarios con rol STUDENT o PROFESSOR
 */
@Route(value = "profile/edit", layout = MainLayout.class)
@PageTitle("Modificar Datos | Perfil")
public class EditProfileView extends SecureView {

    private final UserService userService;
    
    private Person person;
    
    // Campos de Person
    private TextField nameField;
    private TextField phoneField;
    private EmailField emailField;
    
    // Campos de Address
    private TextField streetField;
    private TextField cityField;
    private TextField stateField;
    private TextField countryField;
    
    // Binders
    private Binder<Person> personBinder;

    public EditProfileView(UserService userService) {
        this.userService = userService;
        
        setSpacing(false);
        setPadding(false);
        setSizeFull();
        
        buildLayout();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar que el usuario tenga rol STUDENT o PROFESSOR
        Role role = AuthService.getCurrentRole();
        if (role != Role.STUDENT && role != Role.PROFESSOR) {
            event.rerouteTo("");
            return;
        }
        
        // Cargar los datos del usuario actual
        loadUserData();
    }

    private void loadUserData() {
        Long userId = AuthService.getCurrentUserId();
        Role role = AuthService.getCurrentRole();
        
        if (role == Role.STUDENT) {
            this.person = userService.getStudentByUserId(userId);
        } else if (role == Role.PROFESSOR) {
            this.person = userService.getProfessorByUserId(userId);
        }
        
        // Cargar datos en el formulario
        if (person != null) {
            populateForm();
        }
    }

    private void buildLayout() {
        // Contenedor principal con padding
        Div mainContainer = new Div();
        mainContainer.getStyle()
                .set("padding", "var(--lumo-space-l)")
                .set("max-width", "800px")
                .set("margin", "0 auto")
                .set("width", "100%");

        H2 title = new H2("Modificar Mis Datos");
        title.getStyle()
                .set("margin-top", "0")
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("color", "var(--lumo-header-text-color)");

        // Card principal
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)");

        // Formulario
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setSpacing(true);
        formContainer.setPadding(false);

        // Sección: Datos Personales
        H3 personalDataTitle = new H3("Datos Personales");
        personalDataTitle.getStyle().set("margin-top", "0");

        FormLayout personalDataForm = new FormLayout();
        personalDataForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        nameField = new TextField("Nombre Completo");
        nameField.setPrefixComponent(VaadinIcon.USER.create());
        nameField.setRequiredIndicatorVisible(true);

        phoneField = new TextField("Teléfono");
        phoneField.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneField.setRequiredIndicatorVisible(true);

        emailField = new EmailField("Email");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setRequiredIndicatorVisible(true);
        emailField.setErrorMessage("Ingrese un email válido");

        personalDataForm.add(nameField, phoneField);
        personalDataForm.add(emailField, 2); // Email ocupa 2 columnas

        // Sección: Dirección
        H3 addressTitle = new H3("Dirección");
        addressTitle.getStyle().set("margin-top", "var(--lumo-space-m)");

        FormLayout addressForm = new FormLayout();
        addressForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        streetField = new TextField("Calle");
        streetField.setPrefixComponent(VaadinIcon.ROAD.create());
        streetField.setRequiredIndicatorVisible(true);

        cityField = new TextField("Ciudad");
        cityField.setPrefixComponent(VaadinIcon.BUILDING.create());
        cityField.setRequiredIndicatorVisible(true);

        stateField = new TextField("Estado/Provincia");
        stateField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        stateField.setRequiredIndicatorVisible(true);

        countryField = new TextField("País");
        countryField.setPrefixComponent(VaadinIcon.GLOBE.create());
        countryField.setRequiredIndicatorVisible(true);

        addressForm.add(streetField, 2); // Calle ocupa 2 columnas
        addressForm.add(cityField, stateField);
        addressForm.add(countryField, 2); // País ocupa 2 columnas

        // Botones
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.getStyle().set("margin-top", "var(--lumo-space-l)");
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        Button cancelButton = new Button("Cancelar", VaadinIcon.CLOSE.create());
        cancelButton.addClickListener(e -> navigateBack());

        Button saveButton = new Button("Guardar Cambios", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveChanges());

        buttonLayout.add(cancelButton, saveButton);

        // Ensamblar formulario
        formContainer.add(
                personalDataTitle,
                personalDataForm,
                addressTitle,
                addressForm,
                buttonLayout
        );

        card.add(formContainer);
        mainContainer.add(title, card);
        add(mainContainer);

        // Configurar binder
        configureBinder();
    }

    private void configureBinder() {
        personBinder = new Binder<>(Person.class);

        // Validaciones para Person
        personBinder.forField(nameField)
                .asRequired("El nombre es obligatorio")
                .bind(Person::getName, Person::setName);

        personBinder.forField(phoneField)
                .asRequired("El teléfono es obligatorio")
                .bind(Person::getPhone, Person::setPhone);

        personBinder.forField(emailField)
                .asRequired("El email es obligatorio")
                .withValidator(new EmailValidator("Email inválido"))
                .bind(Person::getEmail, Person::setEmail);

        // Validaciones para Address (acceso a través de Person)
        personBinder.forField(streetField)
                .asRequired("La calle es obligatoria")
                .bind(
                        per -> per.getAddress() != null ? per.getAddress().getStreet() : "",
                        (per, value) -> {
                            if (per.getAddress() != null) {
                                per.getAddress().setStreet(value);
                            }
                        }
                );

        personBinder.forField(cityField)
                .asRequired("La ciudad es obligatoria")
                .bind(
                        per -> per.getAddress() != null ? per.getAddress().getCity() : "",
                        (per, value) -> {
                            if (per.getAddress() != null) {
                                per.getAddress().setCity(value);
                            }
                        }
                );

        personBinder.forField(stateField)
                .asRequired("El estado/provincia es obligatorio")
                .bind(
                        per -> per.getAddress() != null ? per.getAddress().getState() : "",
                        (per, value) -> {
                            if (per.getAddress() != null) {
                                per.getAddress().setState(value);
                            }
                        }
                );

        personBinder.forField(countryField)
                .asRequired("El país es obligatorio")
                .bind(
                        per -> per.getAddress() != null ? per.getAddress().getCountry() : "",
                        (per, value) -> {
                            if (per.getAddress() != null) {
                                per.getAddress().setCountry(value);
                            }
                        }
                );
    }

    private void populateForm() {
        if (person != null) {
            personBinder.readBean(person);
        }
    }

    private void saveChanges() {
        try {
            // Validar el formulario
            if (personBinder.writeBeanIfValid(person)) {
                // Guardar cambios en la base de datos
                userService.updatePersonData(person);
                
                showSuccessNotification("Datos actualizados correctamente");
                navigateBack();
            } else {
                showErrorNotification("Por favor, corrija los errores en el formulario");
            }
        } catch (Exception e) {
            showErrorNotification("Error al guardar los cambios: " + e.getMessage());
        }
    }

    private void navigateBack() {
        Role role = AuthService.getCurrentRole();
        switch (role) {
            case STUDENT -> getUI().ifPresent(ui -> ui.navigate("student/courses"));
            case PROFESSOR -> getUI().ifPresent(ui -> ui.navigate("professor/courses"));
            default -> getUI().ifPresent(ui -> ui.navigate(""));
        }
    }

    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}

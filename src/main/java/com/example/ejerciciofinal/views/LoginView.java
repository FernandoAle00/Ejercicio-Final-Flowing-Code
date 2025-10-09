package com.example.ejerciciofinal.views;

import com.example.ejerciciofinal.security.AuthService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "login")
@PageTitle("Login | Sistema Académico")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthService authService;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;

    public LoginView(AuthService authService) {
        this.authService = authService;
        
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        
        // Título
        H1 title = new H1("Sistema Académico");
        
        // Campos de texto
        usernameField = new TextField("Usuario");
        usernameField.setWidth("300px");
        usernameField.setRequired(true);
        usernameField.setAutofocus(true);
        
        passwordField = new PasswordField("Contraseña");
        passwordField.setWidth("300px");
        passwordField.setRequired(true);
        
        // Botón de ingreso
        loginButton = new Button("Ingresar");
        loginButton.setWidth("300px");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickListener(e -> handleLogin());
        
        // Login con Enter
        passwordField.addKeyPressListener(Key.ENTER, e -> handleLogin());
        
        add(title, usernameField, passwordField, loginButton);
    }

    private void handleLogin() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        
        if (username == null || username.trim().isEmpty()) {
            showError("Ingrese un nombre de usuario");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            showError("Ingrese una contraseña");
            return;
        }
        
        if (authService.authenticate(username, password)) {
            showSuccess("Bienvenido, " + username);
            getUI().ifPresent(ui -> ui.navigate(""));
        } else {
            showError("Usuario o contraseña incorrectos");
            passwordField.clear();
            passwordField.focus();
        }
    }

    private void showError(String message) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    private void showSuccess(String message) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Si ya está autenticado, redirigir a la página principal
        if (AuthService.isAuthenticated()) {
            event.forwardTo("");
        }
    }
}

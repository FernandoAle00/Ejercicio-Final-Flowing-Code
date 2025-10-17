package com.example.ejerciciofinal.security;

import com.example.ejerciciofinal.model.Role;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación con usuarios mockeados
 */
@Service
public class AuthService {

    // Usuarios mockeados: username -> password, role
    private static final java.util.Map<String, UserCredentials> MOCK_USERS = java.util.Map.of(
        "admin", new UserCredentials("admin123", Role.ADMIN),
        "student", new UserCredentials("student123", Role.STUDENT),
        "professor", new UserCredentials("professor123", Role.PROFESSOR)
    );

    private static final String SESSION_USER_KEY = "authenticated_user";
    private static final String SESSION_ROLE_KEY = "user_role";

    public AuthService() {
        System.out.println("=== AuthService inicializado ===");
        System.out.println("Usuarios disponibles: " + MOCK_USERS.keySet());
        MOCK_USERS.forEach((user, creds) -> {
            System.out.println("  - " + user + " -> password: " + creds.password + ", role: " + creds.role);
        });
    }

    /**
     * Autentica un usuario con credenciales mockeadas
     */
    public boolean authenticate(String username, String password) {

        if (username == null || password == null) {
            System.out.println("Username o password es null");
            return false;
        }

        String usernameLower = username.toLowerCase().trim();
        
        UserCredentials credentials = MOCK_USERS.get(usernameLower);
        
        if (credentials != null) {
            
            if (credentials.password.equals(password)) {
                VaadinSession session = VaadinSession.getCurrent();
                
                if (session != null) {
                    session.setAttribute(SESSION_USER_KEY, username);
                    session.setAttribute(SESSION_ROLE_KEY, credentials.role);

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public static boolean isAuthenticated() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }

    /**
     * Obtiene el usuario actual
     */
    public static String getCurrentUser() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null ? (String) session.getAttribute(SESSION_USER_KEY) : null;
    }

    /**
     * Obtiene el rol del usuario actual
     */
    public static Role getCurrentRole() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null ? (Role) session.getAttribute(SESSION_ROLE_KEY) : null;
    }

    /**
     * Cierra la sesión del usuario
     */
    public static void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(SESSION_USER_KEY, null);
            session.setAttribute(SESSION_ROLE_KEY, null);
            session.close();
        }
    }

    /**
     * Clase interna para almacenar credenciales
     */
    private static class UserCredentials {
        final String password;
        final Role role;

        UserCredentials(String password, Role role) {
            this.password = password;
            this.role = role;
        }
    }
}

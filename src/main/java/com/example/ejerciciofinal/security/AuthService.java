package com.example.ejerciciofinal.security;

import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.model.User;
import com.example.ejerciciofinal.repository.UserRepository;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de autenticación con usuarios de la base de datos
 */
@Service
public class AuthService {

    private static final String SESSION_USER_ID_KEY = "authenticated_user_id";
    private static final String SESSION_USERNAME_KEY = "authenticated_username";
    private static final String SESSION_ROLE_KEY = "user_role";

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Autentica un usuario contra la base de datos
     */
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByUserName(username.trim());
        
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        
        // Comparación simple de contraseña (en producción usar BCrypt)
        if (user.getPassword().equals(password)) {
            VaadinSession session = VaadinSession.getCurrent();
            
            if (session != null) {
                session.setAttribute(SESSION_USER_ID_KEY, user.getId());
                session.setAttribute(SESSION_USERNAME_KEY, user.getUserName());
                session.setAttribute(SESSION_ROLE_KEY, user.getRole());
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public static boolean isAuthenticated() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null && session.getAttribute(SESSION_USER_ID_KEY) != null;
    }

    /**
     * Obtiene el ID del usuario actual
     */
    public static Long getCurrentUserId() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null ? (Long) session.getAttribute(SESSION_USER_ID_KEY) : null;
    }

    /**
     * Obtiene el username del usuario actual
     */
    public static String getCurrentUsername() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null ? (String) session.getAttribute(SESSION_USERNAME_KEY) : null;
    }

    /**
     * Obtiene el rol del usuario actual
     */
    public static Role getCurrentRole() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null ? (Role) session.getAttribute(SESSION_ROLE_KEY) : null;
    }

    /*
     * Obtiene el id del usuario actual
     */
    public static Long getCurrentUserIdStatic() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null? (Long) session.getAttribute(SESSION_USER_ID_KEY) : null;
    }

    /**
     * Cierra la sesión del usuario
     */
    public static void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(SESSION_USER_ID_KEY, null);
            session.setAttribute(SESSION_USERNAME_KEY, null);
            session.setAttribute(SESSION_ROLE_KEY, null);
            session.close();
        }
    }
}

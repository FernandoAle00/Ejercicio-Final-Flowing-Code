package com.example.ejerciciofinal.security;

import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.views.DashboardView;
import com.example.ejerciciofinal.views.LoginView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/**
 * Clase base para vistas que requieren autenticación y rol específico
 * Implementa la lógica de seguridad común
 */
public abstract class SecureView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar si hay usuario autenticado
        if (!AuthService.isAuthenticated()) {
            // Redirigir a login si no está autenticado
            event.rerouteTo(LoginView.class);
            return;
        }

        // Verificar si la vista requiere rol ADMIN
        if (this.getClass().isAnnotationPresent(AdminOnly.class)) {
            Role currentRole = AuthService.getCurrentRole();
            
            if (currentRole != Role.ADMIN) {
                // Redirigir a dashboard si no es ADMIN
                event.rerouteTo(DashboardView.class);
            }
        }
    }
}

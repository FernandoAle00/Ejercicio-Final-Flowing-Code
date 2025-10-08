package com.example.ejerciciofinal.security;

import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.views.DashboardView;
import com.example.ejerciciofinal.views.LoginView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/**
 * Clase base para vistas que requieren autenticación y rol específico
 * Implementa la lógica de seguridad común para ADMIN, PROFESSOR y STUDENT
 */
public abstract class SecureView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // 1. Verificar si hay usuario autenticado
        if (!AuthService.isAuthenticated()) {
            // Redirigir a login si no está autenticado
            event.rerouteTo(LoginView.class);
            return;
        }

        // 2. Obtener el rol actual del usuario
        Role currentRole = AuthService.getCurrentRole();
        
        // 3. Verificar anotaciones de rol específico usando REFLEXIÓN
        Class<?> viewClass = this.getClass();
        
        // Verificar si requiere rol ADMIN
        if (viewClass.isAnnotationPresent(AdminOnly.class)) {
            if (currentRole != Role.ADMIN) {
                event.rerouteTo(DashboardView.class);
                return;
            }
        }
        
        // Verificar si requiere rol PROFESSOR
        if (viewClass.isAnnotationPresent(ProfessorOnly.class)) {
            if (currentRole != Role.PROFESSOR) {
                event.rerouteTo(DashboardView.class);
                return;
            }
        }
        
        // Verificar si requiere rol STUDENT
        if (viewClass.isAnnotationPresent(StudentOnly.class)) {
            if (currentRole != Role.STUDENT) {
                event.rerouteTo(DashboardView.class);
                return;
            }
        }
    }
}

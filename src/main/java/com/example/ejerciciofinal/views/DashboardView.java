package com.example.ejerciciofinal.views;

import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.security.AuthService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Sistema Académico")
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        String username = AuthService.getCurrentUsername();
        Role role = AuthService.getCurrentRole();
        
        H1 title = new H1("Bienvenido al Sistema Académico");
        H2 userInfo = new H2("Usuario: " + username + " | Rol: " + role);
        
        Paragraph instructions = new Paragraph(
            "Utilice el menú lateral para navegar por las diferentes secciones del sistema."
        );
        
        add(title, userInfo, instructions);
    }
}

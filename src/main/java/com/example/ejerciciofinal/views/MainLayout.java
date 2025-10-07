package com.example.ejerciciofinal.views;

import com.example.ejerciciofinal.model.Role;
import com.example.ejerciciofinal.security.AuthService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * MainLayout - Layout principal de la aplicación
 * 
 * Funciones:
 * 1. Verifica autenticación antes de renderizar cualquier vista
 * 2. Muestra menú dinámico según el rol del usuario
 * 3. Proporciona navegación lateral y header con logout
 */
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private SideNav nav;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        
        Span title = new Span("Sistema Académico");
        title.getStyle()
            .set("font-size", "var(--lumo-font-size-l)")
            .set("font-weight", "600")
            .set("margin", "0");
        
        String username = AuthService.getCurrentUser();
        Role role = AuthService.getCurrentRole();
        
        // Si no hay usuario autenticado, solo mostrar título
        if (username == null || role == null) {
            HorizontalLayout header = new HorizontalLayout(toggle, title);
            header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            header.setWidthFull();
            header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
            );
            addToNavbar(header);
            return;
        }
        
        Span userInfo = new Span(username + " (" + role + ")");
        userInfo.getStyle().set("margin-left", "auto");
        
        Button logoutButton = new Button("Cerrar Sesión", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        logoutButton.addClickListener(e -> {
            AuthService.logout();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
        
        HorizontalLayout header = new HorizontalLayout(toggle, title, userInfo, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM
        );
        
        addToNavbar(header);
    }

    private void createDrawer() {
        nav = new SideNav();
        
        Role role = AuthService.getCurrentRole();
        
        // Si no hay rol (no autenticado), no crear menú
        if (role == null) {
            return;
        }
        
        // Menú común
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        
        // Menú específico por rol
        switch (role) {
            case ADMIN -> createAdminMenu();
            case PROFESSOR -> createProfessorMenu();
            case STUDENT -> createStudentMenu();
            default -> {}
        }
        
        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        
        addToDrawer(scroller);
    }

    private void createAdminMenu() {
        SideNavItem usersMenu = new SideNavItem("Usuarios");
        usersMenu.setPrefixComponent(VaadinIcon.USERS.create());
        usersMenu.addItem(new SideNavItem("Crear Usuario", "admin/create-user", VaadinIcon.USER_CARD.create()));
        usersMenu.addItem(new SideNavItem("Listar Usuarios", "admin/list-users", VaadinIcon.LIST.create()));
        
        SideNavItem coursesMenu = new SideNavItem("Cursos");
        coursesMenu.setPrefixComponent(VaadinIcon.BOOK.create());
        coursesMenu.addItem(new SideNavItem("Crear Curso", "admin/create-course", VaadinIcon.PLUS.create()));
        coursesMenu.addItem(new SideNavItem("Listar Cursos", "admin/list-courses", VaadinIcon.LIST.create()));
        coursesMenu.addItem(new SideNavItem("Asignar Estudiantes", "admin/assign-students", VaadinIcon.GROUP.create()));
        
        nav.addItem(usersMenu);
        nav.addItem(coursesMenu);
    }

    private void createProfessorMenu() {
        nav.addItem(new SideNavItem("Mis Cursos", "professor/courses", VaadinIcon.BOOK.create()));
        nav.addItem(new SideNavItem("Calificar", "professor/grade", VaadinIcon.EDIT.create()));
    }

    private void createStudentMenu() {
        nav.addItem(new SideNavItem("Mis Cursos", "student/courses", VaadinIcon.BOOK.create()));
        nav.addItem(new SideNavItem("Mis Calificaciones", "student/grades", VaadinIcon.CHART.create()));
    }

    /**
     * IMPORTANTE: Verifica autenticación ANTES de renderizar cualquier vista
     * Si el usuario no está autenticado, redirige a LoginView
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar si el usuario está autenticado
        if (!AuthService.isAuthenticated() && !LoginView.class.equals(event.getNavigationTarget())) {
            // No autenticado y no es LoginView -> redirigir a login
            event.forwardTo(LoginView.class);
        }
    }
}

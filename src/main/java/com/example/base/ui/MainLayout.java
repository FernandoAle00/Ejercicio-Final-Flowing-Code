package com.example.base.ui;

import com.example.ejerciciofinal.security.AuthService;
import com.example.ejerciciofinal.views.LoginView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Layout
public final class MainLayout extends AppLayout implements BeforeEnterObserver {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()));
    }

    private Div createHeader() {
        var appLogo = VaadinIcon.ACADEMY_CAP.create();
        appLogo.addClassNames(TextColor.PRIMARY, IconSize.LARGE);

        var appName = new Span("Sistema Académico");
        appName.addClassNames(FontWeight.SEMIBOLD, FontSize.LARGE);

        var header = new Div(appLogo, appName);
        header.addClassNames(Display.FLEX, Padding.MEDIUM, Gap.MEDIUM, AlignItems.CENTER);
        return header;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(Margin.Horizontal.MEDIUM);
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        
        // Agregar opción de logout
        var logout = new SideNavItem("Cerrar Sesión", "logout", VaadinIcon.SIGN_OUT.create());
        logout.addClassNames(TextColor.ERROR);
        nav.addItem(logout);
        
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new com.vaadin.flow.component.icon.Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar autenticación antes de renderizar cualquier vista
        if (!AuthService.isAuthenticated() && !LoginView.class.equals(event.getNavigationTarget())) {
            event.forwardTo("login");
        }
    }
}

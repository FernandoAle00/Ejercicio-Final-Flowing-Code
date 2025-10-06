package com.example.ejerciciofinal.views;

import com.example.ejerciciofinal.security.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("logout")
@PageTitle("Logout | Sistema Académico")
public class LogoutView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        AuthService.logout();
        UI.getCurrent().getPage().setLocation("login");
    }
}

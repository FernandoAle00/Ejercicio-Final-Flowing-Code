package com.example;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("default")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configurePage(AppShellSettings settings) {
        // Configurar favicon usando el logo de Flowing Code
        settings.addFavIcon("icon", "img/logo-flowing-code.png", "192x192");
        settings.addLink("shortcut icon", "img/logo-flowing-code.png");
        
        // Configurar viewport y título inicial
        settings.setViewport("width=device-width, initial-scale=1");
        settings.setPageTitle("Sistema Académico");
    }

}

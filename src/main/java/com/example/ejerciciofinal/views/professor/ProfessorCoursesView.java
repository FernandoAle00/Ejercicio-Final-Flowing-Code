package com.example.ejerciciofinal.views.professor;

import com.example.ejerciciofinal.security.ProfessorOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Vista de ejemplo para profesores
 * Solo accesible por usuarios con rol PROFESSOR
 */
@ProfessorOnly
@Route(value = "professor/my-courses", layout = MainLayout.class)
@PageTitle("Mis Cursos | Profesor")
public class ProfessorCoursesView extends SecureView {

    public ProfessorCoursesView() {
        setSpacing(true);
        setPadding(true);
        setMaxWidth("1200px");

        H2 title = new H2("Mis Cursos - Vista de Profesor");
        
        Paragraph description = new Paragraph(
            "Esta vista solo es accesible por usuarios con rol PROFESSOR. " +
            "Aquí podrías mostrar los cursos que el profesor enseña, " +
            "estudiantes inscritos, calificaciones, etc."
        );

        add(title, description);
    }
}

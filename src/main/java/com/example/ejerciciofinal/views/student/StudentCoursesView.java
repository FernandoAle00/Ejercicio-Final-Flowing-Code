package com.example.ejerciciofinal.views.student;

import com.example.ejerciciofinal.security.StudentOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Vista de ejemplo para estudiantes
 * Solo accesible por usuarios con rol STUDENT
 */
@StudentOnly
@Route(value = "student/my-courses", layout = MainLayout.class)
@PageTitle("Mis Cursos | Estudiante")
public class StudentCoursesView extends SecureView {

    public StudentCoursesView() {
        setSpacing(true);
        setPadding(true);
        setMaxWidth("1200px");

        H2 title = new H2("Mis Cursos - Vista de Estudiante");
        
        Paragraph description = new Paragraph(
            "Esta vista solo es accesible por usuarios con rol STUDENT. " +
            "Aquí podrías mostrar los cursos en los que el estudiante está inscrito, " +
            "sus calificaciones, horarios, etc."
        );

        add(title, description);
    }
}

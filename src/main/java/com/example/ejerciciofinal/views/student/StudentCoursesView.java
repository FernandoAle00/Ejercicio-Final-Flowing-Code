package com.example.ejerciciofinal.views.student;

import java.util.List;

import com.example.ejerciciofinal.dtos.CourseDTO;
import com.example.ejerciciofinal.dtos.SeatDTO;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.security.AuthService;
import com.example.ejerciciofinal.security.StudentOnly;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

/**
 * Vista de ejemplo para estudiantes Solo accesible por usuarios con rol STUDENT
 */
@StudentOnly
@Route(value = "student/courses", layout = MainLayout.class)
@PageTitle("Mis Cursos | Estudiante")
public class StudentCoursesView extends SecureView {

    private final UserService userService;
    private final AuthService authService;
    private final CourseService courseService;

    public StudentCoursesView(UserService userService, AuthService authService, CourseService courseService) {

        this.userService = userService;
        this.authService = authService;
        this.courseService = courseService;

        setSpacing(false);
        setPadding(false);
        setSizeFull();
        
        // Contenedor principal con padding
        Div mainContainer = new Div();
        mainContainer.getStyle()
                .set("padding", "var(--lumo-space-l)")
                .set("max-width", "1400px")
                .set("margin", "0 auto")
                .set("width", "100%");

        H2 title = new H2("Mis Cursos");
        title.getStyle()
                .set("margin-top", "0")
                .set("margin-bottom", "var(--lumo-space-l)")
                .set("color", "var(--lumo-header-text-color)");

        Long userId = AuthService.getCurrentUserId();

        // Contenedor de cursos en grilla
        Div coursesGrid = new Div();
        coursesGrid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(350px, 1fr))")
                .set("gap", "var(--lumo-space-l)")
                .set("width", "100%");

        // Obtener el Student usando el método correcto que maneja el proxy de Hibernate
        Student student = userService.getStudentByUserId(userId);
        List<Long> courseIds = student.getSeats().stream()
                .map(seat -> seat.getCourse().getId())
                .distinct()
                .toList();

        if (courseIds.isEmpty()) {
            Div emptyState = createEmptyState();
            mainContainer.add(title, emptyState);
        } else {
            for (Long courseId : courseIds) {
                Div courseCard = createCourseCard(courseId, student);
                coursesGrid.add(courseCard);
            }
            mainContainer.add(title, coursesGrid);
        }

        add(mainContainer);
    }

    private Div createEmptyState() {
        Div emptyState = new Div();
        emptyState.getStyle()
                .set("text-align", "center")
                .set("padding", "var(--lumo-space-xl)")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-l)");
        
        Icon icon = new Icon(VaadinIcon.BOOK);
        icon.setSize("64px");
        icon.getStyle().set("color", "var(--lumo-contrast-40pct)");
        
        H3 emptyTitle = new H3("No estás inscrito en ningún curso");
        emptyTitle.getStyle().set("color", "var(--lumo-contrast-60pct)");
        
        Span emptyMessage = new Span("Contacta a un administrador para inscribirte en cursos");
        emptyMessage.getStyle().set("color", "var(--lumo-contrast-50pct)");
        
        emptyState.add(icon, emptyTitle, emptyMessage);
        return emptyState;
    }

    private Div createCourseCard(Long courseId, Student student) {
        CourseDTO course = courseService.getCourseById(courseId);
        
        // Encontrar el seat del estudiante en este curso
        SeatDTO studentSeat = course.getSeats().stream()
                .filter(seat -> seat.getStudentId() != null && seat.getStudentId().equals(student.getId()))
                .findFirst()
                .orElse(null);

        // Tarjeta principal del curso
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer")
                .set("height", "100%")
                .set("display", "flex")
                .set("flex-direction", "column");

        // Efecto hover
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                    .set("box-shadow", "var(--lumo-box-shadow-m)")
                    .set("transform", "translateY(-4px)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                    .set("box-shadow", "var(--lumo-box-shadow-s)")
                    .set("transform", "translateY(0)");
        });

        // Header del curso con color distintivo
        Div header = new Div();
        header.getStyle()
                .set("background", "linear-gradient(135deg, var(--lumo-primary-color) 0%, var(--lumo-primary-color-50pct) 100%)")
                .set("color", "var(--lumo-primary-contrast-color)")
                .set("padding", "var(--lumo-space-m)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("margin-bottom", "var(--lumo-space-m)");

        H3 courseTitle = new H3(course.getName());
        courseTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-l)")
                .set("font-weight", "600")
                .set("color", "inherit");

        header.add(courseTitle);

        // Información del profesor
        Div professorInfo = createInfoRow(VaadinIcon.USER, "Profesor", course.getProfessorName());

        // Información de la nota
        Div markInfo;
        if (studentSeat != null && studentSeat.getMark() != null) {
            String markText = String.format("%.2f", studentSeat.getMark());
            String markColor = getMarkColor(studentSeat.getMark());
            markInfo = createInfoRow(VaadinIcon.CLIPBOARD_TEXT, "Calificación", markText);
            
            // Colorear la nota según el valor
            Span markSpan = (Span) markInfo.getChildren()
                    .filter(component -> component instanceof Span)
                    .skip(1)
                    .findFirst()
                    .orElse(null);
            if (markSpan != null) {
                markSpan.getStyle()
                        .set("color", markColor)
                        .set("font-weight", "bold")
                        .set("font-size", "var(--lumo-font-size-l)");
            }
        } else {
            markInfo = createInfoRow(VaadinIcon.CLIPBOARD_TEXT, "Calificación", "Sin calificar");
            // Colorear "Sin calificar" en gris
            Span markSpan = (Span) markInfo.getChildren()
                    .filter(component -> component instanceof Span)
                    .skip(1)
                    .findFirst()
                    .orElse(null);
            if (markSpan != null) {
                markSpan.getStyle()
                        .set("color", "var(--lumo-contrast-50pct)")
                        .set("font-style", "italic");
            }
        }

        // Año de inscripción
        Div yearInfo = null;
        if (studentSeat != null && studentSeat.getYear() != null) {
            String yearText = String.valueOf(studentSeat.getYear().getYear());
            yearInfo = createInfoRow(VaadinIcon.CALENDAR, "Año de inscripción", yearText);
        }

        // Cantidad de estudiantes inscritos
        long enrolledCount = course.getSeats().stream()
                .filter(seat -> seat.getStudentId() != null)
                .count();
        Div studentsInfo = createInfoRow(VaadinIcon.GROUP, "Estudiantes inscritos", String.valueOf(enrolledCount));

        // Agregar todos los elementos a la tarjeta
        card.add(header, professorInfo, markInfo);
        if (yearInfo != null) {
            card.add(yearInfo);
        }
        card.add(studentsInfo);

        return card;
    }

    private Div createInfoRow(VaadinIcon iconType, String label, String value) {
        Div row = new Div();
        row.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-s)")
                .set("margin-bottom", "var(--lumo-space-s)")
                .set("padding", "var(--lumo-space-xs) 0");

        Icon icon = new Icon(iconType);
        icon.setSize("18px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");

        Span labelSpan = new Span(label + ":");
        labelSpan.getStyle()
                .set("font-weight", "500")
                .set("color", "var(--lumo-contrast-70pct)")
                .set("font-size", "var(--lumo-font-size-s)");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("color", "var(--lumo-body-text-color)")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("margin-left", "auto");

        row.add(icon, labelSpan, valueSpan);
        return row;
    }

    private String getMarkColor(Double mark) {
        if (mark >= 9.0) {
            return "var(--lumo-success-color)"; // Verde
        } else if (mark >= 7.0) {
            return "var(--lumo-primary-color)"; // Azul
        } else if (mark >= 6.0) {
            return "#FFA500"; // Naranja
        } else {
            return "var(--lumo-error-color)"; // Rojo
        }
    }
}

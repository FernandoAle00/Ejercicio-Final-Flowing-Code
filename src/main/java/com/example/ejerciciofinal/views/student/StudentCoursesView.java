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
import com.vaadin.flow.component.dependency.CssImport;
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
@CssImport("./styles/courses-view-styles.css")
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

        // Contenedor principal con padding y centrado
        Div mainContainer = new Div();
        mainContainer.addClassName("courses-main-container");

        Long userId = AuthService.getCurrentUserId();

        H2 title = new H2("Mis Cursos");
        title.addClassName("course-section-title");
        Div coursesGrid = new Div();
        coursesGrid.addClassName("courses-grid");

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
        emptyState.addClassName("empty-state");
        
        Icon icon = new Icon(VaadinIcon.BOOK);
        icon.setSize("64px");
        icon.addClassName("empty-state-icon");
        
        H3 emptyTitle = new H3("No estás inscrito en ningún curso");
        emptyTitle.addClassName("empty-state-text");
        
        Span emptyMessage = new Span("Contacta a un administrador para inscribirte en cursos");
        emptyMessage.addClassName("empty-state-text");
        
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

        Div card = new Div();
        card.addClassName("course-card");
        
        H3 courseTitle = new H3(course.getName());
        courseTitle.addClassName("course-card-title");

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

        card.add(courseTitle, professorInfo, markInfo);
        if (yearInfo != null) {
            card.add(yearInfo);
        }
        card.add(studentsInfo);

        return card;
    }

    private Div createInfoRow(VaadinIcon iconType, String label, String value) {
        Div row = new Div();
        row.addClassName("course-info-row");

        Span labelSpan = new Span(label + ":");
        labelSpan.addClassName("course-info-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("course-info-value");

        row.add(labelSpan, valueSpan);
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

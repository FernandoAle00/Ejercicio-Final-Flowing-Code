package com.example.ejerciciofinal.views.professor;

import java.util.List;

import com.example.ejerciciofinal.dtos.CourseDTO;
import com.example.ejerciciofinal.dtos.SeatDTO;
import com.example.ejerciciofinal.dtos.StudentSearchDTO;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.security.AuthService;
import com.example.ejerciciofinal.security.ProfessorOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Vista de ejemplo para profesores Solo accesible por usuarios con rol
 * PROFESSOR
 */
@ProfessorOnly
@Route(value = "professor/courses", layout = MainLayout.class)
@PageTitle("Mis Cursos | Profesor")
public class ProfessorCoursesView extends SecureView {

    private final UserService userService;
    private final AuthService authService;
    private final CourseService courseService;

    public ProfessorCoursesView(UserService userService, AuthService authService, CourseService courseService) {

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
                .set("grid-template-columns", "repeat(auto-fill, minmax(380px, 1fr))")
                .set("gap", "var(--lumo-space-l)")
                .set("width", "100%");

        // Obtener el Professor usando el método correcto que maneja el proxy de Hibernate
        Professor professor = userService.getProfessorByUserId(userId);
        
        // Ordenar cursos alfabéticamente por nombre
        List<Long> courseIds = professor.getCourses().stream()
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .map(course -> course.getId())
                .distinct()
                .toList();

        if (courseIds.isEmpty()) {
            Div emptyState = createEmptyState();
            mainContainer.add(title, emptyState);
        } else {
            for (Long courseId : courseIds) {
                Div courseCard = createCourseCard(courseId);
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

        H3 emptyTitle = new H3("No tienes ningún curso asignado para dictar");
        emptyTitle.getStyle().set("color", "var(--lumo-contrast-60pct)");

        Span emptyMessage = new Span("Los cursos asignados a tu cuenta aparecerán aquí.");
        emptyMessage.getStyle().set("color", "var(--lumo-contrast-50pct)");

        emptyState.add(icon, emptyTitle, emptyMessage);
        return emptyState;
    }

    private Div createCourseCard(Long courseId) {
        CourseDTO course = courseService.getCourseById(courseId);
        
        // Calcular estadísticas del curso
        long totalSeats = course.getSeats().size();
        long enrolledStudents = course.getSeats().stream()
                .filter(seat -> seat.getStudentId() != null)
                .count();
        long availableSeats = totalSeats - enrolledStudents;
        
        // Calcular promedio de notas del curso
        double averageMark = course.getSeats().stream()
                .filter(seat -> seat.getMark() != null)
                .mapToDouble(SeatDTO::getMark)
                .average()
                .orElse(0.0);
        
        long studentsWithGrades = course.getSeats().stream()
                .filter(seat -> seat.getMark() != null)
                .count();

        // Tarjeta principal del curso
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("transition", "all 0.3s ease")
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

        // Header del curso con gradiente verde para profesor
        Div header = new Div();
        header.getStyle()
                .set("background", "linear-gradient(135deg, #10b981 0%, #059669 100%)")
                .set("color", "white")
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

        // Sección de estadísticas
        Div statsSection = new Div();
        statsSection.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-s)")
                .set("margin-bottom", "var(--lumo-space-m)");

        // Estudiantes inscritos
        Div enrolledInfo = createInfoRow(VaadinIcon.USERS, "Estudiantes inscritos", 
                String.format("%d / %d", enrolledStudents, totalSeats));
        
        // Cupos disponibles
        Div availableInfo = createInfoRow(VaadinIcon.TICKET, "Cupos disponibles", 
                String.valueOf(availableSeats));
        
        // Colorear cupos según disponibilidad
        Span availableSpan = (Span) availableInfo.getChildren()
                .filter(component -> component instanceof Span)
                .skip(1)
                .findFirst()
                .orElse(null);
        if (availableSpan != null) {
            if (availableSeats == 0) {
                availableSpan.getStyle().set("color", "#ef4444").set("font-weight", "bold");
            } else if (availableSeats < 5) {
                availableSpan.getStyle().set("color", "#f59e0b").set("font-weight", "bold");
            } else {
                availableSpan.getStyle().set("color", "#10b981").set("font-weight", "bold");
            }
        }

        // Promedio del curso
        Div averageInfo;
        if (studentsWithGrades > 0) {
            averageInfo = createInfoRow(VaadinIcon.CHART, "Promedio del curso", 
                    String.format("%.2f", averageMark));
            Span avgSpan = (Span) averageInfo.getChildren()
                    .filter(component -> component instanceof Span)
                    .skip(1)
                    .findFirst()
                    .orElse(null);
            if (avgSpan != null) {
                avgSpan.getStyle()
                        .set("color", getMarkColor(averageMark))
                        .set("font-weight", "bold")
                        .set("font-size", "var(--lumo-font-size-l)");
            }
        } else {
            averageInfo = createInfoRow(VaadinIcon.CHART, "Promedio del curso", "Sin calificaciones");
            Span avgSpan = (Span) averageInfo.getChildren()
                    .filter(component -> component instanceof Span)
                    .skip(1)
                    .findFirst()
                    .orElse(null);
            if (avgSpan != null) {
                avgSpan.getStyle()
                        .set("color", "var(--lumo-contrast-50pct)")
                        .set("font-style", "italic");
            }
        }

        statsSection.add(enrolledInfo, availableInfo, averageInfo);

        // Sección de estudiantes inscritos (lista desplegable)
        Div studentsSection = new Div();
        studentsSection.getStyle()
                .set("margin-top", "var(--lumo-space-m)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("padding-top", "var(--lumo-space-m)");

        // Header de la sección con botón de inscribir
        HorizontalLayout studentsHeader = new HorizontalLayout();
        studentsHeader.setWidthFull();
        studentsHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        studentsHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        studentsHeader.getStyle().set("margin-bottom", "var(--lumo-space-s)");

        H3 studentsTitle = new H3("Estudiantes inscritos:");
        studentsTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("color", "var(--lumo-contrast-70pct)");

        Button enrollButton = new Button("Inscribir estudiante", new Icon(VaadinIcon.PLUS));
        enrollButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        enrollButton.addClickListener(e -> showEnrollStudentDialog(courseId));

        studentsHeader.add(studentsTitle, enrollButton);
        studentsSection.add(studentsHeader);

        if (enrolledStudents > 0) {
            Div studentsList = new Div();
            studentsList.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "var(--lumo-space-xs)")
                    .set("max-height", "200px")
                    .set("overflow-y", "auto")
                    .set("padding", "var(--lumo-space-xs)");

            course.getSeats().stream()
                    .filter(seat -> seat.getStudentId() != null)
                    .forEach(seat -> {
                        Div studentRow = createStudentRow(seat, courseId);
                        studentsList.add(studentRow);
                    });

            studentsSection.add(studentsList);
        } else {
            Span noStudents = new Span("No hay estudiantes inscritos aún");
            noStudents.getStyle()
                    .set("color", "var(--lumo-contrast-50pct)")
                    .set("font-style", "italic")
                    .set("text-align", "center")
                    .set("padding", "var(--lumo-space-m)");
            studentsSection.add(noStudents);
        }

        // Agregar todos los elementos a la tarjeta
        card.add(header, statsSection, studentsSection);

        return card;
    }

    private Div createStudentRow(SeatDTO seat, Long courseId) {
        Div row = new Div();
        row.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("padding", "var(--lumo-space-xs)")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-s)")
                .set("border-left", "3px solid var(--lumo-primary-color)");

        // Contenedor para nombre y nota
        Div infoContainer = new Div();
        infoContainer.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)")
                .set("flex", "1");

        // Nombre del estudiante
        Span studentName = new Span(seat.getStudentName() != null ? seat.getStudentName() : "Sin nombre");
        studentName.getStyle()
                .set("font-weight", "500")
                .set("flex", "1");

        // Nota del estudiante con ícono de editar
        HorizontalLayout gradeLayout = new HorizontalLayout();
        gradeLayout.setSpacing(false);
        gradeLayout.setPadding(false);
        gradeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        gradeLayout.getStyle().set("gap", "var(--lumo-space-xs)");
        
        Span gradeDisplay = new Span();
        if (seat.getMark() != null) {
            gradeDisplay.setText(String.format("%.2f", seat.getMark()));
            gradeDisplay.getStyle()
                    .set("color", getMarkColor(seat.getMark()))
                    .set("font-weight", "bold")
                    .set("padding", "2px 8px")
                    .set("border-radius", "var(--lumo-border-radius-s)")
                    .set("background", "var(--lumo-base-color)");
        } else {
            gradeDisplay.setText("Sin nota");
            gradeDisplay.getStyle()
                    .set("color", "var(--lumo-contrast-50pct)")
                    .set("font-style", "italic")
                    .set("font-size", "var(--lumo-font-size-s)");
        }
        
        // Botón para editar nota
        Button editGradeButton = new Button(new Icon(VaadinIcon.EDIT));
        editGradeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editGradeButton.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("padding", "0")
                .set("min-width", "auto");
        editGradeButton.addClickListener(e -> showEditGradeDialog(seat, courseId));
        
        gradeLayout.add(gradeDisplay, editGradeButton);
        infoContainer.add(studentName, gradeLayout);

        // Botón para desinscribir
        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        removeButton.getStyle().set("margin-left", "auto");
        removeButton.addClickListener(e -> showUnassignConfirmDialog(seat.getStudentId(), courseId, seat.getStudentName()));

        row.add(infoContainer, removeButton);
        return row;
    }

    private Div createInfoRow(VaadinIcon iconType, String label, String value) {
        Div row = new Div();
        row.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-s)");

        Icon icon = new Icon(iconType);
        icon.setSize("20px");
        icon.getStyle().set("color", "var(--lumo-contrast-60pct)");

        Span labelSpan = new Span(label + ":");
        labelSpan.getStyle()
                .set("color", "var(--lumo-contrast-60pct)")
                .set("font-size", "var(--lumo-font-size-s)");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("color", "var(--lumo-contrast-90pct)")
                .set("font-weight", "500")
                .set("margin-left", "auto");

        row.add(icon, labelSpan, valueSpan);
        return row;
    }

    private String getMarkColor(Double mark) {
        if (mark == null) return "var(--lumo-contrast-50pct)";
        if (mark >= 9.0) return "#10b981"; // Verde
        if (mark >= 7.0) return "#3b82f6"; // Azul
        if (mark >= 6.0) return "#f59e0b"; // Naranja
        return "#ef4444"; // Rojo
    }

    /**
     * Muestra un diálogo de confirmación para desinscribir a un estudiante
     */
    private void showUnassignConfirmDialog(Long studentId, Long courseId, String studentName) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Desinscribir estudiante");
        dialog.setText("¿Estás seguro de que deseas desinscribir a " + studentName + " de este curso? Se perderá su calificación si la tiene.");
        
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        
        dialog.setConfirmText("Desinscribir");
        dialog.setConfirmButtonTheme("error primary");
        
        dialog.addConfirmListener(e -> {
            try {
                courseService.unassignStudentFromCourse(studentId, courseId);
                showSuccessNotification("Estudiante desinscrito exitosamente");
                refreshView();
            } catch (Exception ex) {
                showErrorNotification("Error al desinscribir: " + ex.getMessage());
            }
        });
        
        dialog.open();
    }

    /**
     * Muestra un diálogo para inscribir un nuevo estudiante
     */
    private void showEnrollStudentDialog(Long courseId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Inscribir estudiante al curso");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setWidth("500px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        // ComboBox para buscar estudiante
        ComboBox<StudentSearchDTO> studentComboBox = new ComboBox<>("Buscar estudiante");
        studentComboBox.setWidthFull();
        studentComboBox.setPlaceholder("Buscar por ID, nombre o número de estudiante");
        studentComboBox.setItemLabelGenerator(StudentSearchDTO::getDisplayLabel);

        // Cargar todos los estudiantes
        List<StudentSearchDTO> allStudents = userService.getAllStudentsForSearch();
        
        // Configurar filtrado
        studentComboBox.setItems(query -> {
            String filter = query.getFilter().orElse("").toLowerCase();
            return allStudents.stream()
                    .filter(student -> student.matchesFilter(filter))
                    .skip(query.getOffset())
                    .limit(query.getLimit());
        });

        // Botones
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        
        Button enrollButton = new Button("Inscribir", e -> {
            StudentSearchDTO selectedStudent = studentComboBox.getValue();
            if (selectedStudent == null) {
                showErrorNotification("Debe seleccionar un estudiante");
                return;
            }
            
            try {
                // Verificar si puede inscribirse
                boolean canAssign = userService.canAssignStudentToCourse(selectedStudent.getId(), courseId);
                if (!canAssign) {
                    showErrorNotification("El estudiante ya está inscrito en este curso");
                    return;
                }
                
                // Inscribir al estudiante
                courseService.assignStudentToCourse(selectedStudent.getId(), courseId);
                showSuccessNotification("Estudiante inscrito exitosamente");
                dialog.close();
                refreshView();
            } catch (Exception ex) {
                showErrorNotification("Error al inscribir: " + ex.getMessage());
            }
        });
        enrollButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(cancelButton, enrollButton);
        dialogLayout.add(studentComboBox, buttonLayout);
        
        dialog.add(dialogLayout);
        dialog.open();
    }

    /**
     * Muestra un diálogo para editar la nota de un estudiante
     */
    private void showEditGradeDialog(SeatDTO seat, Long courseId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Asignar nota a " + seat.getStudentName());
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        // Campo para la nota
        NumberField gradeField = new NumberField("Nota");
        gradeField.setWidthFull();
        gradeField.setPlaceholder("Ingrese la nota (0-10)");
        gradeField.setMin(0.0);
        gradeField.setMax(10.0);
        gradeField.setStep(0.01);
        gradeField.setHelperText("La nota debe estar entre 0 y 10");
        
        // Establecer el valor actual si existe
        if (seat.getMark() != null) {
            gradeField.setValue(seat.getMark());
        }

        // Botones
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        
        Button saveButton = new Button("Guardar", e -> {
            Double newGrade = gradeField.getValue();
            
            if (newGrade == null) {
                showErrorNotification("Debe ingresar una nota válida");
                return;
            }
            
            if (newGrade < 0.0 || newGrade > 10.0) {
                showErrorNotification("La nota debe estar entre 0 y 10");
                return;
            }
            
            try {
                // Llamar al servicio para actualizar la nota
                // Esto también recalculará el promedio del estudiante
                courseService.setMarkToStudentInCourse(courseId, seat.getStudentId(), newGrade);
                showSuccessNotification("Nota asignada exitosamente");
                dialog.close();
                refreshView();
            } catch (Exception ex) {
                showErrorNotification("Error al asignar nota: " + ex.getMessage());
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(cancelButton, saveButton);
        dialogLayout.add(gradeField, buttonLayout);
        
        dialog.add(dialogLayout);
        dialog.open();
    }

    /**
     * Muestra una notificación de éxito
     */
    private void showSuccessNotification(String message) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    /**
     * Muestra una notificación de error
     */
    private void showErrorNotification(String message) {
        Notification notification = new Notification(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    /**
     * Refresca la vista completa
     */
    private void refreshView() {
        getUI().ifPresent(ui -> ui.getPage().reload());
    }
}

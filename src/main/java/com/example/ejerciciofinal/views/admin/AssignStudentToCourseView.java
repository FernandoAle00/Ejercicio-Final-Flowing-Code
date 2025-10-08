package com.example.ejerciciofinal.views.admin;

import java.util.List;

import com.example.ejerciciofinal.dtos.CourseDTO;
import com.example.ejerciciofinal.dtos.StudentSearchDTO;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.security.AdminOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@AdminOnly
@Route(value = "admin/assign-students", layout = MainLayout.class)
@PageTitle("Asignar Estudiante a Curso | Sistema Académico")
public class AssignStudentToCourseView extends SecureView {

    private final UserService userService;
    private final CourseService courseService;

    private final VerticalLayout studentSection = new VerticalLayout();
    private final ComboBox<StudentSearchDTO> studentComboBox = new ComboBox<>("Buscar Estudiante");
    private final Div studentDetailsSection = new Div(); // Este div contendrá los detalles del estudiante seleccionado
    private final Button assignButton = new Button("Guardar");
    
    private final Span canAssignSpan = new Span();

    private final ComboBox<CourseDTO> courseComboBox = new ComboBox<>("Curso");

    private ListDataProvider<StudentSearchDTO> studentDataProvider;
    
    // Variables para rastrear selecciones previas
    private CourseDTO previousCourse = null;
    private StudentSearchDTO previousStudent = null;

    public AssignStudentToCourseView(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");

        H2 title = new H2("Asignar Estudiante a Curso");

        add(title);

        // Configurar estilos del Span de validación para reducir espacio
        canAssignSpan.getStyle()
                .set("margin-top", "5px")
                .set("margin-bottom", "5px")
                .set("display", "block");

        // Combobox de cursos
        courseComboBox.setItems(courseService.getAllCourses());
        courseComboBox.setItemLabelGenerator(CourseDTO::getName);
        courseComboBox.setWidthFull();
        courseComboBox.setRequired(true);
        courseComboBox.setPlaceholder("Seleccione un curso");
        courseComboBox.addValueChangeListener(e -> updateForm(e.getValue()));

        add(courseComboBox, studentComboBox, assignButton, canAssignSpan, studentSection, studentDetailsSection);

        configureStudentComboBox();

        assignButton.setEnabled(false);
        assignButton.addClickListener(e -> {
            CourseDTO selectedCourse = courseComboBox.getValue();
            StudentSearchDTO selectedStudent = studentComboBox.getValue();
            if (selectedCourse != null && selectedStudent != null) {
                try {
                    courseService.assignStudentToCourse(selectedStudent.getId(), selectedCourse.getId());
                    canAssignSpan.setText("Estudiante asignado correctamente al curso.");
                    canAssignSpan.getStyle().set("color", "green").set("font-weight", "bold");
                    canAssignSpan.setVisible(true);
                    assignButton.setEnabled(false);
                    
                    // NO resetear previousCourse ni previousStudent aquí
                    // Esto permite mantener los detalles visibles después de asignar
                    
                } catch (IllegalArgumentException ex) {
                    canAssignSpan.setText("Error al asignar estudiante: " + ex.getMessage());
                    canAssignSpan.getStyle().set("color", "red").set("font-weight", "bold");
                    canAssignSpan.setVisible(true);
                    assignButton.setEnabled(false);
                }
            }});

    }

    private void updateForm(CourseDTO courseDTO) {
        // Solo limpiar si realmente cambió el curso (comparando IDs)
        boolean courseChanged = false;
        
        if (previousCourse == null && courseDTO != null) {
            courseChanged = true; // Primera selección
        } else if (previousCourse != null && courseDTO == null) {
            courseChanged = true; // Se limpió la selección
        } else if (previousCourse != null && courseDTO != null) {
            courseChanged = !previousCourse.getId().equals(courseDTO.getId()); // Comparar por ID
        }
        
        if (courseDTO != null) {
            studentSection.setVisible(true);
        } else {
            studentSection.setVisible(false);
        }
        
        // Solo limpiar campos si el curso realmente cambió
        if (courseChanged) {
            studentComboBox.clear();
            canAssignSpan.setText("");
            canAssignSpan.setVisible(false);
            studentDetailsSection.removeAll();
            studentDetailsSection.setVisible(false);
            assignButton.setEnabled(false);
        }
        
        // Actualizar el curso anterior
        previousCourse = courseDTO;
    }

    private void configureStudentComboBox() {
        // Combobox de estudiante, filtrable
        List<StudentSearchDTO> allStudents = userService.getAllStudentsForSearch();

        // Crear DataProvider con filtrado
        studentDataProvider = DataProvider.ofCollection(allStudents);

        studentComboBox.setItems(studentDataProvider);
        studentComboBox.setItemLabelGenerator(StudentSearchDTO::getDisplayLabel);
        studentComboBox.setWidthFull();
        studentComboBox.setRequired(true);
        studentComboBox.setPlaceholder("Buscar por ID, nombre o número de estudainte");

        // Habilitar filtrado en el combobox
        studentComboBox.setItems(query -> {
            String filter = query.getFilter().orElse("").toLowerCase();
            return allStudents.stream()
                    .filter(student -> student.matchesFilter(filter))
                    .skip(query.getOffset())
                    .limit(query.getLimit());
        });

        // Listener para cuando se seleccione un estudiante
        studentComboBox.addValueChangeListener(e -> {
            StudentSearchDTO selectedStudent = e.getValue();
            
            if (selectedStudent != null) {
                showStudentDetails(selectedStudent.getId());
                checkIfCanAssign(selectedStudent.getId(), courseComboBox.getValue().getId());
            } else {
                studentDetailsSection.removeAll();
                studentDetailsSection.setVisible(false);
            }
            
            // Actualizar estudiante anterior
            previousStudent = selectedStudent;
        });
    }

    private void showStudentDetails(Long studentId) {

        studentDetailsSection.removeAll();

        try {
            // Fetchear por el estudiante con todos sus datos
            Student student = userService.getStudentById(studentId);

            H3 detailsTitle = new H3("Detalles del Estudiante");
            detailsTitle.getStyle()
                    .set("margin-top", "0px")
                    .set("margin-bottom", "10px")
                    .set("color", "1976d2");

            // Crear layout. de detalles con estilos
            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.setPadding(true);
            detailsLayout.setSpacing(false);
            detailsLayout.getStyle()
                    .set("boder", "2px solid #e0e0e0")
                    .set("border-radius", "8px")
                    .set("background-color", "#f5f5f5");

            // Información del estudiante
            detailsLayout.add(createDetailRow("ID:", String.valueOf(student.getId())));
            detailsLayout.add(createDetailRow("Nombre completo:", student.getName()));
            detailsLayout.add(createDetailRow("Número de estudiante", student.getStudentNumber().toString()));
            detailsLayout.add(createDetailRow("Email:", student.getEmail()));
            detailsLayout.add(createDetailRow("Teléfono:", student.getPhone()));
            detailsLayout.add(createDetailRow("Promedio:", String.format("%.2f", student.getAvgMark())));
            detailsLayout.add(createDetailRow("Cursos inscritos:", String.valueOf(student.getSeats().size())));

            // Información de dirección (si existe)
            if (student.getAddress() != null) {
                Span addressTitle = new Span("Dirección:");
                addressTitle.getStyle().set("font-weight", "bold");
                detailsLayout.add(addressTitle);

                VerticalLayout addressLayout = new VerticalLayout();
                addressLayout.setPadding(false);
                addressLayout.setSpacing(false);
                addressLayout.getStyle().set("margin-left", "20px");

                addressLayout.add(new Span("  Calle: " + student.getAddress().getStreet()));
                addressLayout.add(new Span("  Ciudad: " + student.getAddress().getCity()));
                addressLayout.add(new Span("  Estado: " + student.getAddress().getState()));
                addressLayout.add(new Span("  País: " + student.getAddress().getCountry()));

                detailsLayout.add(addressLayout);
            }

            studentDetailsSection.add(detailsTitle, detailsLayout);
            studentDetailsSection.setVisible(true); // ← AGREGAR ESTA LÍNEA

        } catch (IllegalArgumentException e) {
            // Mostrar error si no se encuentra el estudiante
            Span errorMsg = new Span("❌ Error: " + e.getMessage());
            errorMsg.getStyle()
                    .set("color", "red")
                    .set("font-weight", "bold");
            studentDetailsSection.add(errorMsg);
            studentDetailsSection.setVisible(true); // ← AGREGAR ESTA LÍNEA TAMBIÉN
        }

    }

    /*
     * Crear una fila de detalle con label y valor
     */
    private HorizontalLayout createDetailRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        row.setAlignItems(Alignment.BASELINE);

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-weight", "bold")
                .set("min-width", "120px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("color", "#424242");

        row.add(labelSpan, valueSpan);
        return row;
    }

    private void checkIfCanAssign(Long studentId, Long courseId) {
        canAssignSpan.setText("");
        assignButton.setEnabled(false);

        CourseDTO selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null) {
            canAssignSpan.setText("Por favor, seleccione un curso primero.");
            canAssignSpan.getStyle().set("color", "red").set("font-weight", "bold");
            canAssignSpan.setVisible(true); // ← AGREGAR ESTA LÍNEA
            assignButton.setEnabled(false);
            return;
        }

        try {
            boolean canAssign = userService.canAssignStudentToCourse(studentId, courseId);
            if (canAssign) {
                canAssignSpan.setText("El estudiante puede ser asignado a este curso.");
                canAssignSpan.getStyle().set("color", "green").set("font-weight", "bold");
                canAssignSpan.setVisible(true); // ← AGREGAR ESTA LÍNEA
                assignButton.setEnabled(true);
            } else {
                canAssignSpan.setText("El estudiante NO puede ser asignado a este curso (cupo lleno o ya inscrito).");
                canAssignSpan.getStyle().set("color", "red").set("font-weight", "bold");
                canAssignSpan.setVisible(true); // ← AGREGAR ESTA LÍNEA
                assignButton.setEnabled(false);
            }
        } catch (IllegalArgumentException e) {
            canAssignSpan.setText("Error: " + e.getMessage());
            canAssignSpan.getStyle().set("color", "red").set("font-weight", "bold");
            canAssignSpan.setVisible(true); // ← AGREGAR ESTA LÍNEA
        }
    }
}

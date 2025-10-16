package com.example.ejerciciofinal.views.admin;

import com.example.ejerciciofinal.dtos.CourseDTO;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.security.AdminOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@AdminOnly
@Route(value = "admin/assign-mark", layout = MainLayout.class)
@PageTitle("Asignar Nota | Sistema Académico")
public class AssignMarkView extends SecureView {

    private final ComboBox<CourseDTO> courseComboBox = new ComboBox<>("Curso");
    private final ComboBox<Student> studentComboBox = new ComboBox<>("Estudiante");
    private final NumberField markField = new NumberField("Nota");
    private final Span yearOfSeat = new Span("Año de inscripción");
    private final Button saveButton = new Button("Guardar Nota");
    private final Span statusSpan = new Span();
    
    private final CourseService courseService;

    public AssignMarkView(CourseService courseService) {
        this.courseService = courseService;

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");
        setAlignItems(Alignment.STRETCH);

        // Título
        H2 title = new H2("Asignar Nota a Estudiante");
        add(title);

        // ComboBox de cursos
        courseComboBox.setItems(courseService.getAllCourses());
        courseComboBox.setItemLabelGenerator(CourseDTO::getName);
        courseComboBox.setWidthFull();
        courseComboBox.setRequired(true);
        courseComboBox.setPlaceholder("Seleccione un curso");
        courseComboBox.addValueChangeListener(e -> {
            updateStudentComboBox(e.getValue());
            clearForm();
        });

        // ComboBox de estudiantes (inicialmente vacío)
        studentComboBox.setWidthFull();
        studentComboBox.setPlaceholder("Primero seleccione un curso");
        studentComboBox.setEnabled(false);
        studentComboBox.setItemLabelGenerator(student -> 
            String.format("ID: %d - %s - #%s", 
                student.getId(), 
                student.getName(), 
                student.getStudentNumber().toString().substring(0, 8) + "..."
            )
        );
        studentComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                showStudentCurrentMark(e.getValue());
            }
        });

        // Campo de año de en que se tomó el curso, se toma desde Seat, no es editable
        yearOfSeat.setWidthFull();
        yearOfSeat.getStyle()
            .set("padding-top", "5px")
            .set("padding-bottom", "5px")
            .set("border-radius", "5px")
            .set("background-color", "#5da522ff")
            .set("color", "#ffffffff")
            .set("font-weight", "bold")
            .set("text-align", "center")
            .set("display", "none"); // Inicialmente oculto


        // Campo de nota
        markField.setWidthFull();
        markField.setMin(0.0);
        markField.setMax(10.0);
        markField.setStep(0.1);
        markField.setPlaceholder("Ingrese una nota entre 0 y 10");
        markField.setHelperText("Nota: valor entre 0.0 y 10.0");
        markField.setEnabled(false);

        // Span de estado
        statusSpan.getStyle()
            .set("margin-top", "10px")
            .set("padding", "10px")
            .set("border-radius", "5px")
            .set("display", "none"); // Inicialmente oculto

        // Botón guardar
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setWidthFull();
        saveButton.setEnabled(false);
        saveButton.addClickListener(e -> saveMarkToStudent());

        add(courseComboBox, studentComboBox, yearOfSeat, statusSpan, markField, saveButton);
    }

    /**
     * Actualiza el ComboBox de estudiantes con los estudiantes inscritos en el curso seleccionado
     */
    private void updateStudentComboBox(CourseDTO selectedCourse) {
        if (selectedCourse != null) {
            try {
                // Obtener estudiantes del curso de forma eficiente
                var students = courseService.getStudentsInCourse(selectedCourse.getId());
                
                if (students.isEmpty()) {
                    studentComboBox.setItems();
                    studentComboBox.setPlaceholder("No hay estudiantes inscritos en este curso");
                    studentComboBox.setEnabled(false);
                    markField.setEnabled(false);
                    saveButton.setEnabled(false);
                    statusSpan.getStyle().set("display", "none");
                } else {
                    studentComboBox.setItems(students);
                    studentComboBox.setPlaceholder("Seleccione un estudiante");
                    studentComboBox.setEnabled(true);
                }
            } catch (Exception ex) {
                Notification.show("Error al cargar estudiantes: " + ex.getMessage(), 
                    3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                studentComboBox.setEnabled(false);
            }
        } else {
            studentComboBox.clear();
            studentComboBox.setItems();
            studentComboBox.setPlaceholder("Primero seleccione un curso");
            studentComboBox.setEnabled(false);
            markField.setEnabled(false);
            saveButton.setEnabled(false);
        }
    }

    /**
     * Muestra la nota actual del estudiante en el curso seleccionado (si tiene)
     */
    private void showStudentCurrentMark(Student student) {
        CourseDTO selectedCourse = courseComboBox.getValue();
        if (selectedCourse != null && student != null) {
            // Buscar la nota actual del estudiante en este curso
            var currentSeat = student.getSeats().stream()
                .filter(seat -> seat.getCourse().getId().equals(selectedCourse.getId()))
                .findFirst();
            
            if (currentSeat.isPresent()) {
                Double currentMark = currentSeat.get().getMark();
                
                statusSpan.getStyle().set("display", "block");
                statusSpan.getStyle().set("background-color", "#e3f2fd");
                statusSpan.getStyle().set("color", "#1976d2");
                
                if (currentMark != null && currentMark > 0.0) {
                    statusSpan.setText(String.format("Nota actual: %.2f", currentMark));
                    markField.setValue(currentMark);
                } else {
                    statusSpan.setText("Este estudiante aún no tiene nota asignada");
                    markField.clear();
                }
                
                // Mostrar año de inscripción
                yearOfSeat.setText(String.format("Año de inscripción: %d", currentSeat.get().getYear().getYear()));
                yearOfSeat.getStyle().set("display", "block");
                
                markField.setEnabled(true);
                saveButton.setEnabled(true);
            }
        }
    }

    /**
     * Guarda la nota del estudiante en el curso
     */
    private void saveMarkToStudent() {
        CourseDTO selectedCourse = courseComboBox.getValue();
        Student selectedStudent = studentComboBox.getValue();
        Double mark = markField.getValue();

        // Validaciones
        if (selectedCourse == null || selectedStudent == null || mark == null) {
            Notification.show("Debe completar todos los campos", 
                3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (mark < 0.0 || mark > 10.0) {
            Notification.show("La nota debe estar entre 0.0 y 10.0", 
                3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            courseService.setMarkToStudentInCourse(
                selectedCourse.getId(), 
                selectedStudent.getId(), 
                mark
            );
            
            Notification.show(
                String.format("Nota %.2f asignada correctamente a %s en el curso %s", 
                    mark, selectedStudent.getName(), selectedCourse.getName()), 
                4000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            // Actualizar el span de estado
            statusSpan.setText(String.format("Nota guardada: %.2f", mark));
            statusSpan.getStyle().set("background-color", "#e8f5e9");
            statusSpan.getStyle().set("color", "#2e7d32");
            
        } catch (Exception ex) {
            Notification.show("Error al guardar la nota: " + ex.getMessage(), 
                4000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Limpia el formulario al cambiar de curso
     */
    private void clearForm() {
        studentComboBox.clear();
        markField.clear();
        markField.setEnabled(false);
        saveButton.setEnabled(false);
        statusSpan.getStyle().set("display", "none");
        yearOfSeat.getStyle().set("display", "none");
    }
}

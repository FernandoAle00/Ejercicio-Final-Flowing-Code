package com.example.ejerciciofinal.views.admin;

import com.example.ejerciciofinal.dtos.CourseDTO;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin/assign-students", layout = MainLayout.class)
@PageTitle("Asignar Estudiante a Curso | Sistema Académico")
public class AssignStudentToCourseView extends VerticalLayout{

    private final UserService userService;
    private final CourseService courseService;

    private final VerticalLayout studentSection = new VerticalLayout();
    private final TextField studentIdField = new TextField("Id del estudiante");
    
    private final ComboBox<CourseDTO> courseComboBox = new ComboBox<>("Curso");

    
    public AssignStudentToCourseView(UserService userService, CourseService courseService){
        this.userService = userService;
        this.courseService = courseService;

        setSpacing(true);
        setPadding(true);
        setMaxWidth("800px");

        H2 title = new H2("Asignar Estudiante a Curso");

        add(title);

        studentSection.setVisible(false);
        studentSection.add(studentIdField);

        
        studentIdField.setWidthFull();
        studentIdField.setRequired(true);
        studentIdField.setPlaceholder("Ingrese el ID del estudiante");
        
        courseComboBox.setItems(courseService.getAllCourses());
        courseComboBox.setItemLabelGenerator(CourseDTO::getName);
        courseComboBox.setWidthFull();
        courseComboBox.setRequired(true);
        courseComboBox.setPlaceholder("Seleccione un curso");

        add(courseComboBox, studentSection);
        // Listener para cuando se seleccione un curso
        courseComboBox.addValueChangeListener(e -> updateForm(e.getValue()));
    }

    private void updateForm(CourseDTO courseDTO){
        if(courseDTO != null){
            studentSection.setVisible(true);
        } else {
            studentSection.setVisible(false);
        }
    }

}

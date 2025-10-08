package com.example.ejerciciofinal.views.admin;

import org.springframework.data.domain.Page;

import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.model.Person;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Seat;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.security.AdminOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@AdminOnly
@Route(value = "admin/list-users", layout = MainLayout.class)
@PageTitle("Lista de Usuarios | Gestión de Usuarios")
public class ListUsersView extends SecureView {

    private final UserService userService;
    private final Grid<Person> grid = new Grid<>(Person.class, false);

    // Configuración de paginación, no estoy seguro que debería estar definido en esta clase
    private static final int PAGE_SIZE = 10; // 10 usuarios por página

    public ListUsersView(UserService userService) {
        this.userService = userService;

        // Solo ancho completo, altura automática según contenido
        setWidthFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Lista de Usuarios");
        add(title);

        // configurar grid
        configureGrid();

        // Configurar DataProvider con paginación
        configureDataProvider();

        add(grid);
    }

    private void configureGrid() {
        // Altura calculada: ~50px por fila + header + padding
        // 10 filas * 50px = 500px + 100px extra = 600px
        grid.setHeight("600px");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        // Definir columnas, ya que la autogeneración de columnas está deshabilitada
        // Columnas de Person, que aplica a todas las tuplas
        grid.addColumn(Person::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(Person::getName).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Person::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Person::getPhone).setHeader("Teléfono").setAutoWidth(true);

        // Columna que especifica el tipo de Person
        grid.addColumn(person -> {
            if (person instanceof Student) {
                return "Estudiante";
            } else if (person instanceof Professor) {
                return "Profesor";
            } else {
                return "Desconocido";
            }
        }).setHeader("Tipo").setAutoWidth(true);

        // Columna condicional, student number para estudiante, salario para profesor
        grid.addColumn(person -> {
            if (person instanceof Student student) {
                return student.getStudentNumber() != null ? student.getStudentNumber().toString() : "N/A";
            } else if (person instanceof Professor professor) {
                return "$" + String.format("%.2f", professor.getSalary());
            }
            return "-";
        }).setHeader("Nro. Estudiante / Salario").setAutoWidth(true);

        // Columna de address
        grid.addColumn(person -> {
            if (person.getAddress() != null) {
                return person.getAddress().getCity() + ", " + person.getAddress().getCountry();
            }
            return "Sin dirección";
        }).setHeader("Ubicación").setAutoWidth(true);

        // Listener de doble click para ver detalles de usuario
        grid.addItemDoubleClickListener(event -> {
            Person selectedPerson = event.getItem();
            showPersonsDetailsDialog(selectedPerson);
        });
    }

    private void configureDataProvider() {
        // Dataprovider con lazy loading y paginación
        CallbackDataProvider<Person, Void> dataProvider = DataProvider.fromCallbacks(
                query -> {
                    int pageIndex = query.getPage();
                    int pageSize = query.getPageSize();

                    Page<Person> page = userService.findAllPersonsPaginated(pageIndex, pageSize);
                    return page.getContent().stream();
                }, query -> {
                    return (int) userService.countPersons();
                });

        grid.setDataProvider(dataProvider);
        grid.setPageSize(PAGE_SIZE);
    }

    /*
     * Muestra un dialog con todos los detalles de la persona seleccionada
     * Para estudiantes: muestra cursos inscritos con sus notas y promedio
     * Para profesores: muestra salario y cursos que dicta
     */
    private void showPersonsDetailsDialog(Person person) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setCloseOnOutsideClick(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        // Título según tipo de Person
        H2 title = new H2(person instanceof Student ? "Detalles del Estudiante" : "Detalles del Profesor");
        layout.add(title);

        // Información básica de Person
        H3 basicInfoTitle = new H3("Información Personal");
        basicInfoTitle.getStyle().set("margin-top", "5px").set("margin-bottom", "10px");
        layout.add(basicInfoTitle);

        Div infoContainer = new Div();
        infoContainer.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "8px");

        addDetailRow(infoContainer, "ID:", String.valueOf(person.getId()));
        addDetailRow(infoContainer, "Nombre:", person.getName());
        addDetailRow(infoContainer, "Email:", person.getEmail());
        addDetailRow(infoContainer, "Teléfono:", person.getPhone());

        // Dirección
        if (person.getAddress() != null) {
            String fullAddress = String.format("%s, %s, %s, %s",
                    person.getAddress().getStreet(),
                    person.getAddress().getCity(),
                    person.getAddress().getState(),
                    person.getAddress().getCountry()
            );
            addDetailRow(infoContainer, "Dirección:", fullAddress);
        } else {
            addDetailRow(infoContainer, "Dirección:", "Sin dirección registrada");
        }

        layout.add(infoContainer);

        // Detalles específicos según tipo de Person
        if (person instanceof Student) { // Es necesario fetchear luego por los tipos porque Person no tiene los campos de Student/Professor
            Student student = userService.getStudentById(person.getId());
            addStudentSpecificDetails(layout, student);
        } else if (person instanceof Professor) {
            Professor professor = userService.getProfessorById(person.getId());
            addProfessorSpecificDetails(layout, professor);
        }

        dialog.add(layout);
        dialog.open();
    }

    /*
     * Agrega una fila de detalle con etiqueta y valor
     */
    private void addDetailRow(Div container, String label, String value) {
        Div row = new Div();
        row.getStyle().set("display", "flex").set("gap", "10px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-weight", "bold").set("min-width", "120px");

        Span valueSpan = new Span(value != null ? value : "N/A");

        row.add(labelSpan, valueSpan);
        container.add(row);
    }

    /*
     * Agrega detalles específicos para estudiantes
     */
    private void addStudentSpecificDetails(VerticalLayout layout, Student student) {

        H3 studentInfoTitle = new H3("Detalles del Estudiante");
        studentInfoTitle.getStyle().set("margin-top", "15px").set("margin-bottom", "10px");
        layout.add(studentInfoTitle);

        Div studentInfoContainer = new Div();
        studentInfoContainer.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "8px");

        addDetailRow(studentInfoContainer, "Nro. Estudiante:", student.getStudentNumber().toString());
        addDetailRow(studentInfoContainer, "Promedio General:", String.format("%.2f", student.getAvgMark()));
        addDetailRow(studentInfoContainer, "Cursos Inscritos:", String.valueOf(student.getSeats().size()));

        layout.add(studentInfoContainer);

        // Detalles de cursos si tiene
        if (!student.getSeats().isEmpty()) {
            H3 coursesTitle = new H3("Cursos Inscritos");
            coursesTitle.getStyle().set("margin-top", "15px").set("margin-bottom", "10px");
            layout.add(coursesTitle);

            Div coursesContainer = new Div();
            coursesContainer.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "12px")
                    .set("padding", "10px")
                    .set("background-color", "#f5f5f5")
                    .set("border-radius", "5px");

            for (Seat seat : student.getSeats()) {
                Div courseBox = new Div();
                courseBox.getStyle()
                        .set("background-color", "white")
                        .set("padding", "10px")
                        .set("border-radius", "5px")
                        .set("border", "1px solid #ddd");

                Div courseDetails = new Div();
                courseDetails.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "5px");

                addDetailRow(courseDetails, "Curso:", seat.getCourse().getName());
                addDetailRow(courseDetails, "Año:", seat.getYear().toString());
                addDetailRow(courseDetails, "Nota:", String.format("%.2f", seat.getMark()));

                courseBox.add(courseDetails);
                coursesContainer.add(courseBox);
            }
            layout.add(coursesContainer);
        }
    }

    /*
     * Agrega detalles específicos para profesores
     */
    private void addProfessorSpecificDetails(VerticalLayout layout, Professor professor) {

        H3 professorInfoTitle = new H3("Información Profesional");
        professorInfoTitle.getStyle().set("margin-top", "15px").set("margin-bottom", "10px");
        layout.add(professorInfoTitle);

        Div professorInfoContainer = new Div();
        professorInfoContainer.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "8px");

        addDetailRow(professorInfoContainer, "Salario:", String.format("$%.2f", professor.getSalary()));
        addDetailRow(professorInfoContainer, "Cursos Dictados:", String.valueOf(professor.getCourses().size()));

        layout.add(professorInfoContainer);

        // Detalles de cursos si tiene
        if (!professor.getCourses().isEmpty()) {
            H3 coursesTitle = new H3("Cursos que Dicta");
            coursesTitle.getStyle().set("margin-top", "15px").set("margin-bottom", "10px");
            layout.add(coursesTitle);

            Div coursesContainer = new Div();
            coursesContainer.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "12px")
                    .set("padding", "10px")
                    .set("background-color", "#f5f5f5")
                    .set("border-radius", "5px");

            for (Course course : professor.getCourses()) {
                Div courseBox = new Div();
                courseBox.getStyle()
                        .set("background-color", "white")
                        .set("padding", "10px")
                        .set("border-radius", "5px")
                        .set("border", "1px solid #ddd");

                Div courseDetails = new Div();
                courseDetails.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "5px");

                int totalSeats = course.getSeats().size();
                long occupiedSeats = course.getSeats().stream()
                        .filter(seat -> seat.getStudent() != null)
                        .count();

                addDetailRow(courseDetails, "Curso:", course.getName());
                addDetailRow(courseDetails, "Cupos Totales:", String.valueOf(totalSeats));
                addDetailRow(courseDetails, "Cupos Ocupados:", String.valueOf(occupiedSeats));
                addDetailRow(courseDetails, "Cupos Disponibles:", String.valueOf(totalSeats - occupiedSeats));

                courseBox.add(courseDetails);
                coursesContainer.add(courseBox);
            }

            layout.add(coursesContainer);
        }
    }
}

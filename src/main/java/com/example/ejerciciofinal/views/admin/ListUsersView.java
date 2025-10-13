package com.example.ejerciciofinal.views.admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.flowingcode.vaadin.addons.gridexporter.GridExporter;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
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

    // Referencias a columnas para el exporter
    private Column<Person> idColumn;
    private Column<Person> nameColumn;
    private Column<Person> emailColumn;
    private Column<Person> phoneColumn;
    private Column<Person> typeColumn;
    private Column<Person> numberSalaryColumn;
    private Column<Person> locationColumn;

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

        // Configurar GridExporter DESPUÉS del grid
        configureGridExporter();

        add(grid);
    }

    private void configureGrid() {
        // Altura calculada: ~50px por fila + header + padding
        // 10 filas * 50px = 500px + 100px extra = 600px
        grid.setHeight("600px");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        // Definir columnas y guardar referencias para el exporter
        idColumn = grid.addColumn(Person::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        nameColumn = grid.addColumn(Person::getName).setHeader("Nombre").setAutoWidth(true);
        emailColumn = grid.addColumn(Person::getEmail).setHeader("Email").setAutoWidth(true);
        phoneColumn = grid.addColumn(Person::getPhone).setHeader("Teléfono").setAutoWidth(true);

        // Columna que especifica el tipo de Person
        typeColumn = grid.addColumn(person -> {
            if (person instanceof Student) {
                return "Estudiante";
            } else if (person instanceof Professor) {
                return "Profesor";
            } else {
                return "Desconocido";
            }
        }).setHeader("Tipo").setAutoWidth(true);

        // Columna condicional, student number para estudiante, salario para profesor
        numberSalaryColumn = grid.addColumn(person -> {
            if (person instanceof Student student) {
                return student.getStudentNumber() != null ? student.getStudentNumber().toString() : "N/A";
            } else if (person instanceof Professor professor) {
                return "$" + String.format("%.2f", professor.getSalary());
            }
            return "-";
        }).setHeader("Nro. Estudiante / Salario").setAutoWidth(true);

        // Columna de address
        locationColumn = grid.addColumn(person -> {
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

    /**
     * Configura el GridExporter para permitir exportar los datos a Excel, CSV y PDF
     */
    private void configureGridExporter() {
        GridExporter<Person> exporter = GridExporter.createFor(grid);
        
        // Configurar título del documento exportado
        exporter.setTitle("Listado de Usuarios del Sistema");
        
        // Nombre del archivo con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        exporter.setFileName("Usuarios_" + timestamp);
        
        // Configurar valores de exportación para columnas especiales
        // Para Student/Professor, necesitamos manejar el polimorfismo correctamente
        
        // Columna de Tipo
        exporter.setExportValue(typeColumn, person -> {
            if (person instanceof Student) {
                return "Estudiante";
            } else if (person instanceof Professor) {
                return "Profesor";
            }
            return "Desconocido";
        });
        
        // Columna de Nro. Estudiante / Salario
        exporter.setExportValue(numberSalaryColumn, person -> {
            if (person instanceof Student student) {
                return student.getStudentNumber() != null ? 
                    "Nro: " + student.getStudentNumber().toString() : "N/A";
            } else if (person instanceof Professor professor) {
                return String.format("Salario: $%.2f", professor.getSalary());
            }
            return "-";
        });
        
        // Columna de Ubicación
        exporter.setExportValue(locationColumn, person -> {
            if (person.getAddress() != null) {
                return person.getAddress().getCity() + ", " + 
                       person.getAddress().getState() + ", " + 
                       person.getAddress().getCountry();
            }
            return "Sin dirección";
        });
        
        // Configurar auto-ajuste de columnas
        exporter.setAutoSizeColumns(true);
        
        // Configurar charset para CSV (importante para caracteres especiales en español)
        exporter.setCsvCharset(() -> java.nio.charset.StandardCharsets.UTF_8);
        
        // El exporter agrega automáticamente los botones de exportación al footer del grid
    }

    /*
     * Muestra un dialog con todos los detalles de la persona seleccionada
     * Para estudiantes: muestra cursos inscritos con sus notas y promedio
     * Para profesores: muestra salario y cursos que dicta
     */
    private void showPersonsDetailsDialog(Person person) {
        Dialog dialog = new Dialog();
        dialog.setWidth("800px");
        dialog.setMaxHeight("90vh");
        dialog.setCloseOnOutsideClick(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle()
            .set("overflow-y", "auto")
            .set("max-height", "85vh");

        // Título según tipo de Person
        H2 title = new H2(person instanceof Student ? "Detalles del Estudiante" : "Detalles del Profesor");
        title.getStyle()
            .set("margin-top", "0")
            .set("margin-bottom", "20px")
            .set("color", "#1a1a1a")
            .set("font-weight", "500")
            .set("border-bottom", "2px solid #e0e0e0")
            .set("padding-bottom", "10px");
        layout.add(title);

        // Información básica de Person
        H3 basicInfoTitle = new H3("Información Personal");
        basicInfoTitle.getStyle()
            .set("margin-top", "0")
            .set("margin-bottom", "12px")
            .set("color", "#424242")
            .set("font-size", "1.1rem")
            .set("font-weight", "500");
        layout.add(basicInfoTitle);

        Div infoContainer = new Div();
        infoContainer.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "10px")
            .set("padding", "16px")
            .set("background-color", "#fafafa")
            .set("border-radius", "8px")
            .set("border", "1px solid #e0e0e0");

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
        row.getStyle()
            .set("display", "flex")
            .set("gap", "12px")
            .set("align-items", "baseline");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
            .set("font-weight", "500")
            .set("min-width", "140px")
            .set("color", "#616161")
            .set("font-size", "0.9rem");

        Span valueSpan = new Span(value != null ? value : "N/A");
        valueSpan.getStyle()
            .set("color", "#212121")
            .set("font-size", "0.95rem");

        row.add(labelSpan, valueSpan);
        container.add(row);
    }

    /*
     * Agrega detalles específicos para estudiantes
     */
    private void addStudentSpecificDetails(VerticalLayout layout, Student student) {

        H3 studentInfoTitle = new H3("Detalles del Estudiante");
        studentInfoTitle.getStyle()
            .set("margin-top", "24px")
            .set("margin-bottom", "12px")
            .set("color", "#424242")
            .set("font-size", "1.1rem")
            .set("font-weight", "500");
        layout.add(studentInfoTitle);

        Div studentInfoContainer = new Div();
        studentInfoContainer.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "10px")
            .set("padding", "16px")
            .set("background-color", "#fafafa")
            .set("border-radius", "8px")
            .set("border", "1px solid #e0e0e0");

        addDetailRow(studentInfoContainer, "Nro. Estudiante:", student.getStudentNumber().toString());
        addDetailRow(studentInfoContainer, "Promedio General:", String.format("%.2f", student.getAvgMark()));
        addDetailRow(studentInfoContainer, "Cursos Inscritos:", String.valueOf(student.getSeats().size()));

        layout.add(studentInfoContainer);

        // Detalles de cursos si tiene
        if (!student.getSeats().isEmpty()) {
            H3 coursesTitle = new H3("Cursos Inscritos");
            coursesTitle.getStyle()
                .set("margin-top", "24px")
                .set("margin-bottom", "16px")
                .set("color", "#424242")
                .set("font-size", "1.1rem")
                .set("font-weight", "500");
            layout.add(coursesTitle);

            // Grid container con 2 columnas
            Div coursesContainer = new Div();
            coursesContainer.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "repeat(2, 1fr)")
                    .set("gap", "16px")
                    .set("margin-bottom", "16px");

            for (Seat seat : student.getSeats()) {
                Div courseBox = new Div();
                courseBox.getStyle()
                        .set("background-color", "#ffffff")
                        .set("padding", "16px")
                        .set("border-radius", "8px")
                        .set("border", "1px solid #e0e0e0")
                        .set("transition", "box-shadow 0.2s ease")
                        .set("cursor", "default");

                // Título del curso
                Span courseTitle = new Span(seat.getCourse().getName());
                courseTitle.getStyle()
                    .set("display", "block")
                    .set("font-weight", "500")
                    .set("font-size", "1rem")
                    .set("color", "#1a1a1a")
                    .set("margin-bottom", "12px")
                    .set("padding-bottom", "8px")
                    .set("border-bottom", "1px solid #f0f0f0");

                // Información del curso
                Div courseInfo = new Div();
                courseInfo.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "6px");

                addCourseInfoRow(courseInfo, "Año", String.format("%d", seat.getYear().getYear()));
                addCourseInfoRow(courseInfo, "Nota", String.format("%.2f", seat.getMark()));

                courseBox.add(courseTitle, courseInfo);
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
        professorInfoTitle.getStyle()
            .set("margin-top", "24px")
            .set("margin-bottom", "12px")
            .set("color", "#424242")
            .set("font-size", "1.1rem")
            .set("font-weight", "500");
        layout.add(professorInfoTitle);

        Div professorInfoContainer = new Div();
        professorInfoContainer.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "10px")
            .set("padding", "16px")
            .set("background-color", "#fafafa")
            .set("border-radius", "8px")
            .set("border", "1px solid #e0e0e0");

        addDetailRow(professorInfoContainer, "Salario:", String.format("$%.2f", professor.getSalary()));
        addDetailRow(professorInfoContainer, "Cursos Dictados:", String.valueOf(professor.getCourses().size()));

        layout.add(professorInfoContainer);

        // Detalles de cursos si tiene
        if (!professor.getCourses().isEmpty()) {
            H3 coursesTitle = new H3("Cursos que Dicta");
            coursesTitle.getStyle()
                .set("margin-top", "24px")
                .set("margin-bottom", "16px")
                .set("color", "#424242")
                .set("font-size", "1.1rem")
                .set("font-weight", "500");
            layout.add(coursesTitle);

            // Grid container con 2 columnas
            Div coursesContainer = new Div();
            coursesContainer.getStyle()
                    .set("display", "grid")
                    .set("grid-template-columns", "repeat(2, 1fr)")
                    .set("gap", "16px")
                    .set("margin-bottom", "16px");

            for (Course course : professor.getCourses()) {
                Div courseBox = new Div();
                courseBox.getStyle()
                        .set("background-color", "#ffffff")
                        .set("padding", "16px")
                        .set("border-radius", "8px")
                        .set("border", "1px solid #e0e0e0")
                        .set("transition", "box-shadow 0.2s ease")
                        .set("cursor", "default");

                int totalSeats = course.getSeats().size();
                long occupiedSeats = course.getSeats().stream()
                        .filter(seat -> seat.getStudent() != null)
                        .count();
                int availableSeats = totalSeats - (int) occupiedSeats;

                // Título del curso
                Span courseTitle = new Span(course.getName());
                courseTitle.getStyle()
                    .set("display", "block")
                    .set("font-weight", "500")
                    .set("font-size", "1rem")
                    .set("color", "#1a1a1a")
                    .set("margin-bottom", "12px")
                    .set("padding-bottom", "8px")
                    .set("border-bottom", "1px solid #f0f0f0");

                // Información del curso
                Div courseInfo = new Div();
                courseInfo.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "6px");

                addCourseInfoRow(courseInfo, "Cupos Totales", String.valueOf(totalSeats));
                addCourseInfoRow(courseInfo, "Ocupados", String.valueOf(occupiedSeats));
                addCourseInfoRow(courseInfo, "Disponibles", String.valueOf(availableSeats));

                courseBox.add(courseTitle, courseInfo);
                coursesContainer.add(courseBox);
            }

            layout.add(coursesContainer);
        }
    }

    /*
     * Agrega una fila de información para los cursos (más compacta)
     */
    private void addCourseInfoRow(Div container, String label, String value) {
        Div row = new Div();
        row.getStyle()
            .set("display", "flex")
            .set("justify-content", "space-between")
            .set("align-items", "center");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "#757575");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "#212121")
            .set("font-weight", "500");

        row.add(labelSpan, valueSpan);
        container.add(row);
    }
}

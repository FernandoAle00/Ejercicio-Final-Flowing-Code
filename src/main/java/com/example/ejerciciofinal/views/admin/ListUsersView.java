package com.example.ejerciciofinal.views.admin;

import org.springframework.data.domain.Page;

import com.example.ejerciciofinal.model.Person;
import com.example.ejerciciofinal.model.Professor;
import com.example.ejerciciofinal.model.Student;
import com.example.ejerciciofinal.services.UserService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin/list-users", layout = MainLayout.class)
@PageTitle("Lista de Usuarios | Gestión de Usuarios")
public class ListUsersView extends VerticalLayout {

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
}

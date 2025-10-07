package com.example.ejerciciofinal.views.admin;

import org.springframework.data.domain.Page;

import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin/list-courses", layout = MainLayout.class)
@PageTitle("Listado de Cursos | Sistema Académico")
public class ListCoursesView extends VerticalLayout {

    private final CourseService courseService;
    private final Grid<Course> grid = new Grid<>(Course.class, false);

    private static final int PAGE_SIZE = 10; // 10 cursos por página

    public ListCoursesView(CourseService courseService) {

        this.courseService = courseService;

        // Solo ancho completo, altura automática según contenido
        setWidthFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Lista de Cursos");
        add(title);

        // configurar grid
        configureGrid();

        // Configurar DataProvider con paginación
        configureDataProvider();

        add(grid);
    }

    private void configureGrid() {
        grid.setHeight("600px");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        // Definir columnas, ya que la autogeneración de columnas está deshabilitada
        grid.addColumn(Course::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(Course::getName).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(course -> course.getProfessor() != null ? course.getProfessor().getName() : "N/A")
                .setHeader("Profesor")
                .setAutoWidth(true);
        grid.addColumn(course -> course.getSeats() != null ? course.getSeats().size() : 0)
                .setHeader("Cantidad de Cupos")
                .setAutoWidth(true);

    }

    private void configureDataProvider() {
        // DataProvider con lazy loading y paginación
        CallbackDataProvider<Course, Void> dataProvider = DataProvider.fromCallbacks(
                query -> {
                    int pageIndex = query.getPage();
                    int pageSize = query.getPageSize();

                    Page<Course> coursePage = courseService.findAllCoursesPaginated(pageIndex, pageSize);
                    return coursePage.getContent().stream();
                },
                query -> {
                    return (int) courseService.countCourses();
                }
        );

        grid.setDataProvider(dataProvider);
        grid.setPageSize(PAGE_SIZE);
        
    }
}

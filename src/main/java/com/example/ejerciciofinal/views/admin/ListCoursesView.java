package com.example.ejerciciofinal.views.admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;

import com.example.ejerciciofinal.model.Course;
import com.example.ejerciciofinal.security.AdminOnly;
import com.example.ejerciciofinal.security.SecureView;
import com.example.ejerciciofinal.services.CourseService;
import com.example.ejerciciofinal.views.MainLayout;
import com.flowingcode.vaadin.addons.gridexporter.GridExporter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@AdminOnly
@Route(value = "admin/list-courses", layout = MainLayout.class)
@PageTitle("Listado de Cursos | Sistema Académico")
public class ListCoursesView extends SecureView {

    private final CourseService courseService;
    private final Grid<Course> grid = new Grid<>(Course.class, false);

    // Referencias a columnas para el exporter
    private Column<Course> idColumn;
    private Column<Course> nameColumn;
    private Column<Course> professorColumn;
    private Column<Course> seatsColumn;

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

        // Configurar GridExporter DESPUÉS del grid
        configureGridExporter();

        add(grid);
    }

    private void configureGrid() {
        grid.setHeight("600px");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        // Definir columnas y guardar referencias para el exporter
        idColumn = grid.addColumn(Course::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        nameColumn = grid.addColumn(Course::getName).setHeader("Nombre").setAutoWidth(true);
        professorColumn = grid.addColumn(course -> course.getProfessor() != null ? course.getProfessor().getName() : "N/A")
                .setHeader("Profesor")
                .setAutoWidth(true);
        seatsColumn = grid.addColumn(course -> course.getSeats() != null ? course.getSeats().size() : 0)
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

    /**
     * Configura el GridExporter para permitir exportar los datos a Excel, CSV y PDF
     */
    private void configureGridExporter() {
        GridExporter<Course> exporter = GridExporter.createFor(grid);
        
        // Configurar título del documento exportado
        exporter.setTitle("Listado de Cursos del Sistema");
        
        // Nombre del archivo con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        exporter.setFileName("Cursos_" + timestamp);
        
        // Configurar valores de exportación para columnas especiales
        
        // Columna de Profesor - asegurar que siempre hay un valor
        exporter.setExportValue(professorColumn, course -> {
            if (course.getProfessor() != null) {
                return course.getProfessor().getName();
            }
            return "Sin profesor asignado";
        });
        
        // Columna de Cantidad de Cupos
        exporter.setExportValue(seatsColumn, course -> {
            if (course.getSeats() != null) {
                int totalSeats = course.getSeats().size();
                long occupiedSeats = course.getSeats().stream()
                    .filter(seat -> seat.getStudent() != null)
                    .count();
                return String.format("Total: %d | Ocupados: %d | Disponibles: %d", 
                    totalSeats, occupiedSeats, (totalSeats - occupiedSeats));
            }
            return "0";
        });
        
        // Configurar auto-ajuste de columnas
        exporter.setAutoSizeColumns(true);
        
        // Configurar charset para CSV (importante para caracteres especiales en español)
        exporter.setCsvCharset(() -> java.nio.charset.StandardCharsets.UTF_8);
        
        // El exporter agrega automáticamente los botones de exportación al footer del grid
    }
}

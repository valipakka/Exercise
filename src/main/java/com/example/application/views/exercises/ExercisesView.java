
package com.example.application.views.exercises;

import com.example.application.data.Exercises;
import com.example.application.services.ExercisesService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Route(value = "exercises", layout = MainLayout.class)
@PageTitle("Exercises")
public class ExercisesView extends Div implements BeforeEnterObserver {

    private final Grid<Exercises> grid = new Grid<>(Exercises.class, false);
    private final ExercisesService exercisesService;
    private ListDataProvider<Exercises> provider;

    // Suodatinarvot
    private String typeFilterValue = "";
    private String distanceFilterValue = "";
    private String durationFilterValue = "";
    private String notesFilterValue = "";
    private String dateFilterValue = "";

    // Lomake ja binder
    private Exercises exercises;
    private BeanValidationBinder<Exercises> binder;
    private DateTimePicker startTime;
    private DateTimePicker endTime;
    private TextField type;
    private TextField distance;
    private TextField notes;
    private final Button cancel = new Button("Cancel");
    private final Button save   = new Button("Save");

    public ExercisesView(ExercisesService exercisesService) {
        this.exercisesService = exercisesService;
        addClassName("exercises-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(grid);
        updateList();
    }

    private void configureGrid() {
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.setSizeFull();

        grid.addColumn(Exercises::getType)
                .setHeader("Type")
                .setKey("type");
        grid.addColumn(Exercises::getDistance)
                .setHeader("Distance")
                .setKey("distance");
        grid.addColumn(e -> {
                    LocalDateTime s = e.getStartTime();
                    LocalDateTime e2 = e.getEndTime();
                    return (s != null && e2 != null)
                            ? Duration.between(s, e2).toMinutes()
                            : null;
                })
                .setHeader("Duration")
                .setKey("duration");
        grid.addColumn(Exercises::getNotes)
                .setHeader("Notes")
                .setKey("notes");
        grid.addColumn(e -> {
                    LocalDateTime s = e.getStartTime();
                    return s != null
                            ? s.toLocalDate().toString()
                            : "";
                })
                .setHeader("Start Date")
                .setKey("startDate");

        // Alusta provider
        provider = DataProvider.ofCollection(exercisesService.findAll());
        grid.setDataProvider(provider);

        // Lisää header-rivi suodattimia varten
        HeaderRow filterRow = grid.appendHeaderRow();

        // Luo tekstikentät suodattimiin
        TextField typeFilter     = new TextField();
        TextField distanceFilter = new TextField();
        TextField durationFilter = new TextField();
        TextField notesFilter    = new TextField();
        TextField dateFilter     = new TextField();

        // Asetukset
        typeFilter.setPlaceholder("Type...");
        distanceFilter.setPlaceholder("Distance...");
        durationFilter.setPlaceholder("Duration...");
        notesFilter.setPlaceholder("Notes...");
        dateFilter.setPlaceholder("YYYY-MM-DD...");
        for (TextField f : List.of(typeFilter, distanceFilter, durationFilter, notesFilter, dateFilter)) {
            f.setClearButtonVisible(true);
            f.setWidthFull();
        }

        // Kuuntelijat
        typeFilter.addValueChangeListener(e -> {
            typeFilterValue = e.getValue().trim().toLowerCase();
            applyFilter();
        });
        distanceFilter.addValueChangeListener(e -> {
            distanceFilterValue = e.getValue().trim().toLowerCase();
            applyFilter();
        });
        durationFilter.addValueChangeListener(e -> {
            durationFilterValue = e.getValue().trim().toLowerCase();
            applyFilter();
        });
        notesFilter.addValueChangeListener(e -> {
            notesFilterValue = e.getValue().trim().toLowerCase();
            applyFilter();
        });
        dateFilter.addValueChangeListener(e -> {
            dateFilterValue = e.getValue().trim();
            applyFilter();
        });

        // Aseta kentät sarakkeiden päälle
        filterRow.getCell(grid.getColumnByKey("type")).setComponent(typeFilter);
        filterRow.getCell(grid.getColumnByKey("distance")).setComponent(distanceFilter);
        filterRow.getCell(grid.getColumnByKey("duration")).setComponent(durationFilter);
        filterRow.getCell(grid.getColumnByKey("notes")).setComponent(notesFilter);
        filterRow.getCell(grid.getColumnByKey("startDate")).setComponent(dateFilter);

        // Valinta avaa muokkauslomakkeen
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                clearForm();
            } else {
                editExercise(event.getValue());
            }
        });
    }

    private void applyFilter() {
        provider.setFilter(ex -> {
            boolean okType = typeFilterValue.isEmpty() ||
                    (ex.getType() != null
                            && ex.getType().toLowerCase().contains(typeFilterValue));
            boolean okDistance = distanceFilterValue.isEmpty() ||
                    (ex.getDistance() != null
                            && ex.getDistance().toString().contains(distanceFilterValue));
            boolean okDuration = durationFilterValue.isEmpty() ||
                    (ex.getStartTime() != null
                            && ex.getEndTime() != null
                            && Long.toString(Duration.between(
                                    ex.getStartTime(), ex.getEndTime())
                            .toMinutes()).contains(durationFilterValue));
            boolean okNotes = notesFilterValue.isEmpty() ||
                    (ex.getNotes() != null
                            && ex.getNotes().toLowerCase().contains(notesFilterValue));
            boolean okDate = dateFilterValue.isEmpty() ||
                    (ex.getStartTime() != null
                            && ex.getStartTime().toLocalDate().toString()
                            .contains(dateFilterValue));
            return okType && okDistance && okDuration && okNotes && okDate;
        });
    }

    private void updateList() {
        provider.getItems().clear();
        provider.getItems().addAll(exercisesService.findAll());
        provider.refreshAll();
    }

    private void clearForm() {
        this.exercises = null;
        binder.readBean(null);
    }

    private void editExercise(Exercises exercise) {
        this.exercises = exercise;
        binder.readBean(exercise);
    }

    private void configureForm() {
        FormLayout form = new FormLayout();
        startTime = new DateTimePicker("Start time");
        endTime   = new DateTimePicker("End time");
        type      = new TextField("Type");
        distance  = new TextField("Distance");
        notes     = new TextField("Notes");

        form.add(startTime, endTime, type, distance, notes);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> {
            try {
                if (this.exercises == null) {
                    this.exercises = new Exercises();
                }
                binder.writeBean(this.exercises);
                exercisesService.save(this.exercises);
                Notification.show("Saved successfully", 2000, Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                clearForm();
                updateList();
            } catch (Exception ex) {
                Notification.show("Saving failed: " + ex.getMessage(),
                                3000, Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        cancel.addClickListener(e -> clearForm());

        binder = new BeanValidationBinder<>(Exercises.class);
        binder.bindInstanceFields(this);

        Div formLayout = new Div(form, buttons);
        formLayout.addClassName("exercise-form");

        add(formLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Ei reittiparametreja
    }
}

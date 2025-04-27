package com.example.application.views.exercises;

import com.example.application.data.Exercises;
import com.example.application.data.User;
import com.example.application.security.SecurityUtils;
import com.example.application.services.ExercisesService;
import com.example.application.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "my-exercises", layout = MainLayout.class)
@PageTitle("My Exercises")
@RolesAllowed({"USER","ADMIN"})
public class MyExercisesView extends VerticalLayout {

    private final ExercisesService service;
    private final UserService userService;

    private final Grid<Exercises> grid = new Grid<>(Exercises.class, false);
    private ListDataProvider<Exercises> provider;

    // Suodatin‐kentät (poistettu tag)
    private final TextField filterType     = new TextField();
    private final TextField filterDistance = new TextField();
    private final TextField filterNotes    = new TextField();
    private final DateTimePicker filterStart = new DateTimePicker();

    // Lomake ja binder
    private final Binder<Exercises> binder = new BeanValidationBinder<>(Exercises.class);
    private final FormLayout form = new FormLayout();
    private Exercises current;

    // Lomake‐kentät (poistettu tag)
    private final TextField typeField       = new TextField("Type");
    private final NumberField distanceField = new NumberField("Distance");
    private final TextField notesField      = new TextField("Notes");
    private final DateTimePicker startPicker = new DateTimePicker("Start Time");
    private final DateTimePicker endPicker   = new DateTimePicker("End Time");

    // Napit
    private final com.vaadin.flow.component.button.Button save   =
            new com.vaadin.flow.component.button.Button("Save");
    private final com.vaadin.flow.component.button.Button delete =
            new com.vaadin.flow.component.button.Button("Delete");
    private final com.vaadin.flow.component.button.Button cancel =
            new com.vaadin.flow.component.button.Button("Cancel");
    private final com.vaadin.flow.component.button.Button addNew =
            new com.vaadin.flow.component.button.Button("Add New");

    public MyExercisesView(ExercisesService service,
                           UserService userService) {
        this.service     = service;
        this.userService = userService;

        setSizeFull();
        configureGrid();
        configureFilters();
        configureForm();
        updateGrid();

        H3 title = new H3("My Exercises");
        HorizontalLayout toolbar = new HorizontalLayout(
                filterType, filterDistance, filterNotes, filterStart, addNew
        );
        toolbar.setAlignItems(Alignment.END);

        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setSizeFull();
        grid.setSizeFull();
        form.setVisible(false);

        add(title, toolbar, content);
    }

    private void configureGrid() {
        grid.removeAllColumns();
        grid.addColumn(Exercises::getType).setHeader("Type").setKey("type");
        grid.addColumn(Exercises::getDistance).setHeader("Distance").setKey("distance");
        grid.addColumn(Exercises::getNotes).setHeader("Notes").setKey("notes");
        grid.addColumn(Exercises::getStartTime).setHeader("Start").setKey("start");
        grid.addColumn(Exercises::getEndTime).setHeader("End").setKey("end");

        grid.asSingleSelect().addValueChangeListener(e -> {
            Exercises sel = e.getValue();
            if (sel != null) {
                editExercise(sel);
            } else {
                closeEditor();
            }
        });
    }

    private void configureFilters() {
        provider = new ListDataProvider<>(service.findByOwnerEmail(
                SecurityUtils.getCurrentUserEmail()
        ));
        grid.setDataProvider(provider);

        filterType.setPlaceholder("Type");
        filterType.addValueChangeListener(e ->
                provider.addFilter(ex ->
                        ex.getType() != null &&
                                ex.getType().toLowerCase().contains(e.getValue().toLowerCase()))
        );

        filterDistance.setPlaceholder("Distance");
        filterDistance.addValueChangeListener(e ->
                provider.addFilter(ex ->
                        ex.getDistance() != null &&
                                ex.getDistance().toString().contains(e.getValue()))
        );

        filterNotes.setPlaceholder("Notes");
        filterNotes.addValueChangeListener(e ->
                provider.addFilter(ex ->
                        ex.getNotes() != null &&
                                ex.getNotes().toLowerCase().contains(e.getValue().toLowerCase()))
        );

        filterStart.addValueChangeListener(e ->
                provider.addFilter(ex ->
                        e.getValue() == null ||
                                (ex.getStartTime() != null &&
                                        ex.getStartTime().toLocalDate().equals(e.getValue().toLocalDate())))
        );

        addNew.addClickListener(e -> editExercise(new Exercises()));
    }

    private void configureForm() {
        binder.forField(typeField)
                .bind(Exercises::getType, Exercises::setType);

        binder.forField(distanceField)
                .bind(
                        ex -> ex.getDistance() != null ? ex.getDistance().doubleValue() : null,
                        (ex, val) -> ex.setDistance(val != null ? val.intValue() : null)
                );

        binder.forField(notesField)
                .bind(Exercises::getNotes, Exercises::setNotes);
        binder.forField(startPicker)
                .bind(Exercises::getStartTime, Exercises::setStartTime);
        binder.forField(endPicker)
                .bind(Exercises::getEndTime, Exercises::setEndTime);

        save.addClickListener(e -> saveExercise());
        delete.addClickListener(e -> deleteExercise());
        cancel.addClickListener(e -> closeEditor());

        form.add(typeField, distanceField, notesField,
                startPicker, endPicker,
                new HorizontalLayout(save, delete, cancel));
    }

    private void editExercise(Exercises ex) {
        current = ex;
        binder.readBean(ex);
        form.setVisible(true);
    }

    private void saveExercise() {
        try {
            binder.writeBean(current);
            User owner = userService.findByEmail(
                    SecurityUtils.getCurrentUserEmail()
            ).orElseThrow();
            current.setOwner(owner);
            service.save(current);
            Notification.show("Saved")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            updateGrid();
            closeEditor();
        } catch (Exception ex) {
            ex.printStackTrace();              // ← lisää tämä
            Notification.show("Error: " + ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }


    private void deleteExercise() {
        service.delete(current);
        Notification.show("Deleted")
                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        updateGrid();
        closeEditor();
    }

    private void closeEditor() {
        form.setVisible(false);
    }

    private void updateGrid() {
        List<Exercises> items = service.findByOwnerEmail(
                SecurityUtils.getCurrentUserEmail()
        );
        provider.getItems().clear();
        provider.getItems().addAll(items);
        provider.refreshAll();
    }
}

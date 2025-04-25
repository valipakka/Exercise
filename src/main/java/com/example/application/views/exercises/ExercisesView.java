package com.example.application.views.exercises;

import com.example.application.data.Exercises;
import com.example.application.services.ExercisesService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.time.Duration;
import java.util.Optional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Exercises")
@Route("exercises/:exercisesID?/:action?(edit)")
@Menu(order = 2, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
public class ExercisesView extends Div implements BeforeEnterObserver {

    private final String EXERCISES_ID = "exercisesID";
    private final String EXERCISES_EDIT_ROUTE_TEMPLATE = "exercises/%s/edit";

    private final Grid<Exercises> grid = new Grid<>(Exercises.class, false);

    private DateTimePicker startTime;
    private DateTimePicker endTime;
    private TextField type;
    private TextField distance;
    private TextField notes;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Exercises> binder;

    private Exercises exercises;

    private final ExercisesService exercisesService;

    public ExercisesView(ExercisesService exercisesService) {
        this.exercisesService = exercisesService;
        addClassNames("exercises-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("startTime").setAutoWidth(true);
        grid.addColumn("endTime").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.addColumn("distance").setAutoWidth(true);
        grid.addColumn("notes").setAutoWidth(true);
        grid.setItems(query -> exercisesService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EXERCISES_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ExercisesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Exercises.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(distance).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("distance");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.exercises == null) {
                    this.exercises = new Exercises();
                }
                binder.writeBean(this.exercises);
                exercisesService.save(this.exercises);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ExercisesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> exercisesId = event.getRouteParameters().get(EXERCISES_ID).map(Long::parseLong);
        if (exercisesId.isPresent()) {
            Optional<Exercises> exercisesFromBackend = exercisesService.get(exercisesId.get());
            if (exercisesFromBackend.isPresent()) {
                populateForm(exercisesFromBackend.get());
            } else {
                Notification.show(String.format("The requested exercises was not found, ID = %s", exercisesId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ExercisesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        startTime = new DateTimePicker("Start Time");
        startTime.setStep(Duration.ofSeconds(1));
        endTime = new DateTimePicker("End Time");
        endTime.setStep(Duration.ofSeconds(1));
        type = new TextField("Type");
        distance = new TextField("Distance");
        notes = new TextField("Notes");
        formLayout.add(startTime, endTime, type, distance, notes);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Exercises value) {
        this.exercises = value;
        binder.readBean(this.exercises);

    }
}

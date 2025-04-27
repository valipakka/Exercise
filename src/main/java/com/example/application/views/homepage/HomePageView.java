// src/main/java/com/example/application/views/HomePageView.java
package com.example.application.views.homepage;

import com.example.application.data.Exercises;
import com.example.application.security.SecurityUtils;
import com.example.application.services.ExercisesService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.grid.Grid;
import com.example.application.views.MainLayout;

import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
@PermitAll
public class HomePageView extends VerticalLayout {

    private final ExercisesService exercisesService;
    private final Grid<Exercises> grid = new Grid<>(Exercises.class, false);

    public HomePageView(ExercisesService exercisesService) {
        this.exercisesService = exercisesService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Welcome to Your Workout Tracker"));
        configureGrid();
        add(grid);
        updateGrid();
    }

    private void configureGrid() {
        grid.addColumn(Exercises::getType).setHeader("Type");
        grid.addColumn(Exercises::getDistance).setHeader("Distance");
        grid.addColumn(ex -> {
            if (ex.getStartTime() != null && ex.getEndTime() != null) {
                return java.time.Duration.between(ex.getStartTime(), ex.getEndTime()).toMinutes();
            }
            return null;
        }).setHeader("Duration (min)");
        grid.addColumn(Exercises::getNotes).setHeader("Notes");
        grid.setSizeFull();
    }

    private void updateGrid() {
        String email = SecurityUtils.getCurrentUserEmail();
        removeAll();              // Tyhjennä näkymä
        add(new H2("Welcome to Your Workout Tracker"));

        if (email != null) {
            // Kirjautunut käyttäjä: näytä omat treenit
            List<Exercises> items = exercisesService.findByOwnerEmail(email);
            grid.setItems(items);
            add(grid);
        } else {
            // Vierailija: näytä kehote kirjautua
            add(new Paragraph("Login to see your exercises."));
        }
    }
}

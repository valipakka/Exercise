
package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class WorkoutProgram extends AbstractEntity {

    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "featured_exercise_id")
    private Exercises featuredExercise;

    // Getterit ja setterit

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Exercises getFeaturedExercise() {
        return featuredExercise;
    }
    public void setFeaturedExercise(Exercises featuredExercise) {
        this.featuredExercise = featuredExercise;
    }
}

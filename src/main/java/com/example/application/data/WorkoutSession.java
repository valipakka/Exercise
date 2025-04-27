
package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class WorkoutSession extends AbstractEntity {

    private LocalDateTime sessionDate;
    private Integer durationMinutes;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private WorkoutProgram program;

    // Getterit ja setterit

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }
    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public WorkoutProgram getProgram() {
        return program;
    }
    public void setProgram(WorkoutProgram program) {
        this.program = program;
    }
}

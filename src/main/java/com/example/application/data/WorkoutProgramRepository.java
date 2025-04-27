package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutProgramRepository extends JpaRepository<WorkoutProgram, Long> {
    // tarvittaessa omia finderiä, esim. findByName…
}
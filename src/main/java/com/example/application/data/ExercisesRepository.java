package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface ExercisesRepository extends JpaRepository<Exercises, Long>, JpaSpecificationExecutor<Exercises> {

    List<Exercises> findByOwnerEmail(String email);
}

package com.example.application.services;

import com.example.application.data.Exercises;
import com.example.application.data.ExercisesRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ExercisesService {

    private final ExercisesRepository repository;

    public ExercisesService(ExercisesRepository repository) {
        this.repository = repository;
    }

    public Optional<Exercises> get(Long id) {
        return repository.findById(id);
    }

    public Exercises save(Exercises entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Exercises> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Exercises> list(Pageable pageable, Specification<Exercises> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}

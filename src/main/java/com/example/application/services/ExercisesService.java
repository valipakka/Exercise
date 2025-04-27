package com.example.application.services;

import com.example.application.data.Exercises;
import com.example.application.data.ExercisesRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ExercisesService {

    private final ExercisesRepository repository;

    public ExercisesService(ExercisesRepository repository) {
        this.repository = repository;
    }

    /** Hae yksittäinen harjoitus ID:llä */
    public Optional<Exercises> get(Long id) {
        return repository.findById(id);
    }

    /** Tallenna tai päivitä harjoitus */
    public Exercises save(Exercises entity) {
        return repository.save(entity);
    }

    /** Poista olion perusteella (Crud<T> tarvitsee tätä) */
    public void delete(Exercises entity) {
        repository.delete(entity);
    }

    /** Poista ID:n perusteella (jos haluat kutsua pelkällä id:llä) */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /** Sivutettu listaus (jos tarvitaan) */
    public Page<Exercises> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /** Sivutettu ja suodatettu listaus */
    public Page<Exercises> list(Pageable pageable, Specification<Exercises> filter) {
        return repository.findAll(filter, pageable);
    }

    /** Laske kokonaismäärä */
    public int count() {
        return (int) repository.count();
    }

    /** Palauttaa kaikki harjoitukset listana (CRUD-komponentin vaatima) */
    public List<Exercises> findAll() {
        return repository.findAll();
    }
    /** Palauttaa kaikki harjoitukset, joiden owner.email = email */
    public List<Exercises> findByOwnerEmail(String email) {
        return repository.findByOwnerEmail(email);
    }


}

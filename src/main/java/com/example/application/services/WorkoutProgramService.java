
package com.example.application.services;

import com.example.application.data.WorkoutProgram;
import com.example.application.data.WorkoutProgramRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkoutProgramService {

    private final WorkoutProgramRepository repo;

    public WorkoutProgramService(WorkoutProgramRepository repo) {
        this.repo = repo;
    }

    public WorkoutProgram save(WorkoutProgram program) {
        return repo.save(program);
    }

    public void delete(WorkoutProgram program) {
        repo.delete(program);
    }

    public List<WorkoutProgram> findAll() {
        return repo.findAll();
    }
}


package com.example.application.services;

import com.example.application.data.WorkoutProgram;
import com.example.application.data.WorkoutSession;
import com.example.application.data.WorkoutSessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkoutSessionService {

    private final WorkoutSessionRepository repo;

    public WorkoutSessionService(WorkoutSessionRepository repo) {
        this.repo = repo;
    }

    public WorkoutSession save(WorkoutSession session) {
        return repo.save(session);
    }

    public void delete(WorkoutSession session) {
        repo.delete(session);
    }


    public List<WorkoutSession> findAll() {
        return repo.findAll();
    }

    public List<WorkoutSession> findByProgram(WorkoutProgram program) {
        return repo.findByProgram(program);
    }
}

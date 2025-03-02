package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.mpa.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepo;

import java.util.List;

@Service
public class MpaService {
    private final MpaRepo mpaRepo;

    @Autowired
    public MpaService(MpaRepo mpaRepo) {
        this.mpaRepo = mpaRepo;
    }

    public List<MpaRating> getAllMpa() {
        return mpaRepo.getAllMpa();
    }

    public MpaRating getMpaById(Integer id) {
        return mpaRepo.getMpaById(id)
                .orElseThrow(() -> new MpaNotFoundException(String.format("MPA rating with id %d not found", id)));
    }
}
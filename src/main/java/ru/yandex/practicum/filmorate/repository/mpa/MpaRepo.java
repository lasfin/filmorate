package ru.yandex.practicum.filmorate.repository.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaRepo {
    List<MpaRating> getAllMpa();
    Optional<MpaRating> getMpaById(Integer id);
}

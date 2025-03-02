package ru.yandex.practicum.filmorate.repository.mpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Primary
@Repository
public class InMemoryMpaRepo implements MpaRepo {
    private final Map<Long, MpaRating> mpaRatings = new HashMap<>();

    public InMemoryMpaRepo() {
        mpaRatings.put(1L, new MpaRating(1L, "G"));
        mpaRatings.put(2L, new MpaRating(2L, "PG"));
        mpaRatings.put(3L, new MpaRating(3L, "PG-13"));
        mpaRatings.put(4L, new MpaRating(4L, "R"));
        mpaRatings.put(5L, new MpaRating(5L, "NC-17"));
    }

    @Override
    public List<MpaRating> getAllMpa() {
        return new ArrayList<>(mpaRatings.values());
    }

    @Override
    public Optional<MpaRating> getMpaById(Integer id) {
        return Optional.ofNullable(mpaRatings.get(id.longValue()));
    }
}
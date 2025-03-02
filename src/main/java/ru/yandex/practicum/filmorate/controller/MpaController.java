package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundResponse;
import ru.yandex.practicum.filmorate.exceptions.mpa.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Controller
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public ResponseEntity<List<MpaRating>> getAllMpa() {
        return ResponseEntity.ok(mpaService.getAllMpa());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaRating> getMpaById(@PathVariable Integer id) {
        return ResponseEntity.ok(mpaService.getMpaById(id));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(final MpaNotFoundException e) {
        return new NotFoundResponse("Not found", e.getMessage());
    }
}
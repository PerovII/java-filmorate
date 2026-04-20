package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> create(@Valid @RequestBody ReviewDto dto) {
        log.info("Запрос на создание отзыва: {}", dto);
        return ResponseEntity.ok(reviewService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getById(@PathVariable Long id) {
        log.info("Запрос на получение отзыва, id = {}", id);
        return ResponseEntity.ok(reviewService.getById(id));
    }

    @PutMapping
    public ResponseEntity<ReviewDto> update(@Valid @RequestBody UpdateReviewRequest updateReviewRequest) {
        log.info("Запрос на обновление отзыва: {}", updateReviewRequest);
        return ResponseEntity.ok(reviewService.update(updateReviewRequest));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление отзыва, id = {}", id);
        reviewService.delete(id);
    }

    @GetMapping
    public List<ReviewDto> getAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        log.info("Запрос на получение отзывов, filmId = {}, count = {}", filmId, count);
        return reviewService.getAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на добавление лайка к отзыву, id = {}, userId = {}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на добавление дизлайка к отзыву, id = {}, userId = {}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на удаление лайка у отзыва, id = {}, userId = {}", id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на удаление дизлайка у отзыва, id = {}, userId = {}", id, userId);
        reviewService.removeLike(id, userId);
    }

}

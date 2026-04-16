package ru.yandex.practicum.filmorate.controller;

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
    public ResponseEntity<ReviewDto> create(@RequestBody ReviewDto dto) {
        log.info("Create review: {}", dto);
        return ResponseEntity.ok(reviewService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getById(@PathVariable Long id) {
        log.info("Get review by id: {}", id);
        return ResponseEntity.ok(reviewService.getById(id));
    }

    @PutMapping
    public ResponseEntity<ReviewDto> update(@RequestBody UpdateReviewRequest updateReviewRequest) {
        log.info("Update review: {}", updateReviewRequest);
        return ResponseEntity.ok(reviewService.update(updateReviewRequest));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Delete review by id: {}", id);
        reviewService.delete(id);
    }

    @GetMapping
    public List<ReviewDto> getAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        log.info("Get reviews by filmId: {}, count: {}", filmId, count);
        return reviewService.getAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Add like review by id: {}, userId: {}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Add dislike review by id: {}, userId: {}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Remove like review by id: {}, userId: {}", id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Remove dislike review by id: {}, userId: {}", id, userId);
        reviewService.removeLike(id, userId);
    }

}

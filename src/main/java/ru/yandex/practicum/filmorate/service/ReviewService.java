package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage, EventService eventService) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public ReviewDto create(ReviewDto dto) {
        checkDataReviewDto(dto);
        Review review = ReviewMapper.toModel(dto);
        ReviewDto createdReview = ReviewMapper.toDto(reviewStorage.create(review));

        // Добавляем событие о создании отзыва
        eventService.addEvent(createdReview.getUserId(),
                Event.EventType.REVIEW,
                Event.Operation.ADD,
                createdReview.getReviewId());

        return createdReview;
    }

    public ReviewDto update(UpdateReviewRequest updateReviewRequest) {
        Review existingReview = reviewStorage.getById(updateReviewRequest.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        Review updatedReview = ReviewMapper.updateReviewFields(existingReview, updateReviewRequest);
        ReviewDto savedReview = ReviewMapper.toDto(reviewStorage.update(updatedReview));

        // Добавляем событие об обновлении отзыва
        eventService.addEvent(savedReview.getUserId(),
                Event.EventType.REVIEW,
                Event.Operation.UPDATE,
                savedReview.getReviewId());

        return savedReview;
    }

    public ReviewDto getById(Long id) {
        Review review = reviewStorage.getById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        return ReviewMapper.toDto(review);
    }

    private void checkDataReviewDto(ReviewDto dto) {
        filmStorage.findById(dto.getFilmId()).orElseThrow(
                () -> new NotFoundException("Фильм не найден")
        );
        userStorage.findById(dto.getUserId()).orElseThrow(
                () -> new NotFoundException("Пользователь c id = " + dto.getUserId() + "не найден")
        );
    }


    public void delete(Long id) {
        Review review = reviewStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        reviewStorage.delete(id);

        // Добавляем событие об удалении отзыва
        eventService.addEvent(review.getUserId(),
                Event.EventType.REVIEW,
                Event.Operation.REMOVE,
                id);
    }

    public List<ReviewDto> getAll(Long filmId, int count) {
        return reviewStorage.getAll(filmId, count).stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public void addLike(Long reviewId, Long userId) {
        reviewStorage.getById(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c id = " + userId + "не найден")
        );
        reviewStorage.addLike(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        reviewStorage.getById(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c id = " + userId + "не найден")
        );
        reviewStorage.addLike(reviewId, userId, false);
    }

    public void removeLike(Long reviewId, Long userId) {
        reviewStorage.getById(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c id = " + userId + "не найден")
        );
        reviewStorage.removeLike(reviewId, userId);
    }
}

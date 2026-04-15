package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public ReviewDto create(ReviewDto dto) {
        checkDataReviewDto(dto);
        Review review = ReviewMapper.toModel(dto);
        return ReviewMapper.toDto(reviewStorage.create(review));
    }

    public ReviewDto update(UpdateReviewRequest updateReviewRequest) {
        Review updatedReview = reviewStorage.getById(updateReviewRequest.getReviewId()).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        return ReviewMapper.toDto(
                reviewStorage.update(
                        ReviewMapper.updateReviewFields(updatedReview, updateReviewRequest)
                )
        );
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
        reviewStorage.getById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        reviewStorage.delete(id);
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

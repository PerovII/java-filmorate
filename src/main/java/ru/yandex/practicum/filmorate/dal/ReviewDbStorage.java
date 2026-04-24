package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String INSERT_QUERY = """
            INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, 0)
            """;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM review_likes
            WHERE review_id = ? AND user_id = ?
            """;
    private static final String UPDATE_QUERY = """
            UPDATE reviews
            SET content = ?, is_positive = ?
            WHERE review_id = ?
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT *
            FROM reviews
            WHERE (? IS NULL OR film_id = ?)
            ORDER BY useful DESC
            LIMIT ?
            """;

    private static final String MERGE_LIKE_QUERY = """
            MERGE INTO review_likes (review_id, user_id, is_like)
            VALUES (?, ?, ?)
            """;

    @Autowired
    public ReviewDbStorage(
            JdbcTemplate jdbc,
            RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review create(Review review) {
        long id = insert(INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );
        review.setReviewId(id);
        review.setUseful(0);
        log.info("Created review {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        int updatedRows = jdbc.update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        if (updatedRows == 0) {
            throw new NotFoundException("Отзыв с id " + review.getReviewId() + " не найден");
        }

        Review updatedReview = getById(review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        log.info("Updated review {}", updatedReview);
        return updatedReview;
    }

    @Override
    public void delete(Long id) {
        jdbc.update("DELETE FROM review_likes WHERE review_id = ?", id);
        int deleted = jdbc.update(DELETE_BY_ID_QUERY, id);
        if (deleted == 0) {
            throw new NotFoundException("Отзыв с id " + id + " не найден");
        }
        log.info("Deleted review {}", id);
    }

    @Override
    public Optional<Review> getById(Long id) {
        Optional<Review> optionalReview = findOne(FIND_BY_ID_QUERY, id);
        log.info("Found review {}", optionalReview);
        return optionalReview;
    }

    @Override
    public List<Review> getAll(Long filmId, int count) {
        List<Review> list = findMany(FIND_ALL_QUERY, filmId, filmId, count);
        log.info("Found {} reviews for film {}", list.size(), filmId);
        return list;
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        delete(DELETE_LIKE_QUERY, reviewId, userId);
        updateUseful(reviewId);
        log.info("Removed like review {}", reviewId);
    }

    @Override
    public void addLike(Long reviewId, Long userId, boolean isLike) {
        jdbc.update(MERGE_LIKE_QUERY, reviewId, userId, isLike);
        updateUseful(reviewId);
        log.info("Merged like/dislike for review {}", reviewId);
    }

    private void updateUseful(Long reviewId) {
        String sql = """
                UPDATE reviews r
                SET useful = (
                    SELECT COALESCE(SUM(
                        CASE WHEN rl.is_like THEN 1 ELSE -1 END
                    ), 0)
                    FROM review_likes rl
                    WHERE rl.review_id = r.review_id
                )
                WHERE r.review_id = ?
                """;

        update(sql, reviewId);
    }
}

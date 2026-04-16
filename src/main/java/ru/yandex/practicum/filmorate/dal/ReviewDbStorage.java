package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
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
    private static final String INSERT_LIKE_QUERY = """
            INSERT INTO review_likes (review_id, user_id, is_like)
            VALUES (?, ?, ?)
            """;
    private static final String UPDATE_LIKE_QUERY = """
            UPDATE review_likes
            SET is_like = ?
            WHERE review_id = ? AND user_id = ?
            """;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM review_likes
            WHERE review_id = ? AND user_id = ?
            """;
    private static final String UPDATE_QUERY = """
            UPDATE reviews
            SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ?
            WHERE review_id = ?
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT *
            FROM reviews
            WHERE (? IS NULL OR film_id = ?)
            ORDER BY useful DESC
            LIMIT ?
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
        update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getReviewId()
        );
        log.info("Updated review {}", review);
        return review;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_BY_ID_QUERY, id);
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
        if (insert(INSERT_LIKE_QUERY, reviewId, userId, isLike) == -1) {
            update(UPDATE_LIKE_QUERY, isLike, reviewId, userId);
        }
        updateUseful(reviewId);
        log.info("Added like review {}", reviewId);
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

package br.com.novalearn.platform.api.mappers.review;

import br.com.novalearn.platform.api.dtos.review.ReviewListResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.review.Review;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.review.CreateReviewFactory.*;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ReviewMapperTest {
    private LocalDateTime now;
    private ReviewMapper mapper;
    private Course course;
    private User user;
    private Review review;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mapper = new ReviewMapper();
        course = createInitializedCourse();
        user = createInitializedUser();
        ReflectionTestUtils.setField(user, "id", 5L);
        review = createInitializedReview(user, course, now);
    }

    @Test
    void should_map_to_response_dto() {
        LocalDateTime publishedAt = now.minusHours(5);

        review.approveAndPublish(publishedAt);

        Long createdBy = 5L;
        LocalDateTime createdAt = now.minusDays(3);

        review.auditCreate(createdBy, createdAt);

        ReviewResponseDTO dto = mapper.toResponseDTO(review);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(review.getId());
        assertThat(dto.getActive()).isEqualTo(review.isActive());
        assertThat(dto.getDeleted()).isEqualTo(review.isDeleted());

        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getUpdatedAt()).isEqualTo(review.getUpdatedAt());

        assertThat(dto.getObservations()).isEqualTo(review.getObservations());

        assertThat(dto.getCourseId()).isEqualTo(course.getId());
        assertThat(dto.getUserId()).isEqualTo(user.getId());

        assertThat(dto.getRating()).isEqualTo(5);
        assertThat(dto.getComment()).isEqualTo("Excellent course");

        assertThat(dto.getReviewAt()).isEqualTo(review.getReviewAt());

        assertThat(dto.getPublishedAt()).isEqualTo(publishedAt);

        assertThat(dto.getAnonymous()).isFalse();
        assertThat(dto.getStatus()).isEqualTo(ReviewStatus.APPROVED);

        assertThat(dto.getCreatedBy()).isEqualTo(createdBy);
        assertThat(dto.getUpdatedBy()).isEqualTo(review.getUpdatedBy());
    }

    @Test
    void should_map_to_list_response_dto() {
        review.approveAndPublish(now);

        ReviewListResponseDTO dto = mapper.toListResponseDTO(review);

        assertThat(dto).isNotNull();

        assertThat(dto.getId()).isEqualTo(review.getId());

        assertThat(dto.getCourseId()).isEqualTo(course.getId());
        assertThat(dto.getUserId()).isEqualTo(user.getId());

        assertThat(dto.getRating()).isEqualTo(5);

        assertThat(dto.getAnonymous()).isEqualTo(review.isAnonymous());
        assertThat(dto.getStatus()).isEqualTo(review.getStatus());

        assertThat(dto.getActive()).isTrue();
        assertThat(dto.getDeleted()).isFalse();
    }

    @Test
    void should_handle_null_relations() {
        Review review = createInvalidReview(user, course, now);

        ReflectionTestUtils.setField(review, "course", null);

        review.activate();
        review.markAsNotDeleted();

        ReviewResponseDTO dto = mapper.toResponseDTO(review);

        assertThat(dto.getCourseId()).isNull();
        assertThat(dto.getUserId()).isNotNull();
    }
}
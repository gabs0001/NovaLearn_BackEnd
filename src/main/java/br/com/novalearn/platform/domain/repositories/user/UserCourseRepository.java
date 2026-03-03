package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.user.UserCourse;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findAllByDeletedFalse();
    List<UserCourse> findAllByUserIdAndDeletedFalse(Long userId);
    List<UserCourse> findAllByUserIdAndDeletedFalseOrderByEnrolledAtDesc(Long userId);
    List<UserCourse> findAllByUserIdAndEnrollmentStatusAndDeletedFalse(Long userId, EnrollmentStatus status);
    List<UserCourse> findAllByUserIdAndCompletedAtIsNotNullAndDeletedFalse(Long userId);
    List<UserCourse> findAllByUserIdAndCompletedAtIsNullAndDeletedFalse(Long userId);
    List<UserCourse> findAllByUserIdAndEnrollmentStatusAndDeletedFalseOrderByCompletedAtDesc(Long userId, EnrollmentStatus status);
    List<UserCourse> findAllByUserIdAndEnrollmentStatusAndDeletedFalseOrderByEnrolledAtDesc(Long userId, EnrollmentStatus status);
    Optional<UserCourse> findByUserIdAndCourseIdAndDeletedFalse(Long userId, Long courseId);
    boolean existsByUserIdAndCourseIdAndDeletedFalse(Long userId, Long courseId);
    long countByUserIdAndDeletedFalse(Long userId);

    @Query("SELECT COUNT(uc) FROM UserCourse uc " +
            "WHERE uc.user.id = :userId " +
            "AND uc.completedAt IS NOT NULL " +
            "AND uc.deleted = false")
    long countCompletedByUserId(Long userId);
}
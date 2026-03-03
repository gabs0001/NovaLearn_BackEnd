package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.repositories.lesson.LessonContentRepository;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "T_SINL_LESSON_CONTENT",
        indexes = {
                @Index(name = "IDX_T_SINL_LESSONCONTENT_LESSON", columnList = "cod_lesson")
        }
)
@SequenceGenerator(
        name = DatabaseSequences.LESSON_CONTENT_SEQ,
        sequenceName = DatabaseSequences.LESSON_CONTENT_SEQ,
        allocationSize = 1
)
public class LessonContent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.LESSON_CONTENT_SEQ)
    @Column(name = "cod_lesson_content", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_lesson", nullable = false)
    private Lesson lesson;

    @Column(name = "val_video_url", length = 500)
    private String videoUrl;

    @Column(name = "val_transcript_url", length = 500)
    private String transcriptUrl;

    @Column(name = "val_material_url", length = 500)
    private String materialUrl;

    @Lob
    @Column(name = "txt_content")
    private String content;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_has_quiz", length = 1, nullable = false)
    private boolean hasQuiz;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_main_content", length = 1, nullable = false)
    private boolean mainContent;

    @Column(name = "obs_content", length = 500)
    private String observations;

    public LessonContent() {}

    public static LessonContent create(
            Lesson lesson,
            String videoUrl,
            String transcriptUrl,
            String materialUrl,
            String content,
            boolean hasQuiz,
            boolean mainContent,
            String observations,
            Long createdBy,
            LocalDateTime createdAt
    ) {
        LessonContent lc = new LessonContent();
        lc.attachToLesson(lesson);

        lc.videoUrl = sanitize(videoUrl);
        lc.transcriptUrl = sanitize(transcriptUrl);
        lc.materialUrl = sanitize(materialUrl);
        lc.content = sanitize(content);
        lc.hasQuiz = hasQuiz;
        lc.mainContent = mainContent;
        lc.observations = sanitize(observations);

        lc.setActive(true);
        lc.setDeleted(false);
        lc.setCreatedBy(createdBy);
        lc.setCreatedAt(createdAt);

        lc.validate();
        return lc;
    }

    public void attachToLesson(Lesson lesson) {
        if(lesson == null) throw new ValidationException("Lesson cannot be null.");

        if(this.lesson != null) throw new ValidationException("LessonContent is already attached to a Lesson.");

        this.lesson = lesson;
    }

    public void changeVideoUrl(String url) {
        this.videoUrl = sanitize(url);
        validate();
    }

    public void changeMaterialUrl(String url) {
        this.materialUrl = sanitize(url);
        validate();
    }

    public void changeTranscriptUrl(String url) {
        this.transcriptUrl = sanitize(url);
        validate();
    }

    public void changeContent(String content) {
        this.content = sanitize(content);
        validate();
    }

    public void changeHasQuiz(boolean hasQuiz) {
        this.hasQuiz = hasQuiz;
        validate();
    }

    public void changeMainContent(boolean mainContent) {
        this.mainContent = mainContent;
        validate();
    }

    public void markAsMain(LessonContentRepository repo) {
        if(lesson == null) throw new ValidationException("LessonContent must belong to a Lesson.");
        if(repo.existsByLessonIdAndMainContentTrue(lesson.getId())) throw new ValidationException("Lesson already has a main content.");
        this.mainContent = true;
    }

    private void validate() {
        if(lesson == null) throw new ValidationException("Lesson Content must belong to a Lesson.");

        boolean hasAnyContent =
                (videoUrl != null && !videoUrl.isBlank()) ||
                        (materialUrl != null && !materialUrl.isBlank()) ||
                        (content != null && !content.isBlank());

        if(!hasAnyContent) throw new ValidationException("Lesson Content must have at least one type of content.");
    }

    public Long getId() { return id; }
    public Lesson getLesson() { return lesson; }
    public String getVideoUrl() { return videoUrl; }
    public String getTranscriptUrl() { return transcriptUrl; }
    public String getMaterialUrl() { return materialUrl; }
    public String getContent() { return content; }
    public boolean isHasQuiz() { return hasQuiz; }
    public boolean isMainContent() { return mainContent; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "LessonContent{" +
                "id=" + id +
                ", lessonId=" + (lesson != null ? lesson.getId() : null) +
                ", hasVideo=" + (videoUrl != null) +
                ", hasMaterial=" + (materialUrl != null) +
                ", hasTextContent=" + (content != null) +
                ", hasQuiz=" + hasQuiz +
                ", mainContent=" + mainContent +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}
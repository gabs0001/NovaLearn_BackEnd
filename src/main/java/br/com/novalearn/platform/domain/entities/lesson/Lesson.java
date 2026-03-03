package br.com.novalearn.platform.domain.entities.lesson;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.UserLessonProgress;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(
        name = "T_SINL_LESSON",
        indexes = {
                @Index(name = "IDX_T_SINL_LESSON_MODULE", columnList = "cod_module")
        }
)
@SequenceGenerator(
        name = DatabaseSequences.LESSON_SEQ,
        sequenceName = DatabaseSequences.LESSON_SEQ,
        allocationSize = 1
)
public class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.LESSON_SEQ)
    @Column(name = "cod_lesson", nullable = false)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_module", nullable = false)
    private Module module;

    @OneToMany(mappedBy = "lesson")
    private List<UserLessonProgress> progressList;

    @Column(name = "nom_lesson", length = 120, nullable = false)
    private String name;
    
    @Column(name = "des_lesson", length = 255)
    private String description;
    
    @Column(name = "seq_lesson")
    private Integer sequence;
    
    @Column(name = "num_duration_seconds")
    private Integer durationSeconds;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_require_completion", length = 1, nullable = false)
    private boolean requireCompletion;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_visible", length = 1, nullable = false)
    private boolean visible;
    
    @Column(name = "val_preview_url", length = 500)
    private String previewUrl;

    @Lob
    @Column(name = "txt_notes")
    private String notes;

    @Column(name = "obs_lesson", length = 500)
    private String observations;

    protected Lesson() {}

    public static Lesson create(
            Module module,
            String name,
            String description,
            Integer sequence,
            Integer durationSeconds,
            boolean requireCompletion,
            boolean visible,
            String previewUrl,
            String notes,
            String observations
    ) {
        Lesson lesson = new Lesson();

        lesson.attachToModule(module);

        lesson.changeName(name);
        lesson.changeDescription(description);
        lesson.changeSequence(sequence);
        lesson.changeDuration(durationSeconds);
        lesson.changeVisibility(visible);
        lesson.changeRequireCompletion(requireCompletion);
        lesson.changePreviewUrl(previewUrl);
        lesson.changeNotes(notes);
        lesson.changeObservations(observations);

        lesson.activate();
        lesson.markAsNotDeleted();

        lesson.validate();

        return lesson;
    }

    public void changeName(String name) {
        if(name == null || name.isBlank()) throw new ValidationException("Lesson name is required.");
        if(name.length() < 3) throw new ValidationException("Lesson name must have at least 3 characters.");
        this.name = name.trim();
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeSequence(Integer sequence) {
        if(sequence == null || sequence < 1) throw new ValidationException("Lesson sequence must be greater than zero.");
        this.sequence = sequence;
    }

    public void changeDuration(Integer durationSeconds) {
        if(durationSeconds == null || durationSeconds < 30) throw new ValidationException("Lesson duration must be at least 30 seconds.");
        this.durationSeconds = durationSeconds;
    }

    public void changeRequireCompletion(boolean requireCompletion) {
        if(requireCompletion && !visible)
            throw new ValidationException("Invisible lessons cannot require completion.");

        this.requireCompletion = requireCompletion;
        validate();
    }

    public void changeVisibility(boolean visible) {
        this.visible = visible;
        validate();
    }

    public void changePreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void changeNotes(String notes) {
        this.notes = notes;
    }

    public void changeObservations(String observations) {
        this.observations = observations;
    }

    public void attachToModule(Module module) {
        if(module == null) throw new ValidationException("Module cannot be null.");
        if(this.module != null) throw new ValidationException("Lesson is already attached to a Module.");
        this.module = module;
    }

    private void validate() {
        if(name == null || name.isBlank()) throw new ValidationException("Lesson name is required.");
        if(sequence != null && sequence < 1) throw new ValidationException("Lesson sequence must be greater than zero.");
        if(durationSeconds != null && durationSeconds < 30) throw new ValidationException("Lesson duration must be at least 30 seconds.");
        if(requireCompletion && !visible) throw new ValidationException("Invisible lessons cannot require completion.");
    }

    public Long getId() { return id; }
    public Module getModule() { return module; }
    public List<UserLessonProgress> getProgressList() { return progressList; }
    //public List<QuizQuestion> getQuizQuestions() { return quizQuestions; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getSequence() { return sequence; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public boolean isRequireCompletion() { return requireCompletion; }
    public boolean isVisible() { return visible; }
    public String getPreviewUrl() { return previewUrl; }
    public String getNotes() { return notes; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", moduleId=" + (module != null ? module.getId() : null) +
                ", name='" + name + '\'' +
                ", sequence=" + sequence +
                ", durationSeconds=" + durationSeconds +
                ", requireCompletion=" + requireCompletion +
                ", visible=" + visible +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}
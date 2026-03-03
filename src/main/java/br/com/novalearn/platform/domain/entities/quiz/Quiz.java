package br.com.novalearn.platform.domain.entities.quiz;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_SINL_QUIZ")
@SequenceGenerator(
        name = DatabaseSequences.QUIZ_SEQ,
        sequenceName = DatabaseSequences.QUIZ_SEQ,
        allocationSize = 1
)
public class Quiz extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.QUIZ_SEQ)
    @Column(name = "cod_quiz", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_module", nullable = false)
    private Module module;

    @Column(name = "nom_quiz", length = 120, nullable = false)
    private String name;

    @Column(name = "des_quiz", length = 255)
    private String description;

    @Column(name = "txt_instructions", length = 1000)
    private String instructions;

    @Column(name = "qtd_questions")
    private Integer qtdQuestions;

    @Column(name = "val_min_score", precision = 5, scale = 2)
    private BigDecimal minScore;

    @Column(name = "qtd_max_attempts")
    private Integer maxAttempts;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_random_order", length = 1, nullable = false)
    private boolean randomOrder;

    @Column(name = "obs_quiz", length = 500)
    private String observations;

    protected Quiz() {}

    public static Quiz create(
            Module module,
            String name,
            Integer qtdQuestions,
            BigDecimal minScore,
            Integer maxAttempts,
            boolean randomOrder
    ) {
        validateCreation(module, name, qtdQuestions, minScore, maxAttempts);

        Quiz quiz = new Quiz();

        quiz.module = module;
        quiz.name = name;
        quiz.qtdQuestions = qtdQuestions;
        quiz.minScore = minScore;
        quiz.maxAttempts = maxAttempts;
        quiz.randomOrder = randomOrder;
        quiz.activate();
        quiz.markAsNotDeleted();

        return quiz;
    }

    private static void validateCreation(
            Module module,
            String name,
            Integer qtdQuestions,
            BigDecimal minScore,
            Integer maxAttempts
    ) {
        if(module == null) throw new ValidationException("Module is required.");

        if(name == null || name.isBlank()) throw new ValidationException("Quiz name is required.");

        if(qtdQuestions == null || qtdQuestions < 1) throw new ValidationException("Quiz must have at least one question.");

        if(minScore == null || minScore.compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("Minimum score cannot be negative.");

        if(maxAttempts != null && maxAttempts < 1) throw new ValidationException("Max attempts must be at least 1.");
    }

    public void update(
            String name,
            String description,
            String instructions,
            Integer qtdQuestions,
            BigDecimal minScore,
            Integer maxAttempts,
            Boolean randomOrder,
            String observations
    ) {
        if(isDeleted()) throw new ForbiddenOperationException("Deleted quiz cannot be modified.");

        if(!isActive()) throw new InvalidStateException("Inactive quiz cannot be modified.");

        if(name != null && name.isBlank()) throw new ValidationException("Quiz name cannot be blank.");

        if(qtdQuestions != null && qtdQuestions < 1) throw new ValidationException("Quiz must have at least one question.");

        if(minScore != null && minScore.signum() < 0) throw new ValidationException("Minimum score cannot be negative.");

        if(maxAttempts != null && maxAttempts < 1) throw new ValidationException("Max attempts must be at least 1.");

        if(name != null) this.name = name;
        if(description != null) this.description = description;
        if(instructions != null) this.instructions = instructions;
        if(qtdQuestions != null) this.qtdQuestions = qtdQuestions;
        if(minScore != null) this.minScore = minScore;
        if(maxAttempts != null) this.maxAttempts = maxAttempts;
        if(randomOrder != null) this.randomOrder = randomOrder;
        if(observations != null) this.observations = observations;
    }

    public void changeConfiguration(String name, String description, String instructions, Integer maxAttempts, boolean randomOrder) {
        if(!isActive()) throw new ForbiddenOperationException("Inactive quiz cannot be modified.");

        if(isDeleted()) throw new ForbiddenOperationException("Deleted quiz cannot be modified.");

        if(name != null && name.isBlank()) throw new ValidationException("Quiz name cannot be blank.");

        if(maxAttempts != null && maxAttempts < 1) throw new ValidationException("Max attempts must be at least 1.");

        this.name = name != null ? name : this.name; this.description = description;
        this.instructions = instructions;
        this.maxAttempts = maxAttempts;
        this.randomOrder = randomOrder;
    }

    public void validateAttemptAllowed(int currentAttempts) {
        if(!isActive()) throw new InvalidStateException("Inactive quiz cannot be attempted.");

        if(maxAttempts != null && currentAttempts >= maxAttempts) throw new ForbiddenOperationException("Maximum number of attempts reached.");
    }

    public void auditUpdate(Long actorId, LocalDateTime now) {
        super.auditUpdate(actorId, now);
    }

    public Long getId() { return id; }
    public Module getModule() { return module; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public Integer getQtdQuestions() { return qtdQuestions; }
    public BigDecimal getMinScore() { return minScore; }
    public Integer getMaxAttempts() { return maxAttempts; }
    public boolean isRandomOrder() { return randomOrder; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", moduleId=" + (module != null ? module.getId() : null) +
                ", name='" + name + '\'' +
                ", qtdQuestions=" + qtdQuestions +
                ", minScore=" + minScore +
                ", maxAttempts=" + maxAttempts +
                ", randomOrder=" + randomOrder +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}
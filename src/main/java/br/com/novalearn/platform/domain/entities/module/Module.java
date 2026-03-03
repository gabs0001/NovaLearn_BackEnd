package br.com.novalearn.platform.domain.entities.module;

import br.com.novalearn.platform.core.exception.business.InvalidStateException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(
        name = "T_SINL_MODULE",
        indexes = {
                @Index(name = "IDX_T_SINL_MODULE_COURSE", columnList = "cod_course")
        }
)
@SequenceGenerator(
        name = DatabaseSequences.MODULE_SEQ,
        sequenceName = DatabaseSequences.MODULE_SEQ,
        allocationSize = 1
)
public class Module extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.MODULE_SEQ)
    @Column(name = "cod_module", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_course", nullable = false)
    private Course course;

    @Column(name = "nom_module", length = 120, nullable = false)
    private String name;

    @Column(name = "des_module", length = 255)
    private String description;

    @Column(name = "seq_module", length = 6)
    private Integer sequence;

    @Column(name = "obs_module", length = 500)
    private String observations;

    protected Module() {}

    public static Module create(
            Course course,
            String name,
            String description,
            Integer sequence,
            String observations
    ) {
        if(course == null) throw new ValidationException("Course is required.");

        Module module = new Module();

        module.attachToCourse(course);
        module.defineName(name);
        module.defineDescription(description);
        module.defineSequence(sequence);
        module.defineObservations(observations);

        module.activate();
        module.markAsNotDeleted();

        return module;
    }

    public void attachToCourse(Course course) {
        if(course == null) throw new ValidationException("Course cannot be null.");
        if(this.course != null) throw new ValidationException("Module is already attached to a course.");
        if(!course.isActive() || course.isDeleted()) throw new InvalidStateException("Cannot attach module to inactive or deleted course.");
        this.course = course;
    }

    public void defineName(String name) {
        if(name == null || name.isBlank()) throw new ValidationException("Module name is required.");
        if(name.length() > 120) throw new ValidationException("Module name must have at most 120 characters.");
        this.name = name.trim();
    }

    public void defineDescription(String description) {
        if(description != null && description.length() > 255) throw new ValidationException("Description must have at most 255 characters.");
        this.description = description != null ? description.trim() : null;
    }

    public void defineSequence(Integer sequence) {
        if(sequence != null && sequence < 1) throw new ValidationException("Module sequence must be greater than zero.");
        this.sequence = sequence;
    }

    public void defineObservations(String observations) {
        if(observations != null && observations.length() > 500) throw new ValidationException("Observations must have at most 500 characters.");
        this.observations = observations;
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getSequence() { return sequence; }
    public String getObservations() { return observations; }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", courseId=" + (course != null ? course.getId() : null) +
                ", name='" + name + '\'' +
                ", sequence=" + sequence +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}
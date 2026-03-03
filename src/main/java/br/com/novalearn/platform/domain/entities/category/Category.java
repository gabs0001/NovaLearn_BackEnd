package br.com.novalearn.platform.domain.entities.category;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.infra.database.sequences.DatabaseSequences;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "T_SINL_CATEGORY")
@SequenceGenerator(
        name = DatabaseSequences.CATEGORY_SEQ,
        sequenceName = DatabaseSequences.CATEGORY_SEQ,
        allocationSize = 1
)
public class Category extends BaseEntity {
    @Id
    @Column(name = "cod_category", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DatabaseSequences.CATEGORY_SEQ)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_parent_category")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    private List<Category> subcategories;

    @Column(name = "nom_category", length = 120, nullable = false)
    private String name;

    @Column(name = "sig_category", length = 10)
    private String abbreviation;

    @Column(name = "des_category", length = 255)
    private String description;

    @Column(name = "obs_category", length = 500)
    private String observations;

    public Category() {}

    public static Category create(
            String name,
            String abbreviation,
            String description,
            String observations
    ) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Category name is required.");
        }

        Category category = new Category();
        category.name = name.trim();
        category.abbreviation = abbreviation;
        category.description = description;
        category.observations = observations;
        category.active = true;
        category.deleted = false;

        return category;
    }

    public void defineParent(Category parent) {
        if(parent == null) {
            this.parentCategory = null;
            return;
        }

        if(parent == this) throw new ValidationException("Category cannot be parent of itself.");
        if(createsCycle(parent)) throw new ValidationException("Category hierarchy cycle detected.");

        if(!parent.isActive() || parent.isDeleted()) throw new ValidationException("Parent category must be active.");
        this.parentCategory = parent;
    }

    private boolean createsCycle(Category newParent) {
        Category current = newParent;

        while(current != null) {
            if(current == this) return true;
            current = current.parentCategory;
        }

        return false;
    }

    public boolean isRoot() { return parentCategory == null; }
    public boolean isLeaf() { return subcategories == null || subcategories.isEmpty(); }

    public Long getId() { return id; }
    public Category getParentCategory() { return parentCategory; }
    public Long getParentCategoryId() {
        return parentCategory != null ? parentCategory.getId() : null;
    }
    public List<Category> getSubcategories() { return subcategories; }
    public String getName() { return name; }
    public String getAbbreviation() { return abbreviation; }
    public String getDescription() { return description; }
    public String getObservations() { return observations; }

    public void setId(Long id) { this.id = id; }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public void setSubcategories(List<Category> subcategories) {
        this.subcategories = subcategories;
    }

    public void setName(String name) { this.name = name; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public void setDescription(String description) { this.description = description; }
    public void setObservations(String observations) { this.observations = observations; }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", parentCategoryId=" + (parentCategory != null ? parentCategory.getId() : null) +
                ", subcategoriesCount=" + (subcategories != null ? subcategories.size() : 0) +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", active=" + isActive() +
                ", deleted=" + isDeleted() +
                '}';
    }
}
package br.com.novalearn.platform.api.dtos.module.progress;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleProgressResponseDTO {
    private Long moduleId;
    private Long courseId;
    private String name;
    private Integer totalLessons;
    private Integer completedLessons;
    private Integer progressPercent;

    public ModuleProgressResponseDTO() {}

    public ModuleProgressResponseDTO(
            Long moduleId,
            Long courseId,
            String name,
            Integer totalLessons,
            Integer completedLessons,
            Integer progressPercent
    ) {
        this.moduleId = moduleId;
        this.courseId = courseId;
        this.name = name;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.progressPercent = progressPercent;
    }

    public Long getModuleId() { return moduleId; }
    public Long getCourseId() { return courseId; }
    public String getName() { return name; }
    public Integer getTotalLessons() { return totalLessons; }
    public Integer getCompletedLessons() { return completedLessons; }
    public Integer getProgressPercent() { return progressPercent; }

    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setName(String name) { this.name = name; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }
    public void setCompletedLessons(Integer completedLessons) { this.completedLessons = completedLessons; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
}
package br.com.novalearn.platform.api.dtos.lesson.content;

import br.com.novalearn.platform.api.dtos.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonContentResponseDTO extends BaseDTO {
    private Long lessonId;
    private String videoUrl;
    private String transcriptUrl;
    private String materialUrl;
    private String content;
    private Boolean hasQuiz;
    private Boolean mainContent;
    private Long createdBy;
    private Long updatedBy;

    public LessonContentResponseDTO() {}

    public LessonContentResponseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations,
            Long lessonId,
            String videoUrl,
            String transcriptUrl,
            String materialUrl,
            String content,
            Boolean hasQuiz,
            Boolean mainContent,
            Long createdBy,
            Long updatedBy
    ) {
        super(id, active, deleted, createdAt, updatedAt, observations);
        this.lessonId = lessonId;
        this.videoUrl = videoUrl;
        this.transcriptUrl = transcriptUrl;
        this.materialUrl = materialUrl;
        this.content = content;
        this.hasQuiz = hasQuiz;
        this.mainContent = mainContent;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getLessonId() { return lessonId; }
    public String getVideoUrl() { return videoUrl; }
    public String getTranscriptUrl() { return transcriptUrl; }
    public String getMaterialUrl() { return materialUrl; }
    public String getContent() { return content; }
    public Boolean getHasQuiz() { return hasQuiz; }
    public Boolean getMainContent() { return mainContent; }
    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }

    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public void setTranscriptUrl(String transcriptUrl) { this.transcriptUrl = transcriptUrl; }
    public void setMaterialUrl(String materialUrl) { this.materialUrl = materialUrl; }
    public void setContent(String content) { this.content = content; }
    public void setHasQuiz(Boolean hasQuiz) { this.hasQuiz = hasQuiz; }
    public void setMainContent(Boolean mainContent) { this.mainContent = mainContent; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}
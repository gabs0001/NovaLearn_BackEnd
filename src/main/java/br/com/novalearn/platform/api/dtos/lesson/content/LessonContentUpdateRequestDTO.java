package br.com.novalearn.platform.api.dtos.lesson.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonContentUpdateRequestDTO {
    private Long lessonId;

    @URL(message = "videoUrl must be a valid URL")
    @Size(max = 500, message = "videoUrl must have a maximum of 500 characters")
    private String videoUrl;

    @URL(message = "transcriptUrl must be a valid URL")
    @Size(max = 500, message = "transcriptUrl must have a maximum of 500 characters")
    private String transcriptUrl;

    @URL(message = "materialUrl must be a valid URL")
    @Size(max = 500, message = "materialUrl must have a maximum of 500 characters")
    private String materialUrl;

    @Size(min = 3, max = 5000, message = "content must be between 3 and 5000 characters")
    private String content;

    private Boolean hasQuiz;
    private Boolean mainContent;
    private Boolean active;
    private Boolean deleted;

    @Size(max = 500, message = "Observations must have a maximum of 500 characters")
    private String observations;

    public LessonContentUpdateRequestDTO() {}

    public Long getLessonId() { return lessonId; }
    public String getVideoUrl() { return videoUrl; }
    public String getTranscriptUrl() { return transcriptUrl; }
    public String getMaterialUrl() { return materialUrl; }
    public String getContent() { return content; }
    public Boolean getHasQuiz() { return hasQuiz; }
    public Boolean getMainContent() { return mainContent; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }
    public String getObservations() { return observations; }

    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public void setTranscriptUrl(String transcriptUrl) { this.transcriptUrl = transcriptUrl; }
    public void setMaterialUrl(String materialUrl) { this.materialUrl = materialUrl; }
    public void setContent(String content) { this.content = content; }
    public void setHasQuiz(Boolean hasQuiz) { this.hasQuiz = hasQuiz; }
    public void setMainContent(Boolean mainContent) { this.mainContent = mainContent; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public void setObservations(String observations) { this.observations = observations; }
}
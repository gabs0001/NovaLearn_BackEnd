package br.com.novalearn.platform.api.dtos.lesson.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonContentCreateRequestDTO {
    @URL(message = "videoUrl must be a valid URL")
    @Size(max = 500, message = "videoUrl must have a maximum of 500 characters")
    private String videoUrl;

    @URL(message = "transcriptUrl must be a valid URL")
    @Size(max = 500, message = "transcriptUrl must have a maximum of 500 characters")
    private String transcriptUrl;

    @URL(message = "materialUrl must be a valid URL")
    @Size(max = 500, message = "materialUrl must have a maximum of 500 characters")
    private String materialUrl;

    @NotBlank(message = "content is required")
    @Size(min = 3, max = 5000, message = "content must be between 3 and 5000 characters")
    private String content;

    @NotNull(message = "hasQuiz is required")
    private Boolean hasQuiz;

    @NotNull(message = "mainContent is required")
    private Boolean mainContent;

    @Size(max = 500, message = "Observations must have a maximum of 500 characters")
    private String observations;

    public LessonContentCreateRequestDTO() {}

    public LessonContentCreateRequestDTO(
            String videoUrl,
            String transcriptUrl,
            String materialUrl,
            String content,
            Boolean hasQuiz,
            Boolean mainContent,
            String observations
    ) {
        this.videoUrl = videoUrl;
        this.transcriptUrl = transcriptUrl;
        this.materialUrl = materialUrl;
        this.content = content;
        this.hasQuiz = hasQuiz;
        this.mainContent = mainContent;
        this.observations = observations;
    }

    public String getVideoUrl() { return videoUrl; }
    public String getTranscriptUrl() { return transcriptUrl; }
    public String getMaterialUrl() { return materialUrl; }
    public String getContent() { return content; }
    public Boolean getHasQuiz() { return hasQuiz; }
    public Boolean getMainContent() { return mainContent; }
    public String getObservations() { return observations; }

    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public void setTranscriptUrl(String transcriptUrl) { this.transcriptUrl = transcriptUrl; }
    public void setMaterialUrl(String materialUrl) { this.materialUrl = materialUrl; }
    public void setContent(String content) { this.content = content; }
    public void setHasQuiz(Boolean hasQuiz) { this.hasQuiz = hasQuiz; }
    public void setMainContent(Boolean mainContent) { this.mainContent = mainContent; }
    public void setObservations(String observations) { this.observations = observations; }
}
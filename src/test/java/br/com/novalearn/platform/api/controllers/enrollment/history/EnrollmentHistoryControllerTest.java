package br.com.novalearn.platform.api.controllers.enrollment.history;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.enrollment.history.EnrollmentHistoryResponseDTO;
import br.com.novalearn.platform.domain.services.enrollment.history.EnrollmentHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static br.com.novalearn.platform.factories.dtos.enrollment.EnrollmentHistoryTestFactory.historyResponse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentHistoryController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class EnrollmentHistoryControllerTest extends BaseControllerTest {
    @MockitoBean
    private EnrollmentHistoryService enrollmentHistoryService;

    @Test
    void should_return_history_when_get_all() throws Exception {
        when(enrollmentHistoryService.getEnrollmentHistory()).thenReturn(List.of(historyResponse()));

        mockMvc.perform(get("/api/me/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(1L))
                .andExpect(jsonPath("$[0].courseName").value("Java Fundamentals"))
                .andExpect(jsonPath("$[0].enrollmentStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].progressPercent").value(5));

        verify(enrollmentHistoryService).getEnrollmentHistory();
    }

    @Test
    void should_return_history_when_get_completed() throws Exception {
        EnrollmentHistoryResponseDTO dto = historyResponse();

        when(enrollmentHistoryService.getCompletedHistory()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/me/history/completed")).andExpect(status().isOk());

        verify(enrollmentHistoryService).getCompletedHistory();
    }

    @Test
    void should_return_history_when_get_canceled() throws Exception {
        EnrollmentHistoryResponseDTO dto = historyResponse();

        when(enrollmentHistoryService.getCanceledHistory()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/me/history/canceled")).andExpect(status().isOk());

        verify(enrollmentHistoryService).getCanceledHistory();
    }
}
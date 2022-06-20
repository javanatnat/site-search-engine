package ru.javanatnat.sitesearchengine.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.javanatnat.sitesearchengine.service.StatisticsService;
import ru.javanatnat.sitesearchengine.service.statistics.GetStatException;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticController.class)
public class StatisticsControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private StatisticsService service;

    @Test
    void testGetStatistics() throws Exception {
        when(service.getStatistics()).thenReturn(Map.of("result", true));
        mvc.perform(get("/api/statistics/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value(true));
    }

    @Test
    void testGetStatisticsError() throws Exception {
        when(service.getStatistics()).thenThrow(new GetStatException());
        mvc.perform(get("/api/statistics/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}

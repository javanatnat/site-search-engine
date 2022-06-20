package ru.javanatnat.sitesearchengine.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.javanatnat.sitesearchengine.service.IndexService;
import ru.javanatnat.sitesearchengine.service.index.IndexSiteException;
import ru.javanatnat.sitesearchengine.service.index.IndexStopErrorException;
import ru.javanatnat.sitesearchengine.service.index.PageOutOfBoundsException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndexController.class)
public class IndexControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private IndexService service;

    private final ObjectMapper mapper = new JsonMapper();

    @Test
    void testStartFullIndex() throws Exception {
        doNothing().when(service).fullIndex();

        mvc.perform(post("/api/index/startIndexing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value(true));
    }

    @Test
    void testStartFullIndexError() throws Exception {
        doThrow(new IndexSiteException("error msg")).when(service).fullIndex();
        mvc.perform(post("/api/index/startIndexing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("result").value(false))
                .andExpect(jsonPath("error").value("Ошибка индексации"));
    }

    @Test
    void testStopIndexing() throws Exception {
        doNothing().when(service).stopIndex();

        mvc.perform(post("/api/index/stopIndexing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value(true));
    }

    @Test
    void testStopIndexingError() throws Exception {
        doThrow(new IndexStopErrorException()).when(service).stopIndex();

        mvc.perform(post("/api/index/stopIndexing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("result").value(false))
                .andExpect(jsonPath("error").value("Индексация не запущена"));
    }

    @Test
    void testIndexPage() throws Exception {
        doNothing().when(service).indexPage(anyString());

        var params = Map.of(
                "url", "pageUrl"
        );

        mvc.perform(post("/api/index/indexPage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value(true));
    }

    @Test
    void testIndexPageErrorBounds() throws Exception {
        doThrow(new PageOutOfBoundsException()).when(service).indexPage(anyString());

        mvc.perform(post("/api/index/indexPage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("result").value(false))
                .andExpect(jsonPath("error")
                        .value("Ошибка индексации"));
    }

    @Test
    void testIndexPageErrorIndex() throws Exception {
        doThrow(new IndexSiteException("error msg")).when(service).indexPage(anyString());

        mvc.perform(post("/api/index/indexPage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("result").value(false))
                .andExpect(jsonPath("error")
                        .value("Ошибка индексации"));
    }
}

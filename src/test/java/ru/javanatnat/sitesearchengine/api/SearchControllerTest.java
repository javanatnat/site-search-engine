package ru.javanatnat.sitesearchengine.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.javanatnat.sitesearchengine.service.SearchService;
import ru.javanatnat.sitesearchengine.service.search.SearchRequest;
import ru.javanatnat.sitesearchengine.service.search.SearchResponse;
import ru.javanatnat.sitesearchengine.service.search.SearchSiteException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private SearchService service;

    private final ObjectMapper mapper = new JsonMapper();

    @Test
    void testSearch() throws Exception {
        SearchResponse searchResponse = new SearchResponse(1);
        when(service.search(any(SearchRequest.class))).thenReturn(searchResponse);

        var params = Map.of(
                "query", "omega",
                "site", "https://site.ru"
        );

        mvc.perform(get("/api/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").value(1))
                .andExpect(jsonPath("result").value(true));
    }

    @Test
    void testSearchError() throws Exception {
        String errorMsg = "Error: SearchSiteException";
        when(service.search(any(SearchRequest.class)))
                .thenThrow(new SearchSiteException(errorMsg));

        var params = Map.of(
                "query", "omega",
                "site", "https://site.ru"
        );

        mvc.perform(get("/api/search/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(params)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("result").value(false))
                .andExpect(jsonPath("error").value(errorMsg));
    }
}

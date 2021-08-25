package com.example.demo.apps.movie.controller.detail;

import com.example.demo.apps.common.UriConstant;
import com.example.demo.apps.config.ControllerIntegrationTestConfig;
import com.example.demo.apps.response.MovieDetailRESTResponse;
import com.example.demo.core.movie.readmodel.MovieReadModel;
import com.example.demo.core.movie.usecase.detail.MovieDetailRequestModel;
import com.example.demo.persistence.movie.MovieGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static com.google.common.truth.Truth.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author fathyaff
 * @date 15/08/21 01.56
 */
@WebMvcTest(MovieDetailController.class)
@Import(ControllerIntegrationTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MovieDetailControllerIntegrationTest {

    private static final String MOVIE_NAME = "MOVIE NAME";

    private static final String MOVIE_DESC = "MOVIE DESC";

    private static final int DURATIONS = 180;

    private static final String MOVIE_ID = "ID";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieGateway movieGateway;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MovieDetailRequestModel requestModel;

    private MovieReadModel movieReadModel;

    @BeforeEach
    void setUp() {
        movieGateway.save(getMovieReadModel());
        movieReadModel = movieGateway.findAll().stream().findFirst().orElse(getMovieReadModel());
        requestModel = MovieDetailRequestModel.valueOf(movieReadModel.getId());
    }

    @Test
    @SneakyThrows
    void givenMovieId_whenDetail_shouldReturnCorrectResponse() {
        String uri = String.format("%s%s", UriConstant.DEFAULT, UriConstant.DETAIL_MOVIE);
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("movieId", requestModel.getMovieId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        MovieDetailRESTResponse restResponse = objectMapper.readValue(response, MovieDetailRESTResponse.class);
        assertThat(restResponse.getId()).isEqualTo(movieReadModel.getId());
        assertThat(restResponse.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(restResponse.getMovieDescription()).isEqualTo(MOVIE_DESC);
        assertThat(restResponse.getDurations()).isEqualTo(DURATIONS);
    }

    private MovieReadModel getMovieReadModel() {
        MovieReadModel movie = new MovieReadModel();
        movie.setId(MOVIE_ID);
        movie.setMovieName(MOVIE_NAME);
        movie.setMovieDescription(MOVIE_DESC);
        movie.setDurations(DURATIONS);
        return movie;
    }

}

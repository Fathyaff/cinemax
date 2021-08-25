package com.example.demo.apps.movie.controller.detail;

import com.example.demo.apps.response.MovieDetailRESTResponse;
import com.example.demo.core.movie.readmodel.MovieReadModel;
import com.example.demo.core.movie.usecase.detail.MovieDetailInputBoundary;
import com.example.demo.core.movie.usecase.detail.MovieDetailOutputBoundary;
import com.example.demo.core.movie.usecase.detail.MovieDetailRequestModel;
import com.example.demo.core.movie.usecase.detail.MovieDetailResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author fathyaff
 * @date 15/08/21 01.38
 */
class MovieDetailControllerTest {

    private static final String MOVIE_NAME = "MOVIE NAME";

    private static final String MOVIE_DESC = "MOVIE DESC";

    private static final int DURATIONS = 180;

    private static final String MOVIE_ID = "ID";

    private MovieDetailController controller;

    private MovieDetailInputBoundary useCase;

    private MovieDetailRequestModel requestModel;

    @BeforeEach
    void setUp() {
        useCase = mock(MovieDetailInputBoundary.class);
        controller = new MovieDetailController(useCase);
        requestModel = MovieDetailRequestModel.valueOf(MOVIE_ID);
        mockUseCase();
    }

    @Test
    void givenValidRequest_whenDetail_shouldCallUseCase() {
        controller.detail(requestModel);
        verify(useCase, times(1)).detail(eq(requestModel), any());
    }

    @Test
    void givenValidRequest_whenDetail_shouldConstructCorrectRestResponse() {
        MovieDetailRESTResponse response = controller.detail(requestModel);
        assertThat(response.getId()).isEqualTo(MOVIE_ID);
        assertThat(response.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(response.getMovieDescription()).isEqualTo(MOVIE_DESC);
        assertThat(response.getDurations()).isEqualTo(DURATIONS);
    }

    private void mockUseCase() {
        Answer<Void> answer = this::mockResponse;
        doAnswer(answer).when(useCase).detail(any(), any());
    }

    private Void mockResponse(InvocationOnMock invocationOnMock) {
        MovieDetailOutputBoundary presenter = invocationOnMock.getArgument(1);
        MovieReadModel movie = getMovieReadModel();
        MovieDetailResponseModel response = MovieDetailResponseModel.valueOf(movie);
        presenter.present(response);
        return null;
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

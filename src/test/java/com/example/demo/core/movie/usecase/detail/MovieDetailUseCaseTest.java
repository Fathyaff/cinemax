package com.example.demo.core.movie.usecase.detail;

import com.example.demo.core.movie.readmodel.MovieReadModel;
import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.persistence.movie.MovieGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author fathyaff
 * @date 15/08/21 00.34
 */
class MovieDetailUseCaseTest {

    private static final String MOVIE_NAME = "MOVIE NAME";

    private static final String MOVIE_DESC = "MOVIE DESC";

    private static final int DURATIONS = 180;

    private static final String MOVIE_ID = "ID";

    private MovieGateway movieGateway;

    private MovieDetailInputBoundary useCase;

    private MovieDetailOutputBoundary presenter;

    private MovieDetailRequestModel requestModel;

    @BeforeEach
    void setUp() {
        movieGateway = mock(MovieGateway.class);
        presenter = mock(MovieDetailOutputBoundary.class);
        useCase = new MovieDetailUseCase(movieGateway);
        requestModel = MovieDetailRequestModel.valueOf(MOVIE_ID);
        mockMovie();
    }

    @Test
    void givenNullRequest_whenDetailMovie_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> useCase.detail(null, presenter));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Request cannot be null");
    }

    @Test
    void givenNullPresenter_whenDetailMovie_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> useCase.detail(requestModel, null));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Presenter cannot be null");
    }

    @Test
    void givenNullOrEmptyMovieId_whenDetailMovie_shouldThrowException() {
        requestModel.setMovieId(null);
        Exception exception = assertThrows(Exception.class, () -> useCase.detail(requestModel, presenter));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Movie ID cannot be null or empty");
    }

    @Test
    void givenValidRequest_whenDetailMovie_shouldCallMovieGateway() {
        useCase.detail(requestModel, presenter);
        verify(movieGateway, times(1)).findById(eq(MOVIE_ID));
    }

    @Test
    void givenValidRequest_whenDetailMovie_shouldCallPresenter() {
        useCase.detail(requestModel, presenter);
        verify(presenter, times(1)).present(any());
    }

    @Test
    void givenValidRequest_whenDetailMovie_shouldPresentCorrectResponse() {
        AtomicReference<MovieDetailResponseModel> response = new AtomicReference<>();
        useCase.detail(requestModel, response::set);
        assertMovieResponse(response.get());
    }

    private void assertMovieResponse(MovieDetailResponseModel responseModel) {
        assertThat(responseModel.getId()).isEqualTo(MOVIE_ID);
        assertThat(responseModel.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(responseModel.getMovieDescription()).isEqualTo(MOVIE_DESC);
        assertThat(responseModel.getDurations()).isEqualTo(DURATIONS);
    }

    private void mockMovie() {
        MovieReadModel movie = getMovieReadModel();
        when(movieGateway.findById(eq(MOVIE_ID))).thenReturn(movie);
    }

    private MovieReadModel getMovieReadModel() {
        return new MovieReadModel(MOVIE_ID, MOVIE_NAME, MOVIE_DESC, DURATIONS);
    }
}

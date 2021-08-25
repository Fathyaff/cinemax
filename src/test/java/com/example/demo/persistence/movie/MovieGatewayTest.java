package com.example.demo.persistence.movie;

import com.example.demo.core.movie.readmodel.MovieReadModel;
import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.core.exception.NoDataFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author fathyaff
 * @date 15/08/21 00.07
 */
class MovieGatewayTest {

    private static final String MOVIE_NAME = "MOVIE NAME";
    private static final String MOVIE_DESC = "MOVIE DESC";
    private static final int DURATIONS = 180;
    private static final String MOVIE_ID = "ID";
    public static final String DUMMY = "DUMMY";

    private MovieRepository movieRepository;

    private MovieGateway gateway;

    @BeforeEach
    void setUp() {
        movieRepository = Mockito.mock(MovieRepository.class);
        gateway = new JPAMovieGateway(movieRepository);
        when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(getMovieReadModel()));
        when(movieRepository.findAll()).thenReturn(Collections.singletonList(getMovieReadModel()));
    }

    @Test
    void givenNull_whenSave_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> gateway.save(null));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Movie cannot be null");
    }

    @Test
    void givenMovie_whenSave_shouldCallSave() {
        MovieReadModel movie = getMovieReadModel();
        gateway.save(movie);
        verify(movieRepository, times(1)).save(any());
    }

    @Test
    void givenMovie_whenSave_shouldSaveCorrectData() {
        MovieReadModel movie = getMovieReadModel();
        gateway.save(movie);

        ArgumentCaptor<MovieReadModel> modelArgumentCaptor = ArgumentCaptor.forClass(MovieReadModel.class);
        verify(movieRepository, times(1)).save(modelArgumentCaptor.capture());

        MovieReadModel savedMovie = modelArgumentCaptor.getValue();
        assertMovie(savedMovie);
    }

    @Test
    void givenNullOrEmpty_whenFindById_shouldCallFindById() {
        Exception exception = assertThrows(Exception.class, () -> gateway.findById(null));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Movie ID cannot be null or empty");
    }

    @Test
    void givenValidRequestBuNoDataFound_whenFindById_shouldThrowException() {
        when(movieRepository.findById(DUMMY)).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> gateway.findById(DUMMY));
        assertThat(exception).isInstanceOf(NoDataFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("No movie found for ID %s", DUMMY));
    }

    @Test
    void givenValidRequest_whenFindById_shouldCallFindById() {
        MovieReadModel savedMovie = gateway.findById(MOVIE_ID);
        verify(movieRepository, times(1)).findById(eq(MOVIE_ID));
    }

    @Test
    void givenValidRequest_whenFindById_shouldCallFindByAll() {
        gateway.findAll();
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void givenValidRequest_whenFindById_shouldReturnCorrectData() {
        List<MovieReadModel> savedMovies = gateway.findAll();
        assertThat(savedMovies).hasSize(1);
        MovieReadModel savedMovie = savedMovies.get(0);
        assertMovie(savedMovie);
    }

    private MovieReadModel getMovieReadModel() {
        MovieReadModel movie = new MovieReadModel();
        movie.setId(MOVIE_ID);
        movie.setMovieName(MOVIE_NAME);
        movie.setMovieDescription(MOVIE_DESC);
        movie.setDurations(DURATIONS);
        return movie;
    }

    private void assertMovie(MovieReadModel savedMovie) {
        assertThat(savedMovie.getId()).isEqualTo(MOVIE_ID);
        assertThat(savedMovie.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(savedMovie.getMovieDescription()).isEqualTo(MOVIE_DESC);
        assertThat(savedMovie.getDurations()).isEqualTo(DURATIONS);
    }
}

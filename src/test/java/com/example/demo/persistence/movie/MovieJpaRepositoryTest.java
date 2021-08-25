package com.example.demo.persistence.movie;

import com.example.demo.apps.config.H2JPAConfig;
import com.example.demo.core.movie.readmodel.MovieReadModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author fathyaff
 * @date 14/08/21 23.22
 */
@SpringBootTest(classes = H2JPAConfig.class)
class MovieJpaRepositoryTest {

    private static final String MOVIE_ID = "ID";
    private static final String MOVIE_NAME = "MOVIE NAME";
    private static final String MOVIE_DESC = "MOVIE DESC";
    private static final int DURATIONS = 180;

    @Autowired
    MovieRepository repository;

    @Test
    void givenMovie_whenSave_shouldSaveCorrectly() {
        MovieReadModel movieReadModel = getMovieReadModel();
        repository.save(movieReadModel);
        List<MovieReadModel> savedMovies = repository.findAll();
        assertThat(savedMovies).hasSize(1);
        MovieReadModel savedMovie = savedMovies.get(0);
        assertThat(savedMovie.getId()).isNotEmpty();
        assertThat(savedMovie.getDurations()).isEqualTo(DURATIONS);
        assertThat(savedMovie.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(savedMovie.getMovieDescription()).isEqualTo(MOVIE_DESC);
    }

    private MovieReadModel getMovieReadModel() {
        MovieReadModel movieReadModel = new MovieReadModel();
        movieReadModel.setId(MOVIE_ID);
        movieReadModel.setMovieName(MOVIE_NAME);
        movieReadModel.setMovieDescription(MOVIE_DESC);
        movieReadModel.setDurations(DURATIONS);
        return movieReadModel;
    }

}

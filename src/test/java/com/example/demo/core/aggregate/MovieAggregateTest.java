package com.example.demo.core.aggregate;

import com.example.demo.core.movie.aggregate.Movie;
import com.example.demo.core.movie.aggregate.MovieCreatedEvent;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author fathyaff
 * @date 15/08/21 23.44
 */
class MovieAggregateTest {
    private static final String MOVIE_NAME = "MOVIE NAME";

    private static final String MOVIE_DESC = "MOVIE DESC";

    private static final int DURATIONS = 180;

    private static final String MOVIE_ID = "ID";

    private final Movie movie = new Movie();

    @Test
    void givenCreated_whenApply_shouldSetCorrectAggregate() {
        MovieCreatedEvent event = getMovieCreatedEvent();
        movie.apply(event);

        assertThat(movie.getId()).isEqualTo(MOVIE_ID);
        assertThat(movie.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(movie.getMovieDescription()).isEqualTo(MOVIE_DESC);
    }

    private MovieCreatedEvent getMovieCreatedEvent() {
        MovieCreatedEvent event = new MovieCreatedEvent();
        event.setId(MOVIE_ID);
        event.setMovieName(MOVIE_NAME);
        event.setMovieDescription(MOVIE_DESC);
        event.setDurations(DURATIONS);
        return event;
    }
}

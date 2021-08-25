package com.example.demo.core.movie.aggregate;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fathyaff
 * @date 15/08/21 23.37
 */
@Setter
@Getter
@NoArgsConstructor
public class Movie {

    private String id;

    private String movieName;

    private String movieDescription;

    private int durations;

    public void apply(MovieCreatedEvent event) {
        setId(event.getId());
        setMovieName(event.getMovieName());
        setMovieDescription(event.getMovieDescription());
        setDurations(event.getDurations());
    }

}

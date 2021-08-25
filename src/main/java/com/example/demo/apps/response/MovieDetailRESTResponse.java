package com.example.demo.apps.response;

import com.example.demo.core.movie.usecase.detail.MovieDetailResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fathyaff
 * @date 15/08/21 01.45
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailRESTResponse {
    private String id;

    private String movieName;

    private String movieDescription;

    private int durations;

    public static MovieDetailRESTResponse valueOf(MovieDetailResponseModel movie) {
        return MovieDetailRESTResponse.builder()
                .id(movie.getId())
                .movieName(movie.getMovieName())
                .movieDescription(movie.getMovieDescription())
                .durations(movie.getDurations()).build();
    }
}

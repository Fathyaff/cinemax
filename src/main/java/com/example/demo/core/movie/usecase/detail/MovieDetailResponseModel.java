package com.example.demo.core.movie.usecase.detail;

import com.example.demo.core.movie.readmodel.MovieReadModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fathyaff
 * @date 15/08/21 01.17
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailResponseModel {

    private String id;

    private String movieName;

    private String movieDescription;

    private int durations;

    public static MovieDetailResponseModel valueOf(MovieReadModel movie) {
        return MovieDetailResponseModel.builder()
                .id(movie.getId())
                .movieName(movie.getMovieName())
                .movieDescription(movie.getMovieDescription())
                .durations(movie.getDurations()).build();
    }
}

package com.example.demo.core.movie.aggregate;

import com.example.demo.core.common.Event;
import com.example.demo.core.movie.usecase.create.MovieCreationRequestModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fathyaff
 * @date 15/08/21 23.35
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCreatedEvent extends Event {

    private String id;

    private String movieName;

    private String movieDescription;

    private int durations;

    public static MovieCreatedEvent valueOf(String movieId, MovieCreationRequestModel requestModel) {
        MovieCreatedEvent event = MovieCreatedEvent.builder().id(movieId).movieName(requestModel.getMovieName())
                .movieDescription(requestModel.getMovieDescription())
                .durations(requestModel.getDurations()).build();
        event.setUnixTimestamp(System.currentTimeMillis());
        return event;
    }
}

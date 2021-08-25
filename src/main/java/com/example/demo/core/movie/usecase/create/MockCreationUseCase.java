package com.example.demo.core.movie.usecase.create;

import advotics.shared.eventsourcing.wrapper.TransactionalWrapper;
import com.example.demo.core.common.Event;
import com.example.demo.core.gateway.EventSourceGateway;
import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.core.movie.aggregate.Movie;
import com.example.demo.core.movie.aggregate.MovieCreatedEvent;
import com.example.demo.core.util.Publisher;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.UUID;

/**
 * @author fathyaff
 * @date 15/08/21 15.07
 */
@RequiredArgsConstructor
public class MockCreationUseCase implements MovieCreationInputBoundary {

    private final EventSourceGateway eventSourceGateway;

    private final TransactionalWrapper transactionalWrapper;

    @Override
    public void create(MovieCreationRequestModel requestModel, MovieCreationOutputBoundary presenter) {
        Publisher publisher = Publisher.get(eventSourceGateway);
        transactionalWrapper.doInTransaction(() -> executeUseCase(requestModel, presenter, publisher));
        publisher.publishAndTrack(publisher.getEventIds());
    }

    private void executeUseCase(MovieCreationRequestModel requestModel, MovieCreationOutputBoundary presenter,
                                Publisher publisher) {
        validate(requestModel, presenter);
        String movieId = UUID.randomUUID().toString();
        MovieCreatedEvent event = MovieCreatedEvent.valueOf(movieId, requestModel);
        publisher.addEvent(movieId, Movie.class, event, requestModel.getTopic());
        publisher.save();
    }

    private void validate(MovieCreationRequestModel requestModel, MovieCreationOutputBoundary presenter) {
        if(Objects.isNull(requestModel)) {
            throw new InvalidRequestException("Request cannot be null");
        }

        if(Objects.isNull(presenter)) {
            throw new InvalidRequestException("Presenter cannot be null");
        }

        requestModel.validate();
    }
}

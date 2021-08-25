package com.example.demo.core.movie.usecase.create;

import advotics.shared.eventsourcing.EventSourcingMetaData;
import advotics.shared.eventsourcing.wrapper.DefaultTransactionalWrapper;
import advotics.shared.eventsourcing.wrapper.TransactionalWrapper;
import com.example.demo.core.exception.InvalidRequestException;
import com.example.demo.core.gateway.EventSourceGateway;
import com.example.demo.core.movie.aggregate.Movie;
import com.example.demo.core.movie.aggregate.MovieCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author fathyaff
 * @date 15/08/21 14.59
 */
class MovieCreationUseCaseTest {

    private static final String TOPIC = "TOPIC";

    private static final String MOVIE_NAME = "MOVIE NAME";

    private static final String MOVIE_DESC = "MOVIE DESC";

    private static final int DURATIONS = 180;

    private static final String EVENT_ID = "EVENT_ID";

    private EventSourceGateway eventSourceGateway;

    private MovieCreationInputBoundary useCase;

    private MovieCreationOutputBoundary presenter;

    private MovieCreationRequestModel requestModel;

    @BeforeEach
    void setUp() {
        eventSourceGateway = mock(EventSourceGateway.class);
        presenter = mock(MovieCreationOutputBoundary.class);
        TransactionalWrapper transactionalWrapper = new DefaultTransactionalWrapper();
        useCase = new MockCreationUseCase(eventSourceGateway, transactionalWrapper);
        requestModel = new MovieCreationRequestModel(MOVIE_NAME, MOVIE_DESC, DURATIONS, TOPIC);
        when(eventSourceGateway.saveOnly(anyString(), any(), any(), anyString())).thenReturn(EVENT_ID);
        EventSourcingMetaData metaData = getEventSourcingMetaData();
        when(eventSourceGateway.send(anyString(), anyString())).thenReturn(metaData);
    }

    @Test
    void givenNullRequest_whenCreate_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> useCase.create(null, presenter));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Request cannot be null");
    }

    @Test
    void givenNullPresenter_whenCreate_shouldThrowException() {
        Exception exception = assertThrows(Exception.class, () -> useCase.create(requestModel, null));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Presenter cannot be null");
    }

    @Test
    void givenNullMovieName_whenCreate_shouldThrowException() {
        requestModel.setMovieName(null);
        Exception exception = assertThrows(Exception.class, () -> useCase.create(requestModel, presenter));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Movie Name cannot be null or empty");
    }

    @Test
    void givenNullMovieDesc_whenCreate_shouldThrowException() {
        requestModel.setMovieDescription(null);
        Exception exception = assertThrows(Exception.class, () -> useCase.create(requestModel, presenter));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Movie Description cannot be null or empty");
    }

    @Test
    void givenNullDurations_whenCreate_shouldThrowException() {
        requestModel.setDurations(0);
        Exception exception = assertThrows(Exception.class, () -> useCase.create(requestModel, presenter));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Durations is invalid");
    }

    @Test
    void givenNullTopic_whenCreate_shouldThrowException() {
        requestModel.setTopic(null);
        Exception exception = assertThrows(Exception.class, () -> useCase.create(requestModel, presenter));
        assertThat(exception).isInstanceOf(InvalidRequestException.class);
        assertThat(exception.getMessage()).isEqualTo("Topic cannot be null or empty");
    }

    @Test
    void givenRequest_whenCreate_shouldCallESGatewaySave() {
        useCase.create(requestModel, presenter);
        verify(eventSourceGateway, times(1))
                .saveOnly(anyString(), eq(Movie.class), any(), eq(requestModel.getTopic()));
    }

    @Test
    void givenRequest_whenCreate_shouldSetCorrectEvent() {
        useCase.create(requestModel, presenter);
        ArgumentCaptor<MovieCreatedEvent> eventArgumentCaptor = ArgumentCaptor.forClass(MovieCreatedEvent.class);
        verify(eventSourceGateway, times(1))
                .saveOnly(anyString(), eq(Movie.class), eventArgumentCaptor.capture(), eq(requestModel.getTopic()));
        assertEvent(eventArgumentCaptor);
    }

    private void assertEvent(ArgumentCaptor<MovieCreatedEvent> eventArgumentCaptor) {
        MovieCreatedEvent event = eventArgumentCaptor.getValue();
        assertThat(event.getId()).isNotEmpty();
        assertThat(event.getMovieName()).isEqualTo(MOVIE_NAME);
        assertThat(event.getMovieDescription()).isEqualTo(MOVIE_DESC);
        assertThat(event.getDurations()).isEqualTo(DURATIONS);
        assertThat(event.getUnixTimestamp()).isNotNull();
    }

    private EventSourcingMetaData getEventSourcingMetaData() {
        EventSourcingMetaData metaData = new EventSourcingMetaData();
        metaData.setSuccess(true);
        metaData.setTopic(TOPIC);
        return metaData;
    }
}

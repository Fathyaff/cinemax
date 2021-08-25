package com.example.demo.core.util;

import advotics.shared.eventsourcing.EventSourcingMetaData;
import advotics.shared.eventsourcing.exception.FailedToFindTopFailedEventException;
import advotics.shared.eventsourcing.exception.FailedToUpdateStatusEventStoreException;
import advotics.shared.eventsourcing.persistence.EventStore;
import com.example.demo.core.exception.SaveEventStoreException;
import com.example.demo.core.gateway.EventSourceGateway;
import com.example.demo.core.movie.aggregate.Movie;
import com.example.demo.core.movie.aggregate.MovieCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author fathyaff
 * @date 15/08/21 23.32
 */
class PublisherTest {

    private static final String EVENT_ID = "eventId";

    private static final String EVENT_ID_1 = "eventId1";

    private static final String EVENT_ID_2 = "eventId2";

    private static final String TOPIC = "topic";

    private static final String AGGREGATE_ID = "aggregateId";

    private static final String AGGREGATE_ID_1 = "aggregateId1";

    private static final String AGGREGATE_ID_2 = "aggregateId2";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EventSourceGateway esGateway;

    private Publisher publisher;

    private EventSourcingMetaData successMetaData;

    private EventSourcingMetaData failedMetaData;

    @BeforeEach
    void setup() {
        esGateway = mock(EventSourceGateway.class);
        when(esGateway.saveOnly(eq(AGGREGATE_ID), any(), any(), anyString())).thenReturn(EVENT_ID);
        when(esGateway.saveOnly(eq(AGGREGATE_ID_1), any(), any(), anyString())).thenReturn(EVENT_ID_1);
        when(esGateway.saveOnly(eq(AGGREGATE_ID_2), any(), any(), anyString())).thenReturn(EVENT_ID_2);
        publisher = Publisher.get(esGateway);

        setSuccessMetaData();
        setFailedMetaData();
    }

    private void setSuccessMetaData() {
        successMetaData = new EventSourcingMetaData();
        successMetaData.setSuccess(true);
        successMetaData.setTopic("Topic");
    }

    private void setFailedMetaData() {
        failedMetaData = new EventSourcingMetaData();
        failedMetaData.setSuccess(false);
        failedMetaData.setTopic("Topic");
    }

    @Test
    void givenEmptyEventMetaData_whenSaveOnly_shouldNotCallEsGatewaySaveOnly() {
        publisher.save();
        verify(esGateway, times(0)).saveOnly(any(), any(), any(), any());
        assertThat(publisher.getEventIds()).hasSize(0);
    }

    @Test
    void givenOneEventMetaData_whenSaveOnlySuccess_shouldAppendEventIds() {
        MovieCreatedEvent event = addEvent(AGGREGATE_ID);
        publisher.save();
        verify(esGateway, times(1)).saveOnly(any(), eq(Movie.class), eq(event), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(1);
        assertThat(publisher.getSaveEventRedFlag().get()).isFalse();
    }

    @Test
    void givenOneEventMetaData_whenSaveOnlyFailed_shouldNoAppendEventIds() {
        MovieCreatedEvent event = addEvent(AGGREGATE_ID);
        setFailedSaveForAggregateId(AGGREGATE_ID, event);

        assertThrows(Exception.class, () -> publisher.save());
        verify(esGateway, times(1)).saveOnly(any(), eq(Movie.class), eq(event), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(0);
        assertThat(publisher.getSaveEventRedFlag().get()).isTrue();
    }

    @Test
    void givenTwoEventMetaData_whenSaveOnlySuccess_shouldAppendEventIds() {
        addEvent(AGGREGATE_ID);
        addEvent(AGGREGATE_ID_1);
        publisher.save();
        verify(esGateway, times(2)).saveOnly(anyString(), any(), any(), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(2);
        assertThat(publisher.getEventIds()).containsAllIn(Arrays.asList(EVENT_ID, EVENT_ID_1));
        assertThat(publisher.getSaveEventRedFlag().get()).isFalse();
    }

    @Test
    void givenTwoEventMetaData_whenSaveOnlyFailedSecondEvent_shouldCallESGatewaySavedOnlyOnce() {
        addEvent(AGGREGATE_ID);
        MovieCreatedEvent event = addEvent(AGGREGATE_ID_1);
        setFailedSaveForAggregateId(AGGREGATE_ID_1, event);

        assertThrows(Exception.class, () -> publisher.save());
        verify(esGateway, times(2)).saveOnly(anyString(), any(), any(), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(1);
        assertThat(publisher.getEventIds().get(0)).isEqualTo(EVENT_ID);
        assertThat(publisher.getSaveEventRedFlag().get()).isTrue();
    }

    @Test
    void givenThreeEventMetaData_whenSaveOnlyFailedThirdEvent_shouldCallESGatewaySavedOnlyThirdTimes() {
        addEvent(AGGREGATE_ID);
        addEvent(AGGREGATE_ID_1);
        MovieCreatedEvent event = addEvent(AGGREGATE_ID_2);
        setFailedSaveForAggregateId(AGGREGATE_ID_2, event);

        assertThrows(Exception.class, () -> publisher.save());
        verify(esGateway, times(3)).saveOnly(anyString(), any(), any(), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(2);
        assertThat(publisher.getEventIds().get(0)).isEqualTo(EVENT_ID);
        assertThat(publisher.getEventIds().get(1)).isEqualTo(EVENT_ID_1);
        assertThat(publisher.getSaveEventRedFlag().get()).isTrue();
    }

    @Test
    void givenThreeEventMetaData_whenSaveOnlyFailedFirstEvent_shouldOnlyCallESGatewaySavedOnlyOnce() {
        MovieCreatedEvent event = addEvent(AGGREGATE_ID);
        addEvent(AGGREGATE_ID_1);
        addEvent(AGGREGATE_ID_2);
        setFailedSaveForAggregateId(AGGREGATE_ID, event);

        assertThrows(Exception.class, () -> publisher.save());
        verify(esGateway, times(1)).saveOnly(eq(AGGREGATE_ID), any(), any(), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(0);
        assertThat(publisher.getSaveEventRedFlag().get()).isTrue();
    }

    @Test
    void givenSaveEventRedFlag_andEmptyEventIds_whenSave_shouldNotCallDeleteEventStore() {
        MovieCreatedEvent event = addEvent(AGGREGATE_ID);
        setFailedSaveForAggregateId(AGGREGATE_ID, event);

        assertThrows(Exception.class, () -> publisher.save());
        verify(esGateway, times(1)).saveOnly(eq(AGGREGATE_ID), any(), any(), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(0);
        assertThat(publisher.getSaveEventRedFlag().get()).isTrue();

        verify(esGateway, times(0)).deactivate(any());
    }

    @Test
    void givenSaveEventRedFlag_andOneEventIds_whenSave_shouldCallDeleteEventStoreOnce() {
        addEvent(AGGREGATE_ID);
        MovieCreatedEvent event = addEvent(AGGREGATE_ID_1);
        setFailedSaveForAggregateId(AGGREGATE_ID_1, event);

        assertThrows(Exception.class, () -> publisher.save());
        verify(esGateway, times(2)).saveOnly(anyString(), any(), any(), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(1);
        assertThat(publisher.getSaveEventRedFlag().get()).isTrue();

        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(esGateway, times(1)).deactivate(argumentCaptor.capture());
        List<String> eventIds = argumentCaptor.getValue();
        assertThat(eventIds).hasSize(1);
        assertThat(eventIds.get(0)).isEqualTo(EVENT_ID);
    }

    @Test
    void givenSaveEventRedFlag_andTwoEventIds_whenSave_shouldCallDeleteEventStoreWithCorrectEventIds() {
        addEvent(AGGREGATE_ID);
        addEvent(AGGREGATE_ID_1);
        MovieCreatedEvent event = addEvent(AGGREGATE_ID_2);
        setFailedSaveForAggregateId(AGGREGATE_ID_2, event);

        assertThrows(Exception.class, () -> publisher.save());
        verify(esGateway, times(3)).saveOnly(anyString(), any(), any(), eq(TOPIC));
        assertThat(publisher.getEventIds()).hasSize(2);
        assertThat(publisher.getSaveEventRedFlag().get()).isTrue();

        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(esGateway, times(1)).deactivate(argumentCaptor.capture());
        List<String> eventIds = argumentCaptor.getValue();
        assertThat(eventIds).hasSize(2);
        assertThat(eventIds).containsExactlyElementsIn(Arrays.asList(EVENT_ID, EVENT_ID_1));
    }

    @Test
    void givenSaveEventRedFlag_whenSave_shouldThrowException() {
        addEvent(AGGREGATE_ID);
        addEvent(AGGREGATE_ID_1);
        MovieCreatedEvent event = addEvent(AGGREGATE_ID_2);
        setFailedSaveForAggregateId(AGGREGATE_ID_2, event);

        Exception exception = assertThrows(Exception.class, () -> publisher.save());
        assertThat(exception).isInstanceOf(SaveEventStoreException.class);
        assertThat(exception.getMessage())
                .isEqualTo(String.format("Fail when save event stores. Performed rollback for eventIds [%s]",
                        Arrays.asList(EVENT_ID, EVENT_ID_1)));
    }

    private MovieCreatedEvent addEvent(String aggregateId) {
        MovieCreatedEvent event = new MovieCreatedEvent();
        event.setId(aggregateId);
        publisher.addEvent(aggregateId, Movie.class, event, TOPIC);
        return event;
    }

    private void setFailedSaveForAggregateId(String aggregateId, MovieCreatedEvent event) {
        when(esGateway.saveOnly(eq(aggregateId), any(), any(), anyString())).thenThrow(new SaveEventStoreException(
                String.format("Fail when save [%s] with Event [%s]", event.getClass().getSimpleName(), event)));
    }

    @Test
    void givenNoPayload_whenPublishAndTrack_shouldThrowException() throws Exception {
        MovieCreatedEvent event1 = addEvent(AGGREGATE_ID);
        MovieCreatedEvent event2 = addEvent(AGGREGATE_ID_1);

        EventStore es1 = getEventStore(event1, AGGREGATE_ID);
        es1.setEventPayload(null);
        EventStore es2 = getEventStore(event2, AGGREGATE_ID_1);

        when(esGateway.getEventsByFilter(any())).thenReturn(Arrays.asList(es1, es2));

        List<String> ids = publisher.getEventIds();
        publisher.publishAndTrack(ids);
        verify(esGateway, times(2)).updateStatusEventStore(any(), eq(EventStore.Status.FAILED));
    }

    @Test
    void givenNoTopic_whenPublishAndTrack_shouldThrowException() throws Exception {
        MovieCreatedEvent event1 = addEvent(AGGREGATE_ID);
        MovieCreatedEvent event2 = addEvent(AGGREGATE_ID_1);

        EventStore es1 = getEventStore(event1, AGGREGATE_ID);
        es1.setTopic(null);
        EventStore es2 = getEventStore(event2, AGGREGATE_ID_1);

        when(esGateway.getEventsByFilter(any())).thenReturn(Arrays.asList(es1, es2));

        List<String> eve = publisher.getEventIds();
        publisher.publishAndTrack(eve);
        verify(esGateway, times(2)).updateStatusEventStore(any(), eq(EventStore.Status.FAILED));
    }

    @Test
    void givenEventIds_whenPublishAndTrack_shouldCallGetEventsByFilterOnce() {
        addEvent(AGGREGATE_ID);
        addEvent(AGGREGATE_ID_1);
        publisher.save();
        List<String> eventIds = publisher.getEventIds();
        publisher.publishAndTrack(eventIds);

        verify(esGateway, times(1)).getEventsByFilter(any());
    }

    @Test
    void givenOneEventFailed_whenPublishAndTrack_shouldThrowCallUpdateStatusEventStore() throws Exception {
        MovieCreatedEvent event1 = addEvent(AGGREGATE_ID);
        MovieCreatedEvent event2 = addEvent(AGGREGATE_ID_1);

        EventStore es1 = getEventStore(event1, AGGREGATE_ID);
        EventStore es2 = getEventStore(event2, AGGREGATE_ID_1);

        when(esGateway.getEventsByFilter(any())).thenReturn(Arrays.asList(es1, es2));
        when(esGateway.findTopFailedEventAggregate(any())).thenReturn(Optional.of(new EventStore()));

        publisher.save();
        List<String> eventIds = publisher.getEventIds();
        publisher.publishAndTrack(eventIds);
        verify(esGateway, times(2)).updateStatusEventStore(any(), eq(EventStore.Status.FAILED));
        verify(esGateway, times(0)).send(any(), any());
        verify(esGateway, times(0)).updateStatusEventStore(any(), eq(EventStore.Status.SUCCESS));
    }

    @Test
    void givenNoEventFailed_whenPublishAndTrack_shouldPublishAndTrackCorrectly() throws Exception {
        MovieCreatedEvent event1 = addEvent(AGGREGATE_ID);
        MovieCreatedEvent event2 = addEvent(AGGREGATE_ID_1);

        EventStore es1 = getEventStore(event1, AGGREGATE_ID);
        EventStore es2 = getEventStore(event2, AGGREGATE_ID_1);

        when(esGateway.getEventsByFilter(any())).thenReturn(Arrays.asList(es1, es2));
        when(esGateway.findTopFailedEventAggregate(any())).thenReturn(Optional.empty());
        when(esGateway.send(any(), eq("Topic"))).thenReturn(successMetaData);

        publisher.save();
        List<String> eventIds = publisher.getEventIds();
        publisher.publishAndTrack(eventIds);

        verify(esGateway, times(0)).updateStatusEventStore(any(), eq(EventStore.Status.FAILED));
        verify(esGateway, times(2)).send(anyString(), eq("Topic"));

        ArgumentCaptor<EventStore> argumentCaptor = ArgumentCaptor.forClass(EventStore.class);
        verify(esGateway, times(2)).updateStatusEventStore(argumentCaptor.capture(), eq(EventStore.Status.SUCCESS));
        List<EventStore> updateSuccess = argumentCaptor.getAllValues();
        assertThat(updateSuccess).hasSize(2);
        updateSuccess.forEach(eventStore -> {
            assertThat(eventStore.getAggregateId()).isAnyOf(AGGREGATE_ID, AGGREGATE_ID_1);
            assertThat(eventStore.getAggregateType()).isEqualTo(Movie.class.getCanonicalName());
        });
    }

    @Test
    void givenFailedMetaData_whenPublishAndTrack_shouldOnlySendOneEvent() throws Exception {
        MovieCreatedEvent event1 = addEvent(AGGREGATE_ID);
        MovieCreatedEvent event2 = addEvent(AGGREGATE_ID_1);
        EventStore es1 = getEventStore(event1, AGGREGATE_ID);
        EventStore es2 = getEventStore(event2, AGGREGATE_ID_1);

        when(esGateway.getEventsByFilter(any())).thenReturn(Arrays.asList(es1, es2));
        when(esGateway.findTopFailedEventAggregate(any())).thenReturn(Optional.empty());
        when(esGateway.send(any(), eq("Topic"))).thenReturn(failedMetaData);

        publisher.save();
        List<String> eventIds = publisher.getEventIds();
        publisher.publishAndTrack(eventIds);

        ArgumentCaptor<EventStore> failedEventStore = ArgumentCaptor.forClass(EventStore.class);
        verify(esGateway, times(2)).updateStatusEventStore(failedEventStore.capture(), eq(EventStore.Status.FAILED));
        verify(esGateway, times(1)).send(anyString(), eq("Topic"));
        verify(esGateway, times(0)).updateStatusEventStore(any(), eq(EventStore.Status.SUCCESS));

        failedEventStore.getAllValues().forEach(eventStore -> {
            assertThat(eventStore.getAggregateId()).isAnyOf(AGGREGATE_ID, AGGREGATE_ID_1);
            assertThat(eventStore.getAggregateType()).isEqualTo(Movie.class.getCanonicalName());
        });
    }

    @Test
    void givenSuccessMetaData_butFailedUpdateStatusException_whenPublishAndTrack_shouldNotCallUpdateStatusFailed()
            throws Exception {
        MovieCreatedEvent event1 = addEvent(AGGREGATE_ID);
        MovieCreatedEvent event2 = addEvent(AGGREGATE_ID_1);
        EventStore es1 = getEventStore(event1, AGGREGATE_ID);
        EventStore es2 = getEventStore(event2, AGGREGATE_ID_1);

        when(esGateway.getEventsByFilter(any())).thenReturn(Arrays.asList(es1, es2));
        when(esGateway.findTopFailedEventAggregate(any())).thenReturn(Optional.empty());
        when(esGateway.send(any(), eq("Topic"))).thenReturn(successMetaData);
        Answer<Void> answer = this::mockUpdateStatusEventStoreException;
        doAnswer(answer).when(esGateway).updateStatusEventStore(any(), any());

        publisher.save();
        List<String> eventIds = publisher.getEventIds();
        publisher.publishAndTrack(eventIds);

        verify(esGateway, times(0)).updateStatusEventStore(any(), eq(EventStore.Status.FAILED));
    }

    private Void mockUpdateStatusEventStoreException(InvocationOnMock invocationOnMock) {
        throw new FailedToUpdateStatusEventStoreException("Failed when update status event store");
    }

    @Test
    void givenFailedToFindAggregateException_whenPublishAndTrack_shouldCallUpdateStatusEventStore() throws Exception {
        MovieCreatedEvent event1 = addEvent(AGGREGATE_ID);
        MovieCreatedEvent event2 = addEvent(AGGREGATE_ID_1);
        EventStore es1 = getEventStore(event1, AGGREGATE_ID);
        EventStore es2 = getEventStore(event2, AGGREGATE_ID_1);

        when(esGateway.getEventsByFilter(any())).thenReturn(Arrays.asList(es1, es2));
        when(esGateway.findTopFailedEventAggregate(any())).thenReturn(Optional.empty());
        when(esGateway.send(any(), eq("Topic"))).thenReturn(successMetaData);
        Answer<Void> answer = this::mockFindTopFailedEventAggregateException;
        doAnswer(answer).when(esGateway).findTopFailedEventAggregate(any());

        publisher.save();
        List<String> eventIds = publisher.getEventIds();
        publisher.publishAndTrack(eventIds);

        verify(esGateway, times(2)).updateStatusEventStore(any(), eq(EventStore.Status.FAILED));
    }

    private Void mockFindTopFailedEventAggregateException(InvocationOnMock invocationOnMock) {
        throw new FailedToFindTopFailedEventException("Failed find top failed event");
    }

    private EventStore getEventStore(MovieCreatedEvent event2, String aggregateId1) throws JsonProcessingException {
        EventStore es2 = new EventStore();
        es2.setAggregateId(aggregateId1);
        es2.setAggregateType(Movie.class.getCanonicalName());
        es2.setEventPayload(objectMapper.writeValueAsString(event2));
        es2.setTopic("Topic");
        return es2;
    }

}
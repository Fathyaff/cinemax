package com.example.demo.core.util;

import advotics.shared.eventsourcing.EventSourcingMetaData;
import advotics.shared.eventsourcing.EventStoreFilter;
import advotics.shared.eventsourcing.exception.FailedToUpdateStatusEventStoreException;
import advotics.shared.eventsourcing.exception.InvalidEventPayloadException;
import advotics.shared.eventsourcing.exception.InvalidEventTopicException;
import advotics.shared.eventsourcing.persistence.EventStore;
import com.example.demo.core.common.Event;
import com.example.demo.core.exception.SaveEventStoreException;
import com.example.demo.core.gateway.EventSourceGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fathyaff
 * @date 15/08/21 22.55
 */
@Log4j2
@Getter
public class Publisher {

    private final EventSourceGateway eventSourceGateway;

    private final List<EventMetadata<?>> eventMetadata = new CopyOnWriteArrayList<>();

    private final List<String> eventIds = new CopyOnWriteArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    final AtomicBoolean saveEventRedFlag = new AtomicBoolean(false);

    private Publisher(EventSourceGateway eventSourceGateway) {
        this.eventSourceGateway = eventSourceGateway;
    }

    public static Publisher get(EventSourceGateway esGateway) {
        return new Publisher(esGateway);
    }

    public <T> void addEvent(String aggregateId, Class<T> aggregateClass, Event event, String topic) {
        EventMetadata<T> metadata = new EventMetadata<>(aggregateId, aggregateClass, event, topic);
        eventMetadata.add(metadata);
    }

    public void save() {
        doSave();
    }

    private void doSave() {
        eventMetadata.parallelStream().forEachOrdered(this::saveOnly);
        if (saveEventRedFlag.get()) {
            doRollbackSavedEventStores();
        }
    }

    private void doRollbackSavedEventStores() {
        if (!eventIds.isEmpty()) {
            eventSourceGateway.deactivate(eventIds);
        }
        throw new SaveEventStoreException(
                String.format("Fail when save event stores. Performed rollback for eventIds [%s]", eventIds));
    }

    private <T> void saveOnly(EventMetadata<T> metadata) {
        try {
            if (!saveEventRedFlag.get()) {
                String eventId = eventSourceGateway.saveOnly(metadata.getAggregateId(), metadata.getAggregateClass(),
                        metadata.getEvent(), metadata.getTopic());
                eventIds.add(eventId);
            }
        }
        catch (SaveEventStoreException e) {
            saveEventRedFlag.set(true);
        }
    }

    public void publishAndTrack(List<String> eventIds) {
        List<EventStore> eventStores = getEventStoresFilterByEventIds(eventIds);
        AtomicBoolean failedEventFlag = new AtomicBoolean(false);
        eventStores.forEach(eventStore -> publishAndTrack(failedEventFlag, eventStore));
    }

    private void publishAndTrack(AtomicBoolean failedEventFlag, EventStore eventStore) {
        try {
            validateEventStore(eventStore);
            EventStore hasFailedEvent = eventSourceGateway.findTopFailedEventAggregate(eventStore).orElse(null);
            if (aggregateOrSequenceHasFailedEvent(failedEventFlag, hasFailedEvent)) {
                trackStatusFailed(failedEventFlag, eventStore);
                return;
            }

            EventSourcingMetaData meta = publishEvent(eventStore);
            trackEventStore(failedEventFlag, eventStore, meta);
        }
        catch (FailedToUpdateStatusEventStoreException e) {
            log.info(String.format("Failed Update Status of Event Store: %s", e.getMessage()));
            // TODO: send message queue or file
        }
        catch (Exception e) {
            trackStatusFailed(failedEventFlag, eventStore);
        }
    }

    private boolean aggregateOrSequenceHasFailedEvent(AtomicBoolean failedEventFlag, EventStore hasFailedEvent) {
        return hasFailedEvent != null || failedEventFlag.get();
    }

    private void trackStatusFailed(AtomicBoolean failedEventFlag, EventStore eventStore) {
        eventSourceGateway.updateStatusEventStore(eventStore, EventStore.Status.FAILED);
        failedEventFlag.set(true);
    }

    private void trackEventStore(AtomicBoolean failedEventFlag, EventStore eventStore, EventSourcingMetaData meta) {
        if (meta.isSuccess()) {
            eventSourceGateway.updateStatusEventStore(eventStore, EventStore.Status.SUCCESS);
        }
        else {
            trackStatusFailed(failedEventFlag, eventStore);
        }
    }

    private EventSourcingMetaData publishEvent(EventStore eventStore) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(eventStore.getEventPayload());
        return eventSourceGateway.send(payload, eventStore.getTopic());
    }

    private List<EventStore> getEventStoresFilterByEventIds(List<String> eventIds) {
        EventStoreFilter eventStoreFilter = new EventStoreFilter();
        eventStoreFilter.setEventIds(eventIds);
        return eventSourceGateway.getEventsByFilter(eventStoreFilter);
    }

    private void validateEventStore(EventStore eventStore) {
        if (StringUtils.isNullOrEmpty(eventStore.getEventPayload())) {
            throw new InvalidEventPayloadException("Event Payload cannot be null or empty");
        }

        if (StringUtils.isNullOrEmpty(eventStore.getTopic())) {
            throw new InvalidEventTopicException("Event Topic cannot be null or empty");
        }
    }


    @Getter
    @RequiredArgsConstructor
    private static class EventMetadata<T> {

        private final String aggregateId;

        private final Class<T> aggregateClass;

        private final Event event;

        private final String topic;

    }

    AtomicBoolean getSaveEventRedFlag() {
        return saveEventRedFlag;
    }
}

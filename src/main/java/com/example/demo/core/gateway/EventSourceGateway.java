package com.example.demo.core.gateway;

import advotics.shared.eventsourcing.EventSourcingMetaData;
import advotics.shared.eventsourcing.EventStoreFilter;
import advotics.shared.eventsourcing.persistence.EventStore;

import java.util.List;
import java.util.Optional;

/**
 * @author fathyaff
 * @date 15/08/21 15.06
 */
public interface EventSourceGateway {

    <T> T get(String aggregateId, Class<T> aggregateClass);

    List<EventStore> getEventsByFilter(EventStoreFilter filter);

    EventSourcingMetaData send(String payload, String topic);

    <A, E> String saveOnly(String aggregateId, Class<A> klass, E event, String topic);

    void deactivate(List<String> eventIds);

    void updateStatusEventStore(EventStore eventStore, EventStore.Status status);

    Optional<EventStore> findTopFailedEventAggregate(EventStore eventStore);
}

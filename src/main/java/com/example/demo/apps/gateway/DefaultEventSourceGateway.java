package com.example.demo.apps.gateway;

import advotics.shared.eventsourcing.EventSourcingGateway;
import advotics.shared.eventsourcing.EventSourcingMetaData;
import advotics.shared.eventsourcing.EventStoreFilter;
import advotics.shared.eventsourcing.exception.FailedToFindTopFailedEventException;
import advotics.shared.eventsourcing.exception.FailedToUpdateStatusEventStoreException;
import advotics.shared.eventsourcing.persistence.EventStore;
import com.example.demo.core.exception.AggregateFailedRetrievedException;
import com.example.demo.core.exception.DeactivateEventStoreException;
import com.example.demo.core.exception.SaveEventStoreException;
import com.example.demo.core.gateway.EventSourceGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author fathyaff
 * @date 15/08/21 21.14
 */
@RequiredArgsConstructor
public class DefaultEventSourceGateway implements EventSourceGateway {

    private final EventSourcingGateway gateway;

    @Override
    public <T> T get(String aggregateId, Class<T> aggregateClass) {
        try {
            return gateway.get(aggregateId, aggregateClass);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException
                | DataAccessException e) {
            throw new AggregateFailedRetrievedException(
                    String.format("Fail when get [%s] aggregate with Aggregate Id [%s]", aggregateClass.getSimpleName(),
                            aggregateId),
                    e);
        }
    }

    @Override
    public List<EventStore> getEventsByFilter(EventStoreFilter filter) {
        return gateway.getEventsByFilter(filter);
    }

    @Override
    public EventSourcingMetaData send(String payload, String topic) {
        return gateway.send(payload, topic);
    }

    @Override
    public <A, E> String saveOnly(String aggregateId, Class<A> klass, E event, String topic) {
        try {
            return gateway.saveOnly(aggregateId, klass, event, topic);
        }
        catch (Exception e) {
            throw new SaveEventStoreException(
                    String.format("Fail when save [%s] with Event [%s]", event.getClass().getSimpleName(), event), e);
        }
    }

    @Override
    public void deactivate(List<String> eventIds) {
        try {
            gateway.deactivate(eventIds);
        }
        catch (Exception e) {
            throw new DeactivateEventStoreException(String.format("Fail when deactivate eventsIds: %s", eventIds), e);
        }
    }

    @Override
    public void updateStatusEventStore(EventStore eventStore, EventStore.Status status) {
        try {
            gateway.updateStatusEventStore(eventStore, status);
        }
        catch (Exception e) {
            throw new FailedToUpdateStatusEventStoreException(
                    String.format("Fail when update status %s event store ID %s", status, eventStore.getEventId()));
        }
    }

    @Override
    public Optional<EventStore> findTopFailedEventAggregate(EventStore eventStore) {
        try {
            return gateway.findTopFailedEventAggregate(eventStore);
        }
        catch (Exception e) {
            throw new FailedToFindTopFailedEventException(
                    String.format("Fail when find top failed event aggregate ID %s", eventStore.getEventId()), e);
        }
    }
}

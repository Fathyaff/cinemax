package com.example.demo.core.gateway;

//import advotics.shared.eventsourcing.EventSourcingGateway;
import com.example.demo.apps.gateway.DefaultEventSourceGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

/**
 * @author fathyaff
 * @date 15/08/21 20.45
 */
class EventSourceGatewayTest {

    private EventSourceGateway eventSourceGateway;

    @BeforeEach
    void setup() {
//        EventSourcingGateway eventSourcingGateway = mock(EventSourcingGateway.class);
//        eventSourceGateway = new DefaultEventSourceGateway(eventSourcingGateway);
    }

    @Test
    void givenParams_whenSave_shouldCallSaveGateway() {
    }
}

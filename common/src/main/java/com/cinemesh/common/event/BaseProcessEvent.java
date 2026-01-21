package com.cinemesh.common.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class BaseProcessEvent extends DomainEvent {
    public BaseProcessEvent() {
    }

    public BaseProcessEvent(BaseProcessEventName name, Object payload) {
        this.setName(name.name());
        this.setPayload(payload);
        this.setCreatedAt((new Date()).toInstant());
    }

    public BaseProcessEvent(BaseProcessEventName name) {
        this.setName(name.name());
        this.setCreatedAt(Instant.now());
    }

    public static class DomainEventDeserializer extends StdDeserializer<DomainEvent> {
        public DomainEventDeserializer() {
            super(DomainEvent.class);
        }

        public DomainEvent deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return (DomainEvent) jsonParser.readValueAs(BaseProcessEvent.class);
        }
    }

    public static class DomainEventModule extends SimpleModule {
        public DomainEventModule() {
            this.addDeserializer(DomainEvent.class, new DomainEventDeserializer());
        }
    }
}

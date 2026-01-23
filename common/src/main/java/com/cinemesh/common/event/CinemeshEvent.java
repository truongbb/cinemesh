package com.cinemesh.common.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class CinemeshEvent extends DomainEvent {
    public CinemeshEvent() {
        super();
    }

    public CinemeshEvent(CinemeshEventName name, Object payload) {
        this.setName(name.name());
        this.setPayload(payload);
        this.setCreatedAt(new Date().toInstant());
    }

    public CinemeshEvent(CinemeshEventName name) {
        this.setName(name.name());
        this.setCreatedAt(Instant.now());
    }

    public static class DomainEventDeserializer extends StdDeserializer<DomainEvent> {
        public DomainEventDeserializer() {
            super(DomainEvent.class);
        }

        public DomainEvent deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return jsonParser.readValueAs(CinemeshEvent.class);
        }
    }

    public static class DomainEventModule extends SimpleModule {
        {
            addDeserializer(DomainEvent.class, new DomainEventDeserializer());
        }
    }
}

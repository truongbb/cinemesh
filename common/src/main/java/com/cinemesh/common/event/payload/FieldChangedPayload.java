package com.cinemesh.common.event.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class FieldChangedPayload extends BaseEventPayload {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reference;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public FieldChangedPayload() {
    }

    public FieldChangedPayload(String fieldName, Object oldValue, Object newValue) {
        this.fieldName = fieldName;
        this.oldValue = this.formatValue(oldValue);
        this.newValue = this.formatValue(newValue);
    }

    public FieldChangedPayload(Object reference, String fieldName, Object oldValue, Object newValue) {
        this.fieldName = fieldName;
        this.oldValue = this.formatValue(oldValue);
        this.newValue = this.formatValue(newValue);
        this.reference = this.formatValue(reference);
    }

    private String formatValue(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toPlainString();
        } else if (obj instanceof Double) {
            return (new BigDecimal((Double) obj)).toPlainString();
        } else if (obj instanceof Date) {
            return FORMATTER.format(((Date) obj).toInstant());
        } else if (obj instanceof TemporalAccessor) {
            return FORMATTER.format((TemporalAccessor) obj);
        }
        return obj.toString();
    }

    public String getReference() {
        return this.reference;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public void setOldValue(final String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(final String newValue) {
        this.newValue = newValue;
    }
}
package com.cinemesh.common.domain;

public abstract class ValueObject {
    abstract boolean equalsCore(ValueObject other);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueObject that = (ValueObject) o;
        return equalsCore(that);
    }

    @Override
    public int hashCode() {
        // Class con NÊN override lại hashCode dựa trên các thuộc tính
        return super.hashCode();
    }

}


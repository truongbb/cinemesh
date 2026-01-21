package com.cinemesh.common.domain;

public interface LocalEntity<TRoot, ID> extends Entity<ID> {
    TRoot getAggRoot();
}

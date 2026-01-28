package com.cinemesh.common.statics;

public enum LogType {
    CREATE, MODIFY;

    public static LogType getByIsCreated(boolean isCreated) {
        return isCreated ? LogType.CREATE : LogType.MODIFY;
    }
}

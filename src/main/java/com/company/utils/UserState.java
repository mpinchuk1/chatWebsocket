package com.company.utils;

public enum UserState {
    OFFLINE, ONLINE, AWAY;

    @Override
    public String toString() {
        return name();
    }
}

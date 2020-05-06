package com.company.entities;

public enum UserState {
    OFFLINE, ONLINE, AWAY;

    @Override
    public String toString() {
        return name();
    }
}

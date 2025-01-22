package com.ndev.privchat.privchat.entities;

import lombok.Getter;
import lombok.Setter;

// Registration class
@Getter
@Setter
public class RegistrationRecord {
    private int id;
    private String time;

    public RegistrationRecord(int id, String time) {
        this.id = id;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Registration{id=" + id + ", time='" + time + "'}";
    }
}
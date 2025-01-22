package com.ndev.privchat.privchat.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRecord {
    private int id;
    private String time;

    public MessageRecord(int id, String time) {
        this.id = id;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{id=" + id + ", time='" + time + "'}";
    }
}
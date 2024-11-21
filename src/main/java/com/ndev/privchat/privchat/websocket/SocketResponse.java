package com.ndev.privchat.privchat.websocket;

import java.util.List;

public class SocketResponse<T> {
    private String update;
    private List<T> objects;

    // Getter and Setter for update
    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    // Getter and Setter for objects
    public List<T> getObjects() {
        return objects;
    }

    public void setObjects(List<T> objects) {
        this.objects = objects;
    }
}

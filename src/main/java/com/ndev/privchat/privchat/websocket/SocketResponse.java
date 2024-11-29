package com.ndev.privchat.privchat.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocketResponse<T> {
    private Object message;

}

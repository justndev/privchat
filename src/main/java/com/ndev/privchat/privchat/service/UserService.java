package com.ndev.privchat.privchat.service;

import com.ndev.privchat.privchat.swarmPool.SwampUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<String, SwampUser> pool = new ConcurrentHashMap<>();

    public UserService() {}

    public void addUser(SwampUser swampUser) {
        pool.put(swampUser.getNickname(), swampUser);
    }

    public List<SwampUser> allUsers() {
        return new ArrayList<>(pool.values());
    }

    public SwampUser findUserByNickname(String nickname) {
        return pool.get(nickname);
    }

    public void removeUser(String nickname) {
        pool.remove(nickname);
    }

    public boolean userExists(String nickname) {
        return pool.containsKey(nickname);
    }

    public Map<String, SwampUser> getUserMap() {
        return pool;
    }

    public int countUsers() {
        return pool.size();
    }
}

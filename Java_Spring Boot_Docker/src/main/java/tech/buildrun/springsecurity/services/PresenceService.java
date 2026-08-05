package tech.buildrun.springsecurity.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final Set<UUID> onlineUsers = ConcurrentHashMap.newKeySet();

    public void setOnline(UUID userId) {
        onlineUsers.add(userId);
    }

    public void setOffline(UUID userId) {
        onlineUsers.remove(userId);
    }

    public boolean isOnline(UUID userId) {
        return onlineUsers.contains(userId);
    }

}

package com.group17.comic.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class GlobalStorage {
    private final Map<String, Object> storage = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        storage.put(key, value);
    }

    public Object get(String key) {
        return storage.get(key);
    }
}

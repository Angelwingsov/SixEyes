package com.sixeyes.client.api.event;

import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static final IEventBus eventBus = new EventBus();
    private static final List<Object> subscribers = new ArrayList<>();

    public static void subscribe(Object object) {
        if (!subscribers.contains(object)) {
            eventBus.subscribe(object);
            subscribers.add(object);
        }
    }

    public static void unsubscribe(Object object) {
        if (subscribers.contains(object)) {
            eventBus.unsubscribe(object);
            subscribers.remove(object);
        }
    }

    public static <T> T post(T event) {
        return eventBus.post(event);
    }

    public static IEventBus getBus() {
        return eventBus;
    }
}

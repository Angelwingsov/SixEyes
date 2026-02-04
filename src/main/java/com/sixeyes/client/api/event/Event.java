package com.sixeyes.client.api.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Event {
    private boolean cancelled = false;
}
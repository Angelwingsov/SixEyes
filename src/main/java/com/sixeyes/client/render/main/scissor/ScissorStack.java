package com.sixeyes.client.render.main.scissor;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScissorStack {
    private final Deque<ScissorRect> stack = new ArrayDeque<>();
    public ScissorRect current;

    public void push(float x, float y, float width, float height) {
        ScissorRect next;
        if (current != null) {
            float x1 = Math.max(x, current.x());
            float y1 = Math.max(y, current.y());
            float x2 = Math.min(x + width, current.x() + current.width());
            float y2 = Math.min(y + height, current.y() + current.height());

            if (x2 < x1 || y2 < y1) {
                next = new ScissorRect(0, 0, 0, 0);
            } else {
                next = new ScissorRect(x1, y1, x2 - x1, y2 - y1);
            }
        } else {
            next = new ScissorRect(x, y, width, height);
        }
        stack.addLast(next);
        current = next;
    }

    public void push(ScissorRect rect) {
        push(rect.x(), rect.y(), rect.width(), rect.height());
    }

    public void pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
        } else {
            stack.removeLast();
            current = stack.peekLast();
        }
    }
}

package com.ferra13671.cometrenderer.scissor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class ScissorStack {
    
    private final Deque<ScissorRect> stack = new ArrayDeque<>();
    
    private ScissorRect current;

    
    public void push(ScissorRect rect) {
        rect = rect.fixRect();
        ScissorRect scissorRect = rect;
        if (current != null)
            scissorRect = Objects.requireNonNullElse(rect.intersection(current), new ScissorRect(0, 0, 0, 0));
        stack.addLast(scissorRect);
        current = scissorRect;
    }

    
    public ScissorRect current() {
        return current;
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

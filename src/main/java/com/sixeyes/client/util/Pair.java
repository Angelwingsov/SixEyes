package com.sixeyes.client.util;

public class Pair<A, B> {
    private A left = null;
    private B right = null;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public Pair() {}

    public A left() {
        return this.left;
    }

    public B right() {
        return this.right;
    }

    public void left(A left) {
        this.left = left;
    }

    public void right(B right) {
        this.right = right;
    }
}

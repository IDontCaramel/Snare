package net.caramel.snare.event.type;

public final class MovementInputEvent {
    private boolean forward, back, left, right;
    private final boolean screenOpen;
    public MovementInputEvent(boolean forward, boolean back, boolean left, boolean right, boolean screenOpen) {
        this.forward = forward;
        this.back = back;
        this.left = left;
        this.right = right;
        this.screenOpen = screenOpen;
    }
    public boolean forward() { return forward; }
    public void forward(boolean v) { forward = v; }
    public boolean back() { return back; }
    public void back(boolean v) { back = v; }
    public boolean left() { return left; }
    public void left(boolean v) { left = v; }
    public boolean right() { return right; }
    public void right(boolean v) { right = v; }
    public boolean screenOpen() { return screenOpen; }
}

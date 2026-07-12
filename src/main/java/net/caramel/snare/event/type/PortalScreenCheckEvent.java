package net.caramel.snare.event.type;

public final class PortalScreenCheckEvent {
    private boolean blocksPortalClose;

    public PortalScreenCheckEvent(boolean blocksPortalClose) {
        this.blocksPortalClose = blocksPortalClose;
    }

    public boolean blocksPortalClose() {
        return blocksPortalClose;
    }

    public void blocksPortalClose(boolean v) {
        this.blocksPortalClose = v;
    }
}

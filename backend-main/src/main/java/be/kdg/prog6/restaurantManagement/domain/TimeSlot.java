package be.kdg.prog6.restaurantManagement.domain;

import java.time.LocalTime;

// Value Object
public class TimeSlot {
    private LocalTime open;
    private LocalTime close;

    public TimeSlot() {}

    public TimeSlot(LocalTime open, LocalTime close) {
        this.open = open;
        this.close = close;
    }

    // getters and setters
    public LocalTime getOpen() { return open; }
    public void setOpen(LocalTime open) { this.open = open; }
    public LocalTime getClose() { return close; }
    public void setClose(LocalTime close) { this.close = close; }
}

package be.kdg.prog6.restaurantManagement.domain;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklySchedule {
    private Map<DayOfWeek, List<TimeSlot>> schedule;

    public WeeklySchedule(Map<DayOfWeek, List<TimeSlot>> schedule) {
        this.schedule = schedule;
    }
    // Default constructor (needed for Jackson)
    public WeeklySchedule() {
        this.schedule = new HashMap<>();
    }
    public Map<DayOfWeek, List<TimeSlot>> getSchedule() {
        return schedule;
    }

    public void setSchedule(Map<DayOfWeek, List<TimeSlot>> schedule) {
        this.schedule = schedule;
    }

}

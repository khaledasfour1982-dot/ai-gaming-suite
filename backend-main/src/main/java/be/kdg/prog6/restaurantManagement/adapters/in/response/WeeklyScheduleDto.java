package be.kdg.prog6.restaurantManagement.adapters.in.response;

import be.kdg.prog6.restaurantManagement.domain.TimeSlot;
import be.kdg.prog6.restaurantManagement.domain.WeeklySchedule;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyScheduleDto {
    private Map<String, List<TimeSlotDto>> schedule;

    public Map<String, List<TimeSlotDto>> getSchedule() { return schedule; }
    public void setSchedule(Map<String, List<TimeSlotDto>> schedule) { this.schedule = schedule; }

    public WeeklySchedule toDomain() {
        Map<DayOfWeek, List<TimeSlot>> domainMap = new HashMap<>();
        if (schedule != null) {
            schedule.forEach((dayStr, slots) -> {
                domainMap.put(DayOfWeek.valueOf(dayStr.toUpperCase()), TimeSlotDto.toDomainList(slots));
            });
        }
        return new WeeklySchedule(domainMap);
    }

    public static WeeklyScheduleDto fromDomain(WeeklySchedule domain) {
        WeeklyScheduleDto dto = new WeeklyScheduleDto();
        Map<String, List<TimeSlotDto>> dtoMap = new HashMap<>();
        if (domain != null && domain.getSchedule() != null) {
            domain.getSchedule().forEach((day, slots) -> {
                dtoMap.put(day.name(), TimeSlotDto.fromDomainList(slots));
            });
        }
        dto.setSchedule(dtoMap);
        return dto;
    }
}

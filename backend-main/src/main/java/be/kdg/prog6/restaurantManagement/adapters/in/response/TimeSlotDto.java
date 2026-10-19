package be.kdg.prog6.restaurantManagement.adapters.in.response;

import be.kdg.prog6.restaurantManagement.domain.TimeSlot;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSlotDto {
    private String open;  // format "HH:mm"
    private String close;

    public TimeSlotDto() {}

    public TimeSlotDto(String open, String close) {
        this.open = open;
        this.close = close;
    }

    // Convert DTO to domain
    public TimeSlot toDomain() {
        return new TimeSlot(LocalTime.parse(open), LocalTime.parse(close));
    }

    // Convert domain to DTO
    public static TimeSlotDto fromDomain(TimeSlot timeSlot) {
        return new TimeSlotDto(timeSlot.getOpen().toString(), timeSlot.getClose().toString());
    }

    public static List<TimeSlot> toDomainList(List<TimeSlotDto> dtos) {
        return dtos.stream().map(TimeSlotDto::toDomain).collect(Collectors.toList());
    }

    public static List<TimeSlotDto> fromDomainList(List<TimeSlot> domainList) {
        return domainList.stream().map(TimeSlotDto::fromDomain).collect(Collectors.toList());
    }

    // Getters and setters
    public String getOpen() { return open; }
    public void setOpen(String open) { this.open = open; }
    public String getClose() { return close; }
    public void setClose(String close) { this.close = close; }
}

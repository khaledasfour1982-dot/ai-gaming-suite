package be.kdg.prog6.restaurantManagement.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;

@Converter(autoApply = true)
public class WeeklyScheduleConverter implements AttributeConverter<WeeklySchedule, String> {
    private final ObjectMapper mapper;

    public WeeklyScheduleConverter() {
        this.mapper = new ObjectMapper();
        // Register the Java Time module to handle LocalTime, LocalDate, etc.
        this.mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String convertToDatabaseColumn(WeeklySchedule schedule) {
        try {
            return mapper.writeValueAsString(schedule);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize WeeklySchedule", e);
        }
    }

    @Override
    public WeeklySchedule convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new WeeklySchedule(Collections.emptyMap());
        }
        try {
            return mapper.readValue(dbData, WeeklySchedule.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not deserialize WeeklySchedule", e);
        }
    }


}

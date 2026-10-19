package be.kdg.prog6.restaurantManagement.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class FoodTagListConverter implements AttributeConverter<List<FoodTag>, String> {

    @Override
    public String convertToDatabaseColumn(List<FoodTag> attribute) {
        if (attribute == null || attribute.isEmpty()) return "{}";
        return "{" + attribute.stream().map(Enum::name).collect(Collectors.joining(",")) + "}";
    }

    @Override
    public List<FoodTag> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.equals("{}")) return List.of();
        return Arrays.stream(dbData.replaceAll("[{}]", "").split(","))
                .map(FoodTag::valueOf)
                .collect(Collectors.toList());
    }
}

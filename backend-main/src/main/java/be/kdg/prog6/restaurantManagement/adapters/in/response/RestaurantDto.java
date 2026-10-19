package be.kdg.prog6.restaurantManagement.adapters.in.response;

import be.kdg.prog6.restaurantManagement.adapters.out.DishJpaEntity;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.domain.*;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

public record RestaurantDto(
        String uuid,
        String name,
        Address fullAddress,
        String contactEmail,
        String picture,
        TypeOfCuisine typeOfCuisine,
        Double preparationTime,
        Map<String, List<TimeSlotDto>> openingHours,
        List<DishDto> dishes,
        String owner

) {

    // DOMAIN → DTO
    public static RestaurantDto fromDomain(Restaurant restaurant) {
        Map<String, List<TimeSlotDto>> hoursDto = restaurant.getOpeningHours()
                .getSchedule().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),          // DayOfWeek → String
                        entry -> TimeSlotDto.fromDomainList(entry.getValue()) // TimeSlot → DTO
                ));

        return new RestaurantDto(
                restaurant.getId().uuid().toString(),
                restaurant.getName(),
                restaurant.getFullAddress(),
                restaurant.getContactEmail(),
                restaurant.getPicture(),
                restaurant.getTypeOfCuisine(),
                restaurant.getPreparationTime(),
                hoursDto,
                List.of(),
                restaurant.getOwner()
        );
    }

    public static RestaurantDto fromDomain(RestaurantDetails details) {
        Map<String, List<TimeSlotDto>> hoursDto = details.restaurant().getOpeningHours()
                .getSchedule().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        entry -> TimeSlotDto.fromDomainList(entry.getValue())
                ));

        List<DishDto> dishDtos = details.dishes().stream()
                .map(DishDto::fromDomain)
                .collect(Collectors.toList());

        return new RestaurantDto(
                details.restaurant().getId().uuid().toString(),
                details.restaurant().getName(),
                details.restaurant().getFullAddress(),
                details.restaurant().getContactEmail(),
                details.restaurant().getPicture(),
                details.restaurant().getTypeOfCuisine(),
                details.restaurant().getPreparationTime(),
                hoursDto,
                dishDtos,
                details.restaurant().getOwner()
        );
    }

    // DTO → DOMAIN
    public Restaurant toDomain() {
        UUID restaurantId = (uuid == null || uuid.isEmpty())
                ? UUID.randomUUID()  // generate a new UUID if not provided (e.g., on restaurant creation)
                : UUID.fromString(uuid);

        Map<DayOfWeek, List<TimeSlot>> schedule = openingHours == null
                ? Collections.emptyMap()
                : openingHours.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> DayOfWeek.valueOf(e.getKey().toUpperCase()),
                        e -> TimeSlotDto.toDomainList(e.getValue())
                ));

        WeeklySchedule weeklySchedule = new WeeklySchedule(schedule);

        List<Dish> domainDishes = Optional.ofNullable(dishes)
                .orElse(Collections.emptyList())
                .stream()
                .map(DishDto::toDomain)
                .collect(Collectors.toList());


        return new Restaurant(
                RestaurantId.of(restaurantId),
                name,
                fullAddress,
                contactEmail,
                picture,
                typeOfCuisine,
                preparationTime,
                weeklySchedule,
                owner,
                domainDishes)
                ;
    }




}

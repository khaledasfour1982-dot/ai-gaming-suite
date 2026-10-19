package be.kdg.prog6.restaurantManagement.domain;

import be.kdg.prog6.common.events.*;
import be.kdg.prog6.restaurantManagement.adapters.in.response.DishDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// aggregate
public class Restaurant {
    private RestaurantId id;
    private String name;
    private Address fullAddress;
    private String contactEmail;
    // picture is url
    private String picture;
    private TypeOfCuisine typeOfCuisine;
    private Double preparationTime;
    private WeeklySchedule openingHours;
    private String owner;
    private final List<Dish> dishes = new ArrayList<>();
    private final List<DomainEvent> events = new ArrayList<>();


    public Restaurant(RestaurantId id, String name, Address fullAddress, String contactEmail, String picture, TypeOfCuisine typeOfCuisine, Double preparationTime, WeeklySchedule openingHours, String owner, List<Dish> dishes) {
        this.id = id;
        this.name = name;
        this.fullAddress = fullAddress;
        this.contactEmail = contactEmail;
        this.picture = picture;
        this.typeOfCuisine = typeOfCuisine;
        this.preparationTime = preparationTime;
        this.openingHours = openingHours;
        this.owner = owner;
        if (dishes != null) {
            this.dishes.addAll(dishes);
        }
    }

    public Dish addDish(DishDraft draft) {
        DishId newDishId = new DishId(UUID.randomUUID());
        Dish newDish = new Dish(
                newDishId,
                draft.name(),
                draft.type(),
                draft.foodTags(),
                draft.description(),
                draft.price(),
                draft.picture(),
                draft.status()
        );
        dishes.add(newDish);

        // Optionally add DomainEvent

        return newDish;
    }


    public Dish editDish(DishId dishId, Dish updatedDish) {
        Dish dish = dishes.stream()
                .filter(d -> d.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dish not found"));

        dish.editDraft(
                updatedDish.getName(),
                updatedDish.getType(),
                updatedDish.getFoodTags(),
                updatedDish.getDescription(),
                updatedDish.getPrice(),
                updatedDish.getPicture(),
                updatedDish.getStatus()
        );

        return dish;
    }

    public Dish getDishById(DishId dishId) {
        System.out.println("Searching for dish with ID: " + dishId);
        dishes.forEach(d -> System.out.println("Dish in restaurant: " + d.getId()));
        return dishes.stream()
                .filter(dish -> dish.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dish not found with id: " + dishId));
    }



    public void publishDish(DishId dishId) {
        Dish dish = getDishById(dishId);
        dish.publish();

        DishPublishedEvent enrichedEvent = new DishPublishedEvent(
                LocalDateTime.now(),
                dishId.uuid(),
                this.id.uuid(),
                dish.getName(),
                dish.getPrice()
        );
        events.add(enrichedEvent);
    }


    public void unpublishDish(DishId dishId) {
        Dish dish = getDishById(dishId);
        dish.unpublish();

        DishUnpublishedEvent enrichedEvent = new DishUnpublishedEvent(
                LocalDateTime.now(),
                dishId.uuid(),
                this.id.uuid()
        );
        events.add(enrichedEvent);
    }

    public List<DishPublishedEvent> applyPendingDishChanges() {
        List<DishPublishedEvent> batchEvents = new ArrayList<>();
        for (Dish dish : dishes) {
            if (dish.hasDraft()) {
                dish.publish();

                DishPublishedEvent enrichedEvent = new DishPublishedEvent(
                        LocalDateTime.now(),
                        dish.getId().uuid(),
                        this.id.uuid(),
                        dish.getName(),
                        dish.getPrice()
                );
                batchEvents.add(enrichedEvent);
                events.add(enrichedEvent);
            }
        }
        return batchEvents;
    }

    public void markDishOutOfStock(DishId dishId) {
        Dish dish = getDishById(dishId);
        dish.setStatus(DishStatus.OUT_OF_STOCK);

        DishOutOfStockEvent enrichedEvent = new DishOutOfStockEvent(
                LocalDateTime.now(),
                dishId.uuid(),
                this.id.uuid()
        );
        events.add(enrichedEvent);
    }

    public void setOpeningHours(WeeklySchedule newSchedule) {
        this.openingHours = newSchedule;
        OpeningHoursUpdatedEvent event = new OpeningHoursUpdatedEvent(this.id.uuid(), newSchedule);
        events.add(event);
    }


    public RestaurantId getId() {
        return id;
    }

    public void setId(RestaurantId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(Address fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public TypeOfCuisine getTypeOfCuisine() {
        return typeOfCuisine;
    }

    public void setTypeOfCuisine(TypeOfCuisine typeOfCuisine) {
        this.typeOfCuisine = typeOfCuisine;
    }

    public Double getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Double preparationTime) {
        this.preparationTime = preparationTime;
    }

    public WeeklySchedule getOpeningHours() {
        return openingHours;
    }

    public List<DomainEvent> getDomainEvents() {
        return events;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullAddress='" + fullAddress + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", picture='" + picture + '\'' +
                ", typeOfCuisine=" + typeOfCuisine +
                ", preparationTime=" + preparationTime +
                ", openingHours=" + openingHours +
                '}';
    }

    public String getOwner() {
        return owner;
    }

    public List<DomainEvent> clearDomainEvents() {
        List<DomainEvent> currentEvents = new ArrayList<>(this.events);
        this.events.clear();
        return currentEvents;
    }

}
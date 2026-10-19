package be.kdg.prog6.restaurantManagement.adapters.out;

import be.kdg.prog6.restaurantManagement.domain.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Entity
@Table(name = "restaurant",
        // limit db creation to 1 restaurant
        uniqueConstraints = {@UniqueConstraint(columnNames = {"owner"}),},
        schema = "kdg")
public class RestaurantJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private AddressEmbeddable fullAddress;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOfCuisine typeOfCuisine;

    @Column(nullable = false)
    private Double preparationTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = WeeklyScheduleConverter.class)
    private WeeklySchedule openingHours;

    @Column(nullable = false)
    private String owner;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private List<DishJpaEntity> dishes = new ArrayList<>();

    public RestaurantJpaEntity() {
        this.id = UUID.randomUUID();
    }

    public RestaurantJpaEntity(UUID id,String name, AddressEmbeddable fullAddress, String contactEmail, String picture, TypeOfCuisine typeOfCuisine, Double preparationTime, WeeklySchedule openingHours, String owner) {
        this.id = id;
        this.name = name;
        this.fullAddress = fullAddress;
        this.contactEmail = contactEmail;
        this.picture = picture;
        this.typeOfCuisine = typeOfCuisine;
        this.preparationTime = preparationTime;
        this.openingHours = openingHours;
        this.owner = owner;
        this.dishes = new ArrayList<>();


    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressEmbeddable getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(AddressEmbeddable fullAddress) {
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

    public void setOpeningHours(WeeklySchedule openingHours) {
        this.openingHours = openingHours;
    }

        public List<DishJpaEntity> getDishes() {
            return dishes;
        }

    public void setDishes(List<DishJpaEntity> dishes) {
        this.dishes = dishes;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}

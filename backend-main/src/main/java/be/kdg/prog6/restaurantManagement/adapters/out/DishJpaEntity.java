package be.kdg.prog6.restaurantManagement.adapters.out;

import be.kdg.prog6.restaurantManagement.domain.*;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dishes", schema = "kdg")
public class DishJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DishType type;

    @Column(name = "food_tags", columnDefinition = "text")
    private String foodTags;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DishStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "draft_name")),
            @AttributeOverride(name = "type", column = @Column(name = "draft_type")),
            @AttributeOverride(name = "foodTags", column = @Column(name = "draft_food_tags")),
            @AttributeOverride(name = "description", column = @Column(name = "draft_description")),
            @AttributeOverride(name = "price", column = @Column(name = "draft_price")),
            @AttributeOverride(name = "picture", column = @Column(name = "draft_picture")),
            @AttributeOverride(name = "status", column = @Column(name = "draft_status"))
    })
    private DishDraft draft;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantJpaEntity restaurant;

    public boolean isPublished() {
        return this.status == DishStatus.PUBLISHED;
    }

    public DishJpaEntity() {
        this.id = UUID.randomUUID();
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

    public DishType getType() {
        return type;
    }

    public void setType(DishType type) {
        this.type = type;
    }

    public String getFoodTags() {
        return foodTags;
    }

    public void setFoodTags(String foodTag) {
        this.foodTags = foodTag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public DishStatus getStatus() {
        return status;
    }

    public void setStatus(DishStatus status) {
        this.status = status;
    }

    public DishDraft getDraft() {
        return draft;
    }

    public void setDraft(DishDraft draft) {
        this.draft = draft;
    }

    public RestaurantJpaEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantJpaEntity restaurant) {
        this.restaurant = restaurant;
    }
}

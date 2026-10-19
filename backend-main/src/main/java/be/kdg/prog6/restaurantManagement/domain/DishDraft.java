package be.kdg.prog6.restaurantManagement.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

// value object
@Embeddable
public class DishDraft {
    private String name;
    private DishType type;
    private String foodTags;
    private String description;
    private int price;
    private String picture;

    @Enumerated(EnumType.STRING)
    private DishStatus status;

    protected DishDraft() {} // for JPA

    public DishDraft(String name, DishType type, String foodTags,
                     String description, int price, String picture, DishStatus status) {
        this.name = name;
        this.type = type;
        this.foodTags = foodTags;
        this.description = description;
        this.price = price;
        this.picture = picture;
        this.status = status;
    }

    public String name() { return name; }
    public DishType type() { return type; }
    public String foodTags() { return foodTags; }
    public String description() { return description; }
    public int price() { return price; }
    public String picture() { return picture; }
    public DishStatus status() { return status; }
}

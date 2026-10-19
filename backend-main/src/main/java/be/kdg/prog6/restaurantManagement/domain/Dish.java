package be.kdg.prog6.restaurantManagement.domain;

// entity
public class Dish {
    private DishId dishId;
    private String name;
    private DishType type;
    private String foodTags;
    private String description;
    private int price;
    // url
    private String picture;
    private DishDraft draft;
    private DishStatus status;


    public Dish(DishId dishId, String name, DishType type, String foodTags, String description, int price, String picture,DishStatus status) {
        this.dishId = dishId;
        this.name = name;
        this.type = type;
        this.foodTags = foodTags;
        this.description = description;
        this.price = price;
        this.picture = picture;
        this.status = status;
    }

    public void editDraft(String name, DishType type, String foodTags, String description, int price, String picture, DishStatus status) {
        this.draft = new DishDraft(name, type, foodTags, description, price, picture, status);
    }

    public void publish() {
        if (draft == null) {
            throw new IllegalStateException("No draft found for this dish");
        }
        this.name = draft.name();
        this.type = draft.type();
        this.foodTags = draft.foodTags();
        this.description = draft.description();
        this.price = draft.price();
        this.picture = draft.picture();
        this.status = DishStatus.PUBLISHED;  // enforce published
        this.draft = null;
    }


    public void unpublish() {
        if (this.status != DishStatus.UNPUBLISHED) {
            this.status = DishStatus.UNPUBLISHED;
        }
    }

    public boolean isPublished() {
        return this.status == DishStatus.PUBLISHED;
    }

    public DishStatus getStatus() {
        return status;
    }

    public void setStatus(DishStatus status) {
        this.status = status;
    }

    public DishId getDishId() {
        return dishId;
    }

    public void setDishId(DishId dishId) {
        this.dishId = dishId;
    }

    public DishId getId() {
        return dishId;
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

    public void setFoodTags(String foodTags) {
        this.foodTags = foodTags;
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

    public DishDraft getDraft() {
        return draft;
    }

    public void setDraft(DishDraft draft) {
        this.draft = draft;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + dishId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", foodTags=" + foodTags +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", picture='" + picture + '\'' +
                '}';
    }

    public boolean hasDraft() {
        return draft != null;
    }

}

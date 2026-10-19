package be.kdg.prog6.restaurantManagement.adapters.in.response;

import be.kdg.prog6.restaurantManagement.domain.*;

import java.util.UUID;

public class DishDto {
    private String id;

    // Published dish fields
    private String name;
    private DishType type;
    private String foodTags;
    private String description;
    private int price;
    private String picture;
    private DishStatus status;

    // Draft dish fields (optional, nullable)
    private String draftName;
    private DishType draftType;
    private String draftFoodTags;
    private String draftDescription;
    private Integer draftPrice;
    private String draftPicture;
    private DishStatus draftStatus;

    public DishDto() {}

    public DishDto(String id, String name, DishType type, String foodTags,
                   String description, int price, String picture, DishStatus status,
                   String draftName, DishType draftType, String draftFoodTags,
                   String draftDescription, Integer draftPrice, String draftPicture,
                   DishStatus draftStatus){
        this.id = id;
        this.name = name;
        this.type = type;
        this.foodTags = foodTags;
        this.description = description;
        this.price = price;
        this.picture = picture;
        this.status = status;

        this.draftName = draftName;
        this.draftType = draftType;
        this.draftFoodTags = draftFoodTags;
        this.draftDescription = draftDescription;
        this.draftPrice = draftPrice;
        this.draftPicture = draftPicture;
        this.draftStatus = draftStatus;
    }

    // Convert Domain -> DTO including both published and draft
    public static DishDto fromDomain(Dish dish) {
        DishDraft draft = dish.getDraft();

        return new DishDto(
                dish.getId().uuid().toString(),
                dish.getName(),
                dish.getType(),
                dish.getFoodTags(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getPicture(),
                dish.getStatus(),

                draft != null ? draft.name() : null,
                draft != null ? draft.type() : null,
                draft != null ? draft.foodTags() : null,
                draft != null ? draft.description() : null,
                draft != null ? draft.price() : null,
                draft != null ? draft.picture() : null,
                draft != null ? draft.status() : null
        );
    }

    // Convert DTO -> Domain
    public Dish toDomain() {
        return new Dish(
                new DishId(UUID.fromString(id)),
                name,
                type,
                foodTags,
                description,
                price,
                picture,
                status

        );
    }

    public DishDraft convertDtoToDraft(DishDto dto) {
        return new DishDraft(
                dto.getName(),
                dto.getType(),
                dto.getFoodTags(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getPicture(),
                dto.getStatus()
        );
    }

    public DishDraft toDraft() {
        return new DishDraft(
                this.name,
                this.type,
                this.foodTags,
                this.description,
                this.price,
                this.picture,
                this.status
        );
    }


    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DishType getType() { return type; }
    public void setType(DishType type) { this.type = type; }
    public String getFoodTags() { return foodTags; }
    public void setFoodTags(String foodTag) { this.foodTags = foodTag; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
    public DishStatus getStatus() {return status;}
    public void setStatus(DishStatus status) {this.status = status;}

    public String getDraftName() {
        return draftName;
    }

    public void setDraftName(String draftName) {
        this.draftName = draftName;
    }

    public DishType getDraftType() {
        return draftType;
    }

    public void setDraftType(DishType draftType) {
        this.draftType = draftType;
    }

    public String getDraftFoodTags() {
        return draftFoodTags;
    }

    public void setDraftFoodTags(String draftFoodTags) {
        this.draftFoodTags = draftFoodTags;
    }

    public String getDraftDescription() {
        return draftDescription;
    }

    public void setDraftDescription(String draftDescription) {
        this.draftDescription = draftDescription;
    }

    public Integer getDraftPrice() {
        return draftPrice;
    }

    public void setDraftPrice(Integer draftPrice) {
        this.draftPrice = draftPrice;
    }

    public String getDraftPicture() {
        return draftPicture;
    }

    public void setDraftPicture(String draftPicture) {
        this.draftPicture = draftPicture;
    }

    public DishStatus getDraftStatus() {
        return draftStatus;
    }

    public void setDraftStatus(DishStatus draftStatus) {
        this.draftStatus = draftStatus;
    }
}

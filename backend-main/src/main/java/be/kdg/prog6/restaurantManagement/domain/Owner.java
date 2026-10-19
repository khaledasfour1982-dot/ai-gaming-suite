package be.kdg.prog6.restaurantManagement.domain;

// entity
public class Owner {
    private OwnerId ownerId;
    private String name;
    private Restaurant restaurant;

    public Owner(String name, Restaurant restaurant) {
        ownerId = OwnerId.create();
        this.name = name;
        this.restaurant = restaurant;
    }

    public OwnerId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OwnerId ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", restaurant=" + restaurant +
                '}';
    }
}

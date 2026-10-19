package be.kdg.prog6.ordering.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Basket {
    private final UUID id;
    private Customer customer; // nullable at first
    private List<DishId> dishIds;
    private UUID restaurantId;
    private boolean hasOutOfStockDish;

    public Basket(UUID id, Customer customer, List<DishId> dishIds, UUID restaurantId) {
        this.id = id;
        this.customer = customer; // may be null if not assigned yet
        this.dishIds = new ArrayList<>(dishIds);
        this.restaurantId = restaurantId;
    }

    public void markDishOutOfStock(DishId dishId) {
        if (dishIds.contains(dishId)) {
            hasOutOfStockDish = true;
        }
    }

    public boolean hasOutOfStockDish() {
        return hasOutOfStockDish;
    }

    public UUID getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    // Assign customer info (at checkout)
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<DishId> getDishIds() {
        return Collections.unmodifiableList(dishIds);
    }

    public void setDishIds(List<DishId> dishIds) {
        this.dishIds = new ArrayList<>(dishIds);
    }

    public void addDish(DishId dishId) {
        this.dishIds.add(dishId);
    }

    public void removeDish(DishId dishId) {
        this.dishIds.remove(dishId);
    }

    public void clearDishes() {
        this.dishIds.clear();
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }
}

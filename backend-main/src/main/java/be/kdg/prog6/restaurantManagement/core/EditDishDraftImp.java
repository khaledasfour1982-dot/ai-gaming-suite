package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.domain.*;
import be.kdg.prog6.restaurantManagement.ports.in.EditDishDraftPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.out.SaveRestaurantPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditDishDraftImp implements EditDishDraftPort {
    private final LoadRestaurantsPort loadRestaurantsPort;
    private final SaveRestaurantPort saveRestaurantPort;

    public EditDishDraftImp(LoadRestaurantsPort loadRestaurantsPort, SaveRestaurantPort saveRestaurantPort) {
        this.loadRestaurantsPort = loadRestaurantsPort;
        this.saveRestaurantPort = saveRestaurantPort;
    }

    @Transactional
    @Override
    public Dish editDish(RestaurantId restaurantId, DishId dishId, DishDraft draft) {
        Restaurant restaurant = loadRestaurantsPort.loadById(restaurantId).orElseThrow();
        Dish dish = restaurant.getDishById(dishId);
        DishStatus currentStatus = dish.getStatus();
        dish.editDraft(
                draft.name(),
                draft.type(),
                draft.foodTags(),
                draft.description(),
                draft.price(),
                draft.picture(),
                currentStatus
        );

        saveRestaurantPort.saveRestaurant(restaurant);

        return dish;
    }



}

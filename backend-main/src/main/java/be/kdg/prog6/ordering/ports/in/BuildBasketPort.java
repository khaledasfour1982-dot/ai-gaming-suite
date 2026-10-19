package be.kdg.prog6.ordering.ports.in;

import be.kdg.prog6.ordering.domain.Basket;
import be.kdg.prog6.ordering.domain.Customer;
import be.kdg.prog6.ordering.domain.DishId;

import java.util.List;
import java.util.UUID;


public interface BuildBasketPort {
        Basket createBasket(List<DishId> dishIds, String restaurantId);
        Basket loadBasket(UUID basketId);
        Basket addDishes(UUID basketId, List<DishId> dishIds);
        Basket removeDish(UUID basketId, DishId dishId);
        Basket clearBasket(UUID basketId);
        Basket assignCustomerInfo(UUID basketId, Customer customer); // for checkout


}

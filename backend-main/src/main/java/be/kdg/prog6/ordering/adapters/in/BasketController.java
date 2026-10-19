package be.kdg.prog6.ordering.adapters.in;

import be.kdg.prog6.ordering.adapters.in.request.CreateBasketDto;
import be.kdg.prog6.ordering.adapters.in.request.CustomerDto;
import be.kdg.prog6.ordering.adapters.in.response.BasketDto;
import be.kdg.prog6.ordering.adapters.in.response.DishStatusDto;
import be.kdg.prog6.ordering.core.BuildBasketImp;
import be.kdg.prog6.ordering.core.OutOfStockDishProjectionService;
import be.kdg.prog6.ordering.domain.Basket;
import be.kdg.prog6.ordering.domain.Customer;
import be.kdg.prog6.ordering.domain.DishId;
import be.kdg.prog6.ordering.ports.in.BuildBasketPort;
import be.kdg.prog6.restaurantManagement.core.CreateRestaurantImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/basket")
public class BasketController {

    private final BuildBasketPort buildBasketPort;
    private final Logger logger = LoggerFactory.getLogger(BasketController.class);
    private final OutOfStockDishProjectionService outOfStockDishProjectionService;

    public BasketController(BuildBasketPort buildBasketPort, OutOfStockDishProjectionService outOfStockDishProjectionService) {
        this.buildBasketPort = buildBasketPort;
        this.outOfStockDishProjectionService = outOfStockDishProjectionService;
    }

    @PostMapping()
    public ResponseEntity<BasketDto> createBasket(@RequestBody CreateBasketDto request) {
        List<DishId> dishIds = request.dishIds().stream()
                .map(UUID::fromString)
                .map(DishId::new)
                .collect(Collectors.toList());
        Basket basket = buildBasketPort.createBasket(dishIds, request.restaurantId());
        logger.info("Basket created with restaurantId: " + basket.getRestaurantId());
        List<DishStatusDto> dishStatuses = basket.getDishIds().stream()
                .map(dishId -> {
                    var info = outOfStockDishProjectionService.getDishInfo(dishId.id());
                    boolean outOfStock = outOfStockDishProjectionService.isOutOfStock(dishId);
                    return new DishStatusDto(
                            dishId.id().toString(),
                            info != null ? info.name() : "Unknown dish",
                            info != null ? info.price() : 0.0,
                            outOfStock
                    );
                })
                .toList();
        return ResponseEntity.ok(BasketDto.fromDomain(basket,dishStatuses));
    }


    // Add customer info to basket during checkout
    @PutMapping("/{basketId}/customer")
    public ResponseEntity<BasketDto> assignCustomerToBasket(
            @PathVariable UUID basketId,
            @RequestBody CustomerDto customerDto) {
        Customer customer = customerDto.toDomain();
        Basket updatedBasket = buildBasketPort.assignCustomerInfo(basketId, customer);
        List<DishStatusDto> dishStatuses = updatedBasket.getDishIds().stream()
                .map(dishId -> {
                    var info = outOfStockDishProjectionService.getDishInfo(dishId.id());
                    boolean outOfStock = outOfStockDishProjectionService.isOutOfStock(dishId);
                    return new DishStatusDto(
                            dishId.id().toString(),
                            info != null ? info.name() : "Unknown dish",
                            info != null ? info.price() : 0.0,
                            outOfStock
                    );
                })
                .toList();
        return ResponseEntity.ok(BasketDto.fromDomain(updatedBasket,dishStatuses));
    }

    @PostMapping("/{basketId}/addDishes")
    public ResponseEntity<BasketDto> addDishes(@PathVariable UUID basketId, @RequestBody List<String> dishIds) {
        List<DishId> dishIdList = dishIds.stream()
                .map(UUID::fromString)
                .map(DishId::new)
                .collect(Collectors.toList());
        Basket updatedBasket = buildBasketPort.addDishes(basketId, dishIdList);
        List<DishStatusDto> dishStatuses = updatedBasket.getDishIds().stream()
                .map(dishId -> {
                    var info = outOfStockDishProjectionService.getDishInfo(dishId.id());
                    boolean outOfStock = outOfStockDishProjectionService.isOutOfStock(dishId);
                    return new DishStatusDto(
                            dishId.id().toString(),
                            info != null ? info.name() : "Unknown dish",
                            info != null ? info.price() : 0.0,
                            outOfStock
                    );
                })
                .toList();
        return ResponseEntity.ok(BasketDto.fromDomain(updatedBasket,dishStatuses));
    }

    @DeleteMapping("/{basketId}/dish/{dishId}")
    public ResponseEntity<Void> removeDish(@PathVariable UUID basketId, @PathVariable String dishId) {
        buildBasketPort.removeDish(basketId, new DishId(UUID.fromString(dishId)));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{basketId}/clear")
    public ResponseEntity<Void> clearBasket(@PathVariable UUID basketId) {
        buildBasketPort.clearBasket(basketId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{basketId}")
    public ResponseEntity<BasketDto> getBasket(@PathVariable UUID basketId) {
        Basket basket = buildBasketPort.loadBasket(basketId);

        List<DishStatusDto> dishStatuses = basket.getDishIds().stream()
                .map(dishId -> {
                    var info = outOfStockDishProjectionService.getDishInfo(dishId.id());
                    boolean outOfStock = outOfStockDishProjectionService.isOutOfStock(dishId);
                    return new DishStatusDto(
                            dishId.id().toString(),
                            info != null ? info.name() : "Unknown dish",
                            info != null ? info.price() : 0.0,
                            outOfStock
                    );
                })
                .toList();


        BasketDto dto = BasketDto.fromDomain(basket, dishStatuses);
        return ResponseEntity.ok(dto);
    }

}

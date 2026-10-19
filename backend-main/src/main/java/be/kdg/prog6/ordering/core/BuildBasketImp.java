package be.kdg.prog6.ordering.core;

import be.kdg.prog6.ordering.adapters.in.request.CustomerDto;
import be.kdg.prog6.ordering.adapters.in.response.BasketDto;
import be.kdg.prog6.ordering.adapters.out.BasketJpaEntity;
import be.kdg.prog6.ordering.adapters.out.BasketJpaRepository;
import be.kdg.prog6.ordering.domain.*;
import be.kdg.prog6.ordering.ports.in.BuildBasketPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildBasketImp implements BuildBasketPort {

    private final BasketJpaRepository basketJpaRepository;
    private final Logger logger = LoggerFactory.getLogger(BuildBasketImp.class);
    private final OutOfStockDishProjectionService outOfStockDishProjectionService;

    public BuildBasketImp(BasketJpaRepository basketJpaRepository, OutOfStockDishProjectionService outOfStockDishProjectionService) {
        this.basketJpaRepository = basketJpaRepository;
        this.outOfStockDishProjectionService = outOfStockDishProjectionService;
    }

    @Override
    public Basket createBasket(List<DishId> dishIds, String restaurantId) {
        UUID restaurantUUID = null;
        if (restaurantId != null && !restaurantId.isEmpty()) {
            restaurantUUID = UUID.fromString(restaurantId);
        }
        logger.info("Creating new basket with restaurant id {}", restaurantUUID);
        Basket basket = new Basket(UUID.randomUUID(), null, dishIds, restaurantUUID);
        logger.info("Basket created with id: {}", basket.getId());
        BasketJpaEntity entity = fromDomain(basket);
        entity = basketJpaRepository.save(entity);
        return toDomain(entity);
    }

    public void validateBasketForCheckout(UUID basketId) {
        Basket basket = loadBasket(basketId);

        if (basket == null || basket.getDishIds() == null || basket.getDishIds().isEmpty()) {
            throw new IllegalArgumentException("Basket not found or empty");
        }

        List<DishId> outOfStockDishes = basket.getDishIds().stream()
                .filter(outOfStockDishProjectionService::isOutOfStock)
                .toList();

        if (!outOfStockDishes.isEmpty()) {
            String outOfStockIds = outOfStockDishes.stream()
                    .map(id -> id.id().toString())
                    .collect(Collectors.joining(", "));

            throw new IllegalStateException(
                    "Cannot checkout. The following dishes are out of stock: " + outOfStockIds);
        }
    }




    private BasketJpaEntity fromDomain(Basket basket) {
        BasketJpaEntity entity = new BasketJpaEntity();
        entity.setId(basket.getId());
        if (basket.getCustomer() != null) {
            entity.setCustomer(toCustomerEmbeddable(basket.getCustomer()));
        } else {
            entity.setCustomer(null);
        }
        entity.setDishIds(mapDishDomainToEntities(basket.getDishIds()));
        entity.setRestaurantId(basket.getRestaurantId() != null ? basket.getRestaurantId().toString() : null);
        return entity;
    }

    private CustomerEmbeddable toCustomerEmbeddable(Customer customer) {
        return new CustomerEmbeddable(
                customer.getName(),
                toAddressEmbeddable(customer.getDeliveryAddress()),
                customer.getContactEmail());
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
        return new AddressEmbeddable(address);
    }


    public Basket loadBasket(UUID basketId) {
        Optional<BasketJpaEntity> entity = basketJpaRepository.findById(basketId);
        return entity
                .map(this::toDomain)
                .orElseGet(() -> new Basket(UUID.randomUUID() ,null, List.of(),null));
    }



    private Basket toDomain(BasketJpaEntity entity) {
        Customer customer = mapCustomerEntityToDomain(entity.getCustomer());
        List<DishId> dishIds = mapDishEntitiesToDomain(entity.getDishIds());
        String restaurantIdStr = entity.getRestaurantId();
        UUID restaurantId = restaurantIdStr != null ? UUID.fromString(restaurantIdStr) : null;
        return new Basket(entity.getId(), customer, dishIds, restaurantId);
    }

    private Customer mapCustomerEntityToDomain(CustomerEmbeddable customerEmbeddable) {
        if (customerEmbeddable == null) return null;
        Address address = customerEmbeddable.getDeliveryAddress().toDomain();
        return new Customer(
                customerEmbeddable.getName(),
                address,
                customerEmbeddable.getContactEmail()
        );
    }


    private List<DishId> mapDishEntitiesToDomain(List<UUID> dishIds) {
        if (dishIds == null || dishIds.isEmpty()) {
            return List.of();
        }
        return dishIds.stream()
                .map(DishId::new)
                .toList();
    }

    private List<UUID> mapDishDomainToEntities(List<DishId> dishIds) {
        if (dishIds == null || dishIds.isEmpty()) {
            return List.of();
        }
        return dishIds.stream()
                .map(DishId::id)
                .toList();
    }

    public Basket addDishes(UUID basketId, List<DishId> dishIds) {
        Basket basket = loadBasket(basketId); // domain object
        for (DishId dishId : dishIds) {
            if (!basket.getDishIds().contains(dishId)) {
                basket.addDish(dishId);
            }
        }
        BasketJpaEntity entity = fromDomain(basket);
        entity = basketJpaRepository.save(entity);
        return toDomain(entity);
    }

    public Basket removeDish(UUID basketId, DishId dishId) {
        Basket basket = loadBasket(basketId);
        basket.removeDish(dishId);
        BasketJpaEntity entity = fromDomain(basket);
        entity = basketJpaRepository.save(entity);
        return toDomain(entity);
    }


    public Basket clearBasket(UUID basketId) {
        Basket basket = loadBasket(basketId);
        basket.clearDishes();
        BasketJpaEntity entity = fromDomain(basket);
        entity = basketJpaRepository.save(entity);
        return toDomain(entity);
    }


    // Assign customer information to basket during checkout
    public Basket assignCustomerInfo(UUID basketId, Customer customer) {
        BasketJpaEntity entity = basketJpaRepository.findById(basketId)
                .orElseThrow(() -> new IllegalArgumentException("Basket not found: " + basketId));

        CustomerEmbeddable customerEmbeddable = entity.getCustomer();
        if (customerEmbeddable == null) {
            customerEmbeddable = new CustomerEmbeddable();
            entity.setCustomer(customerEmbeddable);
        }

        customerEmbeddable.setName(customer.getName());
        customerEmbeddable.setContactEmail(customer.getContactEmail());

        if (customerEmbeddable.getDeliveryAddress() == null) {
            customerEmbeddable.setDeliveryAddress(new AddressEmbeddable());
        }

        customerEmbeddable.getDeliveryAddress().setStreet(customer.getDeliveryAddress().street());
        customerEmbeddable.getDeliveryAddress().setNumber(customer.getDeliveryAddress().number());
        customerEmbeddable.getDeliveryAddress().setPostalCode(customer.getDeliveryAddress().postalCode());
        customerEmbeddable.getDeliveryAddress().setCity(customer.getDeliveryAddress().city());
        customerEmbeddable.getDeliveryAddress().setCountry(customer.getDeliveryAddress().country());

        basketJpaRepository.save(entity);
        return toDomain(entity);
    }

    public void markDishAsOutOfStockInBasket(UUID dishId) {
        List<BasketJpaEntity> basketEntitiesWithDish = basketJpaRepository.findByDishIdsContaining(dishId);

        for (BasketJpaEntity entity : basketEntitiesWithDish) {
            Basket basket = toDomain(entity);
            basket.markDishOutOfStock(new DishId(dishId));

            BasketJpaEntity updatedEntity = fromDomain(basket);
            basketJpaRepository.save(updatedEntity);
        }
    }



}

package be.kdg.prog6.restaurantManagement.adapters.out;

import be.kdg.prog6.restaurantManagement.adapters.out.DishJpaEntity;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.domain.DishId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DishJpaRepository extends JpaRepository<DishJpaEntity, UUID> {
    Optional<DishJpaEntity> findByRestaurant_IdAndId(UUID restaurantId, UUID dishId);
    List<DishJpaEntity> findByRestaurant_Id(UUID restaurantId);
}



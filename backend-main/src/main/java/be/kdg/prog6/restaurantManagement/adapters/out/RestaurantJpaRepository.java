package be.kdg.prog6.restaurantManagement.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface RestaurantJpaRepository extends JpaRepository<RestaurantJpaEntity, UUID> {
    List<RestaurantJpaEntity> findByOwner(String owner);

}

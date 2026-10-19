package be.kdg.prog6.ordering.adapters.out;

import be.kdg.prog6.ordering.domain.Basket;
import be.kdg.prog6.ordering.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BasketJpaRepository extends JpaRepository<BasketJpaEntity, UUID> {
    Basket save(Basket basket);
    Optional<Basket> findByCustomer(Customer customer);

    @Query("SELECT b FROM BasketJpaEntity b JOIN b.dishIds d WHERE d = :dishId")
    List<BasketJpaEntity> findByDishIdsContaining(@Param("dishId") UUID dishId);

}

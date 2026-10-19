package be.kdg.prog6.ordering.adapters.out;

import be.kdg.prog6.ordering.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {
}

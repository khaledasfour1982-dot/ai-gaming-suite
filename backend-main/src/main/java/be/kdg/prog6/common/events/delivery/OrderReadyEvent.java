package be.kdg.prog6.common.events.delivery;

import org.springframework.modulith.events.Externalized;
import java.util.UUID;
import java.time.Instant;

@Externalized("kdg.events::restaurant.#{restaurantId()}.order.ready.v1")
public record OrderReadyEvent(UUID orderId, UUID restaurantId, Instant occurredAt) {}

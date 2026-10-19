package be.kdg.prog6.common.events.delivery;

import java.time.Instant;
import java.util.UUID;

import org.springframework.modulith.events.Externalized;


@Externalized("kdg.events::delivery.#{courierId()}.order.pickedup.v1")
public record OrderPickedUpEvent(
        UUID orderId,
        UUID courierId,
        Instant occurredAt
) {}
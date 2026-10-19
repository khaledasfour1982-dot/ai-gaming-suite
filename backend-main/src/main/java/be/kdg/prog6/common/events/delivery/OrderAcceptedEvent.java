package be.kdg.prog6.common.events.delivery;

import org.springframework.modulith.events.Externalized;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;

@Externalized("kdg.events::restaurant.#{restaurantId()}.order.accepted.v1")
public class OrderAcceptedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID orderId;
    private UUID restaurantId;
    private Instant acceptedAt;
    public OrderAcceptedEvent(UUID orderId, UUID restaurantId, Instant acceptedAt) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.acceptedAt = acceptedAt;
    }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }
}

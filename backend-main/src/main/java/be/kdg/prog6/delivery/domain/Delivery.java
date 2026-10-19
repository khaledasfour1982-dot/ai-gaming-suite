package be.kdg.prog6.delivery.domain;

import java.util.UUID;
import java.time.Instant;

public class Delivery {
    private final UUID deliveryId;
    private final UUID orderId;
    private UUID courierId;
    private DeliveryStatus status;
    private Instant createdAt;
    private Instant pickedUpAt;
    private Instant deliveredAt;

    public Delivery(UUID deliveryId, UUID orderId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.status = DeliveryStatus.ASSIGNED;
        this.createdAt = Instant.now();
    }

    public UUID getDeliveryId() { return deliveryId; }
    public UUID getOrderId() { return orderId; }
    public UUID getCourierId() { return courierId; }
    public DeliveryStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPickedUpAt() { return pickedUpAt; }
    public Instant getDeliveredAt() { return deliveredAt; }

    public void assignCourier(UUID courierId) {
        this.courierId = courierId;
        this.status = DeliveryStatus.ASSIGNED;
    }

    public void markPickedUp() {
        this.status = DeliveryStatus.PICKED_UP;
        this.pickedUpAt = Instant.now();
    }

    public void markDelivered() {
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = Instant.now();
    }
}

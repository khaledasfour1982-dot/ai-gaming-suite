package be.kdg.prog6.delivery.core;


import be.kdg.prog6.delivery.domain.Delivery;

public class DeliveryStatusUpdater {
    public void markPickedUp(Delivery delivery) {
        delivery.markPickedUp();
        // publish event
    }
    public void markDelivered(Delivery delivery) {
        delivery.markDelivered();
        // publish event
    }
}

package be.kdg.prog6.ordering.core;

import be.kdg.prog6.ordering.domain.Order;
import be.kdg.prog6.ordering.ports.out.LoadOrderPort;
import be.kdg.prog6.ordering.ports.out.SaveOrderPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import be.kdg.prog6.common.events.delivery.OrderReadyEvent;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class ConfirmOrderImp {
    private final ApplicationEventPublisher publisher;
    private final SaveOrderPort saveOrderPort;
    private final LoadOrderPort loadOrderPort;

    public ConfirmOrderImp(ApplicationEventPublisher publisher, SaveOrderPort saveOrderPort, LoadOrderPort loadOrderPort) {
        this.publisher = publisher;
        this.saveOrderPort = saveOrderPort;
        this.loadOrderPort = loadOrderPort;
    }

    public void markOrderReady(UUID orderId, UUID restaurantId) {
        Order order = saveOrderPort.loadById(orderId);
            order.markReady();
            saveOrderPort.save(order);
            publisher.publishEvent(new OrderReadyEvent(orderId, restaurantId, Instant.now()));
        }

    public List<Order> getOrders() {
        return loadOrderPort.loadAllOrders();
    }
}





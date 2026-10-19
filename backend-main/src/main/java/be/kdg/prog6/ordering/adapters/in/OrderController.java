package be.kdg.prog6.ordering.adapters.in;

import be.kdg.prog6.ordering.core.ConfirmOrderImp;
import be.kdg.prog6.ordering.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final ConfirmOrderImp confirmOrderImp;

    public OrderController(ConfirmOrderImp confirmOrderImp) {
        this.confirmOrderImp = confirmOrderImp;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = confirmOrderImp.getOrders();
        return ResponseEntity.ok(orders);
    }


    @PostMapping("/{orderId}/ready")
    public ResponseEntity<Void> markOrderReady(@PathVariable UUID orderId, @RequestParam UUID restaurantId) {
        confirmOrderImp.markOrderReady(orderId, restaurantId);
        return ResponseEntity.ok().build();
    }
}

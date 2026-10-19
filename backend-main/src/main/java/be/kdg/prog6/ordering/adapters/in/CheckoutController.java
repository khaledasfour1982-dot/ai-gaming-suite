package be.kdg.prog6.ordering.adapters.in;

import be.kdg.prog6.ordering.core.BuildBasketImp;
import be.kdg.prog6.ordering.core.CheckoutOrderImp;
import be.kdg.prog6.ordering.domain.Order;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutOrderImp checkoutOrderImp;
    private final BuildBasketImp buildBasketImp;

    public CheckoutController(CheckoutOrderImp checkoutOrderImp, BuildBasketImp buildBasketImp) {
        this.checkoutOrderImp = checkoutOrderImp;
        this.buildBasketImp = buildBasketImp;
    }

    @PostMapping("/{basketId}")
    public ResponseEntity<Order> checkout(@PathVariable UUID basketId) {
        try {
            buildBasketImp.validateBasketForCheckout(basketId);
            Order order = checkoutOrderImp.checkout(basketId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

package be.kdg.prog6.ordering.core;

import be.kdg.prog6.ordering.domain.DishId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Transactional
@Service
public class OutOfStockDishProjectionService {
    private final Set<UUID> outOfStockDishIds = ConcurrentHashMap.newKeySet();

    private final Map<UUID, DishInfo> dishInfoMap = new ConcurrentHashMap<>();

    public void markOutOfStock(UUID dishId) {
        outOfStockDishIds.add(dishId);
    }

    public void markRestocked(UUID dishId) {
        outOfStockDishIds.remove(dishId);
    }

    public boolean isOutOfStock(DishId dishId) {
        return outOfStockDishIds.contains(dishId.id());
    }


    public void updateDishInfo(UUID dishId, String name, double price) {
        dishInfoMap.put(dishId, new DishInfo(dishId, name, price));
    }

    public DishInfo getDishInfo(UUID dishId) {
        return dishInfoMap.get(dishId);
    }



    public static record DishInfo(UUID id, String name, double price) {}
}

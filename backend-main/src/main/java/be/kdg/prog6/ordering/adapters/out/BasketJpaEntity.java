package be.kdg.prog6.ordering.adapters.out;

import be.kdg.prog6.ordering.domain.Customer;
import be.kdg.prog6.ordering.domain.CustomerEmbeddable;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "basket", schema = "kdg")
public class BasketJpaEntity {
    @Id
    private UUID id;

    @Embedded
    private CustomerEmbeddable customer;

    @ElementCollection
    @CollectionTable(name = "basket_dish_ids", joinColumns = @JoinColumn(name = "basket_id"))
    @Column(name = "dish_id")
    private List<UUID> dishIds;

    @Column()
    private String restaurantId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CustomerEmbeddable getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEmbeddable customer) {
        this.customer = customer;
    }

    public List<UUID> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<UUID> dishIds) {
        this.dishIds = dishIds;
    }
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}

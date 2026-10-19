package be.kdg.prog6.restaurantManagement.core;

import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.ports.in.ExploreRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExploreRestaurantsImp implements ExploreRestaurantsPort {
    private final LoadRestaurantsPort loadRestaurantsPort;

    public ExploreRestaurantsImp(LoadRestaurantsPort loadRestaurantsPort) {
        this.loadRestaurantsPort = loadRestaurantsPort;
    }


    @Override
    public List<Restaurant> findAll() {
        return this.loadRestaurantsPort.loadAll();
    }
}

package be.kdg.prog6.restaurantManagement.adapters.in;

import be.kdg.prog6.restaurantManagement.adapters.in.response.RestaurantDto;
import be.kdg.prog6.restaurantManagement.domain.Restaurant;
import be.kdg.prog6.restaurantManagement.domain.RestaurantDetails;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.ports.in.ExploreRestaurantsPort;
import be.kdg.prog6.restaurantManagement.ports.in.ViewRestaurantDetailsPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurants")
public class CustomerController {

    private final ExploreRestaurantsPort exploreRestaurantsPort;
    private final ViewRestaurantDetailsPort viewRestaurantDetailsPort;

    public CustomerController(ExploreRestaurantsPort exploreRestaurantsPort, ViewRestaurantDetailsPort viewRestaurantDetailsPort) {
        this.exploreRestaurantsPort = exploreRestaurantsPort;
        this.viewRestaurantDetailsPort = viewRestaurantDetailsPort;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        List<Restaurant> restaurants = exploreRestaurantsPort.findAll();
        List<RestaurantDto> dtos = restaurants.stream()
                .map(RestaurantDto::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> getRestaurantDetails(@PathVariable String restaurantId) {
        System.out.println("Fetching restaurant with ID: " + restaurantId);
        RestaurantDetails details = viewRestaurantDetailsPort.viewDetails(new RestaurantId(UUID.fromString(restaurantId)));
        if (details == null) {
            System.out.println("Restaurant not found!");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(RestaurantDto.fromDomain(details));
    }
}

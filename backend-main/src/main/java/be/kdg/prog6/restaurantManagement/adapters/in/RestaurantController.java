package be.kdg.prog6.restaurantManagement.adapters.in;

import be.kdg.prog6.restaurantManagement.adapters.in.response.WeeklyScheduleDto;
import be.kdg.prog6.restaurantManagement.core.CreateRestaurantImp;
import be.kdg.prog6.restaurantManagement.domain.RestaurantId;
import be.kdg.prog6.restaurantManagement.adapters.in.response.DishDto;
import be.kdg.prog6.restaurantManagement.adapters.in.response.RestaurantDto;
import be.kdg.prog6.restaurantManagement.domain.*;
import be.kdg.prog6.restaurantManagement.ports.in.*;
import be.kdg.prog6.restaurantManagement.ports.out.LoadDishPort;
import be.kdg.prog6.restaurantManagement.ports.out.LoadRestaurantsPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/owner/restaurants")
public class RestaurantController {
    private final CreateRestaurantPort createRestaurantPort;
    private final EditDishDraftPort editDishDraftPort;
    private final PublishDishDraftPort publishDishDraftPort;
    private final UnpublishDishDraftPort unpublishDishDraftPort;
    private final ApplyPendingDishChangesPort applyPendingDishChangesPort;
    private final MarkDishOutOfStockPort markDishOutOfStockPort;
    private final SetOpeningHoursPort setOpeningHoursPort;
    private final LoadRestaurantsPort loadRestaurantsPort;
    private final LoadDishPort loadDishPort;
    private final AddDishPort addDishPort;
    private final Logger logger = LoggerFactory.getLogger(CreateRestaurantImp.class);

    public RestaurantController(CreateRestaurantPort createRestaurantPort, EditDishDraftPort editDishDraftPort, PublishDishDraftPort publishDishDraftPort, UnpublishDishDraftPort unpublishDishDraftPort, ApplyPendingDishChangesPort applyPendingDishChangesPort, MarkDishOutOfStockPort markDishOutOfStockPort, SetOpeningHoursPort setOpeningHoursPort, LoadRestaurantsPort loadRestaurantsPort, LoadDishPort loadDishPort, AddDishPort addDishPort) {
        this.createRestaurantPort = createRestaurantPort;
        this.editDishDraftPort = editDishDraftPort;
        this.publishDishDraftPort = publishDishDraftPort;
        this.unpublishDishDraftPort = unpublishDishDraftPort;
        this.applyPendingDishChangesPort = applyPendingDishChangesPort;
        this.markDishOutOfStockPort = markDishOutOfStockPort;
        this.setOpeningHoursPort = setOpeningHoursPort;
        this.loadRestaurantsPort = loadRestaurantsPort;
        this.loadDishPort = loadDishPort;
        this.addDishPort = addDishPort;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RestaurantDto dto) {
        String owner = dto.owner();
        List<Restaurant> existing = loadRestaurantsPort.loadByOwner(owner);
        if (!existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("You can only create one restaurant per owner.");
        }
        Restaurant restaurant = dto.toDomain();
        Restaurant saved = createRestaurantPort.create(restaurant);
        return ResponseEntity.ok(RestaurantDto.fromDomain(saved));
    }

    @PatchMapping("/{restaurantId}/dishes/{dishId}")
    public ResponseEntity<DishDto> editDish(@PathVariable String restaurantId,
                                            @PathVariable String dishId,
                                            @RequestBody DishDto dto) {
        DishDraft draft = new DishDraft(
                dto.getName(),
                dto.getType(),
                dto.getFoodTags(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getPicture(),
                dto.getStatus()
        );

        Dish editedDish = editDishDraftPort.editDish(
                new RestaurantId(UUID.fromString(restaurantId)),
                new DishId(UUID.fromString(dishId)),
                draft
        );

        return ResponseEntity.ok(DishDto.fromDomain(editedDish));
    }

    @PatchMapping("/{restaurantId}/dishes/{dishId}/publish")
    public ResponseEntity<DishDto> publishDishDraft(@PathVariable UUID restaurantId,
                                                    @PathVariable UUID dishId) {
        Dish publishedDish = publishDishDraftPort.publishDish(new RestaurantId(restaurantId),new DishId(dishId));
        return ResponseEntity.ok(DishDto.fromDomain(publishedDish));
    }

    @PostMapping("/{restaurantId}/dishes/{dishId}/unpublish")
    public ResponseEntity<DishDto> unpublishDish(@PathVariable UUID restaurantId, @PathVariable UUID dishId) {
        Dish dish = unpublishDishDraftPort.unpublishDish(new DishId(dishId), new RestaurantId(restaurantId));
        return ResponseEntity.ok(DishDto.fromDomain(dish));
    }


    @PostMapping("/{restaurantId}/dishes/apply-pending")
    public ResponseEntity<Void> applyPendingChanges(@PathVariable String restaurantId) {
        applyPendingDishChangesPort.applyPendingDishChanges(new RestaurantId(UUID.fromString(restaurantId)));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{restaurantId}/dishes/{dishId}/out-of-stock")
    public ResponseEntity<DishDto> markOutOfStock(@PathVariable UUID restaurantId,
                                                  @PathVariable UUID dishId) {
        Dish dish = markDishOutOfStockPort.markOutOfStock(new DishId(dishId), new RestaurantId(restaurantId));
        return ResponseEntity.ok(DishDto.fromDomain(dish));
    }

    @PostMapping("/{restaurantId}/opening-hours")
    public ResponseEntity<Void> setOpeningHours(@PathVariable String restaurantId, @RequestBody WeeklyScheduleDto dto) {
        setOpeningHoursPort.setOpeningHours(new RestaurantId(UUID.fromString(restaurantId)), dto.toDomain());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getOwnerRestaurants(
            @AuthenticationPrincipal Jwt jwt) {

        // Get owner name from JWT token
        String ownerName = jwt.getClaimAsString("preferred_username");
        if (ownerName == null) {
            ownerName = jwt.getClaimAsString("name");
        }
        System.out.println(" GET /owner/restaurants: Looking for owner: " + ownerName);
        List<Restaurant> restaurants = loadRestaurantsPort.loadByOwner(ownerName);
        System.out.println(" GET /owner/restaurants: Found " + restaurants.size() + " restaurants");
        List<RestaurantDto> dtos = restaurants.stream()
                .map(RestaurantDto::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{restaurantId}/dishes/{dishId}/draft")
    public ResponseEntity<DishDto> editDishDraft(@PathVariable UUID restaurantId,
                                                 @PathVariable UUID dishId,
                                                 @RequestBody DishDto draftDto) {
        DishDraft draft = new DishDraft(draftDto.getName(), draftDto.getType(), draftDto.getFoodTags(),
                draftDto.getDescription(), draftDto.getPrice(), draftDto.getPicture(),draftDto.getStatus());
        Dish editedDish = editDishDraftPort.editDish(
                new RestaurantId(restaurantId),
                new DishId(dishId),
                draft);
        logger.info("After editing dish: " + editedDish.getName());
        return ResponseEntity.ok(DishDto.fromDomain(editedDish));
    }

    @GetMapping("/{restaurantId}/dishes")
    public ResponseEntity<List<DishDto>> getDishes(@PathVariable UUID restaurantId) {
        List<Dish> dishes = loadDishPort.loadDishes(new RestaurantId(restaurantId));
        List<DishDto> dishDtos = dishes.stream()
                .map(DishDto::fromDomain)
                .toList();
        return ResponseEntity.ok(dishDtos);
    }


    @GetMapping("/{restaurantId}/dishes/{dishId}")
    public ResponseEntity<DishDto> getDishById(@PathVariable UUID restaurantId, @PathVariable UUID dishId) {
        Optional<Dish> dishOpt = loadDishPort.loadDish(new RestaurantId(restaurantId), new DishId(dishId));

        if (dishOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Dish dish = dishOpt.get();
        return ResponseEntity.ok(DishDto.fromDomain(dish));
    }



    @PostMapping("/{restaurantId}/dishes")
    public ResponseEntity<DishDto> createDishDraft(@PathVariable UUID restaurantId,
                                                   @RequestBody DishDto dishDraftDto) {
        RestaurantId rId = RestaurantId.of(restaurantId);
        DishDraft draft = dishDraftDto.toDraft();
        Dish createdDish = addDishPort.addDish(rId, draft);

        DishDto responseDto = DishDto.fromDomain(createdDish);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable UUID restaurantId) {
        Optional<Restaurant> restaurantOpt = loadRestaurantsPort.loadById(new RestaurantId(restaurantId));
        if (restaurantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        RestaurantDto dto = RestaurantDto.fromDomain(restaurantOpt.get());
        return ResponseEntity.ok(dto);
    }


    @PatchMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable UUID restaurantId, @RequestBody RestaurantDto dto) {
        RestaurantId rId = new RestaurantId(restaurantId);
        Restaurant restaurant = loadRestaurantsPort.loadById(rId).orElseThrow();
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }
        restaurant.setName(dto.name());
        restaurant.setContactEmail(dto.contactEmail());
        restaurant.setPicture(dto.picture());
        restaurant.setTypeOfCuisine(dto.typeOfCuisine());

        Address newAddress = dto.toDomain().getFullAddress();
        restaurant.setFullAddress(newAddress);

        Restaurant saved = createRestaurantPort.update(restaurant);
        return ResponseEntity.ok(RestaurantDto.fromDomain(saved));
    }



}

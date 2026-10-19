package be.kdg.prog6.ordering.adapters.in.request;

import be.kdg.prog6.ordering.adapters.in.response.AddressDto;

import java.util.List;

public record CreateBasketDto (
    String name,
    AddressDto deliveryAddress,
    String contactEmail,
    List<String> dishIds,
    // retrieve id from frontend to use it in order(rabbit)
    String restaurantId
) {}


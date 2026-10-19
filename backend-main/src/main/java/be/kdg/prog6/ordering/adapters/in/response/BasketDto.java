package be.kdg.prog6.ordering.adapters.in.response;

import be.kdg.prog6.ordering.adapters.in.request.CustomerDto;
import be.kdg.prog6.ordering.domain.Basket;

import java.util.List;
import java.util.UUID;

public record BasketDto(
        UUID id,
        CustomerDto customer,
        List<DishStatusDto> dishIds
) {
    public static BasketDto fromDomain(Basket basket, List<DishStatusDto> dishStatuses) {
        return new BasketDto(
                basket.getId(),
                CustomerDto.fromDomain(basket.getCustomer()),
                dishStatuses
        );
    }
}
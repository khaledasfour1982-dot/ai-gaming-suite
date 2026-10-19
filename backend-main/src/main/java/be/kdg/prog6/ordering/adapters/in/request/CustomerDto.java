package be.kdg.prog6.ordering.adapters.in.request;

import be.kdg.prog6.ordering.adapters.in.response.AddressDto;
import be.kdg.prog6.ordering.domain.Customer;

public record CustomerDto(String name, AddressDto deliveryAddress, String contactEmail) {
    public static CustomerDto fromDomain(Customer c) {
        if (c == null) return null;
        return new CustomerDto(c.getName(), AddressDto.fromDomain(c.getDeliveryAddress()), c.getContactEmail());
    }


    public Customer toDomain() {
        return new Customer(name, deliveryAddress.toDomain(), contactEmail);
    }
}

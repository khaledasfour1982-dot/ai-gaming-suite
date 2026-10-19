package be.kdg.prog6.ordering.adapters.in.response;

import be.kdg.prog6.ordering.domain.Address;

public record AddressDto(
        String street,
        String number,
        String postalCode,
        String city,
        String country
) {
    public static AddressDto fromDomain(Address address) {
        return new AddressDto(
                address.street(),
                address.number(),
                address.postalCode(),
                address.city(),
                address.country()
        );
    }

    public Address toDomain() {
        return new be.kdg.prog6.ordering.domain.Address(
                street, number, postalCode, city, country
        );
    }


}

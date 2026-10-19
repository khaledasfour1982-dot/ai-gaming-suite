package be.kdg.prog6.restaurantManagement.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {
    private String street;
    private String number;
    private String postalCode;
    private String city;
    private String country;

    protected AddressEmbeddable() {} // JPA

    public AddressEmbeddable(Address address) {
        this.street = address.street();
        this.number = address.number();
        this.postalCode = address.postalCode();
        this.city = address.city();
        this.country = address.country();
    }

    public Address toDomain() {
        return new Address(street, number, postalCode, city, country);
    }
}
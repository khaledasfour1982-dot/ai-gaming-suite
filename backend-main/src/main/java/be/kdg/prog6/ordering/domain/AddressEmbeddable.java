package be.kdg.prog6.ordering.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {
    private String street;
    private String number;
    private String postalCode;
    private String city;
    private String country;

    public AddressEmbeddable() {} // JPA

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

    public static CustomerEmbeddable fromDomain(Customer customer) {
        return new CustomerEmbeddable(
                customer.getName(),
                new AddressEmbeddable(customer.getDeliveryAddress()),  // use constructor instead of fromDomain()
                customer.getContactEmail()
        );
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
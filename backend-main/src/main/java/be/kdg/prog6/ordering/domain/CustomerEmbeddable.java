package be.kdg.prog6.ordering.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class CustomerEmbeddable {
    private String name;

    @Embedded
    private AddressEmbeddable deliveryAddress;

    private String contactEmail;

    public CustomerEmbeddable(String name, AddressEmbeddable deliveryAddress, String contactEmail) {
        this.name = name;
        this.deliveryAddress = deliveryAddress;
        this.contactEmail = contactEmail;
    }
    public CustomerEmbeddable() {
        // JPA uses this constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressEmbeddable getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddressEmbeddable deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Customer toDomain() {
        return new Customer(name, deliveryAddress.toDomain(), contactEmail);
    }

    public static CustomerEmbeddable fromDomain(Customer customer) {
        return new CustomerEmbeddable(
                customer.getName(),
                new AddressEmbeddable(customer.getDeliveryAddress()),
                customer.getContactEmail()
        );
    }


}

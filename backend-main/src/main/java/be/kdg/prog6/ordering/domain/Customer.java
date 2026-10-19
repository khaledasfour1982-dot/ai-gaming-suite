package be.kdg.prog6.ordering.domain;

import be.kdg.prog6.ordering.domain.Address;

// value object
public class Customer {
    String name;
    Address deliveryAddress;
    String contactEmail;

    public Customer(String name, Address deliveryAddress, String contactEmail) {
        this.name = name;
        this.deliveryAddress = deliveryAddress;
        this.contactEmail = contactEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}

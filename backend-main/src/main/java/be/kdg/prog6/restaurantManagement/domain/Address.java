package be.kdg.prog6.restaurantManagement.domain;

// value object
public record Address(
        String street,
        String number,
        String postalCode,
        String city,
        String country
) {
    public Address {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("Street cannot be empty");
        if (number == null || number.isBlank()) throw new IllegalArgumentException("Number cannot be empty");
        if (postalCode == null || postalCode.isBlank()) throw new IllegalArgumentException("Postal code cannot be empty");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("City cannot be empty");
        if (country == null || country.isBlank()) throw new IllegalArgumentException("Country cannot be empty");
    }

    @Override
    public String toString() {
        return street + " " + number + ", " + postalCode + " " + city + ", " + country;
    }
}

package be.kdg.prog6.restaurantManagement.domain;

import java.util.UUID;

public record OwnerId (UUID uuid){
    public static OwnerId create(){
        return new OwnerId(UUID.randomUUID());
    }

}

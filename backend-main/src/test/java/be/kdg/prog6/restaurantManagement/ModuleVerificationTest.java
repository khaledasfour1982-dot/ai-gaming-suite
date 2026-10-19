package be.kdg.prog6.restaurantManagement;

import be.kdg.prog6.RestaurantManagementApplication;
import org.springframework.modulith.core.ApplicationModules;
import org.junit.jupiter.api.Test;

public class ModuleVerificationTest {

    @Test
    void verifyModuleBoundaries() {
        ApplicationModules modules = ApplicationModules.of(RestaurantManagementApplication.class);
        modules.verify();
    }
}

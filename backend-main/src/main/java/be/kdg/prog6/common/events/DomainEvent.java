package be.kdg.prog6.common.events;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime eventTime();

}

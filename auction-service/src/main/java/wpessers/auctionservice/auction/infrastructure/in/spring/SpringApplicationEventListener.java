package wpessers.auctionservice.auction.infrastructure.in.spring;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import wpessers.auctionservice.auction.application.RegistryInitializationService;

@Component
public class SpringApplicationEventListener {

    private final RegistryInitializationService service;

    public SpringApplicationEventListener(RegistryInitializationService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeRegistry() {
        service.initializeRegistry();
    }
}

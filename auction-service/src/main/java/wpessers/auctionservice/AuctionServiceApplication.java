package wpessers.auctionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import wpessers.auctionservice.pricing.infrastructure.out.grpc.PricingServiceConfig;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(PricingServiceConfig.class)
public class AuctionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServiceApplication.class, args);
    }

}

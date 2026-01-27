package wpessers.auctionservice.pricing.infrastructure.out.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Pricing Service gRPC connection.
 */
@ConfigurationProperties(prefix = "pricing-service")
public record PricingServiceConfig(
        String host,
        int port
) {
    public String address() {
        return host + ":" + port;
    }
}

package wpessers.auctionservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
class AuctionServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}

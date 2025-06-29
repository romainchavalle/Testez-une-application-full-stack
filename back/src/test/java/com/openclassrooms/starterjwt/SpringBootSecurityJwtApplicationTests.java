package com.openclassrooms.starterjwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class SpringBootSecurityJwtApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	void mainMethodRuns() {
		assertDoesNotThrow(() ->
				SpringBootSecurityJwtApplication.main(new String[]{})
		);
	}

}

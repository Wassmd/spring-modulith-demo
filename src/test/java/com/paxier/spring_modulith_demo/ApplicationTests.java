package com.paxier.spring_modulith_demo;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = EMBEDDED)
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}

package com.example.MySpringBootApp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MySpringBootAppApplicationTests {

	@Autowired
	private NewsController newsController;

	@Test
	void contextLoads() {
		// Test that the context loads successfully
	}

	@Test
	void newsControllerLoads() {
		// Test that NewsController is properly injected
		assertThat(newsController).isNotNull();
	}

	@Test
	void headlineEndpointReturnsContent() {
		// Test that the headline endpoint returns content
		String headline = newsController.getLatestBusinessNewsHeadline();
		assertThat(headline).isNotNull();
		assertThat(headline).contains("Success Deploying");
	}
}

package com.campusfind.campusfind;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CampusfindApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void additionTest() {
		int a = 10;
		int b = 20;

		int result = a + b;

		assertEquals(30, result);
	}
}
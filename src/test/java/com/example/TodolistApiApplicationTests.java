package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TodolistApiApplicationTests {

	@Test
	void contextLoads() {
		String text = "";
		assertThat(StringUtils.hasText(null)).isFalse();
		assertThat(StringUtils.hasText("")).isFalse();
		assertThat(StringUtils.hasText(" ")).isFalse();
		assertThat(StringUtils.hasText("12345")).isTrue();
		assertThat(StringUtils.hasText(" 12345 ")).isTrue();

		long now1 = (new Date()).getTime();
		long now2 = System.currentTimeMillis();
		if (now1 == now2) {
			System.out.println("euql!");
		}
	}

}

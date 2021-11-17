package com.example;

import com.example.domain.Address;
import com.example.domain.Member;
import com.example.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TodolistApiApplication {

	@Autowired
	MemberRepository memberRepository;

	public static void main(String[] args) {
		SpringApplication.run(TodolistApiApplication.class, args);
	}

	@PostConstruct
	public void init() {
		memberRepository.save(Member.builder()
						.username("alice")
						.userId("aliceid")
						.password("alicepw")
						.address(Address.builder()
								.street("str")
								.city("cit")
								.zipcode("zipc")
								.build())
				.build());
	}
}

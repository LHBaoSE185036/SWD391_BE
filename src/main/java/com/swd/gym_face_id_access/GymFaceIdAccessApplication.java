package com.swd.gym_face_id_access;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
@EnableScheduling
public class GymFaceIdAccessApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymFaceIdAccessApplication.class, args);
	}

}

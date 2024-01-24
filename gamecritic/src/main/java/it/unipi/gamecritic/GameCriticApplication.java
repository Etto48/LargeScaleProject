package it.unipi.gamecritic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GameCriticApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameCriticApplication.class, args);
	}

}

package spring.extreme;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExtremeApplication {

	public static void main(String[] args) {
		Random random = new Random();
		int min = 10000;
		int max = 50000;
		int randomNum = random.nextInt((max - min) + 1) + min;
		System.setProperty("server.port", Integer.toString(randomNum));
		SpringApplication.run(ExtremeApplication.class, args);
	}

}

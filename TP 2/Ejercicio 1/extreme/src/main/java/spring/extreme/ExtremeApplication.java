package spring.extreme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExtremeApplication {

	public static void main(String[] args) {
		System.setProperty("server.port","8080");
		SpringApplication.run(ExtremeApplication.class, args);
	}

}

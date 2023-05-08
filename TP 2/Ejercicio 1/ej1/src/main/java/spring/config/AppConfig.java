package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Bean
    public String nombreMaestro() {
        return "localhost";
    }

    @Bean
    public int puerto() {
        return 8081;
    }

    
}

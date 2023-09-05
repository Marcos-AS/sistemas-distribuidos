package spring;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication

@EnableJpaRepositories(basePackages = "spring.repositories")

public class Application {
    
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

    @Bean
    @FlywayDataSource
    public DataSource flywayDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/bd_task");
        dataSource.setUsername("root");
        dataSource.setPassword("example");
        return dataSource;
    }
}

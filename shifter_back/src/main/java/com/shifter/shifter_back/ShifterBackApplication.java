package com.shifter.shifter_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.Console;

@EnableJpaAuditing
@SpringBootApplication
public class ShifterBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShifterBackApplication.class, args);
        System.out.println("Server running...");


        // Retrieve the environment variable
        String dbUrl = System.getProperty("DB_URL");
        // Print the value of the environment variable
        System.out.println("DB URL: " + (dbUrl != null ? dbUrl : "data not found"));
    }

}

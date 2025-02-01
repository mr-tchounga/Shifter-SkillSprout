package com.shifter.shifter_back;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Base64;
import java.util.logging.Logger;

@EnableJpaAuditing
@SpringBootApplication
public class App {

//    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(App.class, args);
//        loggerr("Server is running on " + System.getProperty("server.port"));
        System.out.println("Server is running...");



        // Retrieve the environment variable
//        String dbUrl = System.getProperty("DB_URL");
//        // Print the value of the environment variable
//        System.out.println("DB URL: " + (dbUrl != null ? dbUrl : "data not found"));
    }

}


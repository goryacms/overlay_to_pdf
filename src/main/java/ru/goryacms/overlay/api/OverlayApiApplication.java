package ru.goryacms.overlay.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Configuration
public class OverlayApiApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(OverlayApiApplication.class, args);
    }

}

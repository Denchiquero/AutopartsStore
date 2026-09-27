package ru.mirea.autopartsstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = UserDetailsServiceAutoConfiguration.class
)
public class AutoPartsStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                AutoPartsStoreApplication.class,
                args
        );
    }
}
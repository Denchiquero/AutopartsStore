package ru.mirea.autopartsstore.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestClient nhtsaRestClient() {
        return RestClient.builder()
                .baseUrl("https://vpic.nhtsa.dot.gov")
                .build();
    }
}
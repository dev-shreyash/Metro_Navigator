package com.example.metro_navigator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Metro Navigator API")
                        .version("1.0.0")
                        .description("REST API for transit routing using Dijkstra's Algorithm. Evaluates weighted edges for distance and travel time.")
                        .contact(new Contact()
                                .name("Shreyash Bhosale")
                                .email("bhosaleshreyash2@gmail.com")));
    }
}
package com.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
        Info info = new Info()
                .title("Todolist API")
                .version(appVersion)
                .description("Todolist Service API")
                .termsOfService("http://swagger.io/terms/")
                .contact(new Contact()
                        .name("jeonyongho")
                        .url("https://jeonyoungho.github.io")
                        .email("yhjun1000@gmail.com"))
                .license(new License()
                        .name("Apache License Version 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0")
                );

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

}

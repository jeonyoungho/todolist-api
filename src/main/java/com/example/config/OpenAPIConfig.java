package com.example.config;

import com.example.jwt.TokenProvider;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;

@Profile("local")
@Configuration
public class OpenAPIConfig {

    private final String BEARER_FORMAT = "JWT";
    private final String SECURITY_SCHEME_NAME = "Bearer Auth";
    private final String SCHEME = TokenProvider.BEARER_TYPE;

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
        Info info = new Info()
                .title("Todolist API")
                .version(appVersion)
                .description("Todolist Service API")
                .termsOfService("https://swagger.io/terms/")
                .contact(new Contact()
                        .name("jeonyongho")
                        .url("https://jeonyoungho.github.io")
                        .email("yhjun1000@gmail.com"))
                .license(new License()
                        .name("Apache License Version 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")
                );

        return new OpenAPI()
                .schemaRequirement(SECURITY_SCHEME_NAME, getSecurityScheme())
                .security(getSecurityRequirement())
                .info(info);
    }

    private List<SecurityRequirement> getSecurityRequirement() {
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList(SECURITY_SCHEME_NAME);
        return Collections.singletonList(securityRequirement);
    }

    private SecurityScheme getSecurityScheme() {
        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.bearerFormat(BEARER_FORMAT);
        securityScheme.type(SecurityScheme.Type.HTTP);
        securityScheme.in(SecurityScheme.In.HEADER);
        securityScheme.scheme(SCHEME);
        return securityScheme;
    }
}

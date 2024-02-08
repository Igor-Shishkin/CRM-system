package com.crm.system.config;

import java.util.List;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    @Value("${app.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("igor.shishkin.work@gmail.com");
        contact.setName("Igor Shishkin");
        contact.setUrl("https://www.linkedin.com/in/igor-shishkin/");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("CRM_SYSTEM")
                .version("0.0.1")
                .contact(contact)
                .description("This API exposes endpoints to manage CRM-System application.")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
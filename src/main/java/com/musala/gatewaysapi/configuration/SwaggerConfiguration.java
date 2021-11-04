package com.musala.gatewaysapi.configuration;

//import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI gatewaysApiOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("gateways-api")
                        .description("a private microservice that serves as gateways and devices")
                        .version("v2.3.0")
                        .license(new License()))
                /*.externalDocs(new ExternalDocumentation()
                        .description("see more details here {gateway-api Confluence Documentation}")
                        .url("readme link"))*/;
    }
}


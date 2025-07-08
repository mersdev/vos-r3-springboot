package com.vehicleoem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vehicleOemServerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vehicle OEM Server API")
                        .description("""
                                # Vehicle OEM Digital Key Management System
                                
                                This API provides comprehensive digital key management services for vehicle manufacturers,
                                enabling secure creation, distribution, and management of digital vehicle keys.
                                
                                ## Key Features
                                - **Digital Key Lifecycle Management**: Create, track, suspend, resume, and terminate digital keys
                                - **Friend Key Sharing**: Share vehicle access with friends and family members
                                - **Vehicle Pairing**: Secure pairing process between devices and vehicles
                                - **Subscription Management**: Handle different subscription tiers and billing
                                - **Security & Cryptography**: ECDSA signatures, ECIES encryption, certificate management
                                - **Real-time Tracking**: Monitor key usage and vehicle interactions
                                
                                ## Security
                                All endpoints require HTTP Basic Authentication. The API uses industry-standard
                                cryptographic protocols including ECDSA digital signatures and ECIES encryption.
                                
                                ## Rate Limiting
                                API calls are rate-limited to ensure system stability and fair usage.
                                
                                ## Error Handling
                                The API returns standardized error responses with appropriate HTTP status codes
                                and detailed error messages to help with debugging and integration.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vehicle OEM Development Team")
                                .email("api-support@vehicleoem.com")
                                .url("https://developer.vehicleoem.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Development Server"),
                        new Server()
                                .url("https://api-dev.vehicleoem.com")
                                .description("Development Environment"),
                        new Server()
                                .url("https://api.vehicleoem.com")
                                .description("Production Environment")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("HTTP Basic Authentication using username and password")));
    }
}

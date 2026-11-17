package com.assignment.dataset_operator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "JSON Dataset Operator API",
                version = "1.0.0",
                description = """
                        REST API for storing arbitrary JSON records
                        and dynamically grouping and sorting datasets.
                        """,
                contact = @Contact(
                        name = "Backend Assignment"
                )
        )
)
public class OpenApiConfig {
}
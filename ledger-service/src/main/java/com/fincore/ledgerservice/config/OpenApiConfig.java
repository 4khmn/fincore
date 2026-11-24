package com.fincore.ledgerservice.config;

import com.fincore.ledgerservice.exception.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "JWT";
        return new OpenAPI()
                .info(new Info()
                        .title("Ledger service (fincore) API")
                        .version("1.0")
                        .description("Ledger Service отвечает за финансовые счета, балансы и движение денежных средств.\n" +
                                "\n" +
                                "Это единственный сервис, который изменяет финансовое состояние счетов и создаёт финансовые записи.")
                        .contact(new Contact().name("Denis Akhmin").email("denis-akhmin@yandex.ru")))

                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer globalErrorResponseCustomizer() {
        return openApi -> {
            ResolvedSchema errorResponseSchema = ModelConverters.getInstance()
                    .readAllAsResolvedSchema(ErrorResponse.class);

            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();

                responses.forEach((statusCode, response) -> {
                    if (statusCode.startsWith("4") || statusCode.startsWith("5")) {
                        response.setContent(new Content().addMediaType("application/json",
                                new MediaType().schema(errorResponseSchema.schema)));
                    }
                });

                if (operation.getSecurity() != null && !operation.getSecurity().isEmpty()) {
                    if (!responses.containsKey("401")) {
                        responses.addApiResponse("401", createErrorApiResponse("Пользователь не аутентифицирован", errorResponseSchema));
                    }
                    if (!responses.containsKey("403")) {
                        responses.addApiResponse("403", createErrorApiResponse("Доступ запрещен", errorResponseSchema));
                    }
                }

            }));
        };
    }

    private io.swagger.v3.oas.models.responses.ApiResponse createErrorApiResponse(String description, ResolvedSchema schema) {
        return new io.swagger.v3.oas.models.responses.ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(schema.schema)));
    }
}

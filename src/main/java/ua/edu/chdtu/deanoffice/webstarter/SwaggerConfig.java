package ua.edu.chdtu.deanoffice.webstarter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    private static final String AUTHENTICATION_HEADER = "Authorization";
    @Value("${swagger.host}")
    private String host;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(Collections.singletonList(authToken()))
                .securityContexts(Collections.singletonList(securityContext()))
                .host(host)
                .select()
                .build();
    }

    private ApiKey authToken() {
        return new ApiKey("Bearer {accessToken}", AUTHENTICATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(path -> PathSelectors.ant("/**").apply(path))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = {authorizationScope};
        return Collections.singletonList(new SecurityReference("Bearer {accessToken}", authorizationScopes));
    }


}

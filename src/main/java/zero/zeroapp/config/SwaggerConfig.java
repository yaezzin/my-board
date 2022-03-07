package zero.zeroapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.*;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Import(BeanValidatorPluginsConfiguration.class) // 1
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo()) // 2
                .select() // 3
                .apis(RequestHandlerSelectors.basePackage("zero.zeroapp.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(apiKey())) // 4
                .securityContexts(List.of(securityContext())); // 5
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Zero : 필요한 용기")
                .description("Zero App REST API Documentation")
                .version("1.0")
                .build();
    }

    private static ApiKey apiKey() {
        return new ApiKey("Authorization", "Bearer Token", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .operationSelector(oc -> oc.requestMappingPattern().startsWith("/api/")).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "global access");
        return List.of(new SecurityReference("Authorization", new AuthorizationScope[] {authorizationScope}));
    }
}

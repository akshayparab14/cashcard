package com.bank.cashcard.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author Akshay Parab
 * 
 * Swagger Configuration class for Rest API documentation 
 *
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	   public static final String AUTHORIZATION_HEADER = "Authorization";
	    public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";
	    

	    @Bean
	    public Docket newsApi() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .select()
	                .apis(RequestHandlerSelectors.any())
	                .paths(PathSelectors.any())
	                .build()
				  .securitySchemes(Lists.newArrayList(apiKey()))
				  .securityContexts(Lists.newArrayList(securityContext()))
				 
	                .apiInfo(apiInfo());
	    }

	    @Bean
	    SecurityContext securityContext() {
	        return SecurityContext.builder()
	                .securityReferences(defaultAuth())
	                .forPaths(PathSelectors.any())
	                .build();
	    }

	    List<SecurityReference> defaultAuth() {
	        AuthorizationScope authorizationScope
	                = new AuthorizationScope("global", "accessEverything");
	        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
	        authorizationScopes[0] = authorizationScope;
	        return Lists.newArrayList(
	                new SecurityReference("JWT", authorizationScopes));
	    }

	    private ApiKey apiKey() {
	        return new ApiKey("JWT", "Authorization", "header");
	    }
	    
	    


	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Cash Card Rest API V1.0")
				.description("Cash Card")
				.contact(new Contact("TCS", "parab.akshay348@gmail.com", "parab.akshay348@gmail.com"))
				.license("TCS").licenseUrl("https://www.tcs.com/").version("swagger 2.0").build();
	}
	   
}

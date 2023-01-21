package app.tasks.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(title = "My API", version = "v1"))
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.APIKEY,
        scheme = "basic",
        in = SecuritySchemeIn.HEADER
) public class SwaggerConfig {

}

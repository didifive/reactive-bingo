package com.reactivebingo.api.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Reactive Bingo"
                , description = "API reativa de bingo"
                , version = "r0.4.0-2"
                , contact = @Contact(
                name = "Luis Zancanela",
                email = "luisczdidi@gmail.com"
        ),
                license = @License(
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html",
                        name = "Apache 2.0"
                )),
        servers = {
                @Server(url = "http://localhost:8080/reactive-bingo", description = "local")
        }
)
public class OpenApiConfig {
}

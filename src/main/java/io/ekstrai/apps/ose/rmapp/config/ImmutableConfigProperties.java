package io.ekstrai.apps.ose.rmapp.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;


//TODO initial config wiring and purposes only.
@Getter
@ConfigurationProperties(prefix = "mail.credentials")
@ConstructorBinding
public class ImmutableConfigProperties {
    private final String authMethod;
    private final String username;
    private final String password;

    public ImmutableConfigProperties(String authMethod, String username, String password) {
        this.authMethod = authMethod;
        this.username = username;
        this.password = password;
    }
}

package org.example.sideservicefbeerproj.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "beer-application.kafka")
public class KafkaProperties {

    @NotBlank
    private String groupId;

    @Valid
    @NotNull
    @NestedConfigurationProperty
    private Flows flows;

    @Data
    public static class Flows {
        @Valid
        @NotNull
        @NestedConfigurationProperty
        private FlowProperties employeeCreation;
    }

    @Data
    public static class FlowProperties {
        @NotBlank
        private String topic;

        @NotBlank
        private String source;
    }
}

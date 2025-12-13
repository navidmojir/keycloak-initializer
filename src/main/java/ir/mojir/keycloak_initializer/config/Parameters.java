package ir.mojir.keycloak_initializer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Parameters {
    @Value("${configurationFilePath:./config.json}")
    private String configurationFilePath;

    @Value("${resultFilePath:./result.json}")
    private String resultFilePath;

    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public String getResultFilePath() {
        return resultFilePath;
    }
}

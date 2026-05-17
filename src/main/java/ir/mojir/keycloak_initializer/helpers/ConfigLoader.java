package ir.mojir.keycloak_initializer.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.mojir.keycloak_initializer.config.Configuration;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigLoader {

    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{([A-Z0-9_]+)}");

    public static Configuration load(String path) throws Exception {
        // 1. Read file as String
        String content = Files.readString(new File(path).toPath(), StandardCharsets.UTF_8);

        // 2. Replace ${VAR} with env values
        String resolved = resolveEnvPlaceholders(content);

        // 3. Deserialize
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(resolved, Configuration.class);
    }

    private static String resolveEnvPlaceholders(String content) {
        Matcher matcher = ENV_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();

        Map<String, String> env = System.getenv();

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = env.get(key);

            if (value == null) {
                throw new IllegalStateException("Missing environment variable: " + key);
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}

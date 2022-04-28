package ru.javanatnat.sitesearchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javanatnat.sitesearchengine.parser.TextParser;

@Configuration
public class LuceneConfig {
    @Bean
    TextParser getTextParser() {
        return new TextParser();
    }
}

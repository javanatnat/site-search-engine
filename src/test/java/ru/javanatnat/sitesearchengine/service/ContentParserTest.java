package ru.javanatnat.sitesearchengine.service;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ContentParserTest {
    @Test
    void testByContent() {
        String CONTENT = """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="generator" content="Asciidoctor 2.0.17">
                    <title>Test title</title>
                </head>
                <body class="book t-left">
                   <div id="header">
                        <h1>Test h1</h1>
                        <a href="test.ru">test</a>
                   </div>
                </body>""";
        ContentParser parser = ContentParser.getInstanceByContent(CONTENT);
        assertThat(parser.getCodeResponse()).isEqualTo(200);
        assertThat(parser.codeResponseIsOk()).isTrue();
        assertThat(parser.getContent()).containsIgnoringWhitespaces(CONTENT);
        assertThat(parser.getAllContentRefs()).isEqualTo(Set.of("test.ru"));
        assertThat(parser.getErrorMessage()).isNull();
        assertThat(parser.getTitle()).isEqualTo("Test title");
        assertThat(parser.getBodyText()).isEqualTo("Test h1 test");
        assertThat(parser.getElementText("h1")).containsIgnoringWhitespaces("Test h1");
    }

    @Test
    void testEmptyParser() {
        ContentParser parser = new ContentEmptyParser(404, "error", "test.ru");
        assertThat(parser.getErrorMessage()).isEqualTo("error");
        assertThat(parser.getCodeResponse()).isEqualTo(404);
        assertThat(parser.codeResponseIsOk()).isFalse();
        assertThat(parser.getContent()).isNull();

        assertThatThrownBy(() -> parser.getSnippet(Collections.emptySet()))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> parser.getElementText(""))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(parser::getTitle).isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(parser::getBodyText).isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(parser::getAllContentRefs).isExactlyInstanceOf(UnsupportedOperationException.class);
    }
}

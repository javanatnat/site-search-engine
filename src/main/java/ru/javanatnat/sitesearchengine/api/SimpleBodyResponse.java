package ru.javanatnat.sitesearchengine.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SimpleBodyResponse {
    private final Boolean result;
    @JsonProperty("error")
    private final String errorMessage;

    private SimpleBodyResponse(Boolean result) {
        this.result = result;
        this.errorMessage = null;
    }

    private SimpleBodyResponse(Boolean result, String errorMessage) {
        Objects.requireNonNull(errorMessage);
        this.result = result;
        this.errorMessage = errorMessage;
    }

    @JsonIgnore
    public static SimpleBodyResponse getOk() {
        return new SimpleBodyResponse(true);
    }

    @JsonIgnore
    public static SimpleBodyResponse getError(String error) {
        return new SimpleBodyResponse(false, error);
    }

    public Boolean getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

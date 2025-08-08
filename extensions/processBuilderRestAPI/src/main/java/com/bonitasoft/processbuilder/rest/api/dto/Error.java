package com.bonitasoft.processbuilder.rest.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Value;

@Value // Generates getters, a constructor for all fields, equals, hashCode, and toString.
@Builder // Generates the full builder pattern (a builder class and a builder() factory method).
@JsonDeserialize(builder = Error.ErrorBuilder.class)
public class Error {
    @JsonIgnore
    private final String name = "error";
    private final String message;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ErrorBuilder {

        private String message;

        public ErrorBuilder message(String message) {
            this.message = message;
            return this;
        }

        public Error build() {
            return new Error(message);
        }

    }

    public static ErrorBuilder builder() {
        return new ErrorBuilder();
    }
}

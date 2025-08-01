package com.bonitasoft.processbuilder.rest.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

/**
 * DTO to encapsulate the HTML content for documentation.
 * Uses Lombok's @Value for immutability and @Builder for automatic builder pattern generation.
 */
@Value // Generates getters, a constructor for all fields, equals, hashCode, and toString.
@Builder // Generates the full builder pattern (a builder class and a builder() factory method).
@JsonDeserialize(builder = ResultDocumentation.ResultDocumentationBuilder.class)
public class ResultDocumentation {
    private final String htmlContent;
    private final String contentType;
}

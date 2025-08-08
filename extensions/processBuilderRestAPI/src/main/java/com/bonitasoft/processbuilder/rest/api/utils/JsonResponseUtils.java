package com.bonitasoft.processbuilder.rest.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.processbuilder.rest.api.constants.Messages;

public class JsonResponseUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonResponseUtils.class.getName());


    private final static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Build an HTTP response.
     *
     * @param responseBuilder the Rest API response builder
     * @param httpStatus      the status of the response
     * @param body            the response body
     * @return a RestAPIResponse
     */
    public static RestApiResponse jsonResponse(RestApiResponseBuilder responseBuilder, int httpStatus, Object body) {
        try {
            return responseBuilder
                    .withResponseStatus(httpStatus)
                    .withResponse(mapper.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
        	LOGGER.error(Messages.ERROR_FAILED_TO_WRITE_BODY_AS_JSON, e);
            throw new RuntimeException(Messages.ERROR_FAILED_TO_WRITE_BODY_AS_JSON + body, e);
        }
    }


    /**
     * Returns a paged result like Bonita BPM REST APIs.
     * Build a response with a content-range.
     *
     * @param responseBuilder the Rest API response builder
     * @param body            the response body
     * @param p               the page index
     * @param c               the number of result per page
     * @param total           the total number of results
     * @return a RestAPIResponse
     */
    public static RestApiResponse pagedJsonResponse(RestApiResponseBuilder responseBuilder, int httpStatus, Object body, int p, int c, long total) {
        try {
            return responseBuilder
                    .withContentRange(p, c, total)
                    .withResponseStatus(httpStatus)
                    .withResponse(mapper.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
        	LOGGER.error(Messages.ERROR_FAILED_TO_WRITE_BODY_AS_JSON, e);
            throw new RuntimeException(Messages.ERROR_FAILED_TO_WRITE_BODY_AS_JSON + body, e);
        }
    }

}

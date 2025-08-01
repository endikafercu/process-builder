package com.bonitasoft.processbuilder.rest.api.utils;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.bonitasoft.web.extension.ResourceProvider;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

	 /**
     * Build an HTTP response.
     *
     * @param responseBuilder the Rest API response builder
     * @param mapper
     * @param httpStatus      the status of the response
     * @param body            the response body
     * @return a RestAPIResponse
     */
	public static RestApiResponse jsonResponse(RestApiResponseBuilder responseBuilder, ObjectMapper mapper, int httpStatus, Object body) {
        try {
            return responseBuilder
                    .withResponseStatus(httpStatus)
                    .withResponse(mapper.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to write body response as JSON: " + body, e);
        }
    }


    /**
     * Returns a paged result like Bonita BPM REST APIs.
     * Build a response with a content-range.
     *
     * @param responseBuilder the Rest API response builder
     * @param mapper
     * @param body            the response body
     * @param p               the page index
     * @param c               the number of result per page
     * @param total           the total number of results
     * @return a RestAPIResponse
     */
    public static RestApiResponse pagedJsonResponse(RestApiResponseBuilder responseBuilder, ObjectMapper mapper, int httpStatus, Object body, int p, int c, long total) {
        try {
            return responseBuilder
                    .withContentRange(p, c, total)
                    .withResponseStatus(httpStatus)
                    .withResponse(mapper.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to write body response as JSON: " + body, e);
        }
    }

    /**
     * Builds an HTTP response with a text body.
     *
     * @param responseBuilder The Rest API response builder.
     * @param httpStatus The HTTP status of the response.
     * @param body The response body content as a String.
     * @param contentType The Content-Type of the response (e.g., "text/html", "text/yaml").
     * @return a RestAPIResponse object.
     */
    public static RestApiResponse textResponse(RestApiResponseBuilder responseBuilder, int httpStatus, String body, String contentType) {
    	return responseBuilder
    	        .withResponseStatus(SC_OK)
    	        .withResponse(body)
    	        .withMediaType(contentType) 
    	        .build();
    }
    
    /**
     * Load a property file into a java.util.Properties
     * 
     * @param fileName
     * @param resourceProvider
     * @return
     */
    public static Properties loadProperties(String fileName, ResourceProvider resourceProvider) {
        try (InputStream is = resourceProvider.getResourceAsStream(fileName)){
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties for REST API extension");
        }
    }
    
    
    /**
     * @param filePath
     * @param resourceProvider
     * @return
     * @throws IOException
     */
    public static  String readResourceFile(String filePath, ResourceProvider resourceProvider) throws IOException {
        try (InputStream inputStream = resourceProvider.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("File not found in classpath: " + filePath);
            }
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
	
}

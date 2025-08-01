package com.bonitasoft.processbuilder.rest.api.doc;

import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiController;

import com.bonitasoft.processbuilder.rest.api.constants.Constants;
import com.bonitasoft.processbuilder.rest.api.dto.Error;
import com.bonitasoft.processbuilder.rest.api.dto.ResultDocumentation;
import com.bonitasoft.processbuilder.rest.api.exception.RestApiResourceException;
import com.bonitasoft.processbuilder.rest.api.exception.ValidationException;
import com.bonitasoft.processbuilder.rest.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;


/**
 * Parent Controller class to hide technical parts
 */
public abstract class AbstractDocumentation implements RestApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDocumentation.class.getName());



    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {

        // Validate request
        try {
            validateInputParameters(request);
        } catch (ValidationException e) {
            LOGGER.error("Request for this REST API extension is not valid", e);
            return Utils.jsonResponse(responseBuilder, mapper, SC_BAD_REQUEST, Error.builder().message(e.getMessage()).build());
        }
        try {
	        // Execute business logic
	        ResultDocumentation result = execute(context, mapper);
	        return Utils.textResponse(responseBuilder, SC_OK, result.getHtmlContent(), Constants.TEXT_HTML);
        } catch (RestApiResourceException e) {
            return Utils.jsonResponse(responseBuilder, mapper, SC_INTERNAL_SERVER_ERROR, Error.builder().message(e.getMessage()).build());
        }
    }

    protected abstract ResultDocumentation execute(RestAPIContext context, ObjectMapper mapper);

    protected abstract void validateInputParameters(HttpServletRequest request);

}
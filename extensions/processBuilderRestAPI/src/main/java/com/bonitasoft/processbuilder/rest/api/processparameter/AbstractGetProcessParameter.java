package com.bonitasoft.processbuilder.rest.api.processparameter;

import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiController;
import com.bonitasoft.processbuilder.rest.api.dto.Error;
import com.bonitasoft.processbuilder.rest.api.dto.Result;
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

/**
 * Parent Controller class to hide technical parts
 */
public abstract class AbstractGetProcessParameter implements RestApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGetProcessParameter.class.getName());

    public static final String MY_PARAMETER_KEY = "myParameterKey";
    public static final String PARAM_P = "p";
    public static final String PARAM_C = "c";

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
        String p = request.getParameter(PARAM_P);
        String c = request.getParameter(PARAM_C);

        // Execute business logic
        Result result = execute(context, p, c);

        // Send the result as a JSON representation
        // You may use pagedJsonResponse if your result is multiple
        return Utils.jsonResponse(responseBuilder,mapper, SC_OK, result);
    }

    protected abstract Result execute(RestAPIContext context, String p, String c);

    protected abstract void validateInputParameters(HttpServletRequest request);

   
}

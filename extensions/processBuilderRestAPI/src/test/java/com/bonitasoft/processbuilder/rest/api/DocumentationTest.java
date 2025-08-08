package com.bonitasoft.processbuilder.rest.api;


import com.bonitasoft.processbuilder.rest.api.constants.Constants;
import com.bonitasoft.processbuilder.rest.api.controller.doc.*;
import com.bonitasoft.processbuilder.rest.api.dto.Error;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bonitasoft.web.extension.ResourceProvider;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentationTest {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationTest.class.getName());
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private RestAPIContext context;
    @Mock
    private ResourceProvider resourceProvider;
    
    // Inyectamos los mocks en la clase a testear
    @InjectMocks
    private Documentation documentationController;

    private RestApiResponseBuilder responseBuilder;
    private ObjectMapper objectMapper;

    private static final String FAKE_HTML_CONTENT = "<html><body><h1>Swagger UI</h1></body></html>";
    private static final String FAKE_YAML_CONTENT = "openapi: 3.0.4\ninfo:\n  title: Test API";

    @BeforeEach
    void setUp() throws IOException {
    	LOGGER.info("--------------- setUp");
        responseBuilder = new RestApiResponseBuilder();
        objectMapper = documentationController.getMapper();

        when(context.getResourceProvider()).thenReturn(resourceProvider);
        
        when(resourceProvider.getResourceAsStream(Constants.PATH_INDEX_SWAGGER))
        .thenReturn(new ByteArrayInputStream(FAKE_HTML_CONTENT.getBytes(StandardCharsets.UTF_8)));
        when(resourceProvider.getResourceAsStream(Constants.PATH_OPENAPI_SWAGGER))
        .thenReturn(new ByteArrayInputStream(FAKE_YAML_CONTENT.getBytes(StandardCharsets.UTF_8)));
    }
    
    @Test
    void should_return_html_by_default_when_no_response_type_is_set() throws IOException {
    	
        when(httpRequest.getParameter(Constants.PARAM_RESPONSE_TYPE)).thenReturn(null);
        RestApiResponse response = documentationController.doHandle(httpRequest, responseBuilder, context);
        assertThat(response.getHttpStatus()).isEqualTo(SC_OK);
        assertThat(response.getResponse()).isEqualTo(FAKE_HTML_CONTENT);
        assertThat(response.getMediaType()).isEqualTo(Constants.TEXT_HTML);
    }
    
    @Test
    void should_return_internal_server_error_if_file_not_found() throws IOException {
    	
        when(httpRequest.getParameter(Constants.PARAM_RESPONSE_TYPE)).thenReturn(Constants.HTML);
        when(resourceProvider.getResourceAsStream(Constants.PATH_INDEX_SWAGGER)).thenReturn(null);
        
        RestApiResponse response = documentationController.doHandle(httpRequest, responseBuilder, context);
        
        Error error = objectMapper.readValue((String) response.getResponse(), Error.class);
        assertThat(response.getHttpStatus()).isEqualTo(SC_INTERNAL_SERVER_ERROR);
        assertThat(error.getMessage()).isEqualTo("Error reading documentation page");
    }
    
   
}
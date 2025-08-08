package com.bonitasoft.processbuilder.rest.api;

import org.bonitasoft.web.extension.rest.RestAPIContext;
import com.bonitasoft.processbuilder.rest.api.dto.ResultGetProcessParameter;
import com.bonitasoft.processbuilder.rest.api.exception.ValidationException;
import com.bonitasoft.processbuilder.rest.api.controller.processparameter.GetProcessParameter;

import org.bonitasoft.web.extension.ResourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetProcessParameterTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessParameterTest.class.getName());
	
    // Declare mocks here
    // Mocks are used to simulate external dependencies behavior
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private ResourceProvider resourceProvider;
    @Mock
    private RestAPIContext context;

    // The controller to test
    private GetProcessParameter getProcessParameter;

    /**
     * You can configure mocks before each tests in the setup method
     */
    @BeforeEach
    void setUp() throws FileNotFoundException {
        // Create a new instance under test
        getProcessParameter = new GetProcessParameter();

        // Simulate access to configuration.properties resource
        when(context.getResourceProvider()).thenReturn(resourceProvider);
        when(resourceProvider.getResourceAsStream("configuration.properties"))
                .thenReturn(GetProcessParameterTest.class.getResourceAsStream("/testConfiguration.properties"));
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_missing() {
        assertThrows(ValidationException.class, () ->
                getProcessParameter.validateInputParameters(httpRequest)
        );
    }

    @Test
    void should_get_result_when_params_ok() {

        // Given
        String persistenceId = "1";

        // When
        ResultGetProcessParameter resultGetProcessParameter  = getProcessParameter.execute(context, persistenceId);

        // Then
        assertThat(resultGetProcessParameter.getProcessParameter().getPersistenceId().toString()).isEqualTo(persistenceId);
    }
/*
    @Test
    void should_return_a_json_representation_as_result() throws IOException {
        // Given "a RestAPIController"

        // Simulate a request with a value for each parameter
        when(httpRequest.getParameter("id")).thenReturn("1");

        // When "Invoking the REST API"
        RestApiResponse apiResponse = getProcessParameter.doHandle(httpRequest, new RestApiResponseBuilder(), context);

        // Then "A JSON representation is returned in response body"
        Result jsonResponse = getProcessParameter.getMapper().readValue((String) apiResponse.getResponse(), Result.class);

        // Validate returned response
        assertThat(apiResponse.getHttpStatus()).isEqualTo(200);
        assertThat(jsonResponse).isNotNull();
    }

    @Test
    void should_return_an_error_response_if_p_is_not_set() throws IOException {
        // Given "a request without p"
        when(httpRequest.getParameter("p")).thenReturn(null);
        when(httpRequest.getParameter("c")).thenReturn("aValue2");

        // When "Invoking the REST API"
        RestApiResponse apiResponse = getProcessParameter.doHandle(httpRequest, new RestApiResponseBuilder(), context);

        // Then "A JSON response is returned with a HTTP Bad Request Status (400) and an error message in body"
        Error jsonResponse = getProcessParameter.getMapper().readValue((String) apiResponse.getResponse(), Error.class);
        // Validate returned response
        assertThat(apiResponse.getHttpStatus()).isEqualTo(400);
        assertThat(jsonResponse.getMessage()).isEqualTo("the parameter p is missing");
    }

    @Test
    void should_return_an_error_response_if_c_is_not_set() throws IOException {
    	
        // Given "a request without c"
        when(httpRequest.getParameter("c")).thenReturn(null);
        when(httpRequest.getParameter("p")).thenReturn("aValue1");
        // When "Invoking the REST API"
        RestApiResponse apiResponse = getProcessParameter.doHandle(httpRequest, new RestApiResponseBuilder(), context);

       
        // Then "A JSON response is returned with a HTTP Bad Request Status (400) and an error message in body"
        Error jsonResponse = getProcessParameter.getMapper().readValue((String) apiResponse.getResponse(), Error.class);
        // Validate returned response
        assertThat(apiResponse.getHttpStatus()).isEqualTo(400);
        assertThat(jsonResponse.getMessage()).isEqualTo("the parameter c is missing");
    }
*/
}

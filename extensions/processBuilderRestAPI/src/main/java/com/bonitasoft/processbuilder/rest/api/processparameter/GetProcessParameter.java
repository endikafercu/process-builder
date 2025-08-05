package com.bonitasoft.processbuilder.rest.api.processparameter;

import com.bonitasoft.processbuilder.rest.api.dto.ResultGetProcessParameter;
import com.bonitasoft.processbuilder.rest.api.exception.ValidationException;
import com.bonitasoftprocessbuilder.model.process.ProcessParameter;

import org.bonitasoft.web.extension.rest.RestAPIContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

import java.time.OffsetDateTime;

/**
 * Controller class
 */
public class GetProcessParameter extends AbstractGetProcessParameter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessParameter.class.getName());

    /**
     * Ensure request is valid
     *
     * @param request the HttpRequest
     */
    @Override
    public void validateInputParameters(HttpServletRequest request) {
        // To retrieve query parameters use the request.getParameter(..) method.
        // Be careful, parameter values are always returned as String values

        // Retrieve p parameter
        String persistenceId = request.getParameter(PARAM_PERSISTENCE_ID);
        if (persistenceId == null) {
            throw new ValidationException(format("the parameter %s is missing", PARAM_PERSISTENCE_ID));
        }
    }

    /**
     * Execute business logic
     *
     * @param context
     * @param persistenceId
     * @return Result
     */
    @Override
	public ResultGetProcessParameter execute(RestAPIContext context, String persistenceId) {

        LOGGER.info(format("Execute rest api call with params:  %s ",  persistenceId));

        ProcessParameter processParameter = new ProcessParameter();
        processParameter.setPersistenceId(1L);
        processParameter.setFullName("My Test Process");
        processParameter.setVersion("1.0.0");
        processParameter.setShortDescription("This is a test description.");
        processParameter.setToken("TEST_TOKEN");
        processParameter.setDisplayName("Test Display Name");
        processParameter.setAppName("Test App");
        processParameter.setAutoCancellationDays(30);
        processParameter.setDocumentsFolderPath("/docs/test/path");
        processParameter.setNumberOfSteps(5);
        processParameter.setBpmProcessDefinitionId(12345L);
        processParameter.setAuCreationDate(OffsetDateTime.now());
        processParameter.setAuCreationUser("system");
        processParameter.setAuModificationDate(OffsetDateTime.now());
        processParameter.setAuModificationUser("system");
        processParameter.setAuActive(true);
        
        return ResultGetProcessParameter.builder().processParameter(processParameter).build();
    }
}
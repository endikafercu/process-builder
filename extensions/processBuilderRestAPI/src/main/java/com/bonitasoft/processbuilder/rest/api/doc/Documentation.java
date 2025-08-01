package com.bonitasoft.processbuilder.rest.api.doc;

import com.bonitasoft.processbuilder.rest.api.constants.Constants;
import com.bonitasoft.processbuilder.rest.api.dto.ResultDocumentation;
import com.bonitasoft.processbuilder.rest.api.exception.RestApiResourceException;
import com.bonitasoft.processbuilder.rest.api.exception.ValidationException;
import com.bonitasoft.processbuilder.rest.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import org.bonitasoft.web.extension.rest.RestAPIContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Controller class
 */
public class Documentation extends AbstractDocumentation {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Documentation.class.getName());
    private final YAMLMapper yamlMapper = new YAMLMapper();
    /**
     * Ensure request is valid
     * 
     * Validates that the request contains no input parameters.
     *
     * @param request The {@link HttpServletRequest} to validate.
     * @throws ValidationException if any parameters are found in the request.
     */
    @Override
    public void validateInputParameters(HttpServletRequest request) {
    	// Check if the request's parameter map is empty.
        if (!request.getParameterMap().isEmpty()) {
            throw new ValidationException("No parameters should be provided in this request.");
        }
    }

    /**
     * Execute business logic
     *
     * @param context
     * @return Result
     */
    @Override
    protected ResultDocumentation execute(RestAPIContext context, ObjectMapper mapper) {       
        try {            
            String htmlTemplate = Utils.readResourceFile(Constants.PATH_INDEX_SWAGGER, context.getResourceProvider());
            String yamlContent = Utils.readResourceFile(Constants.PATH_OPENAPI_SWAGGER, context.getResourceProvider());

            Object yamlObject = yamlMapper.readValue(yamlContent, Object.class);
            String jsonSpec = mapper.writeValueAsString(yamlObject);         
            String htmlContent = htmlTemplate.replace("'{{OPENAPI_SPEC_PLACEHOLDER}}'", jsonSpec);
            
            return ResultDocumentation.builder()
                    .htmlContent(htmlContent)
                    .contentType(Constants.TEXT_HTML)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error reading the documentation file from the classpath.", e);
            throw new RestApiResourceException("Error reading documentation page", e);
        }
    }

    
   
}
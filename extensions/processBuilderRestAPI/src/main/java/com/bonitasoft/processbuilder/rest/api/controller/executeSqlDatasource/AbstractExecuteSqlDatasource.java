package com.bonitasoft.processbuilder.rest.api.controller.executeSqlDatasource;

import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.processbuilder.rest.api.exception.ValidationException;
import com.bonitasoft.processbuilder.rest.api.utils.JsonResponseUtils;
import com.bonitasoft.processbuilder.rest.api.utils.Utils;
import com.bonitasoft.processbuilder.rest.api.dto.ResultExecuteSqlDatasource;
import com.bonitasoft.processbuilder.rest.api.constants.Messages;
import com.bonitasoft.processbuilder.rest.api.constants.Parameters;
import com.bonitasoft.processbuilder.rest.api.dto.ExecuteSqlDatasourceParameters;

import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import com.bonitasoft.processbuilder.rest.api.dto.Error;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.util.HashMap;
import java.util.Map;

/**
 * Parent Controller class to hide technical parts
 */
public abstract class AbstractExecuteSqlDatasource implements RestApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExecuteSqlDatasource.class.getName());




    @Override
    public RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {

    	
        // Validate request
        try {
            validateInputParameters(request, context);
        } catch (ValidationException e) {
            LOGGER.error(Messages.ERROR_REQUEST_FOR_EXTENSION_NOT_VALID, e);
            return JsonResponseUtils.jsonResponse(responseBuilder, SC_BAD_REQUEST, Error.builder().message(Messages.ERROR_REQUEST_FOR_EXTENSION_NOT_VALID + e.getMessage()).build());
        }
        
        // Prepare information
        ExecuteSqlDatasourceParameters executeSqlDatasourceParameters = new ExecuteSqlDatasourceParameters();
        Map<String,Object> params = new HashMap<String,Object>();
        try {        	
        	executeSqlDatasourceParameters.setQueryId(request.getParameter(Parameters.PARAM_QUERY_ID));
        	String query = Utils.getQuery(request.getParameter(Parameters.PARAM_QUERY_ID), context);
        	executeSqlDatasourceParameters.setQuery(query);
        	
            String pStr = request.getParameter(Parameters.PARAM_INPUT_P);
            String cStr = request.getParameter(Parameters.PARAM_INPUT_C);
                        
            if (pStr != null && cStr != null) {
        		executeSqlDatasourceParameters.setIsPaginated(true);
        		executeSqlDatasourceParameters.setP(Integer.valueOf(pStr));
        		executeSqlDatasourceParameters.setC(Integer.valueOf(cStr));
	        	String queryCount = Utils.getQuery(request.getParameter(Parameters.PARAM_QUERY_ID).concat(Parameters.COUNT), context);
	        	LOGGER.info("Save queryCount:"+queryCount);
	        	executeSqlDatasourceParameters.setQueryCount(queryCount);
        	}
            
            String oStr = request.getParameter(Parameters.PARAM_ORDER_BY);
            if (oStr != null) {
            	executeSqlDatasourceParameters.setIsOrdered(true);
        		executeSqlDatasourceParameters.setO(oStr);
            }
            
        	params = getParameters(request);
        	executeSqlDatasourceParameters.setParams(params);
        	executeSqlDatasourceParameters.setDatasource(request.getParameter(Parameters.PARAM_DATASOURCE_NAME));
        } catch (Exception e) {
            LOGGER.error(Messages.ERROR_GENERATING_INFORMATION, e);
            return JsonResponseUtils.jsonResponse(responseBuilder, SC_BAD_REQUEST, Error.builder().message(Messages.ERROR_GENERATING_INFORMATION +e.getMessage()).build());
        }
        
        // Execute business logic
        ResultExecuteSqlDatasource result = null;
		try {
			result = execute(context, executeSqlDatasourceParameters);
        } catch (ExecutionException e) {
            LOGGER.error(Messages.ERROR_EXECUTING_REST_API_EXTENSION, e);
            return JsonResponseUtils.jsonResponse(responseBuilder, SC_BAD_REQUEST, Error.builder().message(Messages.ERROR_EXECUTING_REST_API_EXTENSION + e.getMessage()).build());
        }

		if (executeSqlDatasourceParameters.getIsPaginated()) {
			Integer p =  Integer.valueOf((String) request.getParameter(Parameters.PARAM_INPUT_P));
			Integer c =  Integer.valueOf((String) request.getParameter(Parameters.PARAM_INPUT_C));
			Long total = result.getCount();
			// You may use pagedJsonResponse if your result is multiple
			return JsonResponseUtils.pagedJsonResponse(responseBuilder, SC_OK, result.getResult(), p, c, total);
		} else {
			// Send the result as a JSON representation
	        return JsonResponseUtils.jsonResponse(responseBuilder, SC_OK, result.getResult());
		}
        
    }

    protected abstract ResultExecuteSqlDatasource execute(RestAPIContext context, ExecuteSqlDatasourceParameters executeSqlDatasourceParameters)  throws ExecutionException;

    protected abstract void validateInputParameters(HttpServletRequest request, RestAPIContext context);

    /**
     * @param request
     * @return
     */
    protected Map<String,Object> getParameters(HttpServletRequest request){
    	Map<String, Boolean> fieldsToRemove = new HashMap<>();
    	fieldsToRemove.put(Parameters.PARAM_QUERY_ID, true);
    	fieldsToRemove.put(Parameters.PARAM_DATASOURCE_NAME, true);
    	fieldsToRemove.put(Parameters.PARAM_INPUT_P, true);
    	fieldsToRemove.put(Parameters.PARAM_INPUT_C, true);
    	fieldsToRemove.put(Parameters.PARAM_RELOAD, true);
    	fieldsToRemove.put(Parameters.PARAM_ORDER_BY, true);

    	return Utils.getSqlParameters(request, fieldsToRemove);
    }
    
}

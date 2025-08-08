package com.bonitasoft.processbuilder.rest.api.controller.executeSqlDatasource;

import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.web.extension.rest.RestAPIContext;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.processbuilder.rest.api.constants.Messages;
import com.bonitasoft.processbuilder.rest.api.constants.Parameters;
import com.bonitasoft.processbuilder.rest.api.dto.ExecuteSqlDatasourceParameters;
import com.bonitasoft.processbuilder.rest.api.dto.ResultExecuteSqlDatasource;
import com.bonitasoft.processbuilder.rest.api.exception.ValidationException;
import com.bonitasoft.processbuilder.rest.api.utils.Utils;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;

/**
 * Controller class
 */
public class ExecuteSqlDatasource extends AbstractExecuteSqlDatasource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteSqlDatasource.class.getName());

    /**
     * Ensure request is valid
     *
     * @param request the HttpRequest
     */
    @Override
    public void validateInputParameters(HttpServletRequest request, RestAPIContext context) {
    	
        // Retrieve queryId parameter
        String queryId = request.getParameter(Parameters.PARAM_QUERY_ID);
        if (queryId == null) {
        	LOGGER.error(format(Messages.ERROR_PARAM_MISSING, Parameters.PARAM_QUERY_ID));
            throw new ValidationException(format(Messages.ERROR_PARAM_MISSING, Parameters.PARAM_QUERY_ID));
        } else {
            // Be careful, parameter values are always returned as String values
            String query = Utils.getQuery(queryId, context);
            if (query == null) {
                LOGGER.error(format(Messages.ERROR_VALUE_MISSING, Parameters.PARAM_QUERY_ID));
                throw new ValidationException(format(Messages.ERROR_VALUE_MISSING, Parameters.PARAM_QUERY_ID));
            }
        	
        	String oStr = request.getParameter(Parameters.PARAM_ORDER_BY);
        	if (oStr == null) {
        		if (query.contains(":".concat(Parameters.PARAM_ORDER_BY))) {
        			LOGGER.error(format(Messages.ERROR_PARAM_MISSING, Parameters.PARAM_ORDER_BY));
                    throw new ValidationException(format(Messages.ERROR_PARAM_MISSING, Parameters.PARAM_ORDER_BY));
        		}
        	}
        }
        
        String datasource = request.getParameter(Parameters.PARAM_DATASOURCE_NAME);
        if (datasource == null) {
        	LOGGER.error(format(Messages.ERROR_PARAM_MISSING, Parameters.PARAM_DATASOURCE_NAME));
            throw new ValidationException(format(Messages.ERROR_PARAM_MISSING, Parameters.PARAM_DATASOURCE_NAME));
        }
        
        LOGGER.info(String.format("Parameters: %s", request.getParameterMap().toString()));


        
        String pStr = request.getParameter(Parameters.PARAM_INPUT_P);
        String cStr = request.getParameter(Parameters.PARAM_INPUT_C);
        if (pStr != null && cStr != null) {
        	try {
        		Integer.valueOf(pStr);
        	} catch (Exception e) {
        		LOGGER.error(format(Messages.ERROR_IS_NOT_NUMERICAL, Parameters.PARAM_INPUT_P));
                throw new ValidationException(format(Messages.ERROR_IS_NOT_NUMERICAL, Parameters.PARAM_INPUT_P));
        	}
        	try {
        		Integer.valueOf(cStr);
        	} catch (Exception e) {
        		LOGGER.error(format(Messages.ERROR_IS_NOT_NUMERICAL, Parameters.PARAM_INPUT_C));
                throw new ValidationException(format(Messages.ERROR_IS_NOT_NUMERICAL, Parameters.PARAM_INPUT_C));
        	}
        } else {
        	LOGGER.warn(format(Messages.ERROR_P_OR_C_MISSING));
        }
    }

    /**
     * Execute business logic
     *
     * @param context
     * @param executeSqlDatasourceParameters
     * @return ResultExecuteSqlDatasource
     * @throws ExecutionException 
     */
    @Override
	public ResultExecuteSqlDatasource execute(RestAPIContext context, ExecuteSqlDatasourceParameters executeSqlDatasourceParameters) throws ExecutionException {    	
    	/*
    	DataSource datasource = null;
    	try {
    		datasource = Utils.getDatasource(context, executeSqlDatasourceParameters.getDatasource());
    	} catch (Exception e) {
			LOGGER.error(format(Messages.ERROR_GETTING_DATASOURCE, e.getMessage()));
			throw new ExecutionException(format(Messages.ERROR_GETTING_DATASOURCE, e.getMessage()));
		}
    	
    	*/
    	String query = executeSqlDatasourceParameters.getQuery();
    	if (executeSqlDatasourceParameters.getIsPaginated()) {
    		query = Utils.substituteVariables(query, Parameters.PARAM_INPUT_P, executeSqlDatasourceParameters.getP());
    		query = Utils.substituteVariables(query, Parameters.PARAM_INPUT_C, executeSqlDatasourceParameters.getC());
    	}
    	
    	if (executeSqlDatasourceParameters.getIsOrdered()) {
    		query = Utils.substituteVariables(query, Parameters.PARAM_ORDER_BY,Parameters.ORDER_BY.concat(executeSqlDatasourceParameters.getO()));
    	}
    	
		Map<String, Object> params = executeSqlDatasourceParameters.getParams();
		query = addOrderToQuery(query, params);

    	
		Sql sql = Utils.buildSql(context, executeSqlDatasourceParameters.getDatasource());
		List<GroovyRowResult> rows = null;
    	Long total  = 0L;
    	//JsonBuilder jsonBuilder = new JsonBuilder();
		try {
			
	        
			/**
			 * Ejecutar la consulta query, anyadiendo los parametros, si existen
			 */
			//LOGGER.info("query:"+query);
            rows = Utils.executeSQL(sql, query, params);
			/**
			 * Gestionar los resultados de la consulta si devuelve resultados
			 * Segun los parametros facilitados, devolver los datos obtenidos, paginados o no.
			 */
			if (rows != null) {
				//jsonBuilder = new JsonBuilder(rows);
				if (executeSqlDatasourceParameters.getIsPaginated()) {
					//LOGGER.info("executeSqlDatasourceParameters:"+executeSqlDatasourceParameters.toString());
					String queryCount = executeSqlDatasourceParameters.getQueryCount();		
					LOGGER.info("Is Paginated - queryCount:"+queryCount);
					//queryCount = addOrderToQuery(queryCount, params);
					
					//LOGGER.info("queryCount:"+queryCount);
					List<GroovyRowResult> listGroovyRowResult = Utils.executeSQL(sql, queryCount, params);
					if (listGroovyRowResult != null) {
						GroovyRowResult groovyRowResult = listGroovyRowResult.get(0);
						if (groovyRowResult != null) {
							total =  (Long) groovyRowResult.get(Parameters.OUTPUT_COUNT);
							//LOGGER.info("total:"+((total != null) ? total.toString() : " Es null "));
						}
					}
				} 
				
			} else {
				LOGGER.info("rows es null:");
			}
						
	   
		} catch (Exception e) {
			LOGGER.error(format(Messages.ERROR_EXECUTING_SERVICE, e.getMessage()));
			throw new ExecutionException(format(Messages.ERROR_EXECUTING_SERVICE, e.getMessage()));
		}
        // Return the generated password
        return ResultExecuteSqlDatasource.builder()
                .result(rows)
                .count(total)
                .build();
    }
    
   
    /*
    private ResultSet executeQuery(RestAPIContext context, String query,  ExecuteSqlDatasourceParameters executeSqlDatasourceParameters) {
    	
    	DataSource datasource = Utils.getDatasource(context, executeSqlDatasourceParameters.getDatasource());
    	
    	 Connection connection = null;
         try {
             connection = datasource.getConnection();
             try (PreparedStatement statement = connection.prepareStatement(query)) {
                 Map<String, Object> params = executeSqlDatasourceParameters.getParams();
                 if (params != null && !params.isEmpty()) {
                     for (Map.Entry<String, Object> entry : params.entrySet()) {
                         statement.setObject(entry.getKey(), entry.getValue());
                     }
                 }

                 return statement.executeQuery();
             }
         } catch (SQLException e) {
        	 LOGGER.error(format(Messages.ERROR_EXECUTING_QUERY, e.getMessage()));
 			throw new ExecutionException(format(Messages.ERROR_EXECUTING_QUERY, e.getMessage()));
         } finally {
             if (connection != null) {
                 try {
                     connection.close();
                 } catch (SQLException e) {
                	 LOGGER.error(format(Messages.ERROR_CLOSING_CONNECTION, e.getMessage()));
         			throw new ExecutionException(format(Messages.ERROR_CLOSING_CONNECTION, e.getMessage()));
                 }
             }
         }
    }*/
    
    /**
     * @param query
     * @param params
     * @return
     */
    private String addOrderToQuery(String query, Map<String, Object> params) {
		if (params != null && !params.isEmpty()) {
    		//  If you want to sort, generate the query with the sorting order   		
    		if (params.containsKey(Parameters.PARAM_ORDER_BY)) {
                LOGGER.info(String.format(Messages.MSG_QUERY_BEFORE, query));
                if (query.contains("%s")) {          
                	String queryIdAux = (Parameters.ORDER_BY.concat((String) params.get(Parameters.PARAM_ORDER_BY)));
                	query = String.format(query, queryIdAux);
                }
                LOGGER.info(String.format(Messages.MSG_QUERY_AFTER, query));
            }
		}
		return query;
    }


}
package com.bonitasoft.processbuilder.rest.api.utils;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringSubstitutor;
import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.web.extension.ResourceProvider;
import org.bonitasoft.web.extension.rest.RestAPIContext;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.processbuilder.rest.api.constants.Constants;
import com.bonitasoft.processbuilder.rest.api.constants.Messages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;

public class Utils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class.getName());

    
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
    	        .withResponseStatus(HttpServletResponse.SC_OK)
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
    

    
    
    /**
     * Load queries property file into a java.util.Properties
     */
    public static String getQuery(String queryId, RestAPIContext context) {
        Properties props = loadProperties(Constants.QUERIES_PROPERTIES, context.getResourceProvider());
        return props.getProperty(queryId);
    }
    
    
    /**
     * @param context
     * @param fileName
     * @param propertyName
     * @return String
     */
    public static String getProperty(RestAPIContext context, String fileName, String propertyName) {
    	try {
        	Properties props = Utils.loadProperties(fileName, context.getResourceProvider());
            return props.getProperty(propertyName);
        } catch (Exception e) {
            LOGGER.error(format(Messages.ERROR_GETTING_PROPERTY,propertyName,fileName), e);
            throw new RuntimeException(Messages.ERROR_GETTING_PROPERTY);
        }
    }
    
    /**
     * @param request
     * @return String
     */
    public static String getServerBaseUrl(HttpServletRequest request) {
    	try {        	
        	return String.format("%s://%s:%d%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
        } catch (Exception e) {
            LOGGER.error(Messages.ERROR_GETTING_CURRENT_SERVER_HOST_URL, e);
            throw new RuntimeException(Messages.ERROR_GETTING_CURRENT_SERVER_HOST_URL);
        }
    }
    
    /**
     * @param urlPattern
     * @param placeholders
     * @return String
     * @throws ExecutionException 
     */
    public static String replacePlaceholders(String urlPattern, Map<String, String> placeholders) throws ExecutionException {
    	try {
            StringSubstitutor substitutor = new StringSubstitutor(placeholders, "[", "]");
            return substitutor.replace(urlPattern);
		    
    	} catch (Exception e) {
    		LOGGER.error(format("Error executing replacePlaceholders: %s", e.getMessage()));
			throw new ExecutionException(format("Error executing replacePlaceholders: %s", e.getMessage()));
    	}
    }

    
    /**
     * @param request
     * @param fieldsToRemove
     * @return
     */
    @Deprecated
    public static Map<String, String> getSqlParametersOld(HttpServletRequest request, Map<String, Boolean> fieldsToRemove) {
        Map<String, String> params = new HashMap<String, String>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (!fieldsToRemove.containsKey(parameterName) || !fieldsToRemove.get(parameterName)) {
                params.put(parameterName, request.getParameter(parameterName));
            }
        }
        //params.remove("queryId");
        return params;
    }
    
    /**
     * @param request
     * @param fieldsToRemove
     * @return
     */
    public static Map<String, Object> getSqlParameters(HttpServletRequest request, Map<String, Boolean> fieldsToRemove) {
        Map<String, Object> params = new HashMap<>();
        Collections.list(request.getParameterNames())
                .stream()
                .filter(parameterName -> !fieldsToRemove.containsKey(parameterName) || !fieldsToRemove.get(parameterName))
                .forEach(parameterName -> params.put(parameterName, request.getParameter(parameterName)));
        return params;
    }

    
    /**
     * @param context
     * @param datasourceName
     * @return
     */
    public static DataSource getDatasource(RestAPIContext context,String datasourceName) {
        Properties props = loadProperties(Constants.DATASOURCE_PROPERTIES, context.getResourceProvider());
        try {
            InitialContext ctx = new InitialContext(props);
            return (DataSource) ctx.lookup(props.getProperty(Constants.DATASOURCE_DOT+ datasourceName));
        } catch (NamingException e) {
        	LOGGER.error(format(Messages.ERROR_GETTING_DATASOURCE, e.getMessage()));
        }
		return null;
    }

    /**
     * @param context
     * @param datasourceName
     * @return
     */
    public static Sql buildSql(RestAPIContext context, String datasourceName) {
        DataSource dataSource = getDatasource(context, datasourceName);
        return new Sql(dataSource);
    }
    
    
    
    /**
     * @param sql
     * @param query
     * @param params
     * @return List<GroovyRowResult>
     * @throws ExecutionException
     */
    public static List<GroovyRowResult> executeSQL(Sql sql, String query, Map<String, Object> params) throws ExecutionException {
    	try {
    		LOGGER.info("executeSQL-->SQL: "+query);
			return params.isEmpty() ? sql.rows(query) : sql.rows(params,query);
		} catch (SQLException e) {
    		LOGGER.error(format(Messages.ERROR_EXECUTING_QUERY.concat("This query:").concat(query).concat(" - ERROR:"), e.getMessage()));
			throw new ExecutionException(format(Messages.ERROR_EXECUTING_QUERY, e.getMessage()));
		}
    }
    
 
    /**
     * @param query
     * @param variableName
     * @param variableValue
     * @return
     */
    public static String substituteVariables(String query, String variableName, Object variableValue) {
        String variablePlaceholder = ":" + variableName;
        return query.replaceAll(Pattern.quote(variablePlaceholder), variableValue.toString());
    }
    
    /**
	 * Method that calculates the minutes, seconds and milliseconds it takes to execute. An initial long must be given.
	 * Display the result by the log.
	 *
	 * @param startTime
	 * @param name
	 */
	public static void logElapsedTime(long startTime, String name) {
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		// Convert milliseconds to minutes, seconds, and milliseconds
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
	
		long milliseconds = elapsedTime % 1000;
		
		LOGGER.info("Elapsed time - {}: {}m {}s {}ms", name, minutes, seconds, milliseconds);
		
	}

}

package com.bonitasoft.processbuilder.rest.api.constants;

public class Messages {

	// 
	public static final String ERROR_FAILED_LOAD_PROPERTIES = "Failed to load properties for REST API extension";
	public static final String ERROR_FAILED_TO_WRITE_BODY_AS_JSON = "Failed to write body response as JSON: ";
    public static final String ERROR_PARAM_MISSING = "the parameter %s is missing";
    public static final String ERROR_IS_NOT_NUMERICAL = "the parameter %s should be a numerical value";
    public static final String ERROR_VALUE_MISSING = "the %s does not refer to an existing query";
    public static final String ERROR_P_OR_C_MISSING = "the parameter c or p are missing";

    // Messages to CreateLoginLink
    public static final String ERROR_USER_DOES_NOT_EXIST = "User does not exist";
    public static final String ERROR_EXECUTING_SERVICE = "Error executing service: %s";
    public static final String ERROR_EXECUTING_BUILD_URL = "Error executing buildUrl: %s";
    public static final String ERROR_EXECUTING_REPLACE_PLACEHOLDERS = "Error executing replacePlaceholders: %s";

    // Messages to AbstractCreateLoginLink
    public static final String ERROR_REQUEST_FOR_EXTENSION_NOT_VALID = "Request for this REST API extension is not valid. ";
    public static final String ERROR_GETTING_PROPERTY = "Error getting %s from %s property file.";
    public static final String ERROR_GENERATING_INFORMATION = "Error generating information to execute the service. ";
    public static final String ERROR_GETTING_CURRENT_SERVER_HOST_URL = "Error getting the url of the current server host. ";
    public static final String ERROR_EXECUTING_REST_API_EXTENSION = "Error executing this REST API extension. ";

    // Messages to  UserManager
    public static final String ERROR_CREATING_USER_ALREADY_EXISTS = "Error creating user because user already exists %s";
    public static final String ERROR_CREATING_USER = "Error creating user %s";
    public static final String ERROR_UPDATING_USER_NOT_FOUND = "Error updating user because user not found %s";
    public static final String ERROR_UPDATING_USER = "Error updating user %s";
    public static final String ERROR_GETTING_USER_NOT_FOUND = "Error getting user (user not found) %s";
    public static final String ERROR_GETTING_USER_RETRIEVE_OR_INVALID_SESSION = "Error getting user (retrieve o invalid session) %s";
    
    // Messages to ExecuteSqlDatasource
    public static final String ERROR_GETTING_DATASOURCE = "Error getting datasource - %s";
    public static final String ERROR_DATASOURCE_MISSING = "Datasource is missing";
    public static final String MSG_QUERY_BEFORE = "Query(Before): %s";
    public static final String MSG_QUERY_AFTER = "Query(After): %s";
    public static final String ERROR_EXECUTING_QUERY = "Error executing query sql: %s";
    public static final String ERROR_CLOSING_CONNECTION = "Error closing connection sql: %s";

}


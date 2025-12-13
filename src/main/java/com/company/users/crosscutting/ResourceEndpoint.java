package com.company.users.crosscutting;

public class ResourceEndpoint {

    public static final String API_VERSION = "api/v1";
    public static final String USER = "/user";
    public static final String USERS = "/users";
    public static final String USER_BY_ID = USER+"/{id}";
    public static final String CONSUMES_TYPE_JSON = "application/json";
    public static final String AUTH = "/auth";
    public static final String LOGIN = AUTH+"/login";
    public static final String REFRESH_TOKEN = AUTH+"/refresh";
    public static final String LOGOUT = AUTH+"/logout";
}

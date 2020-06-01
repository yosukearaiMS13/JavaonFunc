package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> profiles = new HashMap<String, Object>();
            profiles.put("dept", "Development div.");
            profiles.put("email", "yoarai@microsoft.com");
        resultMap.put("name", name);
        resultMap.put("profiles", profiles);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            try {
                String resultJson = new ObjectMapper().writeValueAsString(resultMap);
                return request.createResponseBuilder(HttpStatus.OK).body(resultJson).build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("JSON parsing error").build();
            }
//            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
}

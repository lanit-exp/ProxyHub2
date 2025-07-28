package lanit_exp.proxy_hub.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.exceptions.ApiRequestParseException;
import lanit_exp.proxy_hub.models.ApiRequest;
import lanit_exp.proxy_hub.models.ApiResponse;
import lanit_exp.proxy_hub.responses.ValueResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ApiConverter {

    public static ApiRequest requestToDTO(HttpServletRequest request) {

        String method = request.getMethod();

        String uri = normalizeUri(request.getRequestURI());

        MultiValueMap<String, String> headers = getHeaders(request);

        String body = getBody(request);

        return new ApiRequest(method, uri, headers, body);

    }


    public static ResponseEntity<?> responseToEntity(String responseSting) {

        try {
           ApiResponse apiResponse = new ObjectMapper().readValue(responseSting, ApiResponse.class);

           return ResponseEntity.status(apiResponse.getStatusCodeValue())
                   .headers(httpHeaders -> httpHeaders.addAll(apiResponse.getHeaders()))
                   .body(apiResponse.getBody());

        } catch (Exception e){
            return new ValueResponseEntity(responseSting)
                    .getEntity(HttpStatus.BAD_REQUEST);
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    private static MultiValueMap<String, String> getHeaders(HttpServletRequest request) {
        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            Enumeration<String> values = request.getHeaders(headerName);
            List<String> headerValues = new ArrayList<>();

            while (values.hasMoreElements()) {
                headerValues.add(values.nextElement());
            }

            headersMap.put(headerName, headerValues);
        }

        return headersMap;
    }

    private static String getBody(HttpServletRequest request) {
        if (request.getContentLength() <= 0) return null;

        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new ApiRequestParseException("Ошибка чтения тела Api запроса. ", e);
        }
    }

    private static String normalizeUri(String uri) {
        return uri.replaceAll("^/proxy/(id|tag)/[a-zA-Z0-9-]+", "");
    }

}

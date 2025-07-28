package lanit_exp.proxy_hub.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse {

    private Integer statusCodeValue;
    private String statusCode;
    private LinkedMultiValueMap<String, String> headers;
    private String body;

}

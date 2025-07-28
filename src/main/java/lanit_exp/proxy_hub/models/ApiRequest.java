package lanit_exp.proxy_hub.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

@Getter
@AllArgsConstructor
public class ApiRequest {

    private String method;
    private String uri;
    private MultiValueMap<String, String> headers;
    private String body;

}

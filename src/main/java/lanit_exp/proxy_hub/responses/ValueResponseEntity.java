package lanit_exp.proxy_hub.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Getter
public class ValueResponseEntity {

    @JsonProperty("value")
    private String value;

    public ResponseEntity<ValueResponseEntity> getEntity(int code) {
            return ResponseEntity.status(code)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this);
    }


}

package lanit_exp.proxy_hub.models;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class NodeMessage {

    @Getter
    private String message;
    private LocalDateTime createDate;


    public NodeMessage(String message) {
        this.message = message;
        createDate = LocalDateTime.now();
    }


    public boolean isOutdated(int liveTimeSec) {
        return ChronoUnit.SECONDS.between(createDate, LocalDateTime.now()) > liveTimeSec;
    }

}

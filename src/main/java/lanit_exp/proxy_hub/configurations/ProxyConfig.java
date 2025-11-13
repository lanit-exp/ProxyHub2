package lanit_exp.proxy_hub.configurations;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ProxyConfig {

    @Getter
    private static ProxyConfig proxyConfig;


    @Value("${node.message.live_timeout}")
    private Integer messageLiveTimeout;

    @Value("${node.message.await_timeout}")
    private Integer messageAwaitTimeout;


    @Value("${node.session.idle_timeout}")
    @Setter
    private Integer idleTimeout;

    @Value("${node.session.await_timeout}")
    @Setter
    private Integer nodeAwaitTimeout;


    @Value("${proxyHub.version}")
    private String version;


    @PostConstruct
    public void init(){
        ProxyConfig.proxyConfig = this;
    }

}

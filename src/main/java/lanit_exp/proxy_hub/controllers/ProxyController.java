package lanit_exp.proxy_hub.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.services.MainProxyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ProxyController {

    private final MainProxyService mainProxyService;

    public ProxyController(MainProxyService mainProxyService) {
        this.mainProxyService = mainProxyService;
    }

    @RequestMapping(value = {"/**"},
            method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE})
    public ResponseEntity<?> proxyRequest(HttpServletRequest request) {
        return mainProxyService.requestHandler(request);
    }

}

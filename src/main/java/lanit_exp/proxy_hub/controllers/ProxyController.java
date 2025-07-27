package lanit_exp.proxy_hub.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.responses.ValueResponseEntity;
import lanit_exp.proxy_hub.services.MainProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProxyController {

    private final MainProxyService mainProxyService;


    @RequestMapping(value = {"/proxy/id/{id}/**"})
    public ResponseEntity<?> proxyIdRequest(@PathVariable("id") String id, HttpServletRequest request) {
        return mainProxyService.idRequestHandler(id, request);
    }

    @RequestMapping(value = {"/proxy/tag/{tag}/**"})
    public ResponseEntity<?> proxyTagRequest(@PathVariable("tag") String tag, HttpServletRequest request) {
        return mainProxyService.tagRequestHandler(tag, request);
    }



    //------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = {"/**"})
    public ResponseEntity<?> proxyRequest(HttpServletRequest request) {
        String mes = "Поддерживаются только запросы вида '/proxy/id/{id}/...' и '/proxy/tag/{tag}/...'";
        return new ValueResponseEntity(mes)
                .getEntity(HttpStatus.BAD_REQUEST);
    }


}

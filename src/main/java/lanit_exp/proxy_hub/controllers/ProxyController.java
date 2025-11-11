package lanit_exp.proxy_hub.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.responses.ValueResponseEntity;
import lanit_exp.proxy_hub.services.InnerProxyHubService;
import lanit_exp.proxy_hub.services.MainProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProxyController {

    private final MainProxyService mainProxyService;
    private final InnerProxyHubService innerProxyHubService;


    @RequestMapping(value = {"/proxy/id/{id}/**"})
    public ResponseEntity<?> proxyIdRequest(@PathVariable("id") String id, HttpServletRequest request) {
        return mainProxyService.idRequestHandler(id, request);
    }


    //------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = {"/proxy/tag/{tag}/session"}, method = RequestMethod.POST)
    public ResponseEntity<?> proxyNewSessionRequest(@PathVariable("tag") String tag, HttpServletRequest request) {
        return mainProxyService.newSessionRequestHandler(tag, request);
    }

    @RequestMapping(value = {"/proxy/tag/{tag}/session/{sessionId}"}, method = RequestMethod.DELETE)
    public ResponseEntity<?> proxyQuitSessionRequest(@PathVariable("sessionId") String sessionId, HttpServletRequest request) {
        return mainProxyService.quitSessionRequestHandler(sessionId, request);
    }

    @RequestMapping(value = {"/proxy/tag/{tag}/session/{sessionId}/**"})
    public ResponseEntity<?> proxySessionRequest(@PathVariable("sessionId") String sessionId, HttpServletRequest request) {
        return mainProxyService.sessionRequestHandler(sessionId, request);
    }


    @RequestMapping(value = {"/proxy/tag/{tag}/**"})
    public ResponseEntity<?> proxyTagRequest(HttpServletRequest request) {
        String mes = "Неизвестный тип запроса: '%s'".formatted(request.getRequestURI());
        return new ValueResponseEntity(mes).getEntity(HttpStatus.BAD_REQUEST);
    }


    //------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = {"/proxy/status"}, method = RequestMethod.GET)
    public ResponseEntity<?> proxyHubStatus(HttpServletRequest request) {
        return innerProxyHubService.getProxyHubStatus();
    }

    @RequestMapping(value = {"/proxy/idle/{idle}"}, method = RequestMethod.GET)
    public ResponseEntity<?> proxyHubStatus(@PathVariable("idle") String idle, HttpServletRequest request) {
        return innerProxyHubService.setIdleTimeout(idle);
    }

    //------------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = {"/**"})
    public ResponseEntity<?> proxyRequest(HttpServletRequest request) {
        String mes = "Поддерживаются только запросы вида '/proxy/id/{id}/...' и '/proxy/tag/{tag}/...'";
        return new ValueResponseEntity(mes).getEntity(HttpStatus.BAD_REQUEST);
    }


}

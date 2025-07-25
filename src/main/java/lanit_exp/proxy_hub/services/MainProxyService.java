package lanit_exp.proxy_hub.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MainProxyService {

    public ResponseEntity<?> idRequestHandler(String id, HttpServletRequest request){
        System.out.println(request.getMethod() +  " - " + request.getRequestURI());
        return ResponseEntity.ok("qqw");
    }

    public ResponseEntity<?> tagRequestHandler(String tag, HttpServletRequest request){
        System.out.println(request.getMethod() +  " - " + request.getRequestURI());
        return ResponseEntity.ok("qqw");
    }

}

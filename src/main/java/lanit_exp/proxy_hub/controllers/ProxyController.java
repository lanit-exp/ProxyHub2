package lanit_exp.proxy_hub.controllers;


import lanit_exp.proxy_hub.services.InnerProxyHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/proxy")
@RequiredArgsConstructor
public class ProxyController {

    private final InnerProxyHubService innerProxyHubService;


    @RequestMapping(value = {"/dashboard"}, method = RequestMethod.GET)
    public String proxyHubDashboard(Model model) {
        model.addAttribute("data", innerProxyHubService.getProxyHubStatusJson());
        return "nodes_dashboard";
    }

}

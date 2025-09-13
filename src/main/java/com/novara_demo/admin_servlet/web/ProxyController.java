package com.novara_demo.admin_servlet.web;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.services.InstanceRegistry;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

@Controller
@RequestMapping("/proxy")
public class ProxyController {
//    Proxies requests made through the admin UI to the client
//    in order to attach proper authentication. The buttons for
//    endpoint discovery in the UI can't be used since requests
//    are send directly through the browser and auth fails.
//    The data is 'served' raw in the UI. The UI can be extended
//    and the 'static' links of the buttons can be reworked to
//    go through here but currently there is no way to access
//    this controller through a UI element, you have to manually
//    edit the URL on the address bar when you have a client
//    instance opened. URL example:
//    http://localhost:8081/proxy/b8d83c59bc3a/recentlogins
//    TODO: Add a custom view which you can access through a button and serve the data properly.

    private final HttpHeadersProvider headersProvider;
    private final InstanceRegistry instanceRegistry;
    private final RestClient restClient;
    @Value("${app.url.actuator-manage}")
    private String manageUrl;

    public ProxyController(@Qualifier("customHttpHeadersProvider") HttpHeadersProvider headersProvider, InstanceRegistry instanceRegistry) {
        this.headersProvider = headersProvider;
        this.instanceRegistry = instanceRegistry;
        this.restClient = RestClient.create();
    }

    @GetMapping("/{instanceId}/manage")
    public ResponseEntity<?> proxyManage(@PathVariable String instanceId,
                                         HttpServletRequest request) {
        Instance instance = instanceRegistry.getInstance(InstanceId.of(instanceId)).block();
        if (instance == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = headersProvider.getHeaders(instance);
        ResponseEntity<String> resp = restClient
                .get()
                .uri(manageUrl)
                .headers(h -> h.putAll(headers))
                .retrieve()
                .toEntity(String.class);

        return ResponseEntity.status(resp.getStatusCode())
                .headers(resp.getHeaders())
                .body(resp.getBody());
    }

    @GetMapping("/{instanceId}/{endpointId}")
    public ResponseEntity<?> proxyManageCustomEndpoint(@PathVariable String instanceId,
                                                       @PathVariable String endpointId,
                                                       HttpServletRequest request) {
        Instance instance = instanceRegistry.getInstance(InstanceId.of(instanceId)).block();
        if (instance == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = headersProvider.getHeaders(instance);
        ResponseEntity<String> resp = restClient
                .get()
                .uri(manageUrl + "/" + endpointId)
                .headers(h -> h.putAll(headers))
                .retrieve()
                .toEntity(String.class);

        return ResponseEntity.status(resp.getStatusCode())
                .headers(resp.getHeaders())
                .body(resp.getBody());
    }
}

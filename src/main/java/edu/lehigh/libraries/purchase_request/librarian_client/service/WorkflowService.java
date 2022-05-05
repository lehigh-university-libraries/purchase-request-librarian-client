package edu.lehigh.libraries.purchase_request.librarian_client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.lehigh.libraries.purchase_request.model.PurchaseRequest;
import edu.lehigh.libraries.purchase_request.librarian_client.config.PropertiesConfig;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnWebApplication
@Slf4j
public class WorkflowService {

    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders headers;
    private PropertiesConfig config;

    private String BASE_URL;

    public WorkflowService(RestTemplate restTemplate, PropertiesConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
        initHeaders();

        this.BASE_URL = config.getWorkflowServer().getBaseUrl();
    }

    private void initHeaders() {
        this.headers = new HttpHeaders();
        headers.setBasicAuth(
            config.getWorkflowServer().getUsername(),
            config.getWorkflowServer().getPassword());
    }

    public boolean submitRequest(PurchaseRequest purchaseRequest) {
        HttpEntity<Object> request = new HttpEntity<Object>(purchaseRequest, headers);
        Object result = restTemplate.postForObject(
            BASE_URL + "/purchase-requests", 
            request,
            Object.class);
        log.debug("Submitted request with result " + result);
        return true;
    }

}

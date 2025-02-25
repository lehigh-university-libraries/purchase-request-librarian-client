package edu.lehigh.libraries.purchase_request.librarian_client.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.lehigh.libraries.librarian_call_numbers.model.Librarian;
import edu.lehigh.libraries.purchase_request.librarian_client.config.PropertiesConfig;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnWebApplication
@Slf4j
public class LibrarianCallNumberService {

    @Autowired
    private RestTemplate restTemplate;

    private String BASE_URL;

    public LibrarianCallNumberService(RestTemplate restTemplate, PropertiesConfig config) {
        this.restTemplate = restTemplate;

        try {
            this.BASE_URL = config.getLibrarianCallNumberServer().getBaseUrl();
        }
        catch (NullPointerException e) {
            this.BASE_URL = null;
        }
    }

    public List<Librarian> getLibrarians() {
        if (BASE_URL == null) {
            return Collections.emptyList();
        }

        ResponseEntity<List<Librarian>> result = restTemplate.exchange(
            BASE_URL + "/all",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Librarian>>() {}
        );
        List<Librarian> librarians = result.getBody();
        log.debug("Loaded librarians with result " + librarians);
        return librarians;
    }

}

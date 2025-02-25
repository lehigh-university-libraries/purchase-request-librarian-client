package edu.lehigh.libraries.purchase_request.librarian_client.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import edu.lehigh.libraries.librarian_call_numbers.model.Librarian;
import edu.lehigh.libraries.purchase_request.librarian_client.service.LibrarianCallNumberService;
import edu.lehigh.libraries.purchase_request.librarian_client.service.WorkflowService;
import edu.lehigh.libraries.purchase_request.model.PurchaseRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@ConditionalOnWebApplication
@Validated
@Slf4j
public class AjaxController {
    
    private WorkflowService workflowService;
    private LibrarianCallNumberService librarianCallNumberService;

    public AjaxController(WorkflowService workflowService, LibrarianCallNumberService librarianCallNumberService) {
        this.workflowService = workflowService;
        this.librarianCallNumberService = librarianCallNumberService;
    }

    @PostMapping("/purchase-requests")
    ResponseEntity<PurchaseRequest> requestItem(@RequestBody @Valid PurchaseRequest purchaseRequest, 
        Authentication authentication) {
            
        log.info("Request: POST /request " + purchaseRequest);

        try {
            workflowService.submitRequest(purchaseRequest);
        }
        catch (Exception e) {
            if (e instanceof BadRequest) {
                // rethrow so ExceptionHandlers can deal with it
                log.warn("Caught HTTP client exception, re=throwing", e);
                throw e;
            }
            else {
                log.error("Could not submit purchase to workflow server: " + purchaseRequest, e);
                return ResponseEntity.internalServerError().build();
            }
        }

        return new ResponseEntity<PurchaseRequest>(purchaseRequest, HttpStatus.CREATED);
    }

    @GetMapping("/librarians")
    ResponseEntity<List<Librarian>> getLibrarians(Authentication authentication) {
        log.info("Request: GET /librarians");

        List<Librarian> librarians;
        try {
            librarians = librarianCallNumberService.getLibrarians();
        }
        catch (Exception e) {
            if (e instanceof BadRequest) {
                // rethrow so ExceptionHandlers can deal with it
                log.warn("Caught HTTP client exception, re=throwing", e);
                throw e;
            }
            else {
                log.error("Could not load librarians list", e);
                return ResponseEntity.internalServerError().build();
            }
        }

        return new ResponseEntity<List<Librarian>>(librarians, HttpStatus.OK);
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Problem with input")
    @ExceptionHandler({ConstraintViolationException.class, BadRequest.class})
    public void error() {
        // no op
        log.debug("found problem with input");
    }

}
package edu.lehigh.libraries.purchase_request.librarian_client.controller;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import edu.lehigh.libraries.purchase_request.librarian_client.service.WorkflowService;
import edu.lehigh.libraries.purchase_request.model.PurchaseRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@ConditionalOnWebApplication
@Validated
@Slf4j
public class AjaxController {
    
    private WorkflowService workflowService;

    public AjaxController(WorkflowService workflowService) {
        this.workflowService = workflowService;
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

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Problem with input")
    @ExceptionHandler({ConstraintViolationException.class, BadRequest.class})
    public void error() {
        // no op
        log.debug("found problem with input");
    }

}
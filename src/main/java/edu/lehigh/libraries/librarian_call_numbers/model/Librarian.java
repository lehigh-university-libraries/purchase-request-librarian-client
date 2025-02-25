package edu.lehigh.libraries.librarian_call_numbers.model;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode @ToString
public class Librarian {
    
    public static final String SANITIZED_STRING_PATTERN = "^[A-Za-z0-9\\.-_,\\s]+$";
    public static final String SANITIZED_CALL_NUMBER_PATTERN = SANITIZED_STRING_PATTERN;

    private String username;
    private String lastName;
    private String firstName;
    private List<String> callNumberPrefixes;

}

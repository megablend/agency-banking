package com.nibss.agencybankingservice.util;

import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.Set;

public class BindingResultExtractionUtils {

    public static synchronized Set<String> extractErrors(BindingResult bindingResult) throws NullPointerException {
        if( null == bindingResult)
            throw new NullPointerException("bindingResult cannot be null");

        Set<String> errors = new HashSet<>();

        if( bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream().map( e -> e.getDefaultMessage())
                    .forEach( e -> errors.add(e));
        }
        return errors;
    }

}

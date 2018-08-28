/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 *
 * @author cmegafu
 */
public class FieldValueValidator implements ConstraintValidator<FieldsValue, Object> {
    
    private String field;

    @Override
    public void initialize(FieldsValue a) {
        this.field = a.field();
    }

    @Override
    public boolean isValid(Object t, ConstraintValidatorContext cvc) {
        Object fieldValue = new BeanWrapperImpl(t).getPropertyValue(field);
        if (fieldValue instanceof String) {
            return null != fieldValue && ((String) fieldValue).trim().isEmpty();
        }
        return null != fieldValue;
    }
    
}

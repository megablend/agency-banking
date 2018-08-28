/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.exceptions;

/**
 *
 * @author cmegafu
 */
public class CustomAmountException extends RuntimeException {
    private String amount;
    
    public CustomAmountException(String amount, String message) {
        super(message);
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author cmegafu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionReportDto {
    @NotNull
    private String transactionDate;
    private String cashInCount;
    private String cashInValue;
    private String cashOutCount;
    private String cashOutValue;
    private String accountOpeningCount;
    private String accountOpeningValue;
    private String billsPaymentCount;
    private String billsPaymentValue;
    private String airtimeRechargeCount;
    private String airtimeRechargeValue;
    private String fundTransferCount;
    private String fundTransferValue;
    private String bvnEnrollmentCount;
    private String bvnEnrollmentValue;
    private String othersCount;
    private String othersValue;
    private String additionalService1Count;
    private String additionService1Value;
    private String additionalService2Count;
    private String additionalService2Value;
    @NotNull
    private String agentCode;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.controllers;

import com.nibss.agencybankingservice.configurations.ApplicationProperties;
import com.nibss.agencybankingservice.dto.Response;
import com.nibss.agencybankingservice.dto.TransactionReportDto;
import com.nibss.agencybankingservice.dto.TransactionSummaryReport;
import com.nibss.agencybankingservice.exceptions.CustomAmountException;
import com.nibss.agencybankingservice.util.Responses;
import com.nibss.agencybankingservice.util.Util;
import com.upl.nibss.bvn.model.Agent;
import com.upl.nibss.bvn.model.AgentManager;
import com.upl.nibss.bvn.model.AgentTransactionReport;
import com.upl.nibss.bvn.model.AgentTransactionSummaryReport;
import com.upl.nibss.bvn.model.security.Institutions;
import com.upl.nibss.bvn.service.AgentService;
import com.upl.nibss.bvn.service.AgentTransactionReportService;
import com.upl.nibss.bvn.service.ApiAuditService;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author cmegafu
 */
@RestController
@RequestMapping("/api/transactions/")
@Slf4j
public class TransactionController {
    
    @Autowired
    private AgentService agentService;
    @Autowired
    private AgentTransactionReportService agentTransactionReportService;
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    private ApiAuditService apiAuditService;
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TRANSACTION_DATE_PATTERN = "^\\d{4}\\-\\d{2}-\\d{2}$";
    
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/single", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addSingleTransactionReport(@RequestAttribute("agentManager") AgentManager agentManager, 
                                                     @RequestAttribute("institution") Institutions institution,
                                                     @Valid @RequestBody TransactionReportDto transactionReport, BindingResult result) {
        log.trace("the request for transaction report is {}", transactionReport);
        if (result.hasErrors()) {
            log.error("There was error with the data");
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_REQUEST));
        }
        
        if (null == transactionReport)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_REQUEST));
        
        // check if the agent exists
        Agent agent = agentService.getByCode(transactionReport.getAgentCode());
        if (null == agent)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AGENT));
        
        // check the pattern of the agent code
        if (!transactionReport.getTransactionDate().matches(TRANSACTION_DATE_PATTERN))
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
        
        // check if the month provided is valid 
        String[] dateStr = transactionReport.getTransactionDate().split("-");
        if (dateStr.length != 3)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
        log.trace("The transaction month provided is {}", dateStr[1]);
        if (Integer.valueOf(dateStr[1]) > 12)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_MONTH));
        log.trace("The transaction day provided is {}", dateStr[2]);
        if (Integer.valueOf(dateStr[2]) > 31) 
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_DAY));
        
        Date transactionDate = null;
        try {
            transactionDate = Util.formatDate(DATE_FORMAT, transactionReport.getTransactionDate());
        } catch (ParseException ex) {
            log.error("Invalid date format provided {}", transactionReport.getTransactionDate(), ex);
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
        }
        
        // check if transactions for the stated date
        AgentTransactionReport agentTransactionReport = agentTransactionReportService.getReportByDate(transactionDate, agent);
        if (null == agentTransactionReport) { // insert new records 
            AgentTransactionReport ar = new AgentTransactionReport();
            try {
                ar.setAccountOpeningCount(Util.formatValue(transactionReport.getAccountOpeningCount()));
                ar.setAccountOpeningValue(Util.formatAmount(transactionReport.getAccountOpeningValue()));
                ar.setAdditionalService1Count(Util.formatValue(transactionReport.getAdditionalService1Count()));
                ar.setAdditionalService1Value(Util.formatAmount(transactionReport.getAdditionService1Value()));
                ar.setAdditionalService2Count(Util.formatValue(transactionReport.getAdditionalService2Count()));
                ar.setAdditionalService2Value(Util.formatAmount(transactionReport.getAdditionalService2Value()));
                ar.setAgent(agent);
                ar.setAirtimeRechargeCount(Util.formatValue(transactionReport.getAirtimeRechargeCount()));
                ar.setAirtimeRechargeValue(Util.formatAmount(transactionReport.getAirtimeRechargeValue()));
                ar.setBillPaymentCount(Util.formatValue(transactionReport.getBillsPaymentCount()));
                ar.setBillPaymentValue(Util.formatAmount(transactionReport.getBillsPaymentValue()));
                ar.setBvnEnrollmentCount(Util.formatValue(transactionReport.getBvnEnrollmentCount()));
                ar.setBvnEnrollmentValue(Util.formatAmount(transactionReport.getBvnEnrollmentValue()));
                ar.setCashInCount(Util.formatValue(transactionReport.getCashInCount()));
                ar.setCashInValue(Util.formatAmount(transactionReport.getCashInValue()));
                ar.setCashOutCount(Util.formatValue(transactionReport.getCashOutCount()));
                ar.setCashOutValue(Util.formatAmount(transactionReport.getCashOutValue()));
                ar.setFundTransferCount(Util.formatValue(transactionReport.getFundTransferCount()));
                ar.setFundTransferValue(Util.formatAmount(transactionReport.getFundTransferValue()));
                ar.setOthersCount(Util.formatValue(transactionReport.getOthersCount()));
                ar.setOthersValue(Util.formatAmount(transactionReport.getOthersValue()));
                ar.setAgentManager(agentManager);
                ar.setAgentManagerInstitution(agentManager.getAgentManagerInstitution());
            } catch (CustomAmountException customAmountException) {
                log.error("Invalid amount provided {}", customAmountException.getAmount(), customAmountException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AMOUNT));
            } catch (NumberFormatException numberFormatException) {
                log.error("Invalid amount provided", numberFormatException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_NUMBER));
            }
            
            ar.setTransactionDate(transactionDate);
            AgentTransactionReport savedTransactionReport = agentTransactionReportService.save(ar);
            if (null == savedTransactionReport) {
                return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
            }
            apiAuditService.save(agentManager, Util.convertObjectToJson(transactionReport), null, Util.convertObjectToJson(savedTransactionReport));
        } else {// update transaction report
            try {
                agentTransactionReport.setAccountOpeningCount(Util.formatValue(transactionReport.getAccountOpeningCount()));
                agentTransactionReport.setAccountOpeningValue(Util.formatAmount(transactionReport.getAccountOpeningValue()));
                agentTransactionReport.setAdditionalService1Count(Util.formatValue(transactionReport.getAdditionalService1Count()));
                agentTransactionReport.setAdditionalService1Value(Util.formatAmount(transactionReport.getAdditionService1Value()));
                agentTransactionReport.setAdditionalService2Count(Util.formatValue(transactionReport.getAdditionalService2Count()));
                agentTransactionReport.setAdditionalService2Value(Util.formatAmount(transactionReport.getAdditionalService2Value()));
                agentTransactionReport.setAgent(agent);
                agentTransactionReport.setAirtimeRechargeCount(Util.formatValue(transactionReport.getAirtimeRechargeCount()));
                agentTransactionReport.setAirtimeRechargeValue(Util.formatAmount(transactionReport.getAirtimeRechargeValue()));
                agentTransactionReport.setBillPaymentCount(Util.formatValue(transactionReport.getBillsPaymentCount()));
                agentTransactionReport.setBillPaymentValue(Util.formatAmount(transactionReport.getBillsPaymentValue()));
                agentTransactionReport.setBvnEnrollmentCount(Util.formatValue(transactionReport.getBvnEnrollmentCount()));
                agentTransactionReport.setBvnEnrollmentValue(Util.formatAmount(transactionReport.getBvnEnrollmentValue()));
                agentTransactionReport.setCashInCount(Util.formatValue(transactionReport.getCashInCount()));
                agentTransactionReport.setCashInValue(Util.formatAmount(transactionReport.getCashInValue()));
                agentTransactionReport.setCashOutCount(Util.formatValue(transactionReport.getCashOutCount()));
                agentTransactionReport.setCashOutValue(Util.formatAmount(transactionReport.getCashOutValue()));
                agentTransactionReport.setFundTransferCount(Util.formatValue(transactionReport.getFundTransferCount()));
                agentTransactionReport.setFundTransferValue(Util.formatAmount(transactionReport.getFundTransferValue()));
                agentTransactionReport.setOthersCount(Util.formatValue(transactionReport.getOthersCount()));
                agentTransactionReport.setOthersValue(Util.formatAmount(transactionReport.getOthersValue()));
                agentTransactionReport.setAgentManager(agentManager);
                agentTransactionReport.setAgentManagerInstitution(agentManager.getAgentManagerInstitution());
            } catch (CustomAmountException customAmountException) {
                log.error("Invalid amount provided {}", customAmountException.getAmount(), customAmountException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AMOUNT));
            } catch (NumberFormatException numberFormatException) {
                log.error("Invalid amount provided", numberFormatException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_NUMBER));
            }
            
            agentTransactionReport.setTransactionDate(transactionDate);
            AgentTransactionReport updatedAgentTransactionReport = agentTransactionReportService.save(agentTransactionReport);
            if (null == updatedAgentTransactionReport) {
                return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
            }
            apiAuditService.save(agentManager, Util.convertObjectToJson(transactionReport), Util.convertObjectToJson(agentTransactionReport), Util.convertObjectToJson(updatedAgentTransactionReport));
        }
        
        // TODO: add agent manager activities to the audit trail
        
       return ResponseEntity.ok(new Response(Responses.SUCCESSFUL));
    }
    
    @PostMapping(value = "/bulk", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addBulk(@RequestAttribute("agentManager") AgentManager agentManager, @Valid @RequestBody List<TransactionReportDto> transactionReports, BindingResult result) {
        log.trace("the request for bulk transaction report is {}", transactionReports);
        if (result.hasErrors()) {
            log.trace("There was error with the data ==> {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_REQUEST_BULK));
        }
        
        if (transactionReports.size() > applicationProperties.getMaxTransactionReport()) 
            return ResponseEntity.badRequest().body(new Response(Responses.TRANSACTION_REPORT_EXCEEDS_THRESHOLD));
        for (TransactionReportDto transactionReport: transactionReports) {
            // check if the agent exists
            Agent agent = agentService.getByCode(transactionReport.getAgentCode());
            if (null == agent)
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AGENT));
            
            // check the pattern of the agent code
            if (!transactionReport.getTransactionDate().matches(TRANSACTION_DATE_PATTERN))
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
            
            // check if the month provided is valid 
            String[] dateStr = transactionReport.getTransactionDate().split("-");
            if (dateStr.length != 3)
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
            log.trace("The transaction month provided is {}", dateStr[1]);
            if (Integer.valueOf(dateStr[1]) > 12)
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_MONTH));
            log.trace("The transaction day provided is {}", dateStr[2]);
            if (Integer.valueOf(dateStr[2]) > 31) 
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_DAY));

            Date transactionDate = null;
            try {
                transactionDate = Util.formatDate(DATE_FORMAT, transactionReport.getTransactionDate());
            } catch (ParseException ex) {
                log.error("Invalid date format provided {}", transactionReport.getTransactionDate(), ex);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
            }

            // check if transactions for the stated date
            AgentTransactionReport agentTransactionReport = agentTransactionReportService.getReportByDate(transactionDate, agent);
            if (null == agentTransactionReport) { // insert new records 
                AgentTransactionReport ar = new AgentTransactionReport();
                try {
                    ar.setAccountOpeningCount(Util.formatValue(transactionReport.getAccountOpeningCount()));
                    ar.setAccountOpeningValue(Util.formatAmount(transactionReport.getAccountOpeningValue()));
                    ar.setAdditionalService1Count(Util.formatValue(transactionReport.getAdditionalService1Count()));
                    ar.setAdditionalService1Value(Util.formatAmount(transactionReport.getAdditionService1Value()));
                    ar.setAdditionalService2Count(Util.formatValue(transactionReport.getAdditionalService2Count()));
                    ar.setAdditionalService2Value(Util.formatAmount(transactionReport.getAdditionalService2Value()));
                    ar.setAgent(agent);
                    ar.setAirtimeRechargeCount(Util.formatValue(transactionReport.getAirtimeRechargeCount()));
                    ar.setAirtimeRechargeValue(Util.formatAmount(transactionReport.getAirtimeRechargeValue()));
                    ar.setBillPaymentCount(Util.formatValue(transactionReport.getBillsPaymentCount()));
                    ar.setBillPaymentValue(Util.formatAmount(transactionReport.getBillsPaymentValue()));
                    ar.setBvnEnrollmentCount(Util.formatValue(transactionReport.getBvnEnrollmentCount()));
                    ar.setBvnEnrollmentValue(Util.formatAmount(transactionReport.getBvnEnrollmentValue()));
                    ar.setCashInCount(Util.formatValue(transactionReport.getCashInCount()));
                    ar.setCashInValue(Util.formatAmount(transactionReport.getCashInValue()));
                    ar.setCashOutCount(Util.formatValue(transactionReport.getCashOutCount()));
                    ar.setCashOutValue(Util.formatAmount(transactionReport.getCashOutValue()));
                    ar.setFundTransferCount(Util.formatValue(transactionReport.getFundTransferCount()));
                    ar.setFundTransferValue(Util.formatAmount(transactionReport.getFundTransferValue()));
                    ar.setOthersCount(Util.formatValue(transactionReport.getOthersCount()));
                    ar.setOthersValue(Util.formatAmount(transactionReport.getOthersValue()));
                    ar.setAgentManager(agentManager);
                    ar.setAgentManagerInstitution(agentManager.getAgentManagerInstitution());
                } catch (CustomAmountException customAmountException) {
                    log.error("Invalid amount provided {}", customAmountException.getAmount(), customAmountException);
                    return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AMOUNT));
                } catch (NumberFormatException numberFormatException) {
                    log.error("Invalid amount provided", numberFormatException);
                    return ResponseEntity.badRequest().body(new Response(Responses.INVALID_NUMBER));
                }

                ar.setTransactionDate(transactionDate);
                AgentTransactionReport savedTransactionReport = agentTransactionReportService.save(ar);
                if (null == savedTransactionReport) {
                    return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
                }
                apiAuditService.save(agentManager, Util.convertObjectToJson(transactionReport), null, Util.convertObjectToJson(savedTransactionReport));
            } else {// update transaction report
                try {
                    agentTransactionReport.setAccountOpeningCount(Util.formatValue(transactionReport.getAccountOpeningCount()));
                    agentTransactionReport.setAccountOpeningValue(Util.formatAmount(transactionReport.getAccountOpeningValue()));
                    agentTransactionReport.setAdditionalService1Count(Util.formatValue(transactionReport.getAdditionalService1Count()));
                    agentTransactionReport.setAdditionalService1Value(Util.formatAmount(transactionReport.getAdditionService1Value()));
                    agentTransactionReport.setAdditionalService2Count(Util.formatValue(transactionReport.getAdditionalService2Count()));
                    agentTransactionReport.setAdditionalService2Value(Util.formatAmount(transactionReport.getAdditionalService2Value()));
                    agentTransactionReport.setAgent(agent);
                    agentTransactionReport.setAirtimeRechargeCount(Util.formatValue(transactionReport.getAirtimeRechargeCount()));
                    agentTransactionReport.setAirtimeRechargeValue(Util.formatAmount(transactionReport.getAirtimeRechargeValue()));
                    agentTransactionReport.setBillPaymentCount(Util.formatValue(transactionReport.getBillsPaymentCount()));
                    agentTransactionReport.setBillPaymentValue(Util.formatAmount(transactionReport.getBillsPaymentValue()));
                    agentTransactionReport.setBvnEnrollmentCount(Util.formatValue(transactionReport.getBvnEnrollmentCount()));
                    agentTransactionReport.setBvnEnrollmentValue(Util.formatAmount(transactionReport.getBvnEnrollmentValue()));
                    agentTransactionReport.setCashInCount(Util.formatValue(transactionReport.getCashInCount()));
                    agentTransactionReport.setCashInValue(Util.formatAmount(transactionReport.getCashInValue()));
                    agentTransactionReport.setCashOutCount(Util.formatValue(transactionReport.getCashOutCount()));
                    agentTransactionReport.setCashOutValue(Util.formatAmount(transactionReport.getCashOutValue()));
                    agentTransactionReport.setFundTransferCount(Util.formatValue(transactionReport.getFundTransferCount()));
                    agentTransactionReport.setFundTransferValue(Util.formatAmount(transactionReport.getFundTransferValue()));
                    agentTransactionReport.setOthersCount(Util.formatValue(transactionReport.getOthersCount()));
                    agentTransactionReport.setOthersValue(Util.formatAmount(transactionReport.getOthersValue()));
                    agentTransactionReport.setAgentManager(agentManager);
                    agentTransactionReport.setAgentManagerInstitution(agentManager.getAgentManagerInstitution());
                } catch (CustomAmountException customAmountException) {
                    log.error("Invalid amount provided {}", customAmountException.getAmount(), customAmountException);
                    return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AMOUNT));
                } catch (NumberFormatException numberFormatException) {
                    log.error("Invalid amount provided", numberFormatException);
                    return ResponseEntity.badRequest().body(new Response(Responses.INVALID_NUMBER));
                }

                agentTransactionReport.setTransactionDate(transactionDate);
                AgentTransactionReport updatedAgentTransactionReport = agentTransactionReportService.save(agentTransactionReport);
                if (null == updatedAgentTransactionReport) {
                    return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
                }
                apiAuditService.save(agentManager, Util.convertObjectToJson(transactionReport), Util.convertObjectToJson(agentTransactionReport), Util.convertObjectToJson(updatedAgentTransactionReport));
            }
        }
        return ResponseEntity.ok(new Response(Responses.SUCCESSFUL));
    }
    
    @PostMapping(value = "/summary", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addSummaryReport(@RequestAttribute("agentManager") AgentManager agentManager, 
                                                     @RequestAttribute("institution") Institutions institution,
                                                     @Valid @RequestBody TransactionSummaryReport summaryReport, BindingResult result) {
        
        log.trace("the request for transaction summary report is {}", summaryReport);
        if (result.hasErrors()) {
            log.error("There was error with the data");
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_SUMMARY_REQUEST));
        }
        
        if (null == summaryReport)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_SUMMARY_REQUEST));
        
        // check the pattern of the agent code
        if (!summaryReport.getTransactionDate().matches(TRANSACTION_DATE_PATTERN))
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
        
        // check if the month provided is valid 
        String[] dateStr = summaryReport.getTransactionDate().split("-");
        if (dateStr.length != 3)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
        log.trace("The transaction month provided is {}", dateStr[1]);
        if (Integer.valueOf(dateStr[1]) > 12)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_MONTH));
        log.trace("The transaction day provided is {}", dateStr[2]);
        if (Integer.valueOf(dateStr[2]) > 31) 
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_TRANSACTION_DAY));
        
        Date transactionDate = null;
        try {
            transactionDate = Util.formatDate(DATE_FORMAT, summaryReport.getTransactionDate());
        } catch (ParseException ex) {
            log.error("Invalid date format provided {}", summaryReport.getTransactionDate(), ex);
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_DATE_FORMAT));
        }
        
        AgentTransactionSummaryReport sumReport = agentTransactionReportService.getSummaryReportByDate(transactionDate, agentManager);
        if (null == sumReport) {
            AgentTransactionSummaryReport sr = new AgentTransactionSummaryReport();
            try {
                sr.setAccountOpeningCount(Util.formatValue(summaryReport.getAccountOpeningCount()));
                sr.setAccountOpeningValue(Util.formatAmount(summaryReport.getAccountOpeningValue()));
                sr.setAdditionalService1Count(Util.formatValue(summaryReport.getAdditionalService1Count()));
                sr.setAdditionalService1Value(Util.formatAmount(summaryReport.getAdditionService1Value()));
                sr.setAdditionalService2Count(Util.formatValue(summaryReport.getAdditionalService2Count()));
                sr.setAdditionalService2Value(Util.formatAmount(summaryReport.getAdditionalService2Value()));
                sr.setAirtimeRechargeCount(Util.formatValue(summaryReport.getAirtimeRechargeCount()));
                sr.setAirtimeRechargeValue(Util.formatAmount(summaryReport.getAirtimeRechargeValue()));
                sr.setBillPaymentCount(Util.formatValue(summaryReport.getBillsPaymentCount()));
                sr.setBillPaymentValue(Util.formatAmount(summaryReport.getBillsPaymentValue()));
                sr.setBvnEnrollmentCount(Util.formatValue(summaryReport.getBvnEnrollmentCount()));
                sr.setBvnEnrollmentValue(Util.formatAmount(summaryReport.getBvnEnrollmentValue()));
                sr.setCashInCount(Util.formatValue(summaryReport.getCashInCount()));
                sr.setCashInValue(Util.formatAmount(summaryReport.getCashInValue()));
                sr.setCashOutCount(Util.formatValue(summaryReport.getCashOutCount()));
                sr.setCashOutValue(Util.formatAmount(summaryReport.getCashInValue()));
                sr.setFundTransferCount(Util.formatValue(summaryReport.getFundTransferCount()));
                sr.setFundTransferValue(Util.formatAmount(summaryReport.getFundTransferValue()));
                sr.setOthersCount(Util.formatValue(summaryReport.getOthersCount()));
                sr.setOthersValue(Util.formatAmount(summaryReport.getOthersValue()));
                sr.setTransactionDate(transactionDate);
                sr.setAgentManager(agentManager);
            } catch (CustomAmountException customAmountException) {
                log.error("Invalid amount provided {}", customAmountException.getAmount(), customAmountException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AMOUNT));
            } catch (NumberFormatException numberFormatException) {
                log.error("Invalid amount provided", numberFormatException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_NUMBER));
            }
            AgentTransactionSummaryReport savedSumReport = agentTransactionReportService.saveSummary(sr);
            if (null == savedSumReport) {
                return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
            }
            apiAuditService.save(agentManager, Util.convertObjectToJson(summaryReport), null, Util.convertObjectToJson(savedSumReport));
        } else {
            try {
                sumReport.setAccountOpeningCount(Util.formatValue(summaryReport.getAccountOpeningCount()));
                sumReport.setAccountOpeningValue(Util.formatAmount(summaryReport.getAccountOpeningValue()));
                sumReport.setAdditionalService1Count(Util.formatValue(summaryReport.getAdditionalService1Count()));
                sumReport.setAdditionalService1Value(Util.formatAmount(summaryReport.getAdditionService1Value()));
                sumReport.setAdditionalService2Count(Util.formatValue(summaryReport.getAdditionalService2Count()));
                sumReport.setAdditionalService2Value(Util.formatAmount(summaryReport.getAdditionalService2Value()));
                sumReport.setAirtimeRechargeCount(Util.formatValue(summaryReport.getAirtimeRechargeCount()));
                sumReport.setAirtimeRechargeValue(Util.formatAmount(summaryReport.getAirtimeRechargeValue()));
                sumReport.setBillPaymentCount(Util.formatValue(summaryReport.getBillsPaymentCount()));
                sumReport.setBillPaymentValue(Util.formatAmount(summaryReport.getBillsPaymentValue()));
                sumReport.setBvnEnrollmentCount(Util.formatValue(summaryReport.getBvnEnrollmentCount()));
                sumReport.setBvnEnrollmentValue(Util.formatAmount(summaryReport.getBvnEnrollmentValue()));
                sumReport.setCashInCount(Util.formatValue(summaryReport.getCashInCount()));
                sumReport.setCashInValue(Util.formatAmount(summaryReport.getCashInValue()));
                sumReport.setCashOutCount(Util.formatValue(summaryReport.getCashOutCount()));
                sumReport.setCashOutValue(Util.formatAmount(summaryReport.getCashInValue()));
                sumReport.setFundTransferCount(Util.formatValue(summaryReport.getFundTransferCount()));
                sumReport.setFundTransferValue(Util.formatAmount(summaryReport.getFundTransferValue()));
                sumReport.setOthersCount(Util.formatValue(summaryReport.getOthersCount()));
                sumReport.setOthersValue(Util.formatAmount(summaryReport.getOthersValue()));
                sumReport.setAgentManager(agentManager);
            } catch (CustomAmountException customAmountException) {
                log.error("Invalid amount provided {}", customAmountException.getAmount(), customAmountException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AMOUNT));
            } catch (NumberFormatException numberFormatException) {
                log.error("Invalid amount provided", numberFormatException);
                return ResponseEntity.badRequest().body(new Response(Responses.INVALID_NUMBER));
            }
            AgentTransactionSummaryReport updatedSumReport = agentTransactionReportService.saveSummary(sumReport);
            if (null == updatedSumReport) {
                return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
            }
            apiAuditService.save(agentManager, Util.convertObjectToJson(summaryReport), Util.convertObjectToJson(sumReport), Util.convertObjectToJson(updatedSumReport));
        }
        return ResponseEntity.ok(new Response(Responses.SUCCESSFUL));
    }
}

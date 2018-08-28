/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.controllers;

import com.nibss.agencybankingservice.dto.ResetDto;
import com.nibss.agencybankingservice.dto.ResetResponse;
import com.nibss.agencybankingservice.dto.Response;
import com.nibss.agencybankingservice.mail.MailProcessor;
import com.nibss.agencybankingservice.util.Responses;
import com.nibss.agencybankingservice.util.Util;
import com.upl.nibss.bvn.model.security.InstitutionCredentials;
import com.upl.nibss.bvn.model.security.Institutions;
import com.upl.nibss.bvn.model.security.Service;
import com.upl.nibss.bvn.repo.InstitutionService;
import com.upl.nibss.bvn.repo.ServiceRepo;
import com.upl.nibss.bvn.util.PasswordUtil;
import com.upl.nibss.bvn.util.encryption.EncyptionUtil;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author cmegafu
 */
@RestController
@Slf4j
public class ApiController {
    
    @Autowired
    private InstitutionService institutionService;
    
    @Autowired
    private ServiceRepo serviceRepo;
    
    @Autowired
    private MailProcessor mail;
    
    @Value("${app.mail.cc}")
    private String[] mailCc;
    
    @GetMapping("/ping")
    public String ping() {
        return "Service is available";
    }
    
    /**
     * Reset Institution Credentials
     * 
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity reset(@Valid @RequestBody ResetDto reset, BindingResult result) {
        log.trace("The reset request is {}", reset);
        if (result.hasErrors()) {
            log.error("There was error with the data");
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_RESET_REQUEST));
        }
        
        if (null == reset) {
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_RESET_REQUEST));
        }
        
        // get institution details
        Institutions institution = institutionService.getByEmailAndCode(reset.getInstitutionCode(), reset.getEmail());
        if (null == institution)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_INSTITUTION));
        
        List<Service> services = serviceRepo.findAll();
        if (null == services || services.size() <= 0)
            return ResponseEntity.badRequest().body(new Response(Responses.NO_SERVICES));
        
        String aesKey;
        String ivKey;
        try {
            aesKey = EncyptionUtil.generateKey();
            ivKey = EncyptionUtil.generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("Unable to generate aesKey and ivKey",e);
            return ResponseEntity.badRequest().body(new Response(Responses.SYSTEM_ERROR));
        }
        
        String password = Util.generateString(10, true, true);
        
        // check if this institution credential already exists
        InstitutionCredentials institutionCredentials = institutionService.getCredentialsByInstitution(institution);
        InstitutionCredentials savedCredentials = null;
        if (null == institutionCredentials) {
             savedCredentials = institutionService.saveCredentials(aesKey, ivKey, services.get(0), password, institution, new InstitutionCredentials());
        } else {
            savedCredentials = institutionService.saveCredentials(aesKey, ivKey, services.get(0), password, institution, institutionCredentials);
        }
        
        if (null == savedCredentials)
            return ResponseEntity.badRequest().body(new Response(Responses.UNABLE_TO_GENERATE_CREDENTIALS));
        
        // send a mail to the institution
        String[] cc = mailCc;
        String[] recipients = new String[]{institution.getEmail()}; //TODO. add receipients
        String[] variables = new String[]{institution.getName(), aesKey, ivKey, password}; // TODO. add variables 
        mail.sendMail("Agency Banking: Credentials Reset", "reset", cc, null, recipients, variables);
        
        return ResponseEntity.ok(new ResetResponse(Responses.SUCCESSFUL, aesKey, ivKey, password));
    }
}

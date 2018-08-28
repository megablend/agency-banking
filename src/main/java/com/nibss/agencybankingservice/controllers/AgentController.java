/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.controllers;

import com.nibss.agencybankingservice.dto.AgentDto;
import com.nibss.agencybankingservice.dto.AgentResponse;
import com.nibss.agencybankingservice.dto.Response;
import com.nibss.agencybankingservice.dto.UpdateAgentDto;
import com.nibss.agencybankingservice.services.ApiAgentService;
import com.nibss.agencybankingservice.util.Responses;
import com.nibss.agencybankingservice.util.Util;
import com.upl.nibss.bvn.dto.PasswordPolicyResponse;
import com.upl.nibss.bvn.enums.ServicesProvided;
import com.upl.nibss.bvn.model.Agent;
import com.upl.nibss.bvn.model.AgentManager;
import com.upl.nibss.bvn.model.Lga;
import com.upl.nibss.bvn.model.User;
import com.upl.nibss.bvn.model.security.Institutions;
import com.upl.nibss.bvn.service.AgentService;
import com.upl.nibss.bvn.service.ApiAuditService;
import com.upl.nibss.bvn.service.LgaService;
import com.upl.nibss.bvn.service.PasswordValidationService;
import com.upl.nibss.bvn.service.UserService;
import java.util.regex.Pattern;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/agents")
@Slf4j
public class AgentController {
    
    private static final Pattern BVN_PATTERN = Pattern.compile("^\\d{11}$");
    @Autowired
    private UserService userService;
    @Autowired
    private LgaService  lgaService;
    @Autowired
    private ApiAgentService apiAgentService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private PasswordValidationService passwordValidationService;
    @Autowired
    private ApiAuditService apiAuditService;
    private static final String GPS_PATTERN = "^\\d+\\.\\d{4,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    /**
     * Create or Update Agent
     * 
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createAgent(@RequestAttribute("agentManager") AgentManager agentManager, 
                                            @RequestAttribute("institution") Institutions institution,
                                            @Valid @RequestBody AgentDto agent, BindingResult result) {
        log.trace("The request is {}", agent);
        if (result.hasErrors()) {
            log.error("There was error with the data");
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AGENT_REQUEST));
        }
        
        // Validate Provided Records
        ResponseEntity validationResponse = validateAgentParameters(agent);
        if (null != validationResponse)
            return validationResponse;
        
        // check if the provided LGA exists
        Lga lga = lgaService.getLgaByName(agent.getLga());
        if (null == lga) 
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_LGA));
        
        // Check if this user exists
        User user = userService.getByEmail(agent.getEmailAddress());
        User savedUser = null;
        
        if (null == user) {// create user account
            // check if BVN exists
            if (null != userService.getByBvn(agent.getBvn()))
                return ResponseEntity.badRequest().body(new Response(Responses.BVN_EXISTS));
            savedUser = apiAgentService.saveUser(agent, new User(), lga);
            if (null == savedUser)
                return ResponseEntity.badRequest().body(new Response(Responses.UNABLE_TO_CREATE_USER));
            log.trace("Agent user details successfully created");
        } else {
            log.trace("Unable to save user details for the agent because it already exists", agent);
            return ResponseEntity.badRequest().body(new Response(Responses.AGENT_ALREADY_EXISTS));
        }
        
        // check if this agent exists
        Agent agt = agentService.getAgentByUser(savedUser);
        Agent savedAgent = null;
        if (null == agt) { // create a new agent
            String code = agentService.generateCode();
            if (null == code || code.trim().isEmpty())
                return ResponseEntity.badRequest().body(new Response(Responses.UNABLE_TO_GENERATE_CODE));
            savedAgent = apiAgentService.saveAgent(agent, new Agent(), agentManager, savedUser, code);
            apiAuditService.save(agentManager, Util.convertObjectToJson(agent), null, Util.convertObjectToJson(savedAgent));
            log.trace("Agent successfully created");
        } else {
            log.trace("Unable to save agent details because it already exists in the database {}", agent);
            return ResponseEntity.badRequest().body(new Response(Responses.AGENT_ALREADY_EXISTS));
        }
        
        if (null == savedAgent)
            return ResponseEntity.badRequest().body(new Response(Responses.UNABLE_TO_CREATE_AGENT));
        
        return ResponseEntity.ok(new AgentResponse(Responses.SUCCESSFUL, savedAgent.getCode()));
    }
    
    /**
     * Validate Update Agents Parameters
     * 
     */
    public ResponseEntity valildateUpdateAgentParameters(UpdateAgentDto agent) {
        if (null == agent)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AGENT_REQUEST_NULL));
        
        // validate the email address provided
        if (!EMAIL_PATTERN.matcher(agent.getEmailAddress()).matches())
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_EMAIL_ADDRESS));
        
        // check if the GPS parameters provided are correct
        if (null != agent.getLatitude() && !agent.getLatitude().matches(GPS_PATTERN))
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_LATITUDE));
        
        if (null != agent.getLongitude() && !agent.getLongitude().matches(GPS_PATTERN))
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_LONGITUDE));
        
        // check if the bvn provided is valid
        if (!BVN_PATTERN.matcher(agent.getBvn()).matches())
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_BVN));
        
        // check if the services provided is out of scope
        if (null != agent.getServicesProvided() && !agent.getServicesProvided().isEmpty()) {
            for (String s: agent.getServicesProvided()) {
                try {
                    if (null == ServicesProvided.valueOf(s.replace(" ", "_").toUpperCase())) {
                        return ResponseEntity.badRequest().body(new Response(Responses.INVALID_SERVICE_PROVIDED));
                    }
                } catch (Exception e) {
                    log.error("Service not provided", e);
                    return ResponseEntity.badRequest().body(new Response(Responses.INVALID_SERVICE_PROVIDED));
                }
            }
        }
        return null;
    }
    
    /**
     * Validate Agent Parameters 
     * 
     */
    public ResponseEntity validateAgentParameters(AgentDto agent) {
        if (null == agent)
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AGENT_REQUEST_NULL));
        
        // validate the email address provided
        if (!EMAIL_PATTERN.matcher(agent.getEmailAddress()).matches())
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_EMAIL_ADDRESS));
        
        // check if the provided password is inline with our policy
        PasswordPolicyResponse passwordPolicyResponse = passwordValidationService.isValid(agent.getUsername(), agent.getPassword());
        if (null == passwordPolicyResponse || null == passwordPolicyResponse.getResponseCode() || !passwordPolicyResponse.getResponseCode().equals("00")) {
            log.trace("Invalid password provided: {}", null != passwordPolicyResponse && null != passwordPolicyResponse.getResponseMessage() ? passwordPolicyResponse.getResponseMessage() : "Something went wrong when trying to validate user's password, please try again");
            return ResponseEntity.badRequest().body(new Response(Responses.PASSWORD_POLICY_VIOLATION));
        }
        
        // check if the GPS parameters provided are correct
        if (null != agent.getLatitude() && !agent.getLatitude().matches(GPS_PATTERN))
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_LATITUDE));
        
        if (null != agent.getLongitude() && !agent.getLongitude().matches(GPS_PATTERN))
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_LONGITUDE));
        
        // check if the bvn provided is valid
        if (!BVN_PATTERN.matcher(agent.getBvn()).matches())
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_BVN));
        
        // check if the services provided is out of scope
        if (null != agent.getServicesProvided() && !agent.getServicesProvided().isEmpty()) {
            for (String s: agent.getServicesProvided()) {
                try {
                    if (null == ServicesProvided.valueOf(s.replace(" ", "_").toUpperCase())) {
                        return ResponseEntity.badRequest().body(new Response(Responses.INVALID_SERVICE_PROVIDED));
                    }
                } catch (Exception e) {
                    log.error("Service not provided", e);
                    return ResponseEntity.badRequest().body(new Response(Responses.INVALID_SERVICE_PROVIDED));
                }
            }
        }
        return null;
    }
    
    /**
     * Update Agent Details
     * 
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateAgent(@RequestAttribute("agentManager") AgentManager agentManager, 
                                        @RequestAttribute("institution") Institutions institution,
                                        @Valid @RequestBody UpdateAgentDto agent, BindingResult result) {
        log.trace("The request is {}", agent);
        if (result.hasErrors()) {
            log.error("There was an error with the data");
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_AGENT_REQUEST));
        }
        
        // Validate Provided Records
        ResponseEntity validationResponse = valildateUpdateAgentParameters(agent);
        if (null != validationResponse)
            return validationResponse;
        
        // check if the provided LGA exists
        Lga lga = lgaService.getLgaByName(agent.getLga());
        if (null == lga) 
            return ResponseEntity.badRequest().body(new Response(Responses.INVALID_LGA));
        
        // Validate the agent code and ensure it belongs to this agent manager 
        Agent agt = agentService.getByCodeAndAgentManager(agent.getAgentCode(), agentManager);
        if (null == agt) {
            log.trace("The agent with the code {} does not exist for the agent manager {}", agent.getAgentCode(), agentManager);
            return ResponseEntity.badRequest().body(new Response(Responses.AGENT_DOES_NOT_EXIST));
        } else {
            if (null == agt.getUser()) {
                log.trace("The user {} linked to this agent does not exist.", agt.getUser());
                return ResponseEntity.badRequest().body(new Response(Responses.AGENT_DOES_NOT_EXIST));
            } else {
                // check if the bvn exists for another user
                if (null != userService.getByBvnAndNotId(agent.getBvn(), agt.getUser().getId()))
                    return ResponseEntity.badRequest().body(new Response(Responses.BVN_EXISTS));
                
                // Check if the provided email address exists
                if (null != userService.getByEmailAndNotId(agent.getEmailAddress(), agt.getUser().getId()))
                    return ResponseEntity.badRequest().body(new Response(Responses.USER_EMAIL_EXISTS));
                
                // update user details
                User savedUser = apiAgentService.updateUser(agent, agt.getUser(), lga);
                if (null == savedUser)
                    return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
                log.trace("Agent user details successfully updated");
                
                // update agent details 
                Agent savedAgent = apiAgentService.updateAgent(agent, agt, agentManager, savedUser, null);
                
                if (null == savedAgent)
                    return ResponseEntity.badRequest().body(new Response(Responses.DATABASE_ERROR));
                
                apiAuditService.save(agentManager, Util.convertObjectToJson(agent), Util.convertObjectToJson(agt), Util.convertObjectToJson(savedAgent));
                log.trace("Agent successfully updated");
            }
        }
        
        return ResponseEntity.ok(new AgentResponse(Responses.SUCCESSFUL, agent.getAgentCode()));
    }
}

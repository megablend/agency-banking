/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.services;

import com.nibss.agencybankingservice.configurations.ApplicationProperties;
import com.nibss.agencybankingservice.dto.AgentDto;
import com.nibss.agencybankingservice.dto.UpdateAgentDto;
import com.upl.nibss.bvn.embeddables.ContactDetails;
import com.upl.nibss.bvn.embeddables.GPS;
import com.upl.nibss.bvn.embeddables.Name;
import com.upl.nibss.bvn.enums.ServicesProvided;
import com.upl.nibss.bvn.enums.Title;
import com.upl.nibss.bvn.enums.UserType;
import com.upl.nibss.bvn.model.Agent;
import com.upl.nibss.bvn.model.AgentManager;
import com.upl.nibss.bvn.model.Lga;
import com.upl.nibss.bvn.model.User;
import com.upl.nibss.bvn.service.AgentService;
import com.upl.nibss.bvn.service.RoleService;
import com.upl.nibss.bvn.service.UserService;
import com.upl.nibss.bvn.util.encryption.EncyptionUtil;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author cmegafu
 */
@Service
@Transactional(rollbackFor = {Exception.class})
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class ApiAgentService {
    
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationProperties  applicatonProperties;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AgentService  agentService;
    
    public User saveUser(AgentDto agent, User user, Lga lga) {
        if (null == user.getId()) {// save user
            user.setUsername(agent.getUsername());
            user.setPassword(EncyptionUtil.doSHA512Encryption(agent.getPassword(), applicatonProperties.getEncryptionSalt()));
            user.setChange_password(true);
            user.setUserType(UserType.AGENT);
            user.setRole(roleService.getByName(applicatonProperties.getAgentRole()));
            user.setCreatedAt(new Date());
            user.setBvn(agent.getBvn());
        } 
        
        user.setContactDetails(new ContactDetails(agent.getStreetNumber(), agent.getStreetName(), agent.getStreetDescription(), agent.getCity(), lga));
        user.setEmailAddress(agent.getEmailAddress());
        user.setName(new Name(Title.valueOf(agent.getTitle().trim().toUpperCase()), agent.getFirstName(), agent.getMiddleName(), agent.getLastName()));
        user.setPhoneList(agent.getPhoneList());
        user.setUpdatedAt(new Date());
        try {
            return userService.save(user);
        } catch(Exception e) {
            log.error("Unable to save user", e);
        }
        return null;
    }
    
    public User updateUser(UpdateAgentDto agent, User user, Lga lga) {
        user.setBvn(agent.getBvn());
        user.setContactDetails(new ContactDetails(agent.getStreetNumber(), agent.getStreetName(), agent.getStreetDescription(), agent.getCity(), lga));
        user.setEmailAddress(agent.getEmailAddress());
        user.setName(new Name(Title.valueOf(agent.getTitle().trim().toUpperCase()), agent.getFirstName(), agent.getMiddleName(), agent.getLastName()));
        user.setPhoneList(agent.getPhoneList());
        user.setUpdatedAt(new Date());
        try {
            return userService.save(user);
        } catch(Exception e) {
            log.error("Unable to update user", e);
        }
        return null;
    }
    
    public Agent saveAgent(AgentDto agentDto, Agent agent, AgentManager agentManager, User user, String code) {
        log.trace("User ID is {}", user.getId());
        if (null == agent.getId()) {
            agent.setCode(code);
            agent.setUser(user);
            agent.setCreatedAt(new Date());
        }
        Set<ServicesProvided> servicesProvided = agentDto.getServicesProvided().stream().map((s) -> {
            return ServicesProvided.valueOf(s);
        }).collect(Collectors.toSet());
        agent.setAdditionalInfo1(agentDto.getAdditionalInfo1());
        agent.setAdditionalInfo2(agentDto.getAdditionalInfo2());
        agent.setGps(new GPS(Double.valueOf(agentDto.getLongitude()), Double.valueOf(agentDto.getLatitude())));
        agent.setServicesProvided(servicesProvided);
        agent.setAgentManager(agentManager);
        agent.setBiometrics(null);
        agent.setCreatedBy(agentManager.getUser());
        agent.setUpdatedBy(agentManager.getUser());
        agent.setWard(agentDto.getWard());
        agent.setUpdatedAt(new Date());
        log.trace("The id of the agent is {}", agent.getId());
        log.trace("The code for this agent is {}", agent.getCode());
        log.trace("The user ID of the agent is {}", agent.getUser().getId());
        log.error("agent ===> {}", agent);
       return agentService.save(agent);
    }
    
    public Agent updateAgent(UpdateAgentDto agentDto, Agent agent, AgentManager agentManager, User user, String code) {
        Set<ServicesProvided> servicesProvided = agentDto.getServicesProvided().stream().map((s) -> {
            return ServicesProvided.valueOf(s);
        }).collect(Collectors.toSet());
        agent.setAdditionalInfo1(agentDto.getAdditionalInfo1());
        agent.setAdditionalInfo2(agentDto.getAdditionalInfo2());
        agent.setGps(new GPS(Double.valueOf(agentDto.getLongitude()), Double.valueOf(agentDto.getLatitude())));
        agent.setServicesProvided(servicesProvided);
        agent.setAgentManager(agentManager);
        agent.setBiometrics(null);
        agent.setCreatedBy(agentManager.getUser());
        agent.setUpdatedBy(agentManager.getUser());
        agent.setWard(agentDto.getWard());
        agent.setUpdatedAt(new Date());
       return agentService.save(agent);
    }
}

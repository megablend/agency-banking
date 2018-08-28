/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.controllers;

import com.nibss.agencybankingservice.dto.AgentDto;
import com.upl.nibss.bvn.embeddables.ContactDetails;
import com.upl.nibss.bvn.embeddables.Name;
import com.upl.nibss.bvn.enums.ServicesProvided;
import com.upl.nibss.bvn.enums.Title;
import com.upl.nibss.bvn.enums.UserType;
import com.upl.nibss.bvn.model.Country;
import com.upl.nibss.bvn.model.Lga;
import com.upl.nibss.bvn.model.Role;
import com.upl.nibss.bvn.model.State;
import com.upl.nibss.bvn.model.Task;
import com.upl.nibss.bvn.model.User;
import com.upl.nibss.bvn.util.encryption.EncyptionUtil;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author cmegafu
 */
public class Util {
    
    public static Set<Task> getTasks() {
        Set<Task> tasks = new HashSet();
        Task task = new Task();
        task.setName("Create Enroller");
        task.setDescription("Create Enroller");
        task.setActivated(true);
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());
        task.setUrl("/create-task");  
        tasks.add(task);
        return tasks;
    }
    
    public static Role getRole() {
        Role role = new Role();
        role.setActivated(true);
        role.setCreatedAt(new Date());
        role.setName("AGENT");
        role.setTasks(getTasks());
        role.setUpdatedAt(new Date());
        role.setUserType(UserType.AGENT);
        return role;
    }
    
    public static Country getCountry() {
        // save country 
        Country country = new Country();
        country.setCreatedAt(new Date());
        country.setName("Nigeria");
        country.setUpdatedAt(new Date());
        return country;
    }
    
    public static State getState() {
        // save state 
        State state = new State();
        state.setCountry(getCountry());
        state.setCreatedAt(new Date());
        state.setName("Anambra");
        state.setUpdatedAt(new Date());
        return state;
    }
    
    public static AgentDto getAgentRequest() {
        AgentDto agent = new AgentDto();
        Set<String> phoneList = new HashSet<>();
        Set<String> servicesProvided = new HashSet<>();
        servicesProvided.add(ServicesProvided.valueOf("CASH_IN").toString());
        servicesProvided.add(ServicesProvided.valueOf("CASH_OUT").toString());
        phoneList.add("08023905077");
        agent.setBvn("08023905077");
        agent.setCity("Oshodi");
        agent.setEmailAddress("00B2VZ@pagaagents.com");
        agent.setFirstName("Amoo");
        agent.setLastName("Shittu");
        agent.setLatitude("6.493078");
        agent.setLongitude("3.382097");
        agent.setMiddleName("");
        agent.setPassword("gE&DEA7ebZr6Fx");
        agent.setPhoneList(phoneList);
        agent.setServicesProvided(servicesProvided);
        return agent;
    }
    
    public static Lga getLga() {
        Lga lga = new Lga();
        lga.setCreatedAt(new Date());
        lga.setName("Onitsha North");
        lga.setState(getState());
        lga.setUpdatedAt(new Date());
        return lga;
    }
    
    public static User getUser() {
        Set<String> phoneList = new HashSet<>();
        phoneList.add("08060108165");
        User user = new User();
        user.setId(1L);
        user.setUsername("people");
        user.setPassword(EncyptionUtil.doSHA512Encryption("my_password", "salt"));
        user.setChange_password(true);
        user.setUserType(UserType.AGENT);
        user.setRole(getRole());
        user.setBvn("2222222222");
        user.setContactDetails(new ContactDetails("25", "Aguda Street", "Behind Excellence Hotel", "", getLga()));
        user.setEmailAddress("megablendjobs@gmail.com");
        user.setName(new Name(Title.MR, "Charles", "Nonso", "Megafu"));
        user.setPhoneList(phoneList);
        user.setUpdatedAt(new Date());
        return user;
    }
}

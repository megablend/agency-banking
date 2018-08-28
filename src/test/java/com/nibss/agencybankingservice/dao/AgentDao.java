/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.dao;

import com.upl.nibss.bvn.embeddables.ContactDetails;
import com.upl.nibss.bvn.embeddables.GPS;
import com.upl.nibss.bvn.embeddables.Name;
import com.upl.nibss.bvn.enums.ServicesProvided;
import com.upl.nibss.bvn.enums.Title;
import com.upl.nibss.bvn.enums.UserType;
import com.upl.nibss.bvn.model.Agent;
import com.upl.nibss.bvn.model.Country;
import com.upl.nibss.bvn.model.Lga;
import com.upl.nibss.bvn.model.Role;
import com.upl.nibss.bvn.model.State;
import com.upl.nibss.bvn.model.Task;
import com.upl.nibss.bvn.model.User;
import com.upl.nibss.bvn.repo.AgentRepo;
import com.upl.nibss.bvn.util.encryption.EncyptionUtil;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 * @author cmegafu
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class AgentDao {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private AgentRepo agentRepo;
    
    private Set<Task> getTasks() {
        Set<Task> tasks = new HashSet();
        Task task = new Task();
        task.setName("Create Enroller");
        task.setDescription("Create Enroller");
        task.setActivated(true);
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());
        task.setUrl("/create-task");
        Task savedTask = testEntityManager.persist(task);  
        tasks.add(savedTask);
        return tasks;
    }
    
    private Role getRole() {
        Role role = new Role();
        role.setActivated(true);
        role.setCreatedAt(new Date());
        role.setName("AGENT");
        role.setTasks(getTasks());
        role.setUpdatedAt(new Date());
        role.setUserType(UserType.AGENT);
        Role savedRole = testEntityManager.persist(role);
        return savedRole;
    }
    
    private Country getCountry() {
        // save country 
        Country country = new Country();
        country.setCreatedAt(new Date());
        country.setName("Nigeria");
        country.setUpdatedAt(new Date());
        Country savedCountry = testEntityManager.persist(country);
        return savedCountry;
    }
    
    private State getState() {
        // save state 
        State state = new State();
        state.setCountry(getCountry());
        state.setCreatedAt(new Date());
        state.setName("Anambra");
        state.setUpdatedAt(new Date());
        State savedState = testEntityManager.persist(state);
        return savedState;
    }
    
    private Lga getLga() {
        Lga lga = new Lga();
        lga.setCreatedAt(new Date());
        lga.setName("Onitsha North");
        lga.setState(getState());
        lga.setUpdatedAt(new Date());
        return lga;
    }
    
    private User getUser() {
        Set<String> phoneList = new HashSet<>();
        phoneList.add("08060108165");
        User user = new User();
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
        User savedUser = testEntityManager.persist(user);
        return savedUser;
    }
    
    private Set<ServicesProvided> getServicesProvided() {
        Set<ServicesProvided> servicesProvided = new HashSet<>();
        servicesProvided.add(ServicesProvided.BVN_ENROLMENT);
        return servicesProvided;
    }
}

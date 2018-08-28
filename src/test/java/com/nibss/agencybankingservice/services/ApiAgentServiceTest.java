/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.services;

import com.upl.nibss.bvn.embeddables.GPS;
import com.upl.nibss.bvn.model.Agent;
import com.upl.nibss.bvn.service.AgentService;
import java.util.Date;
import org.junit.Before;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author cmegafu
 */
//@RunWith(SpringRunner.class)
public class ApiAgentServiceTest {
    @TestConfiguration
    static class ApiAgentServiceTestContextConfiguration {
        @Bean
        public ApiAgentService apiAgentService() {
            return new ApiAgentService();
        }
        
        @Bean
        public AgentService agentService() {
            return new AgentService();
        }
    }
    
    @Autowired
    private ApiAgentService apiAgentSetrvice;
    @Autowired
    private AgentService agentService;
    
    @Before
    public void setUp() {
        Agent agent = new Agent();
        agent.setCode("000009");
        agent.setUser(null);
        agent.setCreatedAt(new Date());
        agent.setAdditionalInfo1("");
        agent.setAdditionalInfo2("");
        agent.setGps(new GPS(6.6666, 7.8888));
        agent.setServicesProvided(null);
        agent.setAgentManager(null);
        agent.setBiometrics(null);
        agent.setCreatedBy(null);
        agent.setUpdatedBy(null);
        agent.setWard(null);
        agent.setUpdatedAt(new Date());
        when(agentService.save(agent)).thenReturn(agent);
    }
    
//    @Test
//    public void whenSaveAgentUpdateAgent_thenReturnAgent() {
//        Agent agent = new Agent();
//        agent.setCode("000009");
//        Agent foundAgent = apiAgentSetrvice.saveUpdateAgent(null, agent, null, null, null);
//        assertThat(foundAgent.getCode()).isEqualTo(agent.getCode());
//    }
}

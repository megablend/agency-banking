/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.controllers;

import com.nibss.agencybankingservice.configurations.ApplicationProperties;
import com.nibss.agencybankingservice.dto.AgentDto;
import com.nibss.agencybankingservice.filters.SecurityFilter;
import com.nibss.agencybankingservice.services.ApiAgentService;
import com.upl.nibss.bvn.dto.PasswordPolicyResponse;
import com.upl.nibss.bvn.embeddables.Name;
import com.upl.nibss.bvn.enums.Title;
import com.upl.nibss.bvn.enums.UserType;
import com.upl.nibss.bvn.model.Agent;
import com.upl.nibss.bvn.model.AgentManager;
import com.upl.nibss.bvn.model.AgentManagerInstitution;
import com.upl.nibss.bvn.model.Lga;
import com.upl.nibss.bvn.model.User;
import com.upl.nibss.bvn.model.security.InstitutionCredentials;
import com.upl.nibss.bvn.model.security.Institutions;
import com.upl.nibss.bvn.repo.InstitutionService;
import com.upl.nibss.bvn.service.AgentMgrService;
import com.upl.nibss.bvn.service.AgentService;
import com.upl.nibss.bvn.service.ApiAuditService;
import com.upl.nibss.bvn.service.LgaService;
import com.upl.nibss.bvn.service.PasswordValidationService;
import com.upl.nibss.bvn.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 *
 * @author cmegafu
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AgentController.class)
public class AgentControllerTest {
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private WebApplicationContext wac;
    
    @MockBean
    private AgentMgrService agentManagerService;
    @MockBean
    private InstitutionService institutionService;
    
    @MockBean
    private ApplicationProperties applicationProperties;
    
    @MockBean
    private UserService userService;
    @MockBean
    private LgaService  lgaService;
    @MockBean
    private ApiAgentService apiAgentService;
    @MockBean
    private AgentService agentService;
    @MockBean
    private PasswordValidationService passwordValidationService;
    
    @MockBean
    private ApiAuditService apiAuditService;
    private static final String SIGNATURE = "2af36b1486e72c996999b51e815aafa7a627896573bac4c16268f212321c074f7129a2026e51c3d06c9bcc2f2cbad952334a81b6513fac451d72033b3e2d77c7";
    private static final String GENERATED_CODE = "000444";
    private static final String USERNAME = "00B2VZ";
    private static final String PASSWORD = "gE&DEA7ebZr6Fx";
    private static final String AGENT_CODE = "6585585";
    private static final String LGA = "Oshodi/isolo";
    private static final String REQUEST_CONTENT = "afa742d393c0f96f5958d92d228286b726242a69793f98275ad25f6cc1a887e6389f11d6e2c99ca080a580d6a1d626dcc46d8e3e18a99bfadceac7d437b13d84ca1abd0a57f44435ff575bf6f6ef91a646ee004c692237069791246f623207a70e961cf57600e9d73e9677ec8fcd6725ce0f34a8607a39a6e9b96b928cb4d71f81c2d85d36efd7b7212d61838eb3c183af519122b1b380899126ba74beff42aa1d909f7024943576e4ea5846f604854359a715c84a5140483d8f6cdda3c6669856678404b1feba3bea7ff80dfacbf738879217014f474388310585a723556f406f8ee840562b33e8987dcbd28cf334c0fc22dbb5b8b78cc75d830104f0dc1c9bbd3ecb2baef9636ad30b99a508c5493652a47f15452e84a86aa859301c56913ccef5ae2ca118349855c46a20c28dff2b046fd409f5779f6c3d38d917a2ef5b8112ffeb170e17aaf9736e3ff5884dfb5c95513065c8e1f201743d038721a7daf76cb29f46d975b0c7cae2bc8a0e9e9ca260daed2163c08e7d52a8691cd9aa1d790adf41ec0ebce6a32b9745b7037da7801312542747ac0abafe9b1d9f2dcd967c742d2a284067c58ef30ada35ac1e1e75ae1de0b258fb3f78ae60f25e0dc1fa02d1956eeb0391ae777360a9c254b9e5f6e37cbbfc46180fa56d5bd8ebc38e99e286e68efacfddb13f949621c8a5319cd4602b9e51630701c3740431d25fbf2730e328aac0ebc2b74efba51bd76ef58a53";
    
    @Before 
    public void setup() {
        applicationProperties = new ApplicationProperties();
        applicationProperties.setEncryptionSalt("test_encryption_salt");
        
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new SecurityFilter(agentManagerService, institutionService, userService, applicationProperties.getEncryptionSalt()))

                .build();
        // preset agent manager code 
        when(agentManagerService.getByCode("0000009")).thenReturn(getAgentManager());
        when(institutionService.getInstitutionByCode("0000009")).thenReturn(getInstitution());
        when(institutionService.getCredentialsByInstitution(getInstitution())).thenReturn(getCredentials());
    }
    
    
    private AgentManagerInstitution getAgentManagerInstitution() {
        return new AgentManagerInstitution(Long.valueOf(1), "0000009", "Paga Mobile", "page@gmail.com");
    }
    
    private Agent getAgent() {
        Agent agent = new Agent();
        agent.setCode(AGENT_CODE);
        return agent;
    }
    
    private AgentManager getAgentManager() {
        AgentManager agentManager = new AgentManager();
        agentManager.setActivated(true);
        agentManager.setAgentManagerInstitution(getAgentManagerInstitution());
        agentManager.setCode("0000009");
        agentManager.setUser(new User(Long.valueOf(1), "agentmanager@gmail.com", "agentManager", true, null));
        return agentManager;
    }
    
    private Institutions getInstitution() {
        return new Institutions(Long.valueOf(1), "0000009", "Paga Mobile", "uduak@gmail.com", 1, null);
    }
    
    private InstitutionCredentials getCredentials() {
        return new InstitutionCredentials(Long.valueOf(1), getInstitution(), "qh/Q13nHBh1D80qx", "vNQKwxUQ5IAMxUE2", "$5$vNQKwxUQ5IAMxUE2$4rTwBinVhU50xYyMMxEn7Ype4a3YWywOxx/FRKJSMqC", 3, null, Long.valueOf(1));
    }
    
    private Lga lga() {
        return new Lga(Long.valueOf(1), LGA, null);
    }
    
    private User getUserByEmail() {
        return new User(Long.valueOf(1), "megablendjobs@gmail.com", "megacharsy", true, null);
    }
    
    private User getUserByBvn() {
        return new User(Long.valueOf(1), new Name(Title.MR, "Charles", "Nonso", "Megafu"), "22222222222", "megablendjobs@gmail.com", "somebody", true, UserType.AGENT);
    }
    
    @Test
    public void whenCreateAgentWithWrongSignature_thenReturnForbidden() throws Exception {
        mvc.perform(post("/api/agents/create").header("Authorization", "Basic MDAwMDAwOTplanlJNGQ5NVlD")
                        .header("SIGNATURE", "jjkdfjkhdjhdjkfhjkdfh")
                        .header("SIGNATURE_METH", "SHA512")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("afa742d393c0f96f5958d92d228286b726242a69793f98275ad25f6cc1a887e6389f11d6e2c99ca080a580d6a1d626dce35392a56db5c80353557780b1505ab600680c83ea1ae39aa609f8bc548f38ae9fa130fad8c4488bf213e8a573fc1f02fcfc79254866dc477d7ae335b942c25e12967d7ba728ea8a8e7d7b80afb5d97a5752516452339cbe30938a84e7d1b3d26c1642bd6e5003401e737c406710dc687a4f2dc09b55a7fc648d331180512d85372d1e987aab12b72cfd0057412b801c5c5f9156f2312d657c634edb9c0790b6f50afbcbffa7aba01cebd432358af6db1303d6a04722fa2f7215180f0fadd03c7b1aaf0053a0c67b351ad381a6cdc2bf793add6f19fa2d79fc9ec76b2105066f72360bbafdda89759d379dea658056fbc66ac9ade9c0f3c9b65d78dc603cee89bc09aa31eabe571c241d4f67851b3aacb4d77c8cbe4049731752dd0f5c9d63276ac0faeb690e650fbd02379f57d3b5c3492f4d3f6027f6b0473a1c299dece447ac8745cede274bdc56e6fe01b994a98529a7be301523649fb5bf00f61943930f076a32d250c51f64018eb635d4a8fd325eb17a805a6f4a49c4c1b4e8cbbce0f15de8a70a46ca9206697f939d1b484e1b4224af2408359a844b3e9be6329a1ff13d9b77e060583d47c8368fd3336590673953be9f65bf4930a3f77590ebd84d7246013dfdacac92b067c39c4115411fb9"))
//                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", Matchers.is("Invalid Signature")));
    }
    
    @Test
    public void whenCreateAgent_thenReturnResponseEntity() throws Exception {
        // Preset the necessary methods 
        when(lgaService.getLgaByName(LGA)).thenReturn(lga());
        when(userService.getByEmail("megablendjobs@gmail.com")).thenReturn(getUserByEmail());
        when(userService.getByBvn("22222222222")).thenReturn(getUserByBvn()).thenReturn(getUserByBvn());
        when(userService.save(any())).thenReturn(Util.getUser());
        when(apiAgentService.saveUser(any(), any(), any())).thenReturn(Util.getUser());
        when(agentService.getAgentByUser(new User())).thenReturn(getAgent());
        when(agentService.generateCode()).thenReturn(GENERATED_CODE);
        when(apiAgentService.saveAgent(any(), any(), any(), any(), any())).thenReturn(getAgent());
        when(passwordValidationService.isValid(USERNAME, PASSWORD)).thenReturn(new PasswordPolicyResponse("Password Successfully validated", "00"));
        
        mvc.perform(post("/api/agents/create").header("Authorization", "Basic MDAwMDAwOTplanlJNGQ5NVlD")
                        .header("SIGNATURE", SIGNATURE)
                        .header("SIGNATURE_METH", "SHA512")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("afa742d393c0f96f5958d92d228286b726242a69793f98275ad25f6cc1a887e6389f11d6e2c99ca080a580d6a1d626dce35392a56db5c80353557780b1505ab600680c83ea1ae39aa609f8bc548f38ae9fa130fad8c4488bf213e8a573fc1f02fcfc79254866dc477d7ae335b942c25e12967d7ba728ea8a8e7d7b80afb5d97a5752516452339cbe30938a84e7d1b3d26c1642bd6e5003401e737c406710dc687a4f2dc09b55a7fc648d331180512d85372d1e987aab12b72cfd0057412b801c5c5f9156f2312d657c634edb9c0790b6f50afbcbffa7aba01cebd432358af6db1303d6a04722fa2f7215180f0fadd03c7b1aaf0053a0c67b351ad381a6cdc2bf793add6f19fa2d79fc9ec76b2105066f72360bbafdda89759d379dea658056fbc66ac9ade9c0f3c9b65d78dc603cee89bc09aa31eabe571c241d4f67851b3aacb4d77c8cbe4049731752dd0f5c9d63276ac0faeb690e650fbd02379f57d3b5c3492f4d3f6027f6b0473a1c299dece447ac8745cede274bdc56e6fe01b994a98529a7be301523649fb5bf00f61943930f076a32d250c51f64018eb635d4a8fd325eb17a805a6f4a49c4c1b4e8cbbce0f15de8a70a46ca9206697f939d1b484e1b4224af2408359a844b3e9be6329a1ff13d9b77e060583d47c8368fd3336590673953be9f65bf4930a3f77590ebd84d7246013dfdacac92b067c39c4115411fb9"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.responseCode", Matchers.is("00")))
                .andExpect(jsonPath("$.agentCode", Matchers.is(AGENT_CODE)));
    }
    
    @Test
    public void whenCreateAgentWithExistingUser_thenReturnBadRequest() throws Exception {
        // Preset the necessary methods 
        when(lgaService.getLgaByName(LGA)).thenReturn(lga());
        when(userService.getByEmail("00B2VZ@pagaagents.com")).thenReturn(getUserByEmail());
        when(userService.getByBvn("22222222222")).thenReturn(getUserByBvn()).thenReturn(getUserByBvn());
        when(userService.save(any())).thenReturn(Util.getUser());
        when(apiAgentService.saveUser(any(), any(), any())).thenReturn(Util.getUser());
        when(agentService.getAgentByUser(new User())).thenReturn(getAgent());
        when(agentService.generateCode()).thenReturn(GENERATED_CODE);
        when(apiAgentService.saveAgent(any(), any(), any(), any(), any())).thenReturn(getAgent());
        when(passwordValidationService.isValid(USERNAME, PASSWORD)).thenReturn(new PasswordPolicyResponse("Password Successfully validated", "00"));
        
        mvc.perform(post("/api/agents/create").header("Authorization", "Basic MDAwMDAwOTplanlJNGQ5NVlD")
                        .header("SIGNATURE", SIGNATURE)
                        .header("SIGNATURE_METH", "SHA512")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("afa742d393c0f96f5958d92d228286b726242a69793f98275ad25f6cc1a887e6389f11d6e2c99ca080a580d6a1d626dce35392a56db5c80353557780b1505ab600680c83ea1ae39aa609f8bc548f38ae9fa130fad8c4488bf213e8a573fc1f02fcfc79254866dc477d7ae335b942c25e12967d7ba728ea8a8e7d7b80afb5d97a5752516452339cbe30938a84e7d1b3d26c1642bd6e5003401e737c406710dc687a4f2dc09b55a7fc648d331180512d85372d1e987aab12b72cfd0057412b801c5c5f9156f2312d657c634edb9c0790b6f50afbcbffa7aba01cebd432358af6db1303d6a04722fa2f7215180f0fadd03c7b1aaf0053a0c67b351ad381a6cdc2bf793add6f19fa2d79fc9ec76b2105066f72360bbafdda89759d379dea658056fbc66ac9ade9c0f3c9b65d78dc603cee89bc09aa31eabe571c241d4f67851b3aacb4d77c8cbe4049731752dd0f5c9d63276ac0faeb690e650fbd02379f57d3b5c3492f4d3f6027f6b0473a1c299dece447ac8745cede274bdc56e6fe01b994a98529a7be301523649fb5bf00f61943930f076a32d250c51f64018eb635d4a8fd325eb17a805a6f4a49c4c1b4e8cbbce0f15de8a70a46ca9206697f939d1b484e1b4224af2408359a844b3e9be6329a1ff13d9b77e060583d47c8368fd3336590673953be9f65bf4930a3f77590ebd84d7246013dfdacac92b067c39c4115411fb9"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.responseCode", Matchers.is("32")));
    }
    
    @Test
    public void whenCreateAgentWithExistingAgent_thenReturnBadRequest() throws Exception {
        // Preset the necessary methods 
        when(lgaService.getLgaByName(LGA)).thenReturn(lga());
        when(userService.getByEmail("00B2VZ@pagaagents.com")).thenReturn(getUserByEmail());
        when(userService.getByBvn("22222222222")).thenReturn(getUserByBvn()).thenReturn(getUserByBvn());
        when(userService.save(any())).thenReturn(Util.getUser());
        when(apiAgentService.saveUser(any(), any(), any())).thenReturn(Util.getUser());
        when(agentService.getAgentByUser(any())).thenReturn(getAgent());
        when(agentService.generateCode()).thenReturn(GENERATED_CODE);
        when(apiAgentService.saveAgent(any(), any(), any(), any(), any())).thenReturn(getAgent());
        when(passwordValidationService.isValid(USERNAME, PASSWORD)).thenReturn(new PasswordPolicyResponse("Password Successfully validated", "00"));
        
        mvc.perform(post("/api/agents/create").header("Authorization", "Basic MDAwMDAwOTplanlJNGQ5NVlD")
                        .header("SIGNATURE", SIGNATURE)
                        .header("SIGNATURE_METH", "SHA512")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("afa742d393c0f96f5958d92d228286b726242a69793f98275ad25f6cc1a887e6389f11d6e2c99ca080a580d6a1d626dce35392a56db5c80353557780b1505ab600680c83ea1ae39aa609f8bc548f38ae9fa130fad8c4488bf213e8a573fc1f02fcfc79254866dc477d7ae335b942c25e12967d7ba728ea8a8e7d7b80afb5d97a5752516452339cbe30938a84e7d1b3d26c1642bd6e5003401e737c406710dc687a4f2dc09b55a7fc648d331180512d85372d1e987aab12b72cfd0057412b801c5c5f9156f2312d657c634edb9c0790b6f50afbcbffa7aba01cebd432358af6db1303d6a04722fa2f7215180f0fadd03c7b1aaf0053a0c67b351ad381a6cdc2bf793add6f19fa2d79fc9ec76b2105066f72360bbafdda89759d379dea658056fbc66ac9ade9c0f3c9b65d78dc603cee89bc09aa31eabe571c241d4f67851b3aacb4d77c8cbe4049731752dd0f5c9d63276ac0faeb690e650fbd02379f57d3b5c3492f4d3f6027f6b0473a1c299dece447ac8745cede274bdc56e6fe01b994a98529a7be301523649fb5bf00f61943930f076a32d250c51f64018eb635d4a8fd325eb17a805a6f4a49c4c1b4e8cbbce0f15de8a70a46ca9206697f939d1b484e1b4224af2408359a844b3e9be6329a1ff13d9b77e060583d47c8368fd3336590673953be9f65bf4930a3f77590ebd84d7246013dfdacac92b067c39c4115411fb9"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.responseCode", Matchers.is("32")));
    }
   
}

package com.nibss.agencybankingservice.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ConfigurationProperties(prefix = "agency.banking.app")
@Data
@Validated
public class ApplicationProperties implements Serializable{


    /**
     * The key used in retrieving client identifier
     * from the HttpServletRequest's header
     */
    @NotNull
    private String agentManagerCode = "AGENTMANAGERCODE";
    
    /**
     * Encryption Salt
     * 
     */
    @NotNull
    private String encryptionSalt = "sg6T0g6UYQNB4wSM5O7ujCN6i1e8KI0c";
    
    /**
     * Get the agent role
     * 
     */
    @NotNull
    private String agentRole = "AGENT";
    
    /**
     * Maximum bulk transaction size
     * 
     */
    @NotNull
    private int maxTransactionReport = 10;
}

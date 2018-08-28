/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author cmegafu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponse {
    private String responseCode;
    private String agentCode;
}

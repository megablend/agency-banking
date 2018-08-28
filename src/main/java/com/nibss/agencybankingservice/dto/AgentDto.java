/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.dto;

import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author cmegafu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AgentDto {
    private String additionalInfo1;
    private String additionalInfo2;
    @NotBlank
    private String bvn;
    @NotBlank
    private String city;
    @Email
    private String emailAddress;
    
    private String latitude;
    private String longitude;
    @NotBlank
    private String lga;
    private String firstName;
    @NotBlank
    private String lastName;
    private String middleName;
    @NotBlank
    private String title;
    @NotNull
    private Set<String> phoneList;
    @NotNull
    private Set<String> servicesProvided;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String streetNumber;
    @NotBlank
    private String streetName;
    @NotBlank
    private String streetDescription;
    private String ward;
}

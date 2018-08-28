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
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author cmegafu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetDto {
    @NotBlank
    private String institutionCode;
    @NotBlank
    private String email;
}

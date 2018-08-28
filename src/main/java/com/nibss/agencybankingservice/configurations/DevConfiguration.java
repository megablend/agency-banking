/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.configurations;

import com.nibss.agencybankingservice.util.SecurityUtil;
import com.nibss.agencybankingservice.util.Util;
import com.nibss.cryptography.Hasher;
import com.nibss.cryptography.IVAesHasher;
import com.upl.nibss.bvn.util.PasswordUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author cmegafu
 */
@Configuration
@Profile("dev")
@Slf4j
public class DevConfiguration {
    
    @Bean
    public CommandLineRunner runner() {
        return (args) -> {
            String iv = "vNQKwxUQ5IAMxUE2";
            String apiKey = "qh/Q13nHBh1D80qx";
            String authorization = new String(Base64.encodeBase64("0000009:ejyI4d95YC".getBytes()));
            log.trace("Authorization: " + authorization);
            String unencryptedSignature = "0000009" + Util.convertDateToString(new Date(), "yyyyMMdd") + "ejyI4d95YC";
            String signature = SecurityUtil.doSHA512(unencryptedSignature);
            log.trace("Signature: " + signature);
            String password = "jAMHv9dkFL";
            String encryptedPassword = PasswordUtil.hashPassword(password, "0000009",iv);
            log.trace("Encrypted Password: " + encryptedPassword);
            String payload = "{ \"agentCode\":\"02292\", \"additionalInfo1\":\"string\", \"additionalInfo2\":\"string\", \"bvn\":\"22222222222\", \"city\":\"Lagos\", \"emailAddress\":\"opium@gmail.com\", \"latitude\":6.6000, \"longitude\":6.6000, \"lga\":\"Mkpae-Enin\", \"firstName\":\"Theo\", \"lastName\":\"Adebiyi\", \"middleName\":\"Amara\", \"title\":\"Mr\", \"phoneList\":[ \"0803479108199\" ], \"servicesProvided\":[ \"CASH_IN\", \"CASH_OUT\", \"BVN_ENROLLMENT\" ], \"streetNumber\":\"Street Number\", \"streetName\":\"Ahmadu Bello Way\", \"streetDescription\":\"Behind Excellence Hotel\", \"ward\":\"xxx\" }";
            Hasher hasher = new IVAesHasher(apiKey, iv);
            log.trace("The payload is ============");
            log.trace(hasher.encrypt(payload));
        };
    }
}

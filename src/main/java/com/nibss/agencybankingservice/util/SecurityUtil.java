/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.util;

import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author cmegafu
 */
@Slf4j
public class SecurityUtil {
    
    public static String doSHA512(String shaRequest) {
        String resp = "";
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(shaRequest.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            resp = sb.toString();

        } catch (Exception e) {
            log.error("Unable to decrypt SHA 512", e);
        }

        return resp;
    }

    public static String doSHA256(String shaRequest) {
        String resp = "";
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(shaRequest.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            resp = sb.toString();

        } catch (Exception e) {
            log.error("Unable to decrypt SHA-256", e);
        }

        return resp;
    }
}

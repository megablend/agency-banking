/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.mail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author cmegafu
 */
@Service
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MailProcessor {

    @Autowired
    private Environment env;
    @Autowired
    private MailConfig mailConfig;

    @Async
    public void sendMail(String subject, String mailContext, String[] cc, String filePath, String[] recipients, String[] variables) {
        log.trace("The mail context is {} ...", mailContext);
        String mailFileName = mailContext.equals("reset") ? "agency_banking_reset.txt" : "agency_banking_agent.txt";
        try {
            String messageBody = mailBody(env.getProperty("app.email.baseFolder"), mailContext, mailFileName, variables);
            MimeMessage mimeMessage = new MimeMessage(mailConfig.getSession());
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(recipients);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(env.getProperty("app.mail.sender"));
            mimeMessageHelper.setText(messageBody, true);
            mimeMessageHelper.setCc(cc);
            
            if (null != filePath) {
                // attach the error file to the mail
                log.info("Attaching file from path {} ...", filePath);
                FileSystemResource file = new FileSystemResource(filePath);
                mimeMessageHelper.addAttachment(file.getFilename(), file);
            }

            Transport.send(mimeMessage);
            log.info("mail successfully sent to the recipients {}", Arrays.toString(recipients));
        } catch (MessagingException e) {
            log.error("An exception occured in mail configuration", e);
        }
    }

    private String mailBody(String filePath, String mailContext, String fileName, String[] placeHolders) {
        Map valuesMap = new HashMap();
        valuesMap.put("institution", placeHolders[0]);
        if (mailContext.equalsIgnoreCase("reset")) {
            log.trace("Replacing values for successful transactions ...");
            valuesMap.put("aesKey", placeHolders[1]);
            valuesMap.put("ivKey", placeHolders[2]);
            valuesMap.put("password", placeHolders[3]);
        }

        Path path = Paths.get(filePath, fileName);
        try {
            String body = new String(Files.readAllBytes(path));
            log.trace("About to start replacing variables ...");
            StrSubstitutor sub = new StrSubstitutor(valuesMap);
            String resolvedString = sub.replace(body);
            log.trace("String successfully resolved ...");
            return resolvedString;
        } catch (IOException ex) {
            log.error("Unable to locate file {}, please try again", path.toAbsolutePath().toString(), ex);
        }
        return null;
    }
}

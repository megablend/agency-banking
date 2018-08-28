/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import javax.mail.Session;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 *
 * @author cmegafu
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MailConfigImp implements MailConfig {

    @Autowired
    Environment env;

    @Override
    public Session getSession() {
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(env.getProperty("app.mail.reply.name"), env.getProperty("app.mail.reply.name.password"));
            }
        };
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", env.getProperty("app.email.host"));
        properties.setProperty("mail.smtp.port", env.getProperty("app.mail.port"));
        properties.setProperty("mail.smtp.auth", env.getProperty("app.mail.auth.flag"));
        properties.setProperty("mail.smtp.from", env.getProperty("app.mail.from"));
        properties.setProperty("mail.smtp.starttls.enable", env.getProperty("app.enable.mail.startls"));

        Session session = Session.getInstance(properties, auth);
        return session;
    }

}

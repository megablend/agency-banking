/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.configurations;

import com.upl.nibss.bvn.model.AgentManager;
import com.upl.nibss.bvn.repo.AgentManagerRepo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author cmegafu
 */
//@Configuration
//@EnableCaching
//@EnableScheduling
//@EnableConfigurationProperties(ApplicationProperties.class)
//@EnableJpaRepositories(basePackageClasses = AgentManagerRepo.class, entityManagerFactoryRef = "emf", transactionManagerRef ="transactionManager" )
public class AppConfig {
    
//    @Value("${app.dataSource}") 
//    private String appDbConfig;
//    
//    @Primary
//    @Bean(destroyMethod = "close") 
//    public DataSource abDataSource() {
//        HikariConfig config = new HikariConfig(appDbConfig);
//        config.setPoolName("AARSHikaripool-1");
//        return new HikariDataSource(config);
//    }
//    
//    @Bean
//    public EntityManagerFactory emf() {
//        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
//        vendor.setGenerateDdl(true);
//        //vendor.setShowSql(true);
//        vendor.getJpaPropertyMap().put("hibernate.", "update");
//        
//        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
//        bean.setDataSource(abDataSource());
//        bean.setPackagesToScan(AgentManager.class.getPackage().getName());
//        bean.setJpaVendorAdapter(vendor);
//        bean.afterPropertiesSet();
//        
//        return bean.getObject();
//    }
//    
//    @Bean
//    public JpaTransactionManager transactionManager() {
//        return new JpaTransactionManager(emf());
//    }
}

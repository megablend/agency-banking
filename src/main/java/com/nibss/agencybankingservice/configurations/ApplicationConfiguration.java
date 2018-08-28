package com.nibss.agencybankingservice.configurations;

import com.nibss.agencybankingservice.filters.SecurityFilter;
import com.nibss.cryptography.AESKeyGenerator;
import com.upl.nibss.bvn.repo.InstitutionService;
import com.upl.nibss.bvn.service.AgentMgrService;
import com.upl.nibss.bvn.service.UserService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableTransactionManagement
@EnableCaching
@EnableAsync
@ComponentScan({"com.upl.nibss.bvn"})
public class ApplicationConfiguration {

    @Bean
    public FilterRegistrationBean aesFilterBean(AgentMgrService agentManagerService, InstitutionService institutionService, UserService userService,
                                                ApplicationProperties applicationProperties) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        SecurityFilter filter = new SecurityFilter(agentManagerService, institutionService, userService, applicationProperties.getEncryptionSalt());
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.addUrlPatterns("/api/*");
        filterRegistrationBean.setName("aesFilter");
        filterRegistrationBean.setOrder(Integer.MAX_VALUE);

        return filterRegistrationBean;
    }



    @Bean
    public AESKeyGenerator aesKeyGenerator() {
        return  new AESKeyGenerator();
    }

}

package com.example.demo.apps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author fathyaff
 * @date 15/08/21 01.58
 */
@Import({H2JPAConfig.class})
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class ControllerIntegrationTestConfig {

}

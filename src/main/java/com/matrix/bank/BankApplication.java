package com.matrix.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  // JPA Auditing 활성화
@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BankApplication.class, args);
//        String[] iocNames = context.getBeanDefinitionNames();
//        for (String name : iocNames) {
//            System.out.println(name);
//        }
    }
}

package com.sherwin.fitnessPal.messaging;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.sherwin.fitnessPal.messaging.domain.ChatInput;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@Configuration
@ComponentScan
@EnableCaching
@EnableAutoConfiguration
@EnableScheduling
public class MessagingApp extends SpringBootServletInitializer {
    private static Class<MessagingApp> applicationClass = MessagingApp.class;

    public static void main(String[] args) {
        SpringApplication.run(applicationClass, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    @Bean
    HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    Map<Long, ChatInput> messageIdMap() {
        Map<Long, ChatInput> map = hazelcastInstance().getMap("messageId");

        return map;
    }

    @Bean
    Map<String, ChatInput> messageUserNameMap() {
        Map<String, ChatInput> map = hazelcastInstance().getMap("messageUserName");

        return map;
    }

    @Bean
    FlakeIdGenerator keyGenerator() {
        return hazelcastInstance().getFlakeIdGenerator("message");
    }
}

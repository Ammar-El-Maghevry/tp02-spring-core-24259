package com.ioc.springcoredemo.config;
import com.ioc.springcoredemo.common.Coach;
import com.ioc.springcoredemo.common.SwimCoach;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SportConfig {
 @Bean("aquatic") public Coach swimCoach() { return new SwimCoach(); }
}

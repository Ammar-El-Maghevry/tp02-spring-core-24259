package com.ioc.springcoredemo.common;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
@Component
public class CricketCoach implements Coach {
 public CricketCoach() { System.out.println("In constructor: " + getClass().getSimpleName()); }
 @PostConstruct public void doMyStartupStuff() { System.out.println("In doMyStartupStuff()"); }
 @PreDestroy public void doMyCleanupStuff() { System.out.println("In doMyCleanupStuff()"); }
 @Override public String getDailyWorkout() { return "Practice fast bowling for 15 minutes"; }
}

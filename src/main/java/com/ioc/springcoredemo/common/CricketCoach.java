package com.ioc.springcoredemo.common;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CricketCoach implements Coach {
 public CricketCoach() { System.out.println("In constructor: " + getClass().getSimpleName()); }
 @Override public String getDailyWorkout() { return "Practice fast bowling for 15 minutes"; }
}

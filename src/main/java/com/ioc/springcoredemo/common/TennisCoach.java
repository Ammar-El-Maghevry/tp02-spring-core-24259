package com.ioc.springcoredemo.common;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;
@Component
@Lazy
public class TennisCoach implements Coach {
 public TennisCoach() { System.out.println("In constructor: " + getClass().getSimpleName()); }
 @Override public String getDailyWorkout() { return "Practice your backhand volley"; }
}

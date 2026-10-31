package com.ioc.springcoredemo.common;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
@Component
@Primary
public class TrackCoach implements Coach {
 @Override public String getDailyWorkout() { return "Run a hard 5k!"; }
}

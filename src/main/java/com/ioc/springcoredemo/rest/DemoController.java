package com.ioc.springcoredemo.rest;
import com.ioc.springcoredemo.common.Coach;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class DemoController {
 private final Coach myCoach;
 public DemoController(@Qualifier("aquatic") Coach theCoach) { this.myCoach = theCoach;  }
 @GetMapping("/dailyworkout") public String getDailyWorkout() { return myCoach.getDailyWorkout(); }
}

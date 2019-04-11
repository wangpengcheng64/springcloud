package com.wpc.cloud.provider.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class HealthChecker implements HealthIndicator {

  private boolean up = true;

  @Override
  public Health health() {
    if (up) {
      return new Health.Builder(Status.UP).build();
    } else {
      return new Health.Builder(Status.DOWN).build();
    }

  }

  public boolean isUp() {
    return up;
  }

  public void setUp(boolean up) {
    this.up = up;
  }

}

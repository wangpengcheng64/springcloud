package com.wpc.cloud.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HystrixIndexController {

  /**
   * http://localhost:10003/hystrix改成http://localhost:10003
   * @return
   */
  @GetMapping("")
  public String index() {
    return "forward:/hystrix";
  }

}
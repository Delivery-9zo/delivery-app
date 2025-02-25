package com.sparta.deliveryapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MainController {

  @GetMapping("/")
  public RedirectView root() {
    return new RedirectView("/swagger-ui/index.html");
  }
}

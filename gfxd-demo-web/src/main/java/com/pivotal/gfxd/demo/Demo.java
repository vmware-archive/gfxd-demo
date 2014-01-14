package com.pivotal.gfxd.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Jens Deppe
 */
@Controller
@RequestMapping("/data")
public class Demo {
  @RequestMapping(value = "/foo", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity provisionServiceInstance() {
    return new ResponseEntity("Hello World", HttpStatus.OK);
  }
}

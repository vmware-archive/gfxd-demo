package com.pivotal.gfxd.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.logging.Logger;

/**
 * @author Jens Deppe
 */
@Controller
@RequestMapping("/data")
public class StatsController {

  private static final Logger LOG = Logger.getLogger(
      StatsController.class.getName());

  @RequestMapping(value = "/foo", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity provisionServiceInstance() {
    LOG.info("--->>>> StatsController");

    return new ResponseEntity("Hello World", HttpStatus.OK);
  }
}

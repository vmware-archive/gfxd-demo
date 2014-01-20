package com.pivotal.gfxd.demo.controller;

import com.pivotal.gfxd.demo.entity.TimestampValue;
import com.pivotal.gfxd.demo.loader.ILoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * @author Jens Deppe
 */
@Controller
@RequestMapping("/data")
public class DemoController {

  private static final Logger LOG = Logger.getLogger(
      DemoController.class.getName());

  @Autowired
  ILoader loader;

  @RequestMapping(value = "/events-loaded", produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<TimestampValue> getEventsLoaded(HttpSession session) {
    TimestampValue lastCall = (TimestampValue) session.getAttribute("last-call");
    if (lastCall == null) {
      lastCall = new TimestampValue(0, 0);
    }
    long now = System.currentTimeMillis();
    long rows = loader.getRowsInserted();
    float rate = ((rows - lastCall.getValue()) / (now - lastCall.getTimestamp())) * 1000;

    TimestampValue tv = new TimestampValue(now / 1000, rate);

    session.setAttribute("last-call", new TimestampValue(now, rows));

    System.out.println(
        "now=" + now + " last=" + lastCall.getTimestamp() + " lastValue=" + lastCall.getValue() + " rows " + rows);
    System.out.println(tv);

    return new ResponseEntity(tv, HttpStatus.OK);
  }

  @RequestMapping(value = "/random", produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<TimestampValue> getRandom(HttpSession session) {
    float rate = (float) ((Math.random() - 0.5) * 1000 + 5000);

    TimestampValue tv = new TimestampValue(System.currentTimeMillis() / 1000, rate);

    System.out.println(tv);

    return new ResponseEntity(tv, HttpStatus.OK);
  }
}

package com.pivotal.gfxd.demo.controller;

import com.pivotal.gfxd.demo.TimeSlice;
import com.pivotal.gfxd.demo.entity.TimestampValue;
import com.pivotal.gfxd.demo.loader.ILoader;
import com.pivotal.gfxd.demo.prediction.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

  @Autowired
  PredictionService predictionSvc;

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

    return new ResponseEntity(tv, HttpStatus.OK);
  }

  @RequestMapping(value = "/random", produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<TimestampValue> getRandom(HttpSession session) {
    float rate = (float) ((Math.random() - 0.5) * 1000 + 5000);

    TimestampValue tv = new TimestampValue(System.currentTimeMillis() / 1000, rate);

    return new ResponseEntity(tv, HttpStatus.OK);
  }

  @RequestMapping(value = "/predicted-load", produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<TimestampValue> getPrediction(HttpSession session,
      @RequestParam(value="delta", required=false) Integer delta) {
    long now = System.currentTimeMillis() / 1000;
    if (delta != null) {
      now -= delta;
    }

    float predictedLoad = predictionSvc.predictedLoad(now ,
        TimeSlice.Interval.FIVE_MINUTE);

    TimestampValue tv = new TimestampValue(now, predictedLoad);

    return new ResponseEntity(tv, HttpStatus.OK);
  }
}

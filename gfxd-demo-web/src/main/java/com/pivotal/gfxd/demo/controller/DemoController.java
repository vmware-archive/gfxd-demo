package com.pivotal.gfxd.demo.controller;

import com.pivotal.gfxd.demo.TimeSlice;
import com.pivotal.gfxd.demo.entity.TimestampValue;
import com.pivotal.gfxd.demo.loader.ILoader;
import com.pivotal.gfxd.demo.services.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author Jens Deppe
 */
@Controller
@RequestMapping("/data")
public class DemoController {

  private static final Logger LOG = LoggerFactory.getLogger(
      DemoController.class.getName());

  @Autowired
  ILoader loader;

  @Autowired
  PredictionService predictionSvc;

  private float lastCurrentLoad = 0;

  @RequestMapping(value = "/events-loaded",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<TimestampValue> getEventsLoaded(HttpSession session) {
    TimestampValue lastCall = (TimestampValue) session.getAttribute("last-call");
    if (lastCall == null) {
      lastCall = new TimestampValue("event", 0, 0);
    }
    long now = System.currentTimeMillis();
    long rows = loader.getRowsInserted();
    float rate = ((rows - lastCall.getValue("event")) /
        (now - lastCall.getTimestamp())) * 1000;

    TimestampValue tv = new TimestampValue("event", now / 1000, rate);

    session.setAttribute("last-call", new TimestampValue("event", now, rows));

    return new ResponseEntity(tv, HttpStatus.OK);
  }

  @RequestMapping(value = "/random",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<TimestampValue> getRandom() {
    float rate = (float) ((Math.random() - 0.5) * 1000 + 5000);

    TimestampValue tv = new TimestampValue("event", System.currentTimeMillis() / 1000, rate);

    return new ResponseEntity(tv, HttpStatus.OK);
  }

  @RequestMapping(value = "/predicted-load",
      produces = "application/json",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<TimestampValue> getPrediction() {
    long start = System.currentTimeMillis();
    long startSeconds = start / 1000;

    float predictedLoad =  predictionSvc.predictedLoad(startSeconds,
        TimeSlice.Interval.FIVE_MINUTE);
    LOG.debug("Prediction took " + (System.currentTimeMillis() - start) +
        "ms + " + predictedLoad + " for time " + startSeconds);

    start = System.currentTimeMillis();
    float currentLoad = predictionSvc.currentLoad(startSeconds,
        TimeSlice.Interval.FIVE_MINUTE);
    LOG.debug("Current load query took " + (System.currentTimeMillis() - start) +
        "ms + " + currentLoad + " for time " + startSeconds);

    /**
     * This is a hack as sometimes our query will exceed what we have in memory.
     * This can easily happen during the few seconds as we transition between
     * time slices.
     */
    if (Float.isNaN(currentLoad)) {
      currentLoad = lastCurrentLoad;
    } else {
      lastCurrentLoad = currentLoad;
    }

    /**
     * Respond with the current timestamp even though the data is calculated
     * from past data. Seeing that the client sent the delta, it can always
     * re-adjust if it wants to.
     */
    TimestampValue tv = new TimestampValue(startSeconds);
    tv.add("predict", predictedLoad);
    tv.add("current", currentLoad);

    return new ResponseEntity(tv, HttpStatus.OK);
  }

  // Yes, yes - this isn't very RESTful :P
  @RequestMapping(value = "/disturbance",
      method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity setDisturbance(
      @RequestParam(value="value") Integer disturbance) {
    loader.setDisturbance(disturbance / 100F);
    return new ResponseEntity("", HttpStatus.OK);
  }
}

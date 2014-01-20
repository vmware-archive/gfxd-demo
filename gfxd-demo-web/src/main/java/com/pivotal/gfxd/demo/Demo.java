package com.pivotal.gfxd.demo;

import com.pivotal.gfxd.demo.loader.LoadRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

/**
 * @author Jens Deppe
 */
@Component
public class Demo {

  private static final Logger LOG = Logger.getLogger(Demo.class.getName());

  @Autowired
  private LoadRunner runner;

  @PostConstruct
  private void run() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        runner.run(System.getProperty("loadFile"));
      }
    });
    t.start();
  }
}

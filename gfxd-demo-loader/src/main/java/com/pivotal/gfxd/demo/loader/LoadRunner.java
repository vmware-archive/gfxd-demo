package com.pivotal.gfxd.demo.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("loadRunner")
public class LoadRunner {

  private static Logger logger = LoggerFactory
      .getLogger(LoadRunner.class.getCanonicalName());

  @Autowired
  ILoader loader;

  @Autowired
  Executor executor;
  
  @Autowired
  PropertiesConfiguration propConfiguration;

  private int pauseTime = 0;

  private long boostTime = 0;

  // This is the time (in seconds) by which the event stream timestamp needs to
  // be adjusted to make it look like it is happening now.
  private long deltaAdjust = 0;

  // The timestamp of the very first row that is read. This value is in seconds.
  private long startTime = 0;

  private int maxHouseId = Integer.MAX_VALUE;

  public LoadRunner() {
  }

  @PostConstruct
  public void init() {
    pauseTime = propConfiguration.getInt("thread.pause", 0);
    boostTime = propConfiguration.getInt("demo.boost", 0);
    maxHouseId = propConfiguration.getInt("demo.houses", Integer.MAX_VALUE);
  }

  public void asyncInsertBatch(final List<String[]> lines, final long timestamp) {
    
    executor.execute( new Runnable() {
      @Override
      public void run() {
        loader.insertBatch(lines, timestamp);
        if (startTime + deltaAdjust + boostTime < timestamp) {
          pause();
        }
      }
    });
    
  }

  @SuppressWarnings("static-access")
  private void pause() {
    try {
      Thread.currentThread().sleep(pauseTime);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void run(final String CSV_FILE) {

    List<String[]> lines = new LinkedList<>();
    String timestamp = null;

    try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE),
        StandardCharsets.UTF_8)) {

      // Lines are in this form:
      // id, timestamp, value, property, plug_id, household_id, house_id

      for (String line = null; (line = br.readLine()) != null;) {
        String[] split = line.split(",");

        int houseId = Integer.parseInt(split[6]);
        if (houseId >= maxHouseId) {
          continue;
        }

        // We only care about 'load' values at the moment
        int property = Integer.parseInt(split[3]);
        if (property != 1) {
          continue;
        }

        // first iteration
        if (timestamp == null) {
          timestamp = split[1];
          lines.add(split);
          startTime = Long.parseLong(timestamp);
          // The stream of events need to appear as though they started
          // 'boostTime' seconds ago.
          deltaAdjust = (System.currentTimeMillis() / 1000) - startTime - boostTime;

        } else {
          // batch same timestamp objects
          if (timestamp.equals(split[1])) {
            lines.add(split);

          } else {

            // send batched records and create a new batch
            this.asyncInsertBatch(lines, Long.parseLong(timestamp) + deltaAdjust);
            lines = new LinkedList<>();
            lines.add(split);
            timestamp = split[1];
          }
        }
      }
      // insert last pending batch
      this.asyncInsertBatch(lines, Long.parseLong(timestamp) + deltaAdjust);

    } catch (IOException ioex) {
      logger.error("An error occurred during batch insertion or the file was not accessible. Message: "
          + ioex.getMessage());
    }

  }

  public static void main(String[] args) {

    final String CSV_FILE = args[0];

    long startTime = System.currentTimeMillis();
    try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        "classpath:context.xml")) {

      LoadRunner runner = (LoadRunner) context.getBean("loadRunner");

      runner.run(CSV_FILE);

    }
    long endTime = System.currentTimeMillis();
    logger.info("Total execution time: " + (endTime - startTime) + "ms");
  }

}

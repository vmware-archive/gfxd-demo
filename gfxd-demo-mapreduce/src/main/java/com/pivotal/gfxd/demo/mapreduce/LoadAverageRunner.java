package com.pivotal.gfxd.demo.mapreduce;

import com.pivotal.gfxd.demo.mapreduce.LoadAverage;
import org.apache.hadoop.util.Tool;
import org.springframework.stereotype.Component;

/**
 * @author William Markito
 */
@Component
public class LoadAverageRunner {

  public int run(final String namenode) {
    Tool loadAvgTool = new LoadAverage();

    try {

      int rc = loadAvgTool.run(new String[] {namenode});
      System.out.println("Return code" +  rc);

      return rc;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return -1;
  }

}

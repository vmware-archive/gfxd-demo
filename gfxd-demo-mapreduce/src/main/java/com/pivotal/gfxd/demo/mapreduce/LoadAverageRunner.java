package com.pivotal.gfxd.demo.mapreduce;

import com.pivotal.gfxd.demo.mapreduce.LoadAverage;
import org.springframework.stereotype.Component;

/**
 * Created by markito on 2/18/14.
 */
@Component
public class LoadAverageRunner {

  public static void run() {
    LoadAverage la = new LoadAverage();

    String [] args = new String[5];

    try {
      System.out.println("Return code" +  la.run(args));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}

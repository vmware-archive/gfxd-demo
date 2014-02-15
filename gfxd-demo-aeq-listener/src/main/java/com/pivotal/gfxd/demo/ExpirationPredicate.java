package com.pivotal.gfxd.demo;

/**
 * @author Jens Deppe
 */
public class ExpirationPredicate {

  /**
   * Function to determine if a timestamp has expired, given a certain age. This
   * is used to calculate the expiration criteria for the raw_sensor data.
   * @param timestamp the timestamp (in seconds) to test
   * @param age age of the timestamp
   * @return 0 for false, 1 for true
   */
  public static int expired(long timestamp, int age) {
    long now = System.currentTimeMillis() / 1000;
    return (now - age > timestamp) ? 1 : 0;
  }
}

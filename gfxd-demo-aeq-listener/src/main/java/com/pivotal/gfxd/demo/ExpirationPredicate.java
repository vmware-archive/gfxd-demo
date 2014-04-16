/*==========================================================================
 * Copyright (c) 2014 Pivotal Software Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to
 * the terms and conditions of the subcomponent's license, as noted in the
 * LICENSE file.
 *==========================================================================
 */

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

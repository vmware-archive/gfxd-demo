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

package com.pivotal.gfxd.demo.loader;

import java.util.List;

/**
 * @author William Markito
 */
public interface ILoader {

  /**
   * Insert a batch of sensor data, read from a CSV file.
   * @param lines
   * @param timestamp the timestamp (seconds) associated with this batch
   */
  public void insertBatch(final List<String[]> lines, long timestamp);

  /**
   * Return the total number of rows inserted so far.
   * @return total rows inserted
   */
  public long getRowsInserted();

  /**
   * Inject some amount of disturbance into the values being inserted. This
   * allows the UI to alter the profile of data coming in.
   * @param disturbance a floating point value
   */
  public void setDisturbance(float disturbance);
}

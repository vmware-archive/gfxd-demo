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

package com.pivotal.gfxd.demo.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a timestamp, value tuple. Typically serialized into JSON.
 *
 * @author Jens Deppe
 */
public class TimestampValue {

  private long timestamp;
  private Map<String, Float> values = new HashMap<>();


  public TimestampValue(String name, long timestamp, float value) {
    this.timestamp = timestamp;
    add(name, value);
  }

  public TimestampValue(long timestamp) {
    this.timestamp = timestamp;
  }

  public TimestampValue add(String name, float value) {
    values.put(name, value);
    return this;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public Float getValue(String name) {
    return values.get(name);
  }

  public Map<String, Float> getValues() {
    return values;
  }
}

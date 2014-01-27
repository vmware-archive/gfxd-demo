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

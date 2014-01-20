package com.pivotal.gfxd.demo.entity;

/**
 * Represents a timestamp, value tuple. Typically serialized into JSON.
 *
 * @author Jens Deppe
 */
public class TimestampValue {

  private long timestamp;

  private float value;

  public TimestampValue(long timestamp, float value) {
    this.timestamp = timestamp;
    this.value = value;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public float getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "{ timestamp: " + timestamp + ", value: " + value + "}";
  }
}

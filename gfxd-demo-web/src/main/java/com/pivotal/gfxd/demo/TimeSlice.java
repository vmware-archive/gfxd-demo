package com.pivotal.gfxd.demo;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Enum which represents X-minute slices of time in 24 hours.
 *
 * @author Jens Deppe
 */
public class TimeSlice {

  private static final int MINUTES_PER_INTERVAL = 5;

  public enum Interval {

    FIVE_MINUTE(288),

    TEN_MINUTE(144),

    THIRTY_MINUTE(48),

    SIXTY_MINUTE(24),

    ONE_TWENTY_MINUTE(12);

    private int slices;

    Interval(int segments) {
      this.slices = segments;
    }

    /**
     * How many of this type of segment in 24 hours
     *
     * @return
     */
    public int getSegments() {
      return slices;
    }

    /**
     * How many 5-minute intervals in this slice
     *
     * @return
     */
    public int getIntervals() {
      return 288 / slices;
    }
  }

  private Calendar clampedStamp;

  private int intervalStart;

  private Interval interval;

  /**
   * Constructor
   *
   * @param timestamp timestamp in seconds
   * @param interval  the type of interval
   */
  public TimeSlice(long timestamp, Interval interval) {
    clampedStamp = GregorianCalendar.getInstance();
    clampedStamp.setTimeInMillis(timestamp * 1000);
    processStamp(clampedStamp, interval);
  }

  public TimeSlice(Calendar clampedCal, Interval interval) {
    this.clampedStamp = clampedCal;
    this.interval = interval;
    processStamp(clampedStamp, interval);
  }

  private void processStamp(Calendar cal, Interval interval) {
    int timeSlice = (
        clampedStamp.get(Calendar.HOUR_OF_DAY) * 60 +
            clampedStamp.get(Calendar.MINUTE)) /
        (MINUTES_PER_INTERVAL * interval.getIntervals());

    this.intervalStart = timeSlice * interval.getIntervals();
    this.interval = interval;

    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    int minutesIntoDay = intervalStart * MINUTES_PER_INTERVAL;
    cal.set(Calendar.HOUR_OF_DAY, minutesIntoDay / 60);
    cal.set(Calendar.MINUTE, minutesIntoDay % 60);
  }

  public int getIntervalStart() {
    return intervalStart;
  }

  /**
   * Return the end interval. This value is not inclusive.
   *
   * @return
   */
  public int getIntervalEnd() {
    return intervalStart + interval.getIntervals();
  }

  public int getWeekday() {
    return clampedStamp.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Add or subtract a time interval from this TimeSlice, producing a new slice.
   *
   * @param delta the interval to add or remove. The unit of this parameter is
   *              determined by the type of interval. When dealing with a
   *              FIVE_MINUTE interval, one unit would be 5 minutes. When
   *              dealing with a THIRTY_MINUTE interval, one unit would be 30
   *              minutes.
   * @return a new TimeSlice
   */
  public TimeSlice shift(int delta) {
    Calendar newStamp = (Calendar) clampedStamp.clone();
    newStamp.add(Calendar.MINUTE, delta * MINUTES_PER_INTERVAL *
        interval.getIntervals());
    return new TimeSlice(newStamp, interval);
  }

  protected Calendar getStamp() {
    return clampedStamp;
  }
}

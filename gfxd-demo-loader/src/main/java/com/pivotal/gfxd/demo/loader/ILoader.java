package com.pivotal.gfxd.demo.loader;

import java.util.List;

/**
 * @author William Markito
 */
public interface ILoader {

  /**
   * Insert a batch of sensor data, read from a CSV file.
   * @param lines
   * @return the timestamp of the first row in the batch
   */
	public long insertBatch(final List<String[]> lines);

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

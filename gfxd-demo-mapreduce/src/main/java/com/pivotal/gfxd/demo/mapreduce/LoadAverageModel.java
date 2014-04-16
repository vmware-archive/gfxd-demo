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

package com.pivotal.gfxd.demo.mapreduce;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author William Markito
 */
public class LoadAverageModel implements Writable {
  private int house_id;
  private int household_id;
  private int plug_id;
  private int weekday;
  private int time_slice;
  private double total_load;
  private int event_count;

  public LoadAverageModel() {
  }

  public LoadAverageModel(int house_id, int household_id, int plug_id,
      int weekday, int time_slice, double total_load, int event_count) {
    this.house_id = house_id;
    this.household_id = household_id;
    this.plug_id = plug_id;
    this.weekday = weekday;
    this.time_slice = time_slice;
    this.total_load = total_load;
    this.event_count = event_count;
  }

  public int getHouse_id() {
    return house_id;
  }

  public void setHouse_id(int idx, PreparedStatement ps) throws SQLException {
    ps.setInt(idx, house_id);
  }

  public int getHousehold_id() {
    return household_id;
  }

  public void setHousehold_id(int idx,
      PreparedStatement ps) throws SQLException {
    ps.setInt(idx, household_id);
  }

  public int getPlug_id() {
    return plug_id;
  }

  public void setPlug_id(int idx, PreparedStatement ps) throws SQLException {
    ps.setInt(idx, plug_id);
  }

  public int getWeekday() {
    return weekday;
  }

  public void setWeekday(int idx, PreparedStatement ps) throws SQLException {
    ps.setInt(idx, weekday);
  }

  public int getTime_slice() {
    return time_slice;
  }

  public void setTime_slice(int idx, PreparedStatement ps) throws SQLException {
    ps.setInt(idx, time_slice);
  }

  public double getTotal_load() {
    return total_load;
  }

  public void setTotal_load(int idx, PreparedStatement ps) throws SQLException {
    ps.setDouble(idx, total_load);
  }

  public int getEvent_count() {
    return event_count;
  }

  public void setEvent_count(int idx,
      PreparedStatement ps) throws SQLException {
    ps.setInt(idx, event_count);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(house_id);
    out.writeInt(household_id);
    out.writeInt(plug_id);
    out.writeInt(weekday);
    out.writeInt(time_slice);
    out.writeDouble(total_load);
    out.writeInt(event_count);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    house_id = in.readInt();
    household_id = in.readInt();
    plug_id = in.readInt();
    weekday = in.readInt();
    time_slice = in.readInt();
    total_load = in.readDouble();
    event_count = in.readInt();
  }
}

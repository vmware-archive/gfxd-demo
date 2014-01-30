package com.pivotal.gfxd.demo.mapreduce;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by markito on 1/27/14.
 */
public class LoadKey implements Writable {
    private int household_id;
    private int plug_id;
    private int house_id;
    private double value;
    private int event_count;


    public LoadKey() {

    }

    public LoadKey(int household_id, int plug_id, int house_id, int weekday, int time_slice, double value, int event_count) {
        this.household_id = household_id;
        this.plug_id = plug_id;
        this.house_id = house_id;
        this.weekday = weekday;
        this.time_slice = time_slice;
        this.setValue(value);
        this.setEvent_count(event_count);
    }

    public int getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(int household_id) {
        this.household_id = household_id;
    }

    public int getPlug_id() {
        return plug_id;
    }

    public void setPlug_id(int plug_id) {
        this.plug_id = plug_id;
    }

    public int getHouse_id() {
        return house_id;
    }

    public void setHouse_id(int house_id) {
        this.house_id = house_id;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getTime_slice() {
        return time_slice;
    }

    public void setTime_slice(int time_slice) {
        this.time_slice = time_slice;
    }

    private int weekday;
    private int time_slice;

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(household_id);
        out.writeInt(plug_id);
        out.writeInt(house_id);
        out.writeInt(weekday);
        out.writeInt(time_slice);
        out.writeDouble(value);
        out.writeInt(event_count);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
       household_id = in.readInt();
       plug_id= in.readInt();
       house_id = in.readInt();
       weekday = in.readInt();
       time_slice = in.readInt();
       value = in.readDouble();
       event_count = in.readInt();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getEvent_count() {
        return event_count;
    }

    public void setEvent_count(int event_count) {
        this.event_count = event_count;
    }
}

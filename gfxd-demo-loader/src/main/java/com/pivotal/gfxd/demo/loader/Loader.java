package com.pivotal.gfxd.demo.loader;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

@Component("loader")
public class Loader extends JdbcDaoSupport implements ILoader {

  final int MINUTES_PER_INTERVAL = 5; // 5 minute unit slots

  private long rowsInserted;

  @Autowired
  public Loader(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  public long insertBatch(final List<String> lines) {
    String sql = "insert into raw_sensor (id, timestamp, value, property, plug_id, household_id,house_id, weekday, time_slice) values (?,?,?,?,?,?,?,?,?)";
    final Calendar cal = Calendar.getInstance();
    getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps, int i)
          throws SQLException {
        final String line = lines.get(i);
        final String[] split = line.split(",");
        final long timestamp = Long.parseLong(split[1]);
        ps.setLong(1, Long.parseLong(split[0]));
        ps.setLong(2, timestamp);
        ps.setFloat(3, Float.parseFloat(split[2]));
        ps.setInt(4, Integer.parseInt(split[3]));
        ps.setInt(5, Integer.parseInt(split[4]));
        ps.setInt(6, Integer.parseInt(split[5]));
        ps.setInt(7, Integer.parseInt(split[6]));

        cal.setTimeInMillis(timestamp * 1000L);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        int timeSlice = (cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(
            Calendar.MINUTE)) / MINUTES_PER_INTERVAL;

        ps.setInt(8, weekDay); // weekday
        ps.setInt(9, timeSlice); // time_slice
      }

      @Override
      public int getBatchSize() {
        return lines.size();
      }
    });

    System.out.println(
        Thread.currentThread().getId() + " - " + lines.size() + " rows inserted.");

    rowsInserted += lines.size();

    // Return the timestamp of the first row of this batch
    String[] split = lines.get(0).split(",");
    return Long.parseLong(split[1]);
  }

  public long getRowsInserted() {
    return rowsInserted;
  }
}

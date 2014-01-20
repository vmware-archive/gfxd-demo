package com.pivotal.gfxd.demo.loader;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

@Component("loader")
public class Loader extends JdbcDaoSupport implements ILoader {

  private long rowsInserted;

	@Autowired
	public Loader(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	public void insertBatch(final List<String> lines) {
		String sql = "insert into raw_sensor (id, timestamp, value, property, plug_id, household_id,house_id) values (?,?,?,?,?,?,?)";

		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				final String line = lines.get(i);
				final String[] split = line.split(",");

				ps.setLong(1, Long.parseLong(split[0]));
				ps.setLong(2, Long.parseLong(split[1]));
				ps.setFloat(3, Float.parseFloat(split[2]));
				ps.setInt(4, Integer.parseInt(split[3]));
				ps.setInt(5, Integer.parseInt(split[4]));
				ps.setInt(6, Integer.parseInt(split[5]));
				ps.setInt(7, Integer.parseInt(split[6]));
			}

			@Override
			public int getBatchSize() {
				return lines.size();
			}
		});

		System.out.println(Thread.currentThread().getId() + " - " + lines.size() + " rows inserted.");

    rowsInserted += lines.size();
	}

  public long getRowsInserted() {
    return rowsInserted;
  }
}

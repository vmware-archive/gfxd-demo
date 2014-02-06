package com.pivotal.gfxd.demo;

import com.pivotal.gemfirexd.callbacks.AsyncEventListener;
import com.pivotal.gemfirexd.callbacks.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Jens Deppe
 */
public class AggregationListener implements AsyncEventListener {

  private static final Logger LOG = LoggerFactory.getLogger(
      AggregationListener.class.getName());

  private static final String DRIVER = "com.pivotal.gemfirexd.jdbc.ClientDriver";

  private static final String CONN_URL = "jdbc:gemfirexd:";

  private static final String SELECT_SQL = "select * from load_averages where weekday=? and time_slice=? and plug_id=?";

  private static final String INSERT_SQL = "insert into load_averages values (?, ?, ?, ?, ?, ?, ?)";

  private static final String UPDATE_SQL = "update load_averages set total_load=?, event_count=? where weekday=? and time_slice=? and plug_id=?";

  //load driver
  static {
    try {
      Class.forName(DRIVER).newInstance();
    } catch (ClassNotFoundException cnfe) {
      throw new RuntimeException("Unable to load the JDBC driver", cnfe);
    } catch (InstantiationException ie) {
      throw new RuntimeException("Unable to instantiate the JDBC driver", ie);
    } catch (IllegalAccessException iae) {
      throw new RuntimeException("Not allowed to access the JDBC driver", iae);
    }
  }

  private static ThreadLocal<Connection> dConn = new ThreadLocal<Connection>() {
    protected Connection initialValue() {
      return getConnection();
    }
  };

  private static Connection getConnection() {
    Connection conn;
    try {
      conn = DriverManager.getConnection(CONN_URL);
    } catch (SQLException e) {
      throw new IllegalStateException("Unable to create connection", e);
    }
    return conn;
  }

  private static ThreadLocal<Statement> stmt = new ThreadLocal<Statement> () {
    protected Statement initialValue() {
      Statement s;
      try {
        s = dConn.get().createStatement();
      } catch (SQLException se) {
        throw new IllegalStateException("Unable to retrieve statement ", se);
      }
      return s;
    }
  };

  private static ThreadLocal<PreparedStatement> selectStmt = new ThreadLocal<PreparedStatement> () {
    protected PreparedStatement initialValue()  {
      PreparedStatement stmt = null;
      try {
        stmt = dConn.get().prepareStatement(SELECT_SQL);
      } catch (SQLException se) {
        throw new IllegalStateException("Unable to retrieve statement ", se);
      }
      return stmt;
    }
  };

  private static ThreadLocal<PreparedStatement> insertStmt = new ThreadLocal<PreparedStatement> () {
    protected PreparedStatement initialValue()  {
      PreparedStatement stmt = null;
      try {
        stmt = dConn.get().prepareStatement(INSERT_SQL);
      } catch (SQLException se) {
        throw new IllegalStateException("Unable to retrieve statement ", se);
      }
      return stmt;
    }
  };

  private static ThreadLocal<PreparedStatement> updateStmt = new ThreadLocal<PreparedStatement> () {
    protected PreparedStatement initialValue()  {
      PreparedStatement stmt = null;
      try {
        stmt = dConn.get().prepareStatement(UPDATE_SQL);
      } catch (SQLException se) {
        throw new IllegalStateException("Unable to retrieve statement ", se);
      }
      return stmt;
    }
  };

  @Override
  public boolean processEvents(List<Event> events) {
    for (Event e : events) {
      if (e.getType() == Event.Type.AFTER_INSERT) {
        ResultSet eventRS = e.getNewRowsAsResultSet();
        try {
          if (eventRS.getInt("property") != 1) {
            continue;
          }
          PreparedStatement s = selectStmt.get();
          s.setInt(1, eventRS.getInt("weekday"));
          s.setInt(2, eventRS.getInt("time_slice"));
          s.setInt(3, eventRS.getInt("plug_id"));
          ResultSet queryRS = s.executeQuery();

          if (queryRS.next()) {
            PreparedStatement update = updateStmt.get();
            update.setFloat(1,
                queryRS.getFloat("total_load") + eventRS.getFloat("value"));
            update.setInt(2, queryRS.getInt("event_count") + 1);
            update.setInt(3, queryRS.getInt("weekday"));
            update.setInt(4, queryRS.getInt("time_slice"));
            update.setInt(5, queryRS.getInt("plug_id"));
            update.executeUpdate();
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }
    return true;
  }

  @Override
  public void close() {
  }

  @Override
  public void init(String s) {
  }

  @Override
  public void start() {
  }
}

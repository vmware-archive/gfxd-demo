package com.pivotal.gfxd.demo.mapreduce;

import com.vmware.sqlfire.hadoop.mapreduce.Key;
import com.vmware.sqlfire.hadoop.mapreduce.Row;
import com.vmware.sqlfire.hadoop.mapreduce.RowInputFormat;
import com.vmware.sqlfire.hadoop.mapreduce.RowOutputFormat;
import com.vmware.sqlfire.internal.engine.SqlfDataSerializable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author William Markito
 */
public class LoadAverage extends Configured implements Tool {

    public static class LoadAverageMapper extends Mapper<Object, Row, Text, LoadKey> {

        public void map(Object key, Row row, Context context) throws IOException, InterruptedException {
            try {

                ResultSet rs = row.getRowAsResultSet();

                LoadKey out = new LoadKey();

                out.setHousehold_id(rs.getInt("household_id"));
                out.setHouse_id(rs.getInt("house_id"));
                out.setWeekday(rs.getInt("weekday"));
                out.setPlug_id(rs.getInt("plug_id"));
                out.setTime_slice(rs.getInt("time_slice"));
                out.setValue(rs.getDouble("value"));
                out.setEvent_count(1);

                LoadAverageModel outModel = new LoadAverageModel(rs.getInt("house_id"), rs.getInt("household_id"),
                        rs.getInt("plug_id"), rs.getInt("weekday"), rs.getInt("time_slice"), rs.getDouble("value"),
                        1);

                StringBuilder sb = new StringBuilder();

                Text outKey = new Text();

                sb.append(outModel.getTime_slice());
                sb.append(outModel.getHouse_id());
                sb.append(outModel.getWeekday());
                sb.append(outModel.getPlug_id());
                sb.append(outModel.getHousehold_id());

                outKey.set(sb.toString());
                context.write(outKey, out);

            } catch (SQLException sqex) {
                sqex.printStackTrace();
            }

        }
    }

    public static class LoadAverageReducer extends
            Reducer<Text, LoadKey, Key, LoadAverageModel> {

        public void reduce(Text key, Iterable<LoadKey> values,
                           Context context) throws IOException, InterruptedException {

            double valueSum = 0;
            int numEvents = 0;
            LoadKey loadKey = null;
            for (LoadKey model : values) {
                valueSum = model.getValue() + valueSum;
                numEvents = model.getEvent_count() + numEvents;

                if (loadKey == null) {
                    loadKey = model;
                }
            }
//
            LoadAverageModel result = new LoadAverageModel(loadKey.getHouse_id(), loadKey.getHousehold_id(),
                    loadKey.getPlug_id(), loadKey.getWeekday(), loadKey.getTime_slice(), valueSum,
                    numEvents);

            context.write(new Key(), result);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        SqlfDataSerializable.initTypes();
        Configuration conf = getConf();

        Path outputPath = new Path("/output");
        //Path intermediateOutputPath = new Path(args[0] + "_int");
        String hdfsHomeDir = "/sensorStore"; //args[1];
        String tableName = "RAW_SENSOR";
        String outTableName = "LOAD_AVERAGES";
        String gfxdURL = conf.get("sqlfire.url", "jdbc:sqlfire://localhost:1527");

        outputPath.getFileSystem(conf).delete(outputPath, true);

        conf.set(RowInputFormat.HOME_DIR, hdfsHomeDir);
        conf.set(RowInputFormat.INPUT_TABLE, tableName);
        conf.setBoolean(RowInputFormat.CHECKPOINT_MODE, true);

        conf.set(RowOutputFormat.OUTPUT_URL, gfxdURL);
        conf.set(RowOutputFormat.OUTPUT_TABLE, outTableName);

        // print config to troubleshoot possible issues
        Configuration.dumpConfiguration(conf, new PrintWriter(System.out));

        Job job = Job.getInstance(conf, "LoadAverage");

        job.setNumReduceTasks(1);

        job.setInputFormatClass(RowInputFormat.class);

        // configure mapper and reducer
        job.setJarByClass(LoadAverage.class);
        job.setMapperClass(LoadAverageMapper.class);
        job.setReducerClass(LoadAverageReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LoadKey.class);

        TextOutputFormat.setOutputPath(job, outputPath);
        job.setOutputFormatClass(RowOutputFormat.class);
        job.setOutputKeyClass(Key.class);
        job.setOutputValueClass(LoadAverageModel.class);

        //TextOutputFormat.setOutputPath(job, outputPath);
        //job.setOutputFormatClass(TextOutputFormat.class);

        return job.waitForCompletion(true) ? 0 : 1;

    }

    public static void main(String[] args) throws Exception {
        int rc = ToolRunner.run(new LoadAverage(), args);

        System.out.println("Job completed. Return code:" + rc);
        System.exit(rc);
    }

}

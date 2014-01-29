package com.pivotal.gfxd.demo.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

/**
 * @author William Markito
 */
public class LoadAverage {
    static int TIME_SEGMENTS = 288; // 5 min segments of a day


    public static void main(String[] args) throws IllegalArgumentException,
            IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();

        Job job = new Job(conf, "word count");
        job.setJarByClass(LoadAverage.class);
        job.setMapperClass(LoadMapper.class);
        job.setReducerClass(LoadReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public enum POS {
        ID(0), TIMESTAMP(1), VALUE(2), PROPERTY(3), PLUG_ID(4), HOUSEHOLD_ID(5), HOUSE_ID(6);

        private int code;

        private POS(int code) {
            this.code = code;
        }
    };
}

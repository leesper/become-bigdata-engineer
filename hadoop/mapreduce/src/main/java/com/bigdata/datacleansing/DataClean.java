package com.bigdata.datacleansing;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class DataClean {

    enum DataCleanCounter {
        MALFORMED
    }

    public static void main(String[] args)
            throws IOException, InterruptedException, ClassNotFoundException {
        if (args == null || args.length != 2) {
            System.out.println("Usage: DataClean inputPath outputPath");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, DataClean.class.getSimpleName());
        job.setJarByClass(DataClean.class);

        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path(args[0]));

        job.setMapperClass(DataCleanMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setNumReduceTasks(0);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class DataCleanMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] fields = value.toString().split("\t");
            if (fields != null && fields.length == 6) {
                context.write(value, NullWritable.get());
            } else {
                context.getCounter(DataCleanCounter.MALFORMED).increment(1);
            }
        }
    }
}

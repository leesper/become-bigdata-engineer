package com.bigdata.usersearch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class UserSearchCount {
    public static void main(String[] args)
            throws IOException, ClassNotFoundException, InterruptedException {
        if (args == null || args.length != 2) {
            System.out.println("Usage: UserSearchCount inputPath outputPath");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, UserSearchCount.class.getSimpleName());
        job.setJarByClass(UserSearchCount.class);

        TextInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);
        job.setMapperClass(UserSearchMapper.class);

        job.setReducerClass(UserSearchReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setNumReduceTasks(5);

        System.exit(job.waitForCompletion(true)  ? 0 : 1);

    }

    public static class UserSearchMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private Text userID = new Text();
        private final IntWritable ONE = new IntWritable(1);
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] fields = value.toString().split("\t");
            userID.set(fields[1]);
            context.write(userID, ONE);
        }
    }

    public static class UserSearchReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable count = new IntWritable();
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value : values)  {
                sum += value.get();
            }
            count.set(sum);
            context.write(key, count);
        }
    }
}

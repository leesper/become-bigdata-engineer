package com.bigdata.custompartitioner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomPartitionDemo extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = super.getConf();
        Job job = Job.getInstance(conf, CustomPartitionDemo.class.getSimpleName());
        job.setJarByClass(CustomPartitionDemo.class);

        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path(args[0]));

        job.setMapperClass(WordCountMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setPartitionerClass(WordCountPartitioner.class);
        job.setReducerClass(WordCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setNumReduceTasks(4);

        return job.waitForCompletion(true) ? 0 : 1;

    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 2) {
            System.out.println("Usage: CustomPartitionDemo inputPath outputPath");
            System.exit(1);
        }

        Configuration conf = new Configuration();
        System.exit(ToolRunner.run(conf, new CustomPartitionDemo(), args));
    }

    public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private static final IntWritable ONE = new IntWritable(1);
        private Text word = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] words = value.toString().split(" ");
            for (String w : words) {
                word.set(w);
                context.write(word, ONE);
            }
        }
    }

    public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable total = new IntWritable();
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable v : values) {
                sum += v.get();
            }
            total.set(sum);
            context.write(key, total);
        }
    }

    public static class WordCountPartitioner extends Partitioner<Text, IntWritable> {
        private static Map<String, Integer> dict = new HashMap<>();

        static {
            dict.put("Dear", 0);
            dict.put("Bear", 1);
            dict.put("River", 2);
            dict.put("Car", 3);
        }

        @Override
        public int getPartition(Text text, IntWritable intWritable, int i) {
            return dict.get(text.toString());
        }
    }
}

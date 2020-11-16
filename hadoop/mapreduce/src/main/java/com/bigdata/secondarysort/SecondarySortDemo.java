package com.bigdata.secondarysort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;


public class SecondarySortDemo extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = super.getConf();
        Job job = Job.getInstance(conf, SecondarySortDemo.class.getSimpleName());
        job.setJarByClass(SecondarySortDemo.class);

        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path(args[0]));

        job.setMapperClass(SecondaryMapper.class);
        job.setMapOutputKeyClass(Person.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setReducerClass(SecondaryReducer.class);
        job.setOutputKeyClass(Person.class);
        job.setOutputValueClass(NullWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        ToolRunner.run(conf, new SecondarySortDemo(), args);
    }

    public static class SecondaryMapper extends Mapper<LongWritable, Text, Person, NullWritable> {
        private Person person = new Person();
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] fields = value.toString().split("\t");
            person.setName(fields[0]);
            person.setAge(Integer.parseInt(fields[1]));
            person.setSalary(Integer.parseInt(fields[2]));

            context.write(person, NullWritable.get());
        }
    }

    public static class SecondaryReducer extends Reducer<Person, NullWritable, Person, NullWritable> {
        @Override
        protected void reduce(Person key, Iterable<NullWritable> values, Context context)
                throws IOException, InterruptedException {
            context.write(key, NullWritable.get());
        }
    }
}

package com.bigdata.customgroup;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomGroupDemo extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = super.getConf();
        Job job = Job.getInstance(conf, CustomGroupDemo.class.getSimpleName());
        job.setJarByClass(CustomGroupDemo.class);

        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path(args[0]));

        job.setMapperClass(CustomGroupMapper.class);
        job.setMapOutputKeyClass(UserOrder.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        job.setReducerClass(CustomGroupReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setPartitionerClass(CustomPartitioner.class);
        job.setSortComparatorClass(CustomSortComparator.class);
        job.setGroupingComparatorClass(CustomGroupComparator.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        System.exit(ToolRunner.run(conf, new CustomGroupDemo(), args));
    }

    public static class CustomGroupMapper extends Mapper<LongWritable, Text, UserOrder, DoubleWritable> {
        private DoubleWritable totalPrice = new DoubleWritable();
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] fields = value.toString().split("\t");
            if (fields.length == 6) {
                String userID = fields[0];
                String yearMonth = getYearMonthString(fields[1]);
                if (yearMonth != "") {
                    String title = fields[2];
                    double unitPrice = Double.parseDouble(fields[3]);
                    int purchaseNum = Integer.parseInt(fields[4]);
                    String productID = fields[5];

                    UserOrder order = new UserOrder(userID, yearMonth, title, unitPrice, purchaseNum, productID);
                    totalPrice.set(unitPrice * purchaseNum);
                    context.write(order, totalPrice);
                }
            }

        }

        public String getYearMonthString(String dateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
                int year = localDateTime.getYear();
                int month = localDateTime.getMonthValue();
                return year + "" + month;
            } catch (DateTimeParseException ex) {
                System.out.println(ex);
                return "";
            }
        }
    }

    public static class CustomGroupReducer extends Reducer<UserOrder, DoubleWritable, Text, DoubleWritable> {
        private Text textKey = new Text();
        @Override
        protected void reduce(UserOrder key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            int num = 0;
            for (DoubleWritable doubleWritable : values) {
                textKey.set(key.getUserID() + " " + key.getYearMonth());
                context.write(textKey, doubleWritable);
                num++;
                if (num >= 2) {
                    break;
                }
            }
        }
    }

    public static class CustomPartitioner extends Partitioner<UserOrder, DoubleWritable> {

        @Override
        public int getPartition(UserOrder userOrder, DoubleWritable doubleWritable, int numReduceTasks) {
            return (userOrder.getUserID().hashCode() & Integer.MAX_VALUE) % numReduceTasks;
        }
    }

    public static class CustomSortComparator extends WritableComparator {
        protected CustomSortComparator() {
            super(UserOrder.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            UserOrder order1 = (UserOrder)a;
            UserOrder order2 = (UserOrder)b;

            int ret = order1.getUserID().compareTo(order2.getUserID());

            if (ret != 0) return ret;

            ret = order1.getYearMonth().compareTo(order2.getYearMonth());
            if (ret != 0) return ret;

            Double price1 = order1.getUnitPrice() * order1.getPurchaseNum();
            Double price2 = order2.getUnitPrice() * order2.getPurchaseNum();
            return -price1.compareTo(price2);
        }
    }

    public static class CustomGroupComparator extends WritableComparator {
        protected CustomGroupComparator() {
            super(UserOrder.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            UserOrder order1 = (UserOrder)a;
            UserOrder order2 = (UserOrder)b;

            int ret = order1.getUserID().compareTo(order2.getUserID());
            if (ret == 0) {
                return order1.getYearMonth().compareTo(order2.getYearMonth());
            }
            return ret;
        }
    }
}

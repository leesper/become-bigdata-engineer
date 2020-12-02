package com.bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class HBase2HDFS extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node01:2181,node02:2181,node03:2181");
        int run = ToolRunner.run(conf, new HBase2HDFS(), args);
        System.exit(run);
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(super.getConf());
        job.setJobName("HBase2HDFS");
        job.setJarByClass(HBase2HDFS.class);

        Scan scan = new Scan();
        scan.setCaching(500);
        scan.setCacheBlocks(false);

        TableMapReduceUtil.initTableMapperJob(
                "firsttable",
                scan,
                HBaseMapper.class,
                Text.class,
                NullWritable.class,
                job);
        job.setReducerClass(HBaseReducer.class);
        job.setNumReduceTasks(1);
        TextOutputFormat.setOutputPath(job, new Path("/user/hadoop/"));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class HBaseMapper extends TableMapper<Text, NullWritable> {
        public static final byte[] COL1 = "col1".getBytes();
        public static final byte[] COL2 = "col2".getBytes();
        public static final byte[] OID = "oid".getBytes();
        public static final byte[] PROID = "proid".getBytes();
        private Text textRow = new Text();

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context)
                throws IOException, InterruptedException {
            String rowkey = Bytes.toString(key.get());
            String oid = new String(value.getValue(COL1, OID));
            String proid = new String(value.getValue(COL2, PROID));
            String row = String.format("%s\t%s\t%s", rowkey, oid, proid);
            textRow.set(row);
            context.write(textRow, NullWritable.get());
        }
    }

    public static class HBaseReducer extends Reducer<Text, NullWritable, Text, NullWritable> {
        @Override
        protected void reduce(Text key, Iterable<NullWritable> values, Context context)
                throws IOException, InterruptedException {
            context.write(key, NullWritable.get());
        }
    }
}

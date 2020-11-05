package com.bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HDFSOperate {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://node01:8020");
        FileSystem fs = FileSystem.get(conf);
        Path src = new Path("file:///Users/likejun/become-bigdata-engineer/hadoop/hdfs/a.txt");
        Path dst = new Path("/kkb/a.txt");
        fs.copyFromLocalFile(src, dst);
        fs.close();

    }
}

package zedwar.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ETLDriver implements Tool {

    private Configuration conf;
    public int run(String[] strings) throws Exception {

        Job job = Job.getInstance(conf);
        job.setJarByClass(ETLDriver.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.setInputPaths(job,new Path(strings[0]));
        FileOutputFormat.setOutputPath(job,new Path(strings[1]));
        boolean result = job.waitForCompletion(true);
        return result?0:1;
    }

    public void setConf(Configuration configuration) {
        conf = configuration;
    }

    public Configuration getConf() {
        return conf;
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        try {
            int run = ToolRunner.run(configuration, new ETLDriver(), args);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

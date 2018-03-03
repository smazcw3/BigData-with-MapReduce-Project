import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Set;


public class CoOccurrenceStripes {
	public static class TokenizerMapper extends Mapper<LongWritable,Text,Text,MyMapWritable> {
		private int neighbors = 2;
		private MyMapWritable occurrenceMap = new MyMapWritable();
	    private Text word = new Text();

	    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
	        String[] tokens = split_string(value);
	        
	        if (tokens.length > 1) {
	        	loop_checking(tokens, occurrenceMap, neighbors, word, context);   	
	        }
	    }
	    
	    public void loop_checking(String [] tokens, MyMapWritable occurrenceMap, int neighbors, Text word, Context context) throws IOException, InterruptedException{
        	int tokens_length = tokens.length;
	    	for (int i = 0; i < tokens_length; i++) {
                set1(word, occurrenceMap, tokens[i]);
                
                int start = 0;
                if(i - neighbors < 0){
                	start = 0;
                }
                else{
                	start = i - neighbors;
                }
                
                int end = 0;
                if(i + neighbors >= tokens.length){
                	end = tokens.length - 1;
                }
                else{
                	end = i + neighbors;
                }

                for (int j = start; j <= end; j++) {
                    if (check1(j, i) == true) 
                    	continue;
                    Text neighbor = new Text(tokens[j]);
                    if(occurrenceMap.containsKey(neighbor)){
                       IntWritable count = (IntWritable)occurrenceMap.get(neighbor);
                       int count_inc = count.get() + 1;
                       count.set(count_inc);
                    }
                    else{
                        occurrenceMap.put(neighbor,new IntWritable(1));
                    }
                }
              context.write(word,occurrenceMap);
            }
	    }
	    
	    public void set1(Text word, MapWritable occurrenceMap, String tokens){
	    	word.set(tokens);
            occurrenceMap.clear();
	    }
	    
	    public boolean check1(int i, int j){
	    	if(j == i){
	    		return true;
	    	}
	    	else{
	    		return false;
	    	}
	    }
	    
		public String[] split_string(Text value){
			String text = value.toString();
			String[] terms = text.split("\\s+");
			return terms;
		}

	    
	    
		//Start Debugging Purpose
		@Override
		protected void setup(Mapper<LongWritable, Text, Text, MyMapWritable>.Context context) throws IOException, InterruptedException {
			neighbors = context.getConfiguration().getInt("neighbors", 2);
			System.out.println("calls only once at startup");
		}
		@Override
		protected void cleanup(Mapper<LongWritable, Text, Text, MyMapWritable>.Context context) throws IOException, InterruptedException {
			System.out.println("calls only once at end");
		}
	   //End Debugging Purpose

	    
	    
	}
	
	public static class IntSumReducer extends Reducer<Text, MyMapWritable, Text, MyMapWritable> {
	    //private MapWritable incrementingMap = new MapWritable();

	    private MyMapWritable incrementingMap = new MyMapWritable();
	    
	    public void reduce(Text key, Iterable<MyMapWritable> values, Context context) throws IOException, InterruptedException {
	        incrementingMap.clear();
	        if(key.getLength() > 0){
		        for (MyMapWritable value : values) {		       
			        Set<Writable> keys = value.keySet();
			        for (Writable k : keys) {			         
			        	IntWritable fromCount = set_iw(value, k);
			            if (check_key(incrementingMap, k) == true) {			            	
			                IntWritable count = set_iw2(k);
			                count.set(count.get() + fromCount.get());
			            } else {
			                incrementingMap.put(k, fromCount);
			            }
			        }

		        }	        
		        context.write(key, incrementingMap);
	        }
	    }
	    
	    public IntWritable set_iw(MyMapWritable value, Writable k){
	    	IntWritable fromCount = (IntWritable) value.get(k);
	    	return fromCount;
	    }
	    
	    public IntWritable set_iw2(Writable k){
	    	IntWritable count = (IntWritable) incrementingMap.get(k);
	    	return count;
	    }

	    
	    public boolean check_key(MyMapWritable incrementingMap, Writable key){
	    	if(incrementingMap.containsKey(key) == true){
	    		return true;
	    	}
	    	else{
	    		return false;
	    	}
	    }	    
	}
	
	  public static void main(String[] args) throws Exception {
		    Configuration conf = new Configuration();
		    Job job = Job.getInstance(conf, "CoOccurrenceStripes");
		    job.setJarByClass(CoOccurrenceStripes.class);
		    job.setMapperClass(TokenizerMapper.class);
		    job.setCombinerClass(IntSumReducer.class);
		    job.setReducerClass(IntSumReducer.class);
		    job.setMapOutputKeyClass(Text.class);
		    job.setMapOutputValueClass(MyMapWritable.class);
		    job.setOutputKeyClass(Text.class);
		    job.setOutputValueClass(MyMapWritable.class);
		    FileInputFormat.addInputPath(job, new Path(args[0]));
		    FileOutputFormat.setOutputPath(job, new Path(args[1]));
		    //System.exit(job.waitForCompletion(true) ? 0 : 1);
		    
		    //Start Debugging purpose
			int returnValue = job.waitForCompletion(true) ? 0 : 1;
			System.out.println(job.isSuccessful());
			System.exit(returnValue);
			//End Debugging purpose
		    
		  }
	  
	  public static class MyMapWritable extends MapWritable {
		    @Override
		    public String toString() {
		        StringBuilder result = new StringBuilder();
		        Set<Writable> keySet = this.keySet();

		        for (Object key : keySet) {
		            result.append("{" + key.toString() + " = " + this.get(key) + "}");
		        }
		        return "{" + result.toString() + "}" + "\n";
		    }
		}
}

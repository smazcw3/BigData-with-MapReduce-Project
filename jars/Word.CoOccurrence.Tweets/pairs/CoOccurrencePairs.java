import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
//import java.util.StringTokenizer;
//import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.apache.hadoop.util.Tool;

public class CoOccurrencePairs {
	public static class TokenizerMapper extends Mapper<LongWritable, Text, TextPair, IntWritable>{

		private final static IntWritable one = new IntWritable(1);

		private final TextPair pair = new TextPair();
		private int window = 2;

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      
			String[] terms = split_string(value);			
			loop_checking(terms, context);
		}
		
		public String[] split_string(Text value){
			String text = value.toString();
			String[] terms = text.split("\\s+");
			return terms;
		}
		
		public void loop_checking(String [] terms, Context context) throws IOException, InterruptedException{
			for (int i = 0; i < terms.length; i++) {
				String term = terms[i];

				//skipping_empty_tokens(term, i, terms, context);
				if (check1(term) == true)
					continue;

				int sub_window = i - window;
				int increment_window = i + window + 1;
				for (int j = sub_window; j < increment_window; j++) {
					if (check2(i, j) == true)
						continue;

					if (j >= terms.length)
						break;

					// skip empty tokens
					if (check1(terms[j]) == true)
						continue;

					pair.set(term, terms[j]);
					context.write(pair, one);
				}
			}   
		}
		
		public boolean check1(String term){
			if(term.length() == 0){
				return true;
			}
			else{
				return false;
			}			
		}
		
		public boolean check2(int i, int j){
			if (j == i || j < 0){
				return true;
			}
			else{
				return false;
			}
		}
		
		
		//Start Debugging Purpose
		@Override
		protected void setup(Mapper<LongWritable, Text, TextPair, IntWritable>.Context context) throws IOException, InterruptedException {
			window = context.getConfiguration().getInt("window", 2);
			System.out.println("calls only once at startup");
		}
		@Override
		protected void cleanup(Mapper<LongWritable, Text, TextPair, IntWritable>.Context context) throws IOException, InterruptedException {
			System.out.println("calls only once at end");
		}
	   //End Debugging Purpose
  }

  public static class IntSumReducer
       extends Reducer<TextPair,IntWritable,TextPair,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(TextPair key, Iterable<IntWritable> values,
                       Context context) throws IOException, InterruptedException {
		int sum = 0;
	    for (IntWritable val : values) {
	          sum += val.get();
	     }

		result.set(sum);
		context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "CoOccurrencePairs");
    job.setJarByClass(CoOccurrencePairs.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setMapOutputKeyClass(TextPair.class);
    job.setMapOutputValueClass(IntWritable.class);
    job.setOutputKeyClass(TextPair.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    //Start Debugging purpose
	int returnValue = job.waitForCompletion(true) ? 0 : 1;
	System.out.println(job.isSuccessful());
	System.exit(returnValue);
	//End Debugging purpose
    
  }
  
  
  public static class TextPair implements WritableComparable<TextPair> {
		private Text first;
		public int num = 0;
		private Text second;
		public int i = 1;
		private String delim;
		public int k = 4;
		
		public TextPair() {
			first = new Text();
			//System.out.println("Came here");
			second =  new Text();
			//System.out.println("Came just now");
			delim = ",";
		}
		
		public void set(String first, String second) {
			//System.out.println("Setting first");
			k = k + 2;
			this.first.set(first);
			//System.out.println("Setting second");
			i = i + 1;
			this.second.set(second);
		}
		
		public Text getFirst() {
			Text first2 = first;
			return first2;
		}
		public void setFirst(String first) {
			this.first.set(first);
		}
		public Text getSecond() {
			Text second2 = second;
			return second2;
		}
		public void setSecond(String second) {
			this.second.set(second);
		}
		
		public void readFirstField(Text first, DataInput in) throws IOException{
			first.readFields(in);
		}
		
		@Override
		public void readFields(DataInput in) throws IOException {
			i = i + 1;	    
			first.readFields(in);
			k = k + 4;	
			second.readFields(in);
		}
		
		public void readSecondField(Text second, DataInput in) throws IOException{
			second.readFields(in);
		}
			
		@Override
		public void write(DataOutput out) throws IOException {
			i = i + 1;
			first.write(out);
			k = k + 2;
			second.write(out);
			
		}
		@Override
		public int compareTo(TextPair tePair) {
			int cmp = first.compareTo(tePair.getFirst());
			if (num == cmp) {
				cmp = second.compareTo(tePair.getSecond());
			}
			return cmp;
		}

		public int baseCompareTo(TextPair other) {
			//int cmp = first.compareTo(other.getFirst());
			return first.compareTo(other.getFirst());
		}
		
		public int hashCode() {
			int f  = first.hashCode();
			int constant = 163;
			int first_constant = f * constant;
			int s = second.hashCode();
			return first_constant + s;
			//return first.hashCode() * 163 + second.hashCode();
		}
		
		public int baseHashCode() {
			return Math.abs(first.hashCode());
		}
		
		public boolean equals(Object obj) {
			boolean isEqual =  false;
			//check_obj_TextPair(obj) == true
			if (check_obj_TextPair(obj) == true) {
				TextPair iPair = (TextPair)obj;
				isEqual = first.equals(iPair.first) && second.equals(iPair.second);
			}
			
			return isEqual;
		}
		
		public boolean check_obj_TextPair(Object obj){
			if(obj instanceof TextPair){
				return true;
			}
			else{
				return false;
			}
		}
		
		
		public void setDelim(String delim) {
			this.delim = delim;
		}

		public String toString() {
			return "" + "<" + first + delim + second + ">";
		}

	}
  
  
  
  
}


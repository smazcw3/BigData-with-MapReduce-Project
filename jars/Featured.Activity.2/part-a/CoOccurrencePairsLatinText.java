import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CoOccurrencePairsLatinText 
{
	   public static class TokenizerMapper extends Mapper<Object, Text, TextPair, Text>{
		   
		   //creation of hash map from the .csv file
		   //private Text word = new Text();
		   //private final static IntWritable one = new IntWritable(1);
		   //private Text values = new Text();
		   //private int window = 3;
		   
		   private final TextPair pair = new TextPair();
		   public HashMap<String, List<String>> hmap;

		   public void map(Object key, Text value, Mapper<Object, Text, TextPair, Text>.Context context) throws IOException, InterruptedException {

			    //System.out.println(key); //Document id
			    String text = value.toString();		
			    List<String> lemma_list = new ArrayList<String>();
			    if(text.length() > 0){		    
					String[] out_terms = text.split("\t");
					String location = out_terms[0]; //<luc. 1.1>//<luc. 1.2>///////<verg. aen. 1.1>//<verg. aen. 1.2>
					//System.out.println(location);
					String[] chapter_page = location.split("\\s+");	
					//System.out.println(chapter_page[0]);
					String author = chapter_page[0].replaceAll("<", "");  //luc.// verg.
					//System.out.println(author);
					if(author.equals("luc.")){
						//String doc_id = Integer.toString(1);
						String doc_id = author;
						String[] chapter_page_no = chapter_page[1].replaceAll(">", "").split("\\.");
						String chapter = chapter_page_no[0];
						String page = chapter_page_no[1].replaceAll(">", "");				
						String doc_ch_li = "<" + doc_id + "," + "[" + chapter + "," + page + "]" +">" + "."; 
						Text dcl = new Text(doc_ch_li);
						
						String[] in_terms = out_terms[1].split("\\s+");	
						
						//List<String> values = new ArrayList<String>();
						for(int j = 0; j < in_terms.length; j++){
							List<String> values = new ArrayList<String>();
							String replaceString = in_terms[j].toLowerCase().replaceAll("j", "i");
							String replaceString2 = replaceString.replaceAll("v", "u");
							String replaceString3 = replaceString2.replaceAll("[.,:;()?!\\-\\s+\\[\\]\"]+","");
							if(hmap.containsKey(replaceString3)){
								values.addAll(hmap.get(replaceString3));
								lemma_list.addAll(values);
							}
							else{
								values.add(replaceString3);
								lemma_list.addAll(values);
							}
						}
						//System.out.println(lemma_list.toString());
						
						String []terms = lemma_list.toArray(new String[lemma_list.size()]);
						
						for (int i = 0; i < terms.length - 1; i++) {
							String term = terms[i];

							// skip empty tokens
							if (term.length() == 0)
								continue;

							int terms_length = terms.length;
							
							for (int k = i + 1; k < terms_length; k++) {
								if (k == i || k < 0)
									continue;

								if (k >= terms_length)
									break;

								int terms_k_length = terms[k].length();
								
								if (terms_k_length == 0)
									continue;

								pair.set(term, terms[k]);
								//word.set(dcl);
								context.write(pair, dcl);
							}
						}											
	
						
					}	
					
					
					else if(author.equals("verg.")){
						//String doc_id = Integer.toString(2);
						String temp = chapter_page[0] + chapter_page[1];
						String doc_id = temp.replaceAll("<", "");
						String[] chapter_page_no = chapter_page[2].split("\\.");
						String chapter = chapter_page_no[0];
						String page = chapter_page_no[1].replaceAll(">", "");				
				
						String doc_ch_li = " <" + doc_id + "," + "[" + chapter + "," + page + "]" + ">" + "."; 
						Text dcl = new Text(doc_ch_li);
						
						
						String[] in_terms = out_terms[1].split("\\s+");	
						//List<String> values = new ArrayList<String>();
						for(int j = 0; j < in_terms.length; j++){
							List<String> values = new ArrayList<String>();
							String replaceString = in_terms[j].toLowerCase().replaceAll("j", "i");
							String replaceString2 = replaceString.replaceAll("v", "u");
							String replaceString3 = replaceString2.replaceAll("[.,:;()?!\\-\\s+\\[\\]\"]+","");
							if(hmap.containsKey(replaceString3)){
								values.addAll(hmap.get(replaceString3));
								lemma_list.addAll(values);
							}
							else{
								values.add(replaceString3);
								lemma_list.addAll(values);
							}	
						}
						
						//System.out.println(lemma_list.toString());
						
						String []terms = lemma_list.toArray(new String[lemma_list.size()]);
						
						for (int i = 0; i < terms.length - 1; i++) {
							String term = terms[i];

							// skip empty tokens
							int term_length = term.length();
							if (term_length == 0)
								continue;

							int terms_length = terms.length;
							for (int k = i + 1; k < terms_length; k++) {
								if (k == i || k < 0)
									continue;

								if (k >= terms_length)
									break;

								int terms_k_length = terms[k].length();
								if (terms_k_length == 0)
									continue;

								pair.set(term, terms[k]);
								//word.set(dcl);
								context.write(pair, dcl);
							}
						}											
	
					}
					else{
						//***
						String text2 = value.toString();
						String[] out_terms2 = text2.split("\\s+");
						String doc_id = out_terms2[0].replaceAll("<", "");
						String chapter = out_terms2[1];
						String page = out_terms2[2].replaceAll(">", "");
						String doc_ch_li = "<" + doc_id + "," + "[" + chapter + "," + page + "]" + ">" + ".";  
						Text dcl = new Text(doc_ch_li);
						
						String st = "";
						for(int pos = 3; pos < out_terms2.length; pos++){
							st = st + out_terms2[pos] + " ";
						}
						
						
						
						String[] in_terms = st.split("\\s+");	
						//List<String> values = new ArrayList<String>();
						int in_terms_length = in_terms.length;
						for(int j = 0; j < in_terms_length; j++){
							List<String> values = new ArrayList<String>();
							String replaceString = in_terms[j].toLowerCase().replaceAll("j", "i");
							String replaceString2 = replaceString.replaceAll("v", "u");
							String replaceString3 = replaceString2.replaceAll("[.,:;()<>*&#\\\\'/?!\\d+\\-\\s+\\[\\]\"]+","");
							if(hmap.containsKey(replaceString3)){
								values.addAll(hmap.get(replaceString3));
								lemma_list.addAll(values);
							}
							else{
								values.add(replaceString3);
								lemma_list.addAll(values);
							}	
						}
						
						//System.out.println(lemma_list.toString());
						
						String []terms = lemma_list.toArray(new String[lemma_list.size()]);
						
						
						for (int i = 0; i < terms.length - 1; i++) {
							String term = terms[i];

							// skip empty tokens
							if (term.length() == 0)
								continue;

							int terms_length = terms.length;							
							for (int k = i + 1; k < terms_length; k++) {
								if (k == i || k < 0)
									continue;

								if (k >= terms_length)
									break;

								int terms_k_length = terms[k].length();
								if (terms_k_length == 0)
									continue;
								
								if(!term.equals(terms[k])){								
									pair.set(term, terms[k]);
									//word.set(dcl);
									context.write(pair, dcl);
								}
							}
						}											
					}
					}
			    }
		   
		   //Start Debugging Purpose
			@Override
			protected void setup(Mapper<Object, Text, TextPair, Text>.Context context)
					throws IOException, InterruptedException {
				System.out.println("calls only once at startup");
				//Configuration conf = context.getConfiguration();
				   BufferedReader br = new BufferedReader(new FileReader("new_lemmatizer.csv"));
				   hmap = new HashMap<String, List<String>>();		   		   
				   String line = "";
				   String[] cols;
				   while ((line = br.readLine()) != null) {
				       // use comma as separator
				       cols = line.split(",", -1);
				       //map.put(cols[0], new ArrayList<Integer>());
				       //System.out.println(cols[0]);
				       List<String> valSetOne = new ArrayList<String>();
				       valSetOne.add(cols[1]);
				       hmap.put(cols[0], valSetOne);
				       if(cols[2] == null || cols[2].length() == 0){
				    	   continue;	    	    
				       }
				       else{
				    	   valSetOne.add(cols[2]);
				    	   hmap.put(cols[0], valSetOne);
				       }
				       if(cols[3] == null || cols[3].length() == 0){
				    	   continue;
				       }
				       else{
				    	   valSetOne.add(cols[3]);
				    	   hmap.put(cols[0], valSetOne);
				       }
				       if(cols[4] == null || cols[4].length() == 0){
				    	   continue;	    	    
				       }
				       else{
				    	   valSetOne.add(cols[4]);
				    	   hmap.put(cols[0], valSetOne);
				       }
				       if(cols[5] == null || cols[5].length() == 0){
				    	   continue;	    	    
				       }
				       else{
				    	   valSetOne.add(cols[5]);
				    	   hmap.put(cols[0], valSetOne);
				       }

				   }
				   br.close();
			}
			@Override
			protected void cleanup(Mapper<Object, Text, TextPair, Text>.Context context)
					throws IOException, InterruptedException {
				System.out.println("calls only once at end");
				
			}
			//End Debugging Purpose
	   	
		}

	public static class IntSumReducer extends Reducer<TextPair,Text,TextPair,Text> {
		 
		 Text final_result = new Text();
		 		 
	    public void reduce(TextPair key, Iterable<Text> values, Reducer<TextPair, Text, TextPair, Text>.Context context) throws IOException, InterruptedException {
	    	    	 
	   	     //Experiment 1------------
	   		 List<String> result = new ArrayList<String>();
	   		 for(Text val : values){
	   			 result.add(val.toString());
	   		 }
	   		 
	   		 StringBuilder listString = new StringBuilder();
	   		 for (String s : result)
	   		     listString.append(s+"");
	   		  
	   		 listString.setLength(listString.length() - 1);
	   		 
	   		 context.write(key, new Text(listString + " Total count: " + Integer.toString(result.size()) + " "));
	   		 	   		 
	   	 
	    }	 
	}

		 public static void main(String[] args) throws Exception {
			 
			   final long startTime = System.nanoTime();
		
			   Configuration conf = new Configuration(); 
		   
			   Job job = Job.getInstance(conf, "word co-occurrence on latin text");
			   job.setJarByClass(CoOccurrencePairsLatinText.class);
			   job.setMapperClass(TokenizerMapper.class);
			   job.setMapOutputKeyClass(TextPair.class);
			   job.setMapOutputValueClass(Text.class);
			   job.setCombinerClass(IntSumReducer.class);
			   job.setReducerClass(IntSumReducer.class);
			   job.setOutputKeyClass(TextPair.class);
			   job.setOutputValueClass(Text.class);
			   
			   FileInputFormat.addInputPath(job, new Path(args[0]));
			   //FileInputFormat.addInputPath(job, new Path(args[1]));
			   FileOutputFormat.setOutputPath(job, new Path(args[1]));
			   
			   //System.exit(job.waitForCompletion(true) ? 0 : 1);
			   
			   //Start Debugging purpose
				int returnValue = job.waitForCompletion(true) ? 0 : 1;
				System.out.println(job.isSuccessful());
				
				final long duration = System.nanoTime() - startTime; 
				double seconds = (double)duration / 1000000000.0;
				System.out.println("Total time taken(in seconds): " + seconds);
				
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
					int j = 0;
					//System.out.println("Came here");
					j = j + 1;
					second =  new Text();
					//System.out.println("Came just now");
					delim = ",";
				}
				
				public void set(String first, String second) {
					//System.out.println("Setting first");
					int f = 0;
					f = f + 1;
					this.first.set(first);
					//System.out.println("Setting second");
					f = f + 2;
					this.second.set(second);
				}
				
				public Text getFirst() {
					Text first2 = first;
					return first2;
					//return first;
				}
				public void setFirst(String first) {
					this.first.set(first);
				}
				public Text getSecond() {
					Text second2 = second;
					return second2;
					//return second;
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
	


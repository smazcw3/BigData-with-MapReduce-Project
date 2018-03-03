import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WCLatinText {
	
   public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>{
	   
	   //creation of hash map from the .csv file
	   private Text word = new Text();
	   //private Text values = new Text();
	   
	   
	   private HashMap<String, List<String>> hmap;

	   public void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context) throws IOException, InterruptedException {

		    //System.out.println(key); //Document id
		    String text = value.toString();		    
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
						}
						else{
							values.add(replaceString3);						
						}
						for(String item : values){
							//String temp = location + item;
							word.set(item);
							//new Triples(key,chapter, page)
							try {
								context.write(word, dcl);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}														
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
						}
						else{
							values.add(replaceString3);						
						}
						for(String item : values){
							//String temp = location + item;
							word.set(item);
							//new Triples(key,chapter, page)
							try {
								context.write(word, dcl);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							//context.write(word, one);
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
					for(int j = 0; j < in_terms.length; j++){
						List<String> values = new ArrayList<String>();
						String replaceString = in_terms[j].toLowerCase().replaceAll("j", "i");
						String replaceString2 = replaceString.replaceAll("v", "u");
						String replaceString3 = replaceString2.replaceAll("[.,:;()'/?!\\d+\\-\\s+\\[\\]\"]+","");
						if(hmap.containsKey(replaceString3)){
							values.addAll(hmap.get(replaceString3));
						}
						else{
							values.add(replaceString3);						
						}
						for(String item : values){
							//String temp = location + item;
							word.set(item);
							//new Triples(key,chapter, page)
							try {
								context.write(word, dcl);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							//context.write(word, one);
						}
					}						
				}
						
		    }
	   }
	   //Start Debugging Purpose
		@Override
		protected void setup(Mapper<Object, Text, Text, Text>.Context context)
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
		protected void cleanup(Mapper<Object, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			System.out.println("calls only once at end");
		}
		//End Debugging Purpose

	}   
   
	 public static class IntSumReducer extends Reducer<Text,Text,Text,Text> {
		 
		 //Text final_result = new Text();
		 		 
	     public void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
	     	    	 
	    	 if(key.getLength() > 0){
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
	 }

	 public static void main(String[] args) throws Exception {
	
		 Configuration conf = new Configuration(); 
	   
		   Job job = Job.getInstance(conf, "word count on latin text");
		   job.setJarByClass(WCLatinText.class);
		   job.setMapperClass(TokenizerMapper.class);
		   job.setMapOutputValueClass(Text.class);
		   job.setCombinerClass(IntSumReducer.class);
		   job.setReducerClass(IntSumReducer.class);
		   job.setOutputKeyClass(Text.class);
		   job.setOutputValueClass(Text.class);
		   
		   FileInputFormat.addInputPath(job, new Path(args[0]));
		   //FileInputFormat.addInputPath(job, new Path(args[1]));
		   FileOutputFormat.setOutputPath(job, new Path(args[1]));
		   
		   //System.exit(job.waitForCompletion(true) ? 0 : 1);
		   
		   //Start Debugging purpose
			int returnValue = job.waitForCompletion(true) ? 0 : 1;
			System.out.println(job.isSuccessful());
			System.exit(returnValue);
			//End Debugging purpose
		 }
	 	 
}

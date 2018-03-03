# BigData-with-MapReduce-Project

This Lab project was implemented to understand the large scale data (text) processing using Hadoop MapReduce. It was part of the coursework for "Data intensive Computing" coursework in Spring 2017.

Few Notes on Instructions:
-------------------------

1. I have kept the all the sub folders of the 'output' folder empty. This is because the instructions below for different activities will generate the output in their respective folders. To view the sample output, please navigate to 'sample.output' folder.

2. 'jars' are being already created in the respective folder. If you want to compile it again then follow the instructions below. The code for different activities also reside along with their jar files in the respective sub folders inside “jars”

3. 'input' contains all the respective input folders

4. 'R.code' contains all the required code made in R for "getting tweets", "building word cloud" and "plots for featured.activity.2".

5. The R code for generation of plots as well as the plots for "Featured.activity.2" is contained in "~/Lab4/output/Featured.Activity.2/Featured.Activity.2-plots". Alternatively, It is also kept in "~/Lab4/R.Code/Featured.Activity.2.plots".

6. Word Cloud is visualized and kept in “~/Lab4/R.Code/Building.Word.Cloud”. Inside “~/Lab4/R.Code/Building.Word.Cloud” you will find the R code for building word cloud, the output of WordCount on Tweets(part-r-00000) and a word cloud image.


-------------------
WordCount on Tweets
-------------------

Start Hadoop by running:
start-hadoop.sh

Make a new directory on hdfs for the input files:
hdfs dfs -mkdir -p ~/input/

Copy the local "input_tweets.txt" file to the hdfs directory input/:
hdfs dfs -put ~/Lab4/input/Word.Count.Tweets/input_tweets.txt/ ~/input

Make sure you copied them to the right place:
hdfs dfs -ls ~/input



Change the directory:
cd ~/Lab4/jars/Word.Count.Tweets

Run the following to compile WordCount.java:
hadoop com.sun.tools.javac.Main WordCount.java

Run the following to create a jar:
jar cf wc.jar WordCount*.class

You can now run your MR job with the following:
hadoop jar wc.jar WordCount ~/input/input_tweets.txt ~/Lab4/output/Word.Count.Tweets/output1

You can now view the results by running:
hdfs dfs -cat ~/Lab4/output/Word.Count.Tweets/output1/*

Or if you need results locally:
hdfs dfs -get ~/Lab4/output/Word.Count.Tweets/output1 ~/Lab4/output/Word.Count.Tweets/output1
hdfs dfs -cat ~/Lab4/output/Word.Count.Tweets/output1/part-r-00000

----------------------------------------
CoOccurrencePairs on Tweets Instructions
----------------------------------------

Start Hadoop by running:
start-hadoop.sh

Make a new directory on hdfs for the input files:
hdfs dfs -mkdir -p ~/input/

Copy the local "input_tweets.txt" file to the hdfs directory input/:
hdfs dfs -put ~/Lab4/input/Word.CoOccurrence.Tweets/input_tweets.txt/ ~/input

Make sure you copied them to the right place:
hdfs dfs -ls ~/input


Change the directory:
cd ~/Lab4/jars/Word.CoOccurrence.Tweets/pairs

Run the following to compile CoOccurrenceStrips.java:
hadoop com.sun.tools.javac.Main CoOccurrencePairs.java

Run the following to create a jar:
jar cf pairs.jar CoOccurrencePairs*.class

You can now run your MR job with the following:
hadoop jar pairs.jar CoOccurrencePairs ~/input/input_tweets.txt ~/Lab4/output/Word.CoOccurrence.Tweets/pairs/output1

You can now view the results by running
hdfs dfs -cat ~/Lab4/output/Word.CoOccurrence.Tweets/pairs/output1/*

Or if you need results locally
hdfs dfs -get ~/Lab4/output/Word.CoOccurrence.Tweets/pairs/output1 ~/Lab4/output/Word.CoOccurrence.Tweets/pairs/output1
hdfs dfs -cat ~/Lab4/output/Word.CoOccurrence.Tweets/pairs/output1/part-r-00000


------------------------------------------
CoOccurrenceStripes on Tweets Instructions
------------------------------------------

Start Hadoop by running:
start-hadoop.sh

Make a new directory on hdfs for the input files:
hdfs dfs -mkdir -p ~/input/

Copy the local "input_tweets.txt" file to the hdfs directory input/:
hdfs dfs -put ~/Lab4/input/Word.CoOccurrence.Tweets/input_tweets.txt/ ~/input

Make sure you copied them to the right place:
hdfs dfs -ls ~/input


Change the directory:
cd ~/Lab4/jars/Word.CoOccurrence.Tweets/stripes

Run the following to compile CoOccurrenceStrips.java:
hadoop com.sun.tools.javac.Main CoOccurrenceStripes.java

Run the following to create a jar:
jar cf stripes.jar CoOccurrenceStripes*.class

You can now run your MR job with the following:
hadoop jar stripes.jar CoOccurrenceStripes ~/input/input_tweets.txt ~/Lab4/output/Word.CoOccurrence.Tweets/stripes/output1

You can now view the results by running
hdfs dfs -cat ~/Lab4/output/Word.CoOccurrence.Tweets/stripes/output1/*

Or if you need results locally
hdfs dfs -get ~/Lab4/output/Word.CoOccurrence.Tweets/stripes/output1 ~/Lab4/output/Word.CoOccurrence.Tweets/stripes/output1
hdfs dfs -cat ~/Lab4/output/Word.CoOccurrence.Tweets/stripes/output1/part-r-00000


--------------------------------------------------------
Featured Activity 1 : WordCount on Classical Latin Text
--------------------------------------------------------

Start Hadoop by running:
start-hadoop.sh

Make a new directory on hdfs for the input files:
hdfs dfs -mkdir -p ~/input/

Copy the local directory to the hdfs directory input/:
hdfs dfs -put ~/Lab4/input/Featured.Activity.1/ ~/input

Make sure you copied them to the right place:
hdfs dfs -ls ~/input


Change the directory:
cd ~/Lab4/jars/Featured.Activity.1/

Run the following to compile CoOccurrenceStrips.java:
hadoop com.sun.tools.javac.Main WCLatinText.java

Run the following to create a jar:
jar cf wclatintext.jar WCLatinText*.class

You can now run your MR job with the following:
hadoop jar wclatintext.jar WCLatinText ~/input/Featured.Activity.1 ~/Lab4/output/Featured.Activity.1/output1

You can now view the results by running
hdfs dfs -cat ~/Lab4/output/Featured.Activity.1/output1/*

Or if you need results locally
hdfs dfs -get ~/Lab4/output/Featured.Activity.1/output1 ~/Lab4/output/Featured.Activity.1/output1
hdfs dfs -cat ~/Lab4/output/Featured.Activity.1/output1/part-r-00000

------------------------------------------
Output Description for Featured Activity 1
------------------------------------------
ab <verg.aen.,[2,255]>.<verg.aen.,[1,730]>.<verg.aen.,[1,800]> Total count: 3 <luc.,[3,80]> Total count: 1 Total count: 2

ab => word
<verg.aen.,[2,255]> =>   <DocID, [Chapter#, Line#]>
Total count: 3 => count of location of word in document “verg.aen.”
Total count: 1 => count of location of word in document “luc.”
Total count: 2 => count of different documents in which this word appears.




-------------------------------------------------------------
Featured Activity 2 : WordCoOccurrence on Multiple Documents
-------------------------------------------------------------

--------------------
part-a Instructions
--------------------

Start Hadoop by running:
start-hadoop.sh

Make a new directory on hdfs for the input files:
hdfs dfs -mkdir -p ~/input/

Copy the local directory to the hdfs directory input/:
hdfs dfs -put ~/Lab4/input/Featured.Activity.2/part-a/ ~/input

Make sure you copied them to the right place:
hdfs dfs -ls ~/input


Change the directory:
cd ~/Lab4/jars/Featured.Activity.2/part-a

Run the following to compile CoOccurrenceStrips.java:
hadoop com.sun.tools.javac.Main CoOccurrencePairsLatinText.java

Run the following to create a jar:
jar cf cooccurlatintext.jar CoOccurrencePairsLatinText*.class

You can now run your MR job with the following:
hadoop jar cooccurlatintext.jar CoOccurrencePairsLatinText ~/input/part-a ~/Lab4/output/Featured.Activity.2/part-a/output1

You can now view the results by running
hdfs dfs -cat ~/Lab4/output/Featured.Activity.2/part-a/output1/*

Or if you need results locally
hdfs dfs -get ~/Lab4/output/Featured.Activity.2/part-a/output1 ~/Lab4/output/Featured.Activity.2/part-a/output1
hdfs dfs -cat ~/Lab4/output/Featured.Activity.2/part-a/output1/part-r-00000

--------------------------
part-a Output Description
--------------------------
<a,ab> <verg.aen.,[2,255]>.<verg.aen.,[1,730]>.<verg.aen.,[1,800]> Total count: 3 <luc.,[3,80]> Total count: 1 Total count: 2


<a,ab> => (Co-occurring word pair)
<verg.aen.,[2,255]> =>   <DocID, [Chapter#, Line#]>
Total count: 3 => count of location of co-occuring words in document “verg.aen.”
Total count: 1 => count of location of co-occuring words in document “luc.”
Total count: 2 => count of different documents in which this co-occurring appears.



--------------------
part-b Instructions
--------------------

Start Hadoop by running:
start-hadoop.sh

Make a new directory on hdfs for the input files:
hdfs dfs -mkdir -p ~/input/

Copy the local input directory to the hdfs directory input/:
hdfs dfs -put ~/Lab4/input/Featured.Activity.2/part-b/ ~/input

Make sure you copied them to the right place:
hdfs dfs -ls ~/input

Change the directory:
cd ~/Lab4/jars/Featured.Activity.2/part-b

Run the following to compile CoOccurrenceStrips.java:
hadoop com.sun.tools.javac.Main CoOccurrenceNgrams.java

Run the following to create a jar:
jar cf cooccurngramslatintext.jar CoOccurrenceNgrams*.class

You can now run your MR job with the following:
hadoop jar cooccurngramslatintext.jar CoOccurrenceNgrams ~/input/part-b ~/Lab4/output/Featured.Activity.2/part-b/output1

You can now view the results by running
hdfs dfs -cat ~/Lab4/output/Featured.Activity.2/part-b/output1/*

Or if you need results locally
hdfs dfs -get ~/Lab4/output/Featured.Activity.2/part-b/output1 ~/Lab4/output/Featured.Activity.2/part-b/output1
hdfs dfs -cat ~/Lab4/output/Featured.Activity.2/part-b/output1/part-r-00000


-------------------------
part-b Output Description
-------------------------
<a,silentium,tectum>	<verg.aen.,[2,255]>.<verg.aen.,[1,720]>.<verg.aen.,[1,760]>.<verg.aen.,[2,255]>.<verg.aen.,[1,800]> Total count: 5 Total count: 1


<a,silentium,tectum> =>   trigram
<verg.aen.,[2,255]> =>   <DocID, [Chapter#, Line#]>
Total count: 5 =>  count of location in which the trigram occurs
Total count: 1 => count of different documents in which this trigram co-occurs

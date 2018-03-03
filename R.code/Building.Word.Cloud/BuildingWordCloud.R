#Loading the library
library(wordcloud)

#To suppress warnings
options(warn=-1)

#Reading the results obtained by running "word count" on tweets
word_count <- read.table("part-r-00000", header=T, fill = TRUE ,sep="\t")
colnames(word_count) <- c("word", "count")

# plotting wordcloud
set.seed(1234)
wordcloud(words = word_count$word, freq = word_count$count, min.freq = 5, max.words=200 ,random.order=FALSE, rot.per=0.35 ,colors=brewer.pal(8, "Dark2"))

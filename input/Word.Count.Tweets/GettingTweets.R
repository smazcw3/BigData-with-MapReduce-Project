library(twitteR)
library(tm)
options(warn=-1) #To suppress warnings

setup_twitter_oauth("w5VvCSKaoWXHxFLhj26Q87Rzi",
                    "lOBvhjGPT8T7lVND7QrK5zSrn1QZlDweuBHRfjF8pwyjcKLd1k",
                    "75977001-weEFuQgZojHcw7vYaIk89DBGn7FY3NTf3N0ibrFX0",
                    "PYIylXM4nTZPY6SetaXBJgz3R8dDP3BkDkPnvKZpctqJa")

line = "Soccer"

total_tweets <- NULL
for (i in 1:3){
    if(i == 1){
        tweets <- searchTwitter(line, n = 1000)
    }
    else{
        tweets <- searchTwitter(line, n = 1000, maxID = max_id)
    }
    tweets <- twListToDF(tweets)

    max_id <- tweets$id[nrow(tweets)]
    total_tweets <- rbind(tweets, total_tweets)
}

mach_text = total_tweets$text

clean.text <- function(some_txt)
{
    some_txt = gsub("(RT|via)((?:\\b\\W*@\\w+)+)", "", some_txt)
    some_txt = gsub("@\\w+", "", some_txt)
    some_txt = gsub("[[:punct:]]", "", some_txt)
    some_txt = gsub("[[:digit:]]", "", some_txt)
    some_txt = gsub("http\\w+", "", some_txt)
    some_txt = gsub("[ \t]{2,}", "", some_txt)
    some_txt = gsub("^\\s+|\\s+$", "", some_txt)
    some_txt = gsub("amp", "", some_txt)
    #define "tolower error handling" function
    try.tolower = function(x)
    {
        y = NA
        try_error = tryCatch(tolower(x), error=function(e) e)
        if (!inherits(try_error, "error"))
            y = tolower(x)
        return(y)
    }

    some_txt = sapply(some_txt, try.tolower)
    some_txt = some_txt[some_txt != ""]
    names(some_txt) = NULL
    return(some_txt)
}

mach_text = clean.text(mach_text)

stopwords = c("a", "the", "in", "are", "of", "on", "th", "or", "it", "to", "very", "is", "his", "at","an", "i", "b", "c",
              "d", "e", "f", "g", "h", "i","j", "k", "l", "m", "n","o", "p","q","r","s","t","u","v",
              "w","x","y","z", "just", "all", "go", "so" ,"you","and" , "its", "so", "A", "B", "C", "D", "E", 
              "F", "G", "H", "I", "J")

mach_text <- removeWords(mach_text,stopwords)

writeLines(mach_text, conn<-file("input_tweets.txt"))







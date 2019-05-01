# DOCUMENTATION

## Problem Definition
Designing and implementing an efficient checker of correct language usage. Our checker crawls through pages on the Internet looking (and recording) for common usage patterns of words and phrases. It is then  able to look at text and identify words or phrases that are consistent (and inconsistent) with the crawled texts. 

Input: "Hello my name is John Snow."
	Output: : "is John Snow" 100.0
		      "hello my" 28.571 
		  	  "my name"  1.397
		  	  "name is"  1.7391
				
	Input: "This is strange so choice word."
	Output: "strange so choice word" 100.0
		    "is strange" 15.966
		    "this is"  0.0774



## Group members 
Arshitha Basavaraj (arshitha@bu.edu)
Joshua Bassin (jbassin@bu.edu)
Aixa Davila (taraviah@bu.edu)
Abhi Vora (abhivora@bu.edu)

## Implemented Features

### Crawler: 

The overview is that the crawler is starting at an initial URL and then extracting the body and all the URLs on that particular page. It then 
recursively follows all the links it has seen in a Breadth First Search manner. We have used a Arraylist to store the URLs which we have to still visit. This gets populated by all the links the crawler sees on a particular Webpage, and as soon as a URL from this list has been visited, we remove it from the ArrayList. We have multithreaded the Crawling for parallelize and make scraping the web quicker. We have used 8 threads, but that can be modified based on the computational power of a particular machine. We have also integrated the Database here to store the Links which we have already visited so that, we do not visit the same URL twice(It behaves like a Set). The input to the Crawler is a URL and the output is the text(body) of that webpage. This is then forwarded to the Tokenizer which cleans and processes this data. 

### Tokenizer: There are two parts to the tokenizer:

#### Language Detection: 

Basically, this detects the language of the string that has been received. As an additional feature, we were able to extend our checker to Arabic, Chinese and German along with English. If the string is from one of the supported languages, then it’s sent for further processing to the respective tokenizer. (Every language has it’s separate Tokenizer and Tagger) If the crawled string isn’t from any of the supported languages, then we just don’t process it into the database. 

#### Tokenization and Parts-Of-Speech Tagging: 

Within the tokenizer, the string is tagged for parts-of-speech using the Stanford CoreNLP pre-trained models. The tagged string is then split into sentences and then into words (which also contain their POS tags). As a last step, every word in the string is converted into a Token object which has two fields, 
1. Tag 
2. Word

Converting every word_posTag into a token makes it easier to populate the nodes in the database to include the pos tag as well as the corresponding word. 

The same process is repeated for all of the languages except Chinese, where we use the Stanford CoreNLP tokenizer for tokenizing and splitting sentences as well. 

### Database: 

The database is a NoSQL Document and Graph-based system. Words are stored as vertices on a graph, and bigrams are represented as edges between them. Each vertex is a document that contains the word, the part of speech, and the frequency with which it has been seen. Besides indicating the existence of a bigram between nodes, edges also contain the frequency that that particular bigram appears. Queries to the database allow for lookups on the edge, which allows for the checker to request particular bigrams.


### Checker: 

The checker is a class that has a list of suspicions and a list of phrases. There is a default constructor that initializes the suspicions list and phrases. Within this class there is a method called check that takes in a list of list of tokens that comes from the tokenizer. Here we initialize a database to get the stored suspicions. We then add the suspicions and phrases to an object called a Tuple. We created a Tuple so that we can sort on suspicions while also keeping the phrase attached to the corresponding phrases. After obtaining this list, the list is passed through a loop, and in this loop, adjacent suspicions greater than 60 are combined and their suspicions are adjusted accordingly. This creates an n gram model from our original bigram model in the initial tuple. After this, everything is sorted on the suspicions and printed with a helper function.

### Language Implementations: 
Since our Database is designed such that every node is a token with edges to its adjacent words/tokens, there is no difference in how we populate our database for different languages. Also, this theoretically, makes sure that each language has its own graph independent of the other languages. What that means is that a Chinese token based node would never be connected to an English token based node. Also, the idea behind including pos tags in our token objects is that, for example, fall (noun, the season) and fall (verb, to fall) are not the same node in a graph. There are two respective nodes given to each use of the word and which takes care of context. 

The difference would be in how we’d tokenize and tag strings from different languages. For each language, we use a pre-trained model available with the Stanford CoreNLP API for the parts-of-speech tagging. However, for sentence splitting and tokenization, we are using code that we wrote from scratch. Chinese  would be an exception though since there are no delimiters that separate sentences and words. For this we use the, CoreNLP annotator. 
Each token object is then indistinguishable from those of English token objects in structure. Afterwards, when the languages are passed in through the checker, the suspicions are drawn from the database, and then a list is printed containing all the suspicions in order.

### Speech input for checking and Verbal response of suspicious phrases: 

Here the user will be able to enter a String and the output will be the suspicious phrases in the Text which will be saved in a MP3 file in the working directory. The user can also enter a voice recorded audio file which can be a mechanism to use the checker. We have 2 classes for this, and we utilize Google Cloud dependencies and APIs to perform this function.

### Highlighter: 

The highlighter is a class that utilizes the defaultHighlightPainter class. It is implemented at the checker stage. The highlighter class created has a constructor that sets the color to highlight based on the suspicion level given from the checker. The text is highlighted as follows: suspicions of 50 exactly are not colored, suspicions from 0 to 50 are colored green, and suspicions greater than 50 are colored red. The highlighting becomes more or less opaque depending on how suspicious the chunk of text is. The closer to 0 the suspicion is, the more opaque that green color is, and the closer to 100 the suspicion is, the more opaque that red color is.
Human Feedback: The GUI presents the user with all the suspicious phrases and the user can select one of the phrases and suggest corrections which will be taken into account under the assumption that they are correct feedback. We are then going to update our edge weights in the database based on the ratio of bigram frequencies. 

### Reasonable Corrections: 

We have implemented this feature in a way that after giving the suspicious phrases, the user can select the suffix of the correct word and it will suggest the word which is most commonly seen in the Database.

For example: Input : Give your blessing money her. 
For this the user, can select the last correct word i.e blessing, and it will display the most frequent word which follows this word in the Database. 

### Android Client for Checker: 

It was really buggy so we chose not to submit this.



## relevant references and background materials.

Prakash M Nadkarni, Lucila Ohno-Machado, Wendy W Chapman; Natural language processing: an introduction, Journal of the American Medical Informatics Association, Volume 18, Issue 5, 1 September 2011, Pages 544–551, https://doi.org/10.1136/amiajnl-2011-000464

Pignaton de Freitas, Edison (2018/03/04). NoSQL real-time database performance comparison. International Journal of Parallel, Emergent and Distributed Systems, 33, 144-156. doi: 10.1080/17445760.2017.1307367

Nay Yee Lin, Khin Mar Soe, and Ni Lar Thein. Developing a Chunk-Based Grammar Checker for Translated English Sentences. Published at 25th Pacific Asia Conference on Language, Information, and Computation. Retrieved February 26, 2019, from http://aclweb.org/anthology/Y11-1026

Chritstopher D. Manning, Prabhakar Raghavan and Hinrich Schytze, Introduction to Information Retrieval, Cambridge University Press. 2008. https://nlp.stanford.edu/IR-book/

M.Crochemore, F.Mignosi, A.Restivo, S.Salemi, Data Compression using Antidictionaries, Proceedings of the IEEE, Volume 88, Issue 11, November 2000, Pages 1756-1768, https://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=892711&tag=1


## Code
All complete, working Java code used in your implementation.
All data needed by your project to run (or a simple, publicly accessible link thereto).
All testing code utilized to observe the correctness of your code.

## Work breakdown
A detailed statement of what each member of the group contributed to the project, signed by all members of the group.

### Arshitha Basavaraj: 

As part of midterm status report, I had a simple tokenizer for english working. Post midterm, I was able to improve the tokenizer to be more efficient and integrate the parts-of-speech tagger so that our token objects now have both the word and the pos tagger associated with it.
The tokenizer accepts a string from the crawler or the checker. Every string is pos tagged (using pre-trained tagger models from Stanford CoreNLP) and then its split into sentences and words as part of the tokenization. 

As part of additional features, I worked on extending the tokenizer and tagger to Arabic, Chinese and German. We chose these languages because one, we weren’t fluent in any of these languages and most importantly, we had pre-trained models available through Stanford CoreNLP.  The detailed idea behind its implementation has been mentioned above under Language Implementations section. 
As a measure of technical contribution, all of the classes within the tokenizer directory in our src directory were code contributed by me. 

### Joshua Bassin: 

I served as the scrum master for the project, and spent most of my time aiding with the architecure design and integration of the project. Additionally I took point on the logistics of hosting and maintaining the database, as well as made the overall decision as to what database schema to use. As for the software side, my main point was creating the database driver. To this end, I exposed funtions to the rest of the group to find particular bigrams, add new words and bigrams into the database, and perform max weight transversals. Furthermore, I had a minor role in helping to debug individual components. As I was among the more comfortable with idomatic java in the group, I helped with code and logic structure to aid development of the program. Finally, I helped with full system tests, as well as to perform regression and integration tests as the project developed.

### Aixa Davila: 

I started off by working on the Checker originally and stayed working on the Checker to the end. I assisted Arshitha in researching the POS tagger and how to implement it within the Checker. I had worked on a Tokenizer which ultimately failed and Arshitha’s was better. After establishing a rudimentary Checker, I increased the Checker’s accuracy by implementing an n gram model which concatenated adjacent grams with high suspicions. This was better than the original bigram model which didn’t account for anything above 2 for error checking. After this I implemented majority of the GUI. I took lead on doing the highlighting text in the GUI. The text is highlighted based on suspicion. Additionally I implemented the user input portion of the additional features and the suggestions which was also an additional feature. The GUI is not the most sound, however all of the back end code compiles and runs as it should.



### Abhi Vora 
Collaborated and deliberated with the team on how to design and analyze the architecture of the project.
Assisted in picking a Data-Structure with Joshua i.e. Undirected Graphs.
Learnt on how a Web Crawler functions. 

Initially had made a basic Crawler which scraped only the data on a page and stored URLs on that page. The classes for this function are:-
1. WebCrawler.java
2. WebCrawler_prototype_runnable.java

Scaled this crawler to the final version where it crawled the web in a Multithreaded way by visiting all the URLs it sees on a page and passing the body of a page to the Tokenizer. The classes for this are:-
1. Crawl.java
2. Spider.java
3. MultiThreading.java
4. Testing.java

Worked on Speech to Text feature and vise versa. The Classes for that were:-
1. textTospeech.java
2. speechToText.java

Unit Tested the Crawler

All of us are responsible system testing, writing the Readme.md and  



## Jira links
You may link to your Jira project for material that you wish to remain private and accessible only to the course staff.


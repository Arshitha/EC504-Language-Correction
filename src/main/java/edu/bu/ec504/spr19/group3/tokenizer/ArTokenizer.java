package edu.bu.ec504.spr19.group3.tokenizer;
import edu.bu.ec504.spr19.group3.Main;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.*;
import java.util.*;


public class ArTokenizer {

    public static List<List<Token>> getArabicTokens(String inputStr) throws IOException {

        Annotation doc = new Annotation(inputStr);

        // Setup Arabic Properties by loading them from classpath resources
        Properties props = new Properties();
        props.load(IOUtils.readerFromString("StanfordCoreNLP-arabic.properties"));
        props.setProperty("annotators", "tokenize,ssplit");
        props.setProperty("ssplit.eolonly", "true");
        props.setProperty("tokenize.whitespace", "true");

        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        corenlp.annotate(doc);

        /*
         extracts sentences from the annotated document
        */
        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
        List<String> sentenceList = new ArrayList<>();

        // converting from List<CoreMap> to List<String> format
        for(int i=0; i<sentences.size(); i++){
            CoreMap sentence = sentences.get(i);
            sentenceList.add(sentence.toString());
        }

        /*
        * Every List<String> will be a tagged sentence. Needed to process the tokens.
        * */
        List<List<String>> taggedSentenceList = new ArrayList<>();

        for(String sentence: sentenceList){
            List<String> temp = new ArrayList<>();

            // arabic tagging
            temp.add(getArabicTags(sentence));
            taggedSentenceList.add(temp);
        }
        System.out.println(taggedSentenceList);

        /*
         * Creating tokens from the tagged sentences in the taggedSentenceList
         * */
        List<List<Token>> listOLists = new ArrayList<>();
        for(List<String> sentence: taggedSentenceList){

            // splits the tagged at every space
            String[] tempSS = sentence.get(0).split(" ");

            List<String> wordList = Arrays.asList(tempSS);

            // declaring a list of token objects
            List<Token> tokenList = new ArrayList<Token>();

            // separates the tags from the words
            // and creates tokens with the tag and the word
            for(String word: wordList){
                if(word.isEmpty()) continue;

                // posTags and words delimited by forward splash
                String[] slashSplit = word.split("/");
                if (slashSplit.length > 1){
                    String posTag = slashSplit[0];
                    String token = slashSplit[1];
                    tokenList.add(new Token(posTag,token));
                }

            }
            listOLists.add(tokenList);
        }
        // System.out.println(listOLists.size());
        return listOLists;

    }

    // uses core nlp pre-trained model for arabic to tag sentences
    private static String getArabicTags(String text){
        MaxentTagger tagger = new MaxentTagger((new File(Main.ABSOLUTE_EXTERNAL_FILES_PATH, "arabic.tagger")).getAbsolutePath());
        String tagged = tagger.tagString(text);

        return tagged;
    }
}

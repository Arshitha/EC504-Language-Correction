package edu.bu.ec504.spr19.group3.tokenizer;
import edu.bu.ec504.spr19.group3.Main;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class GermanTokenizer {

    public static List<List<Token>> getGermanTokens(String inputStr) throws IOException{
        Annotation document = new Annotation(inputStr);

        // Setup German Properties by loading them from classpath resources
        Properties props = new Properties();
        props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
        props.setProperty("annotators", "tokenize,ssplit,pos");

        // these properties make sure that corenlp doesn't split the sentence or tokenize
        // doing sentence split and tokenization without the annotator
        props.setProperty("ssplit.eolonly", "true");
        props.setProperty("tokenize.whitespace", "true");

        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        corenlp.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> sentenceList = new ArrayList<>();

        /*
        * converting from List<CoreMap> to List<String> so be able to process the sentences
        * */
        for(int i=0; i<sentences.size(); i++){
            CoreMap sentence = sentences.get(i);
            sentenceList.add(sentence.toString());
        }
        // System.out.println(sentenceList);


        /*
        * Tagging the sentences in the sentence list.
        * Creates a List of List of String, where the inner list is a tagged sentence.
        * */
        List<List<String>> taggedSentenceList = new ArrayList<>();
        for(String sentence: sentenceList){
            List<String> temp = new ArrayList<>();
            temp.add(getGermanTags(sentence));
            taggedSentenceList.add(temp);
        }
        // System.out.println(taggedSentenceList);

        /*
        * Creating tokens from the tagged sentences in the taggedSentenceList
        * */
        List<List<Token>> listOLists = new ArrayList<>();
        for(List<String> sentence: taggedSentenceList){
            // split current sentence at whitespace
            String[] tempSS = sentence.get(0).split(" ");

            // convert the array to a List
            List<String> wordList = Arrays.asList(tempSS);
            List<Token> tokenList = new ArrayList<Token>();

            for(String word: wordList){

                if(word.isEmpty()) continue;

                // posTags and words are delimited by an underscore
                // therefore, splitting at underscores
                String[] underscoreSplit = word.split("_");
                if (underscoreSplit.length > 1){
                    String posTag = underscoreSplit[1];
                    String token = underscoreSplit[0];
                    tokenList.add(new Token(posTag,token));
                }

            }
            listOLists.add(tokenList);


        }
        // System.out.println(listOLists.size());
        return listOLists;

    }

    // uses core nlp pre-trained model for german to tag sentences
    private static String getGermanTags(String text){
        MaxentTagger tagger = new MaxentTagger((new File(Main.ABSOLUTE_EXTERNAL_FILES_PATH, "german-dewac.tagger")).getAbsolutePath());
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);
        java.lang.String tagged = tagger.tagString(text);

        return tagged;
    }
}

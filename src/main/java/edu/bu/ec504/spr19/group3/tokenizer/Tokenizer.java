package edu.bu.ec504.spr19.group3.tokenizer;
import edu.bu.ec504.spr19.group3.Main;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tokenizes the string received from the crawler via the POS tagger.
 */
public class Tokenizer {

    /**
     * Converts the input string into a list of token objects sentence by sentence.
     * @param inputStr String received from POS tagger.
     * @return A list of list of token objects.
     */
    public static List<List<Token>> getTokens(String inputStr){

        /*
         * removes everything other than the alphanumeric characters, periods and spaces and URLS.
         * Print statements just for debugging and visualization.
         * uncomment it if required.
         */

        // System.out.println("**********Before Replacement**********");
        // System.out.println(inputStr);
        inputStr = inputStr.replaceAll("([^a-zA-Z\\s\\.'])|(http:\\/\\/.*?(?=[\\s\"']|$))|(www\\..*?(?=[\\s\"']|$))", "");
        inputStr = inputStr.replaceAll("(\\.+)",".");
        // System.out.println("**********After Replacement**********");
        // System.out.println(inputStr);


        // POS tags the cleaned up string
        inputStr = getEnglishTags(inputStr);


        List<List<Token>> listOLists = new ArrayList<>();
        List<String> sentenceList = Arrays.asList(inputStr.split("\\._\\."));

        /*
         * every element in this arrayList would be a sentence. Tokenizing every sentence
         * within the arrayList would give us a list of tokenized words.
         *
         * This is stored and returned as a list of lists.
         */
        ArrayList<Token> tagArr = new ArrayList<Token>();

        for (String ss: sentenceList){

            String[] tempSS;

            // creating word_pos arrayList
            tempSS = ss.replaceAll("^\\s+", "").replaceAll("\\s+$", "").split("\\s");

            List<String> wordList = Arrays.asList(tempSS);
            List<Token> tokenList = new ArrayList<Token>();

            for(String word: wordList){
                if(word.isEmpty()) continue;

                // splits the word at every underscore
                String[] underscoreSplit = word.replaceAll("\\.", "").split("_");

                // creating tokens
                if (underscoreSplit.length > 1){
                    String posTag = underscoreSplit[1].toUpperCase();
                    String token = underscoreSplit[0].toLowerCase();
                    tokenList.add(new Token(posTag,token));
                }

            }
            listOLists.add(tokenList);
        }
//        System.out.println(listOLists);

        return listOLists;
    }

    private static String getEnglishTags(String text){
        // loading english model file
        MaxentTagger tagger = new MaxentTagger((new File(Main.ABSOLUTE_EXTERNAL_FILES_PATH, "english-left3words-distsim.tagger")).getAbsolutePath());
        String tagged = tagger.tagString(text);

        // System.out.println(tagged);
        return tagged;
    }

}


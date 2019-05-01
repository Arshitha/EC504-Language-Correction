package edu.bu.ec504.spr19.group3.tokenizer;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import java.io.*;
import java.util.*;

public class ChineseTokenizer {
    public static List<List<Token>> getChineseTokens(String inputStr) throws IOException{

        Annotation document = new Annotation(inputStr);

        // Setup Chinese Properties by loading them from classpath resources
        Properties props = new Properties();
        props.load(IOUtils.readerFromString("StanfordCoreNLP-chinese.properties"));
        props.setProperty("annotators","tokenize,ssplit,pos");
        StanfordCoreNLP corenlp = new StanfordCoreNLP(props);
        corenlp.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        List<List<Token>> listOLists = new ArrayList<>();

        for(CoreMap sentence: sentences) {
            List<Token> sentTokenList = new ArrayList<>();

            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                Token tempToken;

                // this is the text of the token
                String word = token.get(TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);

                // print statements for visualization
                // System.out.printf("%s %s\n",pos,word);
                // System.out.println("###########");

                tempToken = new Token(pos,word);
                sentTokenList.add(tempToken);

            }
            // System.out.println("**********EndOfSentence**********");
            listOLists.add(sentTokenList);

        }
        // System.out.println(listOLists.size());

        return listOLists;

    }
}

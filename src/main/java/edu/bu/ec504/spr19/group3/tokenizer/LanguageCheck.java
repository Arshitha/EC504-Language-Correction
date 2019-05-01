package edu.bu.ec504.spr19.group3.tokenizer;
import javax.swing.plaf.synth.SynthScrollBarUI;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

public class LanguageCheck {

    public static List<List<Token>> getTokenizer(String body) throws IOException {
        List<List<Token>> listofTokens = new ArrayList<>();
        int language = LangCheck(body);
        //System.out.println(language);

        // detects the language of the string and passes it on to the appropriate tokenizer
        if(language==0){
            listofTokens = Tokenizer.getTokens(body);
            //System.out.println("This is English");

        }else if(language==1){
            listofTokens = ArTokenizer.getArabicTokens(body);
            //System.out.println("This is Arabic");

        }else if(language==2){
            listofTokens = ChineseTokenizer.getChineseTokens(body);
            //System.out.println("This is Chinese");
        } else if (language==3){
            listofTokens = GermanTokenizer.getGermanTokens(body);
            //System.out.println("This is German");
        }
        //System.out.println("here");

        return listofTokens;

    }

    public static int LangCheck(String inputStr){
        boolean[] decisions = new boolean[4];

        // 0th index is assigned to English
        decisions[0] = isEnglish(inputStr);
        // 1st index is assigned to Arabic
        decisions[1] = isArabic(inputStr);
        // 2nd index is assigned to Chinese
        decisions[2] = isChinese(inputStr);
        // 3rd index is assigned to German
        decisions[3] = isGerman(inputStr);

        // true count keeps a count of how many different language strings we have in our input string
        int trueCount = 0;
        for(int i=0; i<decisions.length; i++){
            if(decisions[i]) {
                trueCount += 1;
            }
        }

        /*
        * Order of Precedence of languages: ARABIC > CHINESE > GERMAN > ENGLISH
        * If more than one language in the input text, then set english to false and pick the first
        * language that's true to tokenize the text.
        * Else if the input string only contains one language that language tokenizer is used.
        * */
        if(trueCount>1){
            for(int i=1; i<decisions.length;i++){
                if (decisions[i]) return i;
            }
        } else if (trueCount==1){
            for(int i=0; i<decisions.length;i++){
                if (decisions[i]) return i;
            }
        }

        // -1 returned if the string doesn't contain any of the four supported languages
        return -1;

    }

    /*
    * Check the documentation for more info
    * https://docs.oracle.com/javase/8/docs/api/java/lang/Character.UnicodeScript.html#HAN
    * */
    private static boolean isArabic(String input){
        for (int i = 0; i < input.length(); ) {
            int codepoint = input.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.ARABIC) {
                return true;
            }
        }
        return false;
    }

    private static boolean isChinese(String s) {
        for (int i = 0; i < s.length(); ) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }

    /*
    * German differs from English in only these characters. ä,ö,ß,ü
    * More info: http://esl.fis.edu/grammar/langdiff/german.htm
    * */
    private static boolean isGerman(String input){
        Pattern pattern = Pattern.compile("[äößü]");
        Matcher matcher = pattern.matcher(input);

        return matcher.find();
    }

    private static boolean isEnglish(String input){
        Pattern pattern = Pattern.compile("[a-zA-z]");
        Matcher matcher = pattern.matcher(input);

        return matcher.find();
    }
    
}

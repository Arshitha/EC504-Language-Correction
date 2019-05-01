package edu.bu.ec504.spr19.group3.tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagger {

    public static String random(String text) {
        MaxentTagger tagger = new MaxentTagger("/Users/arshitha/Desktop/OneDesk/Boston University/Sem 4/EC504_project/taggers/out/production/taggers/chinese-distsim.tagger");
        String tagged = tagger.tagString(text);

        System.out.println(tagged);
        return tagged;
    }
}

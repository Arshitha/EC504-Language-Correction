package edu.bu.ec504.spr19.group3.tokenizer;

public class Token {
    public String tag;
    public String token;

    public Token(String pos, String word){
        this.tag = pos;
        this.token = word;
    }
}

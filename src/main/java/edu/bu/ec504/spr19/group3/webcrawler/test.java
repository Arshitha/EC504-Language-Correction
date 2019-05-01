package edu.bu.ec504.spr19.group3.webcrawler;

import java.io.IOException;
import java.lang.reflect.Array;

public class test {
    public static void main (String[] args) throws IOException {

//        Spider test1 = new Spider();
//        test1.Going_Through_Links("http://lingscars.com");
        int n = 8; // Number of threads
        String[] start_links = {"https://en.wikipedia.org/wiki/Main_Page","https://www.lingscars.com/","https://www.reddit.com/","https://www.cnn.com/","http://www.gutenberg.org/ebooks/59133?msg=welcome_stranger","http://bu.edu","https://www.amazon.com/","https://genius.com/"};
        for (int i=0; i<8; i++)
        {
            Thread object = new Thread(new MultiThreading(start_links[i]));
            object.start();
        }


    }
}

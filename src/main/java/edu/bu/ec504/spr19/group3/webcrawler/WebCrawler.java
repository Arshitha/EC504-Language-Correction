/** THIS is the file which will be instantiated from the Main file when we put all our files together. **/


package edu.bu.ec504.spr19.group3.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WebCrawler {

    public static String Crawling() throws IOException {

        int max_pages = 100;// MAX Pages to visit. This will be modified based on how many pages approx tally up to 1TB of Data.

        Set<String> PagesVisited = new HashSet<String>(); // Set of URLs VISITED. Set since we want to avoid same URLs.

        List<String> Next_URL = new LinkedList<String>(); // Linked List of URLs to Visit
        String bodyText="";

//      Validate.isTrue((args.length > 0 && args.length <2),"Enter valid URL"); // This will be used to check the user's input in the latter
//      stages when the user can enter any starting URL.

        String start_URL = "http://www.gutenberg.org/files/59133/59133-h/59133-h.htm"; // Hard coded the initial Start URL
        //String start_URL = "http://news.ycombinator.com/"; //These are a few test case URLs.
        //String start_URL = "http://www.gutenberg.org";

        PagesVisited.add(start_URL);
        try {

            Document doc = Jsoup.connect(start_URL.toString()).data("query", "Java").userAgent("Mozilla").get();

            Elements LinksOnPage = doc.select("a[href]");

            for (Element URL : LinksOnPage) {

                String URL_text = URL.attr("abs:href");
                System.out.println(URL_text);

                if (PagesVisited.contains(URL_text)) {
                } else Next_URL.add(URL_text);
            }

            bodyText = doc.body().text();

//            PrintWriter writer = new PrintWriter("crawler_output.txt", "UTF-32");
//            writer.print(bodyText);
//            writer.close();


            byte[] utf16Bytes = bodyText.getBytes("UTF-32");
            //System.out.println(((utf16Bytes.length) / 1000000.0) + "MB of text on this URL");

            return bodyText;
        } catch (Exception | Error ignored) { // SOME URLS WILL NOT BE PARSABLE THUS THIS IS REQUIRED.
            System.out.println("URL Format not correct.");
            return bodyText;
        }

    }

}






/**
This is the same code just with no Main Function.
 **/



//    public static void main(String[] args) throws IOException {
//        int max_pages = 100;// MAX Pages to visit. This will be modified based on how many pages approx tally up to 1TB of Data.
//
//        Set<String> PagesVisited = new HashSet<String>(); // Set of URLs VISITED. Set since we want to avoid same URLs.
//
//        List<String> Next_URL = new LinkedList<String>(); // Linked List of URLs to Visit
//
////      Validate.isTrue((args.length > 0 && args.length <2),"Enter valid URL"); // This will be used to check the user's input in the latter
////      stages when the user can enter any starting URL.
//
//        String start_URL = "http://www.gutenberg.org/files/59133/59133-h/59133-h.htm"; // Hard coded the initial Start URL
//        //String start_URL = "http://news.ycombinator.com/"; //These are a few test case URLs.
//        //String start_URL = "http://www.gutenberg.org";
//
//        PagesVisited.add(start_URL);
//        try {
//
//            Document doc = Jsoup.connect(start_URL.toString()).data("query", "Java").userAgent("Mozilla").get();
//
//            Elements LinksOnPage = doc.select("a[href]");
//
//            for (Element URL : LinksOnPage) {
//
//                String URL_text = URL.attr("abs:href");
//                System.out.println(URL_text);
//
//                if (PagesVisited.contains(URL_text)) {
//                }
//                else Next_URL.add(URL_text);
//            }
//
//            String bodyText = doc.body().text();
//
//            PrintWriter writer = new PrintWriter("crawler_output.txt", "UTF-32");
//            writer.print(bodyText);
//            writer.close();
//
//            byte[] utf16Bytes= bodyText.getBytes("UTF-32");
//            System.out.println(((utf16Bytes.length)/1000000.0) + "MB of text on this URL");
//
//        } catch (Exception | Error ignored ) { // SOME URLS WILL NOT BE PARSABLE THUS THIS IS REQUIRED.
//            System.out.println( "URL Format not correct.");
//        }
//
//    }





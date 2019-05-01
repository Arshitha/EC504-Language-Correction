package edu.bu.ec504.spr19.group3.webcrawler;

import edu.bu.ec504.spr19.group3.database.Database;
import edu.bu.ec504.spr19.group3.tokenizer.LanguageCheck;
import edu.bu.ec504.spr19.group3.tokenizer.Token;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;

public class Crawl {


    public static List<String> Links_Found_on_Webpage = new LinkedList<String>();
    public static String body;

    public void ScanWebpage(String url_to_visit) throws IOException {

        List<List<Token>> ListOfLists = new ArrayList<>();
        try {


            body = "";
            int size = 0;
            Connection connection = Jsoup.connect(url_to_visit).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21");

            Document Webpage = connection.get();
            Connection.Response response = connection.response();
            if (response.statusCode() == 200) {
                body = Webpage.body().text();

                Elements Links_on_page = Webpage.select("a[href]");

                for (Element i : Links_on_page) {

                    Links_Found_on_Webpage.add(i.absUrl("href"));
                }

                byte[] utf8Bytes = Webpage.toString().getBytes();
                Database.getDatabase().incrementSize((long)utf8Bytes.length);
                //Database.getDatabase().incrementSize((long)size);
                //System.out.println(utf8Bytes.length);
                System.out.println(body);
                //return body;

                ListOfLists = LanguageCheck.getTokenizer(body);
                for (List<Token> tokenList : ListOfLists) {
                    for (int i = 0; i < tokenList.size() - 1; i++) {
                        Database.getDatabase().createOrUpdateEdge(tokenList.get(i).token, tokenList.get(i).tag, tokenList.get(i + 1).token, tokenList.get(i + 1).tag);
                    }
                }


            }
            //else return body;
        } catch (IOException ioe) {
        }
    }


    public List<String> get_All_Links() {
        return Links_Found_on_Webpage;
    }


}

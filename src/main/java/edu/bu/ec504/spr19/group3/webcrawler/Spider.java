package edu.bu.ec504.spr19.group3.webcrawler;

import edu.bu.ec504.spr19.group3.database.Database;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Spider extends Thread{
    public static Set<String> PagesVisited = new HashSet<String>();
    public static ArrayList<String> PagesToVisit = new ArrayList<String>();
    public static BigInteger MaxPages = BigInteger.valueOf(1000);
    public static BigInteger PagesVisitedCount = BigInteger.valueOf(0);

    public  void Going_Through_Links(String url) throws IOException {

        while(PagesVisitedCount.compareTo(MaxPages)==-1) {
//BigInteger bi = BigInteger.valueOf(myInteger.intValue());
            String current_URL;
            Crawl instance = new Crawl();

            if (PagesToVisit.isEmpty()){
                current_URL = url;
                //PagesVisited.add(current_URL);
                Database.getDatabase().addLink(current_URL);
            }
            else {
                current_URL =  URL_to_Visit();
            }

            instance.ScanWebpage(current_URL);
            PagesToVisit.addAll(instance.get_All_Links());


            //System.out.println(current_URL); // TESTING
            //System.out.println(PagesVisitedCount); //TESTING
            PagesVisitedCount = PagesVisitedCount.add(BigInteger.valueOf(1));
        }

        //System.out.println(PagesVisited.size()); //TESTING
    }


    public static String URL_to_Visit() {

        String next_URL;

        do {
            next_URL = PagesToVisit.remove(0);
        } //while (PagesVisited.contains(next_URL));
            while(Database.getDatabase().alreadySeenLink(next_URL));
        //PagesVisited.add(next_URL);
        Database.getDatabase().addLink(next_URL);
        return next_URL;


    }


}
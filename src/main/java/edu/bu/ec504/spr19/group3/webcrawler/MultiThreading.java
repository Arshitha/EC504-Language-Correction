package edu.bu.ec504.spr19.group3.webcrawler;


public class MultiThreading implements Runnable {
    String url;

     MultiThreading(String link){
        this.url=link;

    }


    public void run()  {
        try{
            Spider test1 = new Spider();
            test1.Going_Through_Links(url);
        }
        catch (Exception e){
            }
        }
}



package edu.bu.ec504.spr19.group3.list;

import edu.bu.ec504.spr19.group3.checker.Checker;
import edu.bu.ec504.spr19.group3.database.Database;
import edu.bu.ec504.spr19.group3.database.Node;
import edu.bu.ec504.spr19.group3.tokenizer.LanguageCheck;
import edu.bu.ec504.spr19.group3.tokenizer.Token;
import edu.bu.ec504.spr19.group3.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class MyJList extends JFrame
{

    private JList<String> SuspicionsList;

    public String feedback = "";

    public String TF="";

    //for Tuples from checker
    public MyJList(List<Checker.Tuple> t)
    {
        //get default list model
        DefaultListModel<String> m = new DefaultListModel<>();

        //add elements from checker into list model
        for(int i=0; i<t.size();i++)
        {
            m.addElement(t.get(i).phrase);
        }

        //create the list
        SuspicionsList = new javax.swing.JList<>(m);

        //add to see if a value was selected
        SuspicionsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (!e.getValueIsAdjusting())
                {
                    String selectedValue = SuspicionsList.getSelectedValue();
                    System.out.println(selectedValue);
                    //printSelected(selectedValue);

                    //feedback and TF is  stored as the selected value for later use
                    feedback=selectedValue;
                    TF=selectedValue;
                }
            }
        });

        add(new JScrollPane(SuspicionsList));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Suspicions");
        this.setSize(200, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public String printSelected(String s)
    {
        return s;
    }


    //User feedback for input on whether or not an n gram/ text is suspicious
    public void userFeedback(String selected, boolean isNotSus)
    {
        //create database
        Database db = Database.getDatabase();

        List<List<Token>> listOfLists = new ArrayList<>();
        try{
        listOfLists = LanguageCheck.getTokenizer(selected);}catch(Exception e){}

        //if the user detects a certain phrase as not suspicious, we will decrement the current suspicion by adding
        if(!isNotSus)
        {
            //iterate through tokenized list
            for(int i=0; i<listOfLists.size();i++)
            {
                for(int j=0; j<listOfLists.get(i).size()-1; j++)
                {
                    //add to the database 5 times to change
                    for(int count=0; i<5;i++)
                    {
                        db.createOrUpdateEdge(listOfLists.get(i).get(j).token, listOfLists.get(i).get(j).tag, listOfLists.get(i).get(j+1).token, listOfLists.get(i).get(j+1).tag);
                    }
                }
            }
        }

        //do nothing, since if it was not found less suspicious, then it remains suspicious. Adding it to our database makes it so that it becomes less suspicious.
    }

    //User asking for suggestions based on what they select
    public String suggestions(String selected)
    {
        //create database
        Database db = Database.getDatabase();

        //suggest the top corrections
        List<List<Token>> listOfLists = new ArrayList<>();
        try{
            listOfLists = LanguageCheck.getTokenizer(selected);}catch(Exception e){}

        Node a = db.getBiggestChild(listOfLists.get(0).get(0).token, listOfLists.get(0).get(0).tag);

        String s = listOfLists.get(0).get(0).token + " " + a.getWord();

        System.out.print("This is the best suggestion for the first word");
        return s;

    }

}

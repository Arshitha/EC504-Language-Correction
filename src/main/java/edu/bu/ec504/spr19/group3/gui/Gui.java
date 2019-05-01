package edu.bu.ec504.spr19.group3.gui;

import edu.bu.ec504.spr19.group3.checker.Checker;
import edu.bu.ec504.spr19.group3.database.Database;
import edu.bu.ec504.spr19.group3.highlighter.MyHighlighter;
import edu.bu.ec504.spr19.group3.list.MyJList;
import edu.bu.ec504.spr19.group3.tokenizer.LanguageCheck;
import edu.bu.ec504.spr19.group3.tokenizer.Token;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Gui {

    private JLabel title;
    private JButton check;
    private JEditorPane textBox;
    private JPanel mainPanel;

    private String text;


    private class checkText implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            text = textBox.getText().replaceAll("\n", " ");

            //highlight text
            MyHighlighter hlt = new MyHighlighter(Color.yellow);
            //feed in input text and tokens from tokenizer
            List<List<Token>> listOfLists = new ArrayList<>();
            try {
                listOfLists = LanguageCheck.getTokenizer(text);
            } catch (Exception E) {
            }

            //run the checker
            Checker c = new Checker();
            List<Checker.Tuple> t = c.Check(listOfLists, 60);

            String textString = "";
            for (Checker.Tuple tuple : t) {
                textString += tuple.phrase + ". ";
            }
            //highlight
            try {
                hlt.highlightList(textString, t);
            } catch (Exception E) {
            }


            //implement list choice
            ArrayList<Checker.Tuple> truf = new ArrayList<>();

            //add values to true and false list
            truf.add(new Checker.Tuple(100, "true"));
            truf.add(new Checker.Tuple(100, "false"));

            //first list takes list from checker of the Tuples in the final list
            MyJList sus = new MyJList(t);
            //second list takes list of true or false options from made list above
            MyJList tf = new MyJList(truf);

            //select from list of suspicions, and select from true or false list
            String susses = sus.feedback;
            String trueOrFalse = tf.TF;
            boolean b;

            if(trueOrFalse == "true")
            {
                b=true;
            }
            else
            {
                b=false;
            }

//            System.out.println (susses);
//            System.out.println(b);

            //method to add to database based on what was chosen. If user says true, that means it is suspicious, if they say false then the database is updated.
           sus.userFeedback(susses, b);

            //method for suggestions, after chosing from the list of grams and suspicions, it returns the first word in that ngram and its most probable next word
            String sugg = sus.suggestions(susses);
            System.out.println(sugg);
        }
    }

    public void run() {
        JFrame frame = new JFrame("EC504 NLP");
        frame.setContentPane(new Gui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }


    public Gui() {
        check.addActionListener(new checkText());
    }
}

package edu.bu.ec504.spr19.group3.highlighter;
import edu.bu.ec504.spr19.group3.checker.Checker;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MyHighlighter extends DefaultHighlighter.DefaultHighlightPainter
{

    //constructor to put color
    public MyHighlighter(Color c)
    {
        super(c);
    }


    //"factory" constructor to pass in a suspicion
    public static MyHighlighter MyHighlighterFactory(Float suspicion)
    {
        //Cast float suspicion into integer
        int sus = suspicion.intValue();

        //Where RGB is the red, green, and blue hue, and a is an alpha for opacity
        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;

        //alpha dictates transparency, if suspicious it will be red, if not, then it is green
        a = Math.abs(50 - sus) * 3;
        r = sus > 50 ? (127+(sus/100 *128)) : 0;
        g = sus < 50 ? (127+((100-sus)/100 * 128)) : 0;
        b = 0;

        //create new color based on suspicion
        Color c = new Color(r,g,b,a);

        //call constructor to create new highlighter color
        return new MyHighlighter(c);
    }

    //highlight functions Need a JEditorPane to do this
    public void highlight(JTextComponent textComp, String pattern) throws Exception {
        //create a new highlighter object
        Highlighter hilite = textComp.getHighlighter();
        Document doc = textComp.getDocument();
        String text = doc.getText(0, doc.getLength());
        int position = 0;

        //search for the pattern to highlight
        while ((position = text.indexOf(pattern, position)) >= 0) {
            hilite.addHighlight(position, position + pattern.length(), this);
            position += pattern.length();
        }
    }

    //highlight based on indices
    public void highlightIndex(JTextComponent textComp,int begin, int length)throws Exception {
        //create a new highlighter object
        Highlighter hilite = textComp.getHighlighter();
        Document doc = textComp.getDocument();
        String text = doc.getText(0, doc.getLength());

        //highlight from starting index to next index
        hilite.addHighlight(begin,begin+length, this);
    }

    //highlight given a list of list of tokens from checker
    public JEditorPane highlightList(String text, List<Checker.Tuple> tokens)throws Exception
    {
        //create all the needed frames to show text that is highlighted
        JFrame frame = new JFrame();
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JEditorPane jep = new JEditorPane();
        jep.setText(text);
        frame.add(jep);
        frame.pack();
        frame.setVisible(true);

        //iterate through list given by checker and color everything accordingly
        for(int j=0; j<tokens.size();j++)
            {
                //recolor the highlighter based on the suspicion level of the text
                MyHighlighter h = MyHighlighterFactory(tokens.get(j).suspicion);
                //highlight it
                h.highlight(jep, tokens.get(j).phrase);
            }

//        String x = jep.getText();
//        System.out.println(x);

        return jep;
    }

//    public String mouseReleased(MouseEvent e, JTextComponent textComp) {
//        if (textComp.getSelectedText() != null) { // See if they selected something
//            String s = textComp.getSelectedText();
//            // Do work with String s
//            return s;
//        }


}

package edu.bu.ec504.spr19.group3.gui;

import edu.bu.ec504.spr19.group3.checker.Checker;
import edu.bu.ec504.spr19.group3.database.Database;
import edu.bu.ec504.spr19.group3.tokenizer.Tokenizer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnifiedGui {
    private JPanel mainPanel;
    private JButton checkButton;
    private JLabel titleLabel;
    private JEditorPane textPanel;
    private JButton resetButton;
    private JSlider thresholdSlider;
    private JLabel thresholdLabel;
    private JList suspicionList;
    private JLabel thresholdValueLabel;
    private JLabel replaceLabel;
    private JLabel replaceLabelHeader;
    private DefaultListModel listModel;

    private Checker checker = new Checker();
    private List<List<edu.bu.ec504.spr19.group3.tokenizer.Token>> tokens;
    private List<Checker.Tuple> tuples;

    private UnifiedGui() {
        listModel = new DefaultListModel();
        suspicionList.setModel(listModel);
        resetButton.setEnabled(false);
        resetButton.addActionListener(new ResetAction());
        checkButton.addActionListener(new CheckAction());
        thresholdSlider.addChangeListener(new ThresholdChange());
        suspicionList.addMouseListener(new SuspicionListClick());
    }

    public static void run() {
        JFrame frame = new JFrame("EC504 NLP");
        frame.setContentPane(new UnifiedGui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void printText(List<Checker.Tuple> tuples) {
        List<HighlighterStruct> highlighters = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (Checker.Tuple tuple : tuples) {
            if (tuple == null) continue;
            if (stringBuilder.length() == 0) {
                highlighters.add(new HighlighterStruct(stringBuilder.length(), tuple.phrase.length(), tuple.suspicion, thresholdSlider.getValue()));
                stringBuilder.append(tuple.phrase).append(" ");
            } else {
                Matcher matcher = Pattern.compile("(.*?)\\s(?<text>.*)").matcher(tuple.phrase);
                if (matcher.find()) {
                    highlighters.add(new HighlighterStruct(stringBuilder.length(), matcher.group("text").length(), tuple.suspicion, thresholdSlider.getValue()));
                    stringBuilder.append(matcher.group("text")).append(" ");
                }
            }
        }
        textPanel.setText(stringBuilder.toString());
        for (HighlighterStruct highlighterStruct : highlighters) {
            highlighterStruct.highlight(textPanel);
        }
    }

    private DefaultHighlighter.DefaultHighlightPainter getHighlighter(float suspicion, int threshold) {
        Color highlighterColor = new Color((int) (255 - ((suspicion / 100) * 30.0f)), 0, 0, suspicion > threshold ? 255 : 0);
        return new DefaultHighlighter.DefaultHighlightPainter(highlighterColor);
    }

    private void showSuspicions(List<Checker.Tuple> tuples) {
        List<Checker.Tuple> clonedTuples = new ArrayList<>();
        for (Checker.Tuple tuple : tuples) {
            if (tuple != null) clonedTuples.add(new Checker.Tuple(tuple.suspicion, tuple.phrase));
        }
        clonedTuples.sort(new Comparator<Checker.Tuple>() {
            @Override
            public int compare(Checker.Tuple o1, Checker.Tuple o2) {
                return (int) o2.suspicion -  (int) o1.suspicion;
            }
        });

        listModel.clear();
        for (Checker.Tuple tuple : clonedTuples) {
            listModel.addElement(tuple.phrase + ": " + tuple.suspicion);
        }
    }

    public class CheckAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resetButton.setEnabled(true);
            checkButton.setEnabled(false);
            textPanel.setEditable(false);
            tokens = Tokenizer.getTokens(textPanel.getText());
            tuples = checker.Check(tokens, 50);
            textPanel.setText("");

            printText(tuples);
            showSuspicions(tuples);
//            checker.sortAndPrint(tuples);
        }
    }

    public class ResetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resetButton.setEnabled(false);
            checkButton.setEnabled(true);
            textPanel.setText("");
            listModel.clear();
            textPanel.setEditable(true);
            textPanel.getHighlighter().removeAllHighlights();
            replaceLabel.setText("");
        }
    }

    public class ThresholdChange implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            thresholdValueLabel.setText("" + thresholdSlider.getValue());
            printText(tuples);
        }
    }

    public class SuspicionListClick extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (e.getClickCount() == 1) {
                Matcher matcher = Pattern.compile("(?<first>.+?)\\s(?<second>.+?):(.*)").matcher(listModel.getElementAt(suspicionList.locationToIndex(e.getPoint())).toString());
                if (matcher.find()) {
                    for (List<edu.bu.ec504.spr19.group3.tokenizer.Token> tokenList : tokens) {
                        for (int i = 0; i < tokenList.size() - 1; i++) {
                            if (tokenList.get(i).token.equals(matcher.group("first")) && tokenList.get(i + 1).token.equals(matcher.group("second"))) {
                                replaceLabel.setText(matcher.group("first") + " " + Database.getDatabase().getBiggestChild(tokenList.get(i).token, tokenList.get(i).tag).getWord());
                                return;
                            }
                        }
                    }
                }
            }
            else if (e.getClickCount() == 2) {
                Matcher matcher = Pattern.compile("(?<first>.+?)\\s(?<second>.+?)\\s*:(.*)").matcher(listModel.getElementAt(suspicionList.locationToIndex(e.getPoint())).toString());
                if (matcher.find()) {
                    for (List<edu.bu.ec504.spr19.group3.tokenizer.Token> tokenList : tokens) {
                        for (int i = 0; i < tokenList.size() - 1; i++) {
                            if (tokenList.get(i).token.equals(matcher.group("first")) && tokenList.get(i + 1).token.equals(matcher.group("second"))) {
                                for (int counter = 0; counter < 3; counter++) {
                                    Database.getDatabase().createOrUpdateEdge(tokenList.get(i).token, tokenList.get(i).tag, tokenList.get(i + 1).token, tokenList.get(i + 1).tag);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private class HighlighterStruct {
        private int start;
        private int length;
        private DefaultHighlighter.DefaultHighlightPainter highlighter;

        public HighlighterStruct(int start, int length, float suspicion, int threshold) {
            this.start = start;
            this.length = length;
            this.highlighter = getHighlighter(suspicion, threshold);
        }

        public void highlight(JEditorPane textbox) {
            try {
                textbox.getHighlighter().addHighlight(start, start + length, highlighter);
            } catch (BadLocationException ex) {}
        }
    }
}

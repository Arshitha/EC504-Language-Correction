package edu.bu.ec504.spr19.group3;

import edu.bu.ec504.spr19.group3.checker.Checker;
import edu.bu.ec504.spr19.group3.database.Database;
import edu.bu.ec504.spr19.group3.gui.Gui;
import edu.bu.ec504.spr19.group3.gui.UnifiedGui;
import edu.bu.ec504.spr19.group3.speechRecognition.speechToText;
import edu.bu.ec504.spr19.group3.speechRecognition.textToSpeech;
import edu.bu.ec504.spr19.group3.tokenizer.LanguageCheck;
import edu.bu.ec504.spr19.group3.tokenizer.TextReader;
import edu.bu.ec504.spr19.group3.tokenizer.Token;
import edu.bu.ec504.spr19.group3.tokenizer.Tokenizer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static final File ABSOLUTE_EXTERNAL_FILES_PATH = new File("/home/jbassin/Documents/College/Spring_2019/EC504/finalProject/edu.bu.ec504.spr19.group3/external_files");

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);
        if (args.length == 0) {
            UnifiedGui.run();
        }
        else if (args[0].equals("-a")) {
            if (args.length != 3) {
                System.out.println("Not enough arguments!");
                System.exit(-1);
            }
            try {
                String text = speechToText.run(args[1]);
                List<List<Token>> tokens = Tokenizer.getTokens(text);
                Checker checker = new Checker();
                textToSpeech.run(checker.stringify(checker.Check(tokens, 50)), args[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else  {
            try {
                List<List<Token>> tokens = Tokenizer.getTokens(String.join(" ", args));
                Checker checker = new Checker();
                checker.sortAndPrint(checker.Check(tokens, 50));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
            System.exit(0);
        }
    }
}

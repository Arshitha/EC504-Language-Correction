package edu.bu.ec504.spr19.group3.tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextReader{

    public static String readAsString(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}

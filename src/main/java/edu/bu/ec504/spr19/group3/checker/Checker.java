package edu.bu.ec504.spr19.group3.checker;


import edu.bu.ec504.spr19.group3.database.Database;
import edu.bu.ec504.spr19.group3.database.Edge;
import edu.bu.ec504.spr19.group3.tokenizer.Token;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Checker
{

    //store lists of suspicions, store phrases, unneeded now after edits, but will keep.
    private ArrayList<ArrayList<Float>> suspicions;
    private ArrayList<ArrayList<String>> phrases;

    public ArrayList<Tuple> t;

    //tokenize the given string before putting it through the checker
    //tokenizer outputs a list of lists that contain strings

    //no need for any of these, should remove, but they couple be helpful in the future
    public Checker()
    {
        suspicions = new ArrayList<ArrayList<Float>>();
        phrases = new ArrayList<ArrayList<String>>();
    }

    public ArrayList<ArrayList<Float>> getSuspicions()
    {
        return suspicions;
    }

    public ArrayList<ArrayList<String>> getPhrases()
    {
        return phrases;
    }

    public List<Tuple> Check(List<List<edu.bu.ec504.spr19.group3.tokenizer.Token>> tokens, float threshold) {
        Database db = Database.getDatabase();
        List<Tuple> tuples = new ArrayList<>();

        for (List<edu.bu.ec504.spr19.group3.tokenizer.Token> sentence : tokens) {
            List<Tuple> sentenceTuples = new ArrayList<>();
            for (int i = 0; i < sentence.size() - 1; i++) {
                Edge edge = db.getBigram(sentence.get(i).token, sentence.get(i).tag, sentence.get(i + 1).token, sentence.get(i + 1).tag);
                sentenceTuples.add(new Tuple(edge.suspicion(), edge.getFrom().getWord() + " " + edge.getTo().getWord()));
            }

            List<Tuple> compressedTuple = new ArrayList<>();
            Tuple tupleBuilder = null;
            for (Tuple tuple : sentenceTuples) {
                if (tupleBuilder == null){
                    tupleBuilder = tuple;
                    continue;
                }
                if (tupleBuilder.suspicion >= threshold && tuple.suspicion >= threshold) {
                    tupleBuilder = new Tuple((tupleBuilder.suspicion + tuple.suspicion) / 2, tupleBuilder.phrase + " " + tuple.phrase.split("\\s")[1]);
                } else {
                    compressedTuple.add(tupleBuilder);
                    tupleBuilder = tuple;
                }
            }
            tuples.addAll(compressedTuple);
            tuples.add(tupleBuilder);
        }
        return tuples;
    }

    //helper function to sort and then print the data in the tuple
    public void sortAndPrint(List<Tuple> tuples)
    {
        //use a function to compare values and print in reverse order 100->0,
        tuples.sort(new Comparator<Tuple>() {
            @Override
            public int compare(Tuple o1, Tuple o2) {
                return (int) o2.suspicion -  (int) o1.suspicion;
            }
        });

        //print the suspicions and phrases in the tuple
        for (Tuple tuple: tuples) {
            System.out.println(tuple.phrase + " " + tuple.suspicion);
        }
    }

    public String stringify(List<Tuple> tuples) {
        tuples.sort(new Comparator<Tuple>() {
            @Override
            public int compare(Tuple o1, Tuple o2) {
                return (int) o2.suspicion -  (int) o1.suspicion;
            }
        });

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("List of all phrases in order of suspicions. ");
        for (Tuple tuple: tuples) {
            if (tuple.suspicion < 40) break;
            stringBuilder.append("The phrase \"").append(tuple.phrase).append("\" has a suspicion of ").append((int) tuple.suspicion).append(". ");
        }
        return stringBuilder.toString();
    }

    //tuple used to hold both the suspicions and the phrase
    public static class Tuple {
        public String phrase;
        public float suspicion;

        public Tuple(float suspicion, String phrase) {
            this.suspicion = suspicion;
            this.phrase = phrase;
        }
    }

    public class Token
    {
        public String tag;
        public String token;

        public Token(String pos, String word)
        {
            this.tag=pos;
            this.token=word;
        }
    }

}



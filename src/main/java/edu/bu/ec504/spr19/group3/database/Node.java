package edu.bu.ec504.spr19.group3.database;

import com.arangodb.entity.BaseDocument;

public class Node {
    private String word;
    private long frequency;
    private String pos;

    /**
     * The constructor for a single node
     * @param word The word that this node represents
     * @param frequency The frequency of times this word has been seen
     */
    public Node(String word, long frequency, String pos) {
        this.word = word;
        this.frequency = frequency;
        this.pos = pos;
    }

    /**
     * Returns the node's word
     * @return The word this node represents
     */
    public String getWord() {
        return this.word;
    }

    /**
     * Returns the frequency of this node
     * @return The word's frequency
     */
    public long getFrequency() {
        return this.frequency;
    }

    /**
     * Returns the pos of the word
     * @return The word's pos
     */
    public String getPos() { return this.pos; }

    /**
     * Increments the frequency of this node by one
     */
    public void incrementFrequency() {
        this.frequency++;
    }

    /**
     * Creates and returns a formatted document for submission to the database
     * @return The fully formed BaseDocument
     */
    public BaseDocument getDocument() {
        final BaseDocument node = new BaseDocument();
        node.setKey(this.word + this.pos);
        node.addAttribute("word", this.word);
        node.addAttribute("frequency", this.frequency);
        node.addAttribute("pos", this.pos);
        return node;
    }
}

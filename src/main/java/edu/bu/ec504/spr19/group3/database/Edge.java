package edu.bu.ec504.spr19.group3.database;

import com.arangodb.entity.BaseEdgeDocument;

import java.util.List;

public class Edge {
    private Node from;
    private Node to;
    private long frequency;

    /**
     * The constructor for this edge method. Holds a source and sink node, as well as the frequency at which the two are paired in this order.
     * @param from The source node in the graph.
     * @param to The sink node in the graph.
     * @param frequency The number of times the source node was followed by the sink node.
     */
    public Edge(Node from, Node to, long frequency) {
        this.from = from;
        this.to = to;
        this.frequency = frequency;
    }

    /**
     * Returns the source node of the edge.
     * @return The source node.
     */
    public Node getFrom() {
        return this.from;
    }

    /**
     * Returns the sink node of the edge.
     * @return The sink node.
     */
    public Node getTo() {
        return this.to;
    }

    /**
     * Returns the number of times the source was followed by the sink.
     * @return The source -> sink frequency.
     */
    public long getFrequency() {
        return this.frequency;
    }

    /**
     * Returns the key that this edge is under in the database.
     * @return The edge's key.
     */
    public String getKey() {
        return this.from.getWord() + this.from.getPos() + this.to.getWord() + this.to.getPos();
    }

    /**
     * Returns the ratio of this edge off the source node compared to every time the source has been used.
     * @return The ratio of edge_frequency / from_node_frequency.
     */
    public float ratio() {
        if (this.from.getFrequency() == 0) return 1.0f;
        if (this.frequency == 0) return 1.0f;
        List<Edge> edges = Database.getDatabase().getAllEdges(this.from.getWord(), this.from.getPos());
        int counter = 0;
        int lowBound = 0;
        int oldStrata = 0;
        for (Edge e : edges) {
            counter++;
            if (oldStrata != (int) e.frequency) lowBound = counter;
            oldStrata = (int) e.frequency;
            if (e.to.getWord().equals(this.to.getWord())) break;
        }
        int highBound = lowBound;
        while (highBound < edges.size() && edges.get(highBound).frequency == this.frequency) {
            highBound++;
        }
        return((float) (highBound)) / (float) edges.size();
    }

    /**
     * Returns the ratio after being normalized. A ratio of 0% is a suspicion of 0.0, while a ratio of 100% is a suspicion of 100.0.
     * @return The suspicion level.
     */
    public float suspicion() {
        return this.ratio() * 100;
    }

    /**
     * Increments the frequency of this edge by one.
     */
    public void incrementFrequency() {
        this.frequency++;
    }

    /**
     * Creates and returns a formatted edge for submission to the database.
     * @return The fully formed BaseEdgeDocument.
     */
    public BaseEdgeDocument getDocument() {
        final BaseEdgeDocument edge = new BaseEdgeDocument("words/" + this.from.getWord() + this.from.getPos(), "words/" + this.to.getWord() + this.to.getPos());
        edge.setKey(this.getKey());
        edge.addAttribute("frequency", this.frequency);
        return edge;
    }
}

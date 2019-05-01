package edu.bu.ec504.spr19.group3.database;

import com.arangodb.*;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.CollectionPropertiesEntity;
import com.arangodb.model.EdgeCreateOptions;
import com.arangodb.model.EdgeUpdateOptions;
import com.arangodb.model.VertexCreateOptions;
import com.arangodb.model.VertexUpdateOptions;
import com.arangodb.util.MapBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Database {

    private static Database database;
    private static ArangoDatabase db;

    private static ArangoCollection size;
    private static ArangoCollection links;
    private static ArangoVertexCollection nodes;
    private static ArangoEdgeCollection edges;
    private static final String DATABASE = "group3";
    private static final String GRAPH = "bigrams";
    private static final String NODES = "words";
    private static final String EDGES = "freqs";
    private static final String SIZE = "size";
    private static final String LINKS = "links";

    private static final long EDGE_SIZE = (54 * 2) + (54 * 2) + 2 + 128;
    private static final long NODE_SIZE = 54 + 2 + 128;
    private static final long JOURNAL_SIZE = 32 * 1000 * 1000;

    /**
     * The constructor for the Database class. Since this object is supposed to be a singleton, the constructor is private.
     * Instead of creating a new Database connection each time, the public getDatabase method should be called instead.
     */
    private Database() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.OFF);
        db = new ArangoDB.Builder().host("35.243.180.240").user("root").password("").build().db(DATABASE);
        ArangoGraph graph = db.graph(GRAPH);
        nodes = graph.vertexCollection(NODES);
        edges = graph.edgeCollection(EDGES);
        size = db.collection(SIZE);
        links = db.collection(LINKS);
    }

    /**
     * The getter for the Database singleton. Should be used to get connection instead of the constructor.
     * @return The database singleton.
     */
    public static Database getDatabase() {
        if (database == null) database = new Database();
        return database;
    }

    /**
     * Calculates the disk size in bytes by doing length queries on the vertex and edge collections.
     * @return The total bytes on disk to store our graph.
     */
    private long getDiskSize() {
        long totalSize = 0;
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor cursor;

        cursor = db.query("RETURN LENGTH(words)", bindVars, null, Long.class);
        totalSize += (Long) cursor.next() * NODE_SIZE;

        cursor = db.query("RETURN LENGTH(freqs)", bindVars, null, Long.class);
        totalSize += (Long) cursor.next() * EDGE_SIZE;

        totalSize += totalSize % JOURNAL_SIZE;
        return totalSize;
    }

    /**
     * Returns the disk size in megabytes.
     * @return The database disk size as a double, in megabytes.
     */
    public double getDiskSizeMB() {
        return ((double) getDiskSize()) / 1000 / 1000;
    }

    /**
     * Given a word and part of speech, returns the largest child in the n-gram graph.
     * @param word The word of the node
     * @param pos The part of speech of the word.
     * @return The node that is the biggest child of the input word.
     */
    public Node getBiggestChild(String word, String pos) {
        List<Edge> allEdges = getAllEdges(word, pos);
        return allEdges.get(0).getTo();
    }

    /**
     * Performs a query to get all edges of a node, sorted by the frequency of edge in descending order.
     * @param word The word of the node
     * @param pos The part of speech of the word.
     * @return An edgelist of all edges from the word, sorted by frequency
     */
    public List<Edge> getAllEdges(String word, String pos) {
        Map<String, Object> bindVars = new MapBuilder().get();
        ArangoCursor cursor;

        String query = "FOR e IN " + EDGES + " FILTER e._from == \"words/" + word + pos + "\" SORT e.frequency DESC, e._from, e._to RETURN e";
        cursor = db.query(query, bindVars, null, BaseDocument.class);
        List<BaseDocument> docs = new ArrayList<>();
        while (cursor.hasNext()) {
            docs.add((BaseDocument) cursor.next());
        }

        Node node = new Node(word, 0L, pos);
        List<Edge> allEdges = new ArrayList<>();
        for (BaseDocument document : docs) {
            String newWordComposite = ((String) document.getAttribute("_to")).replace("words/", "");
            Pattern pattern = Pattern.compile("([a-z]+)([A-Z]+)|([A-Z]+)([a-z]+)");
            Matcher matcher = pattern.matcher(newWordComposite);
            if (matcher.find()) {
                String newWord = matcher.group(1);
                String newPOS = matcher.group(2);
                Node newNode = new Node(newWord, 0, newPOS);
                allEdges.add(new Edge(node, newNode, (Long) document.getAttribute("frequency")));
            }
        }
        return allEdges;
    }

    /**
     * Adds link to the database. If the link already exists, silently fail.
     * @param link The link to add to the database.
     */
    public void addLink(String link) {
        BaseDocument newLink = links.getDocument("" + link.hashCode(), BaseDocument.class);
        if(newLink != null) return;
        newLink = new BaseDocument();
        newLink.setKey("" + link.hashCode());
        newLink.addAttribute("link", link);
        links.insertDocument(newLink);
    }

    /**
     * Checks if a link already exists in the database. Return true if it does, false otherwise.
     * @param link The link to check for.
     * @return true if link exists, false otherwise.
     */
    public boolean alreadySeenLink(String link) {
        BaseDocument newLink = links.getDocument("" + link.hashCode(), BaseDocument.class);
        return newLink != null;
    }

    /**
     * Gets the amount of bytes scraped and recorded in the database
     * @return Bytes as a long.
     */
    public long getSize() {
        BaseDocument currentSize = size.getDocument("size", BaseDocument.class);
        return (Long) currentSize.getAttribute("size");
    }

    /**
     * Increments the counter in the database with the new amount of bytes.
     * @param byteSize The number of bytes with which to increment.
     */
    public void incrementSize(Long byteSize) {
        Long currentSize = getSize();
        BaseDocument newSize = new BaseDocument();
        newSize.addAttribute("size", currentSize + byteSize);
        size.updateDocument("size", newSize);
    }

    /**
     * Checks the vertex collection for a node with the given key.
     * @param word The key that the node is stored under.
     * @return The node if it exists in the collection, null otherwise.
     */
    private Node getNode(String word, String pos) {
        BaseDocument node = nodes.getVertex(word + pos, BaseDocument.class);
        if (node == null) return null;
        return new Node((String) node.getAttribute("word"), (Long) node.getAttribute("frequency"), (String) node.getAttribute("pos"));
    }

    /**
     * Guarantees that it will retrieve a node from the graph. If the node doesn't exist it creates one. If the node does exist it increments it.
     * @param word The key of the node to retrieve.
     * @return The node corresponding to the input key.
     */
    private Node guaranteedGetNode(String word, String pos) {
        createOrUpdateNode(word, pos);
        return getNode(word, pos);
    }

    /**
     * Guarantees that it will retrieve a node from the graph. If the node doesn't exist it creates one with a frequency of 0. If it does it returns it.
     * @param word The key of the node to retrieve.
     * @return The node corresponding to the input key.
     */
    private Node guaranteedEtherealNode(String word, String pos) {
        Node node = getNode(word, pos);
        if (node == null) {
            insertNode(word, 0L, pos);
            node = getNode(word, pos);
        }
        return node;
    }

    /**
     * Inserts a node into the vertex collection, with a frequency of one.
     * @param word The word to create a node out of.
     * @return True if the node was successfully inserted, False otherwise.
     */
    private boolean insertNode(String word, String pos) {
        return insertNode(word, 1L, pos);
    }

    /**
     * Inserts a node into the vertex collection, with an arbitrary frequency.
     * @param word The word to create a node out of.
     * @return True if the node was successfully inserted, False otherwise.
     */
    private boolean insertNode(String word, Long frequency, String pos) {
        try {
            nodes.insertVertex(new Node(word, frequency, pos).getDocument(), new VertexCreateOptions());
            return true;
        } catch (ArangoDBException err) {
            return false;
        }
    }

    /**
     * Increments a preexisting node in the vertex collection.
     * @param node The key to find the node to update.
     * @return True if the node was successfully updated, False otherwise.
     */
    private boolean incrementNode(Node node) {
        try {
            node.incrementFrequency();
            nodes.updateVertex(node.getWord() + node.getPos(), node, new VertexUpdateOptions());
            return true;
        } catch (ArangoDBException err) {
            return false;
        }
    }

    /**
     * Creates a node if it doesn't exist in the graph, increments it if it does.
     * @param word The key of the node to insert/increment.
     * @return True if the operation succeeds, False if it fails.
     */
    private boolean createOrUpdateNode(String word, String pos) {
        Node node = getNode(word, pos);
        if (node == null) {
            return insertNode(word, pos);
        }
        return incrementNode(node);
    }

    /**
     * Retrieves an edge from the graph with given source and sink nodes.
     * @param from The source node on the graph.
     * @param to The sink node on the graph.
     * @return An Edge object if the edge was found, null otherwise.
     */
    private Edge getEdge(Node from, Node to) {
        BaseEdgeDocument edge = edges.getEdge(from.getWord() + from.getPos() + to.getWord() + to.getPos(), BaseEdgeDocument.class);
        if (edge == null) return null;
        return new Edge(from, to, (Long) edge.getAttribute("frequency"));
    }

    /**
     * Inserts a new edge between the source and sink nodes with a frequency of one.
     * @param from The source node.
     * @param to The sink node.
     * @return True if the edge was inserted, False if something went wrong.
     */
    private boolean insertEdge(Node from, Node to) {
        try {
                edges.insertEdge(new Edge(from, to, 1).getDocument(), new EdgeCreateOptions());
            return true;
        } catch (ArangoDBException err) {
            return false;
        }
    }

    /**
     * Assumes that the edge already exists. Takes preexisting edge and increments its frequency by one.
     * @param edge The edge to increment.
     * @return True if the edge is incremented, false otherwise.
     */
    private boolean updateEdge(Edge edge) {
        try {
            edge.incrementFrequency();
            edges.updateEdge(edge.getKey(), edge.getDocument(), new EdgeUpdateOptions());
            return true;
        } catch (ArangoDBException err) {
            return false;
        }
    }

    /**
     * Creates the edge if it doesn't exist, or increments it if it does. Also creates/increments the source and sink nodes.
     * @param from The source node of the edge.
     * @param to The sink node of the edge.
     * @return True if the operation succeeds, False if it fails.
     */
    public boolean createOrUpdateEdge(String from, String fromPos, String to, String toPos) {
        Node fromNode = guaranteedGetNode(from, fromPos);
        Node toNode = guaranteedEtherealNode(to, toPos);
        Edge edge = getEdge(fromNode, toNode);
        if (edge == null) {
            return insertEdge(fromNode, toNode);
        }
        return updateEdge(edge);
    }

    /**
     * Gets a Bi-gram from the graph of the source and sink strings.
     * @param from The source node.
     * @param to The sink node.
     * @return An edge object with the source and sink, as well as the frequency between them.
     */
    public Edge getBigram(String from, String fromPos, String to, String toPos) {
        Node fromNode = guaranteedEtherealNode(from, fromPos);
        Node toNode = guaranteedEtherealNode(to, toPos);
        Edge edge = getEdge(fromNode, toNode);
        if (edge == null) return new Edge(fromNode, toNode, 0L);
        return edge;
    }
}

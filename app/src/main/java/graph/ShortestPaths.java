package graph;

/*
 * Nicole Swierstra
 * 7/18
 * Implements dijkstra's algorithm
 */

import heap.Heap;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;


/** Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 * Sample usage:
 *   Graph g = // create your graph
 *   ShortestPaths sp = new ShortestPaths();
 *   Node a = g.getNode("A");
 *   sp.compute(a);
 *   Node b = g.getNode("B");
 *   LinkedList<Node> abPath = sp.getShortestPath(b);
 *   double abPathLength = sp.getShortestPathLength(b);
 *   */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest
    // paths computation:
    private HashMap<Node,PathData> paths;
    
    /** Compute the shortest path to all nodes from origin using Dijkstra's
     * algorithm. Fill in the paths field, which associates each Node with its
     * PathData record, storing total distance from the source, and the
     * backpointer to the previous node on the shortest path.
     * Precondition: origin is a node in the Graph.*/
    public void compute(Node origin) {
        paths = new HashMap<Node,PathData>();
        /* Priority is distance from integer */
        Heap<Node, Double> heapQueue = new Heap<Node, Double>();
        HashMap<Node, Node> prev = new HashMap<Node, Node>(); 
        heapQueue.add(origin, 0.0);
        
        prev.put(origin, null);
        
        while(heapQueue.size() > 0){ /* this is a while loop instead of a for loop */
            Double oldp = heapQueue.peekPriority(); /* i implemented this in my heap, just returns lowest priority */
            Node n = heapQueue.poll();
            paths.put(n, new PathData(oldp, prev.get(n)));

            HashMap<Node, Double> children = n.getNeighbors();
            for(Node child : children.keySet()){
                if(!paths.containsKey(child) && !heapQueue.contains(child)){
                    heapQueue.add(child, children.get(child) + oldp);
                    prev.put(child, n);
                }
            }
        }
    }

    /** Returns the length of the shortest path from the origin to destination.
     * If no path exists, return Double.POSITIVE_INFINITY.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public double shortestPathLength(Node destination) {
        if(paths.get(destination) == null) return Double.POSITIVE_INFINITY;
        return paths.get(destination).distance;    
    }

    /** Returns a LinkedList of the nodes along the shortest path from origin
     * to destination. This path includes the origin and destination. If origin
     * and destination are the same node, it is included only once.
     * If no path to it exists, return null.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public LinkedList<Node> shortestPath(Node destination) {
        PathData pd = paths.get(destination);
        if(pd == null) return null;
        LinkedList<Node> path = new LinkedList<Node>();
        path.add(destination);
        while(pd.previous != null) {
            path.addFirst(pd.previous);
            pd = paths.get(pd.previous);
        }
        return path;
    }


    /** Inner class representing data used by Dijkstra's algorithm in the
     * process of computing shortest paths from a given source node. */
    class PathData {
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /** constructor: initialize distance and previous node */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }
    }


    /** Static helper method to open and parse a file containing graph
     * information. Can parse either a basic file or a DB1B CSV file with
     * flight data. See GraphParser, BasicParser, and DB1BParser for more.*/
    protected static Graph parseGraph(String fileType, String fileName) throws
        FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db1b")) {
            parser = new DB1BParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }

    public static void main(String[] args) {
        // read command line args
        String fileType = args[0];
        String fileName = args[1];
        String origCode = args[2];

        String destCode = null;
        if (args.length == 4) {
            destCode = args[3];
        }

        // parse a graph with the given type and filename
        Graph graph;
        try {
            graph = parseGraph(fileType, fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file " + fileName);
            return;
        }
        graph.report();

        ShortestPaths sp = new ShortestPaths();
        sp.compute(graph.getNode(origCode));
        System.out.println("There are " + sp.paths.size() + " paths.");

        if(destCode != null){
            Node dest = graph.getNode(destCode);
            for(Node n : sp.shortestPath(dest)){
                System.out.print(n + " ");
            }
            System.out.println(sp.shortestPathLength(dest)); 
            return;
        }

        System.out.println("Paths from " + origCode + ": ");
        for(Node dest : graph.getNodes().values()){
            Double dist = sp.shortestPathLength(dest);
            if(dist == Double.POSITIVE_INFINITY) continue;
            for(Node n : sp.shortestPath(dest)){
                System.out.print(n + " ");
            }
            System.out.println(dist); 
        }
    }
}

package graph;

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;

import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URL;
import java.io.FileNotFoundException;

import java.util.LinkedList;

class testBlock {
    Node n0;
    Node n1;
    double exLen;
    int exElem;
    static Graph g;

    public testBlock(String n0, String n1, int exElem, double exLen) {
        this.n0 = g.getNode(n0);
        this.n1 = g.getNode(n1);
        this.exLen = exLen;
        this.exElem = exElem;        
    }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortestPathsTest {

    /* Performs the necessary gradle-related incantation to get the
       filename of a graph text file in the src/test/resources directory at
       test time.*/
    private String getGraphResource(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource.getPath();
    }

    /* Returns the Graph loaded from the file with filename fn located in
     * src/test/resources at test time. */
    private Graph loadBasicGraph(String fn) {
        Graph result = null;
        String filePath = getGraphResource(fn);
        try {
          result = ShortestPaths.parseGraph("basic", filePath);
        } catch (FileNotFoundException e) {
          fail("Could not find graph " + fn);
        }
        return result;
    }

    /** Dummy test case demonstrating syntax to create a graph from scratch.
     * Write your own tests below. */
    @Test
    public void test00Nothing() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        g.addEdge(a, b, 1);

        // sample assertion statements:
        assertTrue(true);
        assertEquals(2+2, 4);
    }

    /** Minimal test case to check the path from A to B in Simple0.txt */
    @Test
    public void test01Simple0() {
        Graph g = loadBasicGraph("Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node b = g.getNode("B");
        LinkedList<Node> abPath = sp.shortestPath(b);
        assertEquals(abPath.size(), 2);
        assertEquals(abPath.getFirst(), a);
        assertEquals(abPath.getLast(),  b);
        assertEquals(sp.shortestPathLength(b), 1.0, 1e-6);
    }

    /* I know better than to be trusting of floating point imprecision */
    boolean isWithin(double a, double b, double t){
        return (a > (b * (1 - t))) && (a < (b * (1 + t)));
    }

    

    boolean pathCorrect(Graph g, testBlock tb){
        ShortestPaths sp = new ShortestPaths();
        sp.compute(tb.n0);
        if(Double.isInfinite(tb.exLen)){
            return Double.isInfinite(sp.shortestPathLength(tb.n1)) && sp.shortestPath(tb.n1) == null;
        }
        boolean isLength = isWithin(tb.exLen, sp.shortestPathLength(tb.n1), 0.01);
        boolean isNodes = sp.shortestPath(tb.n1).size() == tb.exElem;
        return isNodes && isLength;
    }

    /* Pro tip: unless you include @Test on the line above your method header,
     * gradle test will not run it! This gets me every time. */

    @Test
    public void test02Complicated(){
        Graph g = loadBasicGraph("Simple2.txt");
        testBlock.g = g;
        
        testBlock[] tests = {
            new testBlock("A", "B", 4, 5.0), 
            new testBlock("F", "C", 2, 3.0), 
            new testBlock("F", "G", 2, 7.0), 
            new testBlock("E", "A", 0, Double.POSITIVE_INFINITY),
            new testBlock("A", "E", 2, 1.0),
            new testBlock("H", "C", 4, 11.0),
            new testBlock("C", "H", 0, Double.POSITIVE_INFINITY)
        };
        
        for(testBlock tb : tests){
            assertTrue(pathCorrect(g, tb));
        }
    } 
}

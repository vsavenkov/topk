package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.TestUtil;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by filtz on 11/18/16.
 */


/**
 *  Use strings to specify graphs:
 *  s is assumed to be the start node, t - the end node,
 *  all other nodes are named arbitrarily.
 *   (+ "t_end" is internally appended to t to make it a subject)
 *
 *  Graph can be given as a set of paths combined by dots:
 *   "s-t" : path from s to t
 *   "s-a-t. s-b-t" : fork from s to a and to b, then join at t
 *   etc.
 *
 *
 * Created by Vadim on 13.05.2016.
 */
public class BidirectionalTopKTest {


    @Test
    public void one_hop_path() throws Exception {
        //single hop from s to t
        TestUtil.Graph g = new TestUtil.Graph("s-t",1);
        //Graph gi_reverse = new Graph("t-s",1,true);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);
        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }

    @Test
    public void two_hop_path() throws Exception {

        //single three-hop path from s to t
        TestUtil.Graph g = new TestUtil.Graph("s-a1-t",1);
        //Graph gi_reverse = new Graph("t-a1-s",1,true);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }


    @Test
    public void three_hop_path() throws Exception {

        //single three-hop path from s to t
        TestUtil.Graph g = new TestUtil.Graph("s-a1-a2-t",1);
        //TestUtil.Graph g_reverse = new TestUtil.Graph("t-a2-a1-s",1, true);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);
        assertEquals(g.numPaths, solutions.size());
    }


    @Test
    public void simple_fork() throws Exception {

        //two two-hop paths from s to t, one via b1, another via b2
        String sPattern = "s-b1-t. s-b2-t";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,2);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }


    @Test
    public void fork_at_first_hop() throws Exception {

        //fork from s to d1 + d2, then join @c, proceed to t
        String sPattern = "s-d1-c-ff-t. s-d2-c";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,2);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);
        assertEquals(g.numPaths, solutions.size());
    }


    @Test
    public void fork_at_second_hop() throws Exception {

        //from s to ff, then fork to g1 + g2, then join @t
        String sPattern = "s-ff-c-g1-t. c-g2-t";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,2);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }


    @Test
    public void two_forks() throws Exception {

        //two consecutive fork&joins: s(h1|h2)c(i1|i2)t
        String sPattern = "s-h1-c-i1-t. s-h2-c-i2-t";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,4);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }

    @Test
    public void premature_stop() throws Exception
    {
        String sPattern = "s-j1-j2-t . s-k1-k2-k3-t. k2-j1";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,3);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }

    @Test
    public void loop() throws Exception {

        String sPattern = "s-s-t";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,2);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }

    @Test
    public void cycles() throws Exception {

        //an s-l-s loop + direct s-t path
        // s-t; s-l-s-m-s-t; s-m-s-l-s-t should be among possible paths
        String sPattern = "s-l-s-m-s-t";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,3);
        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);
        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }


    @Test
    public void cycle_plus_two_joins() throws Exception {

        //an s-n3-c-n4-s loop + two consecutive fork&joins: s(n1|n2)c(p1|p2)t
        String sPattern = "s-n3-c-n4-s. s-n1-c-p1-t. s-n2-c-p2-t";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,6);
        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);
        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }

    @Test
    public void challenge_example() throws Exception {

        //an s-n3-c-n4-s loop + two consecutive fork&joins: s(n1|n2)c(p1|p2)t
        String sPattern = "s-b-t. s-a-b.t";
        TestUtil.Graph g = new TestUtil.Graph(sPattern,2);
        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);
        TestUtil.printSolutions(solutions,g.gix);

        assertEquals(g.numPaths, solutions.size());
    }

 }



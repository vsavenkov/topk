package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.TestUtil;
import at.ac.wu.graphsense.search.patheval.RegExpPathArbiter;
import org.junit.Test;
import org.rdfhdt.hdt.enums.TripleComponentRole;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by vadim on 04.05.17.
 */
public class RegExpPathArbiterTest {

    @Test
    public void genericRE_label_star() throws Exception {

        String sGraph = "s-b1-t. s-b2-t";
        final TestUtil.Graph g = new TestUtil.Graph(sGraph,2);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths,null);

        if( g.numPaths != solutions.size() ){
            return;
        }

        // Algorithm seems to be OK

        RegExpPathArbiter rpa = new RegExpPathArbiter(null) {
            @Override
            protected String edgePattern() {
                return "([a-zA-Z0-9]*:[a-zA-Z0-9]*)";
            }

            @Override
            protected Object parseEdgeLabel (String edgeLabel){
                try{
                    return g.dict.stringToId(edgeLabel, TripleComponentRole.PREDICATE);
                }
                catch( Exception ex ){

                }
                return null;
            }
        };

        rpa.init(g.gix,g.start,g.target,true);

        topK = new BidirectionalTopK<>();
        topK.init(g.gix);

        solutions = topK.run(g.start, g.target, g.numPaths, rpa);

        assertEquals(g.numPaths, solutions.size());
    }

    @Test
    public void genericRE_label_star2() throws Exception {

        RegExpPathArbiter rpa = new RegExpPathArbiter(null) {
            @Override
            protected String edgePattern() {
                return super.edgePattern();
            }

            @Override
            protected Object parseEdgeLabel (String edgeLabel){
                return super.edgePattern();
            }
        };

        assertTrue(true);
    }


}

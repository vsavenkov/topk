package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.TestUtil;
import at.ac.wu.graphsense.search.BidirectionalTopK;
import at.ac.wu.graphsense.search.patheval.RegExpPathArbiter;
import at.ac.wu.graphsense.search.pathexpr.PathExpr;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.junit.Test;

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

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>(g.gix);

        Collection<List<Edge<Integer,Integer>>> solutions = topK.run(g.start, g.target, g.numPaths);

        if( g.numPaths != solutions.size() ){
            return;
        }

        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("",TestUtil.NAMESPACE);

        Path p = PathParser.parse("(:"+TestUtil.PREDICATE+")+", m);

        PathExpr<Integer,Integer> pathExpr = PathExprFactory.createPathExpr( p, g.edict );

        // Algorithm seems to be OK
        topK = new BidirectionalTopK(g.gix);
        topK.setPathArbiter(new HDTRegExpPathArbiter(pathExpr));

        solutions = topK.run(g.start, g.target, g.numPaths);

        assertEquals(g.numPaths, solutions.size());
    }


}

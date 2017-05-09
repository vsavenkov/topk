package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.EdgeDictionary;
import at.ac.wu.graphsense.TestUtil;
import at.ac.wu.graphsense.search.pathexpr.PathExpr;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.junit.Test;

/**
 * Created by Vadim on 08.05.2017.
 */
public class PathExprFactoryTest {

    @Test
    public void jena_path_1(){

        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("","http://empty/url.com/");
        m.setNsPrefix("a", "http://a/url.com/");

        Path p1 = PathParser.parse("a:b*", m);

        String sGraph = "s-b1-t. s-b2-t";
        final TestUtil.Graph g = new TestUtil.Graph(sGraph,2);

        HDTGraphIndex hgi = new HDTGraphIndex(g.hdt);

        PathExpr<Integer,Integer> pe =
                PathExprFactory.createPathExpr(p1, hgi);

    }
}

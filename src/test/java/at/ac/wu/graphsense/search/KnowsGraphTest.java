package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.TestUtil;
import at.ac.wu.graphsense.VertexDictionary;
import at.ac.wu.graphsense.hdt.HDTGraphIndex;
import at.ac.wu.graphsense.hdt.PathExprFactory;
import at.ac.wu.graphsense.search.patheval.PathArbiter;
import at.ac.wu.graphsense.search.patheval.RegExpPathArbiter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.junit.Test;
import org.rdfhdt.hdt.hdt.HDT;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by vadim on 10.05.17.
 */
public class KnowsGraphTest {

    final static String knowsGraph ="@prefix : <http://dbpedia.org/resource/> . "
                                  + ":a :knows :b . :b :knows :c . :c :name \"qaiser\" ."
                                  + ":a :knows :e . :e :knows :c . :e :name \"mehmood\" ";

    @Test
    public void knows_star(){
        Model model = TestUtil.createLabeledGraph(knowsGraph);
        HDTGraphIndex hdt = new HDTGraphIndex(TestUtil.createHDT(model));

        String px = model.getNsPrefixMap().getOrDefault("","http://dbpedia.org/resource");
        Path p = PathParser.parse("(:knows)*", model);
        PathArbiter<Integer,Integer> parb = new RegExpPathArbiter<>(PathExprFactory.createPathExpr(p,hdt));
        int source = hdt.vertexKey(px+"a", Edge.Component.SOURCE);
        int target = hdt.vertexKey(px+"c", Edge.Component.TARGET);
        parb.init(hdt, source, target, true);

        BidirectionalTopK<Integer,Integer> topK = new BidirectionalTopK<>();
        topK.init(hdt);

        Collection<List<Edge<Integer,Integer>>> results;

        try {
            results = topK.run(source, target, 10, parb);
        }
        catch(IOException ex){

        }
    }
}

package at.ac.wu.graphsense.rdf;

import at.ac.wu.graphsense.EdgeDictionary;
import at.ac.wu.graphsense.TrivialDictionary;
import at.ac.wu.graphsense.search.pathexpr.PathExpr;
import at.ac.wu.graphsense.search.pathexpr.PathExprFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.path.Path;

/**
 * Created by Vadim on 05.06.2017.
 */
public class RDFPathExprFactory {

        public static PathExpr<Node,Node> createPathExpr(Path path){
            return PathExprFactory.createPathExpr(path, TrivialJenaNodeTranslator.instance);
        }


        static class TrivialJenaNodeTranslator extends TrivialDictionary<Node>
                implements PathExprFactory.JenaNodeTranslator<Node> {

            static final TrivialJenaNodeTranslator instance = new TrivialJenaNodeTranslator();

            public TrivialJenaNodeTranslator(){ }

            @Override
            public Node getImpossibleValue() {
                throw new RuntimeException("This call should not happen!");
            }
        }
}

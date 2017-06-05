package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.EdgeDictionary;
import at.ac.wu.graphsense.search.pathexpr.*;
import at.ac.wu.graphsense.search.pathexpr.PathExprFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.path.Path;

/**
 * Created by Vadim on 05.06.2017.
 */
public class HDTPathExprFactory {

        public static PathExpr<Integer,Integer> createPathExpr(Path path, EdgeDictionary<Integer,String> ed){
            return PathExprFactory.createPathExpr(path, new IntJenaNodeTranslator(ed));
        }


        static class IntJenaNodeTranslator implements PathExprFactory.JenaNodeTranslator<Integer>{
            EdgeDictionary<Integer,String> ed;

            public IntJenaNodeTranslator(EdgeDictionary<Integer,String> ed){
                this.ed = ed;
            }

            @Override
            public Node edgeEntry(Integer key) {
                String s = ed.edgeEntry(key);
                return s == null? null :
                        ( s.contains(":") ? NodeFactory.createURI(s) : NodeFactory.createLiteral(s));
            }

            @Override
            public Integer edgeKey(Node entry) {
                String s = null;
                if(entry.isURI()){ s = entry.getURI(); }
                else if (entry.isLiteral()){ s = entry.getLiteralValue().toString(); }
                else if (entry.isBlank()) { s = entry.getBlankNodeLabel(); }

                return s!=null ? ed.edgeKey(s) : null;
            }

            @Override
            public Integer getImpossibleValue() {
                return Integer.MAX_VALUE;
            }
        }
}

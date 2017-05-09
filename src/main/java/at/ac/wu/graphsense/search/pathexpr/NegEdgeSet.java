package at.ac.wu.graphsense.search.pathexpr;

import at.ac.wu.graphsense.Edge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vadim on 09.05.17.
 */
public class NegEdgeSet<V,E> extends PathExpr<V,E> {

    Set<Edge<V,E>> fwEdges = new HashSet<>();
    Set<Edge<V,E>> bwEdges = null; // inverses are not yet supported

    public NegEdgeSet( Collection<Edge<V,E>> fwEdges ){
        this.fwEdges.addAll(fwEdges);
    }

    public Set<Edge<V,E>> getFWEdges(){
        return Collections.unmodifiableSet(fwEdges);
    }

    @Override
    public void visit(ExprVisitor<V,E> v){
        v.visitNegEdgeSet(this);
    }

}

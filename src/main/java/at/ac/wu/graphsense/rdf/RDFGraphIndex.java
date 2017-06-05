package at.ac.wu.graphsense.rdf;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;
import at.ac.wu.graphsense.search.patheval.AllPassArbiter;
import at.ac.wu.graphsense.search.patheval.PathArbiter;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.Iterator;

/**
 * Created by Vadim on 05.06.2017.
 */
public class RDFGraphIndex implements GraphIndex<Node,Node> {
    final static PathArbiter<Node,Node> allPass = new AllPassArbiter<>();

    Graph graph;

    public RDFGraphIndex(Graph graph){
        this.graph = graph;
    }

    @Override
    public Iterator<Edge<Node, Node>> lookupEdges(Node source, Node target) {
        return new WrappingIterator(graph.find(source,null,target), source==null);
    }

    @Override
    public PathArbiter<Node, Node> createAllPassArbiter() {
        return allPass;
    }

    static class WrappingIterator implements Iterator<Edge<Node,Node>>{

        ExtendedIterator<Triple> inner;
        boolean fetchSubjects;

        public WrappingIterator( ExtendedIterator<Triple> inner, boolean fetchSubjects  ){
            this.inner = inner;
            this.fetchSubjects = fetchSubjects;
        }

        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public Edge<Node, Node> next() {
            Triple  t = inner.next();
            if( fetchSubjects ) {
                return new Edge(t.getSubject(),t.getPredicate());
            }
            return new Edge(t.getObject(),t.getPredicate());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

    }

}

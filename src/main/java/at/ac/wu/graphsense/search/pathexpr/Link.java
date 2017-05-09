package at.ac.wu.graphsense.search.pathexpr;
import at.ac.wu.graphsense.Edge;

/**
 * Created by Vadim on 08.05.2017.
 */
public class Link<V,E> extends PathExpr<V,E> {
    Edge<V,E> edge;

    public Link( V vertex, E label ){
        edge = new Edge(vertex, label);
    }

    public Link( E label ){
        edge = new Edge(null, label);
    }

    public Edge<V,E> getEdge(){return edge; }

    public String toString(){
        return getEdge().label().toString()+modifier();
    }
}

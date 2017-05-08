package at.ac.wu.graphsense.search.pathexpr;

import java.util.Collection;

/**
 * Created by Vadim on 08.05.2017.
 */
public interface ExprVisitor<T,V,E> {
    T visitEdge(Edge<V,E> e);

    T visitAlt(Alt<V,E> e);

    T visitSeq(Seq<V,E> e);

    T combine( Collection<T> ts );
}

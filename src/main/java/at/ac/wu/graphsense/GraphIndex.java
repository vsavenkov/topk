package at.ac.wu.graphsense;

import at.ac.wu.graphsense.search.patheval.PathArbiter;

import java.util.Iterator;

/**
 * Created by vadim on 11.10.16.
 */
public interface GraphIndex<V,E> {
    Iterator<Edge<V,E>> lookupEdges(V source, V target);

    PathArbiter<V,E> createAllPassArbiter();
}

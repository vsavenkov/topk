package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;
import at.ac.wu.graphsense.search.patheval.PathArbiter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Vadim on 14.10.2016.
 */
public interface TopKSearchAlgorithm<V,E> {

    void init(GraphIndex<V,E> gi);

    Collection<List<Edge<V,E>>> run(V start, V end, int k, PathArbiter<V,E> fi) throws IOException;

    void addProgressListener( SearchProgressListener<V,E> listener );
}

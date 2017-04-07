package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.Edge;

import java.io.IOException;
import java.util.List;

/**
 * Created by Vadim on 07.12.2016.
 */
public interface SearchProgressListener<V,E> {
    boolean onAdvance(int forw, int backw, int mapvf, int mapvb) throws IOException;
    boolean onPathFound(List<Edge<V,E>> path);
}

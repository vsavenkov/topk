package at.ac.wu.graphsense;

/**
 * Created by vadim on 13.10.16.
 */
public interface VertexDictionary<K,E> {
    E vertexEntry(K key, Edge.Component component );
    K vertexKey(E entry, Edge.Component component );
}

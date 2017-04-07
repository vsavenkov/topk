package at.ac.wu.graphsense;

/**
 * Created by "Vadim Savenkov" on 07.12.16.
 */
public interface EdgeDictionary<K,E> {
   E edgeEntry(K key );
   K edgeKey(E entry );
}

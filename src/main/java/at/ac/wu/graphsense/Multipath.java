package at.ac.wu.graphsense;

import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.triples.IteratorTripleID;

import java.util.*;

/**
 * Path with multiedges
 *
 * Created by vadim on 18.05.16.
 */
public class Multipath<V,E> implements Iterable<Path<V,E>> {

    protected final Path<V,E> p;

    protected Set<Path<V,E>> paths = null;

    Hashtable<V,Hashtable<V,Set<E>>> edges = new Hashtable<>();

    public Multipath(Collection<Integer> vertices, GraphIndex gi ) {
        this( new Path(vertices), gi );
    }

    public Multipath(Path p, GraphIndex gi)
    {
        this.p = p;

        V from = null;
        for( Object oTo : p.vertexSequence() ){
            V to = (V)oTo;
            if( from != null ){
                Iterator<Edge<V,E>> triples = gi.lookupEdges(from, to);

                List<E> edges = new ArrayList<>();

                while(triples.hasNext())
                {
                    edges.add(triples.next().label());
                }
                setEdges( from, to, edges );
            }
            from = to;
        }
    }

    /**
     * Gets all edge labels between two vertexSequence.
     * For non-existing edgeSequence, an empty list is returned.
     *
     * @param from start vertex
     * @param to end vertex
     * @return A list of edge labels between {@code from} and {@code to}, if exist any.
     */
    public Set<E> getEdges(V from, V to){

        if( !edges.containsKey(from) )
        {
            return Collections.EMPTY_SET;
        }
        Hashtable<V,Set<E>> outgoing = edges.get(from);

        if( !outgoing.containsKey(to) )
        {
            return Collections.EMPTY_SET;
        }

        return outgoing.get(to);
    }

    /**
     * Sets edge labels between {@code from} and {@code to}.
     * @param from start vertex
     * @param to end vertex
     * @param edgeLabels List of edge labels
     */
    public void setEdges(V from, V to, Collection<E> edgeLabels){
        if( !edges.containsKey(from) )
        {
            edges.put(from, new Hashtable<V, Set<E>>());
        }
        Hashtable<V,Set<E>> outgoing = edges.get(from);

        outgoing.put(to, new HashSet<>(edgeLabels));

        paths = null;
    }


    public Set<Path<V,E>> enumeratePaths()
    {
        if (paths != null) {
            return Collections.unmodifiableSet(paths);
        }

        if(p==null || p.numVertices() < 2) {
            throw new RuntimeException("Too few vertexSequence in the path: " + p);
        }

        List<List<E>> mp = new LinkedList<>();
        V prev = null;
        for(V v : p.vertexSequence() )
        {
            if(prev != null)
            {
                Set<E> es = getEdges(prev,v);
                if( es.isEmpty() ) {
                    throw new RuntimeException("No edgeSequence found between " + prev + " and " + v);
                }
                mp.add(new ArrayList<>(es));
            }
            prev = v;
        }

        //deduplicate paths
        Set<List<Object>> edgeRuns = new HashSet<>(crossJoin(mp));
        paths = new HashSet<>(edgeRuns.size());

        for( List<Object> run : edgeRuns ){
            paths.add( new Path(p.vertexList(), (List<Integer>)(List<?>)run) );
        }

        return Collections.unmodifiableSet(paths);
    }


    /**
     * @return Edge-agnostic path
     */
    public Path getVertexPath()
    {
        return p;
    }

    @Override
    public Iterator<Path<V,E>> iterator() {
        return enumeratePaths().iterator();
    }

    private static List<List<Object>> crossJoin(List<?> lists) {
        if(lists.size() < 2) {
            return new ArrayList<>((List<List<Object>>)lists);
        }

        List<List<Object>> prod = _crossJoin(0,lists); //it's Cartesian product

        //... however, elements go in the inverse order due to recursion:
        for( List<Object> tuple : prod ) {
            Collections.reverse(tuple);
        }
        return prod;
    }

    private static List<List<Object>> _crossJoin(int index, List<?> lists) {
        List<List<Object>> ret = new ArrayList<>();
        if (index == lists.size()) {
            ret.add(new LinkedList<>());
        } else {
            for (Object elem : (Collection<Object>)lists.get(index)) {
                for (List<Object> list : _crossJoin(index+1, lists)) {
                    list.add(elem);
                    ret.add(list);
                }
            }
        }
        return ret;
    }

}

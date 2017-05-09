package at.ac.wu.graphsense.search.patheval;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;

/**
 * Created by "Vadim Savenkov" on 10.11.16.
 */
public class AllPassArbiter<V,E> implements PathArbiter<V,E> {

    static final CumulativeRank _r = new CumulativeRank(CumulativeRank.MAX);

    public void init(GraphIndex<V,E> gi, V source, V target, boolean bidirectional){}

    public CumulativeRank rankEdge(Edge<V,E> edge, Iterable<Edge<V,E>> path
            , CumulativeRank rank, boolean forkRankObject, boolean backwardPath )
    {
        return _r;
    }

    public double composeRanks( Iterable<Edge<V,E>> prefix, CumulativeRank prefixRank, Iterable<Edge<V,E>> suffix, CumulativeRank suffixRank )
    {
        return CumulativeRank.MAX;
    }

    @Override
    public Object getInitialState(boolean backward){ return null; }
}


package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;
import at.ac.wu.graphsense.search.patheval.AllPassArbiter;
import at.ac.wu.graphsense.search.patheval.CumulativeRank;
import at.ac.wu.graphsense.search.patheval.PathArbiter;

/**
 * Created by "Vadim Savenkov" on 10.11.16.
 */
public class HDTAllPassArbiter extends AllPassArbiter<Integer,Integer> {

    HDTGraphIndex hdt;
    Integer target;
    Long nShared;

    final static CumulativeRank passRank = new CumulativeRank(CumulativeRank.MAX);
    final static CumulativeRank pruneRank = new CumulativeRank(CumulativeRank.PRUNE_PATH);

    @Override
    public void init(GraphIndex<Integer,Integer> gi, Integer source, Integer target, boolean bidirectional)
    {
        hdt = (HDTGraphIndex)gi;
        if( hdt!=null ){
            this.target = target;
            this.nShared = hdt.dict.getNshared();
        }
    }

    @Override
    public CumulativeRank rankEdge(Edge<Integer,Integer> edge, Iterable<Edge<Integer,Integer>> path, CumulativeRank pathRank, boolean forkRankObject, boolean backwardPath ){

        //A workaround for the problem that literals and IRIs can have clashing HDT integer representation.
        if( hdt!=null && !backwardPath ){
            Integer v = edge.vertex();
            if( v>=nShared && v!=target ) {
                if ( v != hdt.vertexKey(hdt.vertexEntry(v, Edge.Component.TARGET), Edge.Component.SOURCE) ){
                    return pruneRank;
                }
            }
        }
        return passRank;
    }
}


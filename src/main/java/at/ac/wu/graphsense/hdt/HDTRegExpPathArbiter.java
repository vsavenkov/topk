package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;
import at.ac.wu.graphsense.search.patheval.CumulativeRank;
import at.ac.wu.graphsense.search.patheval.RegExpPathArbiter;
import at.ac.wu.graphsense.search.pathexpr.PathExpr;

/**
 * Created by Vadim on 10.05.2017.
 */
public class HDTRegExpPathArbiter extends RegExpPathArbiter<Integer,Integer> {

    HDTAllPassArbiter allPass = new HDTAllPassArbiter();

    public HDTRegExpPathArbiter(PathExpr<Integer,Integer> pathExpr){
        super(pathExpr);
    }

    @Override
    public void init(GraphIndex<Integer,Integer> gi, Integer source, Integer target, boolean bidirectional)
    {
        super.init(gi,source,target,bidirectional);
        allPass.init(gi,source,target,bidirectional);
    }

    @Override
    public CumulativeRank rankEdge(Edge<Integer,Integer> edge, Iterable<Edge<Integer,Integer>> path, CumulativeRank pathRank, boolean forkRankObject, boolean backwardPath ){
        CumulativeRank rank = super.rankEdge(edge,path,pathRank,forkRankObject,backwardPath);

        if( !backwardPath && !rank.isPruneRank() ){
            CumulativeRank allPassRank = allPass.rankEdge(edge,path,pathRank,forkRankObject,backwardPath);
            if( allPassRank.isPruneRank() ){
                rank.forcePrune();
            }
        }
        return rank;
    }


}

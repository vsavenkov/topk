package at.ac.wu.graphsense.search.patheval;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;
import at.ac.wu.graphsense.search.patheval.CumulativeRank;

/**
 * Created by Vadim on 20.10.2016.
 */

public interface PathArbiter<V,E> {

    /**
     *
     * @param gi GraphIndex interface to the graph. Could be tested for support of the <code>Dictionary</code> interface
     *           to fetch additional data about paths.
     *
     * @param source source node of the paths to search
     *
     * @param target target node of the paths to search
     *
     * @param bidirectional If <code>rankEdge</code> will be code both for forward and backward paths. If backward paths
     *                      are not supported, throw <code>UnsupportedOperationException</code>.
     *                      In this case the init method should be called again if a unidirectional mode is required.
     *
     * @throws UnsupportedOperationException if only forward paths are supported and <code>bidirecional</code> is
     *                                       <code>true</code>.
     */
    void init(GraphIndex<V,E> gi, V source, V target, boolean bidirectional) throws UnsupportedOperationException;

    /**
     *  Transform the rank <code>rank</code> of the prefix path <code>path</code> into a rank of the path
     *  <code>path.edge</code>.
     *
     * @param edge Current edge to be evaluated
     * @param path The previous path (preceding <code>edge</code>)
     * @param pathRank In: the rank of the last edge in <code>path</code>
     * @param forkRankObject In: if <code>pathRank</code> needs to be cloned, or the same object can be returned
     * @param backwardPath <code>true</code> if unwinding the paths from target to source, <code>false</code> otherwise.
     *                     Can only be <code>true</code> if <code>init</code> method was successfully called with the
     *                     <code>bidirectional</code> parameter set to <code>true</code>.
     * @return A rank of the path <code>path.edge</code>. Negative rank means pruning.
     */
     CumulativeRank rankEdge(Edge<V,E> edge, Iterable<Edge<V,E>> path, CumulativeRank pathRank, boolean forkRankObject, boolean backwardPath );

    /**
     *  Evaluate the rank of the path <code>prefix.suffix</code> given the respective ranks of the prefix and of the suffix.
     *  Optional operation. Should not be called if bidirectional paths are not supported.
     *
     *
     * @param prefix The prefix of the joint path.
     * @param prefixRank The rank of the prefix path.
     * @param suffix The suffix of the joint path.
     * @param suffixRank The rank of the suffix path.
     * @return The rank of the path <code>prefix.suffix</code>.
     * @throws UnsupportedOperationException if not supported.
     */
     double composeRanks( Iterable<Edge<V,E>> prefix, CumulativeRank prefixRank, Iterable<Edge<V,E>> suffix, CumulativeRank suffixRank )
     throws UnsupportedOperationException;

     Object getInitialState(boolean backward);

}

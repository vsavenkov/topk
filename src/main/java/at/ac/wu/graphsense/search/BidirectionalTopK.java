package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.*;
import at.ac.wu.graphsense.search.patheval.AllPassArbiter;
import at.ac.wu.graphsense.search.patheval.CumulativeRank;
import at.ac.wu.graphsense.search.patheval.PathArbiter;

import java.io.IOException;
import java.util.*;

/**
 * Created by vadim on 12.10.16.
 */
public class BidirectionalTopK<V,E> implements TopKSearchAlgorithm<V,E> {

        protected V startNode, targetNode;
        protected int k;
        protected PathArbiter<V,E> arbiter;
        protected GraphIndex<V,E> gix;

        final static int FORWARD = 0;
        final static int BACKWARD = 1;

        List<Frontier> ff;

        Collection<List<Edge<V,E>>> solutions = new HashSet<>();

        List<SearchProgressListener<V,E>> listeners = new LinkedList<>();

        public void init(GraphIndex<V,E> gi){
            gix = gi;

            ff = new LinkedList<>();
            ff.add(null);
            ff.add(null);
            solutions = new HashSet<>();
        }

        public void addProgressListener( SearchProgressListener<V,E> listener ){
            listeners.add(listener);
        }

        //@Loggable(prepend=true)
        public Collection<List<Edge<V,E>>> run(V start, V end, int k, PathArbiter<V,E> arbiter) throws IOException {

            startNode = start;
            targetNode = end;
            this.k = k;
            this.arbiter = arbiter == null ? new AllPassArbiter() : arbiter;

            ff.set(FORWARD, new Frontier(false));
            ff.get(FORWARD).add(makeTerminalEdge(startNode));
            ff.set(BACKWARD, new Frontier(true));
            ff.get(BACKWARD).add(makeTerminalEdge(targetNode));

            int direction=BACKWARD;

            while (!(ff.get(FORWARD).isEmpty() || ff.get(BACKWARD).isEmpty())) {

                direction = 1 - direction;

                ff.set(direction, ff.get(direction).advance());

                //TODO: check for plausibility
                //Frontier size
                int forw = ff.get(FORWARD).vertices().size();
                int backw = ff.get(BACKWARD).vertices().size();
                int mapvf = 0;
                int mapvb = 0;
                Collection<List<TraversalEdge<V, E>>> f = ff.get(FORWARD).aix.values();
                for(List<TraversalEdge<V,E>> x : f) {
                    for (TraversalEdge<V,E> te : x) {
                        System.out.println(Util.format(te.iterator()));
                    }
                    mapvf = mapvf +  x.size();
                }
                Collection<List<TraversalEdge<V, E>>> b = ff.get(BACKWARD).aix.values();
                for(List<TraversalEdge<V,E>> x : b) {
                    for (TraversalEdge<V,E> te : x) {
                        System.out.println(Util.format(te.iterator()));
                    }
                    mapvb = mapvb + x.size();
                }



                for( SearchProgressListener l : listeners ){
                    if(!l.onAdvance(forw, backw, mapvf, mapvb)){
                        break;
                    }
                }

                //concatenate matching paths
                for( V v : ff.get(FORWARD).vertices() ){
                    if( ff.get(FORWARD).hasPaths(v) && ff.get(BACKWARD).hasPaths(v) ) {

                        for( TraversalEdge<V,E> left : ff.get(FORWARD).paths(v) ) {

                            for ( TraversalEdge<V,E> right : ff.get(BACKWARD).paths(v) ){

                                double rank = this.arbiter.composeRanks(left, left.rank, right, right.rank);

                                if(rank > 0){
                                    LinkedList<Edge<V,E>> path = new LinkedList<>();
                                    left.traceLeft(path);
                                    right.traceRight(path);

                                    solutions.add(path);

                                    for( SearchProgressListener l : listeners ){
                                        if(!l.onPathFound(path)){
                                            return solutions;
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
                if (solutions.size() >= k) {
                    return solutions;
                }
            }
            return solutions;
        }

        TraversalEdge<V,E> makeTerminalEdge( V vertex ){
            TraversalEdge<V,E> te = new TraversalEdge<>();
            Edge<V,E> e = new Edge<>(vertex,null);
            te.init( e, null, new CumulativeRank(), vertex==this.targetNode );
            return te;
        }

      //
       class Frontier {
            // Affix (i.e., prefix or suffix) index -- by the end vertex
            Map<V, List<TraversalEdge<V,E>>> aix = new HashMap<>();
            boolean backward;

            Frontier(boolean backward){
                this.backward = backward;
            }

            void add(TraversalEdge<V,E> te){
                V v = te.vertex();
                if ( !aix.containsKey(v)){
                    aix.put(v, new LinkedList<TraversalEdge<V,E>>());
                }
                aix.get(v).add(te);
            }

            Set<V> vertices(){ return aix.keySet(); }

            boolean hasPaths(V vertex) {
                return aix.containsKey(vertex);
            }

            List<TraversalEdge<V,E>> paths( V vertex ){
                return aix.get(vertex);
            }

            Frontier advance(){
                Frontier f = new Frontier(this.backward);
                for( V v : aix.keySet() ){
                    List<TraversalEdge<V,E>> tes = aix.get(v);

                    for(TraversalEdge<V,E> te : tes ) {

                        if( te.e.vertex().toString().equals("427") ){
                            String a = "debug here";
                        }

                        List<TraversalEdge<V,E>> adj = te.adjacentEdges(gix, arbiter, this.backward);

                        for(TraversalEdge<V,E> sprout : adj){
                            try{
                                f.add(sprout);
                            }
                            catch(NullPointerException ex){
                                throw ex;
                            }
                        }
                    }
                }
                return f;
            }

            boolean isEmpty(){ return aix.isEmpty(); }
       }

       static class TraversalEdge<V,E> implements Iterable<Edge<V,E>> {
            CumulativeRank rank;
            Edge<V,E> e;
            TraversalEdge<V,E> prev = null;
            boolean backward = false; //for debugging (swapping end<->start in toString()) only

           //        @Loggable(skipArgs = true)
            public void init( Edge<V,E> edge, TraversalEdge prev, CumulativeRank rank, boolean backward ){
                this.e = edge;
                this.prev = prev;
                this.rank = rank;
                this.backward = backward;
            }

            public V vertex(){
                return e.vertex();
            }

            // @Loggable(skipArgs = true)
            public List<Edge<V,E>> traceLeft(LinkedList<Edge<V,E>> trace){
                trace.addFirst(e);
                return prev==null? trace : prev.traceLeft(trace);
            }

           // @Loggable(skipArgs = true)
           public List<Edge<V,E>> traceRight(List<Edge<V,E>> trace){
               if( prev==null ){
                   return trace;
               }
               trace.add(new Edge<V,E>(prev.vertex(),e.label()));
               return prev.traceRight(trace);
           }

            public boolean visitedEdge(V source, Edge<V,E> edge) {
                if (prev != null) {
                    if( Objects.equals(e, edge) && Objects.equals(prev.vertex(), source) ){
                        return true;
                    }
                    return prev.visitedEdge(source,edge);
                }
                return false;
            }

            public Iterator<Edge<V,E>> iterator(){
                return new Iter<V,E>(this);
            }


            public List<TraversalEdge<V,E>> adjacentEdges(GraphIndex gix, PathArbiter arb, boolean backward){
                List<TraversalEdge<V,E>> es = new LinkedList<>();

                Iterator<Edge<V,E>> edges = backward? gix.lookupEdges(null, e.vertex())
                                                    : gix.lookupEdges(e.vertex(), null);
                while (edges.hasNext()) {
                    Edge<V,E> sprout = edges.next();
                    PathArbiter.PathDecision pd = arb.rankEdge(sprout, this, rank, backward );

                    if( pd!=PathArbiter.PathDecision.PRUNE && !visitedEdge(e.vertex(),sprout) ) {
                        TraversalEdge<V,E> te = new TraversalEdge<>();
                        te.init(sprout, this, new CumulativeRank(), backward);
                        es.add(te);
                    }
                }
                return es;
            }

            @Override
            public String toString(){
                String sv = "("+e.vertex()+")";
                if( prev==null ){ return "["+sv+"]"; }
                String start = "("+prev.vertex()+")", end = sv;

                if(this.backward){ //swap direction (why we need the backward switch in the class at all)
                    sv = start; start = end; end = sv;
                }

                return start + "-"+ e.label() + "-" + end;
            }

           static class Iter<V,E> implements Iterator<Edge<V,E>>{
               TraversalEdge<V,E> te;

               public Iter( TraversalEdge<V,E> te ){this.te = te;}

               public boolean hasNext(){ return te!=null && te.prev!=null; }

               public Edge<V,E> next(){
                   Edge<V,E> ret = te.e;
                   te = te.prev;
                   return ret;
               }

               public void remove(){
                   throw new UnsupportedOperationException();
               }
           }

       }

    }
//
//
//}

package at.ac.wu.graphsense.search.patheval;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;
import at.ac.wu.graphsense.search.pathexpr.*;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

import java.util.*;

public class RegExpPathArbiter<V,E> implements PathArbiter<V,E> {

    Map<E,Character> edgeMap = new HashMap<>();

    Character otherChar = 'b';

    PathExpr<V,E> pe;
    RegExp re, reInv;
    RunAutomaton at,atInv;

    public RegExpPathArbiter(PathExpr<V,E> pathExpr) {
        this.pe = pathExpr;

        LabelCollector lc = new LabelCollector();
        pe.visit(lc);

        Character c = 'a';

        for( E e : lc.labels ){
            edgeMap.put( e, c++ );
        }
        otherChar = c;

        CharRegExpPrinter crp = new CharRegExpPrinter(edgeMap);
        pe.visit(crp);
        re = new RegExp( crp.s.pop() );

        PathExpr<V,E> peInv = pe.inverse();
        peInv.visit(crp);
        reInv = new RegExp( crp.s.pop() );
    }

    @Override
    public void init(GraphIndex<V, E> gi, V source, V target, boolean bidirectional) throws UnsupportedOperationException {

        at = new RunAutomaton(re.toAutomaton());
        atInv = new RunAutomaton( reInv.toAutomaton());
    }

    @Override
    public CumulativeRank rankEdge(Edge<V, E> edge, Iterable<Edge<V, E>> path, CumulativeRank pathRank, boolean forkRankObject, boolean backwardPath) {

        RunAutomaton ra = backwardPath? at : atInv;
        CumulativeRank rank = CumulativeRank.forkIfNeeded(pathRank, forkRankObject);

        int state = path.iterator().hasNext() ? (Integer) rank.getArbiterState() : ra.getInitialState();

        Character edgeEncoding = edgeMap.containsKey(edge.label()) ? edgeMap.get(edge.label()) : otherChar;

        int nextState = ra.step(state, edgeEncoding );

        if( nextState == -1 ){
            rank.setRank(CumulativeRank.PRUNE_PATH);
        }
        else{
            rank.setRank( CumulativeRank.MAX );
            rank.setArbiterState( nextState );
        }

        return rank;
    }

    @Override
    public double composeRanks(Iterable<Edge<V, E>> prefix, CumulativeRank prefixRank, Iterable<Edge<V, E>> suffix, CumulativeRank suffixRank) {

        StringBuilder sb = new StringBuilder();
        for(Edge<V,E> e : prefix){
            sb.append( encodeEdge(e) );
        }
        sb = sb.reverse();
        for(Edge<V,E> e : suffix){
            sb.append( encodeEdge(e) );
        }

        return at.run(sb.toString())? CumulativeRank.MAX : CumulativeRank.PRUNE_PATH;
    }

    @Override
    public Object getInitialState( boolean backward ){
        RunAutomaton a = backward? atInv : at;
        return a==null? null : a.getInitialState();
    }

    protected Character encodeEdge( Edge<V,E> edge ){
        E label = edge.label();
        return edgeMap.containsKey(label)? edgeMap.get(label) : otherChar;
    }

    class LabelCollector extends ExprVisitorBase<V,E> {
        Set<E> labels = new HashSet<>();
        @Override
        public void visitLink(Link<V, E> link) {
            labels.add( link.getEdge().label() );
        }

        @Override
        public void visitNegEdgeSet(NegEdgeSet<V, E> neset) {
            for( Edge<V,E> e : neset.getFWEdges() ){
                labels.add(e.label());
            }
        }
    }

    class CharRegExpPrinter implements ExprVisitor<V,E> {

        Stack<String> s = new Stack<>();
        Map<E,Character> enc;

        CharRegExpPrinter(){
            enc = new HashMap<>();
        }

        CharRegExpPrinter( Map<E,Character> encoding ){
            this.enc = encoding;
        }

        @Override
        public void visitLink(Link<V, E> link) {
            s.push( enc.get(link.getEdge().label())+link.modifier() );
        }

        @Override
        public void visitAlt(Alt<V, E> e) {
            iterate(e.inner(),e.modifier(),"|");
        }
        @Override
        public void visitSeq(Seq<V, E> e) {
            iterate(e.inner(),e.modifier());
        }
        @Override
        public void visitNegEdgeSet(NegEdgeSet<V, E> neset) {
            StringBuilder sb = new StringBuilder("[^");
            for( Edge<V,E> e : neset.getFWEdges() ){
                sb.append(enc.get(e.label()));
            }
            sb.append(']');
            s.push( sb + neset.modifier() );
        }
        void iterate (Collection<PathExpr<V,E>> coll, String modifier){
            iterate( coll, modifier,"");
        }
        void iterate( Collection<PathExpr<V,E>> coll, String modifier, String separator ){
            StringBuilder sb = new StringBuilder();

            for( PathExpr<V,E> i : coll  ){
                i.visit(this);
                String str = s.pop();
                if(sb.length()>0){
                    sb.append(separator);
                }
                sb.append(str);
            }
            s.push( sb.toString()+ modifier );

        }
    }

}


package at.ac.wu.graphsense.search.patheval;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.GraphIndex;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class RegExpPathArbiter<V,E> implements PathArbiter<V,E> {

    Map<E,Character> edgeMap = new HashMap<>();

    Character otherChar = 'b';

    String reString;
    RegExp re;

    RunAutomaton atForward;
    RunAutomaton atBackward;

    public RegExpPathArbiter(String regExp) {
        this.reString = regExp;
    }


    protected String edgePattern() {
        return "[a-zA-Z0-9]+";
    }

    protected E parseEdgeLabel (String edgeLabel){
        throw new UnsupportedOperationException();
    }

    public static RegExp reverseRegularExpression(RegExp re){
        return re;
    }

    @Override
    public void init(GraphIndex<V, E> gi, V source, V target, boolean bidirectional) throws UnsupportedOperationException {

        Character c = 'a';

        Map<String,Character> labelEncodings = new HashMap<>();

        Matcher m = Pattern.compile(edgePattern())
                .matcher(reString);

        while (m.find()) {
            String edgeLabel = m.group();
            E edge = parseEdgeLabel(edgeLabel);

            if( !edgeMap.containsKey(edge) ){
                labelEncodings.put(edgeLabel, c);
                c = Character.valueOf( (char) (c + 1));
            }
        }

        otherChar = Character.valueOf( (char) (c + 1));

        StringBuilder sbPattern = new StringBuilder();
            for( String label : labelEncodings.keySet() ){
            if(sbPattern.length()>0){
                sbPattern.append('|');
            }
            sbPattern.append(label);
        }

        m = Pattern.compile(sbPattern.toString())
                .matcher(reString);

        StringBuffer sb = new StringBuffer();

            while (m.find()) {
            m.appendReplacement(sb, labelEncodings.get(m.group()).toString());
        }
            m.appendTail(sb);

        re = new RegExp(sb.toString());

        atForward = new RunAutomaton(re.toAutomaton());
        atBackward = new RunAutomaton( reverseRegularExpression(re).toAutomaton());

    }

    @Override
    public CumulativeRank rankEdge(Edge<V, E> edge, Iterable<Edge<V, E>> path, CumulativeRank pathRank, boolean forkRankObject, boolean backwardPath) {

        RunAutomaton ra = backwardPath? atBackward : atForward;
        CumulativeRank rank = CumulativeRank.forkIfNeeded(pathRank, forkRankObject);

        int state = path.iterator().hasNext()? (Integer)rank.getArbiterState() : ra.getInitialState();

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
    public double composeRanks(Iterable<Edge<V, E>> prefix, CumulativeRank prefixRank, Iterable<Edge<V, E>> suffix, CumulativeRank suffixRank) throws UnsupportedOperationException {
        return CumulativeRank.MAX;
    }
}


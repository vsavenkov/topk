package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.*;
import at.ac.wu.graphsense.search.patheval.PathArbiter;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by vadim on 11.10.16.
 */
public class HDTGraphIndex implements GraphIndex<Integer,Integer>, VertexDictionary<Integer,String>, EdgeDictionary<Integer,String> {

    public final HDT hdt;
    public final org.rdfhdt.hdt.dictionary.Dictionary dict;

    private static final TripleComponentRole[] TRCOMP;

    static{
        TRCOMP = new TripleComponentRole[Edge.Component.values().length];
        TRCOMP[Edge.Component.SOURCE.ordinal()]=TripleComponentRole.SUBJECT;
        TRCOMP[Edge.Component.EDGE.ordinal()]=TripleComponentRole.PREDICATE;
        TRCOMP[Edge.Component.TARGET.ordinal()]=TripleComponentRole.OBJECT;
    }

    public HDTGraphIndex(String dataset, boolean indexed) {
        try {
            hdt = indexed ? HDTManager.loadIndexedHDT(dataset, null)
                    : HDTManager.loadHDT(dataset, null);
            dict = hdt.getDictionary();
        }
        catch( IOException ex ){
            throw new RuntimeException(ex);
        }
    }

    public HDTGraphIndex( HDT hdt ){
        this.hdt = hdt;
        this.dict = hdt.getDictionary();
    }

    @Override
    public Iterator<Edge<Integer,Integer>> lookupEdges(Integer source, Integer target){

        if(source == null){
            source = 0;
        }
        if(target == null){
            target = 0;
        }

        TripleID tripleid = new TripleID(source, 0, target);
        IteratorTripleID result = null;

        try {
            result = hdt.getTriples().search(tripleid);
        }
        catch(IndexOutOfBoundsException ex){

        }

        return result!=null? new TripleIterator(result, source.equals(0)) :
                new EmptyTripleIterator();
    }

    @Override
    public PathArbiter<Integer,Integer> createAllPassArbiter(){
        return new HDTAllPassArbiter();
    }

    public String entry(Integer key, Edge.Component component ){

        TripleComponentRole tcr;
        switch(component) {
            case SOURCE:
                tcr = TripleComponentRole.SUBJECT;
                break;
            case EDGE:
                tcr = TripleComponentRole.PREDICATE;
                break;
            default:
                tcr = TripleComponentRole.OBJECT;
        }

        return dict.idToString( key, tcr ).toString();
    }

    public Integer key(String entry, Edge.Component component ){

        TripleComponentRole tcr;
        switch(component) {
            case SOURCE:
                tcr = TripleComponentRole.SUBJECT;
                break;
            case EDGE:
                tcr = TripleComponentRole.PREDICATE;
                break;
            default:
                tcr = TripleComponentRole.OBJECT;
        }

        return dict.stringToId( entry, tcr );
    }

    @Override
    public String vertexEntry(Integer key, Edge.Component component ){
        return dict.idToString(key, TRCOMP[component.ordinal()]).toString();
    }

    @Override
    public Integer vertexKey(String entry, Edge.Component component ){
        return dict.stringToId(entry, TRCOMP[component.ordinal()]);
    }

    @Override
    public String edgeEntry(Integer key){
        CharSequence cs = dict.idToString(key, TripleComponentRole.PREDICATE);
        return cs!=null? cs.toString() : null;
    }

    @Override
    public Integer edgeKey(String entry){
        return dict.stringToId(entry, TripleComponentRole.PREDICATE);
    }

    public HDT getHDT(){
        return hdt;
    }


    class TripleIterator implements Iterator<Edge<Integer,Integer>>{

        IteratorTripleID it;
        boolean fetchSubjects;
        EdgeInt e = null;

        TripleIterator( IteratorTripleID it, boolean fetchSubjects ){
            this.it = it;
            this.fetchSubjects = fetchSubjects;

            //fetch the first element proactively, in case to prevent invalid vertices
            peekNext();
        }

        @Override
        public boolean hasNext() {
            return e!=null;
        }

        @Override
        public EdgeInt next(){
            EdgeInt ret = e;
            peekNext();
            return ret;
        }

        protected void peekNext() {

            e = null;

            while( e==null ){
                if( !it.hasNext() ) {
                    return;
                }
                TripleID tid = it.next();
                if( tid==null ){
                    return;
                }
                int vertex = fetchSubjects ? tid.getSubject() : tid.getObject();

                e = new EdgeInt( vertex, tid.getPredicate());
            }
        }

        @Override
        public void remove() {
            throw new RuntimeException("Remove not implemented");
        }
    }

    class EmptyTripleIterator implements Iterator<Edge<Integer,Integer>>{

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public EdgeInt next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new RuntimeException("Remove not implemented");
        }
    }

}

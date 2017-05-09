package at.ac.wu.graphsense.search.pathexpr;

import at.ac.wu.graphsense.Edge;
import dk.brics.automaton.RegExp;
import org.apache.jena.ext.com.google.common.collect.Lists;
import sun.awt.image.ImageWatched;

import java.util.*;

/**
 * Created by Vadim on 08.05.2017.
 */
public abstract class PathExpr<V,E> {

    Integer min, max;

    public Integer getMinOccurrences(){
        return min;
    }

    public Integer getMaxOccurrences(){
        return max;
    }

    public void setMinOccurrences( Integer min ){
        this.min = min;
    }

    public void setMaxOccurrences( Integer max ){
        this.max = max;
    }

    List<PathExpr<V,E>> is = new LinkedList<>();

    public abstract void visit(ExprVisitor<V,E> v);

    public void add( PathExpr<V,E> e ){
        is.add(e);
    }

    public List<PathExpr<V,E>> inner(){
        return Collections.unmodifiableList(is);
    }

    public PathExpr<V,E> inverse(){
        Inverter inv = new Inverter();
        this.visit(inv);
        return inv.s.peek();
    }

    public String modifier(){
        if( min==0 && max == null ){
            return "*";
        }
        if( min==1 && max == null ){
            return "+";
        }
        if( min==0 && max == 1 ){
            return "?";
        }
        if( min!=null && max != null ){
            return "{" + min +"," + max + "}";
        }
        return "";
    }

    public String toString(){
        Printer p = new Printer();
        visit(p);
        return p.s.isEmpty()? "" : p.s.pop();
    }

    class Printer implements ExprVisitor<V,E> {

        Stack<String> s = new Stack<>();

        @Override
        public void visitLink(Link<V, E> link) {
            s.push( link.toString() );
        }

        @Override
        public void visitAlt(Alt<V, E> e) {
            StringBuilder sb = new StringBuilder();

            for( PathExpr<V,E> i : is  ){
                i.visit(this);
                String str = s.pop();
                if(sb.length()>0){
                    sb.append("|");
                }
            }
            s.push( "("+sb.toString()+")"+e.modifier() );
        }

        @Override
        public void visitSeq(Seq<V, E> e) {
            StringBuilder sb = new StringBuilder();

            for( PathExpr<V,E> i : is  ){
                i.visit(this);
                String str = s.pop();
                if(sb.length()>0){
                    sb.append("/");
                }
            }
            s.push( "("+sb.toString()+")"+e.modifier() );
        }

        @Override
        public void visitNegEdgeSet( NegEdgeSet<V,E> neset ){
            StringBuilder sb = new StringBuilder();
            for( Edge<V,E> e : neset.fwEdges ){
                if( sb.length()>0 ){
                    sb.append(",");
                }
                sb.append(e.label());
            }
            s.push("!["+ sb.toString() + "]" + neset.modifier());
        }

    }

    class Inverter implements ExprVisitor<V,E>{

        Stack<PathExpr<V,E>> s = new Stack<>();

        @Override
        public void visitLink(Link<V, E> link) {
            s.push(link);
        }

        @Override
        public void visitAlt(Alt<V, E> alt) {
            Alt<V,E> altNew = new Alt<>();
            altNew.setMinOccurrences( alt.getMinOccurrences() );
            altNew.setMaxOccurrences( alt.getMaxOccurrences() );

            for( PathExpr<V,E> i : alt.inner() ){
                i.visit(this);
                altNew.add(s.pop());
            }
            s.push(altNew);
        }

        @Override
        public void visitSeq(Seq<V, E> seq) {
            LinkedList<PathExpr<V,E>> children = new LinkedList<>();

            for( PathExpr<V,E> i : seq.inner() ){
                i.visit(this);
                children.add(s.pop());
            }
            Seq<V,E> seqNew = new Seq<>();
            seqNew.setMinOccurrences( seq.getMinOccurrences() );
            seqNew.setMaxOccurrences( seq.getMaxOccurrences() );

            while( !children.isEmpty() ){
                seqNew.add( children.pollLast() );
            }
            s.push(seqNew);
        }

        @Override
        public void visitNegEdgeSet( NegEdgeSet<V,E> neset ){
            s.push(neset);
        }

    }

}

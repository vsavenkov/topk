package at.ac.wu.graphsense.search.pathexpr;

import dk.brics.automaton.RegExp;
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

    public void visit(ExprVisitor<V,E> v){
        for( PathExpr<V,E> ex : is  ){
            ex.visit(v);
        }
    }

    public void add( PathExpr<V,E> e ){
        is.add(e);
    }

    public List<PathExpr<V,E>> inner(){
        return Collections.unmodifiableList(is);
    }

    public PathExpr<V,E> inverse(){
        throw new UnsupportedOperationException();
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
    }
}

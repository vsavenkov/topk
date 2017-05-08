package at.ac.wu.graphsense.search.pathexpr;

import sun.awt.image.ImageWatched;

import java.util.LinkedList;
import java.util.List;

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

    List<PathExpr<V,E>> inner = new LinkedList<>();

    public <T> T visit(ExprVisitor<T,V,E> v){
        List<T> ts = new LinkedList<>();
        for( PathExpr<V,E> ex : inner  ){
            ts.add( ex.visit(v) );
        }
        return v.combine(ts);
    }
}

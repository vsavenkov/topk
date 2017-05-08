package at.ac.wu.graphsense.search.pathexpr;

/**
 * Created by Vadim on 08.05.2017.
 */
public class Alt<V,E> extends PathExpr<V,E> {

    public <T> T visit(ExprVisitor<T,V,E> v){
        return v.visitAlt(this);
    }
}

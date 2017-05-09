package at.ac.wu.graphsense.search.pathexpr;

/**
 * Created by Vadim on 08.05.2017.
 */
public class Alt<V,E> extends PathExpr<V,E> {

    public void visit(ExprVisitor<V,E> v){
        v.visitAlt(this);
    }
}

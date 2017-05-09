package at.ac.wu.graphsense.search.pathexpr;

/**
 * Created by Vadim on 09.05.2017.
 */
public abstract class ExprVisitorBase<V,E> implements ExprVisitor<V,E> {

    @Override
    public void visitAlt(Alt<V, E> e) {
        for( PathExpr<V,E> pe : e.inner() ){
            pe.visit(this);
        }
    }

    @Override
    public void visitSeq(Seq<V, E> e) {
        for( PathExpr<V,E> pe : e.inner() ){
            pe.visit(this);
        }
    }
}

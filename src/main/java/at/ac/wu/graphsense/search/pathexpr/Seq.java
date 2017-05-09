package at.ac.wu.graphsense.search.pathexpr;

/**
 * Created by Vadim on 08.05.2017.
 */
public class Seq<V,E> extends PathExpr<V,E> {

    @Override
    public void visit(ExprVisitor<V,E> v){
        v.visitSeq(this);
    }
}

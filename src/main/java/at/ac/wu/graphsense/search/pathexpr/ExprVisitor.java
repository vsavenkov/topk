package at.ac.wu.graphsense.search.pathexpr;

import java.util.Collection;

/**
 * Created by Vadim on 08.05.2017.
 */
public interface ExprVisitor<V,E> {
    void visitLink(Link<V,E> link);

    void visitAlt(Alt<V,E> alt);

    void visitSeq(Seq<V,E> seq);

    void visitNegEdgeSet( NegEdgeSet<V,E> neset );
}

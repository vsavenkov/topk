package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.EdgeDictionary;
import at.ac.wu.graphsense.search.pathexpr.Alt;
import at.ac.wu.graphsense.search.pathexpr.Link;
import at.ac.wu.graphsense.search.pathexpr.PathExpr;
import at.ac.wu.graphsense.search.pathexpr.Seq;
import org.apache.jena.sparql.path.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Vadim on 08.05.2017.
 */
public class PathExprFactory {

    public static PathExpr<Integer,Integer> createPathExpr(Path path, EdgeDictionary<Integer,String> ed){
        PathExprVisitor pev = new PathExprVisitor(ed);

        path.visit(pev);

        return pev.s.pop();
    }

    static class PathExprVisitor extends org.apache.jena.sparql.path.PathVisitorBase {

        Stack<PathExpr<Integer,Integer>> s = new Stack<>();

        EdgeDictionary<Integer,String> ed;

        PathExprVisitor(EdgeDictionary<Integer,String> ed){
            this.ed = ed;
        }

        @Override
        public void visit(P_Link p_link) {
            Integer val = ed.edgeKey(p_link.getNode().getURI());
            s.push( new Link(val!=null? val : Integer.MAX_VALUE ) );
        }

        @Override
        public void visit(P_Alt p_alt) {
            visit2(p_alt, new Alt());
        }

        @Override
        public void visit(P_Seq p_seq) {
            visit2(p_seq, new Seq());
        }

        protected void visit2(P_Path2 p2, PathExpr pe){
            p2.getLeft().visit(this);
            p2.getRight().visit(this);
            List<PathExpr<Integer,Integer>> options = new LinkedList<>();
            pe.add(s.pop());
            pe.add(s.pop());
            s.push(pe);
        }

        @Override
        public void visit(P_ZeroOrMore1 p_zeroOrMore) {
            visit1(p_zeroOrMore, 0, null);
        }

        @Override
        public void visit(P_ZeroOrOne p_zeroOrOne) {
            visit1(p_zeroOrOne, 0, 1);
        }

        @Override
        public void visit(P_OneOrMore1 p_oneOrMore) {
            visit1(p_oneOrMore, 1, null);
        }

        protected void visit1(P_Path1 p1, Integer min, Integer max){
            p1.getSubPath().visit(this);
            PathExpr<Integer,Integer> pe = s.pop();
            pe.setMinOccurrences(min);
            pe.setMaxOccurrences(max);
        }

/*
        @Override
        public void visit(P_ReverseLink p_reverseLink) {

        }

        @Override
        public void visit(P_NegPropSet p_negPropSet) {

        }

        @Override
        public void visit(P_Inverse p_inverse) {

        }

        @Override
        public void visit(P_Mod p_mod) {

        }

        @Override
        public void visit(P_FixedLength p_fixedLength) {

        }

        @Override
        public void visit(P_Distinct p_distinct) {

        }

        @Override
        public void visit(P_Multi p_multi) {

        }

        @Override
        public void visit(P_Shortest p_shortest) {

        }

        @Override
        public void visit(P_ZeroOrMoreN p_zeroOrMoreN) {

        }

        @Override
        public void visit(P_OneOrMoreN p_oneOrMoreN) {

        }
         */
    }

}

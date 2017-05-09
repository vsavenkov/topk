package at.ac.wu.graphsense.hdt;

import at.ac.wu.graphsense.Edge;

/**
 * Created by "Vadim Savenkov" on 21.11.16.
 */
public class EdgeInt extends Edge<Integer,Integer> {
    public EdgeInt(Integer vertex, Integer label){
        super(vertex,label);
    }
    public EdgeInt(EdgeInt e){
        super(e);
    }
}

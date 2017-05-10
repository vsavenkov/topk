package at.ac.wu.graphsense.search.patheval;

public class CumulativeRank {

    public static final double PRUNE_PATH = -1;
    public static final double MAX = 1;
    public static final double MIN_POSITIVE = 0;


    double rank;
    Object arbiterState;

    public CumulativeRank( ){}

    public CumulativeRank(double rank){
        this.rank = rank;
    }

    public CumulativeRank(double rank, Object arbiterState){
        this.rank = rank;
        this.arbiterState = arbiterState;
    }

    public double getRank(){ return rank; }
    public void setRank( double rank ){ this.rank = rank; }

    public Object getArbiterState(){ return arbiterState; }
    public void setArbiterState(Object arbiterState){ this.arbiterState = arbiterState; }

    public boolean isPruneRank(){ return isPruneRank(rank); }

    public void forcePrune(){ rank = PRUNE_PATH; }

    public static boolean isPruneRank(double rank){ return rank < 0; }

    public static CumulativeRank forkIfNeeded(CumulativeRank old, Boolean needed){
        CumulativeRank ret = old;
        if( needed ){
            ret = new CumulativeRank( old.getRank() );
            ret.setArbiterState(old.getArbiterState());
        }
        return ret;
    }
}

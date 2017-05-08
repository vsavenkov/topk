package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.TestUtil;
import at.ac.wu.graphsense.search.patheval.RegExpPathArbiter;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by vadim on 04.05.17.
 */
public class RegExpPathArbiterTest {

    @Test
    public void genericRE_label_star() throws Exception {

        RegExpPathArbiter rpa = new RegExpPathArbiter("Label*") {
            @Override
            protected String edgePattern() {
                return super.edgePattern();
            }

            @Override
            protected Object parseEdgeLabel (String edgeLabel){
                return super.edgePattern();
            }
        };

        assertTrue(true);
    }


}

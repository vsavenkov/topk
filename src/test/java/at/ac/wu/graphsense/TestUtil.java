package at.ac.wu.graphsense;

import at.ac.wu.graphsense.hdt.HDTGraphIndex;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.rdfhdt.hdt.dictionary.Dictionary;
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by "Vadim Savenkov" on 21.11.16.
 */
public class TestUtil {
    public final static String SOURCE = "s";
    public final static String TARGET = "t";
    public final static String PREDICATE = "e";
    public final static String NAMESPACE = "http://ns/";
    public final static String QSOURCE = NAMESPACE + SOURCE;
    public final static String QTARGET = NAMESPACE + TARGET;
    public final static String QPREDICATE = NAMESPACE + PREDICATE;

    private final static String QTARGET_EX = QTARGET + "_end_";

    static final Logger _log = LoggerFactory.getLogger(TestUtil.class);

    public static HDT createVertexLabeledGraph(String spec)
    {
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("",NAMESPACE);
        Property prop = model.createProperty(NAMESPACE, PREDICATE);

        if(spec == null)
            throw new NullPointerException("Model spec cannot be null");
        String[] paths = spec.split("\\.");

        for(int p=0; p<paths.length; p++)
        {
            String[] path = paths[p].split("-");
            for(int iprev=0, i=1; i<path.length; i++)
            {
                String  sA = NAMESPACE+path[iprev].trim(),
                        sB = NAMESPACE+path[i].trim();

                Resource rA = model.getResource(sA)==null ?
                        model.getResource(sA) : model.createResource(sA),
                        rB = model.getResource(sB)==null ?
                                model.getResource(sB) : model.createResource(sB);

                rA.addProperty(prop,rB);
                iprev = i;
            }
        }

        return createHDT(model);
    }

    /**
     * Add a mock property to the TARGET node to ensure it occurs in a
     * subject position, since HDT does not seem to support search by object
     *
     * @param model
     * @param prop Property name to use
     */
    private static void addTail(Model model, Property prop)
    {
        String sT = QTARGET, sTex = QTARGET_EX;
        Resource rT   = model.getResource(sT)==null ?
                model.getResource(sT) : model.createResource(sT),
                rTex = model.getResource(sTex)==null ?
                        model.getResource(sTex) : model.createResource(sTex);

        rT.addProperty(prop,rTex);
        //rTex.addProperty(prop,rTex);
    }

    public static Model createLabeledGraph(String modelTurtle){
        return createLabeledGraph(modelTurtle, null);
    }

    public static Model createLabeledGraph(String modelTurtle, String defaultNamespace)
    {
        Model model = ModelFactory.createDefaultModel();

        if( defaultNamespace!=null && !defaultNamespace.isEmpty() ) {
            model.setNsPrefix("", defaultNamespace);
            modelTurtle = "@prefix : <" + defaultNamespace + ">\n" + modelTurtle;
        }

        model.read(new ByteArrayInputStream(modelTurtle.getBytes()), null, "TURTLE");

        return model;
    }

    public static List<Integer> encodeVertices(String[] vertices, Dictionary dict) {

        List<Integer> path = new ArrayList<>(vertices.length);

        for(int i =0; i< vertices.length; i++ ){
            path.add(dict.stringToId(NAMESPACE+vertices[i], TripleComponentRole.SUBJECT));
        }
        return path;
    }
    public static class Graph
    {
        public HDT hdt;
                public GraphIndex<Integer,Integer> gix;
        public VertexDictionary<Integer,String> vdict;
        public EdgeDictionary<Integer,String> edict;

        public int start = -1, target = -1, edge = -1, numPaths = 0;

        public Graph(String spec, int numPaths)
        {
            hdt = TestUtil.createVertexLabeledGraph(spec);
            HDTGraphIndex hdtgix = new HDTGraphIndex(hdt);
            gix = hdtgix;
            vdict = hdtgix;
            edict = hdtgix;

            start = vdict.vertexKey(TestUtil.QSOURCE, Edge.Component.SOURCE);
            target = vdict.vertexKey(TestUtil.QTARGET, Edge.Component.TARGET);
            edge = edict.edgeKey(TestUtil.QPREDICATE);

            this.numPaths = numPaths;
        }
    }

    public static HDT createHDT(Model model) {

        // since HDT Index can only search for subjects, ensure that
        // our target node occurs in a subject position in the graph:
        Property prop = model.createProperty(NAMESPACE, PREDICATE);
        addTail(model,prop);

        // log it if needed (see log4j.properties to tune logging verbosity)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        model.write(baos, "TURTLE");

        try {
            _log.debug(baos.toString("UTF8"));

            File tmp = new File("testmodel.0.tmp");
            tmp.deleteOnExit();

            model.write( new FileOutputStream(tmp), "TURTLE");

            HDT hdt = HDTManager.generateHDT(tmp.getAbsolutePath(), NAMESPACE,
                    RDFNotation.TURTLE, new EmptyHDTOptions(), null);
            return hdt;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static public <V,E> void printSolutions(Collection<List<Edge<V,E>>> solutions, GraphIndex gix){
        System.out.println("Found the following solutions");
        for(List<Edge<V,E>> path: solutions) {
            System.out.println(Util.format(path,gix));
        }
    }

    static class EmptyHDTOptions implements HDTOptions{

        @Override
        public String get(String s) {
            return null;
        }

        @Override
        public void set(String s, String s1) {

        }

        @Override
        public void setOptions(String s) {

        }

        @Override
        public long getInt(String s) {
            return 0;
        }

        @Override
        public void setInt(String s, long l) {

        }

        @Override
        public void clear() {

        }
    }

}

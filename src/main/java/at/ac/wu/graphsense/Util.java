package at.ac.wu.graphsense;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by vadim on 18.05.16.
 */
public class Util
{

    /**
     * Expand the multiedges.
     * @param p A vertex sequence.
     * @param gi {@code GraphIndex} object from which edgeSequence between adjacent vertexSequence of {@code p} are obtained.
     * @return Set of paths represented by {@code p}.
     */
    public static Set<Path> labelEdges(List<Integer> p, GraphIndex gi)
    {
        return new Multipath(p, gi).enumeratePaths();
    }

    static public void printPaths(Collection<List<Edge<Integer,Integer>>>paths, GraphIndex gi, String key){
        final Logger _log = LoggerFactory.getLogger(Path.class);

        PrintWriter writer;
        try{
            File file = new File("./test_output/" + key);
            file.getParentFile().mkdirs();
            writer = new PrintWriter(new FileOutputStream(file, false));
        }
        catch(FileNotFoundException ex){ return; }

        for (List<Edge<Integer,Integer>> path : paths) {
            _log.debug(format(path,gi));
            writer.println(format(path,gi));
        }
        writer.close();
    }

    /**
     * Format a path using the integer-based representation.
     *
     * @param path A path to convert into string.
     * @return
     */
    public static String format(Path path){
        if( !path.isEdgeLabeled() ){
            return path.toString();
        }
        else{
            StringBuilder sb = new StringBuilder();
            Iterator vs = path.vertexList().iterator();
            for( Object e : path.edgeSequence() ){
                if(sb.length()==0){
                    sb.append("[(");
                    sb.append(vs.next());
                }
                sb.append(")-"); sb.append(e); sb.append("-(");
                sb.append(vs.next());
            }
            if( sb.length()>0 ) {
                sb.append(")]");
            }
            return sb.toString();
        }
    }

    /**
     * Format a path using the integer-based representation.
     *
     * @param path A path to convert into string.
     * @return
     */
    public static <V,E> String format(Collection<Edge<V,E>> path){
        return format(path.iterator());
    }
    /**
     * Format a path using the integer-based representation.
     *
     * @param path A path to convert into string.
     * @return
     */
    public static <V,E> String format(Iterator<Edge<V,E>> path){

        StringBuilder sb = new StringBuilder();
        while( path.hasNext() ){
            Edge<V,E> e = path.next();
            if(sb.length()==0){
                sb.append("[(");
                sb.append(e.vertex());
                continue;
            }
            sb.append(")-");
            sb.append(e.label());
            sb.append("-(");
            sb.append(e.vertex());
        }
        if( sb.length()>0 ) {
            sb.append(")]");
        }
        return sb.toString();
    }

    public static String format(Path path, GraphIndex gi){
        VertexDictionary vertexDict = gi instanceof VertexDictionary ? (VertexDictionary) gi : TrivialDictionary.instance();
        EdgeDictionary edgeDict = gi instanceof EdgeDictionary ? (EdgeDictionary) gi : TrivialDictionary.instance();

        return format(path, vertexDict, edgeDict);
    }

    /**
     * Format a path, translating integers using the {@code Dictionary} of the {@code GraphIndex} object.
     *
     * @param path A path to convert into string.
     * @param vDict {@code VertexDictionary} for string conversion.
     * @param eDict {@code EdgeDictionary} for string conversion.
     * @return
     */
    public static String format(Path path, VertexDictionary vDict, EdgeDictionary eDict )
    {
        if( vDict == null ){
            vDict = TrivialDictionary.instance();
        }
        if( eDict == null ){
            eDict = TrivialDictionary.instance();
        }


        StringBuilder sb = new StringBuilder();

        if( !path.isEdgeLabeled() ){
            sb.append('[');
            for(Object v : path.vertexSequence()) {
                sb.append( trimNS(vDict.vertexEntry(v, Edge.Component.SOURCE).toString()) );
                sb.append(",");
            }
            sb.setCharAt(sb.length()>1? sb.length()-1 : 1,']');
        }
        else{
            for( Object oe : path.edgeSequence() ){
                Edge e = (Edge)oe;
                if(sb.length()==0){
                    sb.append("[(");
                    sb.append(trimNS(vDict.vertexEntry(e.vertex(),Edge.Component.SOURCE).toString()));
                }
                sb.append(")-");
                sb.append(trimNS(eDict.edgeEntry(e.label()).toString(), true));
                sb.append("-(");
                sb.append(trimNS(vDict.vertexEntry(e.vertex(),Edge.Component.TARGET).toString()));
            }
            if( sb.length()>0 ) {
                sb.append(")]");
            }
            return sb.toString();
        }
        return sb.toString();
    }

    public static <V,E> String format(Collection<Edge<V,E>> path, GraphIndex gi) {
        return format(path.iterator(), gi);
    }
    public static <V,E> String format(Iterator<Edge<V,E>> path, GraphIndex gi){
        VertexDictionary vertexDict = gi instanceof VertexDictionary ? (VertexDictionary) gi : TrivialDictionary.instance();
        EdgeDictionary edgeDict = gi instanceof EdgeDictionary ? (EdgeDictionary) gi : TrivialDictionary.instance();
        return format(path, vertexDict, edgeDict);
    }


    /**
     * Format a path, translating integers using the {@code Dictionary} of the {@code GraphIndex} object.
     *
     * @param path A path to convert into string.
     * @param vDict {@code VertexDictionary} for string conversion.
     * @param eDict {@code EdgeDictionary} for string conversion.
     * @return
     */
    public static <V,E> String format(List<Edge<V,E>> path, VertexDictionary vDict, EdgeDictionary eDict ){
        return format(path.iterator(),vDict,eDict);
    }

    public static <V,E> String format(Iterator<Edge<V,E>> path, VertexDictionary vDict, EdgeDictionary eDict )
    {
        if( vDict == null ){
            vDict = TrivialDictionary.instance();
        }
        if( eDict == null ){
            eDict = TrivialDictionary.instance();
        }

        StringBuilder sb = new StringBuilder();

        while( path.hasNext() ){
            Edge<V,E> e = path.next();
            if(sb.length()==0){
                sb.append("[(");
                sb.append(trimNS(vDict.vertexEntry(e.vertex(),Edge.Component.SOURCE)));
                continue;
            }
            sb.append(")-");
            sb.append(trimNS(eDict.edgeEntry(e.label()), true));
            sb.append("-(");
            sb.append(trimNS(vDict.vertexEntry(e.vertex(),Edge.Component.TARGET)));
        }
        sb.append(")]");
        return sb.toString();
    }



    /**
     * Trim long namespaces at RDF IRIs.
     * @param URI
     * @param soft keep the last part of the URI (namespace): makes sense for some ontologies
     *        having properties differing in namespaces but not in local names.
     * @param ignore ignore the operation altogether (makes the calling code cleaner)
     * @return local URI
     */
    public static String trimNS( String URI, boolean soft, boolean ignore ){
        if( ignore ){ return URI; }
        int trimPos = URI.lastIndexOf('#');
        if( trimPos <= 0 ) {
            trimPos = URI.lastIndexOf('/');
        }
        if( soft && trimPos > 0 ){
            int penultimateSlash = URI.substring(0,trimPos-1).lastIndexOf('/');
            trimPos = penultimateSlash > 0 ? penultimateSlash : 0;
        }
        return trimPos>0? URI.substring(trimPos+1) : URI;
    }

    public static String trimNS( Object URI, boolean soft ){
        if( URI == null ){
            return "";
        }
        return trimNS(URI.toString(),soft, false);
    }

    public static String trimNS( Object URI ){
        return trimNS(URI,false);
    }

}

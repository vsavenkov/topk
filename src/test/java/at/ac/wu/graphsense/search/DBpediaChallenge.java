package at.ac.wu.graphsense.search;

import at.ac.wu.graphsense.Edge;
import at.ac.wu.graphsense.HDTGraphIndex;
import at.ac.wu.graphsense.Util;
import at.ac.wu.graphsense.VertexDictionary;
import org.rdfhdt.hdt.dictionary.*;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by Vadim on 21.11.2016.
 */
public class DBpediaChallenge {

        public static void main(String[] args) throws IOException, NotFoundException {
            HashMap<String, String> testcases = new LinkedHashMap<>();

            boolean doPrint = false;
            boolean doCheck = true;


            //testcases.put("task2_q4_1", "1,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,http://dbpedia.org/ontology/president");
            testcases.put("task1_q4_2", "2,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,no");
            testcases.put("task1_q2_3", "3,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,no");
            //testcases.put("task2_q2_3", "3,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/after");
            testcases.put("task1_q2_4", "4,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,no");
            //testcases.put("task2_q2_4", "4,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/after");
            //testcases.put("task2_q4_6", "6,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,http://dbpedia.org/ontology/president");
            testcases.put("task1_q1_8", "8,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,no");
            //testcases.put("task2_q3_12", "12,http://dbpedia.org/resource/Karl_W._Hofmann,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/predecessor");

            testcases.put("task1_q4_16", "16,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,no");
            //testcases.put("task2_q1_32", "32,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,http://dbpedia.org/property/firstWin");
            testcases.put("task1_q3_36", "36,http://dbpedia.org/resource/Karl_W._Hofmann,http://dbpedia.org/resource/Elliot_Richardson,no");
            //testcases.put("task2_q4_72", "72,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,http://dbpedia.org/ontology/president");
            //testcases.put("task2_q2_76", "76,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/after");
            //testcases.put("task2_q3_76", "76,http://dbpedia.org/resource/Karl_W._Hofmann,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/predecessor");
            testcases.put("task1_q2_79", "79,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,no");
            //testcases.put("task2_q1_98", "98,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,http://dbpedia.org/property/firstWin");

            //disjunctive filter
            //testcases.put("task2_q2_151", "151,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/after");
            testcases.put("task1_q2_154", "154,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,no");
            testcases.put("task1_q4_250", "250,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,no");
            testcases.put("task1_q3_336", "336,http://dbpedia.org/resource/Karl_W._Hofmann,http://dbpedia.org/resource/Elliot_Richardson,no");
            testcases.put("task1_q1_344", "344,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,no");


            //testcases.put("task2_q4_614", "614,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,http://dbpedia.org/ontology/president");
            testcases.put("task1_q1_1068", "1068,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,no");
            //testcases.put("task2_q3_1440", "1440,http://dbpedia.org/resource/Karl_W._Hofmann,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/predecessor");
            //testcases.put("task2_q1_1914", "1914,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,http://dbpedia.org/property/firstWin");
            testcases.put("task1_q4_1906", "1906,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,no");
            //TOO SLOW:
            //testcases.put("task2_q2_2311", "2311,http://dbpedia.org/resource/1952_Winter_Olympics,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/after");
            testcases.put("task1_q3_4866", "4866,http://dbpedia.org/resource/Karl_W._Hofmann,http://dbpedia.org/resource/Elliot_Richardson,no");
            //testcases.put("task2_q4_5483", "5483,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,http://dbpedia.org/ontology/president");
            //testcases.put("task2_q3_8088", "8088,http://dbpedia.org/resource/Karl_W._Hofmann,http://dbpedia.org/resource/Elliot_Richardson,http://dbpedia.org/property/predecessor");
            //TOO SLOW:
            //testcases.put("task2_q1_16632", "16632,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,http://dbpedia.org/property/firstWin");
            testcases.put("task1_q1_20152", "20152,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,no");
            testcases.put("task1_q4_20224", "20224,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,no");
            //testcases.put("task2_q4_52649", "52649,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,http://dbpedia.org/ontology/president");
            testcases.put("task1_q4_175560", "175560,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,no");
            //TOO SLOW:
            //testcases.put("task2_q1_212988", "212988,http://dbpedia.org/resource/Felipe_Massa,http://dbpedia.org/resource/Red_Bull,http://dbpedia.org/property/firstWin");
            //TOO SLOW:
            //testcases.put("task2_q4_471199", "471199,http://dbpedia.org/resource/James_K._Polk,http://dbpedia.org/resource/Felix_Grundy,http://dbpedia.org/ontology/president");


            //Challenge
            String dataset = "training_dataset.hdt"; //"evaluation_dataset.hdt";

            long start = System.currentTimeMillis();
            HDTGraphIndex hdt = new HDTGraphIndex(dataset, true); //false: non-indexed (default) | true : indexed
            VertexDictionary<Integer,String> vd = (VertexDictionary<Integer,String>)hdt;
            long end = System.currentTimeMillis();
            System.out.println("Time elapsed to load HDT file: "+ (end - start)+" ms");

            System.out.println("** The first 4 tests comprise the Experiment 2 of the IESD 2016 submission **");

            if( doPrint ) {
                System.out.println("** NOTE: Output is in the test_output folder **");
            }

            for (Map.Entry<String, String> entry : testcases.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println("Testcase: " + key + " Conditions: " + value);
                String[] cond = value.split(",");
                int k = Integer.parseInt(cond[0]);
                String root = cond[1];
                String target = cond[2];
                String edge = cond[3];
                if (edge.equalsIgnoreCase("no")) {
                    edge = null;
                }
                BidirectionalTopK topK = new BidirectionalTopK();
                topK.init(hdt);

                start = System.currentTimeMillis();
                Collection<List<Edge<Integer,Integer>>> results;
                try {
                    results = topK.run(vd.vertexKey(root, Edge.Component.SOURCE)
                                      ,vd.vertexKey(target, Edge.Component.TARGET)
                                      ,k
                                      ,null);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                end = System.currentTimeMillis();

                System.out.println("Time elapsed to find top " + k + " paths: " + (end - start) + " ms");


                if( doPrint ) {
                    start = System.currentTimeMillis();
                    //Util.printPaths(results, hdt, key);
                    end = System.currentTimeMillis();
                    System.out.println("Time elapsed to print paths: " + (end - start) + " ms");
                }

                if( doCheck ) {

                    org.rdfhdt.hdt.dictionary.Dictionary hdtDict = hdt.getHDT().getDictionary();

                    Set<List<Edge<Integer,Integer>>> reference = loadCorrectResults(key, hdtDict);
                    Set<List<Edge<Integer,Integer>>> extraPaths = new HashSet<>();
                    if (!reference.isEmpty()) {
                        int refSize = reference.size();
                        for (List<Edge<Integer,Integer>> path : results) {
                            if (reference.contains(path)) {
                                reference.remove(path);
                            } else {
                                extraPaths.add(path);
                            }
                        }
                        if (!reference.isEmpty()) {
                            System.out.println(" *** Some correct results have not been found! *** ");
                            for (List<Edge<Integer,Integer>> p : reference) {
                                System.out.println(Util.format(p, hdt));
                            }
                        }
                        if (!extraPaths.isEmpty()) {
                            System.out.println(
                                    String.format(" *** Some extra paths have been found (reference: %d, found: %d)! *** "
                                            , refSize, results.size()));
                            //for (Path p : extraPaths) {
                            //    System.out.println(Util.format(p, hdtGIdx));
                            //}
                        }
                    }
                }
            }
        }

        static Set<List<Edge<Integer,Integer>>>
        loadCorrectResults(String key, org.rdfhdt.hdt.dictionary.Dictionary dict ) throws FileNotFoundException {
            Set<List<Edge<Integer,Integer>>> paths = new HashSet<>();
            try {
                Scanner s = new Scanner(new File("training_result_files/" + key + ".txt"));
                while (s.hasNextLine()) {
                    List<Edge<Integer,Integer>> path = parsePath(s.nextLine(), dict);
                    if (!path.isEmpty()) {
                        paths.add(path);
                    }
                }
                s.close();
            }
            catch( FileNotFoundException ex ){
                System.err.println("Cannot verify the results: results for " + key + " not found");
            }
            return paths;
        }
        static List<Edge<Integer,Integer>> parsePath(String strPath, org.rdfhdt.hdt.dictionary.Dictionary dict){
            List<Edge<Integer,Integer>> path = new LinkedList();
            String[] steps = strPath.split("\",\\s+\"");
            if( steps.length > 2 ) {
                steps[0] = steps[0].substring(steps[0].indexOf("http"));
                String last = steps[steps.length-1];
                steps[steps.length-1] = last.substring(0,last.lastIndexOf('\"'));
                path.add(new Edge<Integer, Integer>(dict.stringToId(steps[0], TripleComponentRole.SUBJECT),null));
                for (int i = 1; i + 1 < steps.length; i += 2) {
                    path.add(new Edge<Integer, Integer>(dict.stringToId(steps[i+1], TripleComponentRole.OBJECT),
                                                        dict.stringToId(steps[i], TripleComponentRole.PREDICATE)));
                }
            }
            return path;
        }


}

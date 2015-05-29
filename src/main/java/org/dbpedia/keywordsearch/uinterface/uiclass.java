
package org.dbpedia.keywordsearch.uinterface;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.dbpedia.keywordsearch.importer.neo4j;
import org.dbpedia.keywordsearch.serverproperties.pathvariables;
import org.dbpedia.keywordsearch.indexer.ESNode;
import org.elasticsearch.search.SearchHit;
import org.neo4j.graphdb.GraphDatabaseService;
/**
 *
 * @author enigmatus
 */
public class uiclass {
    private pathvariables Instance;
    public uiclass() throws IOException{
            this.Instance=new pathvariables();
    }
    private File[] rdffileiterator(){
        File folder = new File(this.Instance.getrdf());
        File[] listOfFiles = folder.listFiles();
        return listOfFiles;
    }
    private String graphpath(){
        
        return this.Instance.getgraph();
    }
    public static void main(String[] args) throws IOException {
        /*Cluster Initialization*/
        ESNode esnode=new ESNode("DBpediacluster");
       
        /*Indexing of classes*/
        esnode.lemoncluster("./resources/dbpedia_3Eng_class.ttl","classes");
        
        /*Indexing of Properties*/
        esnode.lemoncluster("./resources/dbpedia_3Eng_property.ttl","properties");
        
        /*Enriching them with surfaceforms*/
        esnode.rdfcluster("./resources/en_surface_forms.ttl", "surfaceforms");
        
        /*Indexing DBpedia labels*/
        esnode.rdfcluster("./resources/dbpedia_labels.ttl", "dbpedialabels");
        
        
        /*A simple query and results*/
        SearchHit[] results=esnode.transportclient("politician","classes");
        for (SearchHit hit : results) {
            System.out.println("................................");
            Map<String,Object> result = hit.getSource();
            System.out.println(result);
        }
        
        /* extracting paths where the graphdb has to be formed*/
        uiclass pathsetter =new uiclass();
        File[] listoffiles = pathsetter.rdffileiterator();
        String graphpath = pathsetter.graphpath();
        
        /* Formation of graph database at specified path*/
        neo4j graphdb = new neo4j(graphpath);
        GraphDatabaseService gdb=graphdb.getgdbservice();
        for (File file : listoffiles) { 
          if (file.isFile()) /*extracting all the files in the specified folder*/ {
               graphdb.graphdbform(gdb, pathsetter.Instance.getrdf()+'/'+file.getName());
           }
        }
    }
}

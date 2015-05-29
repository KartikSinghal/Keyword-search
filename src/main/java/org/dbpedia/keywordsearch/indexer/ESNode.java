
package org.dbpedia.keywordsearch.indexer;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.dbpedia.keywordsearch.importer.jena;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.elasticsearch.search.SearchHit;

/**
 *
 * @author enigmatus
 */
public class ESNode {
    
    private final Node node;
    private Client client;
    
    public ESNode(String clustername){
        /* Initialization of cluster*/
        this.node=nodeBuilder().clusterName(clustername).node();
        /* Starting the central server */
        this.client=this.node.client();
    }
    
    public void lemoncluster(String labelspath, String path) throws FileNotFoundException, IOException{
         /* Data cleaning and preprocessing*/
        BufferedReader in = new BufferedReader(new FileReader(labelspath));
        String line="";
        StringTokenizer st=null;
        int i=0;
        System.out.println("Building the "+path+" data...........");
        while((line=in.readLine())!=null){
           i++;
           st=new StringTokenizer(line," ");
           String token1=st.nextToken();
           st.nextToken();
           String token2=st.nextToken();
           
           /* Indexing the data in the central server*/
           IndexResponse response = this.client.prepareIndex(path, "mappings", String.valueOf(i))
            .setSource(jsonBuilder()
                        .startObject()
                            .field("uri", token1)
                            .field("label", token2)
                        .endObject()
                      )
            .execute()
            .actionGet();
        }    
        System.out.println("Data Entry completed");
    }
    
    public void rdfcluster(String labelspath,String path) throws FileNotFoundException, IOException{
        /* Reading the rdf data with jena */
        StmtIterator iter=jena.jena(labelspath);
        int i=0;
        System.out.println("Building the "+path+" data...........");
        while(iter.hasNext()){
           Statement surfaceform=iter.next();
           i++;
           Resource s= surfaceform.getSubject();
           RDFNode o=surfaceform.getObject();
           
           /* Indexing the data in the central server */
           IndexResponse response = this.client.prepareIndex(path, "mappings", String.valueOf(i))
            .setSource(jsonBuilder()
                        .startObject()
                            .field("uri", s.toString())
                            .field("label", o.toString())
                        .endObject()
                      )
            .execute()
            .actionGet();
        }    
        System.out.println("Data Entry completed");
    }
    
    public SearchHit[] transportclient(String query, String path){
        /* Connecting the remote client with the central cluster */
        Client clientremote = this.node.client();
        
        /* Building the Query */
        MatchQueryBuilder qb = QueryBuilders.matchQuery("label", query);
        SearchRequestBuilder srb = clientremote.prepareSearch(path).setTypes("mappings");
        SearchResponse retrieved = srb.setQuery(qb).execute().actionGet();
        
        /* Retrieving the results from the query */
        SearchHit[] results = retrieved.getHits().getHits();
        return results;
    }
}

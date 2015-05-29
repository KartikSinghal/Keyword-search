package org.dbpedia.keywordsearch.importer;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileDeleteStrategy;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class neo4j {
    
    private GraphDatabaseService db;

    public neo4j(String graphpath) {
        /* Starting a grahdatabase service at apecified path */
            try{
            this.db = new GraphDatabaseFactory().newEmbeddedDatabase(graphpath);
            }catch(Exception e){
                File file = new File(graphpath+"/tm_tx_log.1");
                file.delete();
            }
    }
    
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        /* Graphdatabase service closed */
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    graphDb.shutdown();
                }
            });
        }   
    
    public void shutDown() {
            System.out.println();
            System.out.println("Shutting down database ...");
            db.shutdown();
        }
    
    public void clearDb(String graphpath) throws IOException {
        File files = new File(graphpath);
        for(File file : files.listFiles()){
            FileDeleteStrategy.FORCE.delete(file);
        }
        }
    
    public GraphDatabaseService getgdbservice(){
        return this.db;
    }
    
    private enum Reltypes implements RelationshipType{
	Predicate_of,Subject_of,Object_of;
    }
    
    public void graphdbform(GraphDatabaseService graphdb, String rdfpath) {
        
        /* Initialization of triple nodes */
        Node subjectnode = null;
        Node objectnode = null;
        Node predicatenode = null; 
        
        StmtIterator iter = jena.jena(rdfpath);
        ResourceIterator<Node> nodeindex;
        try (Transaction tx = graphdb.beginTx()) {
            try {
                while ( iter.hasNext() ) {
                
                /* Extracting RDF triple nodes from the specified file */
                Statement stmt = iter.next();
                Resource s = stmt.getSubject();
                Resource p = stmt.getPredicate();
                RDFNode o = stmt.getObject();
                
                /* The formation of graph and label nodes */
                Label subjectlabel = DynamicLabel.label(s.toString());
                
                /* Checking whether the node exists before or not*/
                nodeindex = graphdb.findNodes(subjectlabel);
                if(!nodeindex.hasNext()){
                    subjectnode = graphdb.createNode(subjectlabel);
                }
                else{
                    subjectnode=nodeindex.next();
                }
                Label predicatelabel = DynamicLabel.label(p.toString());
                nodeindex = graphdb.findNodes(predicatelabel);
                if(!nodeindex.hasNext()){
                    predicatenode = graphdb.createNode(predicatelabel);
                }
                else{
                    predicatenode=nodeindex.next();
                }
                Label objectlabel = DynamicLabel.label(o.toString());
                nodeindex = graphdb.findNodes(objectlabel);
                if(!nodeindex.hasNext()){
                    objectnode = graphdb.createNode(objectlabel);
                }
                else{
                    objectnode=nodeindex.next();
                }
                
                /* Creating a fact node for each triple */
                Node factnode = graphdb.createNode();
                
                /* Establishing relationships of each triple with its fact node */
                Relationship relationships = factnode.createRelationshipTo
		(subjectnode,Reltypes.Subject_of);
                Relationship relationshipp = factnode.createRelationshipTo
		(predicatenode,Reltypes.Predicate_of);
                Relationship relationshipo = factnode.createRelationshipTo
		(objectnode,Reltypes.Object_of);
                
               }               
            }finally {
            if ( iter != null ) iter.close();
        } 
            tx.success();
        }
    }
}

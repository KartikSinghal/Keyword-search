package org.dbpedia.keywordsearch.serverproperties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author enigmatus
 */
final public class pathvariables {
    
    /* This class sets all the paths according the server configuration specified in Server properties */
    private String rdffolder;
    private String graphfolder;
    public String getrdf() { return this.rdffolder; }
    public String getgraph() { return this.graphfolder; }
    private void setpaths() throws IOException{
        BufferedReader in = new BufferedReader(new FileReader("./conf/keywordsearch.properties"));
        String line="";
        while((line=in.readLine())!=null){
            if(line.contains("#")||line.equals("")){
                continue;
            }
            if(line.contains("graph")){
                int i = line.indexOf("=");
                this.graphfolder=line.substring(i+1);
            }
            else if(line.contains("rdf")){
                int i = line.indexOf("=");
                this.rdffolder=line.substring(i+1);
            }
        }
    }
    public pathvariables() throws IOException { this.setpaths(); }
    
}

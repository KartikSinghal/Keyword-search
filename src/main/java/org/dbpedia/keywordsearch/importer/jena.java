
package org.dbpedia.keywordsearch.importer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

/**
 *
 * @author enigmatus
 */
public class jena {
    /* Jena is used for extracting rdf triple from .ttl files */
    public static StmtIterator jena(String rdfpath) {
        FileManager.get().addLocatorClassLoader(neo4j.class.getClassLoader());
        Model model = FileManager.get().loadModel(rdfpath, null, "TURTLE");
        StmtIterator iter = model.listStatements();
        return iter;
    }
}

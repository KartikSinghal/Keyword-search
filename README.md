# Keyword-search

1. Download and unzip neo4j.
2. Download Keyword-search folder.
3. Edit the file KeywordSearch.properties in conf and add the paths of the rdf files and graphdatabase folder.
4. Run the uiclass.java present in org.dbpedia.keywordsearch.uinterface
5. On terminal go to the neo4j directory.
6. Run $ ./bin/neo4j console
7. The neo4j server will be up and running on http://localhost:7474/browser/.
8. Run $ Match n return n, in its console to visualize complete graph. 
9. Since graph will be too big, Run $ Match n return n Limit 100

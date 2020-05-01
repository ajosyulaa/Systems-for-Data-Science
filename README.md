# Systems-for-Data-Science

This project has a mockup of the core functionality of a Web search engine. For more details, you may refer to the project website https://marcoserafini.github.io/teaching/systems-for-data-science/fall19/project1.html

In this repository, there are 1977 documents. Each of these documents are crawled from the internet, and named as the document id. The file id_URL_pairs.txt is the mapping of document id and its URL. Each line represent one document. Your search engine are required to return the URL of the document. 


### extract the templates.zip file to templates folder ####

Follow the steps in given order:

1. Move to parent folder where the files are all extracted. 
 
2. Run HDFS server. Load all the text data into "hadoop.2.7.3/Project_data1" folder in home location of hadoop using the following commands.

hdfs dfs -put PathofFolderContainingTextFiles Project_data1  

3. Run the pyspark file in the terminal using the following command.
 python "runme.py"
This will read data from hdfs and load inverted index into rocksdb database named "search4.db"

4. Run the flask related file using the following commands

python search_server.py

5. Enter the query in the search bar to see the results




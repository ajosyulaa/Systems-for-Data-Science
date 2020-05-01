from pyspark.sql import SparkSession
from pyspark import SparkConf, SparkContext
import re
import pandas as pd
import rocksdb

conf = SparkConf().setMaster("local").setAppName("Search_app")
sc = SparkContext(conf = conf)

doc_to_url= pd.read_csv("id_URL_pairs.txt", header=None).set_index(0).to_dict()[1]

hdfs_lines = sc.wholeTextFiles("hdfs:///user/akhila/hdfs_folder/Project1_data/").flatMap(lambda name: map(lambda word: (word, name[0]), name[1].split()))
hdfs_lines1 = hdfs_lines.map(lambda pair: (pair, 1))
# print(hdfs_lines1.collect()[0])
hdfs_lines2 = hdfs_lines1.reduceByKey(lambda count1, count2: count1 + count2)
# print(hdfs_lines2.collect()[0])
hdfs_lines3 = hdfs_lines2.map(lambda pair: (pair[0][0], (pair[0][1], pair[1])))
# print(hdfs_lines3.collect()[0])
hdfs_lines4 = hdfs_lines3.map(lambda pair: (pair[0],re.search(r'doc.*',pair[1][0])[0][:-4]))
# print(hdfs_lines4.collect()[0])
hdfs_lines5 = hdfs_lines4.map(lambda pair: (pair[0], doc_to_url[pair[1]]))
hdfs_lines6 = hdfs_lines5.groupByKey().mapValues(list)
# print(hdfs_lines6.collect()[0])
hdfs_lines7 = hdfs_lines6.map(lambda pair: (pair[0].lower(), ','.join(pair[1]))).collect()
# print(hdfs_lines7[0], hdfs_lines7[1])
db2 = rocksdb.DB("search4.db", rocksdb.Options(create_if_missing=False))

for i in range(len(hdfs_lines7)):
    db2.put(bytes(hdfs_lines7[i][0], 'utf-8'),bytes(hdfs_lines7[i][1], 'utf-8'))
    # print(db2.get(bytes(hdfs_lines7[i][0], 'utf-8')))

print("here")
# db2.close()
print("here2")
sc.stop()
print("end")
# Distributed Word Count

Wordcount is famously the “Hello, world!” of many data science platforms (e.g., MapReduce and Spark). This project is to implement a distributed, fault tolerant version of wordcount in Java.

The distributed wordcount consists of a number of processes. All communication across processes will be via sockets.  It uses a master-worker paradigm: there is one master and N workers. The master ensures that all N workers are up by pinging them periodically with a heartbeat message every 3 seconds. If a worker stops responding, then the master spawns another worker. (The fault-tolerance of the system shown by invoking kill to stop processes.)

Workers repeatedly ask the master which file they should work on (until they learn there is no work left to do). They perform wordcount and output results to a file. When all input files have been processed, the master informs the workers that all work has concluded; upon receiving this message, the workers exit. The master then reads all of the files produced by the workers, combine the results, and output them.

The program prints the word counts in reverse order by frequency (so, most frequent word first) - each line lists a word and its frequency.


Build

Compile and test
```
./gradlew build
```

Compile only
```
./gradlew assemble
```

Test
```
./gradlew test
```

For those who use Windows, you should run `gradlew.bat` with the same parameters.

Both IDEA and Eclipse have plugins for Gradle.

Some existing tests need Java 8.


# Code location

`src/main/java` is for word count code.

`src/test/java` is for tests. And `src/test/resources` is for test data.

In most cases, adding or modifying files in other places is not necessary.


# Test cases
There are two basic tests here in class `TestUtil`




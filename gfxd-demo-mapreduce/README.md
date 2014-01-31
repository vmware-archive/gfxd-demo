# Instructions for gfxd-demo-mapreduce

1 - Build the project jar
2 - Browse to <project>/build/libs/
3 - Set the classpath to include the jar file:

export CLASSPATH=$CLASSPATH:<project>/build/libs/gfxd-demo-mapreduce-1.0.jar

4 - Make sure you have HADOOP_HOME and other hadoop environment variables set.
5 - Call the MapReduce job:

yarn jar gfxd-demo-mapreduce-1.0.jar -Dmapreduce.framework.name=local -Dmapreduce.cluster.local.dir=/tmp/mr-local  -Dsqlfire.url="jdbc:sqlfire://localhost:1527"

6 - The job will generate entries on LOAD_AVERAGES table, which will be used for predictions analysis.

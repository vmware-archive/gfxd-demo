# PlugDataLoader

A Spring based app that perform batch insertion on GemFireXD using Spring JDBC, HikariCP JDBC pool and SpringBoot. 

CSV File available at: https://drive.google.com/file/d/0B0TBL8JNn3JgV29HZWhSSVREQ0E/edit?usp=sharing
More details about the file structure: http://www.cse.iitb.ac.in/debs2014/?page_id=42#

# Configuration

config.properties
	Used for threadPool and queue size. It can also defines the batch size for batch insertion.
	
db.properties 
	Database connectivity parameters
	
context.xml
	Spring context configuration.
	
# Build & Run

For details about how to install Gradle check http://www.gradle.org/installation

Before building, please edit the GFXD_HOME variable in build.gradle file in order to point to your GemFireXD installation directory.  This is step is necessary in order to bundle sqlfireclient.jar file into the build.  
Move to the project folder and check if build.gradle is there. Then type "gradle build", the jar will be available under build/libs/

It can also be built from STS or IntelliJ, but just notice that by default deps may be downloaded to different places.

## To execute:
	java -jar build/libs/PlugDataLoader-1.0.jar [PATH_TO_CSV_FILE] 


 
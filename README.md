GemfireXD Demo
==============

## Note
For now, this doc is intended to be a README-based spec. As such it will describe how things are _intended_ to work, but those intentions may not be implemented yet. Features not implemented will be expressed as **_bolded italics_**.

Background
----------

This demo is based on the [DEBS 2014 Challenge]. It uses the data set presented there and strives to achieve the objectives of the challenge.

Structure
---------

The repo consists of 3 sub-projects:

* `gfxd-demo-loader` - The loader which used to stream events into the system.
* `gfxd-demo-mapreduce` - **_The mapreduce component_**
* `gfxd-demo-web` - **_The web UI component_**

Building
--------

The demo is built using Gradle **_which is bundled with the source_**.

Several Gradle tasks are available:

* `jettyRun <path to csv file` - **_When run with a parameter pointing to a CSV file containing events, the webapp will be run and will automatically start ingesting events from the file._**
* `loader <path to csv file` - **_This option will start the loader as a standalone utility and ingest events from the file provided._**

gfxd-demo-loader
----------------

This component is a Spring based app that performs batch insertion on GemFireXD using Spring JDBC, HikariCP JDBC pool and SpringBoot. 

The input is via CSV File available at: https://drive.google.com/file/d/0B0TBL8JNn3JgV29HZWhSSVREQ0E/edit?usp=sharing. More details about the file structure here [DEBS 2014 Challenge]

### Configuration

* `config.properties` - Used for threadPool and queue size. It can also defines the batch size for batch insertion.
* `db.properties` - Database connectivity parameters.
* `context.xml` - Spring context configuration.
        
### Build & Run

For details about how to install Gradle check http://www.gradle.org/installation

Before building, please edit the GFXD_HOME variable in gradle.properties file in order to point to your GemFireXD installation directory. This is step is necessary in order to bundle sqlfireclient.jar file into the build.  

After executing `gradle build`, the jar will be available under gfxd-demo-loader/build/libs/

It can also be built from STS or IntelliJ, but just notice that by default deps may be downloaded to different places.

Execute directly with: `java -jar gfxd-demo-loader/build/libs/PlugDataLoader-1.0.jar [PATH_TO_CSV_FILE]`

**_Or via Gradle with_**: `gradle loader [PATH_TO_CSV_FILE]`


[DEBS 2014 Challenge]:http://www.cse.iitb.ac.in/debs2014/?page_id=42

# unicorn-master

## Features implemented so far

1. Each sections below showcase an end-to-end scenario of how to use a specific technology stack. And there is a general section later that explains some of the best practices adopted in unit testing, spring boot usage etc.

### Basic setup and bring up the application.

1. This project runs using standard Spring boot and Maven with maven dependency management best practice implemented. It has dependency to REDIS, Cassandra & (Future - Postgres, Kafka) but one can start and checkout the application without having any of these external available in a local laptop. This helps developers to get started immediately and play around with the application without hassles.
2. Open the project in your editor of choice (IntelliJ is recommended) as a maven project.
3. The project will be imported as a Maven project, below single step configuration can start the application in a laptop.
4. Run `unicorn-api/src/main/java/com/unicorn/api/Application` 
    1. Then edit configuration and provide the following values according to the local path on your machine and rerun the application again
    ```
    -Xbootclasspath/p:C:\software\workspace\unicorn-master\unicorn-configuration
    -Dspring.profiles.active=local,isolate
    ```

### Fan-out using AKKA actors usage example.

1. This scenario showcase how to implement an end-to-end asynchronous request response scenario using spring REST & actors.
2. When a fan out generate random request is submitted a job gets executed asynchronously by actors and coordinator actor collects the results as and when generators send results to the coordinator. 
3. The above random-generate job when submitted will return a job ID back and that ID would be the ID of the coordinator actor who coordinates the results asynchronously. The ID gets stored in a cache with the complete actor information for later access. This actor could be a remote actor running on any remote machine. When a subsequent request appear for fetch the results (fan-out-random-generate-response). The actor details is fetched back from cache using the ID and can communicate with that actor for the results.
4. Hit the GET url http://127.0.0.1:8082/unicorn-api/v1/fan-out-random-generate?count=5 this will submit to generate 5 random numbers. 5 actors will be created and each one generates a random number and result will be send to a coordinator actor and the coordinator actor sends the result back to the caller. This call would return the ID for the submitted request.
5. Hit the GET url http://127.0.0.1:8082/unicorn-api/v1/fan-out-random-generate-response?id=8fa3bb58-8f5c-4d34-ad75-69d388088d1f This call would return the results from the previous generate submission.
    ```
        Where 'id' is the 'id' returned from the previous call
    ``` 
6. The ID is stored in a local in-memory cache using spring cache abstraction for local development. For production the cache could be replaced by REDIS. To locally use REDIS instead of in-memory cache, the instructions are provided below.
7. Checkout junit test case for this example as well.

### AKKA persistence usage example

1. This scenario showcase how to use AKKA persistence actors along with spring.
1. AKKA persistence actor example is implemented with below example. (persistence actors provide distributed actors with identity). A BankAccount persistent actor can be created by sending the following HTTP POST request
```
http://127.0.0.1:8082/unicorn-api/v1/bankAccount
with below json payload - make sure to set the HTTP header "Content-Type" to "application/json" in postman
{  
   "name":"Jon",
   "bankTransaction":{  
      "bankTransactionType":"DEPOSIT",
      "transactionAmount":100
   }
}
```
2. To add more money to above customer send the following HTTP PUT request.
```
http://127.0.0.1:8082/unicorn-api/v1/bankAccount
with below json payload - make sure to set the HTTP header "Content-Type" to "application/json" in postman 
{  
   "name":"Jon",
   "bankTransaction":{  
      "bankTransactionType":"DEPOSIT",
      "transactionAmount":100
   }
}
```
3. To retrieve the current balance perform REST GET request
```
http://127.0.0.1:8082/unicorn-api/v1/bankAccount?bankAccountName=Jon
```
4. Persistence actors are using LevelDB as persistent storage, it can be changed by switching maven dependency to point to Cassandra storage and update AKKA configuration for better resiliency.
5. Persistence actors has a bunch of unit-test cases and those one uses in memory persistence storage. Checkout the test cases.

### isolate profile explained.

1. System in production can use REDIS (for cache) and Cassandra (as database, coming more on this later). But it can work without having both of these instance available and it will use in-memory cache and none of the cassandra database access code will not work but application will come up and all other parts are available for access. Its possible because system is configured by default to run with **isolate** profile in local laptop and this is set during system startup look at start up parameters. This mode can be removed by taking out **isolate** profile during startup once REDIS and Cassandra are installed. See the steps later. 
2. REDIS for microsoft windows - download and install MSI  https://github.com/MSOpenTech/redis/releases this would automatically start REDIS as a windows service at localhost:6379. To change any of the default configuration read the documentation that comes with installation "Windows Service Documentation.docx"
3. REDIS can be accessed using Jedis/lettuce API Jedis jar is packaged along with the application and a redisTemplate with name 'primaryRedisTemplate' is configured and ready to use. 

### Cassandra example usage.

1. This scenario showcase how to access data from real Cassandra.
2. Data is stored in cassandra to do that, Install cassandra 2.2.7 from http://www.planetcassandra.org/archived-versions-of-datastaxs-distribution-of-apache-cassandra/- start cassandra as windows service.
3. Open Cassandra CQL shell create a keyspace and table and insert data as below
```
    CREATE KEYSPACE cep  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
    use cep;
    CREATE TABLE flight (
      flightKey varchar PRIMARY KEY,
      pnrs List<text>
    );
    insert into flight (flightKey , pnrs) values ('200-2016:01:01:08:00:12:00-DAL-NYC',['DF45YU','Z45RT7']);
    update flight set pnrs = pnrs + ['GH59T5'] where flightKey = '200-2016:01:01:08:00:12:00-DAL-NYC'
    select * from flight
```
4. Cassandra end point can be tested by issuing a HTTP get request http://127.0.0.1:8082/unicorn-api/v1/flight
5. Once Cassandra & REDIS is running in the above PORT, restart the Application by removing "isolate" profile in the start-up JVM arguments. Now Spring would connect to REDIS & Cassandra severs instead of in-memory cache & with  real column families. A log would appear in the application log that says connecting to REDIS cache.

### Kafka example usage and installation on windows

1. This section show-case how to use Kafka producer and consumer. 
2. To install Kafka on windows first Install 7-zip
3. unzip kafka binary from https://www.apache.org/dyn/closer.cgi?path=/kafka/0.10.0.0/kafka_2.11-0.10.0.0.tgz
4. unzip it start zookeeper and kafka and assume the installation folder is C:\software\kafka_2.11-0.10.0.0
5. start zookeeper
```
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```
6. start kafka server
```
bin\windows\kafka-server-start.bat config\server.properties
```
7. Create a topic named 'flightEventTopic'
```
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic flightEventTopic
```
8. List the created topic
```
bin\windows\kafka-topics.bat --list flightEventTopic --zookeeper localhost:2181
```
9. Submit the below HTTP POST URL to submit a flight message into the system, this message from API layer will be send to the above Kafka Topic and there is another listener that listens for this message and prints it on the console.
```
http://127.0.0.1:8082/unicorn-api/v1/flight
with below json payload - make sure to set the HTTP header "Content-Type" to "application/json" in postman
{  
   "flightKey":"200-2016-01-01-DAL-SFO",
   "flightMessage": "<FightCancel>bla bla bla content..../FightCancel>"
}
```
10. A message appears of producing and consuming the message.
11. **Note** Spring kafka client (http://docs.spring.io/spring-kafka/docs/1.1.0.M1/reference/html/index.html) is tried out but it seems too complex to use, first they don't have enough documentation in samples. But tried and made it to work but removed that from codebase. Direct Kafka API is straight-forward. Spring tries to take the same approach as JMS listener for Kafka and that seems like a leaky abstraction to fit Kafka into it. Currently at-mos-once client example is implemented using Kafka clients, other at-least-once and exactly-once can be easily implemented using the examples from https://github.com/ajmalbabu/kafka-clients

### Other best practices

1. Supports spring Profiles concept, default profile can be extended with "local", "dev", "qa" or "prod" profiles.
    1. Spring profile configuration are supplied in the JVM startup argument. For local laptop development in isolation mode, provide the following profiles "local, isolate" as explained earlier in startup arguments.  All the default spring beans (which does not have any @Profile annotation) gets included in start-up and any beans with @Profile("local") & @Profile("isolate") gets included as well. 
    2. The corresponding configuration file for local profile file is unicorn-configuration/application-local.yaml file.
    3. If certain bean need to be only activated during a certain profile execution it can be controlled via @Profile annotation. Such an example is available in CacheManagerConfiguration.java
    4. Refer to isolate profile explained section above for laptop based isolate development.
2. Supports TDD using Spring and Junit along with integration test cases using spring integration test support to test end to end. Example test classes are available in the "test" folder. 
    1. Another important unit test feature available is to avoid duplicating of source code & resources file during unit testing. If module A depends on module B and module B's test folder has source code and configuration file needed for B's unit testing, all such configuration and code in test from module B is available to module A unit testing without duplicating those files into module A test folder. This is possible by declaring "test-jar" at "test" scope dependency in module A. An example can be found at unicorn-api's pom file for a dependency into unicorn-service module.  
    2. Cassandra unit testing is challenging as the cassandra-unit test libraries is inactive. All Cassandra testing is performed using mockito by mocking Dao code. Look at FlightServiceTest.java
    3. Kafka end to end testing is performed using an embedded Kafka server - For an end to end example refer to FlightEventTest and the comments in that test case.
    4. **mvn clean test** only runs unit test where as **mvn clean install** or **mvn clean verify** runs integration tests. Integration tests take longer to run, also sometimes it fails for no specific reasib (example kafka embedded test) so run again.
3. Logging is controlled through slf4j and logback. There is a default logback.xml that provides console and file appender. The logback file also supports MDC: transactionId to assign a unique transactionId for a request, this is controlled through 'transactionId' parameter. Caller can pass a HTTP header with 'transactionId' as key and a value, if none is provided, unicorn would create a new transactionId for that request and use that during logging wherever that request thread goes.
4. Actors run on arbitrary threads, hence the transactionId that is passed along to actor need to be set at beginning of actor execution to get MDC feature. Plumbing piece are put together to convert transactionId that is settable from actor. Refer to RandomService.java & RandomGeneratorActor.java for an example of how to set the transactionId correctly before actors are invoked. 
5. AKKA is customized to work with Spring, so that AKKA actors are created as spring bean and hence spring properties can be injected to AKKA actors. Core classes to achieve this are in unicorn-common/actor package refer to SpringActorProducer, SpringExtension, ParameterInjector & Parameters. 
6. A default AKKA configuration is provided in 'unicorn.akka.conf' file this file controls the AKKA logging and thread pools used by actors created by Unicorn. 
7. Provide these additional parameters for log file location and to use file appender, by default console appender is used in laptop and these values does not matter, these values will be read by LoggerStartupListener.java and converted back to variables referred within logback.xml
    ```
        -Dlog.dir=C:\temp
        -Dlog.name=unicorn
    ```

### Modules
1. `unicorn-api` - [Readme](./unicorn-api/README.md)
2. `unicorn-common` - [Readme](./unicorn-common/README.md)
3. `unicorn-configuration` - [Readme](./unicorn-configuration/README.md)
4. `unicorn-service` - [Readme](./unicorn-service/README.md)

### Todo

1. ~~Cleanup maven - remove unused artifacts.~~
2. ~~Implement isolate profile for local and unit-testing support in all these below  without having hard dependency on any of the below for local development.~~
3. ~~Add Cassandra.~~
4. ~~Add REDIS as Cache storage~~
5. ~~Add Kafka.~~  
6.  ~~Add unit & integration test support for Kafka by using embedded kafka~~
7. Implement AKKA Cluster sharding.
8. Add RDBMS.
9. ~~Add Akka persistence with Junit Testing.~~
10. Create some examples to use REDIS using spring-data REDIS.
11. Create IONIC with angular - 2 front end with few screens which access RDBMS back-end with spring REST end points. Also evaluate Kendo-UI components.
12. Create installable in IONIC for Android & iOS devices and upload into App-store.
13. Create Hashicorp - Terra-form for AWS Cloud formation template with Chef scripts to deploy these into AWS with elasticity & auto scaling feature.
14. Integrate with Cloud-bees for CI/CD.
15. Use docker using Hashicorp - Packer.
16. Create a new fork form all the above project based on our needs using.
17. Consideration for out of order processing - e.g. flight cancel came first and then came flight time update. (depend on the timestamp of message?). 
18. Implement timeout for remote calls database, cache, redis, cassandra, kafka etc.
19. Implement throttling using hysterix.

#### Minor Todo

1. Remove any unused libraries in pom files.
2. Add mechanism to close old/unused actors in each generate re-submission.
3. Remove default serializers in AKKA.
4. When flight event is published to Kafka and if Kafka is not running at that time, it is blokcing the REST request.
5. TODO Assign a thread-pool for Kafka listeners and use threads from there instead of using random threads. FlightEventListener.java





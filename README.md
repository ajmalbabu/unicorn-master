# unicorn-master

### Setup
1. This project runs using standard Spring boot and Maven with correct maven dependency management practice available in maven.
2. Open the project in your editor of choice (IntelliJ is recommended)
3. The project will be imported as a Maven project
4. Run `unicorn-api/src/main/java/com/unicorn/api/Application` 
    1. Then edit configuration and provide the following values according to the local path on your machine and rerun the application again
    ```
    -Disolate.mode=true
    -Xbootclasspath/p:C:\software\workspace\unicorn-master\unicorn-configuration
    -Dspring.profiles.active=local,server
    ```
    2. Provide these additional parameters for log location, by default console appender is used and these values does not matter, to change to file appender change it in logback,xml, these values will be read by LoggerStartupListener.java and converted back to variables referred within logback.xml
    ```
        -Dlog.dir=C:\temp
        -Dlog.name=unicorn
    ```
5. Below use case shows an end-to-end asynchronous request response flow using spring REST & actors.
6. When generate random request is submitted the job gets executed asynchronously by actors and coordinator actor collects the results as and when generators send results to the coordinator. 
7. The above random-generate job when submitted will return a job ID back and that ID would be the ID of the coordinator actor who coordinates the results asynchronously. The ID gets stored in a cache with the complete actor information for later access. This actor could be a remote actor running on any remote machine. When a subsequent request appear for fetch the results (random-generate-response). The actor details is fetched back from cache using the ID and scan communicate with that actor for the results.
8. Hit the GET url http://127.0.0.1:8082/unicorn-api/v1/random-generate?count=5 this will submit to generate 5 random numbers. 5 actors will be created and each one generates a random number and result will be send to a coordinator actor and the coordinator actor sends the result back to the caller. This call would return the ID for the submitted request.
9. Hit the GET url http://127.0.0.1:8082/unicorn-api/v1/random-generate-response?id=56746bf5-800a-4d61-bd6c-665e1390e00e
    ```
      Where 'id' is the 'id' returned from the previous call
     ```
  This call would return the results from the previous generate submission. 
10. The ID is stored in a local in-memory cache using spring cache abstraction for local development. For production the cache could be replaced by REDIS. To locally use REDIS instead of in-memory cache, the instructions are provided below. 
11. For microsoft windows - download and install MSI  https://github.com/MSOpenTech/redis/releases this would automatically start REDIS as a windows service at localhost:6379. To change any of the default configuration read the documentation that comes with installation "Windows Service Documentation.docx"
12. Once REDIS is running in the above PORT, restart the Application with "-Disolate.mode=false" now Spring would connect to REDIS sever instead of in-memory cache. A log would appear in the application log that says connecting to REDIS cache.
13. REDIS can also be accessed using Jedis/lettuce API Jedis jar is packaged along with the application and a redisTemplate with name 'primaryRedisTemplate' is configured and ready to use. 
14. Logging is controlled through slf4j and logback. There is a default logback.xml that provides console and file appender. The logback file also supports MDC: transactionId to assign a unique transactionId for a request, this is controlled through 'transactionId' parameter. Caller can pass a HTTP header with 'transactionId' as key and a value, if none is provided, unicorn would create a new transactionId for that request and use that during logging wherever that request thread goes.
15. Actors run on arbitrary threads, hence the transactionId that is passed along to actor need to be set at beginning of actor execution to get MDC feature. Plumbing piece are put together to convert transactionId that is settable from actor. Refer to RandomService.java & RandomGeneratorActor.java for an example of how to set the transactionId correctly before actors are invoked. 
16. AKKA is customized to work with Spring, so that AKKA actors are created as spring bean and hence spring properties can be injected to AKKA actors. Core classes to achieve this are in unicorn-common/actor package refer to SpringActorProducer, SpringExtension, ParameterInjector & Parameters. 
17. A default AKKA configuration is provided in 'unicorn.akka.conf' file this file controls the AKKA logging and thread pools used by actors created by Unicorn. 
18. Supports spring Profiles concept, default profile can be extended with "local", "dev", "qa" or "prod" profiles.
    1. Default profile configuration are kept in unicorn-configuration/application.yaml file and this file gets included by default. All the default spring beans (which does not have any @Profile annotation) gets included in start-up. 
    2. A sample "local" profile for local laptop based development is available and corresponding configuration file for this profile is unicorn-configuration/application-local.yaml file. To use this prfile at start up provide -Dspring.profiles.active=local at java start-up. All the default spring beans (which does not have any @Profile annotation) and spring beans with @Profile("local") gets included in start-up. Similar concept can be followed for other environments e.g. "dev", "qa" or "prod".
    3. If certain bean need to be only activated during a certain profile execution it can be controlled via @Profile annotation on that bean. Such an example is available in UnicornRestApi.java - in this case it is enabled for all profiles.
    4. During unit-testing "unit-test" profile can be activated by adding a java runtime parameter -Dspring.profiles.active=unit-test or by adding the profile in the unit-test in the test/resources/application.yaml file, take a look at any such yaml file under test/resources folder for an example. When unit-test profile is enabled all default beans (which does not have any @Profile annotation) gets activated and all beans marked with @Profile("unit-test") gets activated. 
    5. **Note** - There is also "unicorn-configuration/application-server.yaml" file this is not a profile, but rather helps to add few additional server properties at runtime in a different file. 
19. Another important feature available is avoid duplicating of soruce code & resources file during unit testing. If module A depends on module B and module B's test folder has source code and configuration file needed for B's unit testing, all such configuration and code in test from module B is available to module A unit testing without duplicating those files into module A test folder. This is possible by declaring "test-jar" at "test" scope dependency in module A. An example can be found at unicorn-api's pom file for a dependency into unicorn-service module.  

### Modules
1. `unicorn-api` - [Readme](./unicorn-api/README.md) 
2. `unicorn-common` - [Readme](./unicorn-common/README.md)
3. `unicorn-configuration` - [Readme](./unicorn-configuration/README.md)
4. `unicorn-service` - [Readme](./unicorn-service/README.md)

###Todo

1. Implement isolate.mode for local and unit-testing support in all these below  without having hard dependency on any of the below or local development.
2. Add Cassandra.
3. Add RDBMS.
4. Add Kafka.
5. Add reactive-stream.
6. Add Spark. 
7. Add Akka persistence with AKKA clustering. 
8. Create a new fork form all the above project based on our needs using DDD style by reorganizing bounded contexts.
9. Create IONIC with angular - 2 front end with few screens which access RDBMS back-end with spring REST end points. Also evaluate Kendo-UI components.
10. Create installable in IONIC for Android & iOS devices and upload into App-store.
11. Create Cloud formation template with Chef scripts to deploy these into AWS with elasticity & auto scaling feature.
12. Integrate with Cloud-bees for CI/CD.

####Minor Todo
1. Remove any unused libraries in pom files.
2. Add mechanism to close old/unused actors in each generate re-submission.

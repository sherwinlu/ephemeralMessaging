# Text Message Service

Simple ephemeral text message service. You can think of it a little like Snapchat.

## Compiling the project

The project uses maven to build. A pom.xml is included.

From the command line, simply

```
% mvn clean package
```

This should automatically download and install all needed jars for the project

### Prerequisites

For the project to run, you must have:

* Docker
* Maven 3.x
* JDK 8

### Running the project

A Dockerfile is included that will build a MySQL database and run it inside the Docker container and expose it externally.
It will also automatically populate the SQL script as well. To do so, just run the following command from the following commands
from the src/main/resources/db directory.

```
% docker build -t slu/fitnesspaldb .
% docker run --name=messagingdb -d --publish 3306:3306 slu/fitnesspaldb
```

The servjce was written as a spring-boot application and is configured to build a self containing jar that will automatically
launch a local Tomcat server and a local Hazelcast server with all the services running.

From the command line, run the following:

```
% java -jar target/messaging-1.0.jar
```

## Architecture / Design

The services uses a RDBMS for the 'cold' storage and a "cache" for the 'hot' storage. For vendors, MySQL was
chosen for the RDBMS vendor and Hazelcast was chosen as the "cache", or more accurately an in memory data grid.

This was chosen for redundancy such that in a production environment in case any of the servers go down, the data
is replicated across all of servers and as such we will not lose any of the messages. Other more popular caching solutions
such as MemCache there's the risk that the if a server goes down, the data will be lost.

The In Memory Data Grid is utilized for not only as a "cache" via distributed HashMaps, it's also used for Id Generation
such that the message has a consistent id across the entire cluster and lastly it's used for a distributed Lock to ensure
that when the expired message cleanup occurs, it's only running only on one server and not multiple.

As part fo the design we use two distributed HashMaps that hold with each one optimized for the type of query - queryById
and queryByUserName. As such, it's taking up 2x as much memory in the cache.

This solution will scale horizontally as we can deploy this service to how many boxes we need, even with Hazelcast, it can
be deployed on each box and configured automatically to look for each other to create a cluster.

In a production environment I would have dedicated servers for the database (master/slave) and also the username and
password would be more secure.

## Assumption

If a POST request comes in for the same username, but the old message has not expired yet, it gets moved to 'cold'
storage

## Design Limitations / Flaws

As part of the design, there is a job that runs through the entire cache and moves all the "expired" messages from
'hot' storage to 'cold' storage. Despite it doing a bulk insert, it doesn't delete any of the "expired" messages from
the 'hot' storage until all the data have been successfully moved.

This could potentially be an issue if the database is backed up or slow as the job runs every 30 seconds and if the
cleanup job takes more than 30 seconds to complete, that could be an issue.

Error handling and logging all needs to be handled better. Right now if there's an error, it throws the error back, for example if the database is down, it'll report that to the client.

## Future improvements

Besides adding additional logging, error handling and adding Unit tests the APIs aren't secure. It needs to either be deployed behind a firewall in which case the network is handling the security or it will need some sort of authentication to ensure that whomever is calling the REST APIs is authenticated.

Also, Hazelcast would need to be configured (right now it's using the default configuration) to more properly behave.

Lastly, Dockerize the app and the db so that everything is more easily deployable

## Scalability

As it is right now, it will scale horizontally quite well, however, to dynamically scale, the application should be
deployed in the cloud like AWS in which case depending upon certain criteria (volume/load), a new instance could be
automatically be deployed and launched.

Depending on if having one job that runs every 30s to move expired messages from Hazelcast to MySQL is sufficient
it might be more prudent to potentially rewrite the solution.

Finally the MySQL should be tuned for heavy inserts as it seems that we'll be doing a lot of bulk inserts.

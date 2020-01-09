# Commute Range Problem solution

### Problem description
Build a new feature which allows users of the app to determine the reachable towns/cities
from a starting point in a given amount of time. 
For example, a user may pick the city of San Francisco,
and a commute time of 60 minutes and the new feature should list the cities/towns,
which are reachable from San Francisco within 60 minutes.
Input: (city, time)
Output: list of cities

For the purposes of this exercise real­time data does not need to be considered,
rather a static immutable dataset can be assumed to be available and correct.
Also assume that you can receive the data in the format that you need,
e.g. commute time between various cities.

### Complexity

Current approach has (presumably) following max big O complexities:

For first request on given city(/non cached) implementation O(n^2).
For second request on given start city O(n)

### Further improvements and notes
- Currently there is an assumption in in_memory approach, which is:
if we can get from city a to city b in 5 minutes, then we can also get from city b to a in 5 minutes
- Make source mutable.
- Make cache dynamic ( + JMX for setting max cache time).
- Make cache more configurable (specify f.e. max cache time for certain nodes).
- Make cache smarter (possible performance degradation but more memory efficient, further described in CachedGraph)
- Cache invalidation.
- Make API reactive.
- In case question of scalability arises, smart balancing could take place, 
where each service is responsible for certain graph areas 

## Running neo4j
For now, unfortunately, there is no composite docker file, so you need to:
1) Set up neo4j
 - if you want to play around with it, install from https://neo4j.com/download/ (current approach also uses graph.algo, 
which is plugin for neo4j with implementation of basic algorithms)
 - alternatively, run neo4j in docker with following command:
```
    docker run \
        --name neo \
        -p7474:7474 -p7687:7687 \
        -d \
    	--env NEO4J_AUTH=none \
        --env NEO4JLABS_PLUGINS=["graph-algorithms"] \
        --env NEO4J_dbms_security_procedures_unrestricted=algo\.\* \
        neo4j:latest
```
After you start your graph, you can visit http://localhost:7474/browser/ for visual representation and console
Tip:
After starting server (or after you prepare data by yourself), you can run:
```
match p=(firstCity:City )-[:CityConnection*2]-(:City)
WITH *, relationships(p) AS connection
where firstCity.name="Lutadmad"
return p
```
in order to have a view of small portion of a graph, from certain starting point
## Running server
All that is required for running is jdk8+ and maven, after which the only thing to be executed is
 ```
 mvn clean install -DskipTests spring-boot:run
``` 
By default, server will create 100 cities and create random connections between them,
where time may vary from 10 to 360 minutes between cities.

Sample request to server running on localhost:
http://localhost:8080/range/cities?cityName=Gadvedab&maximumMinutes=3600

Or use swagger instead:
http://localhost:8080/swagger-ui.html#/commute-range-controller/

Currently supported runtime parameters:
- city.dao.type - source of cities for computations
    - neo4j (the one and only type of dao currently available, might be updated in future)
- city.dao.generateOnStart - boolean value to determine if application should drop and create sample dataset by itself
- city.repository.type - how to calculate output
    - in_memory - read from dao on start and load to memory. Sad, but effective, if one relies on currently used algorithms
    - in_db - query on db side. No service memory usage, but connection to db and queries should be double-checked.
- city.repository.maxCachedValue - in case of in_memory repository, configure max value for caching. For bigger datasets use lower max cache size
- spring.data.neo4j.password / spring.data.neo4j.username /spring.data.neo4j.uri - db access

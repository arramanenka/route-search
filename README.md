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

### Docs
Currently supported runtime parameters:
1) city.dao.type - source of cities for computations
    1) neo4j (the one and only type of dao currently available, might be updated in future)
2) city.repository.type - how to calculate output
    1) in_memory - read from dao on start and load to memory. Sad, but effective, if one relies on currently used algorithms
    2) in_db - query on db side. No service memory usage, but connection to db and queries should be double checked
3) city.repository.maxCachedValue - in case of in_memory repository, configure max value for caching.
    For bigger datasets use lower max cache size
4) spring.data.neo4j.password / spring.data.neo4j.username /spring.data.neo4j.uri - db access
    
About runtime documentation: Hail humans for swagger. Publicly accessible via /swagger-ui.html

### Further improvements and notes
0) Currently there is an assumption in in_memory approach, which is: 
if we can get from city a to city b in 5 minutes, then we can also get from city b to a in 5 minutes
1) Make source mutable.
2) Make cache dynamic ( + JMX for setting max cache time).
3) Make cache more configurable (specify f.e. max cache time for certain nodes).
4) Make cache smarter (possible performance degradation but more memory efficient, further described in CachedGraph)
5) Cache invalidation.
6) Make API reactive.
7) In case question of scalability arises, smart balancing could take place, 
where each service is responsible for certain graph areas 
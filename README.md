**Task Description:**

The objective of this task was to enhance the performance of repeated database queries by integrating Redis as a caching layer. If a specific query is executed more than a certain number of times (in this case, 5), its result is cached in Redis. Subsequent queries for the same data will retrieve the result from Redis instead of querying the database, improving the response time and reducing the load on the database.

<br>**Additional Details:**

To serialize and deserialize objects for storing in Redis, I used the Gson library. This allows converting Java objects to JSON format and vice versa, which is necessary for efficient caching of complex objects in Redis.

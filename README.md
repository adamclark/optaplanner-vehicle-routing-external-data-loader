# Optaplanner Vehicle Routing Demo - Custom Locations

A simple application to allow custom locations to be externally interfaced into
the Optaplanner vehicle routing demo [https://github.com/kiegroup/optaweb-vehicle-routing].

The application exposes an HTTP endpoint to clear and insert data but
this is implemented using Apache Camel to also allow other forms of integration.

Build the project using:

    mvn clean package

And run it as using:

    mvn spring-boot:run

Clear all current locations using:

    curl http://localhost:8088/locations/clear

Add new locations using:

    curl "http://localhost:8088/locations/add?lat=50.000000000000000&lng=5.000000000000000&name=My_Location"